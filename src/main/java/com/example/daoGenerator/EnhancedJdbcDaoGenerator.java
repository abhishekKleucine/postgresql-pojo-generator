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
 * Enhanced JDBC DAO Generator
 * Handles all table types: single-ID, composite-key, no-primary-key, and custom-key tables
 * Generates appropriate DAO methods based on actual table structure
 */
public class EnhancedJdbcDaoGenerator {
    
    private final JdbcDaoGeneratorConfig config;
    
    // Default configuration
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/qa_";
    private static final String DEFAULT_DB_USER = "postgres";
    private static final String DEFAULT_DB_PASS = "postgres";
    private static final String DEFAULT_POJO_PACKAGE = "com.example.pojogenerator.pojos";
    private static final String DEFAULT_DAO_PACKAGE = "com.example.daoGenerator.dao";
    
    public EnhancedJdbcDaoGenerator() {
        this.config = new JdbcDaoGeneratorConfig();
    }
    
    public static void main(String[] args) {
        System.out.println("Starting Enhanced JDBC DAO Generator...");
        
        EnhancedJdbcDaoGenerator generator = new EnhancedJdbcDaoGenerator();
        generator.generateAllDaos();
        
        System.out.println("Enhanced DAO generation completed!");
    }
    
    public void generateAllDaos() {
        try {
            createOutputDirectories();
            
            List<TableInfo> tables = extractDatabaseSchema();
            List<PojoInfo> pojos = analyzeExistingPojos();
            List<DaoGenerationUnit> units = matchTablesWithPojos(tables, pojos);
            
            int successCount = 0;
            for (DaoGenerationUnit unit : units) {
                try {
                    generateEnhancedDaoForTable(unit);
                    successCount++;
                } catch (Exception e) {
                    System.err.println("❌ Failed to generate DAO for: " + unit.getPojo().getClassName());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Successfully generated " + successCount + " enhanced DAO classes");
            
        } catch (Exception e) {
            System.err.println("Error generating DAOs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Enhanced DAO generation with table type detection
     */
    private void generateEnhancedDaoForTable(DaoGenerationUnit unit) throws IOException {
        TableInfo table = unit.getTable();
        PojoInfo pojo = unit.getPojo();
        
        // Detect table key type
        TableKeyInfo keyInfo = analyzeTableKeyStructure(table, pojo);
        
        System.out.println("Generating enhanced DAO for: " + pojo.getClassName() + 
                         " (Key type: " + keyInfo.keyType + ")");
        
        generateEnhancedDaoInterface(table, pojo, keyInfo);
        generateEnhancedJdbcImplementation(table, pojo, keyInfo);
        generateEnhancedRowMapper(table, pojo);
        generateEnhancedSqlConstants(table, pojo, keyInfo);
        
        System.out.println("✅ Generated enhanced DAO for: " + pojo.getClassName());
    }
    
    /**
     * Analyze table key structure to determine generation strategy
     */
    private TableKeyInfo analyzeTableKeyStructure(TableInfo table, PojoInfo pojo) {
        Set<String> primaryKeys = table.getPrimaryKeys();
        boolean hasIdField = pojo.getFieldMappings().containsKey("id");
        
        if (primaryKeys.isEmpty()) {
            // No primary key - use foreign keys as composite key
            List<String> compositeKeyColumns = extractCompositeKeyFromForeignKeys(table);
            return new TableKeyInfo(TableKeyType.NO_PRIMARY_KEY, compositeKeyColumns);
            
        } else if (primaryKeys.size() == 1) {
            String singleKey = primaryKeys.iterator().next();
            if ("id".equals(singleKey) && hasIdField) {
                // Check if the ID field is actually Long type for SINGLE_ID
                String idType = getFieldType(pojo, "id");
                if ("Long".equals(idType)) {
                    return new TableKeyInfo(TableKeyType.SINGLE_ID, Arrays.asList("id"));
                } else {
                    return new TableKeyInfo(TableKeyType.CUSTOM_SINGLE_KEY, Arrays.asList(singleKey));
                }
            } else {
                return new TableKeyInfo(TableKeyType.CUSTOM_SINGLE_KEY, Arrays.asList(singleKey));
            }
            
        } else {
            // Multiple column primary key
            List<String> keyColumns = new ArrayList<>(primaryKeys);
            return new TableKeyInfo(TableKeyType.COMPOSITE_KEY, keyColumns);
        }
    }
    
    /**
     * Extract composite key from foreign key relationships for tables without primary key
     */
    private List<String> extractCompositeKeyFromForeignKeys(TableInfo table) {
        List<String> keyColumns = new ArrayList<>();
        
        // Add foreign key columns that end with "_id"
        for (String fkColumn : table.getForeignKeys().keySet()) {
            if (fkColumn.endsWith("_id")) {
                keyColumns.add(fkColumn);
            }
        }
        
        // If no suitable foreign keys found, use all foreign keys
        if (keyColumns.isEmpty()) {
            keyColumns.addAll(table.getForeignKeys().keySet());
        }
        
        return keyColumns;
    }
    
    /**
     * Generate enhanced DAO interface based on table key type
     */
    private void generateEnhancedDaoInterface(TableInfo table, PojoInfo pojo, TableKeyInfo keyInfo) throws IOException {
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
        sb.append(" * Enhanced DAO interface for ").append(pojoClassName).append(" entity\n");
        sb.append(" * Table: ").append(table.getName()).append("\n");
        sb.append(" * Key Type: ").append(keyInfo.keyType).append("\n");
        sb.append(" * Generated by Enhanced JDBC DAO Generator\n");
        sb.append(" */\n");
        sb.append("public interface ").append(daoName).append(" {\n\n");
        
        // Generate methods based on key type
        generateKeySpecificMethods(sb, pojo, keyInfo);
        
        // Common methods for all table types
        sb.append("    // Common operations\n");
        sb.append("    List<").append(pojoClassName).append("> findAll();\n");
        sb.append("    ").append(pojoClassName).append(" save(").append(pojoClassName).append(" entity);\n");
        sb.append("    long count();\n\n");
        
        // Foreign key based finders
        generateForeignKeyFinders(sb, table, pojo);
        
        sb.append("}\n");
        
        String fileName = config.getDaoInterfaceOutputDir() + "/" + daoName + ".java";
        writeToFile(fileName, sb.toString());
    }
    
    /**
     * Generate key-specific methods based on table structure
     */
    private void generateKeySpecificMethods(StringBuilder sb, PojoInfo pojo, TableKeyInfo keyInfo) {
        String pojoClassName = pojo.getClassName();
        
        switch (keyInfo.keyType) {
            case SINGLE_ID:
                String idType = getFieldType(pojo, "id");
                sb.append("    // Single ID operations\n");
                sb.append("    Optional<").append(pojoClassName).append("> findById(").append(idType).append(" id);\n");
                sb.append("    void deleteById(").append(idType).append(" id);\n");
                sb.append("    boolean existsById(").append(idType).append(" id);\n\n");
                break;
                
            case CUSTOM_SINGLE_KEY:
                String keyColumn = keyInfo.keyColumns.get(0);
                String keyField = convertColumnNameToFieldName(keyColumn);
                String keyType = getFieldType(pojo, keyField);
                
                sb.append("    // Custom single key operations\n");
                sb.append("    Optional<").append(pojoClassName).append("> findBy").append(capitalize(keyField)).append("(").append(keyType).append(" ").append(keyField).append(");\n");
                sb.append("    void deleteBy").append(capitalize(keyField)).append("(").append(keyType).append(" ").append(keyField).append(");\n");
                sb.append("    boolean existsBy").append(capitalize(keyField)).append("(").append(keyType).append(" ").append(keyField).append(");\n\n");
                break;
                
            case COMPOSITE_KEY:
            case NO_PRIMARY_KEY:
                sb.append("    // Composite key operations\n");
                generateCompositeKeyMethods(sb, pojo, keyInfo);
                break;
        }
    }
    
    /**
     * Generate composite key methods
     */
    private void generateCompositeKeyMethods(StringBuilder sb, PojoInfo pojo, TableKeyInfo keyInfo) {
        String pojoClassName = pojo.getClassName();
        List<String> keyColumns = keyInfo.keyColumns;
        
        if (keyColumns.isEmpty()) {
            return;
        }
        
        // Build method name and parameters
        StringBuilder methodSuffix = new StringBuilder();
        StringBuilder parameters = new StringBuilder();
        
        for (int i = 0; i < keyColumns.size(); i++) {
            String column = keyColumns.get(i);
            String field = convertColumnNameToFieldName(column);
            String type = getFieldType(pojo, field);
            
            if (i > 0) {
                methodSuffix.append("And");
                parameters.append(", ");
            }
            
            methodSuffix.append(capitalize(field));
            parameters.append(type).append(" ").append(field);
        }
        
        String methodName = methodSuffix.toString();
        String paramList = parameters.toString();
        
        // Generate composite key methods
        sb.append("    Optional<").append(pojoClassName).append("> findBy").append(methodName).append("(").append(paramList).append(");\n");
        sb.append("    void deleteBy").append(methodName).append("(").append(paramList).append(");\n");
        sb.append("    boolean existsBy").append(methodName).append("(").append(paramList).append(");\n\n");
    }
    
    /**
     * Generate enhanced JDBC implementation
     */
    private void generateEnhancedJdbcImplementation(TableInfo table, PojoInfo pojo, TableKeyInfo keyInfo) throws IOException {
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
        sb.append(" * Enhanced JDBC implementation of ").append(daoName).append("\n");
        sb.append(" * Key Type: ").append(keyInfo.keyType).append("\n");
        sb.append(" * Generated by Enhanced JDBC DAO Generator\n");
        sb.append(" */\n");
        sb.append("@Repository\n");
        sb.append("public class ").append(implName).append(" implements ").append(daoName).append(" {\n\n");
        
        // Fields and constructor
        sb.append("    private final NamedParameterJdbcTemplate jdbcTemplate;\n");
        sb.append("    private final ").append(rowMapperName).append(" rowMapper;\n\n");
        
        sb.append("    public ").append(implName).append("(NamedParameterJdbcTemplate jdbcTemplate) {\n");
        sb.append("        this.jdbcTemplate = jdbcTemplate;\n");
        sb.append("        this.rowMapper = new ").append(rowMapperName).append("();\n");
        sb.append("    }\n\n");
        
        // Generate implementations based on key type
        generateKeySpecificImplementations(sb, table, pojo, keyInfo, sqlConstantsName);
        
        // Common implementations
        generateCommonImplementations(sb, table, pojo, keyInfo, sqlConstantsName);
        
        sb.append("}\n");
        
        String fileName = config.getDaoImplOutputDir() + "/" + implName + ".java";
        writeToFile(fileName, sb.toString());
    }
    
    /**
     * Generate key-specific method implementations
     */
    private void generateKeySpecificImplementations(StringBuilder sb, TableInfo table, PojoInfo pojo, 
                                                   TableKeyInfo keyInfo, String sqlConstantsName) {
        String pojoClassName = pojo.getClassName();
        
        switch (keyInfo.keyType) {
            case SINGLE_ID:
                generateSingleIdImplementations(sb, pojo, sqlConstantsName);
                break;
                
            case CUSTOM_SINGLE_KEY:
                generateCustomSingleKeyImplementations(sb, pojo, keyInfo, sqlConstantsName);
                break;
                
            case COMPOSITE_KEY:
            case NO_PRIMARY_KEY:
                generateCompositeKeyImplementations(sb, pojo, keyInfo, sqlConstantsName);
                break;
        }
    }
    
    /**
     * Generate single ID implementations with proper type
     */
    private void generateSingleIdImplementations(StringBuilder sb, PojoInfo pojo, String sqlConstantsName) {
        String pojoClassName = pojo.getClassName();
        String idType = getFieldType(pojo, "id");
        
        // findById
        sb.append("    @Override\n");
        sb.append("    public Optional<").append(pojoClassName).append("> findById(").append(idType).append(" id) {\n");
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
        
        // deleteById
        sb.append("    @Override\n");
        sb.append("    public void deleteById(").append(idType).append(" id) {\n");
        sb.append("        jdbcTemplate.update(").append(sqlConstantsName).append(".DELETE_BY_ID, Map.of(\"id\", id));\n");
        sb.append("    }\n\n");
        
        // existsById
        sb.append("    @Override\n");
        sb.append("    public boolean existsById(").append(idType).append(" id) {\n");
        sb.append("        Integer count = jdbcTemplate.queryForObject(\n");
        sb.append("            ").append(sqlConstantsName).append(".EXISTS_BY_ID,\n");
        sb.append("            Map.of(\"id\", id),\n");
        sb.append("            Integer.class\n");
        sb.append("        );\n");
        sb.append("        return count != null && count > 0;\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate custom single key implementations
     */
    private void generateCustomSingleKeyImplementations(StringBuilder sb, PojoInfo pojo, 
                                                       TableKeyInfo keyInfo, String sqlConstantsName) {
        String pojoClassName = pojo.getClassName();
        String keyColumn = keyInfo.keyColumns.get(0);
        String keyField = convertColumnNameToFieldName(keyColumn);
        String keyType = getFieldType(pojo, keyField);
        String methodSuffix = capitalize(keyField);
        
        // findByKey
        sb.append("    @Override\n");
        sb.append("    public Optional<").append(pojoClassName).append("> findBy").append(methodSuffix).append("(").append(keyType).append(" ").append(keyField).append(") {\n");
        sb.append("        try {\n");
        sb.append("            ").append(pojoClassName).append(" result = jdbcTemplate.queryForObject(\n");
        sb.append("                ").append(sqlConstantsName).append(".FIND_BY_").append(keyColumn.toUpperCase()).append(",\n");
        sb.append("                Map.of(\"").append(keyField).append("\", ").append(keyField).append("),\n");
        sb.append("                rowMapper\n");
        sb.append("            );\n");
        sb.append("            return Optional.ofNullable(result);\n");
        sb.append("        } catch (EmptyResultDataAccessException e) {\n");
        sb.append("            return Optional.empty();\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        
        // deleteByKey
        sb.append("    @Override\n");
        sb.append("    public void deleteBy").append(methodSuffix).append("(").append(keyType).append(" ").append(keyField).append(") {\n");
        sb.append("        jdbcTemplate.update(").append(sqlConstantsName).append(".DELETE_BY_").append(keyColumn.toUpperCase()).append(", Map.of(\"").append(keyField).append("\", ").append(keyField).append("));\n");
        sb.append("    }\n\n");
        
        // existsByKey
        sb.append("    @Override\n");
        sb.append("    public boolean existsBy").append(methodSuffix).append("(").append(keyType).append(" ").append(keyField).append(") {\n");
        sb.append("        Integer count = jdbcTemplate.queryForObject(\n");
        sb.append("            ").append(sqlConstantsName).append(".EXISTS_BY_").append(keyColumn.toUpperCase()).append(",\n");
        sb.append("            Map.of(\"").append(keyField).append("\", ").append(keyField).append("),\n");
        sb.append("            Integer.class\n");
        sb.append("        );\n");
        sb.append("        return count != null && count > 0;\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate composite key implementations
     */
    private void generateCompositeKeyImplementations(StringBuilder sb, PojoInfo pojo, 
                                                   TableKeyInfo keyInfo, String sqlConstantsName) {
        String pojoClassName = pojo.getClassName();
        List<String> keyColumns = keyInfo.keyColumns;
        
        if (keyColumns.isEmpty()) {
            return;
        }
        
        // Build method components
        StringBuilder methodSuffix = new StringBuilder();
        StringBuilder parameters = new StringBuilder();
        StringBuilder paramMap = new StringBuilder();
        
        for (int i = 0; i < keyColumns.size(); i++) {
            String column = keyColumns.get(i);
            String field = convertColumnNameToFieldName(column);
            String type = getFieldType(pojo, field);
            
            if (i > 0) {
                methodSuffix.append("And");
                parameters.append(", ");
                paramMap.append(", ");
            }
            
            methodSuffix.append(capitalize(field));
            parameters.append(type).append(" ").append(field);
            paramMap.append("\"").append(field).append("\", ").append(field);
        }
        
        String methodName = methodSuffix.toString();
        String paramList = parameters.toString();
        String mapParams = paramMap.toString();
        String sqlSuffix = String.join("_AND_", keyColumns).toUpperCase();
        
        // findByCompositeKey
        sb.append("    @Override\n");
        sb.append("    public Optional<").append(pojoClassName).append("> findBy").append(methodName).append("(").append(paramList).append(") {\n");
        sb.append("        try {\n");
        sb.append("            ").append(pojoClassName).append(" result = jdbcTemplate.queryForObject(\n");
        sb.append("                ").append(sqlConstantsName).append(".FIND_BY_").append(sqlSuffix).append(",\n");
        sb.append("                Map.of(").append(mapParams).append("),\n");
        sb.append("                rowMapper\n");
        sb.append("            );\n");
        sb.append("            return Optional.ofNullable(result);\n");
        sb.append("        } catch (EmptyResultDataAccessException e) {\n");
        sb.append("            return Optional.empty();\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        
        // deleteByCompositeKey
        sb.append("    @Override\n");
        sb.append("    public void deleteBy").append(methodName).append("(").append(paramList).append(") {\n");
        sb.append("        jdbcTemplate.update(").append(sqlConstantsName).append(".DELETE_BY_").append(sqlSuffix).append(", Map.of(").append(mapParams).append("));\n");
        sb.append("    }\n\n");
        
        // existsByCompositeKey
        sb.append("    @Override\n");
        sb.append("    public boolean existsBy").append(methodName).append("(").append(paramList).append(") {\n");
        sb.append("        Integer count = jdbcTemplate.queryForObject(\n");
        sb.append("            ").append(sqlConstantsName).append(".EXISTS_BY_").append(sqlSuffix).append(",\n");
        sb.append("            Map.of(").append(mapParams).append("),\n");
        sb.append("            Integer.class\n");
        sb.append("        );\n");
        sb.append("        return count != null && count > 0;\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate common implementations (save, findAll, count, etc.)
     */
    private void generateCommonImplementations(StringBuilder sb, TableInfo table, PojoInfo pojo, 
                                             TableKeyInfo keyInfo, String sqlConstantsName) {
        String pojoClassName = pojo.getClassName();
        
        // findAll
        sb.append("    @Override\n");
        sb.append("    public List<").append(pojoClassName).append("> findAll() {\n");
        sb.append("        return jdbcTemplate.query(").append(sqlConstantsName).append(".FIND_ALL, rowMapper);\n");
        sb.append("    }\n\n");
        
        // save - smart implementation based on key type
        generateSmartSaveImplementation(sb, pojo, keyInfo, sqlConstantsName);
        
        // count
        sb.append("    @Override\n");
        sb.append("    public long count() {\n");
        sb.append("        Long result = jdbcTemplate.queryForObject(").append(sqlConstantsName).append(".COUNT, Map.of(), Long.class);\n");
        sb.append("        return result != null ? result : 0L;\n");
        sb.append("    }\n\n");
        
        // Foreign key finders
        generateForeignKeyImplementations(sb, table, pojo, sqlConstantsName);
        
        // Parameter mapping helper
        generateParameterMappingMethod(sb, pojo);
    }
    
    /**
     * Generate smart save implementation based on key type
     */
    private void generateSmartSaveImplementation(StringBuilder sb, PojoInfo pojo, 
                                               TableKeyInfo keyInfo, String sqlConstantsName) {
        String pojoClassName = pojo.getClassName();
        
        sb.append("    @Override\n");
        sb.append("    public ").append(pojoClassName).append(" save(").append(pojoClassName).append(" entity) {\n");
        
        switch (keyInfo.keyType) {
            case SINGLE_ID:
                sb.append("        if (entity.getId() == null) {\n");
                sb.append("            return insert(entity);\n");
                sb.append("        } else {\n");
                sb.append("            return update(entity);\n");
                sb.append("        }\n");
                break;
                
            case CUSTOM_SINGLE_KEY:
                String keyField = convertColumnNameToFieldName(keyInfo.keyColumns.get(0));
                String getterName = "get" + capitalize(keyField);
                sb.append("        if (entity.").append(getterName).append("() == null) {\n");
                sb.append("            return insert(entity);\n");
                sb.append("        } else {\n");
                sb.append("            return update(entity);\n");
                sb.append("        }\n");
                break;
                
            case COMPOSITE_KEY:
            case NO_PRIMARY_KEY:
                // For composite keys, check existence before deciding insert/update
                generateCompositeKeyExistenceCheck(sb, pojo, keyInfo);
                break;
        }
        
        sb.append("    }\n\n");
        
        // Generate insert and update helper methods
        generateInsertUpdateMethods(sb, pojo, keyInfo, sqlConstantsName);
    }
    
    /**
     * Generate composite key existence check for save method
     */
    private void generateCompositeKeyExistenceCheck(StringBuilder sb, PojoInfo pojo, TableKeyInfo keyInfo) {
        List<String> keyColumns = keyInfo.keyColumns;
        
        // For NO_PRIMARY_KEY tables, always insert (no update support)
        if (keyInfo.keyType == TableKeyType.NO_PRIMARY_KEY) {
            sb.append("        // No primary key - always insert\n");
            sb.append("        return insert(entity);\n");
            return;
        }
        
        if (keyColumns.isEmpty()) {
            sb.append("        // No key defined - always insert\n");
            sb.append("        return insert(entity);\n");
            return;
        }
        
        // Build existence check for tables with keys
        StringBuilder methodCall = new StringBuilder("existsBy");
        StringBuilder params = new StringBuilder();
        
        for (int i = 0; i < keyColumns.size(); i++) {
            String column = keyColumns.get(i);
            String field = convertColumnNameToFieldName(column);
            String getterName = "get" + capitalize(field);
            
            if (i > 0) {
                methodCall.append("And");
                params.append(", ");
            }
            
            methodCall.append(capitalize(field));
            params.append("entity.").append(getterName).append("()");
        }
        
        sb.append("        if (").append(methodCall).append("(").append(params).append(")) {\n");
        sb.append("            return update(entity);\n");
        sb.append("        } else {\n");
        sb.append("            return insert(entity);\n");
        sb.append("        }\n");
    }
    
    /**
     * Generate insert and update helper methods
     */
    private void generateInsertUpdateMethods(StringBuilder sb, PojoInfo pojo, 
                                           TableKeyInfo keyInfo, String sqlConstantsName) {
        String pojoClassName = pojo.getClassName();
        
        // Insert method
        sb.append("    private ").append(pojoClassName).append(" insert(").append(pojoClassName).append(" entity) {\n");
        sb.append("        // Set audit fields if they exist\n");
        sb.append("        long now = System.currentTimeMillis();\n");
        
        if (pojo.getFieldMappings().containsKey("createdAt")) {
            sb.append("        if (entity.getCreatedAt() == null) {\n");
            sb.append("            entity.setCreatedAt(now);\n");
            sb.append("        }\n");
        }
        
        if (pojo.getFieldMappings().containsKey("modifiedAt")) {
            sb.append("        entity.setModifiedAt(now);\n");
        }
        
        sb.append("\n        MapSqlParameterSource params = createParameterMap(entity);\n");
        
        if (keyInfo.keyType == TableKeyType.SINGLE_ID) {
            sb.append("        Long generatedId = jdbcTemplate.queryForObject(\n");
            sb.append("            ").append(sqlConstantsName).append(".INSERT,\n");
            sb.append("            params,\n");
            sb.append("            Long.class\n");
            sb.append("        );\n");
            sb.append("        entity.setId(generatedId);\n");
        } else {
            sb.append("        jdbcTemplate.update(").append(sqlConstantsName).append(".INSERT, params);\n");
        }
        
        sb.append("        return entity;\n");
        sb.append("    }\n\n");
        
        // Update method - only generate for tables with primary keys
        if (keyInfo.keyType != TableKeyType.NO_PRIMARY_KEY) {
            sb.append("    private ").append(pojoClassName).append(" update(").append(pojoClassName).append(" entity) {\n");
            
            if (pojo.getFieldMappings().containsKey("modifiedAt")) {
                sb.append("        entity.setModifiedAt(System.currentTimeMillis());\n\n");
            }
            
            sb.append("        MapSqlParameterSource params = createParameterMap(entity);\n");
            sb.append("        int rowsAffected = jdbcTemplate.update(").append(sqlConstantsName).append(".UPDATE, params);\n");
            sb.append("        \n");
            sb.append("        if (rowsAffected == 0) {\n");
            sb.append("            throw new RuntimeException(\"Entity not found for update\");\n");
            sb.append("        }\n");
            sb.append("        return entity;\n");
            sb.append("    }\n\n");
        }
    }
    
    /**
     * Generate enhanced row mapper (reuse existing logic)
     */
    private void generateEnhancedRowMapper(TableInfo table, PojoInfo pojo) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        String rowMapperName = pojo.getClassName() + "RowMapper";
        String pojoClassName = pojo.getClassName();
        
        // Package and imports
        sb.append("package ").append(config.getRowMapperPackage()).append(";\n\n");
        sb.append("import java.sql.ResultSet;\n");
        sb.append("import java.sql.SQLException;\n");
        sb.append("import java.sql.Timestamp;\n");
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
        sb.append(" * Enhanced row mapper for ").append(pojoClassName).append(" entity\n");
        sb.append(" * Generated by Enhanced JDBC DAO Generator\n");
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
        
        String fileName = config.getRowMapperOutputDir() + "/" + rowMapperName + ".java";
        writeToFile(fileName, sb.toString());
    }
    
    /**
     * Generate enhanced SQL constants based on key type
     */
    private void generateEnhancedSqlConstants(TableInfo table, PojoInfo pojo, TableKeyInfo keyInfo) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        String sqlConstantsName = pojo.getClassName() + "Sql";
        String tableName = table.getName();
        
        // Package
        sb.append("package ").append(config.getSqlConstantsPackage()).append(";\n\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * Enhanced SQL constants for ").append(pojo.getClassName()).append(" entity\n");
        sb.append(" * Key Type: ").append(keyInfo.keyType).append("\n");
        sb.append(" * Generated by Enhanced JDBC DAO Generator\n");
        sb.append(" */\n");
        sb.append("public final class ").append(sqlConstantsName).append(" {\n\n");
        
        // Generate SQL queries based on key type
        generateEnhancedSqlQueries(sb, table, pojo, keyInfo);
        
        sb.append("    private ").append(sqlConstantsName).append("() {\n");
        sb.append("        // Utility class\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        String fileName = config.getSqlConstantsOutputDir() + "/" + sqlConstantsName + ".java";
        writeToFile(fileName, sb.toString());
    }
    
    /**
     * Generate enhanced SQL queries based on table key structure
     */
    private void generateEnhancedSqlQueries(StringBuilder sb, TableInfo table, PojoInfo pojo, TableKeyInfo keyInfo) {
        String tableName = table.getName();
        
        // Build column lists
        List<String> columnNames = new ArrayList<>();
        List<String> insertColumns = new ArrayList<>();
        List<String> insertValues = new ArrayList<>();
        List<String> updateSets = new ArrayList<>();
        
        for (FieldMapping field : pojo.getFieldMappings().values()) {
            columnNames.add(field.columnName);
            
            // Skip ID for INSERT in single-ID tables
            if (!(keyInfo.keyType == TableKeyType.SINGLE_ID && field.fieldName.equals("id"))) {
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
        
        // FIND_ALL (common for all table types)
        sb.append("    public static final String FIND_ALL = \"\"\"\n");
        sb.append("        SELECT ").append(columnList).append("\n");
        sb.append("        FROM ").append(tableName).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // COUNT (common for all table types)
        sb.append("    public static final String COUNT = \"\"\"\n");
        sb.append("        SELECT COUNT(*) FROM ").append(tableName).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // Generate key-specific queries
        generateKeySpecificSqlQueries(sb, table, pojo, keyInfo, columnList, insertColumnList, insertValueList, updateSetList);
        
        // Foreign key finders
        generateForeignKeySqlQueries(sb, table, columnList);
    }
    
    /**
     * Generate key-specific SQL queries
     */
    private void generateKeySpecificSqlQueries(StringBuilder sb, TableInfo table, PojoInfo pojo, TableKeyInfo keyInfo,
                                             String columnList, String insertColumnList, String insertValueList, String updateSetList) {
        String tableName = table.getName();
        
        switch (keyInfo.keyType) {
            case SINGLE_ID:
                generateSingleIdSqlQueries(sb, tableName, columnList, insertColumnList, insertValueList, updateSetList);
                break;
                
            case CUSTOM_SINGLE_KEY:
                generateCustomSingleKeySqlQueries(sb, tableName, keyInfo, columnList, insertColumnList, insertValueList, updateSetList);
                break;
                
            case COMPOSITE_KEY:
            case NO_PRIMARY_KEY:
                generateCompositeKeySqlQueries(sb, tableName, keyInfo, columnList, insertColumnList, insertValueList, updateSetList);
                break;
        }
    }
    
    /**
     * Generate single ID SQL queries
     */
    private void generateSingleIdSqlQueries(StringBuilder sb, String tableName, String columnList,
                                          String insertColumnList, String insertValueList, String updateSetList) {
        // FIND_BY_ID
        sb.append("    public static final String FIND_BY_ID = \"\"\"\n");
        sb.append("        SELECT ").append(columnList).append("\n");
        sb.append("        FROM ").append(tableName).append("\n");
        sb.append("        WHERE id = :id\n");
        sb.append("        \"\"\";\n\n");
        
        // INSERT with RETURNING id
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
    }
    
    /**
     * Generate custom single key SQL queries
     */
    private void generateCustomSingleKeySqlQueries(StringBuilder sb, String tableName, TableKeyInfo keyInfo,
                                                 String columnList, String insertColumnList, String insertValueList, String updateSetList) {
        String keyColumn = keyInfo.keyColumns.get(0);
        String keyField = convertColumnNameToFieldName(keyColumn);
        
        // FIND_BY_KEY
        sb.append("    public static final String FIND_BY_").append(keyColumn.toUpperCase()).append(" = \"\"\"\n");
        sb.append("        SELECT ").append(columnList).append("\n");
        sb.append("        FROM ").append(tableName).append("\n");
        sb.append("        WHERE ").append(keyColumn).append(" = :").append(keyField).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // INSERT
        sb.append("    public static final String INSERT = \"\"\"\n");
        sb.append("        INSERT INTO ").append(tableName).append(" (").append(insertColumnList).append(")\n");
        sb.append("        VALUES (").append(insertValueList).append(")\n");
        sb.append("        \"\"\";\n\n");
        
        // UPDATE
        sb.append("    public static final String UPDATE = \"\"\"\n");
        sb.append("        UPDATE ").append(tableName).append(" SET\n");
        sb.append("            ").append(updateSetList).append("\n");
        sb.append("        WHERE ").append(keyColumn).append(" = :").append(keyField).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // DELETE_BY_KEY
        sb.append("    public static final String DELETE_BY_").append(keyColumn.toUpperCase()).append(" = \"\"\"\n");
        sb.append("        DELETE FROM ").append(tableName).append("\n");
        sb.append("        WHERE ").append(keyColumn).append(" = :").append(keyField).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // EXISTS_BY_KEY
        sb.append("    public static final String EXISTS_BY_").append(keyColumn.toUpperCase()).append(" = \"\"\"\n");
        sb.append("        SELECT COUNT(*) FROM ").append(tableName).append("\n");
        sb.append("        WHERE ").append(keyColumn).append(" = :").append(keyField).append("\n");
        sb.append("        \"\"\";\n\n");
    }
    
    /**
     * Generate composite key SQL queries
     */
    private void generateCompositeKeySqlQueries(StringBuilder sb, String tableName, TableKeyInfo keyInfo,
                                              String columnList, String insertColumnList, String insertValueList, String updateSetList) {
        List<String> keyColumns = keyInfo.keyColumns;
        
        if (keyColumns.isEmpty()) {
            // No key - just INSERT
            sb.append("    public static final String INSERT = \"\"\"\n");
            sb.append("        INSERT INTO ").append(tableName).append(" (").append(insertColumnList).append(")\n");
            sb.append("        VALUES (").append(insertValueList).append(")\n");
            sb.append("        \"\"\";\n\n");
            return;
        }
        
        // Build WHERE clause for composite key
        List<String> whereConditions = new ArrayList<>();
        for (String column : keyColumns) {
            String field = convertColumnNameToFieldName(column);
            whereConditions.add(column + " = :" + field);
        }
        String whereClause = String.join(" AND ", whereConditions);
        String sqlSuffix = String.join("_AND_", keyColumns).toUpperCase();
        
        // FIND_BY_COMPOSITE_KEY
        sb.append("    public static final String FIND_BY_").append(sqlSuffix).append(" = \"\"\"\n");
        sb.append("        SELECT ").append(columnList).append("\n");
        sb.append("        FROM ").append(tableName).append("\n");
        sb.append("        WHERE ").append(whereClause).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // INSERT
        sb.append("    public static final String INSERT = \"\"\"\n");
        sb.append("        INSERT INTO ").append(tableName).append(" (").append(insertColumnList).append(")\n");
        sb.append("        VALUES (").append(insertValueList).append(")\n");
        sb.append("        \"\"\";\n\n");
        
        // UPDATE
        sb.append("    public static final String UPDATE = \"\"\"\n");
        sb.append("        UPDATE ").append(tableName).append(" SET\n");
        sb.append("            ").append(updateSetList).append("\n");
        sb.append("        WHERE ").append(whereClause).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // DELETE_BY_COMPOSITE_KEY
        sb.append("    public static final String DELETE_BY_").append(sqlSuffix).append(" = \"\"\"\n");
        sb.append("        DELETE FROM ").append(tableName).append("\n");
        sb.append("        WHERE ").append(whereClause).append("\n");
        sb.append("        \"\"\";\n\n");
        
        // EXISTS_BY_COMPOSITE_KEY
        sb.append("    public static final String EXISTS_BY_").append(sqlSuffix).append(" = \"\"\"\n");
        sb.append("        SELECT COUNT(*) FROM ").append(tableName).append("\n");
        sb.append("        WHERE ").append(whereClause).append("\n");
        sb.append("        \"\"\";\n\n");
    }
    
    /**
     * Generate foreign key SQL queries
     */
    private void generateForeignKeySqlQueries(StringBuilder sb, TableInfo table, String columnList) {
        for (Map.Entry<String, String> fk : table.getForeignKeys().entrySet()) {
            String columnName = fk.getKey();
            String fieldName = convertColumnNameToFieldName(columnName);
            
            sb.append("    public static final String FIND_BY_").append(columnName.toUpperCase()).append(" = \"\"\"\n");
            sb.append("        SELECT ").append(columnList).append("\n");
            sb.append("        FROM ").append(table.getName()).append("\n");
            sb.append("        WHERE ").append(columnName).append(" = :").append(fieldName).append("\n");
            sb.append("        \"\"\";\n\n");
        }
    }
    
    // Helper methods and implementations from original generator
    
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
    
    private void generateForeignKeyImplementations(StringBuilder sb, TableInfo table, PojoInfo pojo, String sqlConstantsName) {
        for (Map.Entry<String, String> fk : table.getForeignKeys().entrySet()) {
            String columnName = fk.getKey();
            String fieldName = convertColumnNameToFieldName(columnName);
            String methodName = "findBy" + capitalize(fieldName);
            
            sb.append("    @Override\n");
            sb.append("    public List<").append(pojo.getClassName()).append("> ").append(methodName).append("(Long ").append(fieldName).append(") {\n");
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
        } else if (field.javaType.contains("LocalDate") || field.javaType.equals("java.time.LocalDate")) {
            sb.append("        java.sql.Date ").append(field.fieldName).append("Date = rs.getDate(\"").append(field.columnName).append("\");\n");
            sb.append("        if (").append(field.fieldName).append("Date != null) {\n");
            sb.append("            entity.").append(setterName).append("(").append(field.fieldName).append("Date.toLocalDate());\n");
            sb.append("        }\n");
        } else if (field.javaType.contains("BigDecimal") || field.javaType.equals("java.math.BigDecimal")) {
            sb.append("        entity.").append(setterName).append("(rs.getBigDecimal(\"").append(field.columnName).append("\"));\n");
        } else if (field.javaType.equals("Double")) {
            sb.append("        entity.").append(setterName).append("(rs.getDouble(\"").append(field.columnName).append("\"));\n");
        } else if (field.javaType.equals("Float")) {
            sb.append("        entity.").append(setterName).append("(rs.getFloat(\"").append(field.columnName).append("\"));\n");
        } else {
            sb.append("        entity.").append(setterName).append("(rs.getString(\"").append(field.columnName).append("\"));\n");
        }
    }
    
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
    
    // Helper methods from original generator
    
    private String getFieldType(PojoInfo pojo, String fieldName) {
        FieldMapping field = pojo.getFieldMappings().get(fieldName);
        return field != null ? field.javaType : "String";
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
    
    /**
     * Check if table should be skipped from DAO generation
     * Excludes system tables like Liquibase migration tables
     */
    private boolean shouldSkipTable(String tableName) {
        // System tables to skip
        Set<String> systemTables = Set.of(
            "databasechangelog",        // Liquibase migration history
            "databasechangeloglock"     // Liquibase lock table
            // Add more system tables here if needed
        );
        
        return systemTables.contains(tableName.toLowerCase());
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
    
    // Copy all helper classes from original generator
    
    private List<TableInfo> extractDatabaseSchema() {
        List<TableInfo> tables = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword())) {
            System.out.println("Connected to database successfully!");
            
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tableRs = meta.getTables(null, config.getSchema(), "%", new String[]{"TABLE"});
            
            while (tableRs.next()) {
                String tableName = tableRs.getString("TABLE_NAME");
                
                // Skip system tables
                if (shouldSkipTable(tableName)) {
                    System.out.println("Skipping system table: " + tableName);
                    continue;
                }
                
                System.out.println("Processing table: " + tableName);
                
                TableInfo table = new TableInfo(tableName);
                table.setColumns(extractColumnsWithConstraints(conn, meta, tableName));
                table.setPrimaryKeys(extractPrimaryKeys(meta, tableName));
                table.setForeignKeys(extractForeignKeys(meta, tableName));
                
                tables.add(table);
            }
            
            System.out.println("Extracted " + tables.size() + " tables from database");
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to extract database schema", e);
        }
        
        return tables;
    }
    
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
    
    private Set<String> extractPrimaryKeys(DatabaseMetaData meta, String tableName) throws SQLException {
        Set<String> keys = new HashSet<>();
        ResultSet rs = meta.getPrimaryKeys(null, null, tableName);
        while (rs.next()) {
            keys.add(rs.getString("COLUMN_NAME"));
        }
        return keys;
    }
    
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
    
    private List<PojoInfo> analyzeExistingPojos() {
        List<PojoInfo> pojos = new ArrayList<>();
        
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
                
                // Skip if corresponding table is a system table
                if (shouldSkipTable(pojo.getTableName())) {
                    System.out.println("Skipping POJO for system table: " + pojo.getTableName());
                    continue;
                }
                
                pojos.add(pojo);
                System.out.println("Analyzed POJO: " + pojo.getClassName());
            } catch (Exception e) {
                System.err.println("Failed to analyze POJO: " + pojoFile.getName() + " - " + e.getMessage());
            }
        }
        
        return pojos;
    }
    
    private PojoInfo analyzeSinglePojo(File pojoFile) throws IOException {
        PojoInfo pojo = new PojoInfo();
        
        String content = Files.readString(pojoFile.toPath());
        
        pojo.setClassName(extractClassName(content));
        pojo.setFieldMappings(extractFieldMappings(content));
        pojo.setTableName(convertClassNameToTableName(pojo.getClassName()));
        
        return pojo;
    }
    
    private String extractClassName(String content) {
        Pattern pattern = Pattern.compile("public class (\\w+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("Could not extract class name");
    }
    
    private Map<String, FieldMapping> extractFieldMappings(String content) {
        Map<String, FieldMapping> mappings = new HashMap<>();
        
        Pattern fieldPattern = Pattern.compile("private (\\w+(?:\\.\\w+)*(?:<[^>]+>)?) (\\w+);");
        Matcher fieldMatcher = fieldPattern.matcher(content);
        
        while (fieldMatcher.find()) {
            String javaType = fieldMatcher.group(1);
            String fieldName = fieldMatcher.group(2);
            
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
     * Convert class name to table name using the same logic as PojoGenerator
     * Handles pluralization and mapping table naming conventions
     */
    private String convertClassNameToTableName(String className) {
        // Convert PascalCase to snake_case
        String tableName = className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        
        // Handle mapping tables (keep as-is)
        if (tableName.endsWith("_mapping")) {
            return tableName;
        }
        
        // Handle regular entity tables (add trailing 's' if not present)
        if (!tableName.endsWith("s") && !tableName.endsWith("ss")) {
            tableName = tableName + "s";
        }
        
        return tableName;
    }
    
    private String convertFieldNameToColumnName(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
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
    
    // Helper classes and enums
    
    enum TableKeyType {
        SINGLE_ID,           // Standard id column
        COMPOSITE_KEY,       // Multiple column primary key
        NO_PRIMARY_KEY,      // No primary key defined
        CUSTOM_SINGLE_KEY    // Single non-id primary key
    }
    
    static class TableKeyInfo {
        final TableKeyType keyType;
        final List<String> keyColumns;
        
        public TableKeyInfo(TableKeyType keyType, List<String> keyColumns) {
            this.keyType = keyType;
            this.keyColumns = keyColumns;
        }
    }
    
    static class TableInfo {
        private String name;
        private List<ColumnInfo> columns = new ArrayList<>();
        private Set<String> primaryKeys = new HashSet<>();
        private Map<String, String> foreignKeys = new HashMap<>();
        
        public TableInfo(String name) {
            this.name = name;
        }
        
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
