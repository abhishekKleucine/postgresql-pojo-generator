# IFacilityRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Facility
- **Primary Purpose**: Manages facility entities representing physical locations/sites with timezone, formatting preferences, and organizational scope
- **Key Relationships**: Central organizational entity linking to Organisation with facility-scoped operations throughout the system
- **Performance Characteristics**: Moderate query volume with facility context retrieval, timezone operations, and facility validation
- **Business Context**: Core organizational component that provides facility context, timezone information, and localization settings for all facility-scoped operations

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | true | null |
| archived | archived | boolean | false | false |
| organisations_id | organisation.id | Long | false | null |
| time_zone | timeZone | String | false | null |
| date_format | dateFormat | String | false | null |
| time_format | timeFormat | String | false | null |
| date_time_format | dateTimeFormat | String | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | organisation | Organisation | LAZY | Parent organisation, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Facility entity)`
- `deleteById(Long id)`
- `delete(Facility entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<Facility> spec)`
- `findAll(Specification<Facility> spec, Pageable pageable)`
- `findAll(Specification<Facility> spec, Sort sort)`
- `findOne(Specification<Facility> spec)`
- `count(Specification<Facility> spec)`

### Custom Query Methods
None - This repository only extends JpaRepository and JpaSpecificationExecutor with no custom methods.

## Method Documentation (Actual Usage Patterns)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Facility> findById(Long id)
List<Facility> findAll()
Facility save(Facility entity)
void deleteById(Long id)
void delete(Facility entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<Facility> findAll(Specification<Facility> spec)
Page<Facility> findAll(Specification<Facility> spec, Pageable pageable)
List<Facility> findAll(Specification<Facility> spec, Sort sort)
Optional<Facility> findOne(Specification<Facility> spec)
long count(Specification<Facility> spec)
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: findById() for Facility Context and Timezone Operations
```yaml
Signature: Optional<Facility> findById(Long facilityId)
Purpose: "Retrieve facility entity for timezone information, context validation, and localization settings"

Business Logic Derivation:
  1. Used extensively throughout system for facility context retrieval and timezone operations in JobService, ParameterExceptionService, and CorrectionService
  2. Critical for date/time formatting operations using facility-specific timezone and format settings in job processing and parameter execution
  3. Used in job operations, parameter execution, scheduling, and audit operations for timezone context and localization
  4. Essential for user context validation ensuring users operate within their assigned facility scope across all business operations
  5. Enables localization operations with facility-specific date/time formatting and timezone conversion for consistent user experience

Common Usage Examples:
  - facilityRepository.findById(principalUser.getCurrentFacilityId()) for user context validation in JobService and workflow operations
  - facility.getTimeZone() for timezone-aware date/time operations in scheduling and job processing
  - facility.getDateFormat() for consistent date formatting across operations in reports and job displays
  - Facility validation in job creation, parameter execution, and workflow operations
  - Exception handling operations requiring facility context for proper timezone and formatting

Transaction: Not Required
Error Handling: ResourceNotFoundException when facility not found, InvalidFacilityException for access validation
```

### Pattern: getReferenceById() for Performance-Optimized Facility Context
```yaml
Signature: Facility getReferenceById(Long facilityId)
Purpose: "Get facility reference for performance-optimized operations without full entity loading"

Business Logic Derivation:
  1. Used extensively for performance-optimized facility context retrieval in high-volume operations across JobService, TaskExecutionService, and ParameterExecutionService
  2. Provides facility reference for timezone and formatting operations without loading full entity in job processing and parameter execution workflows
  3. Critical for job processing, parameter execution, and audit operations requiring facility context with minimal performance overhead
  4. Used in user group operations, scheduling, action creation, and workflow processing for efficient facility access without database overhead
  5. Enables efficient facility context operations with minimal database interaction for performance optimization in high-frequency operations

Common Usage Examples:
  - facilityRepository.getReferenceById(principalUser.getCurrentFacilityId()) for efficient context access in JobService and workflow operations
  - Timezone retrieval for date/time operations in job processing, parameter execution, and scheduling operations
  - Facility context in user group creation, action creation, and workflow operations for efficient context management
  - Audit operations requiring facility information for logging and reporting without performance overhead
  - Scheduler operations requiring facility timezone for accurate scheduling and job processing

Transaction: Not Required
Error Handling: EntityNotFoundException when facility reference not found
```

### Pattern: getOne() for Legacy Facility Operations
```yaml
Signature: Facility getOne(Long facilityId)
Purpose: "Legacy facility retrieval for older operations requiring full entity loading"

Business Logic Derivation:
  1. Used in legacy operations for facility entity retrieval with full entity loading in ChecklistService and CustomViewService operations
  2. Provides complete facility entity for operations requiring all facility information including organization relationships
  3. Used in checklist operations, custom view operations, and user group audit operations that need comprehensive facility data
  4. Critical for operations that need complete facility data including organization relationships for business logic validation
  5. Enables comprehensive facility operations with full entity context for complex business logic requiring all facility attributes

Common Usage Examples:
  - facilityRepository.getOne(facilityId) in checklist creation and custom view operations requiring full facility context
  - User group audit operations requiring complete facility information for audit trail and reporting
  - Custom view creation operations requiring facility context and validation with organization relationship access
  - Legacy operations that require full facility entity loading for backward compatibility
  - Operations requiring facility-organization relationship access for comprehensive business logic

Transaction: Not Required
Error Handling: EntityNotFoundException when facility not found
```

### Pattern: findAllById() for Bulk Facility Operations
```yaml
Signature: List<Facility> findAllById(Iterable<Long> facilityIds)
Purpose: "Bulk facility retrieval for multi-facility operations and facility assignment validation"

Business Logic Derivation:
  1. Used in ChecklistService for bulk facility assignment operations and facility validation during checklist assignment workflows
  2. Enables efficient bulk facility loading for checklist assignment and facility management operations
  3. Critical for multi-facility operations requiring facility validation and assignment in checklist and workflow management
  4. Used in facility assignment operations for checklists and multi-facility workflow management requiring bulk validation
  5. Supports efficient bulk facility operations for facility management and assignment validation with minimal database queries

Common Usage Examples:
  - facilityRepository.findAllById(assignedIds) for checklist facility assignment validation in ChecklistService
  - Bulk facility loading for multi-facility checklist assignments and workflow scope validation
  - Facility validation operations for user assignments and workflow scope validation across multiple facilities
  - Multi-facility operations requiring bulk facility entity loading for comprehensive validation
  - Facility assignment validation for checklists, user groups, and workflow operations requiring multi-facility context

Transaction: Not Required
Error Handling: Returns empty list if no facilities found with specified IDs
```

### Pattern: save() for Facility Lifecycle Management
```yaml
Signature: Facility save(Facility facility)
Purpose: "Create new facilities, update facility information, and manage facility lifecycle"

Business Logic Derivation:
  1. Used for facility creation with proper organization association and configuration in facility management operations
  2. Handles facility information updates including timezone, formatting, and archival operations for facility maintenance
  3. Updates facility settings for localization, timezone configuration, and organizational changes in facility administration
  4. Critical for facility lifecycle management and facility configuration operations maintaining organizational structure
  5. Supports facility operations with organization association management and configuration updates for business continuity

Common Usage Examples:
  - Creating new facilities with organization association and timezone configuration
  - Updating facility timezone and formatting settings for localization changes
  - Facility archival operations for facility lifecycle management
  - Facility configuration updates for organizational restructuring
  - Facility information maintenance for business operational requirements

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations, unique constraint violations
```

### Pattern: findAll(specification) for Dynamic Facility Discovery
```yaml
Signature: List<Facility> findAll(Specification<Facility> specification)
Purpose: "Dynamic facility discovery with complex filtering for facility management operations"

Business Logic Derivation:
  1. Used for advanced facility search and listing operations with dynamic criteria in facility management workflows
  2. Applies dynamic specifications for multi-criteria facility filtering based on organization, archival status, and other attributes
  3. Supports complex facility discovery operations for facility management and organizational structure operations
  4. Enables flexible facility discovery and management operations with complex filtering for administrative operations
  5. Critical for facility management APIs and facility administration functionality requiring dynamic search capabilities

Common Usage Examples:
  - Dynamic facility search with organization and archival status filtering
  - Facility discovery operations for organizational reporting and management
  - Complex facility filtering for administrative operations and reporting
  - Facility management operations requiring dynamic search and filtering
  - Advanced facility discovery for organizational structure analysis and reporting

Transaction: Not Required
Error Handling: Returns empty list if no facilities match criteria
```

## Key Repository Usage Patterns Summary

### Core Business Operations:
1. **Facility Context Retrieval** - Used in all major services for timezone and localization
2. **Performance Optimization** - getReferenceById for high-volume operations
3. **Legacy Support** - getOne for backward compatibility operations
4. **Bulk Operations** - findAllById for multi-facility assignment validation
5. **Dynamic Discovery** - Specification-based search for facility management

### Primary Use Cases:
1. **Timezone Operations** - Job processing, scheduling, parameter execution
2. **User Context Validation** - Ensuring facility-scoped access control
3. **Localization** - Date/time formatting for user interface operations
4. **Audit Operations** - Facility context for logging and reporting
5. **Workflow Management** - Facility-scoped workflow processing and validation

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAll(Specification), getReferenceById, getOne
  - findAllById, existsById, count, findOne(Specification), count(Specification)

Transactional Methods:
  - save (only method actually used in codebase)

Unused Methods:
  - delete, deleteById (available but not used - facilities use archival via `archived` field instead)

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid organisations_id)
    * NOT NULL constraint violations (organisations_id, timeZone, dateFormat, timeFormat, dateTimeFormat)
    * Unique constraint violations on facility name within organization scope
  - EntityNotFoundException: Facility not found by ID or criteria
  - OptimisticLockException: Concurrent facility modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria
  - ResourceNotFoundException: Facility not found during operations

Validation Rules:
  - name: Optional, max length 255 characters, should be descriptive facility identifier
  - archived: Defaults to false, used for soft deletion of facilities
  - organisation: Required, must reference existing organisation, immutable after creation
  - timeZone: Required, must be valid timezone identifier (e.g., "America/New_York")
  - dateFormat: Required, must be valid date format pattern (e.g., "MM/dd/yyyy")
  - timeFormat: Required, must be valid time format pattern (e.g., "HH:mm:ss")
  - dateTimeFormat: Required, must be valid datetime format pattern (e.g., "MM/dd/yyyy HH:mm:ss")

Business Constraints:
  - Cannot modify organisation association after facility creation
  - Timezone must be valid and supported by the system for accurate date/time operations
  - Date/time format patterns must be valid and consistent for localization operations
  - Facility archival should be used instead of deletion for data integrity
  - Facility names should be unique within organisation scope for clarity
  - Timezone changes affect all existing jobs, schedules, and time-sensitive operations
  - Format pattern changes affect display consistency across all facility operations
  - Facility deletion requires validation of dependent jobs, users, and workflows
  - User access to facilities must respect organization-level security boundaries
  - Facility context must be maintained for audit trail and compliance operations
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Facility repository without JPA/Hibernate dependencies, focusing on actual business layer usage patterns.
