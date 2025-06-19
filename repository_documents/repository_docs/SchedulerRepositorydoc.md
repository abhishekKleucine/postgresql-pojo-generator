# ISchedulerRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Scheduler
- **Primary Purpose**: Manages scheduler entities for automated job scheduling with recurrence rules, facility assignment, and checklist-based scheduling workflows
- **Key Relationships**: Central scheduling entity linking Checklist, Facility, UseCase, and Version with comprehensive scheduling configuration and lifecycle management
- **Performance Characteristics**: Moderate query volume with scheduler validation, checklist-based operations, and scheduling management workflows
- **Business Context**: Core scheduling component that manages automated job creation, recurring schedules, facility-scoped scheduling, and integration with Quartz scheduler for workflow automation

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | true | null |
| description | description | String | true | null |
| code | code | String | false | null |
| checklists_id | checklist.id | Long | false | null |
| checklists_name | checklistName | String | true | null |
| facilities_id | facility.id | Long | false | null |
| use_cases_id | useCase.id | Long | false | null |
| expected_start_date | expectedStartDate | Long | false | null |
| due_date_interval | dueDateInterval | Integer | true | null |
| due_date_duration | dueDateDuration | JsonNode | false | '{}' |
| is_repeated | repeated | boolean | false | false |
| recurrence_rule | recurrenceRule | String | true | null |
| is_custom_recurrence | customRecurrence | boolean | false | false |
| enabled | enabled | boolean | false | false |
| data | data | JsonNode | false | '{}' |
| versions_id | version.id | Long | true | null |
| archived | archived | boolean | false | false |
| state | state | State.Scheduler | false | null |
| deprecated_at | deprecatedAt | Long | true | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | checklist | Checklist | LAZY | Parent checklist for scheduling, not null, immutable |
| @ManyToOne | facility | Facility | LAZY | Target facility for scheduled jobs, not null, immutable |
| @ManyToOne | useCase | UseCase | LAZY | Use case context for scheduling, not null, immutable |
| @OneToOne | version | Version | LAZY | Version control for scheduler lifecycle, cascade = DETACH |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Scheduler entity)`
- `deleteById(Long id)`
- `delete(Scheduler entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<Scheduler> spec)`
- `findAll(Specification<Scheduler> spec, Pageable pageable)`
- `findAll(Specification<Scheduler> spec, Sort sort)`
- `findOne(Specification<Scheduler> spec)`
- `count(Specification<Scheduler> spec)`

### Custom Query Methods (3 methods - ALL methods documented)

- `findByChecklistId(Long checklistId)`
- `findByChecklistIdWhereSchedulerIsActive(Long checklistId)`
- `findAll(Specification<Scheduler> specification)` (override)

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Scheduler> findById(Long id)
List<Scheduler> findAll()
Scheduler save(Scheduler entity)
void deleteById(Long id)
void delete(Scheduler entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<Scheduler> findAll(Specification<Scheduler> spec)
Page<Scheduler> findAll(Specification<Scheduler> spec, Pageable pageable)
List<Scheduler> findAll(Specification<Scheduler> spec, Sort sort)
Optional<Scheduler> findOne(Specification<Scheduler> spec)
long count(Specification<Scheduler> spec)
```

### Custom Query Methods

#### Method: findByChecklistId(Long checklistId)
```yaml
Signature: List<Scheduler> findByChecklistId(Long checklistId)
Purpose: "Find all schedulers for a specific checklist for scheduler management and deprecation operations"

Business Logic Derivation:
  1. Used in SchedulerService for checklist-based scheduler retrieval during checklist lifecycle operations
  2. Enables bulk scheduler operations for checklist versioning and checklist deprecation workflows
  3. Critical for scheduler lifecycle management when checklists are revised, deprecated, or archived
  4. Used in scheduler deprecation workflows when new checklist versions supersede existing schedulers
  5. Supports checklist-scheduler relationship management with bulk scheduler operations for version control

SQL Query: |
  SELECT s.* FROM schedulers s
  WHERE s.checklists_id = ?
  ORDER BY s.created_at DESC

Parameters:
  - checklistId: Long (Checklist identifier to find schedulers for)

Returns: List<Scheduler> (all schedulers associated with the checklist)
Transaction: Not Required
Error Handling: Returns empty list if no schedulers found for checklist
```

#### Method: findByChecklistIdWhereSchedulerIsActive(Long checklistId)
```yaml
Signature: boolean findByChecklistIdWhereSchedulerIsActive(Long checklistId)
Purpose: "Check if active schedulers exist for checklist to prevent checklist archival and enforce business rules"

Business Logic Derivation:
  1. Used in ChecklistService for checklist archival validation ensuring no active schedulers prevent archival
  2. Validates that checklist archival operations don't break active scheduling workflows and job automation
  3. Critical for checklist lifecycle management enforcing business rules about active scheduler dependencies
  4. Used in checklist archival validation workflows to prevent data integrity issues with active schedules
  5. Enables checklist lifecycle validation with active scheduler dependency checking for business rule enforcement

SQL Query: |
  SELECT COUNT(*) > 0 FROM schedulers s
  WHERE s.checklists_id = ?
    AND s.enabled = true
    AND s.archived = false
    AND s.state IN ('PUBLISHED', 'BEING_BUILT')

Parameters:
  - checklistId: Long (Checklist identifier to check for active schedulers)

Returns: boolean (true if active schedulers exist for the checklist)
Transaction: Not Required
Error Handling: Returns false if no active schedulers found for checklist
```

#### Method: findAll(Specification<Scheduler> specification) - Override
```yaml
Signature: List<Scheduler> findAll(Specification<Scheduler> specification)
Purpose: "Enhanced specification-based scheduler discovery for calendar operations and scheduler management"

Business Logic Derivation:
  1. Used in SchedulerService for advanced scheduler filtering and calendar event generation workflows
  2. Provides enhanced specification support for complex scheduler filtering with facility and date range criteria
  3. Critical for calendar operations requiring scheduler filtering by facility, date range, and scheduler status
  4. Used in calendar event generation workflows for facility-specific scheduler discovery and timeline operations
  5. Enables advanced scheduler discovery with complex filtering for calendar integration and scheduler management

SQL Query: |
  SELECT s.* FROM schedulers s
  [WHERE clause based on dynamic specification criteria]
  [Typically filtering by facility, date ranges, enabled status, archived status]
  ORDER BY s.expected_start_date, s.created_at

Parameters:
  - specification: Specification<Scheduler> (Dynamic filtering criteria for scheduler discovery)

Returns: List<Scheduler> (schedulers matching specification criteria)
Transaction: Not Required
Error Handling: Returns empty list if no schedulers match specification criteria
```

### Key Repository Usage Patterns

#### Pattern: save() for Scheduler Lifecycle Management
```yaml
Usage: schedulerRepository.save(scheduler)
Purpose: "Create new schedulers, update scheduler configuration, and manage scheduler lifecycle"

Business Logic Derivation:
  1. Used extensively for scheduler creation with proper checklist, facility, and use case associations
  2. Handles scheduler configuration updates including recurrence rules, scheduling parameters, and state management
  3. Updates scheduler lifecycle information including enablement, archival, and deprecation operations
  4. Critical for scheduler lifecycle management and integration with Quartz scheduler system
  5. Supports scheduler operations with version control and comprehensive scheduling configuration management

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findById() for Scheduler Context Operations
```yaml
Usage: schedulerRepository.findById(schedulerId)
Purpose: "Retrieve scheduler entity for scheduler-specific operations and job creation workflows"

Business Logic Derivation:
  1. Used extensively for scheduler context retrieval in job creation, scheduler management, and Quartz integration
  2. Critical for scheduler validation, scheduler configuration access, and scheduler-specific business logic
  3. Used in job creation workflows, scheduler update operations, and Quartz job execution contexts
  4. Essential for scheduler context management and scheduler-based workflow operations
  5. Enables scheduler-centric operations with comprehensive scheduler information and relationship access

Transaction: Not Required
Error Handling: Throws ResourceNotFoundException if scheduler not found
```

#### Pattern: saveAll() for Bulk Scheduler Operations
```yaml
Usage: schedulerRepository.saveAll(schedulers)
Purpose: "Bulk scheduler updates for deprecation workflows and scheduler lifecycle management"

Business Logic Derivation:
  1. Used in SchedulerService for bulk scheduler deprecation during checklist versioning operations
  2. Enables efficient bulk scheduler updates for scheduler state transitions and lifecycle management
  3. Critical for scheduler deprecation workflows when checklist versions change or schedulers become obsolete
  4. Used in scheduler management operations requiring bulk state updates and scheduler lifecycle transitions
  5. Supports efficient bulk scheduler operations with transaction consistency for scheduler management workflows

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findAll(specification, pageable) for Dynamic Scheduler Discovery
```yaml
Usage: schedulerRepository.findAll(specification, pageable)
Purpose: "Dynamic scheduler discovery with complex filtering and pagination for scheduler management"

Business Logic Derivation:
  1. Used extensively in SchedulerService for advanced scheduler search and listing operations with pagination
  2. Applies dynamic specifications for multi-criteria scheduler filtering with facility, date, and status criteria
  3. Supports pagination for large scheduler datasets and scheduler management operations
  4. Enables flexible scheduler discovery and management operations with complex filtering for administrative operations
  5. Critical for scheduler management APIs and scheduler administration functionality requiring dynamic search capabilities

Transaction: Not Required
Error Handling: Returns empty page if no matches found
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Scheduler-Checklist Relationship Management
```yaml
Usage: findByChecklistId() and findByChecklistIdWhereSchedulerIsActive()
Purpose: "Manage scheduler-checklist relationships for version control and lifecycle operations"

Business Logic Derivation:
  1. Scheduler-checklist relationships are critical for checklist version control and scheduler lifecycle management
  2. Active scheduler validation prevents checklist archival ensuring scheduling integrity and business continuity
  3. Scheduler deprecation workflows coordinate with checklist versioning for proper version control
  4. Bulk scheduler operations enable efficient scheduler management during checklist lifecycle transitions
  5. Scheduler validation ensures business rule enforcement for checklist archival and version control operations

Common Usage Examples:
  - findByChecklistIdWhereSchedulerIsActive() in ChecklistService for checklist archival validation
  - findByChecklistId() in SchedulerService for bulk scheduler deprecation during checklist versioning
  - Scheduler lifecycle coordination with checklist version control and deprecation workflows
  - Active scheduler validation for business rule enforcement in checklist management operations
  - Bulk scheduler state management for checklist-scheduler relationship consistency

Transaction: Varies by operation
Error Handling: Business rule validation and scheduler lifecycle constraint enforcement
```

### Pattern: Quartz Integration Operations
```yaml
Usage: findById() for Quartz job execution and scheduler context
Purpose: "Provide scheduler context for Quartz job execution and automated job creation"

Business Logic Derivation:
  1. Scheduler entities provide configuration context for Quartz job execution and automated job scheduling
  2. Scheduler information enables proper job creation with checklist, facility, and scheduling parameter context
  3. Scheduler context supports Quartz integration for automated workflow execution and recurring job management
  4. Scheduler configuration drives job scheduling parameters and execution context for workflow automation
  5. Scheduler-job integration enables automated workflow execution with proper business context and scheduling rules

Common Usage Examples:
  - schedulerRepository.findById() in ProcessJob for Quartz job execution context and scheduler information
  - Scheduler context retrieval in CreateJobService for automated job creation from scheduling workflows
  - Scheduler configuration access for job scheduling parameters and execution context
  - Scheduler-based job automation with checklist and facility context for workflow execution
  - Quartz integration with scheduler configuration for automated job scheduling and execution

Transaction: Not Required
Error Handling: ResourceNotFoundException for missing schedulers in job execution context
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByChecklistId, findByChecklistIdWhereSchedulerIsActive
  - findAll(Specification), findAll(Specification, Pageable), existsById, count
  - findOne(Specification), count(Specification)

Transactional Methods:
  - save, delete, deleteById, saveAll

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid checklists_id, facilities_id, use_cases_id, versions_id)
    * NOT NULL constraint violations (code, checklists_id, facilities_id, use_cases_id, expectedStartDate, state)
    * Invalid enum values for state field
    * Unique constraint violations on scheduler code within facility scope
    * Invalid JSON format in dueDateDuration and data fields
  - EntityNotFoundException: Scheduler not found by ID or criteria
  - OptimisticLockException: Concurrent scheduler modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - ResourceNotFoundException: Scheduler not found during operations

Validation Rules:
  - name: Optional, max length 512 characters, descriptive scheduler name
  - description: Optional, text field for scheduler description
  - code: Required, max length 20 characters, unique scheduler identifier within facility scope
  - checklist: Required, must reference existing checklist, immutable after creation
  - facility: Required, must reference existing facility, immutable after creation
  - useCase: Required, must reference existing use case, immutable after creation
  - expectedStartDate: Required, must be valid future timestamp for scheduling
  - dueDateInterval: Optional, positive integer for due date calculation
  - dueDateDuration: Required, defaults to empty JSON object, must be valid JSON
  - repeated: Defaults to false, indicates if scheduler has recurring schedule
  - recurrenceRule: Optional, must be valid recurrence rule format for recurring schedules
  - customRecurrence: Defaults to false, indicates custom recurrence configuration
  - enabled: Defaults to false, controls scheduler activation and job creation
  - data: Required, defaults to empty JSON object, must be valid JSON
  - archived: Defaults to false, used for soft deletion of schedulers
  - state: Required, must be valid Scheduler enum value
  - deprecatedAt: Optional, must be valid timestamp for deprecation tracking

Business Constraints:
  - Cannot modify checklist, facility, or useCase associations after creation
  - Scheduler code must be unique within facility scope for identification
  - Expected start date must be in the future for new schedulers
  - Recurrence rule must be valid when isRepeated is true
  - Enabled schedulers must have valid configuration for job creation
  - Scheduler archival should be used instead of deletion for data integrity
  - Active schedulers prevent checklist archival for business rule enforcement
  - Scheduler deprecation must coordinate with checklist version control
  - Scheduler state transitions must follow defined lifecycle progression
  - JSON fields (dueDateDuration, data) must contain valid JSON for configuration
  - Quartz integration requires proper scheduler configuration and enabled status
  - Scheduler-job relationships must maintain consistency for automation workflows
  - Version control must be maintained for scheduler lifecycle and deprecation
  - Facility-level scheduler access must respect organizational security boundaries
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Scheduler repository without JPA/Hibernate dependencies.
