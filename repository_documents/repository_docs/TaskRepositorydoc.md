# ITaskRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Task
- **Primary Purpose**: Manages individual workflow tasks within stages, task execution properties, and task relationships
- **Key Relationships**: Child of Stage, parent of Parameters; complex relationships with scheduling, recurrence, dependencies, and automations
- **Performance Characteristics**: High query volume with complex task-parameter relationships, scheduling operations, and dependency management
- **Business Context**: Core workflow execution unit that defines individual work items, their execution properties, and relationships within process workflows

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | false | null |
| order_tree | orderTree | Integer | false | null |
| has_stop | hasStop | boolean | false | false |
| is_solo_task | isSoloTask | boolean | false | false |
| is_timed | isTimed | boolean | false | false |
| timer_operator | timerOperator | String | true | null |
| archived | archived | boolean | false | false |
| is_mandatory | isMandatory | boolean | false | false |
| min_period | minPeriod | Long | true | null |
| max_period | maxPeriod | Long | true | null |
| stages_id | stage.id | Long | false | null |
| enable_recurrence | enableRecurrence | boolean | false | false |
| enable_scheduling | enableScheduling | boolean | false | false |
| task_recurrences_id | taskRecurrence.id | Long | true | null |
| task_schedules_id | taskSchedules.id | Long | true | null |
| has_bulk_verification | hasBulkVerification | boolean | false | false |
| has_interlocks | hasInterlocks | boolean | false | false |
| has_executor_lock | hasExecutorLock | boolean | false | false |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | stage | Stage | LAZY | Parent stage, not null |
| @OneToOne | taskRecurrence | TaskRecurrence | LAZY | Recurrence config, cascade = ALL |
| @OneToOne | taskSchedules | TaskSchedules | LAZY | Schedule config, cascade = ALL |
| @OneToMany | parameters | Set\<Parameter\> | LAZY | Child parameters, cascade = ALL, ordered by order_tree, filtered by archived = false |
| @OneToMany | medias | Set\<TaskMediaMapping\> | LAZY | Task media, cascade = ALL, ordered by created_at |
| @OneToMany | automations | Set\<TaskAutomationMapping\> | LAZY | Task automations, cascade = ALL, ordered by order_tree |
| @OneToMany | dependentTasks | Set\<TaskDependency\> | LAZY | Tasks that depend on this task, cascade = ALL |
| @OneToMany | prerequisiteTasks | Set\<TaskDependency\> | LAZY | Tasks this task depends on, cascade = ALL |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Task entity)`
- `deleteById(Long id)`
- `delete(Task entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods
- `findByParameterId(Long parameterId)`
- `reorderTask(Long taskId, Long order, Long userId, Long modifiedAt)`
- `findByStageIdInOrderByOrderTree(Set<Long> stageIds)`
- `findByTaskIdIn(Long checklistId, Set<Long> taskIds, int totalTaskIds, Long facilityId, boolean isUser, boolean isUserGroup)`
- `findAllByIdInAndArchived(Set<Long> ids, boolean archived)`
- `findByTaskSchedulesId(Long taskSchedulesId)`
- `findAllTaskByEnableSchedulingAndChecklistId(Long checklistId, boolean enableScheduling)`
- `findAllTaskByEnableRecurrenceAndChecklistId(Long checklistId, boolean enableRecurrence)`
- `updateHasTaskExecutorLock(Set<Long> tasksWhereTaskExecutorLockIsUsedOnce, boolean isExecutorLock)`
- `updateTasksHasStopToFalseForChecklistId(Long checklistId)`
- `getAllTaskIdsByStageId(Long stageId)`
- `updateHasInterlocks(Long taskId, boolean flag)`
- `deleteHasInterlocks(Long interlocksIds)`
- `removeHasInterlocks(Long taskId)`
- `increaseOrderTreeByOneAfterTask(Long stageId, Integer orderTree, Long taskId)`
- `findTaskLiteInfoByStageIds(Set<Long> stageIds)`

## Method Documentation

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Task> findById(Long id)
List<Task> findAll()
Task save(Task entity)
void deleteById(Long id)
void delete(Task entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: findByParameterId(Long parameterId)
```yaml
Signature: Task findByParameterId(Long parameterId)
Purpose: "Find task containing a specific parameter for parameter-task context operations"

Business Logic Derivation:
  1. Used extensively in job audit service for parameter-related operations
  2. Critical for navigating from parameter context to parent task
  3. Enables parameter operations to access task context for validation
  4. Used in approval/rejection workflows for parameter verification
  5. Essential for maintaining parameter-task hierarchy and context

SQL Query: |
  SELECT t.* FROM tasks t 
  INNER JOIN parameters p ON t.id = p.tasks_id 
  WHERE p.id = ?

  BUSINESS LOGIC:
  1. Join tasks with parameters to traverse hierarchy upward
  2. Filter by parameter ID to find containing task
  3. Return full task entity for context operations
  4. Enables parameter-centric operations to access task context
  5. Critical for workflow navigation and parameter management

Parameters:
  - parameterId: Long (Parameter identifier to find parent task for)

Returns: Task (parent task containing the parameter)
Transaction: Not Required
Error Handling: Returns null if parameter doesn't exist or has no parent task
```

#### Method: reorderTask(Long taskId, Long order, Long userId, Long modifiedAt)
```yaml
Signature: void reorderTask(Long taskId, Long order, Long userId, Long modifiedAt)
Purpose: "Update task order position and audit metadata for workflow reordering"

Business Logic Derivation:
  1. Used in TaskService.reorderTasks() for task reorganization operations
  2. Atomically updates order position and audit fields in single operation
  3. Critical for workflow reorganization and task sequence management
  4. Maintains audit trail during task reordering operations
  5. Enables efficient batch reordering without loading full entities

SQL Query: |
  UPDATE tasks 
  SET order_tree = ?, modified_by = ?, modified_at = ? 
  WHERE id = ?

  BUSINESS LOGIC:
  1. Directly update order_tree column for task positioning
  2. Update modified_by to track user performing reorder operation
  3. Update modified_at timestamp for audit trail maintenance
  4. Filter by task ID for precise reordering operation
  5. Atomic operation for consistent task order management
  6. Essential for workflow structure reorganization

Parameters:
  - taskId: Long (Task identifier to reorder)
  - order: Long (New order position for the task)
  - userId: Long (User performing the reorder operation)
  - modifiedAt: Long (Timestamp for modification tracking)

Returns: void
Transaction: Required (uses @Transactional and @Modifying)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findByStageIdInOrderByOrderTree(Set<Long> stageIds)
```yaml
Signature: List<Task> findByStageIdInOrderByOrderTree(Set<Long> stageIds)
Purpose: "Find all tasks within multiple stages ordered by workflow sequence"

Business Logic Derivation:
  1. Used in checklist collaboration service for multi-stage task processing
  2. Enables bulk task retrieval across multiple stages in proper order
  3. Critical for cross-stage operations and workflow processing
  4. Supports stage-aware task operations and batch processing
  5. Used in collaboration and audit operations for task context

SQL Query: |
  SELECT t.* FROM tasks t 
  WHERE t.stages_id IN (?) 
  ORDER BY t.order_tree

  BUSINESS LOGIC:
  1. Query tasks table with multiple stage filtering
  2. Use IN clause for efficient multi-stage matching
  3. Order by order_tree for proper workflow sequence
  4. Return tasks across stages in execution order
  5. Enables cross-stage task operations and processing

Parameters:
  - stageIds: Set<Long> (Stage identifiers to find tasks for)

Returns: List<Task> (tasks from specified stages in workflow order)
Transaction: Not Required
Error Handling: Returns empty list if no tasks found in specified stages
```

#### Method: findByTaskIdIn(Long checklistId, Set<Long> taskIds, int totalTaskIds, Long facilityId, boolean isUser, boolean isUserGroup)
```yaml
Signature: List<TaskAssigneeView> findByTaskIdIn(Long checklistId, Set<Long> taskIds, int totalTaskIds, Long facilityId, boolean isUser, boolean isUserGroup)
Purpose: "Get task assignment information with user and user group details"

Business Logic Derivation:
  1. Used in ChecklistService for task assignment visualization
  2. Provides comprehensive task assignment data for UI display
  3. Critical for task assignment management and workforce allocation
  4. Supports both user and user group assignment tracking
  5. Enables facility-scoped assignment queries for multi-facility operations

SQL Query: |
  SELECT t.id as taskId, t.name as taskName, u.id as userId, u.first_name as firstName, 
         u.last_name as lastName, ug.id as userGroupId, ug.name as userGroupName
  FROM tasks t
  LEFT JOIN task_execution_assignee_mapping team ON t.id = team.tasks_id
  LEFT JOIN users u ON team.users_id = u.id AND ? = true
  LEFT JOIN user_groups ug ON team.user_groups_id = ug.id AND ? = true
  WHERE t.id IN (?) AND t.archived = false
    AND (? IS NULL OR team.facilities_id = ?)
  ORDER BY t.order_tree

  BUSINESS LOGIC:
  1. Join tasks with assignee mappings to get assignment information
  2. Left join with users and user groups based on type flags
  3. Filter by task IDs and exclude archived tasks
  4. Apply facility filtering for multi-facility scope
  5. Order by task order for consistent display sequence
  6. Return projection view with assignment details

Parameters:
  - checklistId: Long (Checklist context identifier)
  - taskIds: Set<Long> (Task identifiers to get assignments for)
  - totalTaskIds: int (Total task count for context)
  - facilityId: Long (Facility scope for assignments)
  - isUser: boolean (Include user assignments flag)
  - isUserGroup: boolean (Include user group assignments flag)

Returns: List<TaskAssigneeView> (task assignment projection views)
Transaction: Not Required
Error Handling: Returns empty list if no assignments found
```

#### Method: findAllByIdInAndArchived(Set<Long> ids, boolean archived)
```yaml
Signature: List<Task> findAllByIdInAndArchived(Set<Long> ids, boolean archived)
Purpose: "Find tasks by multiple IDs with archive status filtering"

Business Logic Derivation:
  1. Used in checklist revision service for task revision operations
  2. Enables bulk task retrieval with archive status filtering
  3. Critical for revision workflows that need active tasks only
  4. Supports task lifecycle management and version control
  5. Used for filtering out archived tasks during revision operations

SQL Query: |
  SELECT t.* FROM tasks t 
  WHERE t.id IN (?) AND t.archived = ?

Parameters:
  - ids: Set<Long> (Task identifiers to retrieve)
  - archived: boolean (Archive status filter)

Returns: List<Task> (tasks matching IDs and archive status)
Transaction: Not Required
Error Handling: Returns empty list if no tasks match criteria
```

#### Method: findByTaskSchedulesId(Long taskSchedulesId)
```yaml
Signature: Task findByTaskSchedulesId(Long taskSchedulesId)
Purpose: "Find task associated with a specific task schedule"

Business Logic Derivation:
  1. Used in task execution service for scheduled task operations
  2. Enables navigation from task schedule to parent task
  3. Critical for scheduled task execution and workflow automation
  4. Used in job execution for processing scheduled tasks
  5. Essential for task scheduling and automation workflows

SQL Query: |
  SELECT t.* FROM tasks t 
  WHERE t.task_schedules_id = ?

Parameters:
  - taskSchedulesId: Long (Task schedule identifier to find task for)

Returns: Task (task associated with the schedule)
Transaction: Not Required
Error Handling: Returns null if no task found for the schedule
```

#### Method: findAllTaskByEnableSchedulingAndChecklistId(Long checklistId, boolean enableScheduling)
```yaml
Signature: List<Task> findAllTaskByEnableSchedulingAndChecklistId(Long checklistId, boolean enableScheduling)
Purpose: "Find tasks with scheduling enabled/disabled within a checklist"

Business Logic Derivation:
  1. Used in checklist revision service for scheduling-enabled task processing
  2. Enables bulk retrieval of tasks with specific scheduling configuration
  3. Critical for revision operations that handle task scheduling
  4. Supports task scheduling lifecycle management during revisions
  5. Used for scheduling configuration migration and updates

SQL Query: |
  SELECT t.* FROM tasks t 
  INNER JOIN stages s ON t.stages_id = s.id 
  WHERE s.checklists_id = ? 
    AND t.enable_scheduling = ? 
    AND t.archived = false 
    AND s.archived = false

  BUSINESS LOGIC:
  1. Join tasks with stages to access checklist context
  2. Filter by checklist ID and scheduling enablement status
  3. Exclude archived tasks and stages for active configuration
  4. Return tasks with specific scheduling configuration
  5. Enables bulk scheduling configuration operations

Parameters:
  - checklistId: Long (Checklist identifier to scope search)
  - enableScheduling: boolean (Scheduling enablement filter)

Returns: List<Task> (tasks with specified scheduling configuration)
Transaction: Not Required
Error Handling: Returns empty list if no tasks match criteria
```

#### Method: findAllTaskByEnableRecurrenceAndChecklistId(Long checklistId, boolean enableRecurrence)
```yaml
Signature: List<Task> findAllTaskByEnableRecurrenceAndChecklistId(Long checklistId, boolean enableRecurrence)
Purpose: "Find tasks with recurrence enabled/disabled within a checklist"

Business Logic Derivation:
  1. Used in checklist revision service for recurrence-enabled task processing
  2. Enables bulk retrieval of tasks with specific recurrence configuration
  3. Critical for revision operations that handle task recurrence
  4. Supports task recurrence lifecycle management during revisions
  5. Used for recurrence configuration migration and updates

SQL Query: |
  SELECT t.* FROM tasks t 
  INNER JOIN stages s ON t.stages_id = s.id 
  WHERE s.checklists_id = ? 
    AND t.enable_recurrence = ? 
    AND t.archived = false 
    AND s.archived = false

  BUSINESS LOGIC:
  1. Join tasks with stages to access checklist context
  2. Filter by checklist ID and recurrence enablement status
  3. Exclude archived tasks and stages for active configuration
  4. Return tasks with specific recurrence configuration
  5. Enables bulk recurrence configuration operations

Parameters:
  - checklistId: Long (Checklist identifier to scope search)
  - enableRecurrence: boolean (Recurrence enablement filter)

Returns: List<Task> (tasks with specified recurrence configuration)
Transaction: Not Required
Error Handling: Returns empty list if no tasks match criteria
```

#### Method: updateHasTaskExecutorLock(Set<Long> tasksWhereTaskExecutorLockIsUsedOnce, boolean isExecutorLock)
```yaml
Signature: void updateHasTaskExecutorLock(Set<Long> tasksWhereTaskExecutorLockIsUsedOnce, boolean isExecutorLock)
Purpose: "Update executor lock flag for multiple tasks in batch operation"

Business Logic Derivation:
  1. Used in TaskService for executor lock management operations
  2. Enables bulk update of executor lock status for efficiency
  3. Critical for task executor constraint management
  4. Supports executor lock configuration during task operations
  5. Used for maintaining executor lock consistency across related tasks

SQL Query: |
  UPDATE tasks 
  SET has_executor_lock = ? 
  WHERE id IN (?)

  BUSINESS LOGIC:
  1. Update tasks table to set executor lock flag
  2. Filter by task IDs set for bulk operation
  3. Set has_executor_lock to specified boolean value
  4. Enables bulk executor lock management
  5. Maintains task executor constraint consistency

Parameters:
  - tasksWhereTaskExecutorLockIsUsedOnce: Set<Long> (Task IDs to update)
  - isExecutorLock: boolean (Executor lock flag value)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: updateTasksHasStopToFalseForChecklistId(Long checklistId)
```yaml
Signature: void updateTasksHasStopToFalseForChecklistId(Long checklistId)
Purpose: "Reset stop dependency flags for all tasks in a checklist"

Business Logic Derivation:
  1. Used in migration operations for stop dependency management
  2. Bulk resets stop flags across all tasks in a checklist
  3. Critical for checklist lifecycle management and migration
  4. Supports dependency configuration updates during migrations
  5. Used for maintaining consistent stop dependency state

SQL Query: |
  UPDATE tasks 
  SET has_stop = false 
  WHERE id IN (
    SELECT t.id FROM tasks t 
    INNER JOIN stages s ON t.stages_id = s.id 
    WHERE s.checklists_id = ?
  )

  BUSINESS LOGIC:
  1. Update tasks table to reset has_stop flag
  2. Use subquery to find all tasks in the checklist
  3. Join through stages to access checklist context
  4. Set has_stop to false for all checklist tasks
  5. Enables bulk stop dependency reset operations

Parameters:
  - checklistId: Long (Checklist identifier to reset stop flags for)

Returns: void
Transaction: Required (uses @Modifying)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: getAllTaskIdsByStageId(Long stageId)
```yaml
Signature: Set<Long> getAllTaskIdsByStageId(Long stageId)
Purpose: "Get all task IDs within a specific stage for bulk operations"

Business Logic Derivation:
  1. Used in element copy service for stage copying operations
  2. Provides efficient task ID retrieval for bulk processing
  3. Critical for stage-level operations that need task context
  4. Enables memory-efficient bulk task operations
  5. Used for task copying and dependency management

SQL Query: |
  SELECT t.id FROM tasks t 
  WHERE t.stages_id = ? AND t.archived = false

Parameters:
  - stageId: Long (Stage identifier to get task IDs for)

Returns: Set<Long> (task IDs within the stage)
Transaction: Not Required
Error Handling: Returns empty set if no tasks found in stage
```

#### Method: updateHasInterlocks(Long taskId, boolean flag)
```yaml
Signature: void updateHasInterlocks(Long taskId, boolean flag)
Purpose: "Update interlock flag for a specific task"

Business Logic Derivation:
  1. Used in InterLockService for interlock management operations
  2. Atomically updates interlock flag for task configuration
  3. Critical for task interlock constraint management
  4. Maintains interlock state consistency during interlock operations
  5. Used for tracking interlock associations on tasks

SQL Query: |
  UPDATE tasks 
  SET has_interlocks = ? 
  WHERE id = ?

Parameters:
  - taskId: Long (Task identifier to update)
  - flag: boolean (Interlock flag value)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: deleteHasInterlocks(Long interlocksIds)
```yaml
Signature: void deleteHasInterlocks(Long interlocksIds)
Purpose: "Reset interlock flag when interlock is deleted"

Business Logic Derivation:
  1. Used in InterLockService for interlock deletion operations
  2. Resets interlock flag when associated interlock is removed
  3. Critical for maintaining interlock state consistency
  4. Ensures task interlock flags reflect actual interlock existence
  5. Used for cleanup during interlock removal operations

SQL Query: |
  UPDATE tasks 
  SET has_interlocks = false 
  WHERE id IN (
    SELECT target_entity_id FROM interlocks 
    WHERE id = ?
  )

  BUSINESS LOGIC:
  1. Update tasks table to reset has_interlocks flag
  2. Use subquery to find task associated with deleted interlock
  3. Reset has_interlocks to false for affected task
  4. Maintains interlock state consistency during deletion
  5. Ensures task flags reflect actual interlock state

Parameters:
  - interlocksIds: Long (Interlock identifier being deleted)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: removeHasInterlocks(Long taskId)
```yaml
Signature: void removeHasInterlocks(Long taskId)
Purpose: "Remove interlock flag from a specific task"

Business Logic Derivation:
  1. Used in InterLockService for interlock removal operations
  2. Directly resets interlock flag for specified task
  3. Critical for interlock cleanup and task state management
  4. Ensures task interlock flags are properly maintained
  5. Used when all interlocks are removed from a task

SQL Query: |
  UPDATE tasks 
  SET has_interlocks = false 
  WHERE id = ?

Parameters:
  - taskId: Long (Task identifier to reset interlock flag for)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: increaseOrderTreeByOneAfterTask(Long stageId, Integer orderTree, Long taskId)
```yaml
Signature: void increaseOrderTreeByOneAfterTask(Long stageId, Integer orderTree, Long taskId)
Purpose: "Shift task order positions to make room for new task insertion"

Business Logic Derivation:
  1. Used in element copy service for task duplication operations
  2. Creates space in task ordering sequence for new task insertion
  3. Critical for maintaining proper task order during task creation
  4. Enables task insertion at specific positions without order conflicts
  5. Supports task copying and workflow reorganization operations

SQL Query: |
  UPDATE tasks 
  SET order_tree = order_tree + 1 
  WHERE stages_id = ? 
    AND order_tree > ? 
    AND id != ?

  BUSINESS LOGIC:
  1. Update tasks table to increment order_tree values
  2. Filter by stage ID to scope operation to specific stage
  3. Filter by order_tree > threshold to shift only subsequent tasks
  4. Exclude new task ID to avoid shifting the newly inserted task
  5. Creates gap in ordering sequence for proper task insertion
  6. Maintains workflow ordering integrity during task operations

Parameters:
  - stageId: Long (Stage to perform order shifting in)
  - orderTree: Integer (Order position threshold for shifting)
  - taskId: Long (New task ID to exclude from shifting)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findTaskLiteInfoByStageIds(Set<Long> stageIds)
```yaml
Signature: List<TaskLiteView> findTaskLiteInfoByStageIds(Set<Long> stageIds)
Purpose: "Get lightweight task information for job display operations"

Business Logic Derivation:
  1. Used in job service for task information display
  2. Provides optimized task data for UI and reporting operations
  3. Critical for job execution displays and task progress tracking
  4. Returns projection view for efficient data transfer
  5. Enables lightweight task operations without loading full entities

SQL Query: |
  SELECT t.id as id, t.name as name, t.order_tree as orderTree, t.stages_id as stageId 
  FROM tasks t 
  WHERE t.archived = false AND t.stages_id IN (?) 
  ORDER BY t.order_tree ASC

Parameters:
  - stageIds: Set<Long> (Stage identifiers to get task information for)

Returns: List<TaskLiteView> (lightweight task projection views)
Transaction: Not Required
Error Handling: Returns empty list if no non-archived tasks found
```

### Key Repository Usage Patterns (Based on Codebase Analysis)

#### Pattern: save() for Task Lifecycle Management
```yaml
Usage: taskRepository.save(task)
Purpose: "Create new tasks, update task properties, and manage task lifecycle"

Business Logic Derivation:
  1. Used extensively in TaskService for task creation and modification
  2. Handles task creation with proper stage association and ordering
  3. Updates task properties like name, timing, and execution configurations
  4. Critical for task lifecycle management and workflow building
  5. Supports complex task operations with parameter and media relationships

Common Usage Examples:
  - Task creation: Save new task with stage association and order
  - Property updates: Save task with modified execution properties
  - Configuration changes: Save task with updated scheduling or recurrence
  - Media operations: Save task with added media attachments

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: saveAll() for Bulk Task Operations
```yaml
Usage: taskRepository.saveAll(tasks)
Purpose: "Bulk create or update tasks for efficiency"

Business Logic Derivation:
  1. Used in import/export service for checklist creation
  2. Used in revision service for bulk task revision operations
  3. Optimizes database operations for multi-task workflows
  4. Ensures transactional consistency for complex task operations
  5. Enables efficient task batch operations

Transaction: Required
Error Handling: Batch operation rollback on any failure
```

#### Pattern: findById() for Task Context Operations
```yaml
Usage: taskRepository.findById(taskId)
Purpose: "Retrieve task entity for context-aware operations"

Business Logic Derivation:
  1. Used extensively in TaskService for task-specific operations
  2. Critical for task execution operations that need full task context
  3. Used in parameter service for task validation and context
  4. Essential for task scheduling, recurrence, and automation operations
  5. Enables comprehensive task operations with relationship access

Transaction: Not Required
Error Handling: Throws ResourceNotFoundException if task not found
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByParameterId, findByStageIdInOrderByOrderTree
  - findByTaskIdIn, findAllByIdInAndArchived, findByTaskSchedulesId
  - findAllTaskByEnableSchedulingAndChecklistId, findAllTaskByEnableRecurrenceAndChecklistId
  - getAllTaskIdsByStageId, findTaskLiteInfoByStageIds, existsById, count

Transactional Methods:
  - save, delete, deleteById, reorderTask, updateHasTaskExecutorLock
  - updateTasksHasStopToFalseForChecklistId, updateHasInterlocks
  - deleteHasInterlocks, removeHasInterlocks, increaseOrderTreeByOneAfterTask

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid stages_id, task_recurrences_id, task_schedules_id)
    * NOT NULL constraint violations (name, orderTree, stages_id)
    * Unique constraint violations for order_tree within stage
  - EntityNotFoundException: Task not found by ID or criteria
  - OptimisticLockException: Concurrent task modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or specification criteria

Validation Rules:
  - name: Required, max length 512 characters
  - orderTree: Required, must be positive integer, should be unique within stage
  - stage: Required, must reference existing stage, not null
  - hasStop: Required, boolean flag for stop dependency
  - isSoloTask: Required, boolean flag for solo task execution
  - isTimed: Required, boolean flag for timer constraints
  - timerOperator: Optional, string for timer operation type
  - archived: Required, defaults to false
  - isMandatory: Required, boolean flag for mandatory execution
  - minPeriod: Optional, minimum execution period in milliseconds
  - maxPeriod: Optional, maximum execution period in milliseconds
  - enableRecurrence: Required, boolean flag for recurrence capability
  - enableScheduling: Required, boolean flag for scheduling capability
  - hasBulkVerification: Required, boolean flag for bulk verification
  - hasInterlocks: Required, boolean flag for interlock constraints
  - hasExecutorLock: Required, boolean flag for executor lock constraints

Business Constraints:
  - Cannot modify stage association after creation
  - Order tree should be unique within stage scope for proper sequencing
  - Cannot delete task with active parameters or executions
  - Task order changes must maintain sequence integrity
  - Archived tasks should not participate in active workflows
  - Timer periods must be logical (minPeriod <= maxPeriod)
  - Recurrence and scheduling configurations must be consistent
  - Solo tasks cannot have dependencies or be part of parallel execution
  - Executor lock constraints must be properly configured with valid references
  - Interlock flags must reflect actual interlock entity existence
  - Task names should be descriptive for workflow clarity
  - Bulk verification settings must align with parameter configurations
  - Stop dependencies must be properly managed during workflow execution
