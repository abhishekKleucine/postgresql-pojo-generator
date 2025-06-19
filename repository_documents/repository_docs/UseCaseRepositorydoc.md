# IUseCaseRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: UseCase
- **Primary Purpose**: Manages use case entities representing business use case categories with ordering, metadata management, and hierarchical organization
- **Key Relationships**: Independent entity with hierarchical ordering and metadata for use case categorization throughout the system
- **Performance Characteristics**: Low query volume with ordered use case retrieval and use case validation operations
- **Business Context**: Use case categorization component that provides business use case classification, ordering, and metadata for workflow categorization and facility-use case mapping

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | false | null |
| label | label | String | true | null |
| description | description | String | true | null |
| order_tree | orderTree | Integer | false | null |
| metadata | metadata | JsonNode | false | '{}' |
| archived | archived | boolean | false | false |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

None - This entity uses foreign key references without JPA relationship mappings.

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(UseCase entity)`
- `deleteById(Long id)`
- `delete(UseCase entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<UseCase> spec)`
- `findAll(Specification<UseCase> spec, Pageable pageable)`
- `findAll(Specification<UseCase> spec, Sort sort)`
- `findOne(Specification<UseCase> spec)`
- `count(Specification<UseCase> spec)`

### Custom Query Methods (1 method - ALL methods documented)

- `findAllByArchivedOrderByOrderTree(boolean archived)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<UseCase> findById(Long id)
List<UseCase> findAll()
UseCase save(UseCase entity)
void deleteById(Long id)
void delete(UseCase entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<UseCase> findAll(Specification<UseCase> spec)
Page<UseCase> findAll(Specification<UseCase> spec, Pageable pageable)
List<UseCase> findAll(Specification<UseCase> spec, Sort sort)
Optional<UseCase> findOne(Specification<UseCase> spec)
long count(Specification<UseCase> spec)
```

### Custom Query Methods

#### Method: findAllByArchivedOrderByOrderTree(boolean archived)
```yaml
Signature: List<UseCase> findAllByArchivedOrderByOrderTree(boolean archived)
Purpose: "Get all use cases filtered by archival status ordered by hierarchical order tree for use case listing"

Business Logic Derivation:
  1. Used in UseCaseService for retrieving active use cases in proper hierarchical order for user interface display
  2. Provides ordered list of use cases filtered by archival status for use case selection and categorization
  3. Critical for use case management operations requiring ordered display of available use cases
  4. Used in use case listing workflows for facility-use case mapping and workflow categorization
  5. Enables hierarchical use case display with archival filtering for proper use case organization and selection

SQL Query: |
  SELECT uc.* FROM use_cases uc
  WHERE uc.archived = ?
  ORDER BY uc.order_tree

Parameters:
  - archived: boolean (Archival status filter - typically false for active use cases)

Returns: List<UseCase> (use cases filtered by archival status ordered by hierarchy)
Transaction: Not Required
Error Handling: Returns empty list if no use cases match archival status
```

### Key Repository Usage Patterns

#### Pattern: save() for UseCase Lifecycle Management
```yaml
Usage: useCaseRepository.save(useCase)
Purpose: "Create new use cases, update use case information, and manage use case lifecycle"

Business Logic Derivation:
  1. Used for use case creation with proper ordering and metadata configuration
  2. Handles use case information updates including name, label, description, and metadata operations
  3. Updates use case ordering and metadata for use case organization and categorization
  4. Critical for use case lifecycle management and use case configuration operations
  5. Supports use case operations with hierarchical ordering and metadata management

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findById() for UseCase Context Operations
```yaml
Usage: useCaseRepository.findById(useCaseId)
Purpose: "Retrieve use case entity for use case-specific operations and validation"

Business Logic Derivation:
  1. Used for use case context retrieval and use case validation operations
  2. Critical for use case validation, use case information display, and use case-specific business logic
  3. Used in facility-use case mapping operations and workflow categorization
  4. Essential for use case context management and use case-based workflow operations
  5. Enables use case-centric operations with comprehensive use case information

Transaction: Not Required
Error Handling: Throws ResourceNotFoundException if use case not found
```

#### Pattern: findAll(specification) for Dynamic UseCase Discovery
```yaml
Usage: useCaseRepository.findAll(specification, pageable)
Purpose: "Dynamic use case discovery with complex filtering and pagination for use case management"

Business Logic Derivation:
  1. Used for advanced use case search and listing operations with dynamic criteria
  2. Applies dynamic specifications for multi-criteria use case filtering with business logic
  3. Supports pagination for use case datasets and use case management operations
  4. Enables flexible use case discovery and management operations with complex filtering
  5. Critical for use case management APIs and use case administration functionality

Transaction: Not Required
Error Handling: Returns empty page if no matches found
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: findAllByArchivedOrderByOrderTree(false) for Active UseCase Listing
```yaml
Signature: List<UseCase> findAllByArchivedOrderByOrderTree(false)
Purpose: "Get all active use cases in hierarchical order for use case selection and categorization"

Business Logic Derivation:
  1. Used exclusively in UseCaseService for retrieving active use cases for facility-use case mapping operations
  2. Provides properly ordered list of available use cases for user interface selection and workflow categorization
  3. Critical for facility management operations requiring use case selection and business categorization
  4. Used in facility-use case mapping workflows for associating facilities with business use cases
  5. Enables proper use case organization and selection with hierarchical ordering for business workflow management

Common Usage Examples:
  - useCaseRepository.findAllByArchivedOrderByOrderTree(false) for active use case retrieval in facility management
  - Use case selection for facility-use case mapping operations and business categorization
  - Use case listing for workflow categorization and business process organization
  - Active use case retrieval for user interface selection and business workflow management
  - Hierarchical use case organization for proper business process categorization

Transaction: Not Required
Error Handling: Returns empty list if no active use cases available
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllByArchivedOrderByOrderTree, findAll(Specification)
  - existsById, count, findOne(Specification), count(Specification)

Transactional Methods:
  - save, delete, deleteById

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (name, orderTree)
    * Unique constraint violations on use case name or order tree
    * Invalid JSON format in metadata field
  - EntityNotFoundException: UseCase not found by ID or criteria
  - OptimisticLockException: Concurrent use case modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - ResourceNotFoundException: UseCase not found during operations

Validation Rules:
  - name: Required, max length 255 characters, should be unique use case identifier
  - label: Optional, max length 255 characters, user-friendly display name
  - description: Optional, text field for use case description
  - orderTree: Required, integer for hierarchical ordering, should be unique
  - metadata: Required, defaults to empty JSON object, must be valid JSON
  - archived: Defaults to false, used for soft deletion of use cases

Business Constraints:
  - Use case name should be unique across the system for clarity and identification
  - Order tree should be unique to maintain proper hierarchical ordering
  - Use case archival should be used instead of deletion for data integrity
  - Metadata must be valid JSON format for proper metadata management
  - Order tree changes affect hierarchical display and use case organization
  - Use case deletion requires validation of dependent facilities and workflows
  - Use case modifications must maintain hierarchical ordering consistency
  - Active use cases must be available for facility-use case mapping operations
  - Use case metadata should follow defined schema for consistency and validation
  - Administrative operations must maintain use case hierarchy and ordering integrity
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the UseCase repository without JPA/Hibernate dependencies.
