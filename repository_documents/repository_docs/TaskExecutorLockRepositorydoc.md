# ITaskExecutorLockRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TaskExecutorLock (extends UserAuditIdentifiableBase)
- **Primary Purpose**: Manages task executor lock entities for task execution control with executor locking, task dependency management, and execution permission validation functionality
- **Key Relationships**: Many-to-One relationships with Task entities for comprehensive task execution control and dependency management
- **Performance Characteristics**: Medium to high query volume with execution validation, lock management operations, and task dependency control
- **Business Context**: Task execution control component that provides executor locking, task dependency validation, execution permission control, and task execution management functionality for workflow execution control

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditIdentifiableBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| tasks_id | taskId / task.id | Long | false | null | Foreign key to tasks, not updatable |
| referenced_tasks_id | referencedTaskId / referencedTask.id | Long | false | null | Foreign key to referenced tasks, not updatable |
| lock_type | lockType | TaskExecutorLockType | false | null | Lock type enum (max 50 chars) |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | task | Task | LAZY | Primary task, not null |
| @ManyToOne | referencedTask | Task | LAZY | Referenced task for locking, not null |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(TaskExecutorLock entity)`
- `saveAll(Iterable<TaskExecutorLock> entities)`
- `deleteById(Long id)`
- `delete(TaskExecutorLock entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (8 methods - ALL methods documented)

**Task Executor Lock Retrieval Methods (4 methods):**
- `findByTaskIdAndReferencedTaskIdIn(Long taskId, Set<Long> referencedTaskIds)`
- `findByTaskId(Long taskId)` (returns TaskExecutorLockView)
- `findAllByTaskId(Long taskId)`
- `existsByReferencedTaskId(Long taskId)`

**Task Executor Lock Management Methods (2 methods):**
- `removeByTaskId(Long taskId)`
- `removeByTaskIdOrReferencedTaskId(Long taskId)`

**Task Execution Validation Methods (2 methods):**
- `findTasksWhereTaskExecutorLockHasOneReferencedTask(Long taskId)`
- `findInvalidReferencedTaskExecutions(Long taskId, Long jobId, Long currentUserId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<TaskExecutorLock> findById(Long id)
List<TaskExecutorLock> findAll()
TaskExecutorLock save(TaskExecutorLock entity)
List<TaskExecutorLock> saveAll(Iterable<TaskExecutorLock> entities)
void deleteById(Long id)
void delete(TaskExecutorLock entity)
boolean existsById(Long id)
long count()
```

### Task Executor Lock Retrieval Methods

#### Method: findByTaskIdAndReferencedTaskIdIn(Long taskId, Set<Long> referencedTaskIds)
```yaml
Signature: List<TaskExecutorLock> findByTaskIdAndReferencedTaskIdIn(Long taskId, Set<Long> referencedTaskIds)
Purpose: "Find task executor locks by task ID and multiple referenced task IDs for lock validation and dependency checking"

Business Logic Derivation:
  1. Used in TaskService for task executor lock validation during task execution control and dependency validation operations
  2. Provides executor lock access for task workflows enabling comprehensive lock management and task functionality
  3. Critical for task execution operations requiring lock validation for task management and execution control
  4. Used in task execution workflows for accessing executor locks for validation operations and task processing
  5. Enables task execution with lock validation for comprehensive task processing and execution control

SQL Query: |
  SELECT tel FROM TaskExecutorLock tel 
  WHERE tel.taskId = ? AND tel.referencedTaskId IN (?, ?, ?, ...)

Parameters:
  - taskId: Long (Task identifier for lock validation)
  - referencedTaskIds: Set<Long> (Set of referenced task identifiers for bulk lock checking)

Returns: List<TaskExecutorLock> (executor locks for task and referenced tasks)
Transaction: Not Required (read operation)
Error Handling: Returns empty list if no locks found for task and referenced tasks
```

#### Method: findByTaskId(Long taskId)
```yaml
Signature: List<TaskExecutorLockView> findByTaskId(@Param("taskId") Long taskId)
Purpose: "Find task executor lock views by task ID for execution control display and management"

Business Logic Derivation:
  1. Used in TaskService for task executor lock view retrieval during task execution display and lock management operations
  2. Provides executor lock view access for display workflows enabling comprehensive lock display and task functionality
  3. Critical for task display operations requiring lock view access for task management and display control
  4. Used in task display workflows for accessing executor lock views for display operations and task processing
  5. Enables task display with lock view access for comprehensive task processing and display control

Native SQL Query: |
  SELECT task_id, referenced_task_id, lock_type 
  FROM task_executor_locks 
  WHERE tasks_id = :taskId

Parameters:
  - taskId: Long (Task identifier for lock view retrieval)

Returns: List<TaskExecutorLockView> (executor lock views for task)
Transaction: Not Required (read operation)
Error Handling: Returns empty list if no lock views found for task
```

#### Method: findAllByTaskId(Long taskId)
```yaml
Signature: List<TaskExecutorLock> findAllByTaskId(Long taskId)
Purpose: "Find all task executor locks by task ID for complete lock retrieval and management"

Business Logic Derivation:
  1. Used extensively in TaskMapper and ChecklistRevisionService for complete executor lock retrieval during task mapping and revision operations
  2. Provides complete executor lock access for task workflows enabling comprehensive lock management and task functionality
  3. Critical for task management operations requiring complete lock access for task management and revision control
  4. Used in task management workflows for accessing all executor locks for management operations and task processing
  5. Enables task management with complete lock access for comprehensive task processing and management control

SQL Query: |
  SELECT tel FROM TaskExecutorLock tel WHERE tel.taskId = ?

Parameters:
  - taskId: Long (Task identifier for complete lock retrieval)

Returns: List<TaskExecutorLock> (all executor locks for task)
Transaction: Not Required (read operation)
Error Handling: Returns empty list if no locks found for task
```

#### Method: existsByReferencedTaskId(Long taskId)
```yaml
Signature: boolean existsByReferencedTaskId(Long taskId)
Purpose: "Check if task is referenced in any executor lock for dependency validation and lock checking"

Business Logic Derivation:
  1. Used in TaskMapper for referenced task lock validation during task mapping and dependency checking operations
  2. Provides referenced task lock validation for mapping workflows enabling comprehensive dependency validation and task functionality
  3. Critical for task mapping operations requiring referenced task validation for dependency management and mapping control
  4. Used in task mapping workflows for referenced task validation and dependency checking operations
  5. Enables task mapping with referenced task validation for comprehensive task processing and dependency control

SQL Query: |
  SELECT COUNT(tel) > 0 FROM TaskExecutorLock tel WHERE tel.referencedTaskId = ?

Parameters:
  - taskId: Long (Task identifier for referenced task validation)

Returns: boolean (true if task is referenced in executor locks, false otherwise)
Transaction: Not Required (read operation)
Error Handling: Returns false if task is not referenced in any locks
```

### Task Executor Lock Management Methods

#### Method: removeByTaskId(Long taskId)
```yaml
Signature: void removeByTaskId(@Param("taskId") Long taskId)
Purpose: "Remove task executor locks by task ID for lock cleanup and task management"

Business Logic Derivation:
  1. Used in TaskService for executor lock cleanup during task deletion and cleanup operations
  2. Provides executor lock removal for cleanup workflows enabling comprehensive lock cleanup and task functionality
  3. Critical for task cleanup operations requiring lock removal for cleanup management and task control
  4. Used in task cleanup workflows for executor lock removal and cleanup operations
  5. Enables task cleanup with lock removal for comprehensive cleanup processing and task control

Native SQL Query: |
  DELETE FROM task_executor_locks WHERE tasks_id = :taskId

Parameters:
  - taskId: Long (Task identifier for lock removal)

Returns: void
Transaction: Required (@Transactional annotation)
Error Handling: Database constraint violations for referential integrity issues
```

#### Method: removeByTaskIdOrReferencedTaskId(Long taskId)
```yaml
Signature: void removeByTaskIdOrReferencedTaskId(@Param("taskId") Long taskId)
Purpose: "Remove task executor locks by task ID or referenced task ID for comprehensive lock cleanup"

Business Logic Derivation:
  1. Used in TaskService for comprehensive executor lock cleanup during task deletion and dependency cleanup operations
  2. Provides comprehensive executor lock removal for cleanup workflows enabling complete lock cleanup and task functionality
  3. Critical for task cleanup operations requiring comprehensive lock removal for cleanup management and dependency control
  4. Used in task cleanup workflows for comprehensive executor lock removal and dependency cleanup operations
  5. Enables task cleanup with comprehensive lock removal for complete cleanup processing and task control

Native SQL Query: |
  DELETE FROM task_executor_locks 
  WHERE tasks_id = :taskId OR referenced_tasks_id = :taskId

Parameters:
  - taskId: Long (Task identifier for comprehensive lock removal)

Returns: void
Transaction: Required (@Transactional annotation)
Error Handling: Database constraint violations for referential integrity issues
```

### Task Execution Validation Methods

#### Method: findTasksWhereTaskExecutorLockHasOneReferencedTask(Long taskId)
```yaml
Signature: Set<Long> findTasksWhereTaskExecutorLockHasOneReferencedTask(@Param("taskId") Long taskId)
Purpose: "Find tasks where executor lock has one referenced task for lock optimization and validation"

Business Logic Derivation:
  1. Used in TaskService for executor lock optimization during task execution validation and lock management operations
  2. Provides lock optimization analysis for validation workflows enabling comprehensive lock optimization and task functionality
  3. Critical for task validation operations requiring lock optimization for validation management and execution control
  4. Used in task validation workflows for lock optimization analysis and validation operations
  5. Enables task validation with lock optimization for comprehensive validation processing and execution control

Native SQL Query: |
  SELECT DISTINCT tel.tasks_id 
  FROM task_executor_locks tel 
  WHERE tel.referenced_tasks_id = :taskId 
  GROUP BY tel.tasks_id 
  HAVING COUNT(tel.referenced_tasks_id) = 1

Parameters:
  - taskId: Long (Task identifier for lock optimization analysis)

Returns: Set<Long> (task IDs where executor lock has one referenced task)
Transaction: Not Required (read operation)
Error Handling: Returns empty set if no tasks found with single referenced task lock
```

#### Method: findInvalidReferencedTaskExecutions(Long taskId, Long jobId, Long currentUserId)
```yaml
Signature: List<TaskExecutorLockErrorView> findInvalidReferencedTaskExecutions(@Param("taskId") Long taskId, @Param("jobId") Long jobId, @Param("currentUserId") Long currentUserId)
Purpose: "Find invalid referenced task executions for executor permission validation and error checking"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionService and TaskExecutionService for executor permission validation during task execution and parameter operations
  2. Provides executor permission validation for execution workflows enabling comprehensive permission validation and execution functionality
  3. Critical for execution operations requiring permission validation for execution management and access control
  4. Used in execution workflows for executor permission validation and error checking operations
  5. Enables execution management with permission validation for comprehensive execution processing and access control

Native SQL Query: |
  SELECT tel.tasks_id, tel.referenced_tasks_id, te.assignee_id, te.state
  FROM task_executor_locks tel
  JOIN task_executions te ON tel.referenced_tasks_id = te.tasks_id
  WHERE tel.tasks_id = :taskId 
    AND te.jobs_id = :jobId
    AND (te.assignee_id != :currentUserId OR te.state != 'COMPLETED')

Parameters:
  - taskId: Long (Task identifier for permission validation)
  - jobId: Long (Job identifier for execution context)
  - currentUserId: Long (Current user identifier for permission checking)

Returns: List<TaskExecutorLockErrorView> (invalid referenced task executions)
Transaction: Not Required (read operation)
Error Handling: Returns empty list if no invalid referenced task executions found
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Bulk Lock Management
```yaml
Usage: taskExecutorLockRepository.saveAll(taskExecutorLocks)
Purpose: "Create executor locks in bulk for task execution control and dependency management"

Business Logic Derivation:
  1. Used in TaskService and ChecklistRevisionService for bulk executor lock creation during task setup and revision operations
  2. Provides efficient bulk lock persistence for task workflows enabling comprehensive lock creation and task functionality
  3. Critical for task setup operations requiring bulk lock creation for task management and execution control
  4. Used in task setup workflows for bulk executor lock creation and dependency setup operations
  5. Enables task setup with efficient bulk operations for comprehensive task processing and execution control

Transaction: Required for bulk persistence
Error Handling: DataIntegrityViolationException for bulk constraint violations, referential integrity issues
```

#### Pattern: Executor Permission Validation
```yaml
Usage: taskExecutorLockRepository.findInvalidReferencedTaskExecutions() for execution permission validation
Purpose: "Validate executor permissions for task execution and parameter operations"

Business Logic Derivation:
  1. Used across multiple services for executor permission validation during task execution and parameter operations
  2. Provides permission validation for execution workflows enabling comprehensive access control and execution functionality
  3. Critical for execution operations requiring permission validation for access control and execution management
  4. Used in execution workflows for permission validation and access control operations
  5. Enables execution management with permission validation for comprehensive execution processing and access control

Transaction: Not Required for validation operations
Error Handling: Permission validation error handling and access control verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Task Execution Control and Locking
```yaml
Usage: Complete task execution control for executor locking and dependency management functionality
Purpose: "Control task execution for comprehensive locking functionality and execution processing"

Business Logic Derivation:
  1. Task execution control management provides locking functionality through executor lock creation, validation, and dependency management operations
  2. Locking lifecycle includes lock creation, validation checking, and removal management for execution control
  3. Locking management operations require execution processing for locking lifecycle and dependency control
  4. Execution operations enable comprehensive locking functionality with dependency capabilities and management
  5. Locking lifecycle management supports execution requirements and functionality for task execution processing

Common Usage Examples:
  - taskExecutorLockRepository.findInvalidReferencedTaskExecutions() for execution permission validation
  - taskExecutorLockRepository.saveAll() for bulk lock creation during task setup
  - taskExecutorLockRepository.removeByTaskIdOrReferencedTaskId() for comprehensive cleanup
  - taskExecutorLockRepository.findAllByTaskId() for complete lock retrieval
  - taskExecutorLockRepository.existsByReferencedTaskId() for dependency validation

Transaction: Required for lock management operations
Error Handling: Task execution control error handling and lock validation verification
```

### Pattern: Task Dependency and Permission Management
```yaml
Usage: Task dependency and permission management for execution control and access validation functionality
Purpose: "Manage task dependencies for comprehensive permission functionality and access processing"

Business Logic Derivation:
  1. Task dependency management operations require comprehensive executor lock access for dependency-level execution management and permission functionality
  2. Dependency management supports permission requirements and functionality for execution processing workflows
  3. Dependency-level execution operations ensure proper permission functionality through lock management and dependency control
  4. Execution workflows coordinate dependency management with permission processing for comprehensive execution operations
  5. Dependency management supports permission requirements and functionality for comprehensive task dependency management

Common Usage Examples:
  - taskExecutorLockRepository.findByTaskIdAndReferencedTaskIdIn() for dependency validation
  - taskExecutorLockRepository.findTasksWhereTaskExecutorLockHasOneReferencedTask() for optimization
  - Executor permission validation with comprehensive access control
  - Task dependency analysis with lock management

Transaction: Required for dependency management operations
Error Handling: Dependency management error handling and permission validation verification
```

### Pattern: Task Lifecycle and Cleanup Management
```yaml
Usage: Task lifecycle and cleanup management for task deletion and revision functionality
Purpose: "Manage task lifecycle for comprehensive cleanup functionality and revision processing"

Business Logic Derivation:
  1. Task lifecycle management operations require comprehensive executor lock access for lifecycle-level cleanup management and revision functionality
  2. Lifecycle management supports cleanup requirements and functionality for revision processing workflows
  3. Lifecycle-level cleanup operations ensure proper revision functionality through lock management and lifecycle control
  4. Task workflows coordinate lifecycle management with cleanup processing for comprehensive task operations
  5. Lifecycle management supports cleanup requirements and functionality for comprehensive task lifecycle management

Common Usage Examples:
  - taskExecutorLockRepository.removeByTaskId() for task deletion cleanup
  - taskExecutorLockRepository.removeByTaskIdOrReferencedTaskId() for comprehensive cleanup
  - Task revision lock management with cleanup coordination
  - Lock lifecycle management for task maintenance

Transaction: Required for lifecycle management operations
Error Handling: Task lifecycle error handling and cleanup validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByTaskIdAndReferencedTaskIdIn, findByTaskId, findAllByTaskId, existsByReferencedTaskId, findTasksWhereTaskExecutorLockHasOneReferencedTask, findInvalidReferencedTaskExecutions, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById
  - removeByTaskId, removeByTaskIdOrReferencedTaskId

Repository-Level Transaction: @Transactional for modifying operations
Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Runtime exceptions
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (tasks_id, referenced_tasks_id, lock_type)
    * Foreign key violations (invalid tasks_id, referenced_tasks_id references)
    * Task executor lock integrity constraint violations
  - EntityNotFoundException: Task executor lock not found by ID or criteria
  - OptimisticLockException: Concurrent task executor lock modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or execution context
  - ConstraintViolationException: Task executor lock constraint violations

Validation Rules:
  - task: Required, must reference existing task for lock context
  - referencedTask: Required, must reference existing task for lock dependency
  - taskId: Required, must be valid task identifier
  - referencedTaskId: Required, must be valid referenced task identifier
  - lockType: Required, must be valid TaskExecutorLockType enum value

Business Constraints:
  - Task and referenced task references must be valid for executor lock integrity and functionality
  - Lock type must correspond to valid executor lock types for proper lock management
  - Task executor locks must support execution control requirements and functionality
  - Lock dependencies must maintain referential integrity and execution functionality
  - Executor lock lifecycle management must maintain dependency consistency and lock functionality
  - Lock associations must support task requirements and functionality for execution processing
  - Lock operations must maintain transaction consistency and constraint integrity for execution management
  - Permission validation must maintain execution functionality and consistency
  - Dependency management must maintain lock integrity and execution requirements
  - Lock cleanup must maintain referential integrity while removing dependencies
```

## Task Executor Lock Management Considerations

### Execution Control Integration
```yaml
Execution Locking: Task executor locks enable execution functionality through executor locking and dependency management
Lock Management: Executor lock management enables execution functionality with comprehensive locking capabilities
Lock Lifecycle: Executor lock lifecycle includes creation, validation, and removal operations for execution functionality
Execution Management: Comprehensive execution management for executor lock functionality and execution requirements during execution workflows
Access Control: Executor lock access control for execution functionality and lifecycle management in execution processing
```

### Task Dependency Integration
```yaml
Dependency Control: Task executor locks enable dependency functionality through task dependency management and lock coordination
Dependency Management: Task dependency management with lock coordination and comprehensive dependency functionality
Lock Dependencies: Task executor lock dependencies with dependency validation and comprehensive lock functionality
Dependency Validation: Task dependency validation with lock management and comprehensive validation functionality
Task Coordination: Task dependency coordination with executor locks and comprehensive coordination functionality for execution workflows
```

### Permission and Access Control Integration
```yaml
Permission Validation: Task executor locks enable permission functionality through execution permission validation and access control
Access Management: Executor permission management with lock validation and comprehensive access functionality
Execution Permissions: Task execution permissions with lock validation and comprehensive permission functionality
Permission Control: Task executor permission control with lock management and comprehensive permission functionality
Access Validation: Task access validation with executor locks and comprehensive validation functionality for execution workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TaskExecutorLock repository without JPA/Hibernate dependencies, focusing on task execution control and permission validation patterns.
