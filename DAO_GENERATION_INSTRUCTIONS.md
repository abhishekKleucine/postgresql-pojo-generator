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

## 📞 **Support & Troubleshooting**

### **Debug Mode:**
Add debug logging to see what's happening:
```java
System.out.println("🔍 Processing method: " + methodName);
System.out.println("📊 SQL: " + sqlQuery);
System.out.println("🎯 Parameters: " + paramNames);
```

### **Common Commands:**
```bash
# Recompile after changes:
mvn clean compile

# Check database connectivity:
psql -h localhost -U postgres -d qa_ -c "SELECT 1"

# Verify table structure:
psql -h localhost -U postgres -d qa_ -c "\d your_table_name"

# Clean generated files:
rm -rf src/main/java/com/example/dwiDaoGenerator/yourentity/generated/*
```

### **When to Add Custom Rules:**
- Complex business logic in SQL parameters
- Special audit field handling
- Multi-table join parameter mapping
- Legacy compatibility requirements

---

## 🎉 **Success Metrics**

A successful DAO generation should result in:
- ✅ **Zero compilation errors**
- ✅ **All 21+ methods implemented** (6 CRUD + custom methods)
- ✅ **Complete supporting file ecosystem**
- ✅ **Production-ready code** with proper annotations
- ✅ **Intelligent parameter mapping** with business logic applied

---

## 📚 **Next Steps After Generation**

1. **Integration Testing:** Test generated DAO in your application
2. **Performance Optimization:** Add database indexes for custom queries
3. **Documentation:** Update API documentation with new DAO methods
4. **Code Review:** Review generated code for business logic accuracy
5. **Deployment:** Include generated files in your build pipeline

---

**Remember:** The generator is designed to create a complete, production-ready DAO ecosystem with minimal manual intervention. Focus on getting the input files (POJO, documentation, database schema) correct, and the generator will handle the rest!
