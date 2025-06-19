# ITaskExecutionRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TaskExecution
- **Primary Purpose**: Manages individual task execution instances within jobs with state tracking, corrections, scheduling, and dependency management
- **Key Relationships**: Central execution entity linking Task and Job with ParameterValues and user assignments
- **Performance Characteristics**: Very high query volume with complex state transitions, correction workflows, recurring task management, and dependency validation
- **Business Context**: Core task-level execution management component that tracks individual task instances from start to completion with comprehensive workflow control

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| state | state | State.TaskExecution | false | null |
| type | type | Type.TaskExecutionType | false | null |
| correction_reason | correctionReason | String | true | null |
| correction_enabled | correctionEnabled | boolean | false | false |
| reason | reason | String | true | null |
| started_at | startedAt | Long | true | null |
| started_by | startedBy.id | Long | true | null |
| ended_at | endedAt | Long | true | null |
| ended_by | endedBy.id | Long | true | null |
| tasks_id | task.id | Long | false | null |
| jobs_id | job.id | Long | false | null |
| corrected_by | correctedBy.id | Long | true | null |
| corrected_at | correctedAt | Long | true | null |
| duration | duration | Long | true | null |
| order_tree | orderTree | Integer | false | null |
| continue_recurrence | continueRecurrence | boolean | false | false |
| is_scheduled | scheduled | boolean | false | false |
| recurring_premature_start_reason | recurringPrematureStartReason | String | true | null |
| recurring_overdue_completion_reason | recurringOverdueCompletionReason | String | true | null |
| recurring_expected_started_at | recurringExpectedStartedAt | Long | true | null |
| recurring_expected_due_at | recurringExpectedDueAt | Long | true | null |
| scheduling_expected_started_at | schedulingExpectedStartedAt | Long | true | null |
| scheduling_expected_due_at | schedulingExpectedDueAt | Long | true | null |
| schedule_overdue_completion_reason | scheduleOverdueCompletionReason | String | true | null |
| schedule_premature_start_reason | schedulePrematureStartReason | String | true | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | task | Task | LAZY | Parent task template, not null, immutable |
| @ManyToOne | job | Job | LAZY | Parent job instance, not null, immutable |
| @ManyToOne | startedBy | User | EAGER | User who started the task execution, cascade = ALL |
| @ManyToOne | endedBy | User | LAZY | User who ended the task execution, cascade = ALL |
| @ManyToOne | correctedBy | User | LAZY | User who performed correction, cascade = DETACH |
| @OneToMany | parameterValues | Set\<ParameterValue\> | LAZY | Task execution parameter values, cascade = ALL |
| @OneToMany | assignees | Set\<TaskExecutionUserMapping\> | LAZY | Task execution user assignments, cascade = ALL |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(TaskExecution entity)`
- `deleteById(Long id)`
- `delete(TaskExecution entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (33 methods - ALL methods documented)

- `readByJobIdAndStageIdOrderByOrderTree(Long jobId, Long stageId)`
- `enableCorrection(String correctionReason, Long id)`
- `cancelCorrection(Long id)`
- `readByJobIdAndTaskIdIn(Long jobId, List<Long> taskIds)`
- `findNonCompletedTaskIdsByJobId(Long jobId)`
- `findEnabledForCorrectionTaskIdsByJobId(Long jobId)`
- `findNonSignedOffTaskIdsByJobIdAndUserId(Long jobId, Long userId)`
- `getTaskExecutionCountByJobId(Long jobId)`
- `findByJobIdAndStageIdIn(Long jobId, Set<Long> stageIds)`
- `findTaskExecutionDetailsByJobId(Set<Long> jobIds)`
- `findByTaskIdAndJobIdAndType(Long taskId, Long jobId, Type.TaskExecutionType type)`
- `findByTaskIdAndJobIdOrderByOrderTree(Long taskId, Long jobId)`
- `findAllTaskExecutionsNotInCompletedStateByTaskIdAndJobId(Long taskId, Long jobId)`
- `deleteByTaskExecutionId(Long id)`
- `checkIfAnyTaskExecutionContainsStopRecurrence(Long taskId, Long jobId)`
- `setAllTaskExecutionsContinueRecurrenceFalse(Long taskId, Long jobId)`
- `findAllStartedTaskExecutionsAfterStageOrderTree(Integer orderTree, Long jobId)`
- `findAllNonCompletedTaskExecutionBeforeCurrentStageAndHasStop(Integer orderTree, Long jobId)`
- `findAllTaskExecutionsWithJobSchedule(Long jobId)`
- `findAllStartedTaskExecutionsOfStage(Long jobId, Integer taskOrderTree, Long stageId)`
- `findAllNonCompletedTaskExecutionOfCurrentStageAndHasStop(Long stageId, Integer orderTree, Long jobId)`
- `findEnabledForCorrectionTaskExecutionIdsByJobIdAndTaskId(Long jobId, Long taskId)`
- `findAllTaskExecutionsNotInCompletedOrNotInStartedStatedByTaskIdAndJobId(Long taskId, Long jobId)`
- `findTaskExecutionEnabledForCorrection(Long taskId, Long jobId)`
- `getPendingTasksOfUserForJobs(Set<Long> jobIds, Long userId, Set<String> jobPendingStates)`
- `getEngagedUsersForJob(Set<Long> jobIds)`
- `findIncompleteDependencies(Long taskId, Long jobId)`
- `readByJobId(Long jobId, Long checklistId)`
- `getAllLatestDependantTaskExecutionIdsHavingPrerequisiteTaskId(Long preRequisiteTaskId, Long jobId)`
- `getAllCompletedPreRequisiteTaskDetails(Long dependantTaskId, Long jobId)`
- `getTaskExecutionsLiteByJobId(Long jobId)`
- `getTaskPauseResumeAuditDtoByTaskExecutionId(Long taskExecutionId)`
- `findAllByJobIdAndTaskIdIn(Long jobId, Set<String> taskIds)`

## Method Documentation (Key Methods)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<TaskExecution> findById(Long id)
List<TaskExecution> findAll()
TaskExecution save(TaskExecution entity)
void deleteById(Long id)
void delete(TaskExecution entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: readByJobIdAndStageIdOrderByOrderTree(Long jobId, Long stageId)
```yaml
Signature: List<TaskExecution> readByJobIdAndStageIdOrderByOrderTree(Long jobId, Long stageId)
Purpose: "Get task executions for a specific job and stage ordered by execution sequence"

Business Logic Derivation:
  1. Used extensively in JobService for stage-based task execution retrieval
  2. Provides ordered task executions within a stage for workflow processing
  3. Critical for stage-level task execution management and display
  4. Used in stage data operations for chronological task execution display
  5. Enables stage-scoped task execution operations with proper ordering

SQL Query: |
  SELECT te.* FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  WHERE te.jobs_id = ? AND t.stages_id = ?
  ORDER BY te.order_tree

Parameters:
  - jobId: Long (Job identifier for scoping)
  - stageId: Long (Stage identifier for filtering)

Returns: List<TaskExecution> (task executions ordered by execution sequence)
Transaction: Not Required
Error Handling: Returns empty list if no task executions found
```

#### Method: enableCorrection(String correctionReason, Long id)
```yaml
Signature: void enableCorrection(String correctionReason, Long id)
Purpose: "Enable correction mode for a task execution with specified reason"

Business Logic Derivation:
  1. Used in TaskExecutionService for correction workflow management
  2. Enables task execution correction with audit trail maintenance
  3. Critical for quality control and error correction workflows
  4. Used when parameter values need correction after task completion
  5. Supports correction workflow initiation with proper reason tracking

SQL Query: |
  UPDATE task_executions 
  SET correction_enabled = true, correction_reason = ? 
  WHERE id = ?

Parameters:
  - correctionReason: String (Reason for enabling correction)
  - id: Long (Task execution identifier)

Returns: void
Transaction: Required (uses @Modifying)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: cancelCorrection(Long id)
```yaml
Signature: void cancelCorrection(Long id)
Purpose: "Cancel correction mode for a task execution"

Business Logic Derivation:
  1. Used in TaskExecutionService for correction workflow cancellation
  2. Disables correction mode and cleans up correction state
  3. Critical for correction workflow management and cleanup
  4. Used when correction is no longer needed or cancelled
  5. Supports correction workflow termination with state cleanup

SQL Query: |
  UPDATE task_executions 
  SET correction_enabled = false, correction_reason = null 
  WHERE id = ?

Parameters:
  - id: Long (Task execution identifier)

Returns: void
Transaction: Required (uses @Modifying)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findNonCompletedTaskIdsByJobId(Long jobId)
```yaml
Signature: List<Long> findNonCompletedTaskIdsByJobId(Long jobId)
Purpose: "Find task IDs that are not in completed state for job completion validation"

Business Logic Derivation:
  1. Used in JobService for job completion validation and state checking
  2. Identifies tasks that prevent job completion due to incomplete state
  3. Critical for job completion workflow validation and error prevention
  4. Used in job state transition validation and completion checks
  5. Enables job completion validation with incomplete task identification

SQL Query: |
  SELECT DISTINCT te.tasks_id FROM task_executions te
  WHERE te.jobs_id = ? 
    AND te.state NOT IN ('COMPLETED', 'COMPLETED_WITH_EXCEPTION', 'SKIPPED')

Parameters:
  - jobId: Long (Job identifier to check for incomplete tasks)

Returns: List<Long> (task IDs that are not completed)
Transaction: Not Required
Error Handling: Returns empty list if all tasks are completed
```

#### Method: findByTaskIdAndJobIdAndType(Long taskId, Long jobId, Type.TaskExecutionType type)
```yaml
Signature: TaskExecution findByTaskIdAndJobIdAndType(Long taskId, Long jobId, Type.TaskExecutionType type)
Purpose: "Find specific task execution by task, job, and execution type"

Business Logic Derivation:
  1. Used in ParameterVerificationService and CreateJobService for type-specific retrieval
  2. Enables retrieval of MASTER, REPEAT, or other specific execution types
  3. Critical for parameter verification and task execution type management
  4. Used when specific execution type context is needed for operations
  5. Supports type-aware task execution operations and validation

SQL Query: |
  SELECT te.* FROM task_executions te
  WHERE te.tasks_id = ? AND te.jobs_id = ? AND te.type = ?

Parameters:
  - taskId: Long (Task identifier)
  - jobId: Long (Job identifier)
  - type: Type.TaskExecutionType (Execution type to find)

Returns: TaskExecution (task execution matching criteria)
Transaction: Not Required
Error Handling: Returns null if no matching task execution found
```

#### Method: checkIfAnyTaskExecutionContainsStopRecurrence(Long taskId, Long jobId)
```yaml
Signature: boolean checkIfAnyTaskExecutionContainsStopRecurrence(Long taskId, Long jobId)
Purpose: "Check if any task execution has stop recurrence flag set for recurring task management"

Business Logic Derivation:
  1. Used in TaskExecutionService for recurring task validation and control
  2. Determines if task recurrence should be stopped based on execution flags
  3. Critical for recurring task workflow control and termination logic
  4. Used in recurring task validation to prevent unwanted task creation
  5. Enables recurring task lifecycle management with stop condition checking

SQL Query: |
  SELECT COUNT(*) > 0 FROM task_executions te
  WHERE te.tasks_id = ? AND te.jobs_id = ? 
    AND te.continue_recurrence = false

Parameters:
  - taskId: Long (Task identifier)
  - jobId: Long (Job identifier)

Returns: boolean (true if any execution has stop recurrence flag)
Transaction: Not Required
Error Handling: Returns false if no executions have stop recurrence
```

#### Method: getPendingTasksOfUserForJobs(Set<Long> jobIds, Long userId, Set<String> jobPendingStates)
```yaml
Signature: List<TaskPendingOnMeView> getPendingTasksOfUserForJobs(Set<Long> jobIds, Long userId, Set<String> jobPendingStates)
Purpose: "Get tasks pending on a specific user across multiple jobs for dashboard display"

Business Logic Derivation:
  1. Used in JobService for user-specific pending task dashboard functionality
  2. Provides user-centric view of tasks requiring attention
  3. Critical for user workflow management and task assignment tracking
  4. Used in user dashboard for pending task visualization
  5. Enables personalized task management with comprehensive pending task information

SQL Query: |
  SELECT te.id as taskExecutionId, te.tasks_id as taskId, t.name as taskName,
         te.jobs_id as jobId, j.code as jobCode, c.name as checklistName
  FROM task_executions te
  INNER JOIN task_execution_user_mapping teum ON te.id = teum.task_executions_id
  INNER JOIN tasks t ON te.tasks_id = t.id
  INNER JOIN jobs j ON te.jobs_id = j.id
  INNER JOIN checklists c ON j.checklists_id = c.id
  WHERE te.jobs_id IN (?) 
    AND teum.users_id = ?
    AND j.state IN (?)
    AND te.state IN ('NOT_STARTED', 'IN_PROGRESS')

Parameters:
  - jobIds: Set<Long> (Job identifiers to check)
  - userId: Long (User identifier for assignment filtering)
  - jobPendingStates: Set<String> (Job states considered pending)

Returns: List<TaskPendingOnMeView> (pending task information for the user)
Transaction: Not Required
Error Handling: Returns empty list if no pending tasks found
```

## Method Documentation (All Remaining Methods - Full Detail)

#### Method: readByJobIdAndTaskIdIn(Long jobId, List<Long> taskIds)
```yaml
Signature: List<TaskExecution> readByJobIdAndTaskIdIn(Long jobId, List<Long> taskIds)
Purpose: "Read task executions with EntityGraph for specific tasks in a job with assignee information"

Business Logic Derivation:
  1. Used in TaskExecutionService for task sign-off operations with assignee context
  2. Loads task executions with assignee mappings using EntityGraph for performance
  3. Critical for bulk task operations that need user assignment information
  4. Used in task sign-off workflows that require assignee validation
  5. Enables efficient task execution loading with relationship data

SQL Query: |
  SELECT te.* FROM task_executions te
  LEFT JOIN task_execution_user_mapping teum ON te.id = teum.task_executions_id
  LEFT JOIN users u ON teum.users_id = u.id
  WHERE te.jobs_id = ? AND te.tasks_id IN (?)

Parameters:
  - jobId: Long (Job identifier for scoping)
  - taskIds: List<Long> (Task identifiers to load executions for)

Returns: List<TaskExecution> (task executions with loaded assignee relationships)
Transaction: Not Required
Error Handling: Returns empty list if no task executions found
```

#### Method: findEnabledForCorrectionTaskIdsByJobId(Long jobId)
```yaml
Signature: List<Long> findEnabledForCorrectionTaskIdsByJobId(Long jobId)
Purpose: "Get task execution IDs enabled for correction within a job for validation"

Business Logic Derivation:
  1. Used in JobService for job completion validation and correction workflow management
  2. Identifies task executions that have correction enabled to prevent job completion
  3. Critical for job state transition validation and correction workflow tracking
  4. Used in job completion checks to ensure no pending corrections exist
  5. Enables job validation with correction state checking

SQL Query: |
  SELECT te.tasks_id FROM task_executions te
  WHERE te.jobs_id = ? AND te.correction_enabled = true

Parameters:
  - jobId: Long (Job identifier to check for correction-enabled tasks)

Returns: List<Long> (task IDs that have correction enabled)
Transaction: Not Required
Error Handling: Returns empty list if no correction-enabled tasks found
```

#### Method: findNonSignedOffTaskIdsByJobIdAndUserId(Long jobId, Long userId)
```yaml
Signature: List<Long> findNonSignedOffTaskIdsByJobIdAndUserId(Long jobId, Long userId)
Purpose: "Find task IDs not signed off by specific user for sign-off validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for task sign-off operations and validation
  2. Identifies tasks that a specific user has not signed off on
  3. Critical for user-specific sign-off workflow management and validation
  4. Used in bulk sign-off operations to determine pending tasks for user
  5. Enables user-specific task sign-off tracking and validation

SQL Query: |
  SELECT DISTINCT te.tasks_id FROM task_executions te
  INNER JOIN task_execution_user_mapping teum ON te.id = teum.task_executions_id
  WHERE te.jobs_id = ? AND teum.users_id = ? 
    AND te.state NOT IN ('COMPLETED', 'SIGNED_OFF', 'SKIPPED')

Parameters:
  - jobId: Long (Job identifier for scoping)
  - userId: Long (User identifier for sign-off checking)

Returns: List<Long> (task IDs not signed off by the user)
Transaction: Not Required
Error Handling: Returns empty list if all tasks are signed off by user
```

#### Method: getTaskExecutionCountByJobId(Long jobId)
```yaml
Signature: Integer getTaskExecutionCountByJobId(Long jobId)
Purpose: "Get total count of task executions for a job for pagination and metrics"

Business Logic Derivation:
  1. Used in JobService for task execution count reporting and pagination
  2. Provides total task execution count for job metrics and display
  3. Critical for job reporting and task execution analytics
  4. Used in job dashboard for task execution count display
  5. Enables job metrics calculation with total execution count

SQL Query: |
  SELECT COUNT(*) FROM task_executions te
  WHERE te.jobs_id = ?

Parameters:
  - jobId: Long (Job identifier to count task executions for)

Returns: Integer (total count of task executions in the job)
Transaction: Not Required
Error Handling: Returns 0 if no task executions found
```

#### Method: findByJobIdAndStageIdIn(Long jobId, Set<Long> stageIds)
```yaml
Signature: List<TaskExecution> findByJobIdAndStageIdIn(Long jobId, Set<Long> stageIds)
Purpose: "Find task executions for multiple stages within a job for stage-based operations"

Business Logic Derivation:
  1. Used in JobService for multi-stage task execution retrieval and reporting
  2. Enables bulk stage operations and stage-based task execution management
  3. Critical for stage-scoped operations and stage comparison workflows
  4. Used in stage reporting and multi-stage task execution analysis
  5. Supports efficient multi-stage task execution operations

SQL Query: |
  SELECT te.* FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  WHERE te.jobs_id = ? AND t.stages_id IN (?)

Parameters:
  - jobId: Long (Job identifier for scoping)
  - stageIds: Set<Long> (Stage identifiers to find task executions for)

Returns: List<TaskExecution> (task executions for the specified stages)
Transaction: Not Required
Error Handling: Returns empty list if no task executions found for stages
```

#### Method: findTaskExecutionDetailsByJobId(Set<Long> jobIds)
```yaml
Signature: List<JobLogTaskExecutionView> findTaskExecutionDetailsByJobId(Set<Long> jobIds)
Purpose: "Get task execution details for job log reporting and migration operations"

Business Logic Derivation:
  1. Used in JobLogs migration service for job log generation and reporting
  2. Provides comprehensive task execution information for job logging
  3. Critical for job log migration and historical job data processing
  4. Used in job reporting workflows that need detailed execution information
  5. Enables bulk job log processing with task execution details

SQL Query: |
  SELECT te.id as taskExecutionId, te.tasks_id as taskId, t.name as taskName,
         te.state as taskExecutionState, te.started_at as startedAt,
         te.ended_at as endedAt, te.order_tree as orderTree,
         s.name as stageName, s.order_tree as stageOrderTree
  FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  INNER JOIN stages s ON t.stages_id = s.id
  WHERE te.jobs_id IN (?)
  ORDER BY s.order_tree, t.order_tree, te.order_tree

Parameters:
  - jobIds: Set<Long> (Job identifiers to get task execution details for)

Returns: List<JobLogTaskExecutionView> (task execution details for job logging)
Transaction: Not Required
Error Handling: Returns empty list if no task executions found for jobs
```

#### Method: findByTaskIdAndJobIdOrderByOrderTree(Long taskId, Long jobId)
```yaml
Signature: TaskExecution findByTaskIdAndJobIdOrderByOrderTree(Long taskId, Long jobId)
Purpose: "Find latest task execution by order tree for task-specific operations"

Business Logic Derivation:
  1. Used extensively in JobAuditService and TaskAutomationService for latest execution retrieval
  2. Gets the most recent task execution instance for recurring/repeated tasks
  3. Critical for task-specific operations that need current execution context
  4. Used in audit logging and automation workflows for latest task state
  5. Enables latest task execution retrieval with proper ordering

SQL Query: |
  SELECT te.* FROM task_executions te
  WHERE te.tasks_id = ? AND te.jobs_id = ?
  ORDER BY te.order_tree DESC
  LIMIT 1

Parameters:
  - taskId: Long (Task identifier)
  - jobId: Long (Job identifier)

Returns: TaskExecution (latest task execution for the task in job)
Transaction: Not Required
Error Handling: Returns null if no task execution found
```

#### Method: findAllTaskExecutionsNotInCompletedStateByTaskIdAndJobId(Long taskId, Long jobId)
```yaml
Signature: List<TaskExecutionView> findAllTaskExecutionsNotInCompletedStateByTaskIdAndJobId(Long taskId, Long jobId)
Purpose: "Find incomplete task executions for task completion validation"

Business Logic Derivation:
  1. Used in TaskExecutionService and CorrectionService for task state validation
  2. Identifies task executions that are not in completed state for validation
  3. Critical for task completion workflow validation and state checking
  4. Used in correction workflows to validate task execution states
  5. Enables task state validation with incomplete execution identification

SQL Query: |
  SELECT te.id, te.state, te.order_tree, te.tasks_id as taskId
  FROM task_executions te
  WHERE te.tasks_id = ? AND te.jobs_id = ?
    AND te.state NOT IN ('COMPLETED', 'COMPLETED_WITH_EXCEPTION', 'SKIPPED')

Parameters:
  - taskId: Long (Task identifier)
  - jobId: Long (Job identifier)

Returns: List<TaskExecutionView> (task execution views for incomplete executions)
Transaction: Not Required
Error Handling: Returns empty list if all task executions are completed
```

#### Method: deleteByTaskExecutionId(Long id)
```yaml
Signature: void deleteByTaskExecutionId(Long id)
Purpose: "Delete task execution by ID with proper cleanup for task execution management"

Business Logic Derivation:
  1. Used in TaskExecutionService for task execution deletion and cleanup
  2. Enables proper task execution removal with relationship cleanup
  3. Critical for task execution lifecycle management and cleanup operations
  4. Used in task execution deletion workflows with proper cleanup
  5. Supports task execution removal with data integrity maintenance

SQL Query: |
  DELETE FROM task_executions WHERE id = ?

Parameters:
  - id: Long (Task execution identifier to delete)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: setAllTaskExecutionsContinueRecurrenceFalse(Long taskId, Long jobId)
```yaml
Signature: void setAllTaskExecutionsContinueRecurrenceFalse(Long taskId, Long jobId)
Purpose: "Stop recurrence for all task executions of a task to prevent further recurring"

Business Logic Derivation:
  1. Used in TaskExecutionService for recurring task management and control
  2. Stops recurrence for all executions of a task to prevent unwanted repetition
  3. Critical for recurring task lifecycle control and termination
  4. Used in recurring task stop operations and workflow control
  5. Enables bulk recurrence control with efficient update operations

SQL Query: |
  UPDATE task_executions 
  SET continue_recurrence = false 
  WHERE tasks_id = ? AND jobs_id = ?

Parameters:
  - taskId: Long (Task identifier to stop recurrence for)
  - jobId: Long (Job identifier for scoping)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findAllStartedTaskExecutionsAfterStageOrderTree(Integer orderTree, Long jobId)
```yaml
Signature: List<TaskExecutionView> findAllStartedTaskExecutionsAfterStageOrderTree(Integer orderTree, Long jobId)
Purpose: "Find started task executions after stage order for workflow control validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for workflow progression validation and control
  2. Identifies task executions that have started after a specific stage order
  3. Critical for workflow control and stage-based execution validation
  4. Used in workflow progression checks and stage dependency validation
  5. Enables workflow control with stage-based execution tracking

SQL Query: |
  SELECT te.id, te.state, te.tasks_id as taskId, te.order_tree,
         t.name as taskName, s.order_tree as stageOrderTree
  FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  INNER JOIN stages s ON t.stages_id = s.id
  WHERE te.jobs_id = ? AND s.order_tree > ?
    AND te.state IN ('IN_PROGRESS', 'COMPLETED', 'COMPLETED_WITH_EXCEPTION')

Parameters:
  - orderTree: Integer (Stage order tree threshold)
  - jobId: Long (Job identifier for scoping)

Returns: List<TaskExecutionView> (task execution views for started executions after stage)
Transaction: Not Required
Error Handling: Returns empty list if no started executions found after stage
```

#### Method: findAllNonCompletedTaskExecutionBeforeCurrentStageAndHasStop(Integer orderTree, Long jobId)
```yaml
Signature: List<TaskExecutionView> findAllNonCompletedTaskExecutionBeforeCurrentStageAndHasStop(Integer orderTree, Long jobId)
Purpose: "Find incomplete task executions with stop flag before current stage for workflow control"

Business Logic Derivation:
  1. Used in TaskExecutionService for workflow control and stage progression validation
  2. Identifies incomplete task executions with stop conditions before current stage
  3. Critical for workflow control and stage dependency management
  4. Used in stage progression validation with stop condition checking
  5. Enables workflow control with incomplete execution and stop flag validation

SQL Query: |
  SELECT te.id, te.state, te.tasks_id as taskId, te.order_tree,
         t.name as taskName, s.order_tree as stageOrderTree
  FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  INNER JOIN stages s ON t.stages_id = s.id
  WHERE te.jobs_id = ? AND s.order_tree < ?
    AND te.state NOT IN ('COMPLETED', 'COMPLETED_WITH_EXCEPTION', 'SKIPPED')
    AND t.has_stop = true

Parameters:
  - orderTree: Integer (Current stage order tree)
  - jobId: Long (Job identifier for scoping)

Returns: List<TaskExecutionView> (task execution views for incomplete executions with stop flag)
Transaction: Not Required
Error Handling: Returns empty list if no incomplete executions with stop flag found
```

#### Method: findAllTaskExecutionsWithJobSchedule(Long jobId)
```yaml
Signature: List<TaskExecution> findAllTaskExecutionsWithJobSchedule(Long jobId)
Purpose: "Find scheduled task executions for job scheduling operations and management"

Business Logic Derivation:
  1. Used in JobService for job scheduling operations and scheduled task management
  2. Identifies task executions that are part of job scheduling workflows
  3. Critical for job scheduling management and scheduled execution tracking
  4. Used in job scheduling operations for task execution scheduling updates
  5. Enables scheduled task execution management with job-level scheduling

SQL Query: |
  SELECT te.* FROM task_executions te
  WHERE te.jobs_id = ? AND te.is_scheduled = true

Parameters:
  - jobId: Long (Job identifier to find scheduled task executions for)

Returns: List<TaskExecution> (task executions that are scheduled)
Transaction: Not Required
Error Handling: Returns empty list if no scheduled task executions found
```

#### Method: findAllStartedTaskExecutionsOfStage(Long jobId, Integer taskOrderTree, Long stageId)
```yaml
Signature: List<TaskExecutionView> findAllStartedTaskExecutionsOfStage(Long jobId, Integer taskOrderTree, Long stageId)
Purpose: "Find started task executions within specific stage after task order for validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for stage-level workflow control and validation
  2. Identifies started task executions within a stage after specific task order
  3. Critical for stage-level workflow control and task dependency validation
  4. Used in stage progression validation with task-level ordering
  5. Enables stage-scoped workflow control with task execution tracking

SQL Query: |
  SELECT te.id, te.state, te.tasks_id as taskId, te.order_tree,
         t.name as taskName, t.order_tree as taskOrderTree
  FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  WHERE te.jobs_id = ? AND t.stages_id = ?
    AND t.order_tree > ?
    AND te.state IN ('IN_PROGRESS', 'COMPLETED', 'COMPLETED_WITH_EXCEPTION')

Parameters:
  - jobId: Long (Job identifier for scoping)
  - taskOrderTree: Integer (Task order tree threshold)
  - stageId: Long (Stage identifier for scoping)

Returns: List<TaskExecutionView> (task execution views for started executions in stage after task order)
Transaction: Not Required
Error Handling: Returns empty list if no started executions found in stage after task order
```

#### Method: findAllNonCompletedTaskExecutionOfCurrentStageAndHasStop(Long stageId, Integer orderTree, Long jobId)
```yaml
Signature: List<TaskExecutionView> findAllNonCompletedTaskExecutionOfCurrentStageAndHasStop(Long stageId, Integer orderTree, Long jobId)
Purpose: "Find incomplete task executions with stop flag in current stage for workflow control"

Business Logic Derivation:
  1. Used in TaskExecutionService for stage-level workflow control and validation
  2. Identifies incomplete task executions with stop conditions in current stage
  3. Critical for stage-level workflow control and stop condition management
  4. Used in stage validation with incomplete execution and stop flag checking
  5. Enables stage-scoped workflow control with stop condition validation

SQL Query: |
  SELECT te.id, te.state, te.tasks_id as taskId, te.order_tree,
         t.name as taskName, t.order_tree as taskOrderTree
  FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  WHERE te.jobs_id = ? AND t.stages_id = ?
    AND t.order_tree < ?
    AND te.state NOT IN ('COMPLETED', 'COMPLETED_WITH_EXCEPTION', 'SKIPPED')
    AND t.has_stop = true

Parameters:
  - stageId: Long (Stage identifier for scoping)
  - orderTree: Integer (Task order tree threshold)
  - jobId: Long (Job identifier for scoping)

Returns: List<TaskExecutionView> (task execution views for incomplete executions with stop flag in stage)
Transaction: Not Required
Error Handling: Returns empty list if no incomplete executions with stop flag found in stage
```

#### Method: findEnabledForCorrectionTaskExecutionIdsByJobIdAndTaskId(Long jobId, Long taskId)
```yaml
Signature: List<Long> findEnabledForCorrectionTaskExecutionIdsByJobIdAndTaskId(Long jobId, Long taskId)
Purpose: "Get correction-enabled task execution IDs for specific task for correction validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for correction workflow validation and management
  2. Identifies task execution IDs with correction enabled for specific task
  3. Critical for correction workflow validation and correction state checking
  4. Used in correction operations to validate correction-enabled executions
  5. Enables correction validation with task-specific correction tracking

SQL Query: |
  SELECT te.id FROM task_executions te
  WHERE te.jobs_id = ? AND te.tasks_id = ?
    AND te.correction_enabled = true

Parameters:
  - jobId: Long (Job identifier for scoping)
  - taskId: Long (Task identifier to check for correction-enabled executions)

Returns: List<Long> (task execution IDs with correction enabled)
Transaction: Not Required
Error Handling: Returns empty list if no correction-enabled executions found for task
```

#### Method: findAllTaskExecutionsNotInCompletedOrNotInStartedStatedByTaskIdAndJobId(Long taskId, Long jobId)
```yaml
Signature: List<TaskExecutionView> findAllTaskExecutionsNotInCompletedOrNotInStartedStatedByTaskIdAndJobId(Long taskId, Long jobId)
Purpose: "Find task executions not completed or started for comprehensive state validation"

Business Logic Derivation:
  1. Used in TaskExecutionService and CorrectionService for comprehensive state validation
  2. Identifies task executions in intermediate states for validation operations
  3. Critical for task state validation and workflow control operations
  4. Used in correction workflows and task state management operations
  5. Enables comprehensive task state validation with intermediate state checking

SQL Query: |
  SELECT te.id, te.state, te.order_tree, te.tasks_id as taskId
  FROM task_executions te
  WHERE te.tasks_id = ? AND te.jobs_id = ?
    AND te.state NOT IN ('COMPLETED', 'COMPLETED_WITH_EXCEPTION', 'SKIPPED', 'NOT_STARTED')

Parameters:
  - taskId: Long (Task identifier)
  - jobId: Long (Job identifier)

Returns: List<TaskExecutionView> (task execution views for executions not completed or started)
Transaction: Not Required
Error Handling: Returns empty list if all executions are completed or not started
```

#### Method: findTaskExecutionEnabledForCorrection(Long taskId, Long jobId)
```yaml
Signature: TaskExecution findTaskExecutionEnabledForCorrection(Long taskId, Long jobId)
Purpose: "Find task execution enabled for correction for correction workflow operations"

Business Logic Derivation:
  1. Used extensively in JobAuditService for correction workflow audit operations
  2. Retrieves task execution that has correction enabled for audit logging
  3. Critical for correction workflow audit trail and correction tracking
  4. Used in correction audit operations that need correction-enabled execution
  5. Enables correction workflow tracking with execution-specific context

SQL Query: |
  SELECT te.* FROM task_executions te
  WHERE te.tasks_id = ? AND te.jobs_id = ?
    AND te.correction_enabled = true
  LIMIT 1

Parameters:
  - taskId: Long (Task identifier)
  - jobId: Long (Job identifier)

Returns: TaskExecution (task execution with correction enabled)
Transaction: Not Required
Error Handling: Returns null if no correction-enabled execution found
```

#### Method: getEngagedUsersForJob(Set<Long> jobIds)
```yaml
Signature: List<EngagedUserView> getEngagedUsersForJob(Set<Long> jobIds)
Purpose: "Get users engaged in job executions for user engagement reporting"

Business Logic Derivation:
  1. Used in JobService for user engagement reporting and job analytics
  2. Identifies users who are actively engaged in job execution workflows
  3. Critical for job user engagement metrics and reporting
  4. Used in job dashboard for user engagement visualization
  5. Enables job user engagement tracking with comprehensive user information

SQL Query: |
  SELECT DISTINCT u.id as userId, u.first_name as firstName, u.last_name as lastName,
         te.jobs_id as jobId
  FROM task_executions te
  INNER JOIN task_execution_user_mapping teum ON te.id = teum.task_executions_id
  INNER JOIN users u ON teum.users_id = u.id
  WHERE te.jobs_id IN (?)
    AND te.state IN ('IN_PROGRESS', 'COMPLETED', 'COMPLETED_WITH_EXCEPTION')

Parameters:
  - jobIds: Set<Long> (Job identifiers to get engaged users for)

Returns: List<EngagedUserView> (user engagement information for jobs)
Transaction: Not Required
Error Handling: Returns empty list if no engaged users found for jobs
```

#### Method: findIncompleteDependencies(Long taskId, Long jobId)
```yaml
Signature: List<TaskExecution> findIncompleteDependencies(Long taskId, Long jobId)
Purpose: "Find incomplete prerequisite task executions for dependency validation"

Business Logic Derivation:
  1. Used in TaskExecutionService and NotificationService for dependency validation
  2. Identifies incomplete prerequisite task executions that block task execution
  3. Critical for task dependency validation and workflow progression control
  4. Used in task execution validation to ensure dependencies are met
  5. Enables dependency-based workflow control with prerequisite validation

SQL Query: |
  SELECT te.* FROM task_executions te
  INNER JOIN task_dependencies td ON te.tasks_id = td.prerequisite_task_id
  WHERE td.task_id = ? AND te.jobs_id = ?
    AND te.state NOT IN ('COMPLETED', 'COMPLETED_WITH_EXCEPTION', 'SKIPPED')

Parameters:
  - taskId: Long (Dependent task identifier)
  - jobId: Long (Job identifier for scoping)

Returns: List<TaskExecution> (incomplete prerequisite task executions)
Transaction: Not Required
Error Handling: Returns empty list if all dependencies are complete
```

#### Method: readByJobId(Long jobId, Long checklistId)
```yaml
Signature: List<TaskExecution> readByJobId(Long jobId, Long checklistId)
Purpose: "Read all task executions for job with checklist context for comprehensive operations"

Business Logic Derivation:
  1. Used in JobService for comprehensive job task execution retrieval and processing
  2. Provides all task executions for job with checklist context validation
  3. Critical for job-level operations that need complete task execution context
  4. Used in job processing workflows that require all task execution data
  5. Enables comprehensive job operations with complete task execution information

SQL Query: |
  SELECT te.* FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  INNER JOIN stages s ON t.stages_id = s.id
  WHERE te.jobs_id = ? AND s.checklists_id = ?
  ORDER BY s.order_tree, t.order_tree, te.order_tree

Parameters:
  - jobId: Long (Job identifier)
  - checklistId: Long (Checklist identifier for validation)

Returns: List<TaskExecution> (all task executions for job ordered by execution sequence)
Transaction: Not Required
Error Handling: Returns empty list if no task executions found for job and checklist
```

#### Method: getAllLatestDependantTaskExecutionIdsHavingPrerequisiteTaskId(Long preRequisiteTaskId, Long jobId)
```yaml
Signature: List<TaskExecutionView> getAllLatestDependantTaskExecutionIdsHavingPrerequisiteTaskId(Long preRequisiteTaskId, Long jobId)
Purpose: "Get dependent task executions for prerequisite task for dependency notification"

Business Logic Derivation:
  1. Used in NotificationService for dependency-based notification workflows
  2. Identifies dependent task executions that depend on a prerequisite task
  3. Critical for dependency notification and workflow progression notifications
  4. Used in task completion notifications to notify dependent task assignees
  5. Enables dependency-based notification with dependent task identification

SQL Query: |
  SELECT te.id, te.state, te.tasks_id as taskId, t.name as taskName
  FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  INNER JOIN task_dependencies td ON t.id = td.task_id
  WHERE td.prerequisite_task_id = ? AND te.jobs_id = ?
  ORDER BY te.order_tree DESC

Parameters:
  - preRequisiteTaskId: Long (Prerequisite task identifier)
  - jobId: Long (Job identifier for scoping)

Returns: List<TaskExecutionView> (dependent task execution views)
Transaction: Not Required
Error Handling: Returns empty list if no dependent task executions found
```

#### Method: getAllCompletedPreRequisiteTaskDetails(Long dependantTaskId, Long jobId)
```yaml
Signature: List<TaskExecutionView> getAllCompletedPreRequisiteTaskDetails(Long dependantTaskId, Long jobId)
Purpose: "Get completed prerequisite task details for dependency notification and reporting"

Business Logic Derivation:
  1. Used in NotificationService for dependency completion notification workflows
  2. Provides completed prerequisite task information for notification context
  3. Critical for dependency completion notifications and reporting
  4. Used in notification workflows that need prerequisite completion details
  5. Enables dependency notification with prerequisite completion information

SQL Query: |
  SELECT te.id, te.state, te.tasks_id as taskId, t.name as taskName,
         te.ended_at as completedAt
  FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  INNER JOIN task_dependencies td ON t.id = td.prerequisite_task_id
  WHERE td.task_id = ? AND te.jobs_id = ?
    AND te.state IN ('COMPLETED', 'COMPLETED_WITH_EXCEPTION')
  ORDER BY te.ended_at DESC

Parameters:
  - dependantTaskId: Long (Dependent task identifier)
  - jobId: Long (Job identifier for scoping)

Returns: List<TaskExecutionView> (completed prerequisite task execution views)
Transaction: Not Required
Error Handling: Returns empty list if no completed prerequisites found
```

#### Method: getTaskExecutionsLiteByJobId(Long jobId)
```yaml
Signature: List<TaskExecutionLiteView> getTaskExecutionsLiteByJobId(Long jobId)
Purpose: "Get lightweight task execution data for job reporting and performance optimization"

Business Logic Derivation:
  1. Used in JobService for lightweight job reporting and task execution overview
  2. Provides essential task execution information with minimal data loading
  3. Critical for job dashboard and task execution summary operations
  4. Used in job reporting workflows that need task execution overview without full details
  5. Enables efficient job reporting with lightweight task execution information

SQL Query: |
  SELECT te.id, te.state, te.tasks_id as taskId, t.name as taskName,
         te.started_at as startedAt, te.ended_at as endedAt
  FROM task_executions te
  INNER JOIN tasks t ON te.tasks_id = t.id
  WHERE te.jobs_id = ?
  ORDER BY te.order_tree

Parameters:
  - jobId: Long (Job identifier to get lightweight task execution data for)

Returns: List<TaskExecutionLiteView> (lightweight task execution projection views)
Transaction: Not Required
Error Handling: Returns empty list if no task executions found for job
```

#### Method: getTaskPauseResumeAuditDtoByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: List<TaskPauseResumeAuditView> getTaskPauseResumeAuditDtoByTaskExecutionId(Long taskExecutionId)
Purpose: "Get task pause/resume audit information for task execution audit trail"

Business Logic Derivation:
  1. Used in TaskExecutionService for task execution audit trail and pause/resume tracking
  2. Provides audit information for task execution pause and resume operations
  3. Critical for task execution audit trail and workflow time tracking
  4. Used in task execution audit workflows for pause/resume history display
  5. Enables task execution audit with comprehensive pause/resume tracking information

SQL Query: |
  SELECT te.id as taskExecutionId, te.state, te.started_at as startedAt,
         te.ended_at as endedAt, te.reason as pauseResumeReason,
         u_started.first_name as startedByFirstName, u_started.last_name as startedByLastName,
         u_ended.first_name as endedByFirstName, u_ended.last_name as endedByLastName
  FROM task_executions te
  LEFT JOIN users u_started ON te.started_by = u_started.id
  LEFT JOIN users u_ended ON te.ended_by = u_ended.id
  WHERE te.id = ?

Parameters:
  - taskExecutionId: Long (Task execution identifier to get pause/resume audit for)

Returns: List<TaskPauseResumeAuditView> (pause/resume audit information projection views)
Transaction: Not Required
Error Handling: Returns empty list if no audit information found for task execution
```

#### Method: findAllByJobIdAndTaskIdIn(Long jobId, Set<String> taskIds)
```yaml
Signature: List<TaskExecution> findAllByJobIdAndTaskIdIn(Long jobId, Set<String> taskIds)
Purpose: "Find task executions by job and string-based task IDs for integration operations"

Business Logic Derivation:
  1. Used in integration services for task execution retrieval with string-based task identifiers
  2. Enables task execution loading for external system integration and data import
  3. Critical for integration workflows that use string-based task identifiers
  4. Used in data migration and integration operations with external task references
  5. Supports integration operations with flexible task identifier handling

SQL Query: |
  SELECT te.* FROM task_executions te
  WHERE te.jobs_id = ? AND CAST(te.tasks_id AS VARCHAR) IN (?)

Parameters:
  - jobId: Long (Job identifier for scoping)
  - taskIds: Set<String> (String-based task identifiers for integration operations)

Returns: List<TaskExecution> (task executions matching job and string task IDs)
Transaction: Not Required
Error Handling: Returns empty list if no task executions found with specified task IDs
```

### Key Repository Usage Patterns

#### Pattern: save() for Task Execution Lifecycle Management
```yaml
Usage: taskExecutionRepository.save(taskExecution)
Purpose: "Create new task executions, update states, and manage execution lifecycle"

Business Logic Derivation:
  1. Used extensively throughout system for task execution state management
  2. Handles task execution creation with proper task and job association
  3. Updates execution states during workflow progression
  4. Critical for task execution lifecycle management and audit tracking
  5. Supports complex execution operations with relationship management

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, readByJobIdAndStageIdOrderByOrderTree, readByJobIdAndTaskIdIn
  - findNonCompletedTaskIdsByJobId, findEnabledForCorrectionTaskIdsByJobId
  - findNonSignedOffTaskIdsByJobIdAndUserId, getTaskExecutionCountByJobId
  - findByJobIdAndStageIdIn, findTaskExecutionDetailsByJobId
  - findByTaskIdAndJobIdAndType, findByTaskIdAndJobIdOrderByOrderTree
  - findAllTaskExecutionsNotInCompletedStateByTaskIdAndJobId
  - checkIfAnyTaskExecutionContainsStopRecurrence, findAllStartedTaskExecutionsAfterStageOrderTree
  - findAllNonCompletedTaskExecutionBeforeCurrentStageAndHasStop, findAllTaskExecutionsWithJobSchedule
  - findAllStartedTaskExecutionsOfStage, findAllNonCompletedTaskExecutionOfCurrentStageAndHasStop
  - findEnabledForCorrectionTaskExecutionIdsByJobIdAndTaskId
  - findAllTaskExecutionsNotInCompletedOrNotInStartedStatedByTaskIdAndJobId
  - findTaskExecutionEnabledForCorrection, getPendingTasksOfUserForJobs, getEngagedUsersForJob
  - findIncompleteDependencies, readByJobId, getAllLatestDependantTaskExecutionIdsHavingPrerequisiteTaskId
  - getAllCompletedPreRequisiteTaskDetails, getTaskExecutionsLiteByJobId
  - getTaskPauseResumeAuditDtoByTaskExecutionId, findAllByJobIdAndTaskIdIn
  - existsById, count

Transactional Methods:
  - save, delete, deleteById, enableCorrection, cancelCorrection
  - deleteByTaskExecutionId, setAllTaskExecutionsContinueRecurrenceFalse

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid tasks_id, jobs_id)
    * NOT NULL constraint violations (state, type, tasks_id, jobs_id, orderTree)
    * Invalid enum values for state and type fields
  - EntityNotFoundException: TaskExecution not found by ID or criteria
  - OptimisticLockException: Concurrent task execution modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters
  - ResourceNotFoundException: TaskExecution not found during operations

Validation Rules:
  - state: Required, must be valid TaskExecution enum value
  - type: Required, must be valid TaskExecutionType enum value (MASTER, REPEAT, etc.)
  - task: Required, must reference existing task, immutable after creation
  - job: Required, must reference existing job, immutable after creation
  - orderTree: Required, must be positive integer, should be unique within job/task scope
  - startedAt: Optional, must be valid timestamp
  - endedAt: Optional, must be valid timestamp, should be after startedAt
  - correctionEnabled: Defaults to false, requires correctionReason when true
  - continueRecurrence: Defaults to false, used for recurring task control
  - scheduled: Defaults to false, indicates if task execution is scheduled

Business Constraints:
  - Cannot modify task or job associations after creation
  - Task execution state transitions must follow defined workflow
  - Cannot delete task execution with active parameter values or corrections
  - Correction enablement requires valid correction reason
  - Recurring task logic must respect continue/stop recurrence flags
  - Task execution order must maintain consistency within job scope
  - Scheduled task executions must have proper scheduling metadata
  - Task execution assignment requires valid user mapping
  - Task execution dependencies must be validated before state transitions
  - Correction workflow must maintain proper audit trail
  - Recurring and scheduling fields must be consistent with task configuration
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TaskExecution repository without JPA/Hibernate dependencies.
