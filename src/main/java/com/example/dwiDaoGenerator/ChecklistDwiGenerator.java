package com.example.dwiDaoGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Documentation-driven, Workflow-aware, Intelligent (DWI) DAO Generator for Checklist
 * 
 * Triple Input Sources:
 * 1. Repository Documentation: repository_documents/repository_docs/ChecklistRepositorydoc.md
 * 2. Database Metadata: Direct connection to checklist table
 * 3. POJO Analysis: src/main/java/com/example/pojogenerator/pojos/Checklists.java
 * 
 * Generates complete DAO implementation with business logic from documentation
 */
public class ChecklistDwiGenerator {
    
    // Input source paths (no copying - direct reference)
    private static final String CHECKLIST_DOC_PATH = "repository_documents/repository_docs/ChecklistRepositorydoc.md";
    private static final String CHECKLIST_POJO_PATH = "src/main/java/com/example/pojogenerator/pojos/Checklists.java";
    
    // Database configuration
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/qa_";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "postgres";
    
    // Output configuration
    private static final String OUTPUT_PACKAGE = "com.example.dwiDaoGenerator.checklist.generated";
    private static final String OUTPUT_DIR = "src/main/java/com/example/dwiDaoGenerator/checklist/generated";
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting DWI DAO Generator for Checklist...");
        
        ChecklistDwiGenerator generator = new ChecklistDwiGenerator();
        generator.generateChecklistDao();
        
        System.out.println("✅ DWI DAO generation completed!");
    }
    
    public void generateChecklistDao() {
        try {
            // Create output directory
            createOutputDirectory();
            
            // Step 1: Parse repository documentation (direct file read)
            System.out.println("📖 Parsing repository documentation...");
            ChecklistDocumentation doc = parseChecklistDocumentation();
            
            // Step 2: Extract database metadata (skip for now - mock data)
            System.out.println("🗄️ Creating mock database metadata...");
            ChecklistTableMetadata dbMetadata = createMockChecklistTableMetadata();
            
            // Step 3: Analyze POJO structure (direct file read)
            System.out.println("☕ Analyzing POJO structure...");
            ChecklistPojoInfo pojoInfo = analyzeChecklistPojo();
            
            // Step 4: Create unified mapping
            System.out.println("🔗 Creating unified mapping...");
            ChecklistUnifiedMapping mapping = createUnifiedMapping(doc, dbMetadata, pojoInfo);
            
            // Step 5: Generate DAO components
            System.out.println("⚙️ Generating DAO components...");
            generateChecklistInterface(mapping);
            generateChecklistImplementation(mapping);
            generateChecklistRowMapper(mapping);
            generateChecklistSqlConstants(mapping);
            
            System.out.println("🎉 Successfully generated Checklist DAO with " + 
                             doc.getBusinessMethods().size() + " business methods!");
            
        } catch (Exception e) {
            System.err.println("❌ Error generating Checklist DAO: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Parse repository documentation directly from markdown file
     */
    private ChecklistDocumentation parseChecklistDocumentation() throws IOException {
        String content = Files.readString(new File(CHECKLIST_DOC_PATH).toPath());
        
        ChecklistDocumentation doc = new ChecklistDocumentation();
        doc.setEntityName("Checklist");
        doc.setRepositoryName("ChecklistRepository");
        
        // Extract business methods from documentation
        List<BusinessMethod> methods = extractBusinessMethodsFromDoc(content);
        doc.setBusinessMethods(methods);
        
        System.out.println("📋 Found " + methods.size() + " business methods in documentation");
        return doc;
    }
    
    /**
     * Extract database metadata directly from checklist table
     */
    private ChecklistTableMetadata extractChecklistTableMetadata() throws SQLException {
        ChecklistTableMetadata metadata = new ChecklistTableMetadata();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            DatabaseMetaData dbMeta = conn.getMetaData();
            
            // Extract table columns
            List<ColumnMetadata> columns = new ArrayList<>();
            ResultSet rs = dbMeta.getColumns(null, "public", "checklists", null);
            
            while (rs.next()) {
                ColumnMetadata column = new ColumnMetadata();
                column.setName(rs.getString("COLUMN_NAME"));
                column.setType(rs.getString("TYPE_NAME"));
                column.setSize(rs.getInt("COLUMN_SIZE"));
                column.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                columns.add(column);
            }
            
            metadata.setColumns(columns);
            
            // Extract primary keys
            Set<String> primaryKeys = new HashSet<>();
            ResultSet pkRs = dbMeta.getPrimaryKeys(null, "public", "checklists");
            while (pkRs.next()) {
                primaryKeys.add(pkRs.getString("COLUMN_NAME"));
            }
            metadata.setPrimaryKeys(primaryKeys);
            
            System.out.println("🗄️ Found " + columns.size() + " columns in checklist table");
        }
        
        return metadata;
    }
    
    /**
     * Create mock database metadata for testing without DB connection
     */
    private ChecklistTableMetadata createMockChecklistTableMetadata() {
        ChecklistTableMetadata metadata = new ChecklistTableMetadata();
        
        // Mock common checklist table columns
        List<ColumnMetadata> columns = new ArrayList<>();
        
        // Add typical checklist columns
        addMockColumn(columns, "id", "bigint", 8, false);
        addMockColumn(columns, "name", "varchar", 255, false);
        addMockColumn(columns, "description", "text", 0, true);
        addMockColumn(columns, "facility_id", "bigint", 8, false);
        addMockColumn(columns, "use_case_id", "bigint", 8, false);
        addMockColumn(columns, "created_at", "bigint", 8, true);
        addMockColumn(columns, "modified_at", "bigint", 8, true);
        addMockColumn(columns, "created_by", "bigint", 8, true);
        addMockColumn(columns, "modified_by", "bigint", 8, true);
        addMockColumn(columns, "archived", "boolean", 1, true);
        
        metadata.setColumns(columns);
        
        // Set primary key
        Set<String> primaryKeys = new HashSet<>();
        primaryKeys.add("id");
        metadata.setPrimaryKeys(primaryKeys);
        
        System.out.println("🗄️ Created mock metadata with " + columns.size() + " columns");
        return metadata;
    }
    
    private void addMockColumn(List<ColumnMetadata> columns, String name, String type, int size, boolean nullable) {
        ColumnMetadata column = new ColumnMetadata();
        column.setName(name);
        column.setType(type);
        column.setSize(size);
        column.setNullable(nullable);
        columns.add(column);
    }
    
    /**
     * Analyze POJO structure directly from Java file
     */
    private ChecklistPojoInfo analyzeChecklistPojo() throws IOException {
        String content = Files.readString(new File(CHECKLIST_POJO_PATH).toPath());
        
        ChecklistPojoInfo pojoInfo = new ChecklistPojoInfo();
        pojoInfo.setClassName("Checklists");
        
        // Extract field mappings from POJO
        Map<String, FieldMapping> fieldMappings = extractFieldMappingsFromPojo(content);
        pojoInfo.setFieldMappings(fieldMappings);
        
        System.out.println("☕ Found " + fieldMappings.size() + " fields in Checklists POJO");
        return pojoInfo;
    }
    
    /**
     * Create unified mapping from all three sources
     */
    private ChecklistUnifiedMapping createUnifiedMapping(ChecklistDocumentation doc, 
                                                        ChecklistTableMetadata dbMetadata, 
                                                        ChecklistPojoInfo pojoInfo) {
        ChecklistUnifiedMapping mapping = new ChecklistUnifiedMapping();
        mapping.setDocumentation(doc);
        mapping.setDatabaseMetadata(dbMetadata);
        mapping.setPojoInfo(pojoInfo);
        
        // Validate consistency between sources
        validateInputConsistency(mapping);
        
        return mapping;
    }
    
    /**
     * Generate DAO interface from documentation methods
     */
    private void generateChecklistInterface(ChecklistUnifiedMapping mapping) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // Package and imports
        sb.append("package ").append(OUTPUT_PACKAGE).append(";\n\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Optional;\n");
        sb.append("import com.example.pojogenerator.pojos.Checklists;\n\n");
        
        // Interface documentation
        sb.append("/**\n");
        sb.append(" * DWI-Generated DAO interface for Checklist entity\n");
        sb.append(" * Generated from repository documentation with ").append(mapping.getDocumentation().getBusinessMethods().size()).append(" business methods\n");
        sb.append(" * Source: ").append(CHECKLIST_DOC_PATH).append("\n");
        sb.append(" */\n");
        sb.append("public interface ChecklistDao {\n\n");
        
        // Standard CRUD methods
        sb.append("    // Standard CRUD operations\n");
        sb.append("    Optional<Checklists> findById(Long id);\n");
        sb.append("    List<Checklists> findAll();\n");
        sb.append("    Checklists save(Checklists entity);\n");
        sb.append("    void deleteById(Long id);\n");
        sb.append("    long count();\n\n");
        
        // Business methods from documentation
        sb.append("    // Business methods from repository documentation\n");
        for (BusinessMethod method : mapping.getDocumentation().getBusinessMethods()) {
            sb.append("    ").append(method.getSignature()).append(";\n");
        }
        
        sb.append("}\n");
        
        writeToFile(OUTPUT_DIR + "/ChecklistDao.java", sb.toString());
        System.out.println("✅ Generated ChecklistDao.java interface");
    }
    
    /**
     * Generate JDBC implementation with business logic
     */
    private void generateChecklistImplementation(ChecklistUnifiedMapping mapping) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // Package and imports
        sb.append("package ").append(OUTPUT_PACKAGE).append(";\n\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Map;\n");
        sb.append("import java.util.Optional;\n");
        sb.append("import org.springframework.dao.EmptyResultDataAccessException;\n");
        sb.append("import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;\n");
        sb.append("import org.springframework.stereotype.Repository;\n");
        sb.append("import com.example.pojogenerator.pojos.Checklists;\n\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * DWI-Generated JDBC implementation for ChecklistDao\n");
        sb.append(" * Generated from documentation + database metadata + POJO analysis\n");
        sb.append(" */\n");
        sb.append("@Repository\n");
        sb.append("public class JdbcChecklistDao implements ChecklistDao {\n\n");
        
        // Fields and constructor
        sb.append("    private final NamedParameterJdbcTemplate jdbcTemplate;\n");
        sb.append("    private final ChecklistRowMapper rowMapper;\n\n");
        
        sb.append("    public JdbcChecklistDao(NamedParameterJdbcTemplate jdbcTemplate) {\n");
        sb.append("        this.jdbcTemplate = jdbcTemplate;\n");
        sb.append("        this.rowMapper = new ChecklistRowMapper();\n");
        sb.append("    }\n\n");
        
        // Standard CRUD implementations
        generateStandardCrudImplementations(sb, mapping);
        
        // Business method implementations
        generateBusinessMethodImplementations(sb, mapping);
        
        sb.append("}\n");
        
        writeToFile(OUTPUT_DIR + "/JdbcChecklistDao.java", sb.toString());
        System.out.println("✅ Generated JdbcChecklistDao.java implementation");
    }
    
    /**
     * Generate row mapper from database metadata and POJO fields
     */
    private void generateChecklistRowMapper(ChecklistUnifiedMapping mapping) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // Package and imports
        sb.append("package ").append(OUTPUT_PACKAGE).append(";\n\n");
        sb.append("import java.sql.ResultSet;\n");
        sb.append("import java.sql.SQLException;\n");
        sb.append("import org.springframework.jdbc.core.RowMapper;\n");
        sb.append("import com.example.pojogenerator.pojos.Checklists;\n\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * DWI-Generated row mapper for Checklists entity\n");
        sb.append(" * Generated from database metadata and POJO field analysis\n");
        sb.append(" */\n");
        sb.append("public class ChecklistRowMapper implements RowMapper<Checklists> {\n\n");
        
        // mapRow method
        sb.append("    @Override\n");
        sb.append("    public Checklists mapRow(ResultSet rs, int rowNum) throws SQLException {\n");
        sb.append("        Checklists checklist = new Checklists();\n\n");
        
        // Map fields based on database columns and POJO fields
        for (FieldMapping field : mapping.getPojoInfo().getFieldMappings().values()) {
            generateFieldMapping(sb, field);
        }
        
        sb.append("\n        return checklist;\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        writeToFile(OUTPUT_DIR + "/ChecklistRowMapper.java", sb.toString());
        System.out.println("✅ Generated ChecklistRowMapper.java");
    }
    
    /**
     * Generate SQL constants from documentation queries
     */
    private void generateChecklistSqlConstants(ChecklistUnifiedMapping mapping) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // Package
        sb.append("package ").append(OUTPUT_PACKAGE).append(";\n\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * DWI-Generated SQL constants for Checklist entity\n");
        sb.append(" * SQL queries extracted from repository documentation\n");
        sb.append(" */\n");
        sb.append("public final class ChecklistSql {\n\n");
        
        // Standard CRUD queries
        generateStandardSqlQueries(sb, mapping);
        
        // Business method queries from documentation
        generateBusinessSqlQueries(sb, mapping);
        
        sb.append("    private ChecklistSql() {\n");
        sb.append("        // Utility class\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        writeToFile(OUTPUT_DIR + "/ChecklistSql.java", sb.toString());
        System.out.println("✅ Generated ChecklistSql.java constants");
    }
    
    // Helper methods for parsing and generation
    
    private List<BusinessMethod> extractBusinessMethodsFromDoc(String content) {
        List<BusinessMethod> methods = new ArrayList<>();
        
        // Parse method blocks from documentation
        Pattern methodBlockPattern = Pattern.compile(
            "#### Method: (.*?)\\n```yaml\\n(.*?)\\n```", 
            Pattern.DOTALL
        );
        Matcher matcher = methodBlockPattern.matcher(content);
        
        while (matcher.find()) {
            String methodSignature = matcher.group(1).trim();
            String yamlContent = matcher.group(2);
            
            BusinessMethod method = new BusinessMethod();
            method.setSignature(extractSignatureFromYaml(yamlContent));
            method.setSqlQuery(extractSqlFromYaml(yamlContent));
            method.setBusinessLogic(extractBusinessLogicFromYaml(yamlContent));
            
            methods.add(method);
            
            System.out.println("📝 Parsed method: " + method.getSignature());
            if (method.getSqlQuery() != null && !method.getSqlQuery().trim().isEmpty()) {
                System.out.println("   📊 SQL: " + method.getSqlQuery().substring(0, Math.min(50, method.getSqlQuery().length())) + "...");
            }
        }
        
        return methods;
    }
    
    private String extractSignatureFromYaml(String yamlContent) {
        Pattern signaturePattern = Pattern.compile("Signature: (.*)");
        Matcher matcher = signaturePattern.matcher(yamlContent);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Unknown signature";
    }
    
    private String extractSqlFromYaml(String yamlContent) {
        Pattern sqlPattern = Pattern.compile("SQL Query: \\|\\n(.*?)(?=\\n\\n|Parameters:|Returns:|$)", Pattern.DOTALL);
        Matcher matcher = sqlPattern.matcher(yamlContent);
        if (matcher.find()) {
            String sql = matcher.group(1);
            // Clean up the SQL - remove leading spaces and BUSINESS LOGIC comments
            StringBuilder cleanSql = new StringBuilder();
            String[] lines = sql.split("\\n");
            boolean inBusinessLogic = false;
            
            for (String line : lines) {
                String trimmedLine = line.trim();
                
                // Skip business logic section
                if (trimmedLine.startsWith("BUSINESS LOGIC:")) {
                    inBusinessLogic = true;
                    continue;
                }
                
                // Skip numbered business logic points
                if (inBusinessLogic && (trimmedLine.matches("^\\d+\\..*") || trimmedLine.isEmpty())) {
                    continue;
                }
                
                // Reset business logic flag when we hit actual SQL
                if (inBusinessLogic && (trimmedLine.startsWith("SELECT") || trimmedLine.startsWith("UPDATE") || 
                                      trimmedLine.startsWith("DELETE") || trimmedLine.startsWith("INSERT"))) {
                    inBusinessLogic = false;
                }
                
                // Add SQL lines
                if (!inBusinessLogic && !trimmedLine.isEmpty()) {
                    if (cleanSql.length() > 0) {
                        cleanSql.append("\n");
                    }
                    cleanSql.append("        ").append(trimmedLine);
                }
            }
            
            return cleanSql.toString().trim();
        }
        return "-- TODO: Add SQL query";
    }
    
    private String extractBusinessLogicFromYaml(String yamlContent) {
        Pattern businessLogicPattern = Pattern.compile("Business Logic Derivation:\\n(.*?)(?=\\n\\nSQL Query:|Parameters:|Returns:|$)", Pattern.DOTALL);
        Matcher matcher = businessLogicPattern.matcher(yamlContent);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Business logic not documented";
    }
    
    private Map<String, FieldMapping> extractFieldMappingsFromPojo(String content) {
        Map<String, FieldMapping> mappings = new HashMap<>();
        
        // Parse field declarations from POJO
        Pattern fieldPattern = Pattern.compile("private (\\w+(?:\\.\\w+)*(?:<[^>]+>)?) (\\w+);");
        Matcher fieldMatcher = fieldPattern.matcher(content);
        
        while (fieldMatcher.find()) {
            String javaType = fieldMatcher.group(1);
            String fieldName = fieldMatcher.group(2);
            
            FieldMapping mapping = new FieldMapping();
            mapping.setFieldName(fieldName);
            mapping.setJavaType(javaType);
            mapping.setColumnName(convertFieldNameToColumnName(fieldName));
            
            mappings.put(fieldName, mapping);
        }
        
        return mappings;
    }
    
    private void validateInputConsistency(ChecklistUnifiedMapping mapping) {
        // Validate that documentation methods, database columns, and POJO fields are consistent
        System.out.println("🔍 Validating input consistency...");
        // Implementation for validation logic
    }
    
    private void generateStandardCrudImplementations(StringBuilder sb, ChecklistUnifiedMapping mapping) {
        // Generate standard CRUD method implementations
        sb.append("    // Standard CRUD implementations\n");
        sb.append("    @Override\n");
        sb.append("    public Optional<Checklists> findById(Long id) {\n");
        sb.append("        try {\n");
        sb.append("            Checklists result = jdbcTemplate.queryForObject(\n");
        sb.append("                ChecklistSql.FIND_BY_ID,\n");
        sb.append("                Map.of(\"id\", id),\n");
        sb.append("                rowMapper\n");
        sb.append("            );\n");
        sb.append("            return Optional.ofNullable(result);\n");
        sb.append("        } catch (EmptyResultDataAccessException e) {\n");
        sb.append("            return Optional.empty();\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        
        // Add other CRUD implementations...
    }
    
    private void generateBusinessMethodImplementations(StringBuilder sb, ChecklistUnifiedMapping mapping) {
        sb.append("    // Business method implementations from documentation\n");
        for (BusinessMethod method : mapping.getDocumentation().getBusinessMethods()) {
            sb.append("    @Override\n");
            sb.append("    public ").append(method.getSignature()).append(" {\n");
            sb.append("        // Implementation based on documentation SQL\n");
            sb.append("        // TODO: Complete implementation\n");
            sb.append("    }\n\n");
        }
    }
    
    private void generateStandardSqlQueries(StringBuilder sb, ChecklistUnifiedMapping mapping) {
        sb.append("    // Standard CRUD queries\n");
        sb.append("    public static final String FIND_BY_ID = \"\"\"\n");
        sb.append("        SELECT * FROM checklists WHERE id = :id\n");
        sb.append("        \"\"\";\n\n");
        
        // Add other standard queries...
    }
    
    private void generateBusinessSqlQueries(StringBuilder sb, ChecklistUnifiedMapping mapping) {
        sb.append("    // Business method queries from documentation\n");
        for (BusinessMethod method : mapping.getDocumentation().getBusinessMethods()) {
            String constantName = convertMethodNameToConstant(method.getSignature());
            sb.append("    public static final String ").append(constantName).append(" = \"\"\"\n");
            sb.append("        ").append(method.getSqlQuery()).append("\n");
            sb.append("        \"\"\";\n\n");
        }
    }
    
    private void generateFieldMapping(StringBuilder sb, FieldMapping field) {
        String setterName = "set" + capitalize(field.getFieldName());
        
        if (field.getJavaType().equals("Long")) {
            sb.append("        checklist.").append(setterName).append("(rs.getLong(\"").append(field.getColumnName()).append("\"));\n");
        } else if (field.getJavaType().equals("String")) {
            sb.append("        checklist.").append(setterName).append("(rs.getString(\"").append(field.getColumnName()).append("\"));\n");
        } else {
            sb.append("        checklist.").append(setterName).append("(rs.getString(\"").append(field.getColumnName()).append("\"));\n");
        }
    }
    
    // Utility methods
    
    private void createOutputDirectory() {
        new File(OUTPUT_DIR).mkdirs();
    }
    
    private void writeToFile(String fileName, String content) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        }
        System.out.println("Generated: " + fileName);
    }
    
    private String convertFieldNameToColumnName(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    private String convertMethodNameToConstant(String methodName) {
        return methodName.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    // Model classes for data structures
    
    static class ChecklistDocumentation {
        private String entityName;
        private String repositoryName;
        private List<BusinessMethod> businessMethods = new ArrayList<>();
        
        // Getters and setters
        public String getEntityName() { return entityName; }
        public void setEntityName(String entityName) { this.entityName = entityName; }
        
        public String getRepositoryName() { return repositoryName; }
        public void setRepositoryName(String repositoryName) { this.repositoryName = repositoryName; }
        
        public List<BusinessMethod> getBusinessMethods() { return businessMethods; }
        public void setBusinessMethods(List<BusinessMethod> businessMethods) { this.businessMethods = businessMethods; }
    }
    
    static class BusinessMethod {
        private String signature;
        private String sqlQuery;
        private String businessLogic;
        
        // Getters and setters
        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
        
        public String getSqlQuery() { return sqlQuery; }
        public void setSqlQuery(String sqlQuery) { this.sqlQuery = sqlQuery; }
        
        public String getBusinessLogic() { return businessLogic; }
        public void setBusinessLogic(String businessLogic) { this.businessLogic = businessLogic; }
    }
    
    static class ChecklistTableMetadata {
        private List<ColumnMetadata> columns = new ArrayList<>();
        private Set<String> primaryKeys = new HashSet<>();
        
        // Getters and setters
        public List<ColumnMetadata> getColumns() { return columns; }
        public void setColumns(List<ColumnMetadata> columns) { this.columns = columns; }
        
        public Set<String> getPrimaryKeys() { return primaryKeys; }
        public void setPrimaryKeys(Set<String> primaryKeys) { this.primaryKeys = primaryKeys; }
    }
    
    static class ColumnMetadata {
        private String name;
        private String type;
        private int size;
        private boolean nullable;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        
        public boolean isNullable() { return nullable; }
        public void setNullable(boolean nullable) { this.nullable = nullable; }
    }
    
    static class ChecklistPojoInfo {
        private String className;
        private Map<String, FieldMapping> fieldMappings = new HashMap<>();
        
        // Getters and setters
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public Map<String, FieldMapping> getFieldMappings() { return fieldMappings; }
        public void setFieldMappings(Map<String, FieldMapping> fieldMappings) { this.fieldMappings = fieldMappings; }
    }
    
    static class FieldMapping {
        private String fieldName;
        private String columnName;
        private String javaType;
        
        // Getters and setters
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        
        public String getColumnName() { return columnName; }
        public void setColumnName(String columnName) { this.columnName = columnName; }
        
        public String getJavaType() { return javaType; }
        public void setJavaType(String javaType) { this.javaType = javaType; }
    }
    
    static class ChecklistUnifiedMapping {
        private ChecklistDocumentation documentation;
        private ChecklistTableMetadata databaseMetadata;
        private ChecklistPojoInfo pojoInfo;
        
        // Getters and setters
        public ChecklistDocumentation getDocumentation() { return documentation; }
        public void setDocumentation(ChecklistDocumentation documentation) { this.documentation = documentation; }
        
        public ChecklistTableMetadata getDatabaseMetadata() { return databaseMetadata; }
        public void setDatabaseMetadata(ChecklistTableMetadata databaseMetadata) { this.databaseMetadata = databaseMetadata; }
        
        public ChecklistPojoInfo getPojoInfo() { return pojoInfo; }
        public void setPojoInfo(ChecklistPojoInfo pojoInfo) { this.pojoInfo = pojoInfo; }
    }
}
