package com.example.pojogenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhanced PostgreSQL POJO Generator
 * Generates POJOs from PostgreSQL database tables with comprehensive constraint handling
 * 
 * Features:
 * - Primary Key, Foreign Key, and Index detection
 * - Check Constraint parsing and validation
 * - Unique Constraint detection
 * - Pure Java validation methods (no JPA/Hibernate annotations)
 * - Configurable generation options
 */
public class PojoGenerator {
    
    // Configuration
    private final PojoGeneratorConfig config;
    
    // Default configuration
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/qa_";
    private static final String DEFAULT_DB_USER = "postgres";
    private static final String DEFAULT_DB_PASS = "postgres";
    private static final String DEFAULT_OUTPUT_FOLDER = "src/main/java/com/example/pojogenerator/pojos/";
    
    // Constructor with default configuration
    public PojoGenerator() {
        this.config = new PojoGeneratorConfig();
    }
    
    // Constructor with custom configuration
    public PojoGenerator(PojoGeneratorConfig config) {
        this.config = config;
    }
    
    public static void main(String[] args) {
        System.out.println("Starting Enhanced PostgreSQL POJO Generator...");
        
        // Create generator with default configuration
        PojoGenerator generator = new PojoGenerator();
        generator.generatePojos();
    }
    
    /**
     * Main generation method
     */
    public void generatePojos() {
        // Create output directory
        new java.io.File(config.getOutputFolder()).mkdirs();
        
        try (Connection conn = DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword())) {
            System.out.println("Connected to database successfully!");
            
            // Generate ValidationResult class if needed
            if (config.isGenerateValidationResultClass()) {
                generateValidationResultClass();
            }
            
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, config.getSchema(), "%", new String[]{"TABLE"});

            int tableCount = 0;
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                String className = toCamelCase(tableName, true);

                Map<String, ColumnInfo> columns = new LinkedHashMap<>();
                Set<String> primaryKeys = getPrimaryKeys(meta, tableName);
                Map<String, String> foreignKeys = getForeignKeys(meta, tableName);
                Map<String, IndexInfo> indexes = getIndexes(meta, tableName);
                Map<String, List<CheckConstraint>> checkConstraints = new HashMap<>();
                Map<String, UniqueConstraint> uniqueConstraints = new HashMap<>();
                
                // Get check constraints if enabled
                if (config.isIncludeCheckConstraints()) {
                    checkConstraints = getCheckConstraints(conn, tableName);
                }
                
                // Get unique constraints if enabled
                if (config.isIncludeUniqueConstraints()) {
                    uniqueConstraints = getUniqueConstraints(conn, tableName);
                }

                ResultSet cols = meta.getColumns(null, config.getSchema(), tableName, null);
                while (cols.next()) {
                    String colName = cols.getString("COLUMN_NAME");
                    String dataType = cols.getString("TYPE_NAME");
                    int columnSize = cols.getInt("COLUMN_SIZE");
                    boolean nullable = cols.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                    boolean autoIncrement = "YES".equals(cols.getString("IS_AUTOINCREMENT"));
                    String defaultValue = cols.getString("COLUMN_DEF");
                    
                    ColumnInfo colInfo = new ColumnInfo();
                    colInfo.name = colName;
                    colInfo.javaType = mapToJavaType(dataType);
                    colInfo.sqlType = dataType;
                    colInfo.size = columnSize;
                    colInfo.nullable = nullable;
                    colInfo.autoIncrement = autoIncrement;
                    colInfo.defaultValue = defaultValue;
                    colInfo.isPrimaryKey = primaryKeys.contains(colName);
                    colInfo.isForeignKey = foreignKeys.containsKey(colName);
                    colInfo.foreignKeyReference = foreignKeys.get(colName);
                    
                    // Add constraint information
                    if (checkConstraints.containsKey(colName)) {
                        colInfo.checkConstraints = checkConstraints.get(colName);
                        parseConstraints(colInfo);
                    }
                    
                    // Set length constraint for strings
                    if (colInfo.javaType.equals("String") && columnSize > 0) {
                        colInfo.hasLengthConstraint = true;
                        colInfo.maxLength = columnSize;
                    }
                    
                    columns.put(colName, colInfo);
                }

                generatePojoFile(className, tableName, columns, indexes, uniqueConstraints);
                tableCount++;
            }

            System.out.println("Successfully generated " + tableCount + " POJO classes in: " + config.getOutputFolder());

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error generating POJOs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePojoFile(String className, String tableName, 
                                       Map<String, ColumnInfo> columns, 
                                       Map<String, IndexInfo> indexes,
                                       Map<String, UniqueConstraint> uniqueConstraints) throws IOException {

        StringBuilder sb = new StringBuilder();
        
        // Package declaration
        sb.append("package com.example.pojogenerator.pojos;\n\n");
        sb.append("import java.io.Serializable;\n");
        sb.append("import java.util.Objects;\n");
        
        // Add imports for date/time types if needed
        boolean needsTimeImports = columns.values().stream()
            .anyMatch(col -> col.javaType.startsWith("java.time."));
        if (needsTimeImports) {
            sb.append("import java.time.*;\n");
        }
        
        boolean needsBigDecimal = columns.values().stream()
            .anyMatch(col -> col.javaType.equals("java.math.BigDecimal"));
        if (needsBigDecimal) {
            sb.append("import java.math.BigDecimal;\n");
        }
        
        boolean needsJsonNode = columns.values().stream()
            .anyMatch(col -> col.javaType.equals("JsonNode"));
        if (needsJsonNode) {
            sb.append("import com.fasterxml.jackson.databind.JsonNode;\n");
        }
        
        sb.append("\n");

        // Class documentation
        sb.append("/**\n");
        sb.append(" * POJO class for table: ").append(tableName).append("\n");
        sb.append(" * Generated by PostgreSQL POJO Generator\n");
        sb.append(" * \n");
        sb.append(" * Table Information:\n");
        sb.append(" * - Table Name: ").append(tableName).append("\n");
        
        // Document primary keys
        List<String> pkList = new ArrayList<>();
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            if (entry.getValue().isPrimaryKey) {
                pkList.add(entry.getKey());
            }
        }
        if (!pkList.isEmpty()) {
            sb.append(" * - Primary Keys: ").append(String.join(", ", pkList)).append("\n");
        }
        
        // Document indexes
        if (!indexes.isEmpty()) {
            sb.append(" * \n");
            sb.append(" * Indexes:\n");
            for (IndexInfo idx : indexes.values()) {
                sb.append(" * - ").append(idx.name);
                if (idx.unique) sb.append(" (UNIQUE)");
                sb.append(": ").append(String.join(", ", idx.columns)).append("\n");
            }
        }
        
        // Document foreign keys
        boolean hasForeignKeys = false;
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            if (entry.getValue().isForeignKey) {
                if (!hasForeignKeys) {
                    sb.append(" * \n");
                    sb.append(" * Foreign Keys:\n");
                    hasForeignKeys = true;
                }
                sb.append(" * - ").append(entry.getKey()).append(" → ").append(entry.getValue().foreignKeyReference).append("\n");
            }
        }
        
        sb.append(" */\n");
        sb.append("public class ").append(className).append(" implements Serializable {\n\n");
        sb.append("    private static final long serialVersionUID = 1L;\n\n");

        // Generate fields
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            ColumnInfo col = entry.getValue();
            String fieldName = toCamelCase(col.name, false);
            
            // Field documentation
            sb.append("    /**\n");
            sb.append("     * Database column: ").append(col.name).append("\n");
            sb.append("     * Type: ").append(col.sqlType);
            if (col.size > 0 && col.javaType.equals("String")) {
                sb.append("(").append(col.size).append(")");
            }
            if (!col.nullable) sb.append(" NOT NULL");
            if (col.autoIncrement) sb.append(" AUTO_INCREMENT");
            if (col.defaultValue != null && !col.defaultValue.trim().isEmpty()) {
                sb.append(" DEFAULT: ").append(col.defaultValue);
            }
            sb.append("\n");
            
            if (col.isPrimaryKey) sb.append("     * Primary Key\n");
            if (col.isForeignKey) sb.append("     * Foreign Key → ").append(col.foreignKeyReference).append("\n");
            
            sb.append("     */\n");
            sb.append("    private ").append(col.javaType).append(" ").append(fieldName).append(";\n\n");
        }

        // Default constructor
        sb.append("    /**\n");
        sb.append("     * Default constructor\n");
        sb.append("     */\n");
        sb.append("    public ").append(className).append("() {\n");
        sb.append("    }\n\n");

        // Getters and setters
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            ColumnInfo col = entry.getValue();
            String fieldName = toCamelCase(col.name, false);
            String methodName = toCamelCase(col.name, true);

            // Getter
            sb.append("    /**\n");
            sb.append("     * Gets ").append(col.name).append("\n");
            sb.append("     * @return ").append(col.javaType).append("\n");
            sb.append("     */\n");
            sb.append("    public ").append(col.javaType).append(" get").append(methodName).append("() {\n");
            sb.append("        return this.").append(fieldName).append(";\n");
            sb.append("    }\n\n");

            // Setter with constraint validation
            sb.append("    /**\n");
            sb.append("     * Sets ").append(col.name).append("\n");
            sb.append("     * @param ").append(fieldName).append(" the value to set\n");
            if (hasConstraints(col)) {
                sb.append("     * @throws IllegalArgumentException if constraint validation fails\n");
            }
            sb.append("     */\n");
            sb.append("    public void set").append(methodName).append("(").append(col.javaType).append(" ").append(fieldName).append(") {\n");
            
            // Add constraint validation using if statements
            generateSetterConstraintValidation(sb, col, fieldName);
            
            sb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
            sb.append("    }\n\n");
        }

        // equals method
        sb.append("    @Override\n");
        sb.append("    public boolean equals(Object obj) {\n");
        sb.append("        if (this == obj) return true;\n");
        sb.append("        if (obj == null || getClass() != obj.getClass()) return false;\n");
        sb.append("        ").append(className).append(" other = (").append(className).append(") obj;\n");
        
        // Use primary key for equals if available, otherwise use all fields
        List<String> equalsFields = new ArrayList<>();
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            ColumnInfo col = entry.getValue();
            if (col.isPrimaryKey) {
                equalsFields.add(toCamelCase(col.name, false));
            }
        }
        if (equalsFields.isEmpty()) {
            for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
                equalsFields.add(toCamelCase(entry.getValue().name, false));
            }
        }
        
        sb.append("        return ");
        for (int i = 0; i < equalsFields.size(); i++) {
            if (i > 0) sb.append(" && ");
            sb.append("Objects.equals(this.").append(equalsFields.get(i)).append(", other.").append(equalsFields.get(i)).append(")");
        }
        sb.append(";\n");
        sb.append("    }\n\n");

        // hashCode method
        sb.append("    @Override\n");
        sb.append("    public int hashCode() {\n");
        sb.append("        return Objects.hash(");
        for (int i = 0; i < equalsFields.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("this.").append(equalsFields.get(i));
        }
        sb.append(");\n");
        sb.append("    }\n\n");

        // toString method
        sb.append("    @Override\n");
        sb.append("    public String toString() {\n");
        sb.append("        return \"").append(className).append("{\" +\n");
        boolean first = true;
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            String fieldName = toCamelCase(entry.getValue().name, false);
            if (!first) sb.append(" +\n");
            sb.append("                \"").append(first ? "" : ", ").append(fieldName).append("=\" + ").append(fieldName);
            first = false;
        }
        sb.append(" +\n                '}';\n");
        sb.append("    }\n");
        
        // Generate validation methods if enabled
        if (config.isGenerateValidationMethods()) {
            generateValidationMethods(sb, className, columns);
        }
        
        // Generate constraint constants if enabled
        if (config.isGenerateConstraintConstants()) {
            generateConstraintConstants(sb, columns);
        }

        sb.append("}\n");

        // Write to file
        FileWriter fw = new FileWriter(config.getOutputFolder() + className + ".java");
        fw.write(sb.toString());
        fw.close();
        
        System.out.println("Generated: " + className + ".java");
    }

    private static String mapToJavaType(String sqlType) {
        switch (sqlType.toLowerCase()) {
            case "varchar":
            case "text":
            case "char":
            case "bpchar":
                return "String";
            case "int4":
            case "serial":
            case "integer":
                return "Integer";
            case "int8":
            case "bigint":
            case "bigserial":
                return "Long";
            case "float8":
            case "double":
                return "Double";
            case "float4":
            case "real":
                return "Float";
            case "numeric":
            case "decimal":
                return "java.math.BigDecimal";
            case "bool":
            case "boolean":
                return "Boolean";
            case "date":
                return "java.time.LocalDate";
            case "timestamp":
            case "timestamptz":
            case "timestamp without time zone":
                return "java.time.LocalDateTime";
            case "time":
                return "java.time.LocalTime";
            case "uuid":
                return "java.util.UUID";
            case "json":
            case "jsonb":
                return "JsonNode";
            default:
                return "String";
        }
    }

    private static String toCamelCase(String str, boolean capitalizeFirst) {
        StringBuilder result = new StringBuilder();
        for (String part : str.split("_")) {
            if (part.isEmpty()) continue;
            result.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
        }
        if (!capitalizeFirst && result.length() > 0)
            result.replace(0, 1, result.substring(0, 1).toLowerCase());
        return result.toString();
    }

    private static Set<String> getPrimaryKeys(DatabaseMetaData meta, String tableName) throws SQLException {
        Set<String> keys = new HashSet<>();
        ResultSet rs = meta.getPrimaryKeys(null, null, tableName);
        while (rs.next()) {
            keys.add(rs.getString("COLUMN_NAME"));
        }
        return keys;
    }

    private static Map<String, String> getForeignKeys(DatabaseMetaData meta, String tableName) throws SQLException {
        Map<String, String> fks = new HashMap<>();
        ResultSet rs = meta.getImportedKeys(null, null, tableName);
        while (rs.next()) {
            String fkCol = rs.getString("FKCOLUMN_NAME");
            String pkTable = rs.getString("PKTABLE_NAME");
            String pkCol = rs.getString("PKCOLUMN_NAME");
            fks.put(fkCol, pkTable + "." + pkCol);
        }
        return fks;
    }
    
    private static Map<String, IndexInfo> getIndexes(DatabaseMetaData meta, String tableName) throws SQLException {
        Map<String, IndexInfo> indexes = new HashMap<>();
        ResultSet rs = meta.getIndexInfo(null, null, tableName, false, false);
        
        while (rs.next()) {
            String indexName = rs.getString("INDEX_NAME");
            if (indexName == null) continue;
            
            // Skip primary key indexes
            if (indexName.toLowerCase().contains("pkey") || indexName.toLowerCase().contains("primary")) {
                continue;
            }
            
            String columnName = rs.getString("COLUMN_NAME");
            boolean unique = !rs.getBoolean("NON_UNIQUE");
            
            IndexInfo indexInfo = indexes.computeIfAbsent(indexName, k -> new IndexInfo());
            indexInfo.name = indexName;
            indexInfo.unique = unique;
            indexInfo.columns.add(columnName);
        }
        
        return indexes;
    }
    
    /**
     * Get check constraints for a table
     */
    private Map<String, List<CheckConstraint>> getCheckConstraints(Connection conn, String tableName) throws SQLException {
        Map<String, List<CheckConstraint>> checkConstraints = new HashMap<>();
        
        String query = """
            SELECT 
                cc.constraint_name,
                cc.check_clause,
                ccu.column_name
            FROM information_schema.check_constraints cc
            JOIN information_schema.constraint_column_usage ccu 
                ON cc.constraint_name = ccu.constraint_name
            WHERE ccu.table_name = ? 
            AND cc.constraint_schema = ?
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tableName);
            stmt.setString(2, config.getSchema());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String constraintName = rs.getString("constraint_name");
                String checkClause = rs.getString("check_clause");
                String columnName = rs.getString("column_name");
                
                CheckConstraint constraint = new CheckConstraint();
                constraint.name = constraintName;
                constraint.clause = checkClause;
                
                checkConstraints.computeIfAbsent(columnName, k -> new ArrayList<>()).add(constraint);
            }
        }
        
        return checkConstraints;
    }
    
    /**
     * Get unique constraints for a table
     */
    private Map<String, UniqueConstraint> getUniqueConstraints(Connection conn, String tableName) throws SQLException {
        Map<String, UniqueConstraint> uniqueConstraints = new HashMap<>();
        
        String query = """
            SELECT 
                tc.constraint_name,
                string_agg(kcu.column_name, ',' ORDER BY kcu.ordinal_position) as columns
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu 
                ON tc.constraint_name = kcu.constraint_name
            WHERE tc.table_name = ? 
            AND tc.constraint_type = 'UNIQUE'
            AND tc.table_schema = ?
            GROUP BY tc.constraint_name
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tableName);
            stmt.setString(2, config.getSchema());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String constraintName = rs.getString("constraint_name");
                String[] columns = rs.getString("columns").split(",");
                
                UniqueConstraint constraint = new UniqueConstraint();
                constraint.name = constraintName;
                constraint.columns = Arrays.asList(columns);
                constraint.isComposite = columns.length > 1;
                
                uniqueConstraints.put(constraintName, constraint);
            }
        }
        
        return uniqueConstraints;
    }
    
    /**
     * Parse check constraints to extract validation information
     */
    private void parseConstraints(ColumnInfo colInfo) {
        for (CheckConstraint constraint : colInfo.checkConstraints) {
            String clause = constraint.clause.toLowerCase();
            
            // Parse range constraints (age >= 18 AND age <= 65)
            Pattern rangePattern = Pattern.compile("(\\w+)\\s*>=\\s*(\\d+).*?(\\w+)\\s*<=\\s*(\\d+)");
            Matcher rangeMatcher = rangePattern.matcher(clause);
            if (rangeMatcher.find()) {
                colInfo.minValue = Integer.parseInt(rangeMatcher.group(2));
                colInfo.maxValue = Integer.parseInt(rangeMatcher.group(4));
                constraint.constraintType = "RANGE";
                continue;
            }
            
            // Parse single range constraints
            Pattern minPattern = Pattern.compile("(\\w+)\\s*>=\\s*(\\d+)");
            Matcher minMatcher = minPattern.matcher(clause);
            if (minMatcher.find()) {
                colInfo.minValue = Integer.parseInt(minMatcher.group(2));
                constraint.constraintType = "MIN";
            }
            
            Pattern maxPattern = Pattern.compile("(\\w+)\\s*<=\\s*(\\d+)");
            Matcher maxMatcher = maxPattern.matcher(clause);
            if (maxMatcher.find()) {
                colInfo.maxValue = Integer.parseInt(maxMatcher.group(2));
                constraint.constraintType = "MAX";
            }
            
            // Parse enum constraints (status IN ('ACTIVE', 'INACTIVE', 'PENDING'))
            Pattern enumPattern = Pattern.compile("\\w+\\s+in\\s*\\(([^)]+)\\)");
            Matcher enumMatcher = enumPattern.matcher(clause);
            if (enumMatcher.find()) {
                String enumValues = enumMatcher.group(1);
                String[] values = enumValues.split(",");
                for (String value : values) {
                    String cleanValue = value.trim().replaceAll("'", "");
                    colInfo.enumValues.add(cleanValue);
                }
                constraint.constraintType = "ENUM";
                continue;
            }
            
            // Parse length constraints (length(name) <= 50)
            Pattern lengthPattern = Pattern.compile("length\\(\\w+\\)\\s*<=\\s*(\\d+)");
            Matcher lengthMatcher = lengthPattern.matcher(clause);
            if (lengthMatcher.find()) {
                colInfo.maxLength = Integer.parseInt(lengthMatcher.group(1));
                colInfo.hasLengthConstraint = true;
                constraint.constraintType = "LENGTH";
                continue;
            }
            
            // Parse regex patterns (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
            Pattern regexPattern = Pattern.compile("\\w+\\s*~\\s*'([^']+)'");
            Matcher regexMatcher = regexPattern.matcher(clause);
            if (regexMatcher.find()) {
                colInfo.regexPattern = regexMatcher.group(1);
                constraint.constraintType = "REGEX";
                continue;
            }
        }
    }
    
    /**
     * Generate ValidationResult utility class
     */
    private void generateValidationResultClass() throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("package ").append(config.getPackageName()).append(";\n\n");
        sb.append("import java.util.*;\n\n");
        sb.append("/**\n");
        sb.append(" * Validation result container\n");
        sb.append(" * Generated by PostgreSQL POJO Generator\n");
        sb.append(" */\n");
        sb.append("public class ValidationResult {\n");
        sb.append("    private final List<String> errors = new ArrayList<>();\n");
        sb.append("    private final List<String> warnings = new ArrayList<>();\n\n");
        
        sb.append("    public boolean isValid() {\n");
        sb.append("        return errors.isEmpty();\n");
        sb.append("    }\n\n");
        
        sb.append("    public void addError(String error) {\n");
        sb.append("        errors.add(error);\n");
        sb.append("    }\n\n");
        
        sb.append("    public void addWarning(String warning) {\n");
        sb.append("        warnings.add(warning);\n");
        sb.append("    }\n\n");
        
        sb.append("    public List<String> getErrors() {\n");
        sb.append("        return new ArrayList<>(errors);\n");
        sb.append("    }\n\n");
        
        sb.append("    public List<String> getWarnings() {\n");
        sb.append("        return new ArrayList<>(warnings);\n");
        sb.append("    }\n\n");
        
        sb.append("    public String getErrorMessage() {\n");
        sb.append("        return String.join(\"; \", errors);\n");
        sb.append("    }\n\n");
        
        sb.append("    @Override\n");
        sb.append("    public String toString() {\n");
        sb.append("        return \"ValidationResult{valid=\" + isValid() + \", errors=\" + errors.size() + \", warnings=\" + warnings.size() + \"}\";\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        FileWriter fw = new FileWriter(config.getOutputFolder() + "ValidationResult.java");
        fw.write(sb.toString());
        fw.close();
        
        System.out.println("Generated: ValidationResult.java");
    }
    
    /**
     * Generate validation methods for fields with constraints
     */
    private void generateValidationMethods(StringBuilder sb, String className, Map<String, ColumnInfo> columns) {
        sb.append("\n    // ========== VALIDATION METHODS ==========\n\n");
        
        // Check if any fields have constraints
        boolean hasConstraints = columns.values().stream()
            .anyMatch(col -> hasConstraints(col));
        
        if (!hasConstraints) {
            return; // No constraints to validate
        }
        
        // Generate individual field validation methods
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            ColumnInfo col = entry.getValue();
            if (hasConstraints(col)) {
                generateFieldValidationMethod(sb, col);
            }
        }
        
        // Generate overall validation method
        generateOverallValidationMethod(sb, className, columns);
    }
    
    /**
     * Generate validation method for a specific field
     */
    private void generateFieldValidationMethod(StringBuilder sb, ColumnInfo col) {
        String fieldName = toCamelCase(col.name, false);
        String methodName = toCamelCase(col.name, true);
        
        sb.append("    /**\n");
        sb.append("     * Validates ").append(col.name).append(" field constraints\n");
        sb.append("     * @param value the value to validate\n");
        sb.append("     * @return ValidationResult with validation status and messages\n");
        sb.append("     */\n");
        sb.append("    public ValidationResult validate").append(methodName).append("(").append(col.javaType).append(" value) {\n");
        sb.append("        ValidationResult result = new ValidationResult();\n");
        
        // NOT NULL validation
        if (!col.nullable) {
            sb.append("        \n");
            sb.append("        // NOT NULL constraint\n");
            sb.append("        if (value == null) {\n");
            sb.append("            result.addError(\"").append(col.name).append(" cannot be null\");\n");
            sb.append("            return result;\n");
            sb.append("        }\n");
        }
        
        // String length validation
        if (col.javaType.equals("String") && col.hasLengthConstraint) {
            sb.append("        \n");
            sb.append("        // Length constraint\n");
            sb.append("        if (value != null && value.length() > ").append(col.maxLength).append(") {\n");
            sb.append("            result.addError(\"").append(col.name).append(" length cannot exceed ").append(col.maxLength).append(" characters\");\n");
            sb.append("        }\n");
        }
        
        // Range validation
        if (col.minValue != null || col.maxValue != null) {
            sb.append("        \n");
            sb.append("        // Range constraint\n");
            if (col.minValue != null) {
                sb.append("        if (value != null && ((Number)value).intValue() < ").append(col.minValue).append(") {\n");
                sb.append("            result.addError(\"").append(col.name).append(" must be at least ").append(col.minValue).append("\");\n");
                sb.append("        }\n");
            }
            if (col.maxValue != null) {
                sb.append("        if (value != null && ((Number)value).intValue() > ").append(col.maxValue).append(") {\n");
                sb.append("            result.addError(\"").append(col.name).append(" cannot exceed ").append(col.maxValue).append("\");\n");
                sb.append("        }\n");
            }
        }
        
        // Enum validation
        if (!col.enumValues.isEmpty()) {
            sb.append("        \n");
            sb.append("        // Enum constraint\n");
            sb.append("        if (value != null) {\n");
            sb.append("            java.util.List<String> validValues = java.util.Arrays.asList(");
            for (int i = 0; i < col.enumValues.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(col.enumValues.get(i)).append("\"");
            }
            sb.append(");\n");
            sb.append("            if (!validValues.contains(value.toString())) {\n");
            sb.append("                result.addError(\"").append(col.name).append(" must be one of: \" + validValues);\n");
            sb.append("            }\n");
            sb.append("        }\n");
        }
        
        // Regex validation
        if (col.regexPattern != null) {
            sb.append("        \n");
            sb.append("        // Pattern constraint\n");
            sb.append("        if (value != null && !value.toString().matches(\"").append(col.regexPattern.replace("\"", "\\\"")).append("\")) {\n");
            sb.append("            result.addError(\"").append(col.name).append(" does not match required pattern\");\n");
            sb.append("        }\n");
        }
        
        sb.append("        \n");
        sb.append("        return result;\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate overall validation method for the entity
     */
    private void generateOverallValidationMethod(StringBuilder sb, String className, Map<String, ColumnInfo> columns) {
        sb.append("    /**\n");
        sb.append("     * Validates all fields in this entity\n");
        sb.append("     * @return ValidationResult with all validation errors\n");
        sb.append("     */\n");
        sb.append("    public ValidationResult validate() {\n");
        sb.append("        ValidationResult overallResult = new ValidationResult();\n");
        sb.append("        \n");
        
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            ColumnInfo col = entry.getValue();
            if (hasConstraints(col)) {
                String fieldName = toCamelCase(col.name, false);
                String methodName = toCamelCase(col.name, true);
                
                sb.append("        // Validate ").append(col.name).append("\n");
                sb.append("        ValidationResult ").append(fieldName).append("Result = validate").append(methodName).append("(this.").append(fieldName).append(");\n");
                sb.append("        if (!").append(fieldName).append("Result.isValid()) {\n");
                sb.append("            ").append(fieldName).append("Result.getErrors().forEach(overallResult::addError);\n");
                sb.append("        }\n");
                sb.append("        \n");
            }
        }
        
        sb.append("        return overallResult;\n");
        sb.append("    }\n\n");
        
        // Generate validation exception method
        sb.append("    /**\n");
        sb.append("     * Validates all fields and throws exception if invalid\n");
        sb.append("     * @throws IllegalStateException if validation fails\n");
        sb.append("     */\n");
        sb.append("    public void validateAndThrow() {\n");
        sb.append("        ValidationResult result = validate();\n");
        sb.append("        if (!result.isValid()) {\n");
        sb.append("            throw new IllegalStateException(\"Validation failed: \" + result.getErrorMessage());\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate constraint constants
     */
    private void generateConstraintConstants(StringBuilder sb, Map<String, ColumnInfo> columns) {
        sb.append("    // ========== CONSTRAINT CONSTANTS ==========\n\n");
        
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            ColumnInfo col = entry.getValue();
            String constantPrefix = col.name.toUpperCase();
            
            // Length constraints
            if (col.hasLengthConstraint) {
                sb.append("    public static final int ").append(constantPrefix).append("_MAX_LENGTH = ").append(col.maxLength).append(";\n");
            }
            
            // Range constraints
            if (col.minValue != null) {
                sb.append("    public static final int ").append(constantPrefix).append("_MIN_VALUE = ").append(col.minValue).append(";\n");
            }
            if (col.maxValue != null) {
                sb.append("    public static final int ").append(constantPrefix).append("_MAX_VALUE = ").append(col.maxValue).append(";\n");
            }
            
            // Enum values
            if (!col.enumValues.isEmpty()) {
                sb.append("    public static final String[] ").append(constantPrefix).append("_VALID_VALUES = {");
                for (int i = 0; i < col.enumValues.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append("\"").append(col.enumValues.get(i)).append("\"");
                }
                sb.append("};\n");
            }
            
            // Regex patterns
            if (col.regexPattern != null) {
                sb.append("    public static final String ").append(constantPrefix).append("_PATTERN = \"").append(col.regexPattern.replace("\"", "\\\"")).append("\";\n");
            }
        }
        
        sb.append("\n");
    }
    
    /**
     * Generate constraint validation logic for setter methods
     */
    private void generateSetterConstraintValidation(StringBuilder sb, ColumnInfo col, String fieldName) {
        // NOT NULL validation
        if (!col.nullable) {
            sb.append("        if (").append(fieldName).append(" == null) {\n");
            sb.append("            throw new IllegalArgumentException(\"").append(col.name).append(" cannot be null\");\n");
            sb.append("        }\n");
        }
        
        // String length validation
        if (col.javaType.equals("String") && col.hasLengthConstraint) {
            sb.append("        if (").append(fieldName).append(" != null && ").append(fieldName).append(".length() > ").append(col.maxLength).append(") {\n");
            sb.append("            throw new IllegalArgumentException(\"").append(col.name).append(" length cannot exceed ").append(col.maxLength).append(" characters\");\n");
            sb.append("        }\n");
        }
        
        // Range validation for numeric types
        if (col.minValue != null || col.maxValue != null) {
            if (col.minValue != null) {
                sb.append("        if (").append(fieldName).append(" != null && ((Number)").append(fieldName).append(").intValue() < ").append(col.minValue).append(") {\n");
                sb.append("            throw new IllegalArgumentException(\"").append(col.name).append(" must be at least ").append(col.minValue).append("\");\n");
                sb.append("        }\n");
            }
            if (col.maxValue != null) {
                sb.append("        if (").append(fieldName).append(" != null && ((Number)").append(fieldName).append(").intValue() > ").append(col.maxValue).append(") {\n");
                sb.append("            throw new IllegalArgumentException(\"").append(col.name).append(" cannot exceed ").append(col.maxValue).append("\");\n");
                sb.append("        }\n");
            }
        }
        
        // Enum validation
        if (!col.enumValues.isEmpty()) {
            sb.append("        if (").append(fieldName).append(" != null) {\n");
            sb.append("            java.util.List<String> validValues = java.util.Arrays.asList(");
            for (int i = 0; i < col.enumValues.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(col.enumValues.get(i)).append("\"");
            }
            sb.append(");\n");
            sb.append("            if (!validValues.contains(").append(fieldName).append(".toString())) {\n");
            sb.append("                throw new IllegalArgumentException(\"").append(col.name).append(" must be one of: \" + validValues);\n");
            sb.append("            }\n");
            sb.append("        }\n");
        }
        
        // Regex validation
        if (col.regexPattern != null) {
            sb.append("        if (").append(fieldName).append(" != null && !").append(fieldName).append(".toString().matches(\"").append(col.regexPattern.replace("\"", "\\\"")).append("\")) {\n");
            sb.append("            throw new IllegalArgumentException(\"").append(col.name).append(" does not match required pattern\");\n");
            sb.append("        }\n");
        }
    }
    
    /**
     * Check if a column has any constraints that need validation
     */
    private boolean hasConstraints(ColumnInfo col) {
        return !col.nullable || 
               col.hasLengthConstraint || 
               col.minValue != null || 
               col.maxValue != null || 
               !col.enumValues.isEmpty() || 
               col.regexPattern != null;
    }
    
    // Helper classes
    static class ColumnInfo {
        String name;
        String javaType;
        String sqlType;
        int size;
        boolean nullable;
        boolean autoIncrement;
        String defaultValue;
        boolean isPrimaryKey;
        boolean isForeignKey;
        String foreignKeyReference;
        
        // Enhanced constraint information
        List<CheckConstraint> checkConstraints = new ArrayList<>();
        List<String> enumValues = new ArrayList<>();
        Integer minValue;
        Integer maxValue;
        String regexPattern;
        boolean hasLengthConstraint;
        int maxLength;
    }
    
    static class IndexInfo {
        String name;
        boolean unique;
        List<String> columns = new ArrayList<>();
    }
    
    static class CheckConstraint {
        String name;
        String clause;
        String constraintType; // RANGE, ENUM, REGEX, LENGTH
        Map<String, Object> parameters = new HashMap<>();
    }
    
    static class UniqueConstraint {
        String name;
        List<String> columns;
        boolean isComposite;
    }
    
    /**
     * Configuration class for POJO Generator
     */
    static class PojoGeneratorConfig {
        // Database configuration
        private String dbUrl = DEFAULT_DB_URL;
        private String dbUser = DEFAULT_DB_USER;
        private String dbPassword = DEFAULT_DB_PASS;
        private String schema = "public";
        
        // Output configuration
        private String outputFolder = DEFAULT_OUTPUT_FOLDER;
        private String packageName = "com.example.pojogenerator.pojos";
        
        // Generation options
        private boolean generateValidationMethods = false;  // Disabled - only use setter validation
        private boolean generateConstraintDocumentation = true;
        private boolean generateValidationResultClass = false;  // Disabled - not needed for setter validation
        private boolean includeCheckConstraints = true;
        private boolean includeUniqueConstraints = true;
        private boolean generateConstraintConstants = false;  // Disabled - keep POJOs clean
        private boolean useIfStatementsForValidation = true;
        
        // Getters and setters
        public String getDbUrl() { return dbUrl; }
        public void setDbUrl(String dbUrl) { this.dbUrl = dbUrl; }
        
        public String getDbUser() { return dbUser; }
        public void setDbUser(String dbUser) { this.dbUser = dbUser; }
        
        public String getDbPassword() { return dbPassword; }
        public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }
        
        public String getSchema() { return schema; }
        public void setSchema(String schema) { this.schema = schema; }
        
        public String getOutputFolder() { return outputFolder; }
        public void setOutputFolder(String outputFolder) { this.outputFolder = outputFolder; }
        
        public String getPackageName() { return packageName; }
        public void setPackageName(String packageName) { this.packageName = packageName; }
        
        public boolean isGenerateValidationMethods() { return generateValidationMethods; }
        public void setGenerateValidationMethods(boolean generateValidationMethods) { this.generateValidationMethods = generateValidationMethods; }
        
        public boolean isGenerateConstraintDocumentation() { return generateConstraintDocumentation; }
        public void setGenerateConstraintDocumentation(boolean generateConstraintDocumentation) { this.generateConstraintDocumentation = generateConstraintDocumentation; }
        
        public boolean isGenerateValidationResultClass() { return generateValidationResultClass; }
        public void setGenerateValidationResultClass(boolean generateValidationResultClass) { this.generateValidationResultClass = generateValidationResultClass; }
        
        public boolean isIncludeCheckConstraints() { return includeCheckConstraints; }
        public void setIncludeCheckConstraints(boolean includeCheckConstraints) { this.includeCheckConstraints = includeCheckConstraints; }
        
        public boolean isIncludeUniqueConstraints() { return includeUniqueConstraints; }
        public void setIncludeUniqueConstraints(boolean includeUniqueConstraints) { this.includeUniqueConstraints = includeUniqueConstraints; }
        
        public boolean isGenerateConstraintConstants() { return generateConstraintConstants; }
        public void setGenerateConstraintConstants(boolean generateConstraintConstants) { this.generateConstraintConstants = generateConstraintConstants; }
        
        public boolean isUseIfStatementsForValidation() { return useIfStatementsForValidation; }
        public void setUseIfStatementsForValidation(boolean useIfStatementsForValidation) { this.useIfStatementsForValidation = useIfStatementsForValidation; }
    }
}
