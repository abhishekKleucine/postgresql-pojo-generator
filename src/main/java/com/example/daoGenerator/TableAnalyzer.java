package com.example.daoGenerator;

import java.sql.*;
import java.util.*;

/**
 * Analyzes database tables to categorize them by primary key structure
 * This helps determine the appropriate DAO generation strategy for each table
 */
public class TableAnalyzer {
    
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    
    public TableAnalyzer(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }
    
    public static void main(String[] args) {
        TableAnalyzer analyzer = new TableAnalyzer(
            "jdbc:postgresql://localhost:5432/qa_",
            "postgres", 
            "postgres"
        );
        
        try {
            TableAnalysisReport report = analyzer.analyzeAllTables();
            report.printReport();
        } catch (SQLException e) {
            System.err.println("Error analyzing tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public TableAnalysisReport analyzeAllTables() throws SQLException {
        TableAnalysisReport report = new TableAnalysisReport();
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            System.out.println("Connected to database for table analysis...");
            
            // Get all tables with their primary key information
            Map<String, TablePrimaryKeyInfo> tableInfoMap = analyzePrimaryKeys(conn);
            
            // Get foreign key information for composite key candidates
            Map<String, List<ForeignKeyInfo>> foreignKeyMap = analyzeForeignKeys(conn);
            
            // Categorize tables
            for (Map.Entry<String, TablePrimaryKeyInfo> entry : tableInfoMap.entrySet()) {
                String tableName = entry.getKey();
                TablePrimaryKeyInfo pkInfo = entry.getValue();
                List<ForeignKeyInfo> foreignKeys = foreignKeyMap.getOrDefault(tableName, new ArrayList<>());
                
                categorizeTable(report, tableName, pkInfo, foreignKeys);
            }
            
            System.out.println("Table analysis completed!");
        }
        
        return report;
    }
    
    private Map<String, TablePrimaryKeyInfo> analyzePrimaryKeys(Connection conn) throws SQLException {
        Map<String, TablePrimaryKeyInfo> tableInfoMap = new HashMap<>();
        
        String sql = """
            SELECT 
                t.table_name,
                COALESCE(pk.constraint_name, 'NO_PRIMARY_KEY') as pk_constraint,
                COALESCE(
                    STRING_AGG(pk.column_name, ', ' ORDER BY pk.ordinal_position), 
                    'NONE'
                ) as pk_columns,
                COUNT(pk.column_name) as pk_column_count
            FROM information_schema.tables t
            LEFT JOIN (
                SELECT 
                    tc.table_name,
                    tc.constraint_name,
                    kcu.column_name,
                    kcu.ordinal_position
                FROM information_schema.table_constraints tc
                JOIN information_schema.key_column_usage kcu 
                    ON tc.constraint_name = kcu.constraint_name
                WHERE tc.constraint_type = 'PRIMARY KEY'
                    AND tc.table_schema = 'public'
            ) pk ON t.table_name = pk.table_name
            WHERE t.table_schema = 'public' 
                AND t.table_type = 'BASE TABLE'
            GROUP BY t.table_name, pk.constraint_name
            ORDER BY t.table_name
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String tableName = rs.getString("table_name");
                String pkConstraint = rs.getString("pk_constraint");
                String pkColumns = rs.getString("pk_columns");
                int pkColumnCount = rs.getInt("pk_column_count");
                
                TablePrimaryKeyInfo pkInfo = new TablePrimaryKeyInfo(
                    tableName, pkConstraint, pkColumns, pkColumnCount
                );
                
                tableInfoMap.put(tableName, pkInfo);
            }
        }
        
        return tableInfoMap;
    }
    
    private Map<String, List<ForeignKeyInfo>> analyzeForeignKeys(Connection conn) throws SQLException {
        Map<String, List<ForeignKeyInfo>> foreignKeyMap = new HashMap<>();
        
        String sql = """
            SELECT 
                tc.table_name,
                kcu.column_name,
                ccu.table_name AS foreign_table_name,
                ccu.column_name AS foreign_column_name
            FROM information_schema.table_constraints AS tc 
            JOIN information_schema.key_column_usage AS kcu
                ON tc.constraint_name = kcu.constraint_name
                AND tc.table_schema = kcu.table_schema
            JOIN information_schema.constraint_column_usage AS ccu
                ON ccu.constraint_name = tc.constraint_name
                AND ccu.table_schema = tc.table_schema
            WHERE tc.constraint_type = 'FOREIGN KEY' 
                AND tc.table_schema = 'public'
            ORDER BY tc.table_name, kcu.column_name
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String tableName = rs.getString("table_name");
                String columnName = rs.getString("column_name");
                String foreignTableName = rs.getString("foreign_table_name");
                String foreignColumnName = rs.getString("foreign_column_name");
                
                ForeignKeyInfo fkInfo = new ForeignKeyInfo(
                    columnName, foreignTableName, foreignColumnName
                );
                
                foreignKeyMap.computeIfAbsent(tableName, k -> new ArrayList<>()).add(fkInfo);
            }
        }
        
        return foreignKeyMap;
    }
    
    private void categorizeTable(TableAnalysisReport report, String tableName, 
                                TablePrimaryKeyInfo pkInfo, List<ForeignKeyInfo> foreignKeys) {
        
        if (pkInfo.pkColumnCount == 0) {
            // No primary key - candidate for composite key
            CompositeKeyCandidate candidate = createCompositeKeyCandidate(tableName, foreignKeys);
            report.addNoPrimaryKeyTable(tableName, pkInfo, candidate);
            
        } else if (pkInfo.pkColumnCount == 1) {
            if ("id".equals(pkInfo.pkColumns)) {
                // Standard single ID primary key
                report.addSingleIdTable(tableName, pkInfo);
            } else {
                // Custom single column primary key
                report.addCustomPrimaryKeyTable(tableName, pkInfo);
            }
            
        } else {
            // Multiple column primary key
            report.addCompositeKeyTable(tableName, pkInfo);
        }
    }
    
    private CompositeKeyCandidate createCompositeKeyCandidate(String tableName, List<ForeignKeyInfo> foreignKeys) {
        // For tables without primary key, suggest composite key from foreign keys
        List<String> keyColumns = new ArrayList<>();
        
        // Add foreign key columns that could form a natural composite key
        for (ForeignKeyInfo fk : foreignKeys) {
            if (fk.columnName.endsWith("_id")) {
                keyColumns.add(fk.columnName);
            }
        }
        
        // If no suitable foreign keys, use all foreign keys
        if (keyColumns.isEmpty()) {
            for (ForeignKeyInfo fk : foreignKeys) {
                keyColumns.add(fk.columnName);
            }
        }
        
        return new CompositeKeyCandidate(tableName, keyColumns, foreignKeys);
    }
    
    // Helper classes
    public static class TablePrimaryKeyInfo {
        public final String tableName;
        public final String pkConstraint;
        public final String pkColumns;
        public final int pkColumnCount;
        
        public TablePrimaryKeyInfo(String tableName, String pkConstraint, String pkColumns, int pkColumnCount) {
            this.tableName = tableName;
            this.pkConstraint = pkConstraint;
            this.pkColumns = pkColumns;
            this.pkColumnCount = pkColumnCount;
        }
    }
    
    public static class ForeignKeyInfo {
        public final String columnName;
        public final String foreignTableName;
        public final String foreignColumnName;
        
        public ForeignKeyInfo(String columnName, String foreignTableName, String foreignColumnName) {
            this.columnName = columnName;
            this.foreignTableName = foreignTableName;
            this.foreignColumnName = foreignColumnName;
        }
    }
    
    public static class CompositeKeyCandidate {
        public final String tableName;
        public final List<String> keyColumns;
        public final List<ForeignKeyInfo> foreignKeys;
        
        public CompositeKeyCandidate(String tableName, List<String> keyColumns, List<ForeignKeyInfo> foreignKeys) {
            this.tableName = tableName;
            this.keyColumns = keyColumns;
            this.foreignKeys = foreignKeys;
        }
    }
    
    public static class TableAnalysisReport {
        private final List<String> singleIdTables = new ArrayList<>();
        private final List<String> compositeKeyTables = new ArrayList<>();
        private final List<String> customPrimaryKeyTables = new ArrayList<>();
        private final List<String> noPrimaryKeyTables = new ArrayList<>();
        private final Map<String, TablePrimaryKeyInfo> tableDetails = new HashMap<>();
        private final Map<String, CompositeKeyCandidate> compositeKeyCandidates = new HashMap<>();
        
        public void addSingleIdTable(String tableName, TablePrimaryKeyInfo pkInfo) {
            singleIdTables.add(tableName);
            tableDetails.put(tableName, pkInfo);
        }
        
        public void addCompositeKeyTable(String tableName, TablePrimaryKeyInfo pkInfo) {
            compositeKeyTables.add(tableName);
            tableDetails.put(tableName, pkInfo);
        }
        
        public void addCustomPrimaryKeyTable(String tableName, TablePrimaryKeyInfo pkInfo) {
            customPrimaryKeyTables.add(tableName);
            tableDetails.put(tableName, pkInfo);
        }
        
        public void addNoPrimaryKeyTable(String tableName, TablePrimaryKeyInfo pkInfo, CompositeKeyCandidate candidate) {
            noPrimaryKeyTables.add(tableName);
            tableDetails.put(tableName, pkInfo);
            compositeKeyCandidates.put(tableName, candidate);
        }
        
        public void printReport() {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DATABASE TABLE ANALYSIS REPORT");
            System.out.println("=".repeat(80));
            
            System.out.println("\n📊 SUMMARY:");
            System.out.println("Total tables analyzed: " + (singleIdTables.size() + compositeKeyTables.size() + 
                             customPrimaryKeyTables.size() + noPrimaryKeyTables.size()));
            System.out.println("- Single ID tables: " + singleIdTables.size());
            System.out.println("- Composite key tables: " + compositeKeyTables.size());
            System.out.println("- Custom primary key tables: " + customPrimaryKeyTables.size());
            System.out.println("- No primary key tables: " + noPrimaryKeyTables.size());
            
            printTableCategory("🆔 SINGLE ID TABLES (Standard entity tables)", singleIdTables);
            printTableCategory("🔗 COMPOSITE KEY TABLES (Multi-column primary keys)", compositeKeyTables);
            printTableCategory("🔑 CUSTOM PRIMARY KEY TABLES (Non-standard keys)", customPrimaryKeyTables);
            printNoPrimaryKeyTables();
            
            System.out.println("\n" + "=".repeat(80));
        }
        
        private void printTableCategory(String title, List<String> tables) {
            if (tables.isEmpty()) return;
            
            System.out.println("\n" + title + " (" + tables.size() + "):");
            System.out.println("-".repeat(60));
            
            Collections.sort(tables);
            for (String tableName : tables) {
                TablePrimaryKeyInfo pkInfo = tableDetails.get(tableName);
                System.out.printf("  %-35s | PK: %s%n", tableName, pkInfo.pkColumns);
            }
        }
        
        private void printNoPrimaryKeyTables() {
            if (noPrimaryKeyTables.isEmpty()) return;
            
            System.out.println("\n❌ NO PRIMARY KEY TABLES (Need composite key generation) (" + noPrimaryKeyTables.size() + "):");
            System.out.println("-".repeat(60));
            
            Collections.sort(noPrimaryKeyTables);
            for (String tableName : noPrimaryKeyTables) {
                CompositeKeyCandidate candidate = compositeKeyCandidates.get(tableName);
                System.out.printf("  %-35s | Suggested composite key: %s%n", 
                    tableName, String.join(" + ", candidate.keyColumns));
                
                // Show foreign key details
                for (ForeignKeyInfo fk : candidate.foreignKeys) {
                    System.out.printf("    └─ %s → %s.%s%n", 
                        fk.columnName, fk.foreignTableName, fk.foreignColumnName);
                }
                System.out.println();
            }
        }
        
        // Getters for programmatic access
        public List<String> getSingleIdTables() { return new ArrayList<>(singleIdTables); }
        public List<String> getCompositeKeyTables() { return new ArrayList<>(compositeKeyTables); }
        public List<String> getCustomPrimaryKeyTables() { return new ArrayList<>(customPrimaryKeyTables); }
        public List<String> getNoPrimaryKeyTables() { return new ArrayList<>(noPrimaryKeyTables); }
        public Map<String, CompositeKeyCandidate> getCompositeKeyCandidates() { return new HashMap<>(compositeKeyCandidates); }
    }
}
