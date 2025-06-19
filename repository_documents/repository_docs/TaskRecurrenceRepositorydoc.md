# ITaskRecurrenceRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TaskRecurrence (extends UserAuditIdentifiableBase)
- **Primary Purpose**: Manages task recurrence entities for recurring task configuration with interval management, tolerance settings, and recurrence scheduling functionality
- **Key Relationships**: One-to-One relationship with Task entity for comprehensive task recurrence configuration and scheduling management
- **Performance Characteristics**: Low query volume with recurrence configuration, interval management operations, and task scheduling
- **Business Context**: Task scheduling component that provides recurring task configuration, interval management, tolerance settings, and task recurrence functionality for scheduled task workflows

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditIdentifiableBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| start_date_interval | startDateInterval | Integer | true | null | Start date interval value |
| start_date_duration | startDateDuration | JsonNode | true | '{}' | Start date duration configuration (JSONB) |
| positive_start_date_tolerance_interval | positiveStartDateToleranceInterval | Integer | true | null | Positive start date tolerance interval |
| positive_start_date_tolerance_duration | positiveStartDateToleranceDuration | JsonNode | true | '{}' | Positive start date tolerance duration (JSONB) |
| negative_start_date_tolerance_interval | negativeStartDateToleranceInterval | Integer | true | null | Negative start date tolerance interval |
| negative_start_date_tolerance_duration | negativeStartDateToleranceDuration | JsonNode | true | '{}' | Negative start date tolerance duration (JSONB) |
| due_date_interval | dueDateInterval | Integer | true | null | Due date interval value |
| due_date_duration | dueDateDuration | JsonNode | true | '{}' | Due date duration configuration (JSONB) |
| positive_due_date_tolerance_interval | positiveDueDateToleranceInterval | Integer | true | null | Positive due date tolerance interval |
| positive_due_date_tolerance_duration | positiveDueDateToleranceDuration | JsonNode | true | '{}' | Positive due date tolerance duration (JSONB) |
| negative_due_date_tolerance_interval | negativeDueDateToleranceInterval | Integer | true | null | Negative due date tolerance interval |
| negative_due_date_tolerance_duration | negativeDueDateToleranceDuration | JsonNode | true | '{}' | Negative due date tolerance duration (JSONB) |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @OneToOne | task | Task | LAZY | Associated task, mapped by taskRecurrence |

## Available Repository Methods

### Standard CRUD Methods (ALL methods - No custom methods defined)

**Basic CRUD Operations:**
- `findById(Long id)`
- `findAll()`
- `findAllById(Iterable<Long> ids)`
- `save(TaskRecurrence entity)`
- `saveAll(Iterable<TaskRecurrence> entities)`
- `deleteById(Long id)`
- `delete(TaskRecurrence entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods
**No custom query methods defined - Repository uses only standard JpaRepository methods**

## Method Documentation (Standard CRUD Methods - Based on Actual Usage)

### Standard CRUD Operations
```java
// Standard JpaRepository methods - ALL documented based on actual usage
Optional<TaskRecurrence> findById(Long id)
List<TaskRecurrence> findAll()
List<TaskRecurrence> findAllById(Iterable<Long> ids)
TaskRecurrence save(TaskRecurrence entity)
List<TaskRecurrence> saveAll(Iterable<TaskRecurrence> entities)
void deleteById(Long id)
void delete(TaskRecurrence entity)
boolean existsById(Long id)
long count()
```

### Key Repository Usage Patterns Based on Actual Service Usage

#### Pattern: findById() for Task Recurrence Retrieval
```yaml
Usage: taskRecurrenceRepository.findById(taskRecurrenceId)
Purpose: "Retrieve task recurrence configuration for recurring task management and schedule validation"

Business Logic Derivation:
  1. Used extensively in TaskService for task recurrence retrieval during task configuration and recurrence management operations
  2. Provides recurrence configuration access for task workflows enabling comprehensive recurrence management and task functionality
  3. Critical for task configuration operations requiring recurrence access for task management and scheduling control
  4. Used in task configuration workflows for accessing recurrence settings for configuration operations and task processing
  5. Enables task configuration with recurrence access for comprehensive task processing and scheduling control

SQL Query: |
  SELECT tr FROM TaskRecurrence tr WHERE tr.id = ?

Parameters:
  - id: Long (Task recurrence identifier for configuration retrieval)

Returns: Optional<TaskRecurrence> (task recurrence if found, empty otherwise)
Transaction: Required (@Transactional annotation at repository level)
Error Handling: ResourceNotFoundException when task recurrence not found for specified ID
```

#### Pattern: save() for Task Recurrence Configuration
```yaml
Usage: taskRecurrenceRepository.save(taskRecurrence)
Purpose: "Create and update task recurrence configurations for recurring task setup and management"

Business Logic Derivation:
  1. Used in TaskService for task recurrence persistence during task recurrence configuration and scheduling setup operations
  2. Provides recurrence configuration persistence for scheduling workflows enabling comprehensive recurrence management and task functionality
  3. Critical for task scheduling operations requiring recurrence persistence for scheduling management and task control
  4. Used in task scheduling workflows for recurrence configuration persistence and scheduling management operations
  5. Enables task scheduling with recurrence persistence for comprehensive scheduling processing and task control

Transaction: Required (@Transactional annotation at repository level)
Error Handling: DataIntegrityViolationException for constraint violations, recurrence configuration issues
```

#### Pattern: saveAll() for Bulk Recurrence Management
```yaml
Usage: taskRecurrenceRepository.saveAll(taskRecurrenceList)
Purpose: "Create task recurrences in bulk for checklist revision and bulk scheduling setup"

Business Logic Derivation:
  1. Used in ChecklistRevisionService for bulk task recurrence creation during checklist revision and bulk scheduling operations
  2. Provides efficient bulk recurrence persistence for revision workflows enabling comprehensive recurrence creation and revision functionality
  3. Critical for revision operations requiring bulk recurrence creation for revision management and scheduling control
  4. Used in revision workflows for bulk recurrence creation and scheduling setup operations
  5. Enables revision management with efficient bulk operations for comprehensive revision processing and scheduling control

Transaction: Required (@Transactional annotation at repository level)
Error Handling: DataIntegrityViolationException for bulk constraint violations, recurrence configuration issues
```

#### Pattern: findAllById() for Bulk Recurrence Retrieval
```yaml
Usage: taskRecurrenceRepository.findAllById(recurrenceIds)
Purpose: "Retrieve multiple task recurrences for bulk recurrence management and revision processing"

Business Logic Derivation:
  1. Used in ChecklistRevisionService for bulk task recurrence retrieval during checklist revision and recurrence copying operations
  2. Provides efficient bulk recurrence access for revision workflows enabling comprehensive recurrence retrieval and revision functionality
  3. Critical for revision operations requiring bulk recurrence access for revision management and recurrence control
  4. Used in revision workflows for bulk recurrence retrieval and revision processing operations
  5. Enables revision management with efficient bulk access for comprehensive revision processing and recurrence control

Transaction: Required (@Transactional annotation at repository level)
Error Handling: Returns empty list for non-existent recurrence IDs
```

#### Pattern: deleteById() for Recurrence Cleanup
```yaml
Usage: taskRecurrenceRepository.deleteById(taskRecurrenceId)
Purpose: "Delete task recurrence configuration for task cleanup and recurrence removal"

Business Logic Derivation:
  1. Used in TaskService for task recurrence cleanup during task recurrence removal and cleanup operations
  2. Provides recurrence removal for cleanup workflows enabling comprehensive recurrence cleanup and task functionality
  3. Critical for task cleanup operations requiring recurrence removal for cleanup management and task control
  4. Used in task cleanup workflows for recurrence removal and cleanup operations
  5. Enables task cleanup with recurrence removal for comprehensive cleanup processing and task control

Transaction: Required (@Transactional annotation at repository level)
Error Handling: EmptyResultDataAccessException if recurrence not found for deletion
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Task Recurrence Configuration Management
```yaml
Usage: Complete task recurrence configuration for recurring task management and scheduling functionality
Purpose: "Manage task recurrence configurations for comprehensive scheduling functionality and recurrence processing"

Business Logic Derivation:
  1. Task recurrence configuration management provides scheduling functionality through recurrence creation, configuration management, and scheduling operations
  2. Recurrence lifecycle includes configuration creation, schedule management, and recurrence removal for scheduling control
  3. Recurrence management operations require scheduling processing for recurrence lifecycle and configuration control
  4. Scheduling operations enable comprehensive recurrence functionality with configuration capabilities and management
  5. Recurrence lifecycle management supports scheduling requirements and functionality for task recurrence processing

Common Usage Examples:
  - taskRecurrenceRepository.findById() for recurrence configuration retrieval in TaskService
  - taskRecurrenceRepository.save() for recurrence configuration persistence
  - taskRecurrenceRepository.deleteById() for recurrence cleanup during task management
  - taskRecurrenceRepository.saveAll() for bulk recurrence creation during checklist revision
  - taskRecurrenceRepository.findAllById() for bulk recurrence retrieval during revision

Transaction: Required (repository-level @Transactional annotation)
Error Handling: Task recurrence configuration error handling and scheduling validation verification
```

### Pattern: Checklist Revision and Bulk Operations
```yaml
Usage: Checklist revision and bulk operations for task recurrence copying and revision functionality
Purpose: "Manage checklist revisions for comprehensive revision functionality and recurrence processing"

Business Logic Derivation:
  1. Checklist revision management operations require comprehensive task recurrence access for revision-level recurrence management and scheduling functionality
  2. Revision management supports scheduling requirements and functionality for recurrence processing workflows
  3. Revision-level recurrence operations ensure proper scheduling functionality through recurrence management and revision control
  4. Revision workflows coordinate recurrence management with revision processing for comprehensive revision operations
  5. Recurrence management supports revision requirements and functionality for comprehensive checklist revision management

Common Usage Examples:
  - taskRecurrenceRepository.findAllById() for bulk recurrence retrieval during revision
  - taskRecurrenceRepository.saveAll() for bulk recurrence creation in revised checklists
  - Recurrence copying and configuration transfer during checklist revision
  - Bulk recurrence management for revision processing

Transaction: Required for revision operations
Error Handling: Revision error handling and recurrence validation verification
```

### Pattern: Task Lifecycle and Scheduling Management
```yaml
Usage: Task lifecycle and scheduling management for task configuration and recurrence functionality
Purpose: "Manage task lifecycle for comprehensive scheduling functionality and recurrence processing"

Business Logic Derivation:
  1. Task lifecycle management operations require comprehensive task recurrence access for lifecycle-level scheduling management and recurrence functionality
  2. Lifecycle management supports scheduling requirements and functionality for recurrence processing workflows
  3. Lifecycle-level scheduling operations ensure proper recurrence functionality through recurrence management and lifecycle control
  4. Task workflows coordinate lifecycle management with scheduling processing for comprehensive task operations
  5. Lifecycle management supports scheduling requirements and functionality for comprehensive task lifecycle management

Common Usage Examples:
  - Task recurrence configuration during task setup and configuration
  - Recurrence schedule management for recurring task execution
  - Task recurrence removal during task cleanup and deletion
  - Scheduling configuration with tolerance and interval management

Transaction: Required for lifecycle management operations
Error Handling: Task lifecycle error handling and recurrence validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllById, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById

Repository-Level Transaction: @Transactional(rollbackFor = Exception.class)
Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Constraint violations for recurrence configuration
    * Invalid JSON data in JSONB fields
    * Task recurrence integrity constraint violations
  - EntityNotFoundException: Task recurrence not found by ID or criteria
  - OptimisticLockException: Concurrent task recurrence modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or recurrence context
  - ConstraintViolationException: Task recurrence constraint violations
  - ResourceNotFoundException: Task recurrence not found (used in service layer)

Validation Rules:
  - startDateInterval: Optional, positive integer for start date interval
  - startDateDuration: Optional, valid JSON configuration for start date duration
  - positiveStartDateToleranceInterval: Optional, positive integer for tolerance interval
  - positiveStartDateToleranceDuration: Optional, valid JSON configuration for tolerance duration
  - negativeStartDateToleranceInterval: Optional, positive integer for tolerance interval
  - negativeStartDateToleranceDuration: Optional, valid JSON configuration for tolerance duration
  - dueDateInterval: Optional, positive integer for due date interval
  - dueDateDuration: Optional, valid JSON configuration for due date duration
  - positiveDueDateToleranceInterval: Optional, positive integer for tolerance interval
  - positiveDueDateToleranceDuration: Optional, valid JSON configuration for tolerance duration
  - negativeDueDateToleranceInterval: Optional, positive integer for tolerance interval
  - negativeDueDateToleranceDuration: Optional, valid JSON configuration for tolerance duration

Business Constraints:
  - Task recurrence configurations must support recurring task requirements and functionality
  - JSON duration configurations must be valid for scheduling calculations
  - Interval values must be positive integers for proper scheduling
  - Tolerance settings must support flexible scheduling requirements
  - Task recurrence lifecycle management must maintain referential integrity and scheduling functionality consistency
  - Recurrence associations must support task requirements and functionality for scheduling processing
  - Recurrence operations must maintain transaction consistency and constraint integrity for scheduling management
  - Scheduling configuration must maintain recurrence functionality and consistency
  - Task relationship must maintain one-to-one integrity for proper task association
  - JSONB fields must contain valid JSON data for duration and tolerance configurations
```

## Task Recurrence Management Considerations

### Recurrence Configuration Integration
```yaml
Scheduling Configuration: Task recurrences enable scheduling functionality through interval configuration and duration management
Configuration Management: Task recurrence configuration enables scheduling functionality with comprehensive recurrence capabilities
Recurrence Lifecycle: Task recurrence lifecycle includes creation, configuration updates, and removal operations for scheduling functionality
Scheduling Management: Comprehensive scheduling management for task recurrence functionality and scheduling requirements during scheduling workflows
Schedule Control: Task recurrence schedule control for scheduling functionality and lifecycle management in scheduling processing
```

### Interval and Duration Management
```yaml
Interval Configuration: Task recurrence intervals enable scheduling functionality through start date and due date interval management
Duration Management: Task recurrence durations with JSON configuration and comprehensive duration functionality
Tolerance Settings: Task recurrence tolerances with positive and negative tolerance configuration and comprehensive tolerance functionality
Schedule Flexibility: Task recurrence flexibility with tolerance and interval configuration and comprehensive flexibility functionality
Time Management: Task recurrence time management with interval and duration coordination and comprehensive time functionality for scheduling workflows
```

### Task Integration and Scheduling
```yaml
Task Association: Task recurrences enable task functionality through one-to-one task association and recurrence management
Recurring Tasks: Task recurrence configuration enables recurring task execution and comprehensive recurring functionality
Schedule Execution: Task recurrence schedule execution with interval-based scheduling and comprehensive execution functionality
Task Scheduling: Task recurrence task scheduling with configuration coordination and comprehensive scheduling functionality
Recurrence Control: Task recurrence recurrence control with task integration and comprehensive control functionality for scheduling workflows
```

### JSONB Configuration Management
```yaml
JSON Configuration: Task recurrence JSONB fields enable flexible configuration with duration and tolerance settings
Configuration Flexibility: JSONB configuration provides flexible recurrence settings and comprehensive configuration functionality
Duration Configuration: JSON duration configuration enables complex scheduling scenarios and comprehensive duration functionality
Tolerance Configuration: JSON tolerance configuration enables flexible scheduling tolerance and comprehensive tolerance functionality
Schema Evolution: JSONB fields support configuration schema evolution and comprehensive schema functionality for recurrence workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TaskRecurrence repository without JPA/Hibernate dependencies, focusing on recurring task configuration and scheduling management patterns.
