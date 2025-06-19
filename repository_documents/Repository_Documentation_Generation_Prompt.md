# Repository Documentation Generation Prompt

Use this prompt template to generate concrete, comprehensive documentation for any repository. Replace `{ENTITY_NAME}` and `{REPOSITORY_NAME}` with actual values.

---

## Prompt Template

```
STEP 1: VERIFY REPOSITORY INTERFACE COMPLETENESS
MANDATORY: Before any documentation, examine the actual repository interface:
- Read the complete repository interface file (.java)
- List ALL methods declared in the interface (custom methods)
- List ALL inherited methods from parent interfaces (JpaRepository, JpaSpecificationExecutor, etc.)
- Verify no @Query annotations are missed
- Check for any @Modifying annotations
- Note all method signatures exactly as declared

STEP 2: ANALYZE CODEBASE USAGE
Search the codebase for actual usage patterns:
- Search for repository usage in service classes
- Identify how methods are actually called
- Find real business scenarios and usage contexts
- Note any patterns like bulk operations, specifications, etc.

STEP 3: GENERATE DOCUMENTATION
Generate comprehensive DAO migration documentation for {REPOSITORY_NAME} following this exact structure:

# {REPOSITORY_NAME} - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: {ENTITY_NAME}
- **Primary Purpose**: [Detailed business purpose]
- **Key Relationships**: [Parent/child relationships with other entities]
- **Performance Characteristics**: [Query volume, complexity level]
- **Business Context**: [How this entity fits in the business domain]

## Entity Mapping Documentation

### Field Mappings
Create a table with these exact columns:
| Database Column | Java Field | Type | Nullable | Default |

Include ALL database columns with exact Java field names (camelCase), precise data types, nullable status, and actual default values.

### Relationships
IF entity has JPA relationships (@OneToMany, @ManyToOne, etc.):
Create a table with these exact columns:
| Relationship Type | Field | Target Entity | Fetch Type | Notes |

IF entity has NO JPA relationships:
Simply state: "None - This entity uses foreign key references without JPA relationship mappings."

## Available Repository Methods

### Standard CRUD Methods
List all basic JpaRepository methods:
- `findById(Long id)`
- `findAll()`
- `save({ENTITY_NAME} entity)`
- `deleteById(Long id)`
- `delete({ENTITY_NAME} entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods
List ALL custom methods with exact signatures:
- `methodName(parameters with types)`

## Method Documentation

### Standard CRUD Operations
Always include this section with basic JpaRepository methods.

### Custom Query Methods OR Usage Patterns

**CRITICAL REQUIREMENT: DOCUMENT EVERY METHOD IN COMPLETE DETAIL**
- Document ALL custom methods with full YAML blocks - NO EXCEPTIONS
- Document length is NOT a constraint - comprehensive detail is required
- NEVER summarize or abbreviate method documentation
- Each method MUST have complete business logic derivation, SQL query, parameters, etc.
- If there are 50+ methods, document all 50+ methods individually
- Quality and completeness take precedence over document length

IF repository has custom methods:
Document EVERY SINGLE custom method with this complete format:

```yaml
Signature: [Complete method signature]
Purpose: "[Business purpose in quotes]"

Business Logic Derivation: [MANDATORY - MUST HAVE AT LEAST 5 STEPS]
  1. [Step-by-step business logic breakdown based on actual codebase usage]
  2. [Real business scenarios where this method is used]
  3. [Actual data transformations and filtering logic]
  4. [Integration with service layer patterns]
  5. [Performance and workflow considerations]
  [Additional steps as needed - aim for comprehensive understanding]

SQL Query: |
  [Complete executable SQL query with business logic explanation]
  [If using native query constant, extract the ACTUAL SQL from Queries.java]
  [Use ? for parameters]
  [MUST be runnable SQL - no placeholders]
  [Include JOINs, WHERE clauses, ORDER BY, etc. as needed]
  [BUSINESS LOGIC: Explain what this query does step by step]

Parameters:
  - paramName: Type (Description from actual usage context)

Returns: [Return type with detailed description of what is returned]
Transaction: [Required/Not Required with explanation]
Error Handling: [Actual exceptions from codebase with scenarios]
```

**MANDATORY: Document ALL methods individually - no grouping, no summarization**
- If repository has 1 method → document 1 method fully
- If repository has 10 methods → document 10 methods fully  
- If repository has 50 methods → document 50 methods fully
- If repository has 100 methods → document 100 methods fully
- Document length is unlimited - aim for maximum completeness

IF repository has NO custom methods (only extends JpaRepository/JpaSpecificationExecutor):
Document actual usage patterns found in service classes:

```yaml
Pattern: [actual method usage like save(), findAll(spec, pageable)]
Purpose: "[How it's actually used in business logic]"

Business Logic Derivation: [MANDATORY - MUST HAVE AT LEAST 3 STEPS]
  1. [Real usage context from service classes]
  2. [Actual business scenarios and operations]
  3. [Integration patterns with other components]
  4. [Transaction and performance considerations]
  [Additional steps based on actual usage]

Common Usage Examples:
  - [Real examples from codebase analysis]
  - [Actual method calls and parameters]
  - [Business scenarios where this pattern is used]

Transaction: [Based on actual usage]
Error Handling: [Based on actual error scenarios]
```

VALIDATION CHECKS - EVERY METHOD/PATTERN MUST PASS:
✅ Business Logic Derivation has minimum 3 numbered steps
✅ Business Logic based on ACTUAL codebase usage, not assumptions
✅ SQL Query contains no placeholder text or brackets (OR reference to actual native query constants)
✅ All method parameters are documented
✅ Return type is specified
✅ Transaction requirement is specified
✅ Error handling scenarios are listed
✅ If no custom methods exist, document actual usage patterns from service classes
```

## Transaction Requirements
```yaml
Read-Only Methods: [List]
Transactional Methods: [List]
Isolation Level: [Specify]
Timeout: [Specify]
Rollback: [Conditions]
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - ExceptionType: Description

Validation Rules:
  - field: validation rule

Business Constraints:
  - [Business rules to enforce]
```

REQUIREMENTS: Document EVERY method, field, and relationship. Use exact names and types. Provide complete executable SQL. Explain business context. Include performance and error handling. No placeholders or TODOs.

INTERNAL VALIDATION - USE FOR GENERATION BUT DO NOT INCLUDE IN FINAL OUTPUT:

CRITICAL: Before completing documentation, verify COMPLETENESS:
✅ Repository Interface Verification: All methods from Step 1 interface reading are documented
✅ Field Mappings table has [X] rows (count all database columns)
✅ Relationships table has [X] rows (count all JPA relationships)  
✅ Standard CRUD section lists [X] methods (match JpaRepository interface exactly)
✅ Custom Query section lists [X] methods (match repository interface exactly)
✅ Method Documentation section has [X] method blocks (one per custom method)
✅ EVERY custom method has Business Logic Derivation with minimum 3 steps
✅ EVERY method has executable SQL with no brackets []
✅ EVERY method parameter is documented with exact types
✅ All method signatures match repository interface exactly (character-for-character)
✅ No @Query annotations missed
✅ No @Modifying annotations missed  
✅ No placeholder text or [brackets] remain in final output

RELIABILITY CHECK:
✅ Can a developer implement this repository from scratch using only this documentation? (YES/NO)
✅ Are ALL methods from the repository interface covered? (YES/NO)
✅ Is every SQL query executable without modification? (YES/NO)

DO NOT INCLUDE THIS VERIFICATION SECTION IN THE FINAL REPOSITORY DOCUMENTATION.
```

## Quality Checklist

✅ Every database column mapped to Java field  
✅ Every relationship documented with fetch strategy  
✅ Every method has complete signature and SQL query  
✅ Business purpose explained for every method  
✅ Business logic derivation provided for all custom methods  
✅ Error conditions documented  
✅ Transaction requirements clear  
✅ No placeholder text or TODOs  
✅ All SQL queries are executable

---

**Strategy (200 words max):**

Generate concrete repository documentation by analyzing the existing JPA repository interface and entity class. Extract all method signatures, field mappings, and relationships. For each custom query method, reverse-engineer the business logic from the method name, parameters, and any existing SQL. Include comprehensive error handling covering data integrity violations, constraint failures, and business rule violations. Specify transaction requirements for all write operations. Focus on business context - explain WHY each method exists, not just what it does. The resulting documentation should be a complete specification that any developer can use to implement an identical repository using pure JDBC/SQL without any JPA dependencies.
