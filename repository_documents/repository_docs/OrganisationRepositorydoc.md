# IOrganisationRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Organisation
- **Primary Purpose**: Manages organisation entities representing the top-level organizational hierarchy with FQDN management, notification context, and organizational scope
- **Key Relationships**: Root organizational entity with facilities and comprehensive organizational context throughout the system
- **Performance Characteristics**: Low to moderate query volume with organizational context retrieval, FQDN operations, and notification management
- **Business Context**: Core organizational root component that provides organizational context, FQDN information, and notification settings for all organization-scoped operations

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | false | null |
| archived | archived | boolean | false | false |
| fqdn | fqdn | String | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @OneToMany | facilities | Set\<Facility\> | LAZY | Child facilities, cascade = ALL |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Organisation entity)`
- `deleteById(Long id)`
- `delete(Organisation entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<Organisation> spec)`
- `findAll(Specification<Organisation> spec, Pageable pageable)`
- `findAll(Specification<Organisation> spec, Sort sort)`
- `findOne(Specification<Organisation> spec)`
- `count(Specification<Organisation> spec)`

### Custom Query Methods
None - This repository only extends JpaRepository and JpaSpecificationExecutor with no custom methods.

## Method Documentation (Actual Usage Patterns)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Organisation> findById(Long id)
List<Organisation> findAll()
Organisation save(Organisation entity)
void deleteById(Long id)
void delete(Organisation entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<Organisation> findAll(Specification<Organisation> spec)
Page<Organisation> findAll(Specification<Organisation> spec, Pageable pageable)
List<Organisation> findAll(Specification<Organisation> spec, Sort sort)
Optional<Organisation> findOne(Specification<Organisation> spec)
long count(Specification<Organisation> spec)
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: getReferenceById() for Performance-Optimized Organisation Context
```yaml
Signature: Organisation getReferenceById(Long organisationId)
Purpose: "Get organisation reference for performance-optimized operations without full entity loading"

Business Logic Derivation:
  1. Used extensively for performance-optimized organisation context retrieval in high-volume operations across ChecklistService, ImportExportChecklistService, and NotificationService
  2. Provides organisation reference for entity associations and organisational context without loading full entity
  3. Critical for entity creation operations requiring organisation association with minimal performance overhead
  4. Used in checklist creation, media creation, and notification operations for efficient organisation context management
  5. Enables efficient organisation context operations with minimal database interaction for performance optimization in high-frequency operations

Common Usage Examples:
  - organisationRepository.getReferenceById(principalUser.getOrganisationId()) for efficient context access in entity creation
  - Organisation association in checklist creation, media creation, and workflow operations
  - Organisation context in notification workflows for FQDN retrieval and notification routing
  - Entity association operations requiring organisation context for business logic validation
  - Performance-optimized organisation context retrieval for entity relationship management

Transaction: Not Required
Error Handling: EntityNotFoundException when organisation reference not found
```

### Pattern: getOne() for Organisation Context with FQDN Operations
```yaml
Signature: Organisation getOne(Long organisationId)
Purpose: "Get complete organisation entity for FQDN operations and notification context"

Business Logic Derivation:
  1. Used extensively in NotificationService for FQDN retrieval and notification routing across all notification workflows
  2. Provides complete organisation entity including FQDN for notification URL generation and email routing
  3. Critical for notification operations requiring FQDN for proper notification delivery and URL construction
  4. Used in all notification workflows including corrections, exceptions, user groups, and workflow notifications
  5. Enables notification operations with complete organisation information including FQDN for proper notification routing

Common Usage Examples:
  - organisationRepository.getOne(organisationId) in NotificationService for FQDN retrieval and notification routing
  - FQDN access for notification URL generation and email routing across all notification types
  - Organisation context in correction workflows, parameter exception workflows, and user group notifications
  - Complete organisation information for notification context and organizational branding
  - Organisation data for notification templates and organizational identification

Transaction: Not Required
Error Handling: EntityNotFoundException when organisation not found
```

### Pattern: findById() for Organisation Validation and Context Operations
```yaml
Signature: Optional<Organisation> findById(Long organisationId)
Purpose: "Retrieve organisation entity for validation, context operations, and organisational information"

Business Logic Derivation:
  1. Used in UserService for organisation validation during user creation and user association operations
  2. Provides complete organisation entity for validation operations and organisational context validation
  3. Critical for user management operations requiring organisation validation and association
  4. Used in user lifecycle operations for organisation context validation and user-organisation relationship management
  5. Enables user management operations with organisation validation and organisational context for business logic

Common Usage Examples:
  - organisationRepository.findById(principalUser.getOrganisationId()) for user organisation validation in UserService
  - Organisation validation during user creation and user management operations
  - Organisation context validation for user-organisation relationship management
  - User lifecycle operations requiring organisation validation and context
  - Organisation information retrieval for user management and validation workflows

Transaction: Not Required
Error Handling: Returns Optional.empty() if organisation not found, requires null checking
```

### Pattern: save() for Organisation Lifecycle Management
```yaml
Signature: Organisation save(Organisation organisation)
Purpose: "Create new organisations, update organisation information, and manage organisation lifecycle"

Business Logic Derivation:
  1. Used for organisation creation with proper FQDN configuration and organisational setup
  2. Handles organisation information updates including name, FQDN, and archival operations for organisation maintenance
  3. Updates organisation settings for organisational changes and FQDN configuration updates
  4. Critical for organisation lifecycle management and organisational configuration operations
  5. Supports organisation operations with facility relationship management and organisational hierarchy maintenance

Common Usage Examples:
  - Creating new organisations with FQDN configuration and organisational setup
  - Updating organisation FQDN and name for organisational changes and rebranding
  - Organisation archival operations for organisation lifecycle management
  - Organisation configuration updates for organizational restructuring and management
  - Organisation information maintenance for business operational requirements

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations, unique constraint violations
```

### Pattern: findAll(specification) for Dynamic Organisation Discovery
```yaml
Signature: List<Organisation> findAll(Specification<Organisation> specification)
Purpose: "Dynamic organisation discovery with complex filtering for organisation management operations"

Business Logic Derivation:
  1. Used for advanced organisation search and listing operations with dynamic criteria in organisation management workflows
  2. Applies dynamic specifications for multi-criteria organisation filtering based on archival status, name, and other attributes
  3. Supports complex organisation discovery operations for organisation management and reporting
  4. Enables flexible organisation discovery and management operations with complex filtering for administrative operations
  5. Critical for organisation management APIs and organisational administration functionality requiring dynamic search capabilities

Common Usage Examples:
  - Dynamic organisation search with archival status and name filtering
  - Organisation discovery operations for reporting and administrative management
  - Complex organisation filtering for administrative operations and organizational reporting
  - Organisation management operations requiring dynamic search and filtering
  - Advanced organisation discovery for multi-tenant management and organizational analysis

Transaction: Not Required
Error Handling: Returns empty list if no organisations match criteria
```

## Key Repository Usage Patterns Summary

### Core Business Operations:
1. **Organisation Context Retrieval** - Used across all services for organisational scope and validation
2. **FQDN Operations** - Critical for notification routing and URL generation
3. **Performance Optimization** - getReferenceById for high-volume entity associations
4. **Validation Operations** - findById for organisation validation in user management
5. **Dynamic Discovery** - Specification-based search for organisation management

### Primary Use Cases:
1. **Notification Routing** - FQDN retrieval for notification delivery and URL generation
2. **Entity Associations** - Organisation context for entity creation and relationship management
3. **User Management** - Organisation validation and user-organisation relationship management
4. **Organisational Context** - Organisation scope validation across all business operations
5. **Administrative Operations** - Organisation management and multi-tenant operations

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAll(Specification), getReferenceById, getOne
  - existsById, count, findOne(Specification), count(Specification)

Transactional Methods:
  - save (only method actually used in codebase)

Unused Methods:
  - delete, deleteById (available but not used - organisations use archival via `archived` field instead)

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (name, fqdn)
    * Unique constraint violations on organisation name or FQDN
    * Invalid FQDN format or domain validation failures
  - EntityNotFoundException: Organisation not found by ID or criteria
  - OptimisticLockException: Concurrent organisation modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria
  - ResourceNotFoundException: Organisation not found during operations

Validation Rules:
  - name: Required, max length 255 characters, should be unique organisation identifier
  - archived: Defaults to false, used for soft deletion of organisations
  - fqdn: Required, must be valid fully qualified domain name for notification routing
  - facilities: Optional relationship, cascade operations for facility management

Business Constraints:
  - Organisation name should be unique across the system for clarity and identification
  - FQDN must be valid and accessible for notification delivery and URL generation
  - Organisation archival should be used instead of deletion for data integrity
  - FQDN changes affect all notification routing and URL generation for the organisation
  - Organisation deletion requires validation of dependent facilities, users, and entities
  - Multi-tenant operations must respect organisation-level security boundaries
  - Organisation context must be maintained for audit trail and compliance operations
  - FQDN must be properly configured for notification services and email delivery
  - Organisation hierarchy must be maintained for proper multi-tenant functionality
  - Administrative operations must respect organisation-level access controls and permissions
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Organisation repository without JPA/Hibernate dependencies, focusing on actual business layer usage patterns.
