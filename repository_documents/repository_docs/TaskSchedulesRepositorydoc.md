# ITaskSchedulesRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TaskSchedules (extends UserAuditIdentifiableBase)
- **Primary Purpose**: Manages task schedule entities for conditional task scheduling with task dependency scheduling, condition-based execution, and schedule configuration functionality
- **Key Relationships**: Many-to-One relationship with Task entity for comprehensive task scheduling and dependency management
- **Performance Characteristics**: Medium query volume with schedule retrieval, condition-based filtering, and task scheduling operations
- **Business Context**: Task scheduling component that provides conditional task scheduling, dependency-based execution, schedule configuration, and task timing functionality for workflow execution scheduling

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditIdentifiableBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| type | type | ScheduledTaskType | false | null | Schedule type enum (max 50 chars) |
| referenced_tasks_id | referencedTaskId / referencedTask.id | Long | false | null | Foreign key to referenced tasks, not updatable |
| condition | condition | ScheduledTaskCondition | false | null | Schedule condition enum (max 50 chars) |
| start_date_duration | startDateDuration | JsonNode | false | '{}' | Start date duration configuration (JSONB) |
| start_date_interval | startDateInterval | Integer | true | null | Start date interval value |
| due_date_duration | dueDateDuration | JsonNode | false | '{}' | Due date duration configuration (JSONB) |
| due_date_interval | dueDateInterval | Integer | true | null | Due date interval value |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | referencedTask | Task | LAZY | Referenced task for scheduling, not null, not updatable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `findAllById(Iterable<Long> ids)`
- `save(TaskSchedules entity)`
- `saveAll(Iterable<TaskSchedules> entities)`
- `deleteById(Long id)`
- `delete(TaskSchedules entity)`
- `existsById(Long id)`
- `count()`
- `getReferenceById(Long id)`

### Custom Query Methods (1 method - ALL methods documented)

**Task Schedule Retrieval Methods (1 method):**
- `findByReferencedTaskIdAndCondition(Long referencedTaskId, Type.ScheduledTaskCondition condition)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<TaskSchedules> findById(Long id)
List<TaskSchedules> findAll()
List<TaskSchedules> findAllById(Iterable<Long> ids)
TaskSchedules save(TaskSchedules entity)
List<TaskSchedules> saveAll(Iterable<TaskSchedules> entities)
void deleteById(Long id)
void delete(TaskSchedules entity)
boolean existsById(Long id)
long count()
TaskSchedules getReferenceById(Long id)
```

### Task Schedule Retrieval Methods

#### Method: findByReferencedTaskIdAndCondition(Long referencedTaskId, Type.ScheduledTaskCondition condition)
```yaml
Signature: List<TaskSchedules> findByReferencedTaskIdAndCondition(Long referencedTaskId, Type.ScheduledTaskCondition condition)
Purpose: "Find task schedules by referenced task ID and condition for conditional task scheduling and dependency execution"

Business Logic Derivation:
  1. Used extensively in TaskExecutionService for conditional task scheduling during task execution and dependency management operations
  2. Provides condition-based schedule access for execution workflows enabling comprehensive schedule management and execution functionality
  3. Critical for execution operations requiring conditional scheduling for execution management and dependency control
  4. Used in execution workflows for accessing conditional schedules for scheduling operations and execution processing
  5. Enables execution management with conditional scheduling for comprehensive execution processing and dependency control

SQL Query: |
  SELECT ts FROM TaskSchedules ts 
  WHERE ts.referencedTaskId = ? AND ts.condition = ?

Parameters:
  - referencedTaskId: Long (Referenced task identifier for schedule retrieval)
  - condition: Type.ScheduledTaskCondition (Schedule condition for conditional filtering)

Returns: List<TaskSchedules> (task schedules for referenced task and condition)
Transaction: Not Required (read operation)
Error Handling: Returns empty list if no schedules found for referenced task and condition
```

### Key Repository Usage Patterns Based on Actual Service Usage

#### Pattern: getReferenceById() for Schedule Reference
```yaml
Usage: taskSchedulesRepository.getReferenceById(taskSchedulesId)
Purpose: "Get task schedule reference for job execution and schedule association"

Business Logic Derivation:
  1. Used in JobService for task schedule reference retrieval during job execution and schedule association operations
  2. Provides schedule reference access for job workflows enabling comprehensive schedule association and job functionality
  3. Critical for job execution operations requiring schedule reference for job management and execution control
  4. Used in job execution workflows for accessing schedule references for association operations and job processing
  5. Enables job execution with schedule reference for comprehensive job processing and execution control

Transaction: Not Required (reference retrieval)
Error Handling: EntityNotFoundException when task schedule not found for specified ID
```

#### Pattern: save() for Schedule Configuration
```yaml
Usage: taskSchedulesRepository.save(taskSchedules)
Purpose: "Create and update task schedules for task scheduling setup and configuration"

Business Logic Derivation:
  1. Used in TaskService for task schedule persistence during task scheduling configuration and setup operations
  2. Provides schedule persistence for scheduling workflows enabling comprehensive schedule management and task functionality
  3. Critical for task scheduling operations requiring schedule persistence for scheduling management and task control
  4. Used in task scheduling workflows for schedule persistence and configuration operations
  5. Enables task scheduling with schedule persistence for comprehensive scheduling processing and task control

Transaction: Not Required (JpaRepository handles transactions)
Error Handling: DataIntegrityViolationException for constraint violations, schedule configuration issues
```

#### Pattern: saveAll() for Bulk Schedule Management
```yaml
Usage: taskSchedulesRepository.saveAll(taskSchedulesList)
Purpose: "Create task schedules in bulk for checklist revision and bulk scheduling setup"

Business Logic Derivation:
  1. Used in ChecklistRevisionService for bulk task schedule creation during checklist revision and bulk scheduling operations
  2. Provides efficient bulk schedule persistence for revision workflows enabling comprehensive schedule creation and revision functionality
  3. Critical for revision operations requiring bulk schedule creation for revision management and scheduling control
  4. Used in revision workflows for bulk schedule creation and scheduling setup operations
  5. Enables revision management with efficient bulk operations for comprehensive revision processing and scheduling control

Transaction: Not Required (JpaRepository handles transactions)
Error Handling: DataIntegrityViolationException for bulk constraint violations, schedule configuration issues
```

#### Pattern: findAllById() for Bulk Schedule Retrieval
```yaml
Usage: taskSchedulesRepository.findAllById(scheduleIds)
Purpose: "Retrieve multiple task schedules for bulk schedule management and revision processing"

Business Logic Derivation:
  1. Used in ChecklistRevisionService for bulk task schedule retrieval during checklist revision and schedule copying operations
  2. Provides efficient bulk schedule access for revision workflows enabling comprehensive schedule retrieval and revision functionality
  3. Critical for revision operations requiring bulk schedule access for revision management and schedule control
  4. Used in revision workflows for bulk schedule retrieval and revision processing operations
  5. Enables revision management with efficient bulk access for comprehensive revision processing and schedule control

Transaction: Not Required (read operation)
Error Handling: Returns empty list for non-existent schedule IDs
```

#### Pattern: deleteById() for Schedule Cleanup
```yaml
Usage: taskSchedulesRepository.deleteById(scheduleId)
Purpose: "Delete task schedule for task cleanup and schedule removal"

Business Logic Derivation:
  1. Used in TaskService for task schedule cleanup during task schedule removal and cleanup operations
  2. Provides schedule removal for cleanup workflows enabling comprehensive schedule cleanup and task functionality
  3. Critical for task cleanup operations requiring schedule removal for cleanup management and task control
  4. Used in task cleanup workflows for schedule removal and cleanup operations
  5. Enables task cleanup with schedule removal for comprehensive cleanup processing and task control

Transaction: Not Required (JpaRepository handles transactions)
Error Handling: EmptyResultDataAccessException if schedule not found for deletion
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Conditional Task Scheduling and Execution
```yaml
Usage: Conditional task scheduling for execution control and dependency management functionality
Purpose: "Control task execution for comprehensive scheduling functionality and conditional processing"

Business Logic Derivation:
  1. Conditional task scheduling management provides execution functionality through condition-based scheduling, dependency management, and execution control operations
  2. Scheduling lifecycle includes schedule creation, condition evaluation, and execution management for scheduling control
  3. Scheduling management operations require execution processing for scheduling lifecycle and dependency control
  4. Execution operations enable comprehensive scheduling functionality with conditional capabilities and management
  5. Scheduling lifecycle management supports execution requirements and functionality for conditional task processing

Common Usage Examples:
  - taskSchedulesRepository.findByReferencedTaskIdAndCondition() for START and COMPLETE condition filtering
  - taskSchedulesRepository.getReferenceById() for schedule reference in job execution
  - taskSchedulesRepository.save() for schedule configuration and setup
  - Conditional execution based on ScheduledTaskCondition (START, COMPLETE)
  - Task dependency scheduling with referenced task associations

Transaction: Not Required for most operations (JpaRepository handles transactions)
Error Handling: Conditional scheduling error handling and execution validation verification
```

### Pattern: Task Dependency and Scheduling Management
```yaml
Usage: Task dependency and scheduling management for execution control and timing functionality
Purpose: "Manage task dependencies for comprehensive scheduling functionality and timing processing"

Business Logic Derivation:
  1. Task dependency scheduling management operations require comprehensive task schedule access for dependency-level execution management and scheduling functionality
  2. Dependency management supports scheduling requirements and functionality for execution processing workflows
  3. Dependency-level scheduling operations ensure proper execution functionality through schedule management and dependency control
  4. Execution workflows coordinate dependency management with scheduling processing for comprehensive execution operations
  5. Dependency management supports scheduling requirements and functionality for comprehensive task dependency management

Common Usage Examples:
  - taskSchedulesRepository.findByReferencedTaskIdAndCondition() for dependency-based scheduling
  - Referenced task scheduling with condition-based execution
  - Task timing management with start and due date configurations
  - Schedule dependency analysis with task associations

Transaction: Not Required for dependency operations
Error Handling: Dependency management error handling and scheduling validation verification
```

### Pattern: Checklist Revision and Bulk Scheduling
```yaml
Usage: Checklist revision and bulk scheduling for revision management and scheduling functionality
Purpose: "Manage checklist revisions for comprehensive revision functionality and scheduling processing"

Business Logic Derivation:
  1. Checklist revision management operations require comprehensive task schedule access for revision-level scheduling management and revision functionality
  2. Revision management supports scheduling requirements and functionality for schedule processing workflows
  3. Revision-level scheduling operations ensure proper revision functionality through schedule management and revision control
  4. Revision workflows coordinate scheduling management with revision processing for comprehensive revision operations
  5. Scheduling management supports revision requirements and functionality for comprehensive checklist revision management

Common Usage Examples:
  - taskSchedulesRepository.findAllById() for bulk schedule retrieval during revision
  - taskSchedulesRepository.saveAll() for bulk schedule creation in revised checklists
  - Schedule copying and configuration transfer during checklist revision
  - Bulk schedule management for revision processing

Transaction: Not Required for revision operations
Error Handling: Revision error handling and schedule validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllById, findByReferencedTaskIdAndCondition, existsById, count, getReferenceById

Transactional Methods:
  - save, saveAll, delete, deleteById (JpaRepository handles transactions automatically)

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Runtime exceptions
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (type, referenced_tasks_id, condition)
    * Foreign key violations (invalid referenced_tasks_id reference)
    * Invalid JSON data in JSONB fields
    * Task schedule integrity constraint violations
  - EntityNotFoundException: Task schedule not found by ID or criteria
  - OptimisticLockException: Concurrent task schedule modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or schedule context
  - ConstraintViolationException: Task schedule constraint violations

Validation Rules:
  - type: Required, must be valid ScheduledTaskType enum value
  - referencedTask: Required, must reference existing task for schedule dependency
  - referencedTaskId: Required, must be valid referenced task identifier, not updatable
  - condition: Required, must be valid ScheduledTaskCondition enum value
  - startDateDuration: Required, valid JSON configuration for start date duration, defaults to '{}'
  - startDateInterval: Optional, positive integer for start date interval
  - dueDateDuration: Required, valid JSON configuration for due date duration, defaults to '{}'
  - dueDateInterval: Optional, positive integer for due date interval

Business Constraints:
  - Referenced task reference must be valid for task schedule integrity and functionality
  - Schedule type and condition must correspond to valid enum values for proper scheduling
  - Task schedules must support conditional execution requirements and functionality
  - Schedule dependencies must maintain referential integrity and execution functionality
  - Task schedule lifecycle management must maintain dependency consistency and schedule functionality
  - Schedule associations must support task requirements and functionality for execution processing
  - Schedule operations must maintain transaction consistency and constraint integrity for execution management
  - Conditional execution must maintain schedule functionality and consistency
  - Dependency management must maintain schedule integrity and execution requirements
  - JSON duration configurations must be valid for scheduling calculations
  - Referenced task ID field is not updatable to maintain schedule integrity
```

## Task Schedule Management Considerations

### Conditional Execution Integration
```yaml
Condition-Based Scheduling: Task schedules enable execution functionality through conditional scheduling and dependency management
Schedule Management: Task schedule management enables execution functionality with comprehensive scheduling capabilities
Schedule Lifecycle: Task schedule lifecycle includes creation, condition evaluation, and execution operations for scheduling functionality
Execution Management: Comprehensive execution management for task schedule functionality and execution requirements during execution workflows
Conditional Control: Task schedule conditional control for execution functionality and lifecycle management in execution processing
```

### Task Dependency Integration
```yaml
Dependency Scheduling: Task schedules enable dependency functionality through referenced task scheduling and condition coordination
Dependency Management: Task dependency management with schedule coordination and comprehensive dependency functionality
Schedule Dependencies: Task schedule dependencies with dependency validation and comprehensive schedule functionality
Dependency Execution: Task dependency execution with schedule management and comprehensive execution functionality
Task Coordination: Task dependency coordination with schedules and comprehensive coordination functionality for execution workflows
```

### Duration and Timing Configuration
```yaml
Duration Management: Task schedules enable timing functionality through JSON duration configuration and interval management
Timing Configuration: Task schedule timing with start and due date configuration and comprehensive timing functionality
Schedule Timing: Task schedule timing with duration and interval coordination and comprehensive timing functionality
Time Management: Task schedule time management with duration configuration and comprehensive time functionality
Schedule Control: Task schedule schedule control with timing management and comprehensive control functionality for scheduling workflows
```

### JSONB Configuration Management
```yaml
JSON Configuration: Task schedule JSONB fields enable flexible configuration with duration and timing settings
Configuration Flexibility: JSONB configuration provides flexible schedule settings and comprehensive configuration functionality
Duration Configuration: JSON duration configuration enables complex scheduling scenarios and comprehensive duration functionality
Schedule Configuration: JSON schedule configuration enables flexible scheduling timing and comprehensive configuration functionality
Schema Evolution: JSONB fields support configuration schema evolution and comprehensive schema functionality for schedule workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TaskSchedules repository without JPA/Hibernate dependencies, focusing on conditional task scheduling and dependency execution patterns.
