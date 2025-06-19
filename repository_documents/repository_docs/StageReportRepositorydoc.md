# IStageReportRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: StageExecutionReport (extends BaseEntity)
- **Primary Purpose**: Manages stage execution report entities for job stage tracking with task completion counting, stage progress management, and execution reporting functionality
- **Key Relationships**: References Job and Stage entities for comprehensive job stage execution tracking and progress reporting
- **Performance Characteristics**: Medium query volume with stage progress updates, task completion counting, and job stage reporting operations
- **Business Context**: Stage execution reporting component that provides job stage progress tracking, task completion monitoring, stage state management, and execution reporting functionality for workflow execution monitoring

## Entity Mapping Documentation

### Field Mappings (Inherits from BaseEntity)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| jobs_id | jobId | Long | false | null | Foreign key to jobs, not updatable |
| stages_id | stageId | Long | false | null | Foreign key to stages, not updatable |
| stage_name | stageName | String | true | null | Stage name, not updatable |
| total_tasks | totalTasks | Integer | false | 0 | Total task count for stage |
| completed_tasks | completedTasks | Integer | false | null | Completed task count |
| tasks_in_progress | tasksInProgress | Boolean | true | false | Stage progress status |
| created_at | createdAt | Long | false | current_timestamp | Creation timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp | Modification timestamp |

### Relationships
- **References**: Job entity via jobId for job stage association
- **References**: Stage entity via stageId for stage execution tracking
- **Reporting Entity**: Aggregates execution data for job stage progress reporting

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(StageExecutionReport entity)`
- `saveAll(Iterable<StageExecutionReport> entities)`
- `deleteById(Long id)`
- `delete(StageExecutionReport entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (4 methods - ALL methods documented)

**Stage Report Retrieval Methods (1 method):**
- `findByJobId(Long jobId)`

**Stage Progress Management Methods (3 methods):**
- `incrementTaskCompleteCount(Long jobId, Long stageId)`
- `updateStageToInProgress(Long jobId, Long stageId)`
- `deleteStagesForJob(Long jobId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<StageExecutionReport> findById(Long id)
List<StageExecutionReport> findAll()
StageExecutionReport save(StageExecutionReport entity)
List<StageExecutionReport> saveAll(Iterable<StageExecutionReport> entities)
void deleteById(Long id)
void delete(StageExecutionReport entity)
boolean existsById(Long id)
long count()
```

### Stage Report Retrieval Methods

#### Method: findByJobId(Long jobId)
```yaml
Signature: List<StageExecutionReport> findByJobId(@Param("jobId") Long jobId)
Purpose: "Find stage execution reports by job ID for job stage progress tracking and reporting"

Business Logic Derivation:
  1. Provides job-specific stage execution reports for job execution tracking and stage progress monitoring functionality
  2. Used for job stage progress access during job execution enabling comprehensive stage tracking and job functionality
  3. Critical for job execution operations requiring stage progress access for job management and execution control
  4. Used in job execution workflows for accessing stage reports for tracking operations and job processing
  5. Enables job execution with stage progress access for comprehensive job processing and execution control

JPQL Query: |
  SELECT ser FROM StageExecutionReport ser WHERE ser.jobId = :jobId

Parameters:
  - jobId: Long (Job identifier for stage execution report retrieval)

Returns: List<StageExecutionReport> (stage execution reports for job)
Transaction: Not Required (read operation)
Error Handling: Returns empty list if no stage reports found for job
```

### Stage Progress Management Methods

#### Method: incrementTaskCompleteCount(Long jobId, Long stageId)
```yaml
Signature: void incrementTaskCompleteCount(@Param("jobId") Long jobId, @Param("stageId") Long stageId)
Purpose: "Increment completed task count for stage execution progress tracking and task completion monitoring"

Business Logic Derivation:
  1. Used for task completion tracking during job execution enabling comprehensive task progress monitoring and stage functionality
  2. Provides task completion counting for stage workflows enabling comprehensive completion tracking and stage functionality
  3. Critical for stage execution operations requiring task completion tracking for stage management and execution control
  4. Used in task execution workflows for task completion counting and progress tracking operations
  5. Enables stage execution with task completion tracking for comprehensive stage processing and execution control

Native SQL Query: |
  UPDATE stage_execution_report 
  SET completed_tasks = completed_tasks + 1, modified_at = CURRENT_TIMESTAMP
  WHERE jobs_id = :jobId AND stages_id = :stageId

Parameters:
  - jobId: Long (Job identifier for stage execution context)
  - stageId: Long (Stage identifier for task completion tracking)

Returns: void
Transaction: Required (@Transactional with rollback for Exception.class)
Error Handling: Database constraint violations for invalid job or stage references
```

#### Method: updateStageToInProgress(Long jobId, Long stageId)
```yaml
Signature: void updateStageToInProgress(@Param("jobId") Long jobId, @Param("stageId") Long stageId)
Purpose: "Update stage status to in progress for stage execution state management and progress tracking"

Business Logic Derivation:
  1. Used for stage state management during job execution enabling comprehensive stage progress tracking and state functionality
  2. Provides stage state updates for execution workflows enabling comprehensive state management and execution functionality
  3. Critical for stage execution operations requiring state management for execution management and stage control
  4. Used in stage execution workflows for state updates and progress tracking operations
  5. Enables stage execution with state management for comprehensive execution processing and stage control

Native SQL Query: |
  UPDATE stage_execution_report 
  SET tasks_in_progress = true, modified_at = CURRENT_TIMESTAMP
  WHERE jobs_id = :jobId AND stages_id = :stageId

Parameters:
  - jobId: Long (Job identifier for stage execution context)
  - stageId: Long (Stage identifier for state management)

Returns: void
Transaction: Required (@Transactional with rollback for Exception.class)
Error Handling: Database constraint violations for invalid job or stage references
```

#### Method: deleteStagesForJob(Long jobId)
```yaml
Signature: void deleteStagesForJob(@Param("jobId") Long jobId)
Purpose: "Delete all stage execution reports for job cleanup and data management"

Business Logic Derivation:
  1. Used for job cleanup operations during job lifecycle management enabling comprehensive data cleanup and job functionality
  2. Provides stage report cleanup for job workflows enabling comprehensive cleanup management and job functionality
  3. Critical for job lifecycle operations requiring stage report cleanup for data management and job control
  4. Used in job cleanup workflows for stage report deletion and data cleanup operations
  5. Enables job lifecycle with stage report cleanup for comprehensive job processing and lifecycle control

Native SQL Query: |
  DELETE FROM stage_execution_report 
  WHERE jobs_id = :jobId

Parameters:
  - jobId: Long (Job identifier for stage report cleanup)

Returns: void
Transaction: Required (@Transactional with rollback for Exception.class)
Error Handling: Database constraint violations and referential integrity constraints
```

### Key Repository Usage Patterns

#### Pattern: Stage Progress Tracking and Monitoring
```yaml
Usage: Stage progress tracking for job execution monitoring and stage completion management
Purpose: "Track stage progress for comprehensive execution functionality and monitoring processing"

Business Logic Derivation:
  1. Stage progress tracking management provides execution functionality through task completion monitoring, state management, and progress tracking operations
  2. Progress lifecycle includes task completion counting, state updates, and progress reporting for execution control
  3. Progress management operations require execution processing for progress lifecycle and monitoring control
  4. Execution operations enable comprehensive progress functionality with tracking capabilities and management
  5. Progress lifecycle management supports execution requirements and functionality for stage progress processing

Common Usage Examples:
  - Stage execution report retrieval for job progress monitoring
  - Task completion counting with incrementTaskCompleteCount()
  - Stage state management with updateStageToInProgress()
  - Job cleanup with deleteStagesForJob()

Transaction: Required for modifying operations
Error Handling: Stage progress tracking error handling and execution validation verification
```

#### Pattern: Job Stage Execution Management
```yaml
Usage: Job stage execution management for comprehensive job execution and stage tracking functionality
Purpose: "Manage job stage execution for comprehensive execution functionality and stage processing"

Business Logic Derivation:
  1. Job stage execution management operations require comprehensive stage report access for execution-level stage management and tracking functionality
  2. Execution management supports tracking requirements and functionality for stage processing workflows
  3. Execution-level stage operations ensure proper tracking functionality through stage management and execution control
  4. Job workflows coordinate execution management with stage processing for comprehensive job operations
  5. Execution management supports tracking requirements and functionality for comprehensive job stage management

Common Usage Examples:
  - Job stage progress monitoring and reporting
  - Task completion tracking for stage execution
  - Stage state transitions for execution management
  - Job lifecycle cleanup for data management

Transaction: Required for execution management operations
Error Handling: Job execution error handling and stage validation verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Task Completion and Progress Monitoring
```yaml
Usage: Task completion and progress monitoring for stage execution tracking and monitoring functionality
Purpose: "Monitor task completion for comprehensive tracking functionality and progress processing"

Business Logic Derivation:
  1. Task completion monitoring provides tracking functionality through completion counting, progress tracking, and state management operations
  2. Monitoring lifecycle includes completion counting, progress updates, and tracking management for monitoring control
  3. Monitoring management operations require tracking processing for monitoring lifecycle and completion control
  4. Tracking operations enable comprehensive monitoring functionality with completion capabilities and management
  5. Monitoring lifecycle management supports tracking requirements and functionality for task completion processing

Common Usage Examples:
  - Task completion counting during job execution
  - Stage progress monitoring for execution tracking
  - Progress reporting for job stage status
  - Completion rate calculation for stage execution

Transaction: Required for completion tracking operations
Error Handling: Task completion monitoring error handling and progress validation verification
```

### Pattern: Job Lifecycle and Data Management
```yaml
Usage: Job lifecycle and data management for job execution cleanup and data management functionality
Purpose: "Manage job lifecycle for comprehensive data management functionality and cleanup processing"

Business Logic Derivation:
  1. Job lifecycle management operations require comprehensive stage report access for lifecycle-level data management and cleanup functionality
  2. Lifecycle management supports cleanup requirements and functionality for data processing workflows
  3. Lifecycle-level data operations ensure proper cleanup functionality through data management and lifecycle control
  4. Job workflows coordinate lifecycle management with data processing for comprehensive job operations
  5. Lifecycle management supports cleanup requirements and functionality for comprehensive job data management

Common Usage Examples:
  - Job stage report cleanup during job completion
  - Data management for job lifecycle transitions
  - Stage report deletion for job cleanup
  - Job execution data maintenance

Transaction: Required for lifecycle management operations
Error Handling: Job lifecycle error handling and data validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByJobId, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById
  - incrementTaskCompleteCount, updateStageToInProgress, deleteStagesForJob

Repository-Level Transaction: @Transactional(rollbackFor = Exception.class) for modifying operations
Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (jobs_id, stages_id, total_tasks)
    * Foreign key violations (invalid jobs_id, stages_id references)
    * Stage execution report integrity constraint violations
  - EntityNotFoundException: Stage execution report not found by ID or criteria
  - OptimisticLockException: Concurrent stage execution report modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or execution context
  - ConstraintViolationException: Stage execution report constraint violations

Validation Rules:
  - jobId: Required, must reference existing job for execution context
  - stageId: Required, must reference existing stage for execution tracking
  - stageName: Optional, stage name for reporting purposes
  - totalTasks: Required, non-negative integer for task count, defaults to 0
  - completedTasks: Optional, non-negative integer for completion tracking
  - tasksInProgress: Optional, boolean flag for progress status, defaults to false

Business Constraints:
  - Job and stage references must be valid for stage execution report integrity and tracking functionality
  - Task counts must be non-negative for proper progress tracking
  - Completed task count should not exceed total task count for data integrity
  - Stage execution reports must support job execution requirements and functionality
  - Progress tracking must maintain consistency with actual execution state
  - Stage execution report lifecycle management must maintain referential integrity and tracking functionality consistency
  - Execution tracking must ensure proper monitoring control and functionality
  - Stage execution report associations must support job requirements and functionality for execution processing
  - Progress operations must maintain transaction consistency and constraint integrity for execution management
  - Task completion tracking must maintain execution functionality and consistency
  - Stage state management must maintain execution integrity and progress requirements
```

## Stage Execution Report Management Considerations

### Job Execution Integration
```yaml
Job Tracking: Stage execution reports enable job functionality through stage progress tracking and execution monitoring
Execution Management: Stage execution management enables job functionality with comprehensive tracking capabilities
Progress Lifecycle: Stage progress lifecycle includes tracking creation, completion counting, and state management for execution functionality
Job Management: Comprehensive job management for stage execution functionality and tracking requirements during execution workflows
Execution Control: Stage execution control for job functionality and lifecycle management in execution processing
```

### Stage Progress and State Management
```yaml
Progress Tracking: Stage progress tracking with completion counting and comprehensive progress functionality
State Management: Stage state management with progress status and comprehensive state functionality
Completion Monitoring: Stage completion monitoring with task counting and comprehensive monitoring functionality
Progress Control: Stage progress control with state transitions and comprehensive progress functionality
Execution Status: Stage execution status management with progress tracking and completion functionality for execution workflows
```

### Task Completion and Counting
```yaml
Completion Tracking: Task completion tracking for stage functionality and completion management
Task Counting: Task completion counting with progress tracking and comprehensive counting functionality
Progress Calculation: Task progress calculation with completion rates and comprehensive calculation functionality
Completion Management: Task completion management with counting coordination and comprehensive completion functionality
Progress Monitoring: Task progress monitoring with completion tracking and comprehensive monitoring functionality for stage workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the StageReport repository without JPA/Hibernate dependencies, focusing on stage execution tracking and job progress monitoring patterns.
