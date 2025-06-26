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
 * Repository-Driven DAO Generator
 * 
 * Combines the best of both worlds:
 * 1. Database introspection (from EnhancedJdbcDaoGenerator)
 * 2. Repository documentation parsing (enhanced from ChecklistDwiGenerator)
 * 3. Complete method implementation with proper SQL
 * 
 * Inputs:
 * - Table name and schema (database connection)
 * - POJO class (existing generated POJO)
 * - Repository documentation (markdown file)
 * 
 * Outputs:
 * - Complete DAO with custom methods from documentation
 * - Proper SQL implementation
 * - Full Spring Boot integration
 */
public class RepositoryDrivenDaoGenerator {
    
    // Database configuration
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/qa_";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "postgres";
    private static final String SCHEMA = "public";
    
    // Base paths
    private static final String POJO_BASE_PATH = "src/main/java/com/example/pojogenerator/pojos/";
    private static final String REPO_DOC_BASE_PATH = "repository_documents/repository_docs/";
    private static final String OUTPUT_BASE_PATH = "src/main/java/com/example/dwiDaoGenerator/";
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Repository-Driven DAO Generator...");
        
        RepositoryDrivenDaoGenerator generator = new RepositoryDrivenDaoGenerator();
        
        // Example: Generate DAO for Checklist
        generator.generateRepositoryDrivenDao(
            "checklists",           // table name
            "Checklist",            // entity name
            "ChecklistRepositorydoc.md"  // repository doc file
        );
        
        System.out.println("✅ Repository-Driven DAO generation completed!");
    }
    
    /**
     * Main generation method
     */
    public void generateRepositoryDrivenDao(String tableName, String entityName, String repoDocFile) {
        try {
            System.out.println("📋 Generating DAO for: " + entityName + " (table: " + tableName + ")");
            
            // Step 1: Extract database metadata (real connection)
            System.out.println("🗄️ Extracting database metadata...");
            TableMetadata tableMetadata = extractTableMetadata(tableName);
            
            // Step 2: Analyze existing POJO
            System.out.println("☕ Analyzing POJO structure...");
            PojoMetadata pojoMetadata = analyzePojoStructure(entityName);
            
            // Step 3: Parse repository documentation
            System.out.println("📖 Parsing repository documentation...");
            RepositoryDocumentation repoDoc = parseRepositoryDocumentation(repoDocFile);
            
            // Step 4: Create unified model
            System.out.println("🔗 Creating unified DAO model...");
            DaoGenerationModel model = createDaoModel(tableMetadata, pojoMetadata, repoDoc, entityName);
            
            // Step 5: Generate complete DAO
            System.out.println("⚙️ Generating DAO components...");
            generateCompleteDao(model);
            
            System.out.println("🎉 Successfully generated " + entityName + "Dao with " + 
                             repoDoc.getCustomMethods().size() + " custom methods!");
            
        } catch (Exception e) {
            System.err.println("❌ Error generating DAO for " + entityName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Extract real database metadata using JDBC
     */
    private TableMetadata extractTableMetadata(String tableName) throws SQLException {
        TableMetadata metadata = new TableMetadata();
        metadata.setTableName(tableName);
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("✅ Connected to database successfully!");
            
            DatabaseMetaData dbMeta = conn.getMetaData();
            
            // Extract columns
            List<ColumnInfo> columns = new ArrayList<>();
            ResultSet rs = dbMeta.getColumns(null, SCHEMA, tableName, null);
            
            while (rs.next()) {
                ColumnInfo column = new ColumnInfo();
                column.setName(rs.getString("COLUMN_NAME"));
                column.setType(rs.getString("TYPE_NAME"));
                column.setSize(rs.getInt("COLUMN_SIZE"));
                column.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                column.setDefaultValue(rs.getString("COLUMN_DEF"));
                columns.add(column);
            }
            metadata.setColumns(columns);
            
            // Extract primary keys
            Set<String> primaryKeys = new HashSet<>();
            ResultSet pkRs = dbMeta.getPrimaryKeys(null, SCHEMA, tableName);
            while (pkRs.next()) {
                primaryKeys.add(pkRs.getString("COLUMN_NAME"));
            }
            metadata.setPrimaryKeys(primaryKeys);
            
            // Extract foreign keys
            Map<String, String> foreignKeys = new HashMap<>();
            ResultSet fkRs = dbMeta.getImportedKeys(null, SCHEMA, tableName);
            while (fkRs.next()) {
                String fkCol = fkRs.getString("FKCOLUMN_NAME");
                String pkTable = fkRs.getString("PKTABLE_NAME");
                String pkCol = fkRs.getString("PKCOLUMN_NAME");
                foreignKeys.put(fkCol, pkTable + "." + pkCol);
            }
            metadata.setForeignKeys(foreignKeys);
            
            System.out.println("📊 Found " + columns.size() + " columns, " + 
                             primaryKeys.size() + " primary keys, " + 
                             foreignKeys.size() + " foreign keys");
        }
        
        return metadata;
    }
    
    /**
     * Analyze existing POJO structure
     */
    private PojoMetadata analyzePojoStructure(String entityName) throws IOException {
        String pojoPath = POJO_BASE_PATH + entityName + ".java";
        String content = Files.readString(new File(pojoPath).toPath());
        
        PojoMetadata metadata = new PojoMetadata();
        metadata.setClassName(entityName);
        metadata.setPackageName("com.example.pojogenerator.pojos");
        
        // Extract field mappings
        Map<String, FieldInfo> fields = new HashMap<>();
        Pattern fieldPattern = Pattern.compile("private (\\w+(?:\\.\\w+)*(?:<[^>]+>)?) (\\w+);");
        Matcher fieldMatcher = fieldPattern.matcher(content);
        
        while (fieldMatcher.find()) {
            String javaType = fieldMatcher.group(1);
            String fieldName = fieldMatcher.group(2);
            
            FieldInfo field = new FieldInfo();
            field.setFieldName(fieldName);
            field.setJavaType(javaType);
            field.setColumnName(convertFieldNameToColumnName(fieldName));
            
            fields.put(fieldName, field);
        }
        
        metadata.setFields(fields);
        System.out.println("☕ Found " + fields.size() + " fields in " + entityName + " POJO");
        
        return metadata;
    }
    
    /**
     * Parse repository documentation with enhanced SQL extraction
     */
    private RepositoryDocumentation parseRepositoryDocumentation(String repoDocFile) throws IOException {
        String docPath = REPO_DOC_BASE_PATH + repoDocFile;
        String content = Files.readString(new File(docPath).toPath());
        
        RepositoryDocumentation doc = new RepositoryDocumentation();
        doc.setDocumentPath(docPath);
        
        // Extract custom methods with complete information
        List<CustomMethod> methods = extractCustomMethodsFromDoc(content);
        doc.setCustomMethods(methods);
        
        // Extract transaction requirements
        doc.setTransactionRequirements(extractTransactionRequirements(content));
        
        System.out.println("📖 Parsed " + methods.size() + " custom methods from documentation");
        return doc;
    }
    
    /**
     * Enhanced method extraction with proper SQL parsing
     */
    private List<CustomMethod> extractCustomMethodsFromDoc(String content) {
        List<CustomMethod> methods = new ArrayList<>();
        
        // Pattern to match method documentation blocks
        Pattern methodBlockPattern = Pattern.compile(
            "#### Method: ([^\\n]+)\\n```yaml\\n(.*?)\\n```", 
            Pattern.DOTALL
        );
        Matcher matcher = methodBlockPattern.matcher(content);
        
        while (matcher.find()) {
            String methodTitle = matcher.group(1).trim();
            String yamlContent = matcher.group(2);
            
            CustomMethod method = new CustomMethod();
            method.setMethodName(extractMethodNameFromTitle(methodTitle));
            method.setSignature(extractSignatureFromYaml(yamlContent));
            method.setSqlQuery(extractAndCleanSqlFromYaml(yamlContent));
            method.setParameters(extractParametersFromYaml(yamlContent));
            method.setReturnType(extractReturnTypeFromYaml(yamlContent));
            method.setTransactionRequired(extractTransactionFromYaml(yamlContent));
            method.setPurpose(extractPurposeFromYaml(yamlContent));
            
            methods.add(method);
            
            System.out.println("📝 Parsed method: " + method.getMethodName());
            if (method.getSqlQuery() != null && !method.getSqlQuery().trim().isEmpty()) {
                System.out.println("   📊 SQL extracted: " + 
                    method.getSqlQuery().substring(0, Math.min(60, method.getSqlQuery().length())) + "...");
            }
        }
        
        return methods;
    }
    
    /**
     * Enhanced SQL extraction with proper cleaning
     */
    private String extractAndCleanSqlFromYaml(String yamlContent) {
        Pattern sqlPattern = Pattern.compile("SQL Query: \\|\\n(.*?)(?=\\n\\nParameters:|\\nReturns:|\\nTransaction:|$)", Pattern.DOTALL);
        Matcher matcher = sqlPattern.matcher(yamlContent);
        
        if (matcher.find()) {
            String rawSql = matcher.group(1);
            return cleanSqlQuery(rawSql);
        }
        
        return null;
    }
    
    /**
     * Clean SQL query by removing business logic comments and formatting properly
     */
    private String cleanSqlQuery(String rawSql) {
        StringBuilder cleanSql = new StringBuilder();
        String[] lines = rawSql.split("\\n");
        boolean inBusinessLogic = false;
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // Skip empty lines at the beginning
            if (cleanSql.length() == 0 && trimmedLine.isEmpty()) {
                continue;
            }
            
            // Detect business logic section
            if (trimmedLine.startsWith("BUSINESS LOGIC:")) {
                inBusinessLogic = true;
                continue;
            }
            
            // Skip business logic lines
            if (inBusinessLogic) {
                if (trimmedLine.matches("^\\d+\\..*") || trimmedLine.isEmpty()) {
                    continue;
                }
                // Reset when we hit actual SQL
                if (trimmedLine.startsWith("SELECT") || trimmedLine.startsWith("UPDATE") || 
                    trimmedLine.startsWith("DELETE") || trimmedLine.startsWith("INSERT")) {
                    inBusinessLogic = false;
                } else {
                    continue;
                }
            }
            
            // Add SQL lines
            if (!trimmedLine.isEmpty()) {
                if (cleanSql.length() > 0) {
                    cleanSql.append("\n");
                }
                cleanSql.append(trimmedLine);
            }
        }
        
        return cleanSql.toString().trim();
    }
    
    /**
     * Extract method parameters from YAML
     */
    private List<MethodParameter> extractParametersFromYaml(String yamlContent) {
        List<MethodParameter> parameters = new ArrayList<>();
        
        Pattern paramPattern = Pattern.compile("Parameters:\\n(.*?)(?=\\n\\nReturns:|\\nTransaction:|$)", Pattern.DOTALL);
        Matcher matcher = paramPattern.matcher(yamlContent);
        
        if (matcher.find()) {
            String paramSection = matcher.group(1);
            Pattern paramLinePattern = Pattern.compile("- (\\w+): (\\w+(?:<[^>]+>)?) \\(([^)]+)\\)");
            Matcher paramMatcher = paramLinePattern.matcher(paramSection);
            
            while (paramMatcher.find()) {
                MethodParameter param = new MethodParameter();
                param.setName(paramMatcher.group(1));
                param.setType(paramMatcher.group(2));
                param.setDescription(paramMatcher.group(3));
                parameters.add(param);
            }
        }
        
        return parameters;
    }
    
    /**
     * Generate complete DAO with all components and supporting files
     */
    private void generateCompleteDao(DaoGenerationModel model) throws IOException {
        String outputDir = OUTPUT_BASE_PATH + model.getEntityName().toLowerCase() + "/generated";
        new File(outputDir).mkdirs();
        
        // Generate core DAO components
        generateDaoInterface(model, outputDir);
        generateDaoImplementation(model, outputDir);
        generateRowMapper(model, outputDir);
        generateSqlConstants(model, outputDir);
        
        // Generate supporting components automatically
        generateViewClasses(model, outputDir);
        generateStateEnums(model, outputDir);
        generateSpringDataTypes(model, outputDir);
        
        System.out.println("✅ Generated complete DAO package with all supporting files");
    }
    
    /**
     * Generate DAO interface with custom methods
     */
    private void generateDaoInterface(DaoGenerationModel model, String outputDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // Package and imports
        sb.append("package com.example.dwiDaoGenerator.").append(model.getEntityName().toLowerCase()).append(".generated;\n\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Optional;\n");
        sb.append("import java.util.Set;\n");
        sb.append("import java.util.Collection;\n");
        sb.append("import ").append(model.getPojoMetadata().getPackageName()).append(".").append(model.getEntityName()).append(";\n");
        
        // Add SpringDataTypes imports if needed
        if (needsSpringDataImports(model)) {
            sb.append("import com.example.dwiDaoGenerator.").append(model.getEntityName().toLowerCase()).append(".generated.SpringDataTypes.*;\n");
        }
        
        sb.append("\n");
        
        // Interface documentation
        sb.append("/**\n");
        sb.append(" * Repository-Driven DAO interface for ").append(model.getEntityName()).append(" entity\n");
        sb.append(" * Generated from: ").append(model.getRepositoryDoc().getDocumentPath()).append("\n");
        sb.append(" * Custom methods: ").append(model.getRepositoryDoc().getCustomMethods().size()).append("\n");
        sb.append(" */\n");
        sb.append("public interface ").append(model.getEntityName()).append("Dao {\n\n");
        
        // Standard CRUD methods
        sb.append("    // Standard CRUD operations\n");
        sb.append("    Optional<").append(model.getEntityName()).append("> findById(Long id);\n");
        sb.append("    List<").append(model.getEntityName()).append("> findAll();\n");
        sb.append("    ").append(model.getEntityName()).append(" save(").append(model.getEntityName()).append(" entity);\n");
        sb.append("    void deleteById(Long id);\n");
        sb.append("    boolean existsById(Long id);\n");
        sb.append("    long count();\n\n");
        
        // Custom methods from documentation
        sb.append("    // Custom methods from repository documentation\n");
        for (CustomMethod method : model.getRepositoryDoc().getCustomMethods()) {
            sb.append("    ").append(method.getSignature()).append(";\n");
        }
        
        sb.append("}\n");
        
        writeToFile(outputDir + "/" + model.getEntityName() + "Dao.java", sb.toString());
        System.out.println("✅ Generated " + model.getEntityName() + "Dao.java interface");
    }
    
    /**
     * Generate complete DAO implementation with working SQL
     */
    private void generateDaoImplementation(DaoGenerationModel model, String outputDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // Package and imports
        sb.append("package com.example.dwiDaoGenerator.").append(model.getEntityName().toLowerCase()).append(".generated;\n\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Map;\n");
        sb.append("import java.util.Optional;\n");
        sb.append("import java.util.Set;\n");
        sb.append("import java.util.HashSet;\n");
        sb.append("import java.util.Collection;\n");
        sb.append("import org.springframework.dao.EmptyResultDataAccessException;\n");
        sb.append("import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;\n");
        sb.append("import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;\n");
        sb.append("import org.springframework.stereotype.Repository;\n");
        sb.append("import org.springframework.transaction.annotation.Transactional;\n");
        sb.append("import ").append(model.getPojoMetadata().getPackageName()).append(".").append(model.getEntityName()).append(";\n");
        
        // Add SpringDataTypes imports if needed
        if (needsSpringDataImports(model)) {
            sb.append("import com.example.dwiDaoGenerator.").append(model.getEntityName().toLowerCase()).append(".generated.SpringDataTypes.*;\n");
        }
        
        sb.append("\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * Repository-Driven implementation for ").append(model.getEntityName()).append("Dao\n");
        sb.append(" * Generated with complete SQL implementations from documentation\n");
        sb.append(" */\n");
        sb.append("@Repository\n");
        sb.append("@Transactional(rollbackFor = Exception.class)\n");
        sb.append("public class ").append(model.getEntityName()).append("DaoImpl implements ").append(model.getEntityName()).append("Dao {\n\n");
        
        // Fields and constructor
        sb.append("    private final NamedParameterJdbcTemplate jdbcTemplate;\n");
        sb.append("    private final ").append(model.getEntityName()).append("RowMapper rowMapper;\n\n");
        
        sb.append("    public ").append(model.getEntityName()).append("DaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {\n");
        sb.append("        this.jdbcTemplate = jdbcTemplate;\n");
        sb.append("        this.rowMapper = new ").append(model.getEntityName()).append("RowMapper();\n");
        sb.append("    }\n\n");
        
        // Generate standard CRUD implementations
        generateStandardCrudMethods(sb, model);
        
        // Generate custom method implementations
        generateCustomMethodImplementations(sb, model);
        
        sb.append("}\n");
        
        writeToFile(outputDir + "/" + model.getEntityName() + "DaoImpl.java", sb.toString());
        System.out.println("✅ Generated " + model.getEntityName() + "DaoImpl.java implementation");
    }
    
    /**
     * Generate custom method implementations with proper SQL
     */
    private void generateCustomMethodImplementations(StringBuilder sb, DaoGenerationModel model) {
        sb.append("    // Custom method implementations from repository documentation\n\n");
        
        for (CustomMethod method : model.getRepositoryDoc().getCustomMethods()) {
            // Add transaction annotation if required
            if (method.isTransactionRequired()) {
                sb.append("    @Transactional\n");
            }
            
            sb.append("    @Override\n");
            sb.append("    public ").append(method.getSignature()).append(" {\n");
            
            if (method.getSqlQuery() != null && !method.getSqlQuery().trim().isEmpty()) {
                generateMethodImplementationBody(sb, method, model);
            } else {
                sb.append("        // TODO: Implement method - SQL not found in documentation\n");
                sb.append("        throw new UnsupportedOperationException(\"Method not implemented: ").append(method.getMethodName()).append("\");\n");
            }
            
            sb.append("    }\n\n");
        }
    }
    
    /**
     * Generate method implementation body with proper SQL execution
     */
    private void generateMethodImplementationBody(StringBuilder sb, CustomMethod method, DaoGenerationModel model) {
        String returnType = method.getReturnType();
        String methodName = method.getMethodName();
        
        // Handle special cases for dynamic queries and Spring Data types
        if (isDynamicQuery(method.getSqlQuery()) || isSpringDataMethod(method)) {
            generateDynamicMethodImplementation(sb, method, model);
            return;
        }
        
        String sqlConstant = model.getEntityName() + "Sql." + 
                           convertMethodNameToConstant(method.getMethodName());
        
        // Extract parameter names from method signature (not YAML)
        List<String> signatureParams = extractParameterNamesFromSignature(method.getSignature());
        
        // Build parameter map using signature parameters
        if (!signatureParams.isEmpty()) {
            sb.append("        MapSqlParameterSource params = new MapSqlParameterSource();\n");
            for (String paramName : signatureParams) {
                sb.append("        params.addValue(\"").append(paramName).append("\", ").append(paramName).append(");\n");
            }
            sb.append("\n");
        }
        
        // Generate appropriate query execution based on return type
        if (returnType.startsWith("Optional<")) {
            generateOptionalQueryExecution(sb, sqlConstant, signatureParams, model);
        } else if (returnType.startsWith("List<")) {
            generateListQueryExecution(sb, sqlConstant, signatureParams, returnType, model);
        } else if (returnType.startsWith("Set<")) {
            generateSetQueryExecution(sb, sqlConstant, signatureParams, returnType, model);
        } else if (returnType.equals("void")) {
            generateUpdateExecution(sb, sqlConstant, signatureParams);
        } else {
            generateSingleValueExecution(sb, sqlConstant, signatureParams, returnType);
        }
    }
    
    /**
     * Check if method uses Spring Data types
     */
    private boolean isSpringDataMethod(CustomMethod method) {
        String signature = method.getSignature();
        return signature.contains("Page<") || signature.contains("Pageable") || 
               signature.contains("Sort") || signature.contains("Specification");
    }
    
    /**
     * Generate implementation for dynamic queries and Spring Data methods
     */
    private void generateDynamicMethodImplementation(StringBuilder sb, CustomMethod method, DaoGenerationModel model) {
        String methodName = method.getMethodName();
        String returnType = method.getReturnType();
        
        switch (methodName) {
            case "findAll":
                if (returnType.startsWith("Page<")) {
                    generatePagedFindAllImplementation(sb, model);
                } else {
                    generateStandardFindAllImplementation(sb, model);
                }
                break;
                
            case "findAllByIdIn":
                generateFindAllByIdInImplementation(sb, model);
                break;
                
            default:
                sb.append("        // TODO: Implement dynamic method - requires custom business logic\n");
                sb.append("        throw new UnsupportedOperationException(\"Dynamic method not implemented: ").append(methodName).append("\");\n");
                break;
        }
    }
    
    /**
     * Generate paged findAll implementation
     */
    private void generatePagedFindAllImplementation(StringBuilder sb, DaoGenerationModel model) {
        sb.append("        // TODO: Implement paged findAll with Specification\n");
        sb.append("        // This requires dynamic SQL generation based on Specification criteria\n");
        sb.append("        // For now, return empty page\n");
        sb.append("        throw new UnsupportedOperationException(\"Paged findAll with Specification not implemented yet\");\n");
    }
    
    /**
     * Generate standard findAll implementation
     */
    private void generateStandardFindAllImplementation(StringBuilder sb, DaoGenerationModel model) {
        sb.append("        return jdbcTemplate.query(").append(model.getEntityName()).append("Sql.FIND_ALL, rowMapper);\n");
    }
    
    /**
     * Generate findAllByIdIn implementation with Sort handling
     */
    private void generateFindAllByIdInImplementation(StringBuilder sb, DaoGenerationModel model) {
        sb.append("        MapSqlParameterSource params = new MapSqlParameterSource();\n");
        sb.append("        params.addValue(\"id\", id);\n");
        sb.append("        \n");
        sb.append("        // TODO: Apply sort parameter to SQL query\n");
        sb.append("        // For now, use basic query without sorting\n");
        sb.append("        return jdbcTemplate.query(\n");
        sb.append("            ").append(model.getEntityName()).append("Sql.FIND_ALL_BY_ID_IN,\n");
        sb.append("            params,\n");
        sb.append("            rowMapper\n");
        sb.append("        );\n");
    }
    
    /**
     * Generate Optional query execution
     */
    private void generateOptionalQueryExecution(StringBuilder sb, String sqlConstant, List<String> signatureParams, DaoGenerationModel model) {
        sb.append("        try {\n");
        sb.append("            ").append(model.getEntityName()).append(" result = jdbcTemplate.queryForObject(\n");
        sb.append("                ").append(sqlConstant).append(",\n");
        if (!signatureParams.isEmpty()) {
            sb.append("                params,\n");
        } else {
            sb.append("                Map.of(),\n");
        }
        sb.append("                rowMapper\n");
        sb.append("            );\n");
        sb.append("            return Optional.ofNullable(result);\n");
        sb.append("        } catch (EmptyResultDataAccessException e) {\n");
        sb.append("            return Optional.empty();\n");
        sb.append("        }\n");
    }
    
    /**
     * Generate List query execution with proper row mapper
     */
    private void generateListQueryExecution(StringBuilder sb, String sqlConstant, List<String> signatureParams, String returnType, DaoGenerationModel model) {
        // Check if it's a list of view objects or entity objects
        if (returnType.contains("View") || returnType.contains("Long") || returnType.contains("String")) {
            // For view objects or primitive types, use appropriate row mapper
            String rowMapperType = extractRowMapperType(returnType);
            sb.append("        return jdbcTemplate.query(\n");
            sb.append("            ").append(sqlConstant).append(",\n");
            if (!signatureParams.isEmpty()) {
                sb.append("            params,\n");
            } else {
                sb.append("            Map.of(),\n");
            }
            sb.append("            ").append(rowMapperType).append("\n");
            sb.append("        );\n");
        } else {
            // For entity objects, use entity row mapper
            sb.append("        return jdbcTemplate.query(\n");
            sb.append("            ").append(sqlConstant).append(",\n");
            if (!signatureParams.isEmpty()) {
                sb.append("            params,\n");
            } else {
                sb.append("            Map.of(),\n");
            }
            sb.append("            rowMapper\n");
            sb.append("        );\n");
        }
    }
    
    /**
     * Generate Set query execution
     */
    private void generateSetQueryExecution(StringBuilder sb, String sqlConstant, List<String> signatureParams, String returnType, DaoGenerationModel model) {
        sb.append("        List<").append(extractGenericType(returnType)).append("> resultList = jdbcTemplate.query(\n");
        sb.append("            ").append(sqlConstant).append(",\n");
        if (!signatureParams.isEmpty()) {
            sb.append("            params,\n");
        } else {
            sb.append("            Map.of(),\n");
        }
        sb.append("            ").append(extractRowMapperType(returnType)).append("\n");
        sb.append("        );\n");
        sb.append("        return new HashSet<>(resultList);\n");
    }
    
    /**
     * Generate update execution
     */
    private void generateUpdateExecution(StringBuilder sb, String sqlConstant, List<String> signatureParams) {
        sb.append("        jdbcTemplate.update(\n");
        sb.append("            ").append(sqlConstant).append(",\n");
        if (!signatureParams.isEmpty()) {
            sb.append("            params\n");
        } else {
            sb.append("            Map.of()\n");
        }
        sb.append("        );\n");
    }
    
    /**
     * Generate single value execution
     */
    private void generateSingleValueExecution(StringBuilder sb, String sqlConstant, List<String> signatureParams, String returnType) {
        if (returnType.contains("State.") || returnType.contains("View")) {
            // For enum types and view types, use proper row mapper
            sb.append("        return jdbcTemplate.queryForObject(\n");
            sb.append("            ").append(sqlConstant).append(",\n");
            if (!signatureParams.isEmpty()) {
                sb.append("            params,\n");
            } else {
                sb.append("            Map.of(),\n");
            }
            sb.append("            ").append(extractRowMapperType(returnType)).append("\n");
            sb.append("        );\n");
        } else {
            // For primitive types, use .class
            sb.append("        return jdbcTemplate.queryForObject(\n");
            sb.append("            ").append(sqlConstant).append(",\n");
            if (!signatureParams.isEmpty()) {
                sb.append("            params,\n");
            } else {
                sb.append("            Map.of(),\n");
            }
            sb.append("            ").append(getJdbcTypeClass(returnType)).append(".class\n");
            sb.append("        );\n");
        }
    }
    
    /**
     * Extract generic type from parameterized type
     */
    private String extractGenericType(String parameterizedType) {
        Pattern pattern = Pattern.compile("<([^>]+)>");
        Matcher matcher = pattern.matcher(parameterizedType);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Object";
    }
    
    /**
     * Extract appropriate row mapper type based on return type
     */
    private String extractRowMapperType(String returnType) {
        if (returnType.contains("Long")) {
            return "(rs, rowNum) -> rs.getLong(1)";
        } else if (returnType.contains("String")) {
            return "(rs, rowNum) -> rs.getString(1)";
        } else if (returnType.contains("State.Checklist")) {
            return "(rs, rowNum) -> State.Checklist.valueOf(rs.getString(1))";
        } else if (returnType.contains("JobLogMigrationChecklistView")) {
            return "(rs, rowNum) -> new JobLogMigrationChecklistView(rs.getLong(\"id\"), rs.getString(\"name\"), rs.getString(\"code\"), rs.getString(\"state\"))";
        } else if (returnType.contains("ChecklistJobLiteView")) {
            return "(rs, rowNum) -> new ChecklistJobLiteView(rs.getLong(\"id\"), rs.getString(\"name\"), rs.getString(\"code\"))";
        } else if (returnType.contains("ChecklistView")) {
            return "(rs, rowNum) -> new ChecklistView(rs.getLong(\"id\"), rs.getString(\"code\"), rs.getString(\"name\"), rs.getString(\"colorCode\"))";
        } else {
            return "rowMapper";
        }
    }
    
    // Helper methods and utility classes...
    
    private void generateStandardCrudMethods(StringBuilder sb, DaoGenerationModel model) {
        sb.append("    // Standard CRUD implementations\n\n");
        
        // findById
        sb.append("    @Override\n");
        sb.append("    public Optional<").append(model.getEntityName()).append("> findById(Long id) {\n");
        sb.append("        try {\n");
        sb.append("            ").append(model.getEntityName()).append(" result = jdbcTemplate.queryForObject(\n");
        sb.append("                ").append(model.getEntityName()).append("Sql.FIND_BY_ID,\n");
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
        sb.append("    public List<").append(model.getEntityName()).append("> findAll() {\n");
        sb.append("        return jdbcTemplate.query(").append(model.getEntityName()).append("Sql.FIND_ALL, rowMapper);\n");
        sb.append("    }\n\n");
        
        // save - smart implementation
        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public ").append(model.getEntityName()).append(" save(").append(model.getEntityName()).append(" entity) {\n");
        sb.append("        if (entity.getId() == null) {\n");
        sb.append("            return insert(entity);\n");
        sb.append("        } else {\n");
        sb.append("            return update(entity);\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        
        // deleteById
        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public void deleteById(Long id) {\n");
        sb.append("        jdbcTemplate.update(").append(model.getEntityName()).append("Sql.DELETE_BY_ID, Map.of(\"id\", id));\n");
        sb.append("    }\n\n");
        
        // existsById
        sb.append("    @Override\n");
        sb.append("    public boolean existsById(Long id) {\n");
        sb.append("        Boolean result = jdbcTemplate.queryForObject(\n");
        sb.append("            ").append(model.getEntityName()).append("Sql.EXISTS_BY_ID,\n");
        sb.append("            Map.of(\"id\", id),\n");
        sb.append("            Boolean.class\n");
        sb.append("        );\n");
        sb.append("        return result != null && result;\n");
        sb.append("    }\n\n");
        
        // count
        sb.append("    @Override\n");
        sb.append("    public long count() {\n");
        sb.append("        Long result = jdbcTemplate.queryForObject(").append(model.getEntityName()).append("Sql.COUNT_ALL, Map.of(), Long.class);\n");
        sb.append("        return result != null ? result : 0L;\n");
        sb.append("    }\n\n");
        
        // Helper methods for save implementation
        generateSaveHelperMethods(sb, model);
    }
    
    /**
     * Generate helper methods for save implementation
     */
    private void generateSaveHelperMethods(StringBuilder sb, DaoGenerationModel model) {
        String entityName = model.getEntityName();
        
        // insert method
        sb.append("    private ").append(entityName).append(" insert(").append(entityName).append(" entity) {\n");
        sb.append("        // Set audit fields if they exist\n");
        sb.append("        long now = System.currentTimeMillis();\n");
        sb.append("        if (entity.getCreatedAt() == null) {\n");
        sb.append("            entity.setCreatedAt(now);\n");
        sb.append("        }\n");
        sb.append("        entity.setModifiedAt(now);\n\n");
        
        sb.append("        MapSqlParameterSource params = createParameterMap(entity);\n");
        sb.append("        Long generatedId = jdbcTemplate.queryForObject(\n");
        sb.append("            ").append(entityName).append("Sql.INSERT,\n");
        sb.append("            params,\n");
        sb.append("            Long.class\n");
        sb.append("        );\n");
        sb.append("        entity.setId(generatedId);\n");
        sb.append("        return entity;\n");
        sb.append("    }\n\n");
        
        // update method
        sb.append("    private ").append(entityName).append(" update(").append(entityName).append(" entity) {\n");
        sb.append("        entity.setModifiedAt(System.currentTimeMillis());\n\n");
        sb.append("        MapSqlParameterSource params = createParameterMap(entity);\n");
        sb.append("        int rowsAffected = jdbcTemplate.update(").append(entityName).append("Sql.UPDATE, params);\n");
        sb.append("        \n");
        sb.append("        if (rowsAffected == 0) {\n");
        sb.append("            throw new RuntimeException(\"Entity not found for update\");\n");
        sb.append("        }\n");
        sb.append("        return entity;\n");
        sb.append("    }\n\n");
        
        // createParameterMap method
        sb.append("    private MapSqlParameterSource createParameterMap(").append(entityName).append(" entity) {\n");
        sb.append("        MapSqlParameterSource params = new MapSqlParameterSource();\n\n");
        
        // Add parameter mappings for all fields
        for (FieldInfo field : model.getPojoMetadata().getFields().values()) {
            String getterName = "get" + capitalize(field.getFieldName());
            String fieldName = field.getFieldName();
            
            if (field.getJavaType().contains("JsonNode")) {
                sb.append("        if (entity.").append(getterName).append("() != null) {\n");
                sb.append("            params.addValue(\"").append(fieldName).append("\", entity.").append(getterName).append("().toString());\n");
                sb.append("        } else {\n");
                sb.append("            params.addValue(\"").append(fieldName).append("\", null);\n");
                sb.append("        }\n");
            } else {
                sb.append("        params.addValue(\"").append(fieldName).append("\", entity.").append(getterName).append("());\n");
            }
        }
        
        sb.append("\n        return params;\n");
        sb.append("    }\n\n");
    }
    
    // Utility methods
    private String extractMethodNameFromTitle(String title) {
        // Extract method name from "Method: methodName(params)"
        Pattern pattern = Pattern.compile("(\\w+)\\s*\\(");
        Matcher matcher = pattern.matcher(title);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return title.trim();
    }
    
    private String extractSignatureFromYaml(String yamlContent) {
        Pattern pattern = Pattern.compile("Signature: (.*)");
        Matcher matcher = pattern.matcher(yamlContent);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "void unknownMethod()";
    }
    
    private String extractReturnTypeFromYaml(String yamlContent) {
        Pattern pattern = Pattern.compile("Returns: ([^\\n]+)");
        Matcher matcher = pattern.matcher(yamlContent);
        if (matcher.find()) {
            String returnDesc = matcher.group(1).trim();
            // Extract type from description like "Optional<Checklist> (parent checklist or empty)"
            Pattern typePattern = Pattern.compile("^([^\\s\\(]+)");
            Matcher typeMatcher = typePattern.matcher(returnDesc);
            if (typeMatcher.find()) {
                return typeMatcher.group(1);
            }
        }
        return "void";
    }
    
    private boolean extractTransactionFromYaml(String yamlContent) {
        return yamlContent.contains("Transaction: Required");
    }
    
    private String extractPurposeFromYaml(String yamlContent) {
        Pattern pattern = Pattern.compile("Purpose: \"([^\"]+)\"");
        Matcher matcher = pattern.matcher(yamlContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    
    private String extractTransactionRequirements(String content) {
        // Extract transaction requirements section
        Pattern pattern = Pattern.compile("## Transaction Requirements\\n```yaml\\n(.*?)\\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    
    private String convertFieldNameToColumnName(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    private String convertMethodNameToConstant(String methodName) {
        return methodName.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
    
    private String getJdbcTypeClass(String returnType) {
        switch (returnType) {
            case "String": return "String";
            case "Long": return "Long";
            case "Integer": return "Integer";
            case "Boolean": return "Boolean";
            default: return "String";
        }
    }
    
    /**
     * Generate enhanced row mapper with complete field mapping
     */
    private void generateRowMapper(DaoGenerationModel model, String outputDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // Package and imports
        sb.append("package com.example.dwiDaoGenerator.").append(model.getEntityName().toLowerCase()).append(".generated;\n\n");
        sb.append("import java.sql.ResultSet;\n");
        sb.append("import java.sql.SQLException;\n");
        sb.append("import java.sql.Timestamp;\n");
        sb.append("import org.springframework.jdbc.core.RowMapper;\n");
        sb.append("import ").append(model.getPojoMetadata().getPackageName()).append(".").append(model.getEntityName()).append(";\n");
        
        // Add JSON imports if needed
        boolean needsJsonImports = model.getPojoMetadata().getFields().values().stream()
            .anyMatch(field -> field.getJavaType().contains("JsonNode"));
        if (needsJsonImports) {
            sb.append("import com.fasterxml.jackson.databind.JsonNode;\n");
            sb.append("import com.fasterxml.jackson.databind.ObjectMapper;\n");
        }
        
        sb.append("\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * Enhanced row mapper for ").append(model.getEntityName()).append(" entity\n");
        sb.append(" * Generated with complete field mapping for all Java types\n");
        sb.append(" */\n");
        sb.append("public class ").append(model.getEntityName()).append("RowMapper implements RowMapper<").append(model.getEntityName()).append("> {\n\n");
        
        if (needsJsonImports) {
            sb.append("    private final ObjectMapper objectMapper = new ObjectMapper();\n\n");
        }
        
        // mapRow method
        sb.append("    @Override\n");
        sb.append("    public ").append(model.getEntityName()).append(" mapRow(ResultSet rs, int rowNum) throws SQLException {\n");
        sb.append("        ").append(model.getEntityName()).append(" entity = new ").append(model.getEntityName()).append("();\n\n");
        
        // Map each field with proper type handling
        for (FieldInfo field : model.getPojoMetadata().getFields().values()) {
            generateEnhancedFieldMapping(sb, field);
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
        
        writeToFile(outputDir + "/" + model.getEntityName() + "RowMapper.java", sb.toString());
        System.out.println("✅ Generated " + model.getEntityName() + "RowMapper.java with enhanced field mapping");
    }
    
    /**
     * Generate enhanced field mapping with proper type handling
     */
    private void generateEnhancedFieldMapping(StringBuilder sb, FieldInfo field) {
        String setterName = "set" + capitalize(field.getFieldName());
        String columnName = field.getColumnName();
        String javaType = field.getJavaType();
        
        if (javaType.contains("JsonNode")) {
            sb.append("        String ").append(field.getFieldName()).append("Json = rs.getString(\"").append(columnName).append("\");\n");
            sb.append("        if (").append(field.getFieldName()).append("Json != null) {\n");
            sb.append("            entity.").append(setterName).append("(parseJsonNode(").append(field.getFieldName()).append("Json));\n");
            sb.append("        }\n");
        } else if (javaType.equals("Long")) {
            sb.append("        entity.").append(setterName).append("(rs.getLong(\"").append(columnName).append("\"));\n");
        } else if (javaType.equals("Integer")) {
            sb.append("        entity.").append(setterName).append("(rs.getInt(\"").append(columnName).append("\"));\n");
        } else if (javaType.equals("String")) {
            sb.append("        entity.").append(setterName).append("(rs.getString(\"").append(columnName).append("\"));\n");
        } else if (javaType.equals("Boolean")) {
            sb.append("        entity.").append(setterName).append("(rs.getBoolean(\"").append(columnName).append("\"));\n");
        } else if (javaType.contains("LocalDateTime")) {
            sb.append("        Timestamp ").append(field.getFieldName()).append("Ts = rs.getTimestamp(\"").append(columnName).append("\");\n");
            sb.append("        if (").append(field.getFieldName()).append("Ts != null) {\n");
            sb.append("            entity.").append(setterName).append("(").append(field.getFieldName()).append("Ts.toLocalDateTime());\n");
            sb.append("        }\n");
        } else if (javaType.contains("LocalDate") || javaType.equals("java.time.LocalDate")) {
            sb.append("        java.sql.Date ").append(field.getFieldName()).append("Date = rs.getDate(\"").append(columnName).append("\");\n");
            sb.append("        if (").append(field.getFieldName()).append("Date != null) {\n");
            sb.append("            entity.").append(setterName).append("(").append(field.getFieldName()).append("Date.toLocalDate());\n");
            sb.append("        }\n");
        } else if (javaType.contains("BigDecimal") || javaType.equals("java.math.BigDecimal")) {
            sb.append("        entity.").append(setterName).append("(rs.getBigDecimal(\"").append(columnName).append("\"));\n");
        } else if (javaType.equals("Double")) {
            sb.append("        entity.").append(setterName).append("(rs.getDouble(\"").append(columnName).append("\"));\n");
        } else if (javaType.equals("Float")) {
            sb.append("        entity.").append(setterName).append("(rs.getFloat(\"").append(columnName).append("\"));\n");
        } else {
            // Default to String for unknown types
            sb.append("        entity.").append(setterName).append("(rs.getString(\"").append(columnName).append("\"));\n");
        }
    }
    
    /**
     * Capitalize first letter of string
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * Generate SQL constants with intelligent parsing and parameter conversion
     */
    private void generateSqlConstants(DaoGenerationModel model, String outputDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // Package and class header
        sb.append("package com.example.dwiDaoGenerator.").append(model.getEntityName().toLowerCase()).append(".generated;\n\n");
        
        // Class documentation
        sb.append("/**\n");
        sb.append(" * SQL constants for ").append(model.getEntityName()).append(" entity\n");
        sb.append(" * Generated from repository documentation with intelligent SQL parsing\n");
        sb.append(" * Parameters converted from positional (?) to named (:paramName) based on method signatures\n");
        sb.append(" */\n");
        sb.append("public final class ").append(model.getEntityName()).append("Sql {\n\n");
        
        // Standard CRUD queries
        generateStandardSqlQueries(sb, model);
        
        // Custom method queries from documentation
        generateCustomSqlQueries(sb, model);
        
        sb.append("    private ").append(model.getEntityName()).append("Sql() {\n");
        sb.append("        // Utility class\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        writeToFile(outputDir + "/" + model.getEntityName() + "Sql.java", sb.toString());
        System.out.println("✅ Generated " + model.getEntityName() + "Sql.java with intelligent SQL parsing");
    }
    
    /**
     * Generate standard CRUD SQL queries
     */
    private void generateStandardSqlQueries(StringBuilder sb, DaoGenerationModel model) {
        String tableName = model.getTableMetadata().getTableName();
        
        sb.append("    // Standard CRUD queries\n");
        sb.append("    public static final String FIND_BY_ID = \"\"\"\n");
        sb.append("        SELECT * FROM ").append(tableName).append(" WHERE id = :id\n");
        sb.append("        \"\"\";\n\n");
        
        sb.append("    public static final String FIND_ALL = \"\"\"\n");
        sb.append("        SELECT * FROM ").append(tableName).append(" ORDER BY id\n");
        sb.append("        \"\"\";\n\n");
        
        sb.append("    public static final String COUNT_ALL = \"\"\"\n");
        sb.append("        SELECT COUNT(*) FROM ").append(tableName).append("\n");
        sb.append("        \"\"\";\n\n");
        
        sb.append("    public static final String EXISTS_BY_ID = \"\"\"\n");
        sb.append("        SELECT EXISTS(SELECT 1 FROM ").append(tableName).append(" WHERE id = :id)\n");
        sb.append("        \"\"\";\n\n");
        
        sb.append("    public static final String DELETE_BY_ID = \"\"\"\n");
        sb.append("        DELETE FROM ").append(tableName).append(" WHERE id = :id\n");
        sb.append("        \"\"\";\n\n");
        
        // Generate INSERT and UPDATE queries
        generateInsertUpdateSqlQueries(sb, model);
    }
    
    /**
     * Generate INSERT and UPDATE SQL queries based on POJO fields
     */
    private void generateInsertUpdateSqlQueries(StringBuilder sb, DaoGenerationModel model) {
        String tableName = model.getTableMetadata().getTableName();
        
        // Build column lists for INSERT and UPDATE
        List<String> insertColumns = new ArrayList<>();
        List<String> insertValues = new ArrayList<>();
        List<String> updateSets = new ArrayList<>();
        
        for (FieldInfo field : model.getPojoMetadata().getFields().values()) {
            String columnName = field.getColumnName();
            String fieldName = field.getFieldName();
            
            // Skip ID for INSERT (auto-generated)
            if (!"id".equals(fieldName)) {
                insertColumns.add(columnName);
                
                if (field.getJavaType().contains("JsonNode")) {
                    insertValues.add(":" + fieldName + "::jsonb");
                    updateSets.add(columnName + " = :" + fieldName + "::jsonb");
                } else {
                    insertValues.add(":" + fieldName);
                    updateSets.add(columnName + " = :" + fieldName);
                }
            }
        }
        
        String insertColumnList = String.join(", ", insertColumns);
        String insertValueList = String.join(", ", insertValues);
        String updateSetList = String.join(",\n            ", updateSets);
        
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
    }
    
    /**
     * Generate custom method SQL queries from documentation
     */
    private void generateCustomSqlQueries(StringBuilder sb, DaoGenerationModel model) {
        sb.append("    // Custom method queries from repository documentation\n\n");
        
        for (CustomMethod method : model.getRepositoryDoc().getCustomMethods()) {
            String sqlQuery = method.getSqlQuery();
            
            // Skip dynamic queries
            if (isDynamicQuery(sqlQuery)) {
                sb.append("    // ").append(convertMethodNameToConstant(method.getMethodName()))
                  .append(" - Dynamic query (handled in service layer)\n");
                sb.append("    // Purpose: ").append(method.getPurpose()).append("\n");
                sb.append("    // Implementation: Use CriteriaBuilder or Specification pattern\n\n");
                continue;
            }
            
            // Process executable queries
            List<String> paramNames = extractParameterNamesFromSignature(method.getSignature());
            String processedSql = processAndConvertSql(sqlQuery, paramNames, method.getMethodName(), model.getEntityName());
            
            if (processedSql != null && !processedSql.trim().isEmpty()) {
                String constantName = convertMethodNameToConstant(method.getMethodName());
                
                sb.append("    /**\n");
                sb.append("     * ").append(method.getPurpose()).append("\n");
                if (!paramNames.isEmpty()) {
                    sb.append("     * Parameters: ").append(String.join(", ", paramNames)).append("\n");
                }
                sb.append("     */\n");
                sb.append("    public static final String ").append(constantName).append(" = \"\"\"\n");
                
                // Format SQL with proper indentation
                String[] lines = processedSql.split("\n");
                for (String line : lines) {
                    sb.append("        ").append(line.trim()).append("\n");
                }
                
                sb.append("        \"\"\";\n\n");
            }
        }
    }
    
    /**
     * Extract parameter names from method signature
     */
    private List<String> extractParameterNamesFromSignature(String signature) {
        List<String> paramNames = new ArrayList<>();
        
        // Extract parameters from signature like "Optional<Checklist> findByTaskId(Long taskId)"
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(signature);
        
        if (matcher.find()) {
            String params = matcher.group(1).trim();
            if (!params.isEmpty()) {
                String[] paramPairs = params.split(",");
                for (String paramPair : paramPairs) {
                    String[] parts = paramPair.trim().split("\\s+");
                    if (parts.length >= 2) {
                        // Get the last part as parameter name
                        paramNames.add(parts[parts.length - 1]);
                    }
                }
            }
        }
        
        return paramNames;
    }
    
    /**
     * Check if query is dynamic (should be skipped)
     */
    private boolean isDynamicQuery(String sql) {
        if (sql == null) return true;
        
        String lowerSql = sql.toLowerCase();
        return lowerSql.contains("dynamic sql") || 
               lowerSql.contains("specification criteria") ||
               lowerSql.contains("spring data jpa") ||
               lowerSql.contains("generated by spring") ||
               lowerSql.contains("criteriabuilder");
    }
    
    /**
     * Check if query is executable SQL
     */
    private boolean isExecutableQuery(String sql) {
        if (sql == null || sql.trim().isEmpty()) return false;
        
        String trimmedSql = sql.trim().toUpperCase();
        return trimmedSql.startsWith("SELECT") || 
               trimmedSql.startsWith("UPDATE") || 
               trimmedSql.startsWith("DELETE") || 
               trimmedSql.startsWith("INSERT");
    }
    
    /**
     * Process and convert SQL with intelligent parameter substitution
     * Enhanced to use method-specific business logic rules
     */
    private String processAndConvertSql(String rawSql, List<String> paramNames, String methodName, String entityName) {
        if (rawSql == null || rawSql.trim().isEmpty()) {
            return null;
        }
        
        // Skip dynamic queries
        if (isDynamicQuery(rawSql)) {
            return null;
        }
        
        // Process executable SQL
        if (isExecutableQuery(rawSql)) {
            String cleanSql = cleanSqlQuery(rawSql);
            // Use enhanced method-specific parameter mapping
            return applyMethodSpecificParameterRules(cleanSql, methodName, paramNames, entityName);
        }
        
        return rawSql;
    }
    
    /**
     * Convert positional parameters (?) to named parameters (:paramName)
     * Enhanced with method-specific business logic rules
     */
    private String convertPositionalToNamedParameters(String sql, List<String> paramNames) {
        String result = sql;
        int paramIndex = 0;
        
        // Replace ? with :paramName in order of appearance
        while (result.contains("?") && paramIndex < paramNames.size()) {
            result = result.replaceFirst("\\?", ":" + paramNames.get(paramIndex));
            paramIndex++;
        }
        
        // Handle remaining ? with generic names if any
        int genericIndex = 1;
        while (result.contains("?")) {
            result = result.replaceFirst("\\?", ":param" + genericIndex);
            genericIndex++;
        }
        
        return result;
    }
    
    /**
     * Apply method-specific parameter mapping rules for business logic
     */
    private String applyMethodSpecificParameterRules(String sql, String methodName, List<String> paramNames, String entityName) {
        // Apply checklist-specific rules
        if ("Checklist".equals(entityName)) {
            return applyChecklistSpecificParameterRules(sql, methodName, paramNames);
        }
        
        // Add other entity-specific rules here in the future
        return convertPositionalToNamedParameters(sql, paramNames);
    }
    
    /**
     * Apply checklist-specific parameter mapping rules
     */
    private String applyChecklistSpecificParameterRules(String sql, String methodName, List<String> paramNames) {
        switch (methodName) {
            case "updateChecklistDuringRecall":
                // FIXED: Business rule - both created_by and modified_by use userId
                return sql.replace("created_by = ?", "created_by = :userId")
                         .replace("modified_by = ?", "modified_by = :userId")
                         .replace("WHERE id = ?", "WHERE id = :checklistId");
                         
            case "findAllChecklistIdsForCurrentFacilityAndOrganisationByObjectTypeInData":
                // FIXED: Complex query with correct parameter mapping
                return applyComplexChecklistQueryParameterMapping(sql, paramNames);
                
            default:
                return convertPositionalToNamedParameters(sql, paramNames);
        }
    }
    
    /**
     * Apply complex checklist query parameter mapping
     */
    private String applyComplexChecklistQueryParameterMapping(String sql, List<String> paramNames) {
        // Expected parameters: facilityId, organisationId, objectTypeId, useCaseId, name, archived
        String result = sql;
        
        // Map parameters in the correct order based on business logic
        result = result.replace("(cfm.facilities_id = ? OR", "(cfm.facilities_id = :facilityId OR")
                       .replace("c.organisations_id = ?", "c.organisations_id = :organisationId")
                       .replace("p.data->>'objectTypeId'= ?", "p.data->>'objectTypeId' = :objectTypeId")
                       .replace("c.archived = ?", "c.archived = :archived")
                       .replace("c.use_cases_id = ?", "c.use_cases_id = :useCaseId")
                       .replace("CAST(? as varchar)", "CAST(:name as varchar)")
                       .replace("|| ? ||", "|| :name ||");
        
        return result;
    }
    
    private DaoGenerationModel createDaoModel(TableMetadata tableMetadata, PojoMetadata pojoMetadata, 
                                            RepositoryDocumentation repoDoc, String entityName) {
        DaoGenerationModel model = new DaoGenerationModel();
        model.setEntityName(entityName);
        model.setTableMetadata(tableMetadata);
        model.setPojoMetadata(pojoMetadata);
        model.setRepositoryDoc(repoDoc);
        return model;
    }
    
    /**
     * Generate view classes based on method return types
     */
    private void generateViewClasses(DaoGenerationModel model, String outputDir) throws IOException {
        Set<String> viewTypes = extractViewTypesFromMethods(model);
        
        for (String viewType : viewTypes) {
            generateViewClass(viewType, outputDir);
        }
        
        if (!viewTypes.isEmpty()) {
            System.out.println("✅ Generated " + viewTypes.size() + " view classes: " + String.join(", ", viewTypes));
        }
    }
    
    /**
     * Extract view types from method return types
     */
    private Set<String> extractViewTypesFromMethods(DaoGenerationModel model) {
        Set<String> viewTypes = new HashSet<>();
        
        for (CustomMethod method : model.getRepositoryDoc().getCustomMethods()) {
            String returnType = method.getReturnType();
            
            // Extract view types from return types
            if (returnType.contains("View")) {
                // Extract class name from types like "List<ChecklistView>" or "ChecklistJobLiteView"
                Pattern pattern = Pattern.compile("([A-Z]\\w*View)");
                Matcher matcher = pattern.matcher(returnType);
                while (matcher.find()) {
                    viewTypes.add(matcher.group(1));
                }
            }
        }
        
        return viewTypes;
    }
    
    /**
     * Generate individual view class
     */
    private void generateViewClass(String viewType, String outputDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("package com.example.dwiDaoGenerator.checklist.generated;\n\n");
        sb.append("/**\n");
        sb.append(" * Lightweight view for ").append(viewType.replace("View", "")).append(" projections\n");
        sb.append(" * Auto-generated based on method return types\n");
        sb.append(" */\n");
        sb.append("public class ").append(viewType).append(" {\n\n");
        
        // Generate fields based on view type
        generateViewFields(sb, viewType);
        
        // Generate constructors
        sb.append("    public ").append(viewType).append("() {}\n\n");
        generateViewConstructor(sb, viewType);
        
        // Generate getters and setters
        generateViewGettersSetters(sb, viewType);
        
        // Generate toString
        generateViewToString(sb, viewType);
        
        sb.append("}\n");
        
        writeToFile(outputDir + "/" + viewType + ".java", sb.toString());
    }
    
    /**
     * Generate fields for view class based on type
     */
    private void generateViewFields(StringBuilder sb, String viewType) {
        switch (viewType) {
            case "ChecklistView":
                sb.append("    private Long id;\n");
                sb.append("    private String code;\n");
                sb.append("    private String name;\n");
                sb.append("    private String colorCode;\n\n");
                break;
                
            case "ChecklistJobLiteView":
                sb.append("    private Long id;\n");
                sb.append("    private String name;\n");
                sb.append("    private String code;\n\n");
                break;
                
            case "JobLogMigrationChecklistView":
                sb.append("    private Long id;\n");
                sb.append("    private String name;\n");
                sb.append("    private String code;\n");
                sb.append("    private String state;\n\n");
                break;
                
            default:
                // Generic view with common fields
                sb.append("    private Long id;\n");
                sb.append("    private String name;\n\n");
                break;
        }
    }
    
    /**
     * Generate parameterized constructor for view class
     */
    private void generateViewConstructor(StringBuilder sb, String viewType) {
        switch (viewType) {
            case "ChecklistView":
                sb.append("    public ").append(viewType).append("(Long id, String code, String name, String colorCode) {\n");
                sb.append("        this.id = id;\n");
                sb.append("        this.code = code;\n");
                sb.append("        this.name = name;\n");
                sb.append("        this.colorCode = colorCode;\n");
                sb.append("    }\n\n");
                break;
                
            case "ChecklistJobLiteView":
                sb.append("    public ").append(viewType).append("(Long id, String name, String code) {\n");
                sb.append("        this.id = id;\n");
                sb.append("        this.name = name;\n");
                sb.append("        this.code = code;\n");
                sb.append("    }\n\n");
                break;
                
            case "JobLogMigrationChecklistView":
                sb.append("    public ").append(viewType).append("(Long id, String name, String code, String state) {\n");
                sb.append("        this.id = id;\n");
                sb.append("        this.name = name;\n");
                sb.append("        this.code = code;\n");
                sb.append("        this.state = state;\n");
                sb.append("    }\n\n");
                break;
                
            default:
                sb.append("    public ").append(viewType).append("(Long id, String name) {\n");
                sb.append("        this.id = id;\n");
                sb.append("        this.name = name;\n");
                sb.append("    }\n\n");
                break;
        }
    }
    
    /**
     * Generate getters and setters for view class
     */
    private void generateViewGettersSetters(StringBuilder sb, String viewType) {
        switch (viewType) {
            case "ChecklistView":
                generateGetterSetter(sb, "Long", "id");
                generateGetterSetter(sb, "String", "code");
                generateGetterSetter(sb, "String", "name");
                generateGetterSetter(sb, "String", "colorCode");
                break;
                
            case "ChecklistJobLiteView":
                generateGetterSetter(sb, "Long", "id");
                generateGetterSetter(sb, "String", "name");
                generateGetterSetter(sb, "String", "code");
                break;
                
            case "JobLogMigrationChecklistView":
                generateGetterSetter(sb, "Long", "id");
                generateGetterSetter(sb, "String", "name");
                generateGetterSetter(sb, "String", "code");
                generateGetterSetter(sb, "String", "state");
                break;
                
            default:
                generateGetterSetter(sb, "Long", "id");
                generateGetterSetter(sb, "String", "name");
                break;
        }
    }
    
    /**
     * Generate getter and setter for a field
     */
    private void generateGetterSetter(StringBuilder sb, String type, String fieldName) {
        String capitalizedField = capitalize(fieldName);
        
        // Getter
        sb.append("    public ").append(type).append(" get").append(capitalizedField).append("() {\n");
        sb.append("        return ").append(fieldName).append(";\n");
        sb.append("    }\n\n");
        
        // Setter
        sb.append("    public void set").append(capitalizedField).append("(").append(type).append(" ").append(fieldName).append(") {\n");
        sb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate toString method for view class
     */
    private void generateViewToString(StringBuilder sb, String viewType) {
        sb.append("    @Override\n");
        sb.append("    public String toString() {\n");
        sb.append("        return \"").append(viewType).append("{\" +\n");
        
        switch (viewType) {
            case "ChecklistView":
                sb.append("                \"id=\" + id +\n");
                sb.append("                \", code='\" + code + '\\'' +\n");
                sb.append("                \", name='\" + name + '\\'' +\n");
                sb.append("                \", colorCode='\" + colorCode + '\\'' +\n");
                break;
                
            case "ChecklistJobLiteView":
                sb.append("                \"id=\" + id +\n");
                sb.append("                \", name='\" + name + '\\'' +\n");
                sb.append("                \", code='\" + code + '\\'' +\n");
                break;
                
            case "JobLogMigrationChecklistView":
                sb.append("                \"id=\" + id +\n");
                sb.append("                \", name='\" + name + '\\'' +\n");
                sb.append("                \", code='\" + code + '\\'' +\n");
                sb.append("                \", state='\" + state + '\\'' +\n");
                break;
                
            default:
                sb.append("                \"id=\" + id +\n");
                sb.append("                \", name='\" + name + '\\'' +\n");
                break;
        }
        
        sb.append("                '}';\n");
        sb.append("    }\n");
    }
    
    /**
     * Generate state enums for the entity
     */
    private void generateStateEnums(DaoGenerationModel model, String outputDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("package com.example.dwiDaoGenerator.").append(model.getEntityName().toLowerCase()).append(".generated;\n\n");
        sb.append("/**\n");
        sb.append(" * State enums for various entities\n");
        sb.append(" * Auto-generated to support DAO operations\n");
        sb.append(" */\n");
        sb.append("public class State {\n\n");
        
        // Generate entity-specific state enum
        String entityName = model.getEntityName();
        sb.append("    /**\n");
        sb.append("     * ").append(entityName).append(" state enumeration\n");
        sb.append("     */\n");
        sb.append("    public enum ").append(entityName).append(" {\n");
        
        // Generate states based on entity type
        generateEntityStates(sb, entityName);
        
        sb.append("    }\n");
        
        // Add other common state enums
        generateCommonStateEnums(sb);
        
        sb.append("}\n");
        
        writeToFile(outputDir + "/State.java", sb.toString());
        System.out.println("✅ Generated State.java with " + entityName + " states");
    }
    
    /**
     * Generate states for specific entity
     */
    private void generateEntityStates(StringBuilder sb, String entityName) {
        switch (entityName) {
            case "Checklist":
                sb.append("        BEING_BUILT,\n");
                sb.append("        PUBLISHED,\n");
                sb.append("        ARCHIVED,\n");
                sb.append("        DRAFT,\n");
                sb.append("        UNDER_REVIEW,\n");
                sb.append("        APPROVED,\n");
                sb.append("        REJECTED,\n");
                sb.append("        RECALLED\n");
                break;
                
            case "Task":
                sb.append("        PENDING,\n");
                sb.append("        IN_PROGRESS,\n");
                sb.append("        COMPLETED,\n");
                sb.append("        SKIPPED,\n");
                sb.append("        FAILED\n");
                break;
                
            case "Job":
                sb.append("        CREATED,\n");
                sb.append("        ASSIGNED,\n");
                sb.append("        IN_PROGRESS,\n");
                sb.append("        COMPLETED,\n");
                sb.append("        CANCELLED,\n");
                sb.append("        FAILED\n");
                break;
                
            default:
                // Generic states
                sb.append("        ACTIVE,\n");
                sb.append("        INACTIVE,\n");
                sb.append("        PENDING,\n");
                sb.append("        COMPLETED\n");
                break;
        }
    }
    
    /**
     * Generate common state enums for other entities
     */
    private void generateCommonStateEnums(StringBuilder sb) {
        sb.append("\n    /**\n");
        sb.append("     * Task state enumeration\n");
        sb.append("     */\n");
        sb.append("    public enum Task {\n");
        sb.append("        PENDING,\n");
        sb.append("        IN_PROGRESS,\n");
        sb.append("        COMPLETED,\n");
        sb.append("        SKIPPED,\n");
        sb.append("        FAILED\n");
        sb.append("    }\n");
        
        sb.append("\n    /**\n");
        sb.append("     * Job state enumeration\n");
        sb.append("     */\n");
        sb.append("    public enum Job {\n");
        sb.append("        CREATED,\n");
        sb.append("        ASSIGNED,\n");
        sb.append("        IN_PROGRESS,\n");
        sb.append("        COMPLETED,\n");
        sb.append("        CANCELLED,\n");
        sb.append("        FAILED\n");
        sb.append("    }\n");
    }
    
    /**
     * Generate Spring Data placeholder types
     */
    private void generateSpringDataTypes(DaoGenerationModel model, String outputDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("package com.example.dwiDaoGenerator.").append(model.getEntityName().toLowerCase()).append(".generated;\n\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.function.Function;\n\n");
        
        sb.append("/**\n");
        sb.append(" * Placeholder types for Spring Data compatibility\n");
        sb.append(" * Auto-generated simplified versions to avoid Spring Data dependencies\n");
        sb.append(" */\n");
        sb.append("public class SpringDataTypes {\n\n");
        
        // Generate Page interface
        generatePageInterface(sb);
        
        // Generate Pageable interface
        generatePageableInterface(sb);
        
        // Generate Sort interface
        generateSortInterface(sb);
        
        // Generate Specification interface
        generateSpecificationInterface(sb);
        
        // Generate implementation classes
        generateSpringDataImplementations(sb);
        
        sb.append("}\n");
        
        writeToFile(outputDir + "/SpringDataTypes.java", sb.toString());
        System.out.println("✅ Generated SpringDataTypes.java with placeholder implementations");
    }
    
    /**
     * Generate Page interface
     */
    private void generatePageInterface(StringBuilder sb) {
        sb.append("    /**\n");
        sb.append("     * Simplified Page interface\n");
        sb.append("     */\n");
        sb.append("    public interface Page<T> extends Iterable<T> {\n");
        sb.append("        int getTotalPages();\n");
        sb.append("        long getTotalElements();\n");
        sb.append("        boolean hasNext();\n");
        sb.append("        boolean isFirst();\n");
        sb.append("        boolean isLast();\n");
        sb.append("        boolean hasContent();\n");
        sb.append("        List<T> getContent();\n");
        sb.append("        int getNumber();\n");
        sb.append("        int getSize();\n");
        sb.append("        int getNumberOfElements();\n");
        sb.append("        boolean hasPrevious();\n");
        sb.append("        Pageable getPageable();\n");
        sb.append("        Pageable nextPageable();\n");
        sb.append("        Pageable previousPageable();\n");
        sb.append("        <U> Page<U> map(Function<? super T, ? extends U> converter);\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate Pageable interface
     */
    private void generatePageableInterface(StringBuilder sb) {
        sb.append("    /**\n");
        sb.append("     * Simplified Pageable interface\n");
        sb.append("     */\n");
        sb.append("    public interface Pageable {\n");
        sb.append("        int getPageNumber();\n");
        sb.append("        int getPageSize();\n");
        sb.append("        long getOffset();\n");
        sb.append("        Sort getSort();\n");
        sb.append("        Pageable next();\n");
        sb.append("        Pageable previousOrFirst();\n");
        sb.append("        Pageable first();\n");
        sb.append("        boolean hasPrevious();\n");
        sb.append("        boolean isPaged();\n");
        sb.append("        boolean isUnpaged();\n");
        sb.append("        \n");
        sb.append("        static Pageable unpaged() {\n");
        sb.append("            return new UnpagedPageable();\n");
        sb.append("        }\n");
        sb.append("        \n");
        sb.append("        static Pageable ofSize(int pageSize) {\n");
        sb.append("            return new SimplePageable(0, pageSize, Sort.unsorted());\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate Sort interface
     */
    private void generateSortInterface(StringBuilder sb) {
        sb.append("    /**\n");
        sb.append("     * Simplified Sort interface\n");
        sb.append("     */\n");
        sb.append("    public interface Sort extends Iterable<Sort.Order> {\n");
        sb.append("        Sort and(Sort sort);\n");
        sb.append("        Sort ascending();\n");
        sb.append("        Sort descending();\n");
        sb.append("        boolean isEmpty();\n");
        sb.append("        boolean isSorted();\n");
        sb.append("        boolean isUnsorted();\n");
        sb.append("        \n");
        sb.append("        static Sort by(String... properties) {\n");
        sb.append("            return new SimpleSort(properties);\n");
        sb.append("        }\n");
        sb.append("        \n");
        sb.append("        static Sort unsorted() {\n");
        sb.append("            return new UnsortedSort();\n");
        sb.append("        }\n");
        sb.append("        \n");
        sb.append("        /**\n");
        sb.append("         * Sort order\n");
        sb.append("         */\n");
        sb.append("        public static class Order {\n");
        sb.append("            private final Direction direction;\n");
        sb.append("            private final String property;\n");
        sb.append("            \n");
        sb.append("            public Order(Direction direction, String property) {\n");
        sb.append("                this.direction = direction;\n");
        sb.append("                this.property = property;\n");
        sb.append("            }\n");
        sb.append("            \n");
        sb.append("            public Direction getDirection() { return direction; }\n");
        sb.append("            public String getProperty() { return property; }\n");
        sb.append("            \n");
        sb.append("            public static Order asc(String property) {\n");
        sb.append("                return new Order(Direction.ASC, property);\n");
        sb.append("            }\n");
        sb.append("            \n");
        sb.append("            public static Order desc(String property) {\n");
        sb.append("                return new Order(Direction.DESC, property);\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("        \n");
        sb.append("        /**\n");
        sb.append("         * Sort direction\n");
        sb.append("         */\n");
        sb.append("        public enum Direction {\n");
        sb.append("            ASC, DESC\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate Specification interface
     */
    private void generateSpecificationInterface(StringBuilder sb) {
        sb.append("    /**\n");
        sb.append("     * Simplified Specification interface\n");
        sb.append("     */\n");
        sb.append("    public interface Specification<T> {\n");
        sb.append("        // Placeholder for JPA Criteria API\n");
        sb.append("        // In real implementation, this would work with CriteriaBuilder\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Generate implementation classes for Spring Data types
     */
    private void generateSpringDataImplementations(StringBuilder sb) {
        sb.append("    // Implementation classes (simplified)\n\n");
        
        // UnpagedPageable
        sb.append("    private static class UnpagedPageable implements Pageable {\n");
        sb.append("        @Override public int getPageNumber() { return 0; }\n");
        sb.append("        @Override public int getPageSize() { return Integer.MAX_VALUE; }\n");
        sb.append("        @Override public long getOffset() { return 0; }\n");
        sb.append("        @Override public Sort getSort() { return Sort.unsorted(); }\n");
        sb.append("        @Override public Pageable next() { return this; }\n");
        sb.append("        @Override public Pageable previousOrFirst() { return this; }\n");
        sb.append("        @Override public Pageable first() { return this; }\n");
        sb.append("        @Override public boolean hasPrevious() { return false; }\n");
        sb.append("        @Override public boolean isPaged() { return false; }\n");
        sb.append("        @Override public boolean isUnpaged() { return true; }\n");
        sb.append("    }\n\n");
        
        // SimplePageable
        sb.append("    private static class SimplePageable implements Pageable {\n");
        sb.append("        private final int page; private final int size; private final Sort sort;\n");
        sb.append("        public SimplePageable(int page, int size, Sort sort) { this.page = page; this.size = size; this.sort = sort; }\n");
        sb.append("        @Override public int getPageNumber() { return page; }\n");
        sb.append("        @Override public int getPageSize() { return size; }\n");
        sb.append("        @Override public long getOffset() { return (long) page * size; }\n");
        sb.append("        @Override public Sort getSort() { return sort; }\n");
        sb.append("        @Override public Pageable next() { return new SimplePageable(page + 1, size, sort); }\n");
        sb.append("        @Override public Pageable previousOrFirst() { return page == 0 ? this : new SimplePageable(page - 1, size, sort); }\n");
        sb.append("        @Override public Pageable first() { return new SimplePageable(0, size, sort); }\n");
        sb.append("        @Override public boolean hasPrevious() { return page > 0; }\n");
        sb.append("        @Override public boolean isPaged() { return true; }\n");
        sb.append("        @Override public boolean isUnpaged() { return false; }\n");
        sb.append("    }\n\n");
        
        // SimpleSort and UnsortedSort
        sb.append("    private static class SimpleSort implements Sort {\n");
        sb.append("        private final String[] properties;\n");
        sb.append("        public SimpleSort(String... properties) { this.properties = properties; }\n");
        sb.append("        @Override public Sort and(Sort sort) { return this; }\n");
        sb.append("        @Override public Sort ascending() { return this; }\n");
        sb.append("        @Override public Sort descending() { return this; }\n");
        sb.append("        @Override public boolean isEmpty() { return properties.length == 0; }\n");
        sb.append("        @Override public boolean isSorted() { return properties.length > 0; }\n");
        sb.append("        @Override public boolean isUnsorted() { return properties.length == 0; }\n");
        sb.append("        @Override public java.util.Iterator<Order> iterator() {\n");
        sb.append("            return java.util.Arrays.stream(properties).map(prop -> Order.asc(prop)).iterator();\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
        
        sb.append("    private static class UnsortedSort implements Sort {\n");
        sb.append("        @Override public Sort and(Sort sort) { return sort; }\n");
        sb.append("        @Override public Sort ascending() { return this; }\n");
        sb.append("        @Override public Sort descending() { return this; }\n");
        sb.append("        @Override public boolean isEmpty() { return true; }\n");
        sb.append("        @Override public boolean isSorted() { return false; }\n");
        sb.append("        @Override public boolean isUnsorted() { return true; }\n");
        sb.append("        @Override public java.util.Iterator<Order> iterator() {\n");
        sb.append("            return java.util.Collections.emptyIterator();\n");
        sb.append("        }\n");
        sb.append("    }\n");
    }

    /**
     * Check if SpringDataTypes imports are needed
     */
    private boolean needsSpringDataImports(DaoGenerationModel model) {
        for (CustomMethod method : model.getRepositoryDoc().getCustomMethods()) {
            String signature = method.getSignature();
            if (signature.contains("Page<") || signature.contains("Pageable") || 
                signature.contains("Sort") || signature.contains("Specification")) {
                return true;
            }
        }
        return false;
    }

    private void writeToFile(String fileName, String content) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        }
        System.out.println("Generated: " + fileName);
    }
    
    // Model classes
    static class TableMetadata {
        private String tableName;
        private List<ColumnInfo> columns = new ArrayList<>();
        private Set<String> primaryKeys = new HashSet<>();
        private Map<String, String> foreignKeys = new HashMap<>();
        
        // Getters and setters
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        public List<ColumnInfo> getColumns() { return columns; }
        public void setColumns(List<ColumnInfo> columns) { this.columns = columns; }
        public Set<String> getPrimaryKeys() { return primaryKeys; }
        public void setPrimaryKeys(Set<String> primaryKeys) { this.primaryKeys = primaryKeys; }
        public Map<String, String> getForeignKeys() { return foreignKeys; }
        public void setForeignKeys(Map<String, String> foreignKeys) { this.foreignKeys = foreignKeys; }
    }
    
    static class ColumnInfo {
        private String name;
        private String type;
        private int size;
        private boolean nullable;
        private String defaultValue;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public boolean isNullable() { return nullable; }
        public void setNullable(boolean nullable) { this.nullable = nullable; }
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    }
    
    static class PojoMetadata {
        private String className;
        private String packageName;
        private Map<String, FieldInfo> fields = new HashMap<>();
        
        // Getters and setters
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public String getPackageName() { return packageName; }
        public void setPackageName(String packageName) { this.packageName = packageName; }
        public Map<String, FieldInfo> getFields() { return fields; }
        public void setFields(Map<String, FieldInfo> fields) { this.fields = fields; }
    }
    
    static class FieldInfo {
        private String fieldName;
        private String javaType;
        private String columnName;
        
        // Getters and setters
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public String getJavaType() { return javaType; }
        public void setJavaType(String javaType) { this.javaType = javaType; }
        public String getColumnName() { return columnName; }
        public void setColumnName(String columnName) { this.columnName = columnName; }
    }
    
    static class RepositoryDocumentation {
        private String documentPath;
        private List<CustomMethod> customMethods = new ArrayList<>();
        private String transactionRequirements;
        
        // Getters and setters
        public String getDocumentPath() { return documentPath; }
        public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }
        public List<CustomMethod> getCustomMethods() { return customMethods; }
        public void setCustomMethods(List<CustomMethod> customMethods) { this.customMethods = customMethods; }
        public String getTransactionRequirements() { return transactionRequirements; }
        public void setTransactionRequirements(String transactionRequirements) { this.transactionRequirements = transactionRequirements; }
    }
    
    static class CustomMethod {
        private String methodName;
        private String signature;
        private String sqlQuery;
        private List<MethodParameter> parameters = new ArrayList<>();
        private String returnType;
        private boolean transactionRequired;
        private String purpose;
        
        // Getters and setters
        public String getMethodName() { return methodName; }
        public void setMethodName(String methodName) { this.methodName = methodName; }
        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
        public String getSqlQuery() { return sqlQuery; }
        public void setSqlQuery(String sqlQuery) { this.sqlQuery = sqlQuery; }
        public List<MethodParameter> getParameters() { return parameters; }
        public void setParameters(List<MethodParameter> parameters) { this.parameters = parameters; }
        public String getReturnType() { return returnType; }
        public void setReturnType(String returnType) { this.returnType = returnType; }
        public boolean isTransactionRequired() { return transactionRequired; }
        public void setTransactionRequired(boolean transactionRequired) { this.transactionRequired = transactionRequired; }
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }
    
    static class MethodParameter {
        private String name;
        private String type;
        private String description;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    static class DaoGenerationModel {
        private String entityName;
        private TableMetadata tableMetadata;
        private PojoMetadata pojoMetadata;
        private RepositoryDocumentation repositoryDoc;
        
        // Getters and setters
        public String getEntityName() { return entityName; }
        public void setEntityName(String entityName) { this.entityName = entityName; }
        public TableMetadata getTableMetadata() { return tableMetadata; }
        public void setTableMetadata(TableMetadata tableMetadata) { this.tableMetadata = tableMetadata; }
        public PojoMetadata getPojoMetadata() { return pojoMetadata; }
        public void setPojoMetadata(PojoMetadata pojoMetadata) { this.pojoMetadata = pojoMetadata; }
        public RepositoryDocumentation getRepositoryDoc() { return repositoryDoc; }
        public void setRepositoryDoc(RepositoryDocumentation repositoryDoc) { this.repositoryDoc = repositoryDoc; }
    }
}
