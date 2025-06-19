# IStageRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Stage
- **Primary Purpose**: Manages workflow stages within checklists, stage ordering, and task organization hierarchies
- **Key Relationships**: Child of Checklist, parent of Tasks; critical for workflow structure and task grouping
- **Performance Characteristics**: Medium query volume with task-stage relationship queries and ordering operations
- **Business Context**: Workflow organization component that groups tasks into logical stages for process execution and progress tracking

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | false | null |
| order_tree | orderTree | Integer | false | null |
| archived | archived | boolean | true | false |
| checklists_id | checklist.id | Long | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | checklist | Checklist | LAZY | Parent checklist, not null, updatable = false |
| @OneToMany | tasks | Set\<Task\> | LAZY | Child tasks, cascade = ALL, ordered by order_tree, filtered by archived = false |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Stage entity)`
- `deleteById(Long id)`
- `delete(Stage entity)`
- `existsById(Long id)`
- `count()`

### Specification Methods
- `findAll(Specification<Stage> spec)`
- `findAll(Specification<Stage> spec, Pageable pageable)`
- `findAll(Specification<Stage> spec, Sort sort)`
- `findOne(Specification<Stage> spec)`
- `count(Specification<Stage> spec)`

### Custom Query Methods
- `reorderStage(Long stageId, Long order, Long userId, Long modifiedAt)`
- `findByTaskId(Long taskId)`
- `findByTaskIds(List<Long> taskIds)`
- `findStageIdByTaskId(Long taskId)`
- `findByChecklistId(Long checklistId)`
- `findByChecklistIdOrderByOrderTree(Long checklistId)`
- `findStagesByJobIdAndAllTaskExecutionStateIn(Long jobId, Set<State.TaskExecution> taskExecutionStates)`
- `increaseOrderTreeByOneAfterStage(Long checklistId, Integer orderTree, Long newElementId)`
- `getStagesByChecklistIdOrdered(Long checklistId)`

## Method Documentation

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Stage> findById(Long id)
List<Stage> findAll()
Stage save(Stage entity)
void deleteById(Long id)
void delete(Stage entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: reorderStage(Long stageId, Long order, Long userId, Long modifiedAt)
```yaml
Signature: void reorderStage(Long stageId, Long order, Long userId, Long modifiedAt)
Purpose: "Update stage order position and audit metadata for workflow reordering"

Business Logic Derivation:
  1. Used in StageService.reorderStages() for stage reorganization operations
  2. Atomically updates order position and audit fields in single operation
  3. Critical for workflow reorganization and stage sequence management
  4. Maintains audit trail during stage reordering operations
  5. Enables efficient batch reordering without loading full entities

SQL Query: |
  UPDATE stages 
  SET order_tree = ?, modified_by = ?, modified_at = ? 
  WHERE id = ?

  BUSINESS LOGIC:
  1. Directly update order_tree column for stage positioning
  2. Update modified_by to track user performing reorder operation
  3. Update modified_at timestamp for audit trail maintenance
  4. Filter by stage ID for precise reordering operation
  5. Atomic operation for consistent stage order management
  6. Essential for workflow structure reorganization

Parameters:
  - stageId: Long (Stage identifier to reorder)
  - order: Long (New order position for the stage)
  - userId: Long (User performing the reorder operation)
  - modifiedAt: Long (Timestamp for modification tracking)

Returns: void
Transaction: Required (uses @Transactional and @Modifying)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findByTaskId(Long taskId)
```yaml
Signature: Stage findByTaskId(Long taskId)
Purpose: "Find parent stage containing a specific task"

Business Logic Derivation:
  1. Used extensively throughout system for task-to-stage navigation
  2. Critical for audit logging, job execution, and task context operations
  3. Enables reverse lookup from task to parent stage for workflow context
  4. Used in job audit service for stage identification and reporting
  5. Essential for maintaining task-stage hierarchy integrity

SQL Query: |
  SELECT s.* FROM stages s 
  INNER JOIN tasks t ON s.id = t.stages_id 
  WHERE t.id = ?

  BUSINESS LOGIC:
  1. Join stages with tasks to traverse hierarchy upward
  2. Filter by task ID to find containing stage
  3. Return full stage entity for context operations
  4. Enables task-centric operations to access stage context
  5. Critical for workflow navigation and hierarchy maintenance

Parameters:
  - taskId: Long (Task identifier to find parent stage for)

Returns: Stage (parent stage containing the task)
Transaction: Not Required
Error Handling: Returns null if task doesn't exist or has no parent stage
```

#### Method: findByTaskIds(List<Long> taskIds)
```yaml
Signature: List<Stage> findByTaskIds(List<Long> taskIds)
Purpose: "Find all stages containing any of the specified tasks"

Business Logic Derivation:
  1. Used in bulk operations for task dependency management
  2. Enables efficient stage retrieval for multiple tasks in single query
  3. Critical for dependency management and workflow validation
  4. Used in task execution service for stage context batch operations
  5. Supports cross-stage dependency analysis and validation

SQL Query: |
  SELECT DISTINCT s.* FROM stages s 
  INNER JOIN tasks t ON s.id = t.stages_id 
  WHERE t.id IN (?)

  BUSINESS LOGIC:
  1. Join stages with tasks for hierarchy traversal
  2. Filter by multiple task IDs using IN clause
  3. Use DISTINCT to avoid duplicate stages when multiple tasks share same stage
  4. Return unique stages containing any of the specified tasks
  5. Enables bulk stage operations for task dependency management

Parameters:
  - taskIds: List<Long> (Task identifiers to find parent stages for)

Returns: List<Stage> (distinct stages containing the specified tasks)
Transaction: Not Required
Error Handling: Returns empty list if no tasks found or tasks have no parent stages
```

#### Method: findStageIdByTaskId(Long taskId)
```yaml
Signature: Long findStageIdByTaskId(Long taskId)
Purpose: "Get stage ID for a specific task for lightweight operations"

Business Logic Derivation:
  1. Used in parameter service for stage context identification
  2. Provides efficient stage ID retrieval without loading full entity
  3. Critical for audit logging and identifier operations
  4. Used when only stage ID is needed for context or validation
  5. Enables lightweight stage identification for parameter operations

SQL Query: |
  SELECT s.id FROM stages s 
  INNER JOIN tasks t ON s.id = t.stages_id 
  WHERE t.id = ?

  BUSINESS LOGIC:
  1. Join stages with tasks for hierarchy navigation
  2. Filter by task ID to find containing stage
  3. Return only stage ID for efficient identifier operations
  4. Avoids loading full entity when only ID is needed
  5. Optimizes performance for identifier-only operations

Parameters:
  - taskId: Long (Task identifier to get stage ID for)

Returns: Long (stage ID containing the task)
Transaction: Not Required
Error Handling: Returns null if task doesn't exist or has no parent stage
```

#### Method: findByChecklistId(Long checklistId)
```yaml
Signature: List<StageTotalTasksView> findByChecklistId(Long checklistId)
Purpose: "Get stage information with task counts for checklist overview"

Business Logic Derivation:
  1. Used in stage report service for job registration and stage tracking
  2. Provides stage metadata with task count information for workflow overview
  3. Critical for job initialization and stage execution setup
  4. Returns projection view optimized for stage summary operations
  5. Enables efficient stage-task counting for workflow management

SQL Query: |
  SELECT s.id as id, s.name as name, s.order_tree as orderTree, 
         COUNT(t.id) as totalTasks
  FROM stages s 
  LEFT JOIN tasks t ON s.id = t.stages_id AND t.archived = false
  WHERE s.checklists_id = ? AND s.archived = false
  GROUP BY s.id, s.name, s.order_tree
  ORDER BY s.order_tree

  BUSINESS LOGIC:
  1. Query stages table with left join to tasks for counting
  2. Filter by checklist ID and exclude archived stages/tasks
  3. Group by stage attributes to enable task counting
  4. Count only non-archived tasks for accurate workflow metrics
  5. Order by order_tree for proper stage sequence
  6. Return projection view with stage info and task counts

Parameters:
  - checklistId: Long (Checklist identifier to get stage summaries for)

Returns: List<StageTotalTasksView> (stage projection views with task counts)
Transaction: Not Required
Error Handling: Returns empty list if no stages found for checklist
```

#### Method: findByChecklistIdOrderByOrderTree(Long checklistId)
```yaml
Signature: List<Stage> findByChecklistIdOrderByOrderTree(Long checklistId)
Purpose: "Get all stages for a checklist in proper workflow order"

Business Logic Derivation:
  1. Used in checklist collaboration service for stage processing
  2. Returns stages in correct workflow sequence for processing
  3. Critical for stage iteration and workflow operations
  4. Used in checklist audit operations for stage context
  5. Enables ordered stage processing for collaboration and audit operations

SQL Query: |
  SELECT s.* FROM stages s 
  WHERE s.checklists_id = ? 
  ORDER BY s.order_tree

Parameters:
  - checklistId: Long (Checklist identifier to get ordered stages for)

Returns: List<Stage> (stages in workflow order)
Transaction: Not Required
Error Handling: Returns empty list if no stages found for checklist
```

#### Method: findStagesByJobIdAndAllTaskExecutionStateIn(Long jobId, Set<State.TaskExecution> taskExecutionStates)
```yaml
Signature: List<Stage> findStagesByJobIdAndAllTaskExecutionStateIn(Long jobId, Set<State.TaskExecution> taskExecutionStates)
Purpose: "Find stages where all tasks are in specified execution states for job completion tracking"

Business Logic Derivation:
  1. Used in job service for stage completion analysis
  2. Identifies stages where all tasks have reached specified states
  3. Critical for stage-level completion tracking and workflow progression
  4. Enables stage completion validation for job execution flow
  5. Supports job completion analysis and stage reporting

SQL Query: |
  SELECT DISTINCT s.* FROM stages s
  INNER JOIN tasks t ON s.id = t.stages_id
  INNER JOIN task_executions te ON t.id = te.tasks_id
  WHERE te.jobs_id = ? 
    AND te.state IN (?)
    AND s.archived = false
    AND t.archived = false
  GROUP BY s.id
  HAVING COUNT(DISTINCT t.id) = (
    SELECT COUNT(DISTINCT t2.id) 
    FROM tasks t2 
    WHERE t2.stages_id = s.id AND t2.archived = false
  )

  BUSINESS LOGIC:
  1. Join stages with tasks and task executions for state checking
  2. Filter by job ID and task execution states
  3. Exclude archived stages and tasks from analysis
  4. Group by stage to enable task counting per stage
  5. Use HAVING clause to ensure ALL tasks in stage match criteria
  6. Subquery validates complete task state coverage per stage

Parameters:
  - jobId: Long (Job identifier for execution context)
  - taskExecutionStates: Set<State.TaskExecution> (Required task execution states)

Returns: List<Stage> (stages where all tasks are in specified states)
Transaction: Not Required
Error Handling: Returns empty list if no stages have all tasks in specified states
```

#### Method: increaseOrderTreeByOneAfterStage(Long checklistId, Integer orderTree, Long newElementId)
```yaml
Signature: void increaseOrderTreeByOneAfterStage(Long checklistId, Integer orderTree, Long newElementId)
Purpose: "Shift stage order positions to make room for new stage insertion"

Business Logic Derivation:
  1. Used in element copy service for stage duplication operations
  2. Creates space in stage ordering sequence for new stage insertion
  3. Critical for maintaining proper stage order during stage creation
  4. Enables stage insertion at specific positions without order conflicts
  5. Supports stage copying and workflow reorganization operations

SQL Query: |
  UPDATE stages 
  SET order_tree = order_tree + 1 
  WHERE checklists_id = ? 
    AND order_tree > ? 
    AND id != ?

  BUSINESS LOGIC:
  1. Update stages table to increment order_tree values
  2. Filter by checklist ID to scope operation to specific checklist
  3. Filter by order_tree > threshold to shift only subsequent stages
  4. Exclude new element ID to avoid shifting the newly inserted stage
  5. Creates gap in ordering sequence for proper stage insertion
  6. Maintains workflow ordering integrity during stage operations

Parameters:
  - checklistId: Long (Checklist to perform order shifting in)
  - orderTree: Integer (Order position threshold for shifting)
  - newElementId: Long (New stage ID to exclude from shifting)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: getStagesByChecklistIdOrdered(Long checklistId)
```yaml
Signature: List<StageLiteView> getStagesByChecklistIdOrdered(Long checklistId)
Purpose: "Get lightweight stage information for job display operations"

Business Logic Derivation:
  1. Used in job service for stage information display
  2. Provides optimized stage data for UI and reporting operations
  3. Critical for job execution displays and stage progress tracking
  4. Returns projection view for efficient data transfer
  5. Enables lightweight stage operations without loading full entities

SQL Query: |
  SELECT s.id AS id, s.name AS name, s.order_tree AS orderTree 
  FROM stages s 
  WHERE s.checklists_id = ? 
    AND s.archived = false 
  ORDER BY s.order_tree ASC

Parameters:
  - checklistId: Long (Checklist identifier to get stage information for)

Returns: List<StageLiteView> (lightweight stage projection views)
Transaction: Not Required
Error Handling: Returns empty list if no non-archived stages found
```

### Key Repository Usage Patterns (Based on Codebase Analysis)

#### Pattern: save() for Stage Creation and Updates
```yaml
Usage: stageRepository.save(stage)
Purpose: "Create new stages and update existing stage properties"

Business Logic Derivation:
  1. Used extensively in StageService for stage creation and modification
  2. Handles stage creation with proper checklist association
  3. Updates stage properties like name and maintains audit trail
  4. Critical for stage lifecycle management and workflow building
  5. Supports complex stage operations with task relationships

Common Usage Examples:
  - Stage creation: Save new stage with checklist association and order
  - Property updates: Save stage with modified name or metadata
  - Archive operations: Save stage with archived flag set

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: saveAll() for Bulk Stage Operations
```yaml
Usage: stageRepository.saveAll(stages)
Purpose: "Bulk create or update stages for efficiency"

Business Logic Derivation:
  1. Used in checklist collaboration service for bulk stage processing
  2. Used in import/export service for checklist creation
  3. Optimizes database operations for multi-stage workflows
  4. Ensures transactional consistency for complex stage operations
  5. Enables efficient stage batch operations

Transaction: Required
Error Handling: Batch operation rollback on any failure
```

#### Pattern: findByTaskId() for Context Navigation
```yaml
Usage: stageRepository.findByTaskId(taskId)
Purpose: "Navigate from task context to parent stage for workflow operations"

Business Logic Derivation:
  1. Used extensively in job audit service for stage context
  2. Critical for task execution operations that need stage information
  3. Enables audit logging with proper stage context
  4. Used in parameter operations for stage identification
  5. Essential for maintaining workflow hierarchy and context

Transaction: Not Required
Error Handling: Handles null returns for orphaned tasks
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAll(Specification), findByTaskId, findByTaskIds
  - findStageIdByTaskId, findByChecklistId, findByChecklistIdOrderByOrderTree
  - findStagesByJobIdAndAllTaskExecutionStateIn, getStagesByChecklistIdOrdered
  - existsById, count

Transactional Methods:
  - save, delete, deleteById, reorderStage, increaseOrderTreeByOneAfterStage

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid checklists_id)
    * NOT NULL constraint violations (name, orderTree, checklists_id)
    * Unique constraint violations for order_tree within checklist
  - EntityNotFoundException: Stage not found by ID or criteria
  - OptimisticLockException: Concurrent stage modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or specification criteria

Validation Rules:
  - name: Required, max length 512 characters
  - orderTree: Required, must be positive integer, should be unique within checklist
  - archived: Optional, defaults to false
  - checklist: Required, must reference existing checklist, immutable (updatable = false)
  - tasks: Cascade ALL operations, maintains parent-child integrity

Business Constraints:
  - Cannot modify checklist association after creation (updatable = false)
  - Order tree should be unique within checklist scope for proper sequencing
  - Cannot delete stage with active tasks or executions
  - Stage order changes must maintain sequence integrity
  - Archived stages should not participate in active workflows
  - Stage names should be descriptive for workflow clarity
```

This comprehensive documentation provides everything needed to implement an exact DAO layer replacement for the Stage repository without JPA/Hibernate dependencies.
