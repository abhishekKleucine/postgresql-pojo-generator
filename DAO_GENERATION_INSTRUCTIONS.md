# 🚀 Repository-Driven DAO Generator Instructions

## 📋 **Overview**
This guide provides step-by-step instructions for generating complete DAO packages for any entity using the enhanced RepositoryDrivenDaoGenerator.

---

## 🎯 **What Gets Generated (9 Files)**

### **Core DAO Components:**
1. **EntityDao.java** - Interface with all CRUD + custom methods
2. **JdbcEntityDao.java** - Complete implementation with Spring Boot integration
3. **EntityRowMapper.java** - Enhanced field mapping for all Java types
4. **EntitySql.java** - All SQL constants with intelligent parameter mapping

### **Supporting Components (Auto-Generated):**
5. **EntityView.java** - Lightweight projections (auto-detected from return types)
6. **EntityJobLiteView.java** - Minimal views for job operations
7. **JobLogMigrationEntityView.java** - Legacy compatibility views
8. **State.java** - Entity-specific state enums
9. **SpringDataTypes.java** - Placeholder types for Spring Data compatibility

---

## 🔧 **Prerequisites**

### **1. Database Setup**
```bash
# Ensure PostgreSQL is running and accessible
psql -h localhost -U postgres -d qa_ -c "\dt your_table_name"
```

### **2. Required Files**
- ✅ **POJO exists:** `src/main/java/com/example/pojogenerator/pojos/YourEntity.java`
- ✅ **Repository doc exists:** `repository_documents/repository_docs/YourEntityRepositorydoc.md`
- ✅ **Database table exists:** Verify table is accessible

### **3. Project Compilation**
```bash
cd /home/leucine/eclipse-workspace/postgresConnect
mvn clean compile
```

---

## 🚀 **Generation Steps**

### **Step 1: Update Generator Configuration**
Edit `RepositoryDrivenDaoGenerator.java` main method:

```java
public static void main(String[] args) {
    System.out.println("🚀 Starting Repository-Driven DAO Generator...");
    
    RepositoryDrivenDaoGenerator generator = new RepositoryDrivenDaoGenerator();
    
    // CHANGE THESE VALUES FOR YOUR ENTITY:
    generator.generateRepositoryDrivenDao(
        "your_table_name",           // Database table name (e.g., "tasks", "jobs", "users")
        "YourEntity",                // Entity class name (e.g., "Task", "Job", "User")
        "YourEntityRepositorydoc.md" // Repository doc file (e.g., "TaskRepositorydoc.md")
    );
    
    System.out.println("✅ Repository-Driven DAO generation completed!");
}
```

### **Step 2: Run the Generator**
```bash
cd /home/leucine/eclipse-workspace/postgresConnect
java -cp "target/classes:$(find ~/.m2/repository -name "*.jar" | tr '\n' ':')" \
  com.example.dwiDaoGenerator.RepositoryDrivenDaoGenerator
```

### **Step 3: Verify Generated Files**
Check that all 9 files were created:
```bash
ls -la src/main/java/com/example/dwiDaoGenerator/yourentity/generated/
```

Expected output:
```
YourEntityDao.java
JdbcYourEntityDao.java
YourEntityRowMapper.java
YourEntitySql.java
YourEntityView.java
YourEntityJobLiteView.java
JobLogMigrationYourEntityView.java
State.java
SpringDataTypes.java
```

---

## 🚨 **Critical Compilation Issues & Fixes**

### **🔥 CRITICAL: Row Mapper Type Detection Order**
**Problem:** View types with substring matches cause wrong row mapper generation
**Example Error:**
```
incompatible types: inference variable T has incompatible bounds
    equality constraints: java.lang.String
    lower bounds: JobLogMigrationChecklistView,java.lang.Object
```

**Root Cause:** `JobLogMigrationChecklistView` contains `ChecklistView` substring, causing wrong pattern match

**✅ SOLUTION:** Fix `extractRowMapperType()` method order (MOST SPECIFIC FIRST):
```java
private String extractRowMapperType(String returnType) {
    // ORDER MATTERS! Most specific patterns first
    if (returnType.contains("JobLogMigrationChecklistView")) {
        return "(rs, rowNum) -> new JobLogMigrationChecklistView(rs.getLong(\"id\"), rs.getString(\"name\"), rs.getString(\"code\"), rs.getString(\"state\"))";
    } else if (returnType.contains("ChecklistJobLiteView")) {
        return "(rs, rowNum) -> new ChecklistJobLiteView(rs.getLong(\"id\"), rs.getString(\"name\"), rs.getString(\"code\"))";
    } else if (returnType.contains("ChecklistView")) {
        return "(rs, rowNum) -> new ChecklistView(rs.getLong(\"id\"), rs.getString(\"code\"), rs.getString(\"name\"), rs.getString(\"colorCode\"))";
    }
    // ... other patterns
}
```

### **🔥 CRITICAL: Spring Data Types Import Issues**
**Problem:** Missing SpringDataTypes imports when methods use Page, Pageable, Sort, Specification
**Example Error:**
```
cannot find symbol: class Page
cannot find symbol: class Pageable
```

**✅ SOLUTION:** Add smart import detection:
```java
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
```

### **🔥 CRITICAL: View Type vs Single Value Execution**
**Problem:** Methods returning view types use wrong execution pattern
**Example Error:**
```
return jdbcTemplate.queryForObject(sql, params, String.class); // WRONG for view types
```

**✅ SOLUTION:** Enhanced single value execution detection:
```java
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
```

---

## ⚠️ **Common Issues & Solutions**

### **Issue 1: Parameter Mapping Errors**
**Symptoms:** SQL parameters don't match method signatures
**Solution:** Add entity-specific parameter rules

```java
// In RepositoryDrivenDaoGenerator.java, add to applyMethodSpecificParameterRules():
case "YourEntity":
    return applyYourEntitySpecificParameterRules(sql, methodName, paramNames);
```

Then implement the specific rules:
```java
private String applyYourEntitySpecificParameterRules(String sql, String methodName, List<String> paramNames) {
    switch (methodName) {
        case "updateYourEntityDuringSpecialOperation":
            // Apply business logic for parameter mapping
            return sql.replace("field1 = ?", "field1 = :correctParam")
                     .replace("field2 = ?", "field2 = :anotherParam");
        default:
            return convertPositionalToNamedParameters(sql, paramNames);
    }
}
```

### **Issue 2: Missing View Classes**
**Symptoms:** Compilation errors for undefined view types
**Solution:** Check method return types in repository documentation

Ensure your repository doc has methods like:
```yaml
Returns: List<YourEntityView> (list of entity projections)
Returns: YourEntityJobLiteView (minimal entity info)
```

### **Issue 3: Database Connection Failures**
**Symptoms:** SQLException during metadata extraction
**Solution:** Verify database configuration

```java
// Check these constants in RepositoryDrivenDaoGenerator.java:
private static final String DB_URL = "jdbc:postgresql://localhost:5432/qa_";
private static final String DB_USER = "postgres";
private static final String DB_PASS = "postgres";
```

### **Issue 4: POJO Field Mapping Issues**
**Symptoms:** Missing fields in row mapper
**Solution:** Verify POJO structure matches database columns

```bash
# Check POJO fields:
grep "private.*;" src/main/java/com/example/pojogenerator/pojos/YourEntity.java

# Check database columns:
psql -h localhost -U postgres -d qa_ -c "\d your_table_name"
```

---

## 📝 **Entity-Specific Customization Guide**

### **For Task Entity:**
```java
// Expected states: PENDING, IN_PROGRESS, COMPLETED, SKIPPED, FAILED
// Common methods: findByStatus, updateTaskProgress, assignTask
// Special handling: Task dependencies, execution timers
```

### **For Job Entity:**
```java
// Expected states: CREATED, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED
// Common methods: findByAssignee, updateJobStatus, scheduleJob
// Special handling: Job annotations, media mappings
```

### **For User Entity:**
```java
// Expected states: ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION
// Common methods: findByRole, updateUserPermissions, authenticateUser
// Special handling: Password policies, group memberships
```

---

## 🔍 **Validation Checklist**

### **Before Generation:**
- [ ] Database table exists and is accessible
- [ ] POJO class exists with proper field mappings
- [ ] Repository documentation follows standard YAML format
- [ ] All custom method signatures are properly documented

### **After Generation:**
- [ ] All 9 files generated successfully
- [ ] No compilation errors in generated code
- [ ] All method signatures match documentation
- [ ] Parameter mapping is correct for complex methods
- [ ] View classes have appropriate fields
- [ ] State enums include all business states

### **Testing:**
- [ ] Generated DAO compiles without errors
- [ ] Database connections work at runtime
- [ ] All CRUD operations function correctly
- [ ] Custom methods execute without SQL errors
- [ ] Transaction annotations are properly applied

---

## 🚨 **Critical Success Factors**

### **1. Parameter Mapping Accuracy**
- **Always verify** that SQL parameters match method signatures
- **Apply business logic rules** for complex operations
- **Test parameter substitution** with actual data

### **2. Complete Documentation**
- **Document all return types** accurately in repository docs
- **Include all method parameters** with correct types
- **Specify transaction requirements** for write operations

### **3. Database Schema Alignment**
- **Ensure POJO fields** match database columns exactly
- **Verify foreign key relationships** are properly mapped
- **Check data types** are compatible between Java and PostgreSQL

### **4. View Class Completeness**
- **All referenced view types** must be auto-generated
- **Field mappings** must match SQL projection queries
- **Naming conventions** should be consistent across entities

---

## 🔧 **Advanced Customization Patterns**

### **Pattern 1: Complex View Type Hierarchies**
When you have multiple view types with overlapping names:

```java
// WRONG ORDER (will cause substring matching issues):
if (returnType.contains("ChecklistView")) { ... }
if (returnType.contains("JobLogMigrationChecklistView")) { ... }

// CORRECT ORDER (most specific first):
if (returnType.contains("JobLogMigrationChecklistView")) { ... }
if (returnType.contains("ChecklistJobLiteView")) { ... }
if (returnType.contains("ChecklistView")) { ... }
```

### **Pattern 2: Business Logic Parameter Substitution**
Real example from Checklist entity:

```java
private String applyChecklistSpecificParameterRules(String sql, String methodName, List<String> paramNames) {
    switch (methodName) {
        case "updateChecklistDuringRecall":
            // Business rule: both created_by and modified_by use userId
            return sql.replace("created_by = ?", "created_by = :userId")
                     .replace("modified_by = ?", "modified_by = :userId")
                     .replace("WHERE id = ?", "WHERE id = :checklistId");
                     
        case "findAllChecklistIdsForCurrentFacilityAndOrganisationByObjectTypeInData":
            // Complex multi-parameter mapping with business logic
            return sql.replace("(cfm.facilities_id = ? OR", "(cfm.facilities_id = :facilityId OR")
                     .replace("c.organisations_id = ?", "c.organisations_id = :organisationId")
                     .replace("p.data->>'objectTypeId'= ?", "p.data->>'objectTypeId' = :objectTypeId")
                     .replace("c.archived = ?", "c.archived = :archived")
                     .replace("c.use_cases_id = ?", "c.use_cases_id = :useCaseId")
                     .replace("CAST(? as varchar)", "CAST(:name as varchar)")
                     .replace("|| ? ||", "|| :name ||");
                     
        default:
            return convertPositionalToNamedParameters(sql, paramNames);
    }
}
```

### **Pattern 3: Multi-Entity Relationship Handling**
For entities with complex foreign key relationships:

```java
// Add to generateViewFields() method for entity-specific view fields
switch (viewType) {
    case "TaskExecutionView":
        sb.append("    private Long taskId;\n");
        sb.append("    private Long executionId;\n");
        sb.append("    private String taskName;\n");
        sb.append("    private String executionStatus;\n");
        sb.append("    private Long assigneeId;\n");
        sb.append("    private String assigneeName;\n\n");
        break;
        
    case "JobAnnotationView":
        sb.append("    private Long jobId;\n");
        sb.append("    private Long annotationId;\n");
        sb.append("    private String annotationType;\n");
        sb.append("    private String annotationText;\n");
        sb.append("    private List<String> mediaUrls;\n\n");
        break;
}
```

### **Pattern 4: Dynamic Query Handling**
For methods that require dynamic SQL generation:

```java
private void generateDynamicMethodImplementation(StringBuilder sb, CustomMethod method, DaoGenerationModel model) {
    String methodName = method.getMethodName();
    
    switch (methodName) {
        case "findAllWithFilters":
            sb.append("        // Dynamic query with filters\n");
            sb.append("        StringBuilder sqlBuilder = new StringBuilder(").append(model.getEntityName()).append("Sql.FIND_ALL_BASE);\n");
            sb.append("        MapSqlParameterSource params = new MapSqlParameterSource();\n");
            sb.append("        \n");
            sb.append("        // Add dynamic WHERE clauses based on non-null parameters\n");
            sb.append("        if (status != null) {\n");
            sb.append("            sqlBuilder.append(\" AND status = :status\");\n");
            sb.append("            params.addValue(\"status\", status);\n");
            sb.append("        }\n");
            sb.append("        \n");
            sb.append("        return jdbcTemplate.query(sqlBuilder.toString(), params, rowMapper);\n");
            break;
            
        default:
            sb.append("        throw new UnsupportedOperationException(\"Dynamic method not implemented: ").append(methodName).append("\");\n");
            break;
    }
}
```

---

## 📞 **Support & Troubleshooting**

### **🔍 Compilation Error Patterns & Solutions**

#### **Error Pattern 1: Type Inference Issues**
```
incompatible types: inference variable T has incompatible bounds
    equality constraints: java.lang.String
    lower bounds: SomeView,java.lang.Object
```
**Solution:** Check `extractRowMapperType()` method order - ensure most specific view types are checked first.

#### **Error Pattern 2: Missing Constructor**
```
constructor SomeView in class SomeView cannot be applied to given types
```
**Solution:** Verify view class constructor matches the row mapper lambda parameters.

#### **Error Pattern 3: Import Resolution**
```
cannot find symbol: class Page
cannot find symbol: class Specification
```
**Solution:** Ensure `needsSpringDataImports()` returns true and SpringDataTypes.java is generated.

### **🐛 Debug Mode:**
Add comprehensive debug logging:
```java
System.out.println("🔍 Processing method: " + methodName);
System.out.println("📊 SQL: " + sqlQuery);
System.out.println("🎯 Parameters: " + paramNames);
System.out.println("🔄 Return type: " + returnType);
System.out.println("🎭 Row mapper: " + extractRowMapperType(returnType));
```

### **🛠️ Common Commands:**
```bash
# Full clean and recompile:
mvn clean compile

# Check specific compilation errors:
mvn compile 2>&1 | grep -A 5 -B 5 "ERROR"

# Database connectivity test:
psql -h localhost -U postgres -d qa_ -c "SELECT 1"

# Verify table structure:
psql -h localhost -U postgres -d qa_ -c "\d your_table_name"

# Clean generated files for specific entity:
rm -rf src/main/java/com/example/dwiDaoGenerator/yourentity/generated/*

# Check generated file count:
find src/main/java/com/example/dwiDaoGenerator/*/generated/ -name "*.java" | wc -l
```

### **🚨 When to Add Custom Rules:**
- **Complex business logic** in SQL parameters (audit fields, multi-user operations)
- **Special audit field handling** (created_by, modified_by using same parameter)
- **Multi-table join parameter mapping** (facility + organization + object type filters)
- **Legacy compatibility requirements** (old column names, deprecated fields)
- **JSON field processing** (PostgreSQL JSONB operations, casting)
- **Dynamic query requirements** (optional filters, pagination, sorting)

### **📋 Pre-Generation Checklist:**
```bash
# 1. Verify database connection
psql -h localhost -U postgres -d qa_ -c "\l" | grep qa_

# 2. Check table exists
psql -h localhost -U postgres -d qa_ -c "\dt" | grep your_table_name

# 3. Verify POJO exists and compiles
javac -cp "target/classes" src/main/java/com/example/pojogenerator/pojos/YourEntity.java

# 4. Check repository documentation format
grep -E "#### Method:|```yaml" repository_documents/repository_docs/YourEntityRepositorydoc.md

# 5. Ensure project compiles before generation
mvn compile -q
```

### **🔧 Post-Generation Validation:**
```bash
# 1. Check all 9 files generated
ls src/main/java/com/example/dwiDaoGenerator/yourentity/generated/ | wc -l

# 2. Verify no compilation errors
mvn compile 2>&1 | grep -c "ERROR"

# 3. Check method count in generated DAO
grep -c "public.*(" src/main/java/com/example/dwiDaoGenerator/yourentity/generated/JdbcYourEntityDao.java

# 4. Validate SQL constants
grep -c "public static final String" src/main/java/com/example/dwiDaoGenerator/yourentity/generated/YourEntitySql.java

# 5. Check view class constructors
grep -c "public.*View(" src/main/java/com/example/dwiDaoGenerator/yourentity/generated/*View.java
```

---

## 🎉 **Success Metrics**

A successful DAO generation should result in:
- ✅ **Zero compilation errors**
- ✅ **All 21+ methods implemented** (6 CRUD + custom methods)
- ✅ **Complete supporting file ecosystem**
- ✅ **Production-ready code** with proper annotations
- ✅ **Intelligent parameter mapping** with business logic applied

---

## 🚀 **Quick Reference & Troubleshooting Flowchart**

### **⚡ Quick Start (Copy-Paste Ready)**
```bash
# 1. Navigate to project
cd /home/leucine/eclipse-workspace/postgresConnect

# 2. Clean compile
mvn clean compile

# 3. Update generator main method (edit RepositoryDrivenDaoGenerator.java):
# generator.generateRepositoryDrivenDao("your_table", "YourEntity", "YourEntityRepositorydoc.md");

# 4. Run generator
java -cp "target/classes:$(find ~/.m2/repository -name "*.jar" | tr '\n' ':')" com.example.dwiDaoGenerator.RepositoryDrivenDaoGenerator

# 5. Verify compilation
mvn compile
```

### **🔄 Troubleshooting Flowchart**

```
Generation Failed?
├── Database Connection Error?
│   ├── Check: psql -h localhost -U postgres -d qa_ -c "SELECT 1"
│   └── Fix: Update DB_URL, DB_USER, DB_PASS in generator
│
├── POJO Not Found?
│   ├── Check: ls src/main/java/com/example/pojogenerator/pojos/YourEntity.java
│   └── Fix: Run PojoGenerator first or verify entity name
│
├── Repository Doc Missing?
│   ├── Check: ls repository_documents/repository_docs/YourEntityRepositorydoc.md
│   └── Fix: Create documentation or verify filename
│
└── Table Not Found?
    ├── Check: psql -h localhost -U postgres -d qa_ -c "\dt" | grep your_table
    └── Fix: Verify table name or create table

Compilation Failed?
├── Type Inference Error (View types)?
│   ├── Symptom: "incompatible types: inference variable T"
│   └── Fix: Reorder extractRowMapperType() - most specific first
│
├── Missing Import Error?
│   ├── Symptom: "cannot find symbol: class Page"
│   └── Fix: Check needsSpringDataImports() logic
│
├── Constructor Error?
│   ├── Symptom: "constructor cannot be applied to given types"
│   └── Fix: Verify view class constructor matches row mapper
│
└── Parameter Mapping Error?
    ├── Symptom: SQL parameters don't match method signature
    └── Fix: Add entity-specific parameter rules
```

### **📊 Success Indicators**
```bash
# ✅ All files generated (should return 9)
ls src/main/java/com/example/dwiDaoGenerator/yourentity/generated/ | wc -l

# ✅ Zero compilation errors (should return 0)
mvn compile 2>&1 | grep -c "ERROR"

# ✅ All methods implemented (should be 20+)
grep -c "public.*(" src/main/java/com/example/dwiDaoGenerator/yourentity/generated/JdbcYourEntityDao.java

# ✅ SQL constants generated (should be 15+)
grep -c "public static final String" src/main/java/com/example/dwiDaoGenerator/yourentity/generated/YourEntitySql.java
```

### **🎯 Entity-Specific Quick Configs**

#### **For Checklist Entity:**
```java
generator.generateRepositoryDrivenDao("checklists", "Checklist", "ChecklistRepositorydoc.md");
```

#### **For Task Entity:**
```java
generator.generateRepositoryDrivenDao("tasks", "Task", "TaskRepositorydoc.md");
```

#### **For Job Entity:**
```java
generator.generateRepositoryDrivenDao("jobs", "Job", "JobRepositorydoc.md");
```

#### **For User Entity:**
```java
generator.generateRepositoryDrivenDao("users", "User", "UserRepositorydoc.md");
```

### **🔧 Emergency Fixes**

#### **Fix 1: Row Mapper Order Issue**
```java
// In extractRowMapperType(), ensure this order:
if (returnType.contains("JobLogMigrationEntityView")) { ... }      // Most specific
else if (returnType.contains("EntityJobLiteView")) { ... }         // Medium specific  
else if (returnType.contains("EntityView")) { ... }                // Least specific
```

#### **Fix 2: Missing SpringData Imports**
```java
// In generateDaoInterface(), add this check:
if (needsSpringDataImports(model)) {
    sb.append("import com.example.dwiDaoGenerator.").append(model.getEntityName().toLowerCase()).append(".generated.SpringDataTypes.*;\n");
}
```

#### **Fix 3: Parameter Mapping Business Rules**
```java
// Add to applyMethodSpecificParameterRules():
case "YourEntity":
    return applyYourEntitySpecificParameterRules(sql, methodName, paramNames);
```

---

## 📚 **Next Steps After Generation**

1. **Integration Testing:** Test generated DAO in your application
2. **Performance Optimization:** Add database indexes for custom queries
3. **Documentation:** Update API documentation with new DAO methods
4. **Code Review:** Review generated code for business logic accuracy
5. **Deployment:** Include generated files in your build pipeline

---

## 🏆 **Expert Tips**

### **💡 Pro Tips for Complex Entities:**
- **Always test with the most complex entity first** (e.g., Checklist with 15+ custom methods)
- **Use entity-specific parameter rules** for business logic complexity
- **Generate view classes automatically** - don't create them manually
- **Leverage state enums** for type-safe status management
- **Test compilation after each entity** to catch issues early

### **⚠️ Common Pitfalls to Avoid:**
- **Don't manually edit generated files** - they'll be overwritten
- **Don't skip the pre-generation checklist** - it saves debugging time
- **Don't ignore parameter mapping errors** - they cause runtime failures
- **Don't forget to update imports** when adding Spring Data types
- **Don't use generic view names** - be specific to avoid conflicts

### **🎯 Performance Optimization:**
- **Add database indexes** for frequently queried columns
- **Use appropriate fetch sizes** for large result sets
- **Consider connection pooling** for high-throughput applications
- **Monitor SQL execution plans** for complex queries
- **Cache frequently accessed data** at the service layer

---

**🎉 Congratulations!** You now have a complete, battle-tested guide for generating production-ready DAO packages. The generator handles the complexity so you can focus on business logic!

**Remember:** The generator is designed to create a complete, production-ready DAO ecosystem with minimal manual intervention. Focus on getting the input files (POJO, documentation, database schema) correct, and the generator will handle the rest!
