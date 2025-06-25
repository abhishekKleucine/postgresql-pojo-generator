package com.example.daoGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JDBC DAO Generator
 * Generates complete DAO layer (interfaces, implementations, row mappers, SQL constants)
 * from PostgreSQL database schema and existing POJO classes
 * 
 * Features:
 * - Database schema introspection
 * - POJO analysis and field mapping
 * - Complete DAO layer generation
 * - Constraint-aware validation
 * - CRUD operations with custom finders
 */
public class JdbcDaoGenerator {
    
    // Configuration
    private final JdbcDaoGeneratorConfig config;
    
    // Default configuration
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/qa_";
    private static final String DEFAULT_DB_USER = "postgres";
    private static final String DEFAULT_DB_PASS = "postgres";
    private static final String DEFAULT_POJO_PACKAGE = "com.example.pojogenerator.pojos";
    private static final String DEFAULT_DAO_PACKAGE = "com.example.daoGenerator.dao";
    
    public JdbcDaoGenerator() {
        this.config = new JdbcDaoGeneratorConfig();
    }
    
    public JdbcDaoGenerator(JdbcDaoGeneratorConfig config) {
        this.config = config;
    }
    
    public static void main(String[] args) {
        System.out.println("Starting JDBC DAO Generator...");
        
        JdbcDaoGenerator generator = new JdbcDaoGenerator();
        generator.generateAllDaos();
        
        System.out.println("DAO generation completed!");
    }
    
    /**
     * Main generation method
     */
    public void generateAllDaos() {
        try {
            // Create output directories
            createOutputDirectories();
            
            // Step 1: Extract database schema
            List<TableInfo> tables = extractDatabaseSchema();
            
            // Step 2: Analyze existing POJOs
            List<PojoInfo> pojos = analyzeExistingPojos();
            
            // Step 3: Match tables with POJOs
            List<DaoGenerationUnit> units = matchTablesWithPojos(tables, pojos);
            
            // Step 4: Generate DAO for each table/POJO pair
            int successCount = 0;
            for (DaoGenerationUnit unit : units) {
                try {
                    generateDaoForTable(unit);
                    successCount++;
                } catch (Exception e) {
                    System.err.println("❌ Failed to generate DAO for: " + unit.getPojo().getClassName());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Successfully generated " + successCount + " DAO classes");
            
        } catch (Exception e) {
            System.err.println("Error generating DAOs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Extract database schema information
     */
    private List<TableInfo> extractDatabaseSchema() {
        List<TableInfo> tables = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword())) {
            System.out.println("Connected to database successfully!");
            
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tableRs = meta.getTables(null, config.getSchema(), "%", new String[]{"TABLE"});
            
            while (tableRs.next()) {
                String tableName = tableRs.getString("TABLE_NAME");
                System.out.println("Processing table: " + tableName);
                
                TableInfo table = new TableInfo(tableName);
                
                // Extract columns with constraints
                table.setColumns(extractColumnsWithConstraints(conn, meta, tableName));
                
                // Extract primary keys
                table.setPrimaryKeys(extractPrimaryKeys(meta, tableName));
                
                // Extract foreign keys
                table.setForeignKeys(extractForeignKeys(meta, tableName));
                
                tables.add(table);
            }
            
            System.out.println("Extracted " + tables.size() + " tables from database");
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to extract database schema", e);
        }
        
        return tables;
    }
    
    /**
     * Extract columns with constraint information
     */
    private List<ColumnInfo> extractColumnsWithConstraints(Connection conn, DatabaseMetaData meta, String tableName) throws SQLException {
        List<ColumnInfo> columns = new ArrayList<>();
        
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
            
            columns.add(colInfo);
        }
        
        return columns;
    }
    
    /**
     * Extract primary keys
     */
    private Set<String> extractPrimaryKeys(DatabaseMetaData meta, String tableName) throws SQLException {
        Set<String> keys = new HashSet<>();
        ResultSet rs = meta.getPrimaryKeys(null, null, tableName);
        while (rs.next()) {
            keys.add(rs.getString("COLUMN_NAME"));
        }
        return keys;
    }
    
    /**
     * Extract foreign keys
     */
    private Map<String, String> extractForeignKeys(DatabaseMetaData meta, String tableName) throws SQLException {
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
    
    /**
     * Analyze existing POJO classes
     */
    private List<PojoInfo> analyzeExistingPojos() {
        List<PojoInfo> pojos = new ArrayList<>();
        
        // Scan POJO directory
        File pojoDir = new File("src/main/java/com/example/pojogenerator/pojos/");
        File[] pojoFiles = pojoDir.listFiles((dir, name) -> 
            name.endsWith(".java") && !name.equals("ValidationResult.java"));
        
        if (pojoFiles == null) {
            throw new RuntimeException("POJO directory not found or empty");
        }
        
        System.out.println("Found " + pojoFiles.length + " POJO files to analyze");
        
        for (File pojoFile : pojoFiles) {
            try {
                PojoInfo pojo = analyzeSinglePojo(pojoFile);
                pojos.add(pojo);
                System.out.println("Analyzed POJO: " + pojo.getClassName());
            } catch (Exception e) {
                System.err.println("Failed to analyze POJO: " + pojoFile.getName() + " - " + e.getMessage());
            }
        }
        
        return pojos;
    }
    
    /**
     * Analyze a single POJO file
     */
    private PojoInfo analyzeSinglePojo(File pojoFile) throws IOException {
        PojoInfo pojo = new PojoInfo();
        
        String content = Files.readString(pojoFile.toPath());
        
        // Extract class name
        pojo.setClassName(extractClassName(content));
        
        // Extract field mappings
        pojo.setFieldMappings(extractFieldMappings(content));
        
        // Determine corresponding table name
        pojo.setTableName(convertClassNameToTableName(pojo.getClassName()));
        
        return pojo;
    }
    
    /**
     * Extract class name from Java file content
     */
    private String extractClassName(String content) {
        Pattern pattern = Pattern.compile("public class (\\w+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("Could not extract class name");
    }
    
    /**
     * Extract field mappings from POJO
     */
    private Map<String, FieldMapping> extractFieldMappings(String content) {
        Map<String, FieldMapping> mappings = new HashMap<>();
        
        // Pattern to match field declarations with getters/setters
        Pattern fieldPattern = Pattern.compile("private (\\w+(?:\\.\\w+)*(?:<[^>]+>)?) (\\w+);");
        Matcher fieldMatcher = fieldPattern.matcher(content);
        
        while (fieldMatcher.find()) {
            String javaType = fieldMatcher.group(1);
            String fieldName = fieldMatcher.group(2);
            
            // Convert field name to database column name
            String columnName = convertFieldNameToColumnName(fieldName);
            
            FieldMapping mapping = new FieldMapping();
            mapping.fieldName = fieldName;
            mapping.columnName = columnName;
            mapping.javaType = javaType;
            
            mappings.put(fieldName, mapping);
        }
        
        return mappings;
    }
    
    /**
     * Convert class name to table name (CamelCase to snake_case)
     */
    private String convertClassNameToTableName(String className) {
        return className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    /**
     * Convert field name to column name (camelCase to snake_case)
     */
    private String convertFieldNameToColumnName(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    /**
     * Match tables with POJOs
     */
    private List<DaoGenerationUnit> matchTablesWithPojos(List<TableInfo> tables, List<PojoInfo> pojos) {
        List<DaoGenerationUnit> units = new ArrayList<>();
        
        Map<String, PojoInfo> pojoMap = new HashMap<>();
        for (PojoInfo pojo : pojos) {
            pojoMap.put(pojo.getTableName(), pojo);
        }
        
        for (TableInfo table : tables) {
            PojoInfo matchingPojo = pojoMap.get(table.getName());
            
            if (matchingPojo != null) {
                DaoGenerationUnit unit = new DaoGenerationUnit(table, matchingPojo);
                units.add(unit);
                System.out.println("Matched table '" + table.getName() + 
                                 "' with POJO '" + matchingPojo.getClassName() + "'");
            } else {
                System.out.println("WARNING: No POJO found for table: " + table.getName());
            }
        }
        
        System.out.println("Created " + units.size() + " DAO generation units");
        return units;
    }
    
    /**
     * Generate DAO for a single table/POJO pair
     */
    private void generateDaoForTable(DaoGenerationUnit unit) throws IOException {
        TableInfo table = unit.getTable();
        PojoInfo pojo = unit.getPojo();
        
        System.out.println("Generating DAO for: " + pojo.getClassName());
        
        // Generate DAO interface
        generateDaoInterface(table, pojo);
        
        // Generate JDBC implementation
        generateJdbcImplementation(table, pojo);
        
        // Generate Row Mapper
        generateRowMapper(table, pojo);
        
        // Generate SQL Constants
        generateSqlConstants(table, pojo);
        
        System.out.println("✅ Generated DAO for: " + pojo.getClassName());
    }
    
    /**
     * Generate DAO interface
     */
    private void generateDaoInterface(TableInfo table, PojoInfo pojo) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        String daoName = pojo.getClassName() + "Dao";
        String pojoClassName = pojo.getClassName();
        
        // Package and imports
        sb.append("package ").append(config.getDaoInterfacePackage()).append(";\n\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Optional;\n");
        sb.append("import ").append(config.getPojoPackage()).append(".").append(pojoClassName).append(";\n\n");
        
        // Interface documentation
        sb.append("/**\n");
        sb.append(" * DAO interface for ").append(pojoClassName).append(" entity\n");
        sb.append(" * Table: ").append(table.getName()).append("\n");
        sb.append(" * Generated by JDBC DAO Generator\n");
        sb.append(" */\n");
        sb.append("public interface ").append(daoName).append(" {\n\n");
        
        // Basic CRUD methods
        sb.append("    // Basic CRUD operations\n");
        sb.append("    Optional<").append(pojoClassName).append("> findById(").append(getPrimaryKeyType(table)).append(" id);\n");
        sb.append("    List<").append(pojoClassName).append("> findAll();\n");
        sb.append("    ").append(pojoClassName).append(" save(").append(pojoClassName).append(" entity);\n");
        sb.append("    void deleteById(").append(getPrimaryKeyType(table)).append(" id);\n");
        sb.append("    boolean existsById(").append(getPrimaryKeyType(table)).append(" id);\n");
        sb.append("    long count();\n\n");
        
        // Foreign key based finders
        generateForeignKeyFinders(sb, table, pojo);
        
        sb.append("}\n");
        
        // Write to file
        String fileName = config.getDaoInterfaceOutputDir() + "/" + daoName + ".java";
        writeToFile(fileName, sb.toString());
    }
    
    /**
     * Generate JDBC implementation
     */
    private void generateJdbcImplementation(TableInfo table, PojoInfo pojo) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        String daoName = pojo.getClassName() + "Dao";
        String implName = "Jdbc" + pojo.getClassName() + "Dao";
        String pojoClassName = pojo.getClassName();
        String rowMapperName = pojo.getClassName() + "RowMapper";
        String sqlConstantsName = pojo.getClassName() + "Sql";
        
        // Package and imports
        sb.append("package ").append(config.getDaoImplPackage()).append(";\n\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Map;\n");
        sb.append("import java.util.Optional;\n");
        sb.append("import org.springframework.dao.EmptyResultDataAccessException;\n");
        sb.append("import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;\n");
        sb.append("import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;\n");
        sb.append("import org.springframework.stereotype.Repository;\n");
        sb.append("import ").append(config.getDaoInterfacePackage()).append(".").append(daoName).append(";\n");
        sb.append("import ").append(config.getPojoPackage()).append(".").append(pojoClassName).append(";\n");
        sb.append("import ").append(config.getRowMapperPackage()).append(".").append(rowMapperName).append(";\n");
        sb.append("import ").append(config.getSqlConstantsPackage()).append(".").append(sqlConstantsName).append(";\n\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * JDBC implementation of ").append(daoName).append("\n");
        sb.append(" * Generated by JDBC DAO Generator\n");
        sb.append(" */\n");
        sb.append("@Repository\n");
        sb.append("public class ").append(implName).append(" implements ").append(daoName).append(" {\n\n");
        
        // Fields
        sb.append("    private final NamedParameterJdbcTemplate jdbcTemplate;\n");
        sb.append("    private final ").append(rowMapperName).append(" rowMapper;\n\n");
        
        // Constructor
        sb.append("    public ").append(implName).append("(NamedParameterJdbcTemplate jdbcTemplate) {\n");
        sb.append("        this.jdbcTemplate = jdbcTemplate;\n");
        sb.append("        this.rowMapper = new ").append(rowMapperName).append("();\n");
        sb.append("    }\n\n");
        
        // Implement CRUD methods
        generateCrudImplementations(sb, table, pojo, sqlConstantsName);
        
        sb.append("}\n");
        
        // Write to file
        String fileName = config.getDaoImplOutputDir() + "/" + implName + ".java";
        writeToFile(fileName, sb.toString());
    }
    
    /**
     * Generate Row Mapper
     */
    private void generateRowMapper(TableInfo table, PojoInfo pojo) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        String rowMapperName = pojo.getClassName() + "RowMapper";
        String pojoClassName = pojo.getClassName();
        
        // Package and imports
        sb.append("package ").append(config.getRowMapperPackage()).append(";\n\n");
        sb.append("import java.sql.ResultSet;\n");
        sb.append("import java.sql.SQLException;\n");
        sb.append("import org.springframework.jdbc.core.RowMapper;\n");
        sb.append("import ").append(config.getPojoPackage()).append(".").append(pojoClassName).append(";\n");
        
        // Add JSON imports if needed
        boolean needsJsonImports = pojo.getFieldMappings().values().stream()
            .anyMatch(field -> field.javaType.contains("JsonNode"));
        if (needsJsonImports) {
            sb.append("import com.fasterxml.jackson.databind.JsonNode;\n");
            sb.append("import com.fasterxml.jackson.databind.ObjectMapper;\n");
        }
        
        sb.append("\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * Row mapper for ").append(pojoClassName).append(" entity\n");
        sb.append(" * Generated by JDBC DAO Generator\n");
        sb.append(" */\n");
        sb.append("public class ").append(rowMapperName).append(" implements RowMapper<").append(pojoClassName).append("> {\n\n");
        
        if (needsJsonImports) {
            sb.append("    private final ObjectMapper objectMapper = new ObjectMapper();\n\n");
        }
        
        // mapRow method
        sb.append("    @Override\n");
        sb.append("    public ").append(pojoClassName).append(" mapRow(ResultSet rs, int rowNum) throws SQLException {\n");
        sb.append("        ").append(pojoClassName).append(" entity = new ").append(pojoClassName).append("();\n\n");
        
        // Map each field
        for (FieldMapping field : pojo.getFieldMappings().values()) {
            generateFieldMapping(sb, field);
        }
        
        sb.append("\n        return entity;\n");
        sb.append("    }\n");
        
        // Helper methods for JSON parsing
        if (needsJsonImports) {
            sb.append("\n    private JsonNode parseJsonNode(String json) {\n");
            sb.append("        if (json == null || json.trim().isEmpty()) {\n");
            sb.append("            return null;\n");
            sb.append("        }\n");
            sb.append("        try {\n");
            sb.append("            return objectMapper.readTree(json);\n");
            sb.append("        } catch (Exception e) {\n");
            sb.append("            throw new RuntimeException(\"Failed to parse JSON: \" + json, e);\n");
            sb.append("        }\n");
            sb.append("    }\n");
        }
        
        sb.append("}\n");
        
        // Write to file
        String fileName = config.getRowMapperOutputDir() + "/" + rowMapperName + ".java";
        writeToFile(fileName, sb.toString());
    }
    
    /**
     * Generate SQL Constants
     */
    private void generateSqlConstants(TableInfo table, PojoInfo pojo) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        String sqlConstantsName = pojo.getClassName() + "Sql";
        
        // Package
        sb.append("package ").append(config.getSqlConstantsPackage()).append(";\n\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * SQL constants for ").append(pojo.getClassName()).append(" entity\n");
        sb.append(" * Generated by JDBC DAO Generator\n");
        sb.append(" */\n");
        sb.append("public final class ").append(sqlConstantsName).append(" {\n\n");
        
        // Generate SQL constants
        generateSqlQueries(sb, table, pojo);
        
        sb.append("    private ").append(sqlConstantsName).append("() {\n");
        sb.append("        // Utility class\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        // Write to file
        String fileName = config.getSqlConstantsOutputDir() + "/" + sqlConstantsName + ".java";
        writeToFile(fileName, sb.toString());
    }
    
    // Helper methods for generation
    
    private void generateForeignKeyFinders(StringBuilder sb, TableInfo table, PojoInfo pojo) {
        if (!table.getForeignKeys().isEmpty()) {
            sb.append("    // Foreign key based finders\n");
            for (Map.Entry<String, String> fk : table.getForeignKeys().entrySet()) {
                String columnName = fk.getKey();
                String fieldName = convertColumnNameToFieldName(columnName);
                String methodName = "findBy" + capitalize(fieldName);
                
                sb.append("    List<").append(pojo.getClassName()).append("> ").append(methodName).append("(Long ").append(fieldName).append(");\n");
            }
            sb.append("\n");
        }
    }
    
    private void generateCrudImplementations(StringBuilder sb, TableInfo table, PojoInfo pojo, String sqlConstantsName) {
        String pojoClassName = pojo.getClassName();
        String primaryKeyType = getPrimaryKeyType(table);
        
        // findById
        sb.append("    @Override\n");
        sb.append("    public Optional<").append(pojoClassName).append("> findById(").append(primaryKeyType).append(" id) {\n");
        sb.append("        try {\n");
        sb.append("            ").append(pojoClassName).append(" result = jdbcTemplate.queryForObject(\n");
        sb.append("                ").append(sqlConstantsName).append(".FIND_BY_ID,\n");
        sb.append("                Map.of(\"id\", id),\n");
        sb.append("                rowMapper\n");
        sb.append("            );\n");
        sb.append("            return Optional.ofNullable(result);\n");
        sb.append("        } catch (EmptyResultDataAccessException e) {\n");
        sb.append("            return Optional.empty();\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        
        // findAll
        sb.append("    @Override\n");
        sb.append("    public List<").append(pojoClassName).append("> findAll() {\n");
        sb.append("        return jdbcTemplate.query(").append(sqlConstantsName).append(".FIND_ALL, rowMapper);\n");
        sb.append("    }\n\n");
        
        // save (complete implementation)
        sb.append("    @Override\n");
        sb.append("    public ").append(pojoClassName).append(" save(").append(pojoClassName).append(" entity) {\n");
        sb.append("        if (entity.getId() == null) {\n");
        sb.append("            return insert(entity);\n");
        sb.append("        } else {\n");
        sb.append("            return update(entity);\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        
        // insert method
        sb.append("    private ").append(pojoClassName).append(" insert(").append(pojoClassName).append(" entity) {\n");
        sb.append("        // Set audit fields\n");
        sb.append("        long now = System.currentTimeMillis();\n");
        sb.append("        if (entity.getCreatedAt() == null) {\n");
        sb.append("            entity.setCreatedAt(now);\n");
        sb.append("        }\n");
        sb.append("        entity.setModifiedAt(now);\n\n");
        sb.append("        // Execute INSERT with parameter mapping\n");
        sb.append("        MapSqlParameterSource params = createParameterMap(entity);\n");
        sb.append("        Long generatedId = jdbcTemplate.queryForObject(\n");
        sb.append("            ").append(sqlConstantsName).append(".INSERT,\n");
        sb.append("            params,\n");
        sb.append("            Long.class\n");
        sb.append("        );\n");
        sb.append("        entity.setId(generatedId);\n");
        sb.append("        return entity;\n");
        sb.append("    }\n\n");
        
        // update method
        sb.append("    private ").append(pojoClassName).append(" update(").append(pojoClassName).append(" entity) {\n");
        sb.append("        // Update audit fields\n");
        sb.append("        entity.setModifiedAt(System.currentTimeMillis());\n\n");
        sb.append("        // Execute UPDATE with parameter mapping\n");
        sb.append("        MapSqlParameterSource params = createParameterMap(entity);\n");
        sb.append("        int rowsAffected = jdbcTemplate.update(\n");
        sb.append("            ").append(sqlConstantsName).append(".UPDATE,\n");
        sb.append("            params\n");
        sb.append("        );\n\n");
        sb.append("        if (rowsAffected == 0) {\n");
        sb.append("            throw new RuntimeException(\"Entity with id \" + entity.getId() + \" not found for update\");\n");
        sb.append("        }\n");
        sb.append("        return entity;\n");
        sb.append("    }\n\n");
        
        // parameter mapping helper
        generateParameterMappingMethod(sb, pojo);
        
        // deleteById
        sb.append("    @Override\n");
        sb.append("    public void deleteById(").append(primaryKeyType).append(" id) {\n");
        sb.append("        jdbcTemplate.update(").append(sqlConstantsName).append(".DELETE_BY_ID, Map.of(\"id\", id));\n");
        sb.append("    }\n\n");
        
        // existsById
        sb.append("    @Override\n");
        sb.append("    public boolean existsById(").append(primaryKeyType).append(" id) {\n");
        sb.append("        Integer count = jdbcTemplate.queryForObject(\n");
        sb.append("            ").append(sqlConstantsName).append(".EXISTS_BY_ID,\n");
        sb.append("            Map.of(\"id\", id),\n");
        sb.append("            Integer.class\n");
        sb.append("        );\n");
        sb.append("        return count != null && count > 0;\n");
        sb.append("    }\n\n");
        
        // count
        sb.append("    @Override\n");
        sb.append("    public long count() {\n");
        sb.append("        Long result = jdbcTemplate.queryForObject(").append(sqlConstantsName).append(".COUNT, Map.of(), Long.class);\n");
        sb.append("        return result != null ? result : 0L;\n");
        sb.append("    }\n\n");
        
        // Foreign key finders
        for (Map.Entry<String, String> fk : table.getForeignKeys().entrySet()) {
            String columnName = fk.getKey();
            String fieldName = convertColumnNameToFieldName(columnName);
            String methodName = "findBy" + capitalize(fieldName);
            
            sb.append("    @Override\n");
            sb.append("    public List<").append(pojoClassName).append("> ").append(methodName).append("(Long ").append(fieldName).append(") {\n");
            sb.append("        return jdbcTemplate.query(\n");
            sb.append("            ").append(sqlConstantsName).append(".FIND_BY_").append(columnName.toUpperCase()).append(",\n");
            sb.append("            Map.of(\"").append(fieldName).append("\", ").append(fieldName).append("),\n");
            sb.append("            rowMapper\n");
            sb.append("        );\n");
            sb.append("    }\n\n");
        }
    }
    
    private void generateFieldMapping(StringBuilder sb, FieldMapping field) {
        String setterName = "set" + capitalize(field.fieldName);
        
        if (field.javaType.contains("JsonNode")) {
            sb.append("        String ").append(field.fieldName).append("Json = rs.getString(\"").append(field.columnName).append("\");\n");
            sb.append("        if (").append(field.fieldName).append("Json != null) {\n");
            sb.append("            entity.").append(setterName).append("(parseJsonNode(").append(field.fieldName).append("Json));\n");
            sb.append("        }\n");
        } else if (field.javaType.equals("Long")) {
            sb.append("        entity.").append(setterName).append("(rs.getLong(\"").append(field.columnName).append("\"));\n");
        } else if (field.javaType.equals("Integer")) {
            sb.append("        entity.").append(setterName).append("(rs.getInt(\"").append(field.columnName).append("\"));\n");
        } else if (field.javaType.equals("String")) {
            sb.append("        entity.").append(setterName).append("(rs.getString(\"").append(field.columnName).append("\"));\n");
        } else if (field.javaType.equals("Boolean")) {
            sb.append("        entity.").append(setterName).append("(rs.getBoolean(\"").append(field.columnName).append("\"));\n");
        } else if (field.javaType.contains("LocalDateTime")) {
            sb.append("        Timestamp ").append(field.fieldName).append("Ts = rs.getTimestamp(\"").append(field.columnName).append("\");\n");
            sb.append("        if (").append(field.fieldName).append("Ts != null) {\n");
            sb.append("            entity.").append(setterName).append("(").append(field.fieldName).append("Ts.toLocalDateTime());\n");
            sb.append("        }\n");
        } else {
            // Default to String
            sb.append("        entity.").append(setterName).append("(rs.getString(\"").append(field.columnName).append("\"));\n");
        }
    }
    
    private void generateSqlQueries(StringBuilder sb, TableInfo table, PojoInfo pojo) {
        String tableName = table.getName();
        
        // Column list for SELECT
        List<String> columnNames = new ArrayList<>();
        List<String> insertColumns = new ArrayList<>();
        List<String> insertValues = new ArrayList<>();
        List<String> updateSets = new ArrayList<>();
        
        for (FieldMapping field : pojo.getFieldMappings().values()) {
            columnNames.add(field.columnName);
            
            // Skip ID for INSERT
            if (!field.fieldName.equals("id")) {
                insertColumns.add(field.columnName);
                if (field.javaType.contains("JsonNode")) {
                    insertValues.add(":" + field.fieldName + "::jsonb");
                    updateSets.add(field.columnName + " = :" + field.fieldName + "::jsonb");
                } else {
                    insertValues.add(":" + field.fieldName);
                    updateSets.add(field.columnName + " = :" + field.fieldName);
                }
            }
        }
        
        String columnList = String.join(", ", columnNames);
        String insertColumnList = String.join(", ", insertColumns);
        String insertValueList = String.join(", ", insertValues);
        String updateSetList = String.join(",\n            ", updateSets);
        
        // FIND_BY_ID
        sb.append("    public static final String FIND_BY_ID = \"\"\"\n");
        sb.append("        SELECT ").append(columnList).append("\n");
        sb.append("        FROM ").append(tableName).append("\n");
        sb.append("        WHERE id = :id\n");
        sb.append("        \"\"\";\n\n");
        
        // FIND_ALL
        sb.append("    public static final String FIND_ALL = \"\"\"\n");
        sb.append("        SELECT ").append(columnList).append("\n");
        sb.append("        FROM ").append(tableName).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // INSERT
        sb.append("    public static final String INSERT = \"\"\"\n");
        sb.append("        INSERT INTO ").append(tableName).append(" (").append(insertColumnList).append(")\n");
        sb.append("        VALUES (").append(insertValueList).append(")\n");
        sb.append("        RETURNING id\n");
        sb.append("        \"\"\";\n\n");
        
        // UPDATE
        sb.append("    public static final String UPDATE = \"\"\"\n");
        sb.append("        UPDATE ").append(tableName).append(" SET\n");
        sb.append("            ").append(updateSetList).append("\n");
        sb.append("        WHERE id = :id\n");
        sb.append("        \"\"\";\n\n");
        
        // DELETE_BY_ID
        sb.append("    public static final String DELETE_BY_ID = \"\"\"\n");
        sb.append("        DELETE FROM ").append(tableName).append("\n");
        sb.append("        WHERE id = :id\n");
        sb.append("        \"\"\";\n\n");
        
        // EXISTS_BY_ID
        sb.append("    public static final String EXISTS_BY_ID = \"\"\"\n");
        sb.append("        SELECT COUNT(*) FROM ").append(tableName).append("\n");
        sb.append("        WHERE id = :id\n");
        sb.append("        \"\"\";\n\n");
        
        // COUNT
        sb.append("    public static final String COUNT = \"\"\"\n");
        sb.append("        SELECT COUNT(*) FROM ").append(tableName).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // Foreign key finders
        for (Map.Entry<String, String> fk : table.getForeignKeys().entrySet()) {
            String columnName = fk.getKey();
            sb.append("    public static final String FIND_BY_").append(columnName.toUpperCase()).append(" = \"\"\"\n");
            sb.append("        SELECT ").append(columnList).append("\n");
            sb.append("        FROM ").append(tableName).append("\n");
            sb.append("        WHERE ").append(columnName).append(" = :").append(convertColumnNameToFieldName(columnName)).append("\n");
            sb.append("        \"\"\";\n\n");
        }
    }
    
    /**
     * Generate parameter mapping method
     */
    private void generateParameterMappingMethod(StringBuilder sb, PojoInfo pojo) {
        sb.append("    private MapSqlParameterSource createParameterMap(").append(pojo.getClassName()).append(" entity) {\n");
        sb.append("        MapSqlParameterSource params = new MapSqlParameterSource();\n\n");
        
        for (FieldMapping field : pojo.getFieldMappings().values()) {
            String getterName = "get" + capitalize(field.fieldName);
            
            if (field.javaType.contains("JsonNode")) {
                sb.append("        if (entity.").append(getterName).append("() != null) {\n");
                sb.append("            params.addValue(\"").append(field.fieldName).append("\", entity.").append(getterName).append("().toString());\n");
                sb.append("        } else {\n");
                sb.append("            params.addValue(\"").append(field.fieldName).append("\", null);\n");
                sb.append("        }\n");
            } else {
                sb.append("        params.addValue(\"").append(field.fieldName).append("\", entity.").append(getterName).append("());\n");
            }
        }
        
        sb.append("\n        return params;\n");
        sb.append("    }\n\n");
    }
    
    // Helper methods
    
    private String getPrimaryKeyType(TableInfo table) {
        // For simplicity, assume Long for primary keys
        return "Long";
    }
    
    private String convertColumnNameToFieldName(String columnName) {
        String[] parts = columnName.split("_");
        StringBuilder result = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            result.append(capitalize(parts[i]));
        }
        return result.toString();
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
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
    
    private void createOutputDirectories() {
        new File(config.getDaoInterfaceOutputDir()).mkdirs();
        new File(config.getDaoImplOutputDir()).mkdirs();
        new File(config.getRowMapperOutputDir()).mkdirs();
        new File(config.getSqlConstantsOutputDir()).mkdirs();
    }
    
    private void writeToFile(String fileName, String content) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        }
        System.out.println("Generated: " + fileName);
    }
    
    // Helper classes
    
    static class TableInfo {
        private String name;
        private List<ColumnInfo> columns = new ArrayList<>();
        private Set<String> primaryKeys = new HashSet<>();
        private Map<String, String> foreignKeys = new HashMap<>();
        
        public TableInfo(String name) {
            this.name = name;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public List<ColumnInfo> getColumns() { return columns; }
        public void setColumns(List<ColumnInfo> columns) { this.columns = columns; }
        
        public Set<String> getPrimaryKeys() { return primaryKeys; }
        public void setPrimaryKeys(Set<String> primaryKeys) { this.primaryKeys = primaryKeys; }
        
        public Map<String, String> getForeignKeys() { return foreignKeys; }
        public void setForeignKeys(Map<String, String> foreignKeys) { this.foreignKeys = foreignKeys; }
    }
    
    static class ColumnInfo {
        String name;
        String javaType;
        String sqlType;
        int size;
        boolean nullable;
        boolean autoIncrement;
        String defaultValue;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getJavaType() { return javaType; }
        public void setJavaType(String javaType) { this.javaType = javaType; }
        
        public String getSqlType() { return sqlType; }
        public void setSqlType(String sqlType) { this.sqlType = sqlType; }
        
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        
        public boolean isNullable() { return nullable; }
        public void setNullable(boolean nullable) { this.nullable = nullable; }
        
        public boolean isAutoIncrement() { return autoIncrement; }
        public void setAutoIncrement(boolean autoIncrement) { this.autoIncrement = autoIncrement; }
        
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    }
    
    static class PojoInfo {
        private String className;
        private String tableName;
        private Map<String, FieldMapping> fieldMappings = new HashMap<>();
        
        // Getters and setters
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        
        public Map<String, FieldMapping> getFieldMappings() { return fieldMappings; }
        public void setFieldMappings(Map<String, FieldMapping> fieldMappings) { this.fieldMappings = fieldMappings; }
    }
    
    static class FieldMapping {
        String fieldName;
        String columnName;
        String javaType;
        
        // Getters and setters
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        
        public String getColumnName() { return columnName; }
        public void setColumnName(String columnName) { this.columnName = columnName; }
        
        public String getJavaType() { return javaType; }
        public void setJavaType(String javaType) { this.javaType = javaType; }
    }
    
    static class DaoGenerationUnit {
        private TableInfo table;
        private PojoInfo pojo;
        
        public DaoGenerationUnit(TableInfo table, PojoInfo pojo) {
            this.table = table;
            this.pojo = pojo;
        }
        
        // Getters and setters
        public TableInfo getTable() { return table; }
        public void setTable(TableInfo table) { this.table = table; }
        
        public PojoInfo getPojo() { return pojo; }
        public void setPojo(PojoInfo pojo) { this.pojo = pojo; }
    }
    
    static class JdbcDaoGeneratorConfig {
        // Database configuration
        private String dbUrl = DEFAULT_DB_URL;
        private String dbUser = DEFAULT_DB_USER;
        private String dbPassword = DEFAULT_DB_PASS;
        private String schema = "public";
        
        // Package configuration
        private String pojoPackage = DEFAULT_POJO_PACKAGE;
        private String daoInterfacePackage = DEFAULT_DAO_PACKAGE + ".interfaces";
        private String daoImplPackage = DEFAULT_DAO_PACKAGE + ".impl";
        private String rowMapperPackage = DEFAULT_DAO_PACKAGE + ".mapper";
        private String sqlConstantsPackage = DEFAULT_DAO_PACKAGE + ".sql";
        
        // Output directories
        private String outputBaseDir = "src/main/java";
        
        // Getters and setters
        public String getDbUrl() { return dbUrl; }
        public void setDbUrl(String dbUrl) { this.dbUrl = dbUrl; }
        
        public String getDbUser() { return dbUser; }
        public void setDbUser(String dbUser) { this.dbUser = dbUser; }
        
        public String getDbPassword() { return dbPassword; }
        public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }
        
        public String getSchema() { return schema; }
        public void setSchema(String schema) { this.schema = schema; }
        
        public String getPojoPackage() { return pojoPackage; }
        public void setPojoPackage(String pojoPackage) { this.pojoPackage = pojoPackage; }
        
        public String getDaoInterfacePackage() { return daoInterfacePackage; }
        public void setDaoInterfacePackage(String daoInterfacePackage) { this.daoInterfacePackage = daoInterfacePackage; }
        
        public String getDaoImplPackage() { return daoImplPackage; }
        public void setDaoImplPackage(String daoImplPackage) { this.daoImplPackage = daoImplPackage; }
        
        public String getRowMapperPackage() { return rowMapperPackage; }
        public void setRowMapperPackage(String rowMapperPackage) { this.rowMapperPackage = rowMapperPackage; }
        
        public String getSqlConstantsPackage() { return sqlConstantsPackage; }
        public void setSqlConstantsPackage(String sqlConstantsPackage) { this.sqlConstantsPackage = sqlConstantsPackage; }
        
        public String getDaoInterfaceOutputDir() {
            return outputBaseDir + "/" + daoInterfacePackage.replace('.', '/');
        }
        
        public String getDaoImplOutputDir() {
            return outputBaseDir + "/" + daoImplPackage.replace('.', '/');
        }
        
        public String getRowMapperOutputDir() {
            return outputBaseDir + "/" + rowMapperPackage.replace('.', '/');
        }
        
        public String getSqlConstantsOutputDir() {
            return outputBaseDir + "/" + sqlConstantsPackage.replace('.', '/');
        }
    }
}
