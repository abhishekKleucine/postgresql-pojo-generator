# ITaskDependencyRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TaskDependency (extends UserAuditOptionalBase)
- **Primary Purpose**: Manages task dependency entities for task prerequisite management with dependency tracking, task sequencing, and workflow control functionality
- **Key Relationships**: Links dependent Task with prerequisite Task entities for comprehensive task dependency management and workflow sequencing control
- **Performance Characteristics**: Moderate query volume with task-based dependency retrieval and dependency lifecycle management operations
- **Business Context**: Task workflow management component that provides task prerequisites, dependency tracking, task sequencing, and workflow control functionality for task execution and dependency workflows

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditOptionalBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| dependent_task_id | dependentTask.id | Long | false | null | Foreign key to tasks, task that has dependencies |
| prerequisite_task_id | prerequisiteTask.id | Long | false | null | Foreign key to tasks, task that must be completed first |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | dependentTask | Task | LAZY | Task that depends on prerequisite, not null |
| @ManyToOne | prerequisiteTask | Task | LAZY | Task that must be completed first, not null |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(TaskDependency entity)`
- `saveAll(Iterable<TaskDependency> entities)`
- `deleteById(Long id)`
- `delete(TaskDependency entity)`
- `deleteAll(Iterable<TaskDependency> entities)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (3 methods - ALL methods documented)

**Task Dependency Retrieval Methods:**
- `findAllByDependentTaskId(Long taskId)`
- `findAllByDependentTaskIdIn(List<Long> taskIds)`

**Bulk Dependency Management Methods:**
- `save(List<TaskDependency> taskDependencies)` (returns List<TaskDependency>)

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<TaskDependency> findById(Long id)
List<TaskDependency> findAll()
TaskDependency save(TaskDependency entity)
List<TaskDependency> saveAll(Iterable<TaskDependency> entities)
void deleteById(Long id)
void delete(TaskDependency entity)
void deleteAll(Iterable<TaskDependency> entities)
boolean existsById(Long id)
long count()
```

### Task Dependency Retrieval Methods

#### Method: findAllByDependentTaskId(Long taskId)
```yaml
Signature: List<TaskDependency> findAllByDependentTaskId(Long taskId)
Purpose: "Find task dependencies by dependent task ID for task prerequisite management and dependency tracking"

Business Logic Derivation:
  1. Used extensively in TaskDependencyService for prerequisite task retrieval during dependency management and workflow control operations
  2. Provides task-specific dependency access for workflow management enabling comprehensive prerequisite tracking and task functionality
  3. Critical for task workflow operations requiring prerequisite identification for task sequencing and dependency management
  4. Used in task management workflows for accessing task prerequisites for dependency operations and workflow processing
  5. Enables task dependency management with prerequisite identification for comprehensive workflow processing and dependency control

SQL Query: |
  SELECT td.* FROM task_dependencies td
  WHERE td.dependent_task_id = ?

Parameters:
  - taskId: Long (Dependent task identifier for prerequisite retrieval)

Returns: List<TaskDependency> (task dependencies where specified task is the dependent task)
Transaction: Not Required
Error Handling: Returns empty list if no dependencies found for task
```

#### Method: findAllByDependentTaskIdIn(List<Long> taskIds)
```yaml
Signature: List<TaskDependency> findAllByDependentTaskIdIn(List<Long> taskIds)
Purpose: "Find task dependencies by multiple dependent task IDs for bulk dependency management and batch workflow operations"

Business Logic Derivation:
  1. Used in TaskDependencyService for bulk task dependency retrieval during batch dependency management and workflow control operations
  2. Provides efficient bulk dependency access for workflow management enabling comprehensive batch prerequisite tracking and task functionality
  3. Critical for bulk workflow operations requiring multiple prerequisite identification for batch task sequencing and dependency management
  4. Used in batch workflow processing for accessing multiple task prerequisites for bulk dependency operations and workflow management
  5. Enables bulk dependency management with efficient prerequisite retrieval for comprehensive batch workflow processing and dependency control

SQL Query: |
  SELECT td.* FROM task_dependencies td
  WHERE td.dependent_task_id IN (?, ?, ?, ...)

Parameters:
  - taskIds: List<Long> (List of dependent task identifiers for bulk prerequisite retrieval)

Returns: List<TaskDependency> (task dependencies for all specified dependent tasks)
Transaction: Not Required
Error Handling: Returns empty list if no dependencies found for any of the tasks
```

### Bulk Dependency Management Methods

#### Method: save(List<TaskDependency> taskDependencies)
```yaml
Signature: List<TaskDependency> save(List<TaskDependency> taskDependencies)
Purpose: "Save task dependencies in bulk for dependency management and workflow setup operations"

Business Logic Derivation:
  1. Used in TaskDependencyService for bulk dependency creation during workflow setup and dependency management operations
  2. Provides efficient bulk dependency persistence for workflow management enabling comprehensive dependency creation and task functionality
  3. Critical for workflow setup operations requiring bulk dependency creation for task sequencing and dependency management
  4. Used in dependency management workflows for bulk dependency creation and workflow setup operations
  5. Enables dependency management with efficient bulk persistence for comprehensive workflow processing and dependency control

SQL Query: |
  INSERT INTO task_dependencies (dependent_task_id, prerequisite_task_id, created_at, modified_at, created_by, modified_by)
  VALUES (?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?), ...
  RETURNING *

Parameters:
  - taskDependencies: List<TaskDependency> (List of task dependencies for bulk creation)

Returns: List<TaskDependency> (saved task dependencies with generated IDs)
Transaction: Required
Error Handling: DataIntegrityViolationException for bulk constraint violations, dependency integrity issues
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Dependency Creation
```yaml
Usage: taskDependencyRepository.saveAll(addedDependencies)
Purpose: "Create task dependencies in bulk for workflow setup and dependency management operations"

Business Logic Derivation:
  1. Used extensively in TaskDependencyService for bulk dependency creation during workflow configuration and dependency setup operations
  2. Provides efficient bulk dependency persistence for workflow management enabling comprehensive dependency creation and task functionality
  3. Critical for workflow configuration operations requiring bulk dependency creation for task sequencing and dependency management
  4. Used in dependency configuration workflows for bulk dependency creation and workflow setup operations
  5. Enables dependency configuration with efficient bulk operations for comprehensive workflow processing and dependency control

Transaction: Required
Error Handling: DataIntegrityViolationException for bulk constraint violations, dependency configuration conflicts
```

#### Pattern: deleteAll() for Dependency Cleanup
```yaml
Usage: taskDependencyRepository.deleteAll(toRemove)
Purpose: "Delete task dependencies in bulk for dependency cleanup and workflow management operations"

Business Logic Derivation:
  1. Used extensively in TaskDependencyService and TaskService for bulk dependency deletion during workflow cleanup and dependency management operations
  2. Provides efficient bulk dependency cleanup for workflow management enabling comprehensive dependency removal and task functionality
  3. Critical for workflow management operations requiring bulk dependency cleanup for task sequencing and dependency control
  4. Used in dependency management workflows for bulk dependency removal and workflow cleanup operations
  5. Enables dependency management with efficient bulk cleanup for comprehensive workflow processing and dependency control

Transaction: Required
Error Handling: No exception if dependencies not found for cleanup criteria
```

#### Pattern: Prerequisite Task Discovery and Management
```yaml
Usage: Task prerequisite discovery for workflow management and task sequencing
Purpose: "Discover task prerequisites for comprehensive workflow management and task sequencing functionality"

Business Logic Derivation:
  1. Task prerequisite discovery workflows enable proper task sequencing through dependency identification and workflow management functionality
  2. Prerequisite management supports workflow requirements and dependency functionality for task processing workflows
  3. Task dependency operations depend on prerequisite identification for proper workflow management and task sequencing
  4. Workflow processing requires prerequisite management for comprehensive task functionality and dependency control
  5. Dependency processing requires comprehensive prerequisite access and workflow functionality for task management

Transaction: Not Required for discovery and prerequisite identification operations
Error Handling: Dependency discovery error handling and prerequisite identification validation
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Task Dependency Lifecycle Management
```yaml
Usage: Complete dependency lifecycle for task prerequisite management and workflow control
Purpose: "Manage task dependencies for comprehensive workflow lifecycle functionality and task sequencing processing"

Business Logic Derivation:
  1. Task dependency lifecycle management provides workflow functionality through dependency creation, management, and cleanup operations
  2. Dependency lifecycle includes dependency creation, prerequisite identification, management operations, and cleanup workflows for task control
  3. Dependency management operations require task workflow processing for dependency lifecycle and task control
  4. Task dependency operations enable comprehensive workflow functionality with lifecycle capabilities and dependency management
  5. Dependency lifecycle management supports task requirements and workflow functionality for task dependency processing

Common Usage Examples:
  - taskDependencyRepository.findAllByDependentTaskId() in TaskDependencyService for prerequisite retrieval during dependency management
  - taskDependencyRepository.saveAll() for bulk dependency creation during workflow setup and dependency configuration
  - taskDependencyRepository.deleteAll() for bulk dependency cleanup during workflow management and dependency removal
  - taskDependencyRepository.findAllByDependentTaskIdIn() for bulk dependency retrieval during batch workflow processing
  - Comprehensive task dependency management with lifecycle control and workflow functionality

Transaction: Required for lifecycle operations and dependency management
Error Handling: Task dependency error handling and lifecycle validation verification
```

### Pattern: Workflow Prerequisite Management and Task Sequencing
```yaml
Usage: Workflow prerequisite management for task sequencing and workflow control functionality
Purpose: "Manage workflow prerequisites for comprehensive task sequencing functionality and workflow processing"

Business Logic Derivation:
  1. Workflow prerequisite management operations require comprehensive dependency access for task sequencing and workflow functionality
  2. Prerequisite management supports workflow requirements and sequencing functionality for workflow processing workflows
  3. Task prerequisite operations ensure proper workflow sequencing through prerequisite management and dependency control
  4. Workflow sequencing coordinates prerequisite management with task processing for comprehensive workflow operations
  5. Prerequisite management supports workflow requirements and sequencing functionality for comprehensive task workflow management

Common Usage Examples:
  - Prerequisite task identification for workflow sequencing and task management operations
  - Task dependency tracking for workflow control and prerequisite management functionality
  - Workflow sequencing operations for task management and dependency control functionality
  - Task prerequisite validation for workflow processing and dependency management operations
  - Comprehensive workflow prerequisite management with sequencing functionality and dependency control

Transaction: Required for prerequisite management operations and workflow control
Error Handling: Workflow prerequisite operation error handling and sequencing validation verification
```

### Pattern: Bulk Dependency Operations and Batch Workflow Management
```yaml
Usage: Bulk dependency operations for batch workflow management and dependency configuration functionality
Purpose: "Manage bulk task dependencies for comprehensive batch workflow functionality and dependency processing"

Business Logic Derivation:
  1. Bulk dependency operations enable workflow functionality through batch dependency management and configuration control
  2. Batch workflow management supports bulk dependency requirements and workflow functionality for batch processing workflows
  3. Bulk dependency operations ensure proper workflow functionality through dependency management and batch control
  4. Batch workflows coordinate dependency management with workflow processing for comprehensive batch operations
  5. Bulk dependency management supports workflow requirements and batch functionality for comprehensive workflow processing

Common Usage Examples:
  - Bulk dependency creation for workflow configuration and dependency setup operations
  - Batch dependency retrieval for workflow management and dependency processing functionality
  - Bulk dependency cleanup for workflow maintenance and dependency management operations
  - Batch workflow processing with dependency management and workflow functionality
  - Comprehensive bulk dependency management with batch workflow operations for dependency control

Transaction: Required for bulk operations and batch dependency management
Error Handling: Bulk operation error handling and batch dependency validation
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllByDependentTaskId, findAllByDependentTaskIdIn, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById, deleteAll, save(List<TaskDependency>)

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (dependent_task_id, prerequisite_task_id)
    * Foreign key violations (invalid dependent_task_id, prerequisite_task_id references)
    * Unique constraint violations for dependency combinations
    * Circular dependency constraint violations
    * Task dependency integrity constraint violations
  - EntityNotFoundException: Task dependency not found by ID or criteria
  - OptimisticLockException: Concurrent task dependency modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or dependency context
  - ConstraintViolationException: Task dependency constraint violations

Validation Rules:
  - dependentTask: Required, must reference existing task for dependency context
  - prerequisiteTask: Required, must reference existing task for prerequisite context
  - Circular dependencies must be prevented (task cannot depend on itself transitively)
  - Both dependent and prerequisite tasks must be valid and active

Business Constraints:
  - Task dependencies should be unique per dependent-prerequisite task combination for proper dependency integrity
  - Dependent and prerequisite task references must be valid for dependency integrity and workflow functionality
  - Task dependencies must support workflow requirements and dependency functionality
  - Dependency lifecycle management must maintain referential integrity and workflow functionality consistency
  - Dependency management must ensure proper workflow control and dependency functionality
  - Dependency associations must support workflow requirements and dependency functionality for task processing
  - Dependency operations must maintain transaction consistency and constraint integrity for workflow management
  - Dependency lifecycle management must maintain workflow functionality and dependency consistency
  - Workflow management must maintain dependency integrity and workflow requirements
  - Sequencing operations must ensure proper workflow management and dependency control
  - Circular dependencies must be prevented to maintain workflow integrity
```

## Task Dependency Considerations

### Workflow Integration
```yaml
Workflow Integration: Task dependencies enable workflow functionality through prerequisite management and sequencing functionality
Workflow Management: Dependency associations enable workflow functionality with comprehensive prerequisite capabilities
Workflow Lifecycle: Dependency lifecycle includes creation, management, and cleanup operations for workflow functionality
Workflow Management: Comprehensive workflow management for dependency functionality and workflow requirements during task workflows
Sequencing Control: Dependency sequencing control for workflow functionality and lifecycle management in workflow processing
```

### Task Prerequisite Integration
```yaml
Prerequisite Integration: Task prerequisite integration for workflow sequencing functionality through dependency management
Prerequisite Management: Task prerequisite management with dependency tracking and comprehensive prerequisite functionality
Prerequisite Types: Task prerequisite type management for workflow sequencing and prerequisite integration functionality
Task Management: Task prerequisite organization for workflow workflows and task prerequisite management
Task System Integration: Task system integration for workflow prerequisite functionality and task prerequisite management
```

### Dependency Lifecycle Integration
```yaml
Lifecycle Management: Dependency lifecycle management for task dependency tracking and lifecycle functionality
Lifecycle Control: Dependency lifecycle control with management status tracking and comprehensive lifecycle functionality
Management Status: Dependency management status management for workflow workflows and dependency functionality
Lifecycle Tracking: Dependency lifecycle tracking with management control and comprehensive lifecycle functionality
Management Workflow: Management workflow control with lifecycle management and dependency functionality for workflow workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TaskDependency repository without JPA/Hibernate dependencies, focusing on task dependency management and workflow sequencing patterns.
