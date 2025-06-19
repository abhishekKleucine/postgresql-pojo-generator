# IPropertyRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Property
- **Primary Purpose**: Manages property entities for dynamic property definitions with use case associations, property type management, and global property configuration
- **Key Relationships**: Property entity linking to UseCase with many-to-one relationship for use case-scoped property definitions and organizational property management
- **Performance Characteristics**: Low to moderate query volume with property discovery, specification-based filtering, and property configuration management
- **Business Context**: Dynamic property management component that provides use case-scoped property definitions, property type classification, global property configuration, and archived property management for flexible data modeling and organizational property control

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | false | null |
| label | label | String | false | null |
| place_holder | placeHolder | String | true | null |
| use_cases_id | useCase.id / useCaseId | Long | false | null |
| type | type | Type.PropertyType | false | null |
| order_tree | orderTree | Integer | false | null |
| is_global | isGlobal | boolean | false | false |
| archived | archived | boolean | false | false |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Property Constants
- **ORGANISATION_ID**: "organisationId" - Organization context identifier
- **TYPE**: "type" - Property type classification
- **ARCHIVED**: "archived" - Archived status flag
- **ORDER_TREE**: "orderTree" - Property ordering identifier

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | useCase | UseCase | LAZY | Parent use case, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Property entity)`
- `deleteById(Long id)`
- `delete(Property entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<Property> spec)`
- `findAll(Specification<Property> spec, Pageable pageable)`
- `findAll(Specification<Property> spec, Sort sort)`
- `findOne(Specification<Property> spec)`
- `count(Specification<Property> spec)`

### Custom Query Methods (1 method - ALL methods documented)

- `findAll(Specification<Property> specification, Pageable pageable)` (Override)

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Property> findById(Long id)
List<Property> findAll()
Property save(Property entity)
void deleteById(Long id)
void delete(Property entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<Property> findAll(Specification<Property> spec)
Page<Property> findAll(Specification<Property> spec, Pageable pageable)
List<Property> findAll(Specification<Property> spec, Sort sort)
Optional<Property> findOne(Specification<Property> spec)
long count(Specification<Property> spec)
```

### Custom Query Methods

#### Method: findAll(Specification<Property> specification, Pageable pageable) [Override]
```yaml
Signature: Page<Property> findAll(@Nullable Specification<Property> specification, Pageable pageable)
Purpose: "Find properties with specification filtering and pagination for advanced property discovery and management"

Business Logic Derivation:
  1. Used for advanced property search with multiple filtering criteria and pagination support for property management
  2. Provides complex property discovery capabilities for property configuration and organizational property management
  3. Critical for property management operations requiring multi-criteria filtering and large property dataset handling
  4. Used in property administration workflows requiring comprehensive property search and discovery capabilities
  5. Enables advanced property discovery with complex filtering for comprehensive property management and configuration

SQL Query: |
  SELECT p.* FROM properties p
  LEFT JOIN use_cases uc ON p.use_cases_id = uc.id
  WHERE [dynamic specification criteria]
  ORDER BY [pageable sort criteria]
  LIMIT ? OFFSET ?

Parameters:
  - specification: Specification<Property> (Dynamic specification criteria for property filtering, nullable)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<Property> (paginated properties matching specification criteria)
Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty page if no properties match specification criteria
```

### Key Repository Usage Patterns

#### Pattern: save() for Property Lifecycle Management
```yaml
Usage: propertyRepository.save(property)
Purpose: "Create new properties, update property configuration, and manage property lifecycle"

Business Logic Derivation:
  1. Used for property creation with use case associations, property type configuration, and organizational property management
  2. Handles property updates including label modifications, type changes, and global property configuration
  3. Updates property information for property management and use case configuration operations
  4. Critical for property lifecycle management and dynamic data modeling operations
  5. Supports property operations with comprehensive configuration management and organizational property control

Transaction: Required (@Transactional annotation on repository)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: Advanced Property Discovery with Specifications
```yaml
Usage: findAll(specification, pageable) for complex property search
Purpose: "Advanced property search with complex filtering for property management and configuration"

Business Logic Derivation:
  1. Advanced property search enables complex filtering with use case scoping, property type filtering, and organizational context
  2. Specification-based filtering supports property discovery with multiple criteria and pagination for large property datasets
  3. Property search workflows enable property administration and configuration management operations
  4. Global property filtering enables organizational property management and global property configuration
  5. Archived property filtering enables property lifecycle management and property archival operations

Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty page for search criteria with no matching properties
```

#### Pattern: Property Configuration for Dynamic Data Modeling
```yaml
Usage: Properties for dynamic data modeling and organizational property management
Purpose: "Provide property configuration for dynamic data modeling and organizational property control"

Business Logic Derivation:
  1. Properties enable dynamic data modeling with use case-scoped property definitions and organizational context
  2. Property type classification supports property categorization and type-based property management
  3. Global property configuration enables organizational property control and cross-use-case property management
  4. Property ordering supports property hierarchy and display management for user interface operations
  5. Archived property management enables property lifecycle control and property configuration management

Transaction: Not Required for configuration access
Error Handling: Configuration validation and property definition verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Use Case-Scoped Property Management
```yaml
Usage: Property management within use case context for dynamic data modeling
Purpose: "Manage use case-scoped properties for dynamic data modeling and organizational property control"

Business Logic Derivation:
  1. Properties provide use case-scoped data modeling capabilities enabling flexible property definitions
  2. Use case association ensures property definitions are properly scoped and managed within use case context
  3. Property type classification enables type-based property management and validation for data modeling
  4. Global property configuration enables cross-use-case property sharing and organizational property management
  5. Property ordering enables proper property hierarchy and display management for user interface operations

Common Property Patterns:
  - Name and label configuration for property identification and display management
  - Placeholder configuration for user interface guidance and property input assistance
  - Property type classification for data validation and property behavior control
  - Order tree configuration for property hierarchy and display ordering management
  - Global property flags for cross-use-case property sharing and organizational control
  - Archived property management for property lifecycle control and configuration management

Transaction: Required for property persistence and lifecycle management
Error Handling: Configuration validation and property definition verification
```

### Pattern: Advanced Property Search and Administration
```yaml
Usage: Specification-based property search for property administration and management
Purpose: "Advanced property discovery with complex filtering for property administration and configuration"

Business Logic Derivation:
  1. Advanced property search enables property administration with multi-criteria filtering and organizational context
  2. Specification-based filtering supports property discovery with use case scoping and property type filtering
  3. Property administration workflows require comprehensive property search and configuration management
  4. Global property filtering enables organizational property management and global property configuration
  5. Archived property filtering enables property lifecycle management and property archival administration

Common Search Criteria:
  - Organization ID filtering for organizational property management and multi-tenant property isolation
  - Property type filtering for type-based property administration and management workflows
  - Archived status filtering for property lifecycle management and archived property administration
  - Use case scoping for use case-specific property administration and management operations
  - Global property filtering for cross-use-case property management and organizational control

Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty results for search criteria with no matching properties
```

### Pattern: Property Configuration and Lifecycle Management
```yaml
Usage: Property configuration management for dynamic data modeling and organizational control
Purpose: "Manage property configuration and lifecycle for dynamic data modeling and organizational property control"

Business Logic Derivation:
  1. Property configuration management enables dynamic data modeling with use case-scoped property definitions
  2. Property lifecycle management includes creation, modification, global configuration, and archival operations
  3. Property type management enables type-based property validation and behavior control for data modeling
  4. Global property configuration enables organizational property control and cross-use-case property sharing
  5. Property archival management enables property lifecycle control and configuration management operations

Common Configuration Operations:
  - Property creation with use case association and property type configuration
  - Property modification for label updates, type changes, and configuration adjustments
  - Global property configuration for cross-use-case property sharing and organizational control
  - Property archival for lifecycle management and configuration cleanup operations
  - Property ordering management for hierarchy control and display management

Transaction: Required for property lifecycle and configuration operations
Error Handling: DataIntegrityViolationException, configuration validation errors
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAll(Specification), existsById, count
  - findOne(Specification), count(Specification)

Transactional Methods:
  - save, delete, deleteById, findAll(Specification, Pageable)

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback per repository annotation)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (name, label, use_cases_id, type, order_tree)
    * Foreign key violations (invalid use_cases_id references)
    * Unique constraint violations on name within use case scope
    * Invalid enum values for type field
  - EntityNotFoundException: Property not found by ID or criteria
  - OptimisticLockException: Concurrent property modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - ConstraintViolationException: Property configuration constraint violations

Validation Rules:
  - name: Required, max length 255 characters, property identifier within use case scope
  - label: Required, max length 255 characters, property display label for user interface
  - placeHolder: Optional, max length 255 characters, user interface guidance text
  - useCase: Required, must reference existing use case, immutable after creation
  - useCaseId: Derived from useCase relationship for use case-scoped operations
  - type: Required, must be valid PropertyType enum value for property classification
  - orderTree: Required, integer for property ordering and hierarchy management
  - isGlobal: Defaults to false, boolean for global property configuration and cross-use-case sharing
  - archived: Defaults to false, boolean for property lifecycle management and archival

Business Constraints:
  - Properties must belong to valid use cases for proper property scoping and management
  - Property names should be unique within use case scope for property identification
  - Property types must be consistent with property usage and data modeling requirements
  - Global properties must be properly configured for cross-use-case sharing and organizational control
  - Property ordering must maintain proper hierarchy and display management requirements
  - Archived properties must be handled appropriately in property discovery and configuration operations
  - Property lifecycle management must maintain use case associations and property configuration consistency
  - Property modifications must maintain data integrity and configuration consistency
  - Global property changes must consider cross-use-case impact and organizational requirements
  - Property archival must maintain data integrity and configuration management requirements
```

## Property-Specific Considerations

### Dynamic Data Modeling
```yaml
Property Types: Type classification for property behavior control and data validation
Use Case Scoping: Properties are scoped to specific use cases for proper property management
Global Configuration: Global properties enable cross-use-case sharing and organizational control
Ordering Management: Order tree for property hierarchy and display management
Lifecycle Control: Archived flag for property lifecycle management and configuration control
```

### Organizational Management
```yaml
Use Case Association: Properties are associated with use cases for proper scoping and management
Global Properties: Global flag enables organizational property control and cross-use-case sharing
Property Administration: Advanced search capabilities for property administration and management
Configuration Management: Property configuration includes name, label, type, and ordering information
Archival Management: Archived properties enable lifecycle control and configuration management
```

### User Interface Integration
```yaml
Display Management: Label and placeholder configuration for user interface operations
Ordering Control: Order tree for property hierarchy and display ordering management
Property Classification: Type-based property classification for user interface behavior control
Configuration Interface: Property administration interfaces require advanced search and filtering
Global Property UI: Global properties require special handling for cross-use-case operations
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Property repository without JPA/Hibernate dependencies, focusing on dynamic property management and organizational property control patterns.
