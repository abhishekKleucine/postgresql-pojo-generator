# IFacilityUseCasePropertyMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: FacilityUseCasePropertyMapping (Composite Key Entity)
- **Primary Purpose**: Manages facility use case property mappings with composite key support, property value analytics, and facility-scoped property management functionality
- **Key Relationships**: Complex relationships with Facility, UseCase, and Property entities through composite key associations
- **Performance Characteristics**: Medium to high query volume with property value analytics, distinct value queries, and facility-scoped operations
- **Business Context**: Property mapping component providing facility use case property associations, property value analytics, distinct value analysis, and facility-scoped property management for multi-tenant property workflows
- **Advanced Features**: Extends JpaSpecificationExecutor for dynamic query capabilities and uses composite key for entity identification

## Entity Overview
- **Composite Key**: Uses FacilityUseCasePropertyCompositeKey for unique identification
- **Multi-Tenant**: Facility-scoped property mappings for comprehensive isolation
- **Property Analytics**: Advanced property value analysis and distinct value queries
- **Dynamic Queries**: JpaSpecificationExecutor support for complex filtering

## Available Repository Methods

### Standard CRUD Methods (JpaRepository + JpaSpecificationExecutor)
- `findById(FacilityUseCasePropertyCompositeKey id)`
- `findAll()`
- `save(FacilityUseCasePropertyMapping entity)`
- `saveAll(Iterable<FacilityUseCasePropertyMapping> entities)`
- `deleteById(FacilityUseCasePropertyCompositeKey id)`
- `delete(FacilityUseCasePropertyMapping entity)`
- `existsById(FacilityUseCasePropertyCompositeKey id)`
- `count()`
- **Dynamic Queries**: `findAll(Specification<FacilityUseCasePropertyMapping> spec)`, etc.

### Custom Query Methods (3 methods - ALL methods documented)

**Facility Use Case Property Retrieval Methods (1 method):**
- `findAllByFacilityIdAndUseCaseId(Long facilityId, Long useCaseId)`

**Property Value Analytics Methods (2 methods):**
- `findDistinctPropertyValuesOfFacilityAndProperty(...)`
- `findTotalPropertyValuesOfFacilityAndProperty(...)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods with composite key
Optional<FacilityUseCasePropertyMapping> findById(FacilityUseCasePropertyCompositeKey id)
List<FacilityUseCasePropertyMapping> findAll()
FacilityUseCasePropertyMapping save(FacilityUseCasePropertyMapping entity)
List<FacilityUseCasePropertyMapping> saveAll(Iterable<FacilityUseCasePropertyMapping> entities)
void deleteById(FacilityUseCasePropertyCompositeKey id)
void delete(FacilityUseCasePropertyMapping entity)
boolean existsById(FacilityUseCasePropertyCompositeKey id)
long count()

// JpaSpecificationExecutor methods for dynamic queries
Optional<FacilityUseCasePropertyMapping> findOne(Specification<FacilityUseCasePropertyMapping> spec)
List<FacilityUseCasePropertyMapping> findAll(Specification<FacilityUseCasePropertyMapping> spec)
Page<FacilityUseCasePropertyMapping> findAll(Specification<FacilityUseCasePropertyMapping> spec, Pageable pageable)
List<FacilityUseCasePropertyMapping> findAll(Specification<FacilityUseCasePropertyMapping> spec, Sort sort)
long count(Specification<FacilityUseCasePropertyMapping> spec)
```

### Facility Use Case Property Retrieval Methods

#### Method: findAllByFacilityIdAndUseCaseId(Long facilityId, Long useCaseId)
```yaml
Signature: List<FacilityUseCasePropertyMapping> findAllByFacilityIdAndUseCaseId(Long facilityId, Long useCaseId)
Purpose: "Find all property mappings for facility and use case for comprehensive property management and mapping retrieval"

Business Logic Derivation:
  1. Used for facility use case property retrieval during property management and mapping operations
  2. Provides property mapping access for facility workflows enabling comprehensive property management and facility functionality
  3. Critical for property operations requiring mapping access for property management and facility control
  4. Used in property workflows for accessing property mappings for mapping operations and property processing
  5. Enables property management with mapping access for comprehensive property processing and facility control

SQL Query: |
  SELECT fucpm FROM FacilityUseCasePropertyMapping fucpm 
  WHERE fucpm.facilityId = ? AND fucpm.useCaseId = ?

Parameters:
  - facilityId: Long (Facility identifier for property mapping retrieval)
  - useCaseId: Long (Use case identifier for property mapping retrieval)

Returns: List<FacilityUseCasePropertyMapping> (property mappings for facility and use case)
Transaction: Not Required (read operation)
Error Handling: Returns empty list if no mappings found for facility and use case
```

### Property Value Analytics Methods

#### Method: findDistinctPropertyValuesOfFacilityAndProperty(...)
```yaml
Signature: List<Object> findDistinctPropertyValuesOfFacilityAndProperty(@Param("facilityId") Long facilityId, @Param("propertyId") Long propertyId, @Param("state") String state, @Param("archived") boolean archived, @Param("limit") int limit, @Param("offset") long offset, @Param("propertyNameInput") String propertyNameInput)
Purpose: "Find distinct property values for facility and property with filtering and pagination for property value analytics"

Business Logic Derivation:
  1. Used for property value analytics during property analysis and value discovery operations
  2. Provides distinct value analysis for analytics workflows enabling comprehensive property analytics and value functionality
  3. Critical for analytics operations requiring distinct value analysis for analytics management and value control
  4. Used in analytics workflows for property value analysis and discovery operations
  5. Enables analytics management with distinct value analysis for comprehensive analytics processing and value control

Native SQL Query: |
  SELECT DISTINCT property_value 
  FROM facility_use_case_property_mappings fucpm
  INNER JOIN entity_objects eo ON fucpm.entity_object_id = eo.id
  WHERE fucpm.facility_id = :facilityId 
    AND fucpm.property_id = :propertyId
    AND eo.state = :state 
    AND eo.archived = :archived
    AND property_value ILIKE '%' || :propertyNameInput || '%'
  ORDER BY property_value
  LIMIT :limit OFFSET :offset

Parameters:
  - facilityId: Long (Facility identifier for property value analysis)
  - propertyId: Long (Property identifier for value analysis)
  - state: String (Entity state for filtering)
  - archived: boolean (Archive status for filtering)
  - limit: int (Pagination limit)
  - offset: long (Pagination offset)
  - propertyNameInput: String (Search filter for property values)

Returns: List<Object> (distinct property values)
Transaction: Not Required (read operation)
Error Handling: Returns empty list if no distinct values found
```

#### Method: findTotalPropertyValuesOfFacilityAndProperty(...)
```yaml
Signature: long findTotalPropertyValuesOfFacilityAndProperty(@Param("facilityId") Long facilityId, @Param("propertyId") Long propertyId, @Param("state") String state, @Param("archived") boolean archived, @Param("propertyNameInput") String propertyNameInput)
Purpose: "Count total property values for facility and property with filtering for pagination and analytics"

Business Logic Derivation:
  1. Used for property value counting during property analytics and pagination operations
  2. Provides value counting for analytics workflows enabling comprehensive property analytics and counting functionality
  3. Critical for analytics operations requiring value counting for analytics management and counting control
  4. Used in analytics workflows for property value counting and pagination operations
  5. Enables analytics management with value counting for comprehensive analytics processing and counting control

Native SQL Query: |
  SELECT COUNT(DISTINCT property_value) 
  FROM facility_use_case_property_mappings fucpm
  INNER JOIN entity_objects eo ON fucpm.entity_object_id = eo.id
  WHERE fucpm.facility_id = :facilityId 
    AND fucpm.property_id = :propertyId
    AND eo.state = :state 
    AND eo.archived = :archived
    AND property_value ILIKE '%' || :propertyNameInput || '%'

Parameters:
  - facilityId: Long (Facility identifier for value counting)
  - propertyId: Long (Property identifier for counting)
  - state: String (Entity state for filtering)
  - archived: boolean (Archive status for filtering)
  - propertyNameInput: String (Search filter for property values)

Returns: long (total count of distinct property values)
Transaction: Not Required (read operation)
Error Handling: Returns 0 if no values found
```

### Key Repository Usage Patterns

#### Pattern: JpaSpecificationExecutor for Dynamic Property Queries
```yaml
Usage: Dynamic property mapping queries with complex criteria and facility-scoped filtering
Purpose: "Support complex property mapping searches and filtering with dynamic query construction"

Business Logic Derivation:
  1. Enables complex property mapping search functionality with dynamic criteria construction for advanced property management operations
  2. Provides flexible query capabilities for property workflows enabling comprehensive search functionality and property management
  3. Critical for advanced search operations requiring dynamic query construction for property management and search control
  4. Used in search workflows for dynamic property mapping queries and advanced search operations
  5. Enables advanced property management with dynamic search capabilities for comprehensive property processing and search control

Common Usage Examples:
  - Property mapping search with multiple criteria (facility, use case, property type)
  - Filtered property listings with pagination and sorting
  - Complex property queries with joins and conditions
  - Dynamic property reporting with flexible criteria

Transaction: Not Required for query operations
Error Handling: Specification validation and query construction error handling
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Facility-Scoped Property Management
```yaml
Usage: Facility-scoped property management for multi-tenant property mapping and facility functionality
Purpose: "Manage facility properties for comprehensive facility functionality and property processing"

Business Logic Derivation:
  1. Facility property management provides facility functionality through property mapping, value management, and facility operations
  2. Property lifecycle includes mapping creation, value management, and facility coordination for property control
  3. Property management operations require facility processing for property lifecycle and mapping control
  4. Facility operations enable comprehensive property functionality with mapping capabilities and management
  5. Property lifecycle management supports facility requirements and functionality for facility property processing

Common Usage Examples:
  - facilityUseCasePropertyMappingRepository.findAllByFacilityIdAndUseCaseId() for facility property retrieval
  - Property value analytics with distinct value analysis
  - Facility-scoped property value discovery and analysis
  - Multi-tenant property management with facility isolation

Transaction: Not Required for property management operations
Error Handling: Facility property management error handling and mapping validation verification
```

### Pattern: Property Value Analytics and Discovery
```yaml
Usage: Property value analytics and discovery for data analysis and property insights functionality
Purpose: "Analyze property values for comprehensive analytics functionality and insights processing"

Business Logic Derivation:
  1. Property analytics management operations require comprehensive property mapping access for analytics-level property management and insights functionality
  2. Analytics management supports insights requirements and functionality for property processing workflows
  3. Analytics-level property operations ensure proper insights functionality through property management and analytics control
  4. Property workflows coordinate analytics management with insights processing for comprehensive property operations
  5. Analytics management supports insights requirements and functionality for comprehensive property analytics management

Common Usage Examples:
  - facilityUseCasePropertyMappingRepository.findDistinctPropertyValuesOfFacilityAndProperty() for value discovery
  - facilityUseCasePropertyMappingRepository.findTotalPropertyValuesOfFacilityAndProperty() for pagination
  - Property value analytics with filtering and search capabilities
  - Data discovery and analysis for property insights

Transaction: Not Required for analytics operations
Error Handling: Property analytics error handling and insights validation verification
```

### Pattern: Composite Key Entity Management
```yaml
Usage: Composite key entity management for complex entity identification and mapping functionality
Purpose: "Manage composite key entities for comprehensive identification functionality and mapping processing"

Business Logic Derivation:
  1. Composite key management operations require comprehensive entity access for identification-level entity management and mapping functionality
  2. Identification management supports mapping requirements and functionality for entity processing workflows
  3. Identification-level entity operations ensure proper mapping functionality through entity management and identification control
  4. Entity workflows coordinate identification management with mapping processing for comprehensive entity operations
  5. Identification management supports mapping requirements and functionality for comprehensive composite key entity management

Common Usage Examples:
  - FacilityUseCasePropertyCompositeKey for unique entity identification
  - Composite key-based CRUD operations
  - Multi-field entity identification with facility, use case, and property
  - Complex entity relationships with composite key associations

Transaction: Not Required for identification operations
Error Handling: Composite key error handling and identification validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllByFacilityIdAndUseCaseId, findDistinctPropertyValuesOfFacilityAndProperty, findTotalPropertyValuesOfFacilityAndProperty, existsById, count, JpaSpecificationExecutor methods

Transactional Methods:
  - save, saveAll, delete, deleteById

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Runtime exceptions
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Composite key constraint violations
    * Foreign key violations (facility, use case, property references)
    * Property mapping integrity constraint violations
  - EntityNotFoundException: Property mapping not found by composite key or criteria
  - OptimisticLockException: Concurrent property mapping modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or mapping context
  - ConstraintViolationException: Property mapping constraint violations

Validation Rules:
  - Composite key must have valid facility, use case, and property identifiers
  - Property mappings must reference valid facility, use case, and property entities
  - Property values must be compatible with property type definitions
  - Analytics queries must include valid filtering parameters

Business Constraints:
  - Facility, use case, and property references must be valid for mapping integrity
  - Property mappings must support multi-tenant requirements and facility isolation
  - Property value analytics must maintain performance for large datasets
  - Composite key relationships must maintain referential integrity
  - Property mapping lifecycle management must maintain consistency and functionality
  - Mapping associations must support property requirements and functionality for property processing
  - Property operations must maintain transaction consistency and constraint integrity for property management
  - Analytics operations must maintain performance and accuracy for property insights
  - Facility isolation must be maintained for multi-tenant property management
  - Dynamic queries must support complex property search requirements and functionality
```

## Facility Use Case Property Mapping Considerations

### Composite Key Integration
```yaml
Composite Key Design: FacilityUseCasePropertyCompositeKey provides unique identification across facility, use case, and property dimensions
Multi-Field Identity: Composite key enables complex entity relationships and associations
Key Management: Composite key operations require careful handling of multi-field identification
Relationship Integrity: Composite key design maintains referential integrity across multiple entities
Association Management: Composite key enables comprehensive entity association management
```

### Multi-Tenant Property Management
```yaml
Facility Isolation: Property mappings enable facility-scoped isolation for multi-tenant functionality
Tenant Management: Facility-based property management ensures proper tenant isolation
Property Scoping: Multi-tenant property management with facility-level isolation
Isolation Control: Facility isolation supports multi-tenant property functionality and access control
Tenant Security: Property mapping isolation ensures tenant data security and privacy
```

### Property Value Analytics Integration
```yaml
Value Discovery: Property value analytics enable comprehensive data discovery and insights
Analytics Performance: Optimized queries for property value analysis and discovery
Data Insights: Property value analytics support business intelligence and reporting
Value Analysis: Comprehensive property value analysis with filtering and pagination
Reporting Support: Property analytics enable advanced reporting and data visualization
```

### Dynamic Query and Search Integration
```yaml
Search Functionality: Property mappings enable search functionality through JpaSpecificationExecutor and dynamic query construction
Filtering: Property mapping filtering with specification support and comprehensive filtering functionality
Reporting: Property mapping reporting with dynamic queries and comprehensive reporting functionality
Data Discovery: Property mapping data discovery with search coordination and comprehensive discovery functionality
Query Optimization: Property mapping query optimization with specification support and comprehensive optimization functionality
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the FacilityUseCasePropertyMapping repository without JPA/Hibernate dependencies, focusing on composite key management, property analytics, and multi-tenant property management patterns.
