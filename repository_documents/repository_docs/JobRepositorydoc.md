# IJobRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Job
- **Primary Purpose**: Manages job instances representing individual workflow executions with state management, scheduling, user assignments, and comprehensive workflow control
- **Key Relationships**: Central workflow orchestration entity linking Checklist, Facility, Organisation, UseCase, TaskExecution, ParameterValue, and comprehensive workflow components
- **Performance Characteristics**: Very high query volume with complex job filtering, state management, scheduling operations, and multi-criteria job discovery with pagination
- **Business Context**: Core workflow execution component that manages individual job instances from creation through completion with comprehensive state tracking, user assignments, scheduling, and approval workflows

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| code | code | String | false | null |
| state | state | State.Job | false | null |
| checklists_id | checklist.id | Long | false | null |
| facilities_id | facility.id | Long | false | null |
| organisations_id | organisation.id | Long | false | null |
| use_cases_id | useCase.id | Long | false | null |
| started_at | startedAt | Long | true | null |
| started_by | startedBy.id | Long | true | null |
| ended_at | endedAt | Long | true | null |
| ended_by | endedBy.id | Long | true | null |
| is_scheduled | scheduled | boolean | false | false |
| schedulers_id | scheduler.id | Long | true | null |
| expected_start_date | expectedStartDate | Long | true | null |
| expected_end_date | expectedEndDate | Long | true | null |
| checklist_ancestor_id | checklistAncestorId | Long | true | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | checklist | Checklist | LAZY | Parent checklist template, not null, immutable |
| @ManyToOne | facility | Facility | LAZY | Parent facility, not null, immutable |
| @ManyToOne | organisation | Organisation | LAZY | Parent organisation, not null |
| @ManyToOne | useCase | UseCase | LAZY | Parent use case, not null, immutable |
| @ManyToOne | startedBy | User | LAZY | User who started the job, cascade = ALL |
| @ManyToOne | endedBy | User | LAZY | User who ended the job, cascade = ALL |
| @OneToOne | scheduler | Scheduler | LAZY | Job scheduler for scheduled jobs |
| @OneToMany | taskExecutions | Set\<TaskExecution\> | LAZY | Job task executions, cascade = ALL |
| @OneToMany | parameterValues | Set\<ParameterValue\> | LAZY | Job parameter values, cascade = ALL |
| @OneToMany | relationValues | Set\<RelationValue\> | LAZY | Job relation values, cascade = ALL |
| @OneToMany | parameterVerifications | Set\<ParameterVerification\> | LAZY | Job parameter verifications, cascade = ALL |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Job entity)`
- `deleteById(Long id)`
- `delete(Job entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<Job> spec)`
- `findAll(Specification<Job> spec, Pageable pageable)`
- `findAll(Specification<Job> spec, Sort sort)`
- `findOne(Specification<Job> spec)`
- `count(Specification<Job> spec)`

### Custom Query Methods (15 methods - ALL methods documented)

- `updateJobToUnassignedIfNoUserAssigned()`
- `findByChecklistIdWhereStateNotIn(Long checklistId, Set<State.Job> jobStates)`
- `findJobProcessInfo(Long jobId)`
- `isJobExistsBySchedulerIdAndDateGreaterThanOrEqualToExpectedStartDate(Long schedulerId, Long date)`
- `getAllPendingForApprovalParameters(long facilityId, String parameterName, String processName, String objectId, String jobId, String userId, Long useCaseId, boolean showAllException, Long requestedBy, Pageable pageable)`
- `findAllByChecklistId(Long checklistId)`
- `getMyJobs(Long organisationId, Long facilityId, Long usecaseId, List<String> jobStates, List<String> taskExecutionStates, Long userId, String objectId, boolean pom, Long checklistAncestorId, String name, String code, int limit, long offset)`
- `countMyJob(Long organisationId, Long facilityId, Long usecaseId, List<String> jobStates, List<String> taskExecutionStates, Long userId, String objectId, boolean pom, Long checklistAncestorId, String name, String code)`
- `findJobsByIdInOrderBy(Set<Long> ids)`
- `getChecklistIdByJobId(Long jobId)`
- `getStateByJobId(Long jobId)`
- `findAllByIdIn(Set<Long> ids)`
- `getFacilityIdByJobId(Long jobId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Job> findById(Long id)
List<Job> findAll()
Job save(Job entity)
void deleteById(Long id)
void delete(Job entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<Job> findAll(Specification<Job> spec)
Page<Job> findAll(Specification<Job> spec, Pageable pageable)
List<Job> findAll(Specification<Job> spec, Sort sort)
Optional<Job> findOne(Specification<Job> spec)
long count(Specification<Job> spec)
```

### Custom Query Methods

#### Method: updateJobToUnassignedIfNoUserAssigned()
```yaml
Signature: void updateJobToUnassignedIfNoUserAssigned()
Purpose: "Update job state to UNASSIGNED if no users are assigned for automated job state management"

Business Logic Derivation:
  1. Used in UserService and UserGroupService for automated job state maintenance during user management operations
  2. Automatically updates job states when user assignments are removed or modified
  3. Critical for job state consistency during user archival, role changes, and user group modifications
  4. Used in user lifecycle operations to ensure jobs don't remain in invalid assignment states
  5. Enables automated job state management with user assignment validation for workflow integrity

SQL Query: |
  UPDATE jobs 
  SET state = 'UNASSIGNED'
  WHERE state = 'ASSIGNED'
    AND id NOT IN (
      SELECT DISTINCT j.id FROM jobs j
      INNER JOIN task_executions te ON j.id = te.jobs_id
      INNER JOIN task_execution_user_mapping teum ON te.id = teum.task_executions_id
      WHERE j.state = 'ASSIGNED'
    )

Parameters: None

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findByChecklistIdWhereStateNotIn(Long checklistId, Set<State.Job> jobStates)
```yaml
Signature: boolean findByChecklistIdWhereStateNotIn(Long checklistId, Set<State.Job> jobStates)
Purpose: "Check if active jobs exist for checklist to prevent checklist archival or deletion"

Business Logic Derivation:
  1. Used in ChecklistService for checklist archival validation and business rule enforcement
  2. Prevents checklist archival when active jobs are still running or incomplete
  3. Critical for data integrity validation and checklist lifecycle management
  4. Used in checklist deletion workflows to ensure no active dependencies exist
  5. Enables checklist lifecycle validation with active job dependency checking for business rule enforcement

SQL Query: |
  SELECT COUNT(*) > 0 FROM jobs j
  WHERE j.checklists_id = ?
    AND j.state NOT IN (?)

Parameters:
  - checklistId: Long (Checklist identifier to check for active jobs)
  - jobStates: Set<State.Job> (Job states to exclude from active check, typically completed states)

Returns: boolean (true if active jobs exist for the checklist)
Transaction: Not Required
Error Handling: Returns false if no active jobs found for checklist
```

#### Method: findJobProcessInfo(Long jobId)
```yaml
Signature: JobProcessInfoView findJobProcessInfo(Long jobId)
Purpose: "Get job process information for task automation and job context operations"

Business Logic Derivation:
  1. Used in TaskAutomationService for task automation context retrieval and automation rule processing
  2. Provides comprehensive job process information including checklist, facility, and organisation context
  3. Critical for task automation workflows that need complete job context for rule evaluation
  4. Used in automation rule processing to access job-level metadata and process information
  5. Enables automation context management with comprehensive job process information for rule execution

SQL Query: |
  SELECT j.id as jobId, j.code as jobCode, j.state as jobState,
         c.id as checklistId, c.name as checklistName, c.code as checklistCode,
         f.id as facilityId, f.name as facilityName,
         o.id as organisationId, o.name as organisationName,
         uc.id as useCaseId, uc.name as useCaseName
  FROM jobs j
  INNER JOIN checklists c ON j.checklists_id = c.id
  INNER JOIN facilities f ON j.facilities_id = f.id
  INNER JOIN organisations o ON j.organisations_id = o.id
  INNER JOIN use_cases uc ON j.use_cases_id = uc.id
  WHERE j.id = ?

Parameters:
  - jobId: Long (Job identifier to get process information for)

Returns: JobProcessInfoView (job process information projection view)
Transaction: Not Required
Error Handling: Returns null if job not found
```

#### Method: isJobExistsBySchedulerIdAndDateGreaterThanOrEqualToExpectedStartDate(Long schedulerId, Long date)
```yaml
Signature: boolean isJobExistsBySchedulerIdAndDateGreaterThanOrEqualToExpectedStartDate(Long schedulerId, Long date)
Purpose: "Check if scheduled job exists for scheduler to prevent duplicate job creation in scheduled workflows"

Business Logic Derivation:
  1. Used in CreateJobService for scheduled job validation and duplicate prevention during job scheduling operations
  2. Prevents creation of duplicate scheduled jobs for the same scheduler and time period
  3. Critical for job scheduling integrity and scheduled workflow management
  4. Used in job scheduling workflows to validate unique scheduled job creation
  5. Enables scheduled job validation with duplicate prevention for scheduling integrity and resource management

SQL Query: |
  SELECT COUNT(*) > 0 FROM jobs j
  WHERE j.schedulers_id = ?
    AND j.expected_start_date >= ?

Parameters:
  - schedulerId: Long (Scheduler identifier to check for existing jobs)
  - date: Long (Date threshold to check for existing scheduled jobs)

Returns: boolean (true if scheduled job exists for scheduler and date range)
Transaction: Not Required
Error Handling: Returns false if no scheduled jobs found for criteria
```

#### Method: getAllPendingForApprovalParameters(long facilityId, String parameterName, String processName, String objectId, String jobId, String userId, Long useCaseId, boolean showAllException, Long requestedBy, Pageable pageable)
```yaml
Signature: Page<PendingForApprovalStatusView> getAllPendingForApprovalParameters(long facilityId, String parameterName, String processName, String objectId, String jobId, String userId, Long useCaseId, boolean showAllException, Long requestedBy, Pageable pageable)
Purpose: "Get paginated pending approval parameters across jobs for approval dashboard and workflow management"

Business Logic Derivation:
  1. Used in JobService for approval workflow dashboard and pending approval parameter management
  2. Provides comprehensive approval workflow status across multiple jobs with advanced filtering
  3. Critical for approval workflow management and approval dashboard functionality
  4. Used in approval workflows for parameter approval tracking and management operations
  5. Enables approval workflow management with complex filtering and pagination for efficient approval processing

SQL Query: |
  SELECT pva.id as approvalId, pv.id as parameterValueId, p.label as parameterName,
         j.code as jobCode, c.name as processName, j.id as jobId,
         pva.approval_status as status, pva.created_at as requestedAt,
         u_requested_by.first_name as requestedByFirstName,
         u_requested_by.last_name as requestedByLastName,
         u_requested_to.first_name as requestedToFirstName,
         u_requested_to.last_name as requestedToLastName,
         eo.external_id as objectId
  FROM parameter_value_approvals pva
  INNER JOIN parameter_values pv ON pva.parameter_values_id = pv.id
  INNER JOIN parameters p ON pv.parameters_id = p.id
  INNER JOIN jobs j ON pv.jobs_id = j.id
  INNER JOIN checklists c ON j.checklists_id = c.id
  INNER JOIN users u_requested_by ON pva.created_by = u_requested_by.id
  LEFT JOIN users u_requested_to ON pva.users_id = u_requested_to.id
  LEFT JOIN entity_objects eo ON pv.entity_object_id = eo.id
  WHERE j.facilities_id = ?
    AND (? IS NULL OR c.use_cases_id = ?)
    AND (? IS NULL OR LOWER(p.label) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(c.name) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(eo.external_id) LIKE LOWER(?))
    AND (? IS NULL OR j.id = CAST(? AS BIGINT))
    AND (? IS NULL OR pva.users_id = CAST(? AS BIGINT))
    AND (? IS NULL OR pva.created_by = ?)
    AND (? = true OR pva.approval_status = 'PENDING')
  ORDER BY pva.created_at DESC

Parameters:
  - facilityId: long (Facility identifier for scoping)
  - parameterName: String (Parameter name filter, nullable)
  - processName: String (Process/checklist name filter, nullable)
  - objectId: String (Entity object ID filter, nullable)
  - jobId: String (Job ID filter as string, nullable)
  - userId: String (User ID filter as string, nullable)
  - useCaseId: Long (Use case identifier for filtering, nullable)
  - showAllException: boolean (Flag to show all approval statuses or only pending)
  - requestedBy: Long (User identifier for approval requester filtering, nullable)
  - pageable: Pageable (Pagination parameters)

Returns: Page<PendingForApprovalStatusView> (paginated pending approval parameter views)
Transaction: Not Required
Error Handling: Returns empty page if no pending approvals match criteria
```

#### Method: findAllByChecklistId(Long checklistId)
```yaml
Signature: List<Job> findAllByChecklistId(Long checklistId)
Purpose: "Find all jobs for checklist for job migration, reporting, and checklist-based operations"

Business Logic Derivation:
  1. Used in JobLogs migration service for job log generation and historical job data processing
  2. Retrieves all job instances created from a specific checklist template
  3. Critical for job migration workflows and checklist-based job analysis
  4. Used in job reporting and analysis workflows that need all jobs for a checklist
  5. Enables checklist-based job operations with comprehensive job retrieval for reporting and migration

SQL Query: |
  SELECT j.* FROM jobs j
  WHERE j.checklists_id = ?
  ORDER BY j.created_at DESC

Parameters:
  - checklistId: Long (Checklist identifier to get all jobs for)

Returns: List<Job> (all jobs created from the checklist)
Transaction: Not Required
Error Handling: Returns empty list if no jobs found for checklist
```

#### Method: getMyJobs(Long organisationId, Long facilityId, Long usecaseId, List<String> jobStates, List<String> taskExecutionStates, Long userId, String objectId, boolean pom, Long checklistAncestorId, String name, String code, int limit, long offset)
```yaml
Signature: List<IdView> getMyJobs(Long organisationId, Long facilityId, Long usecaseId, List<String> jobStates, List<String> taskExecutionStates, Long userId, String objectId, boolean pom, Long checklistAncestorId, String name, String code, int limit, long offset)
Purpose: "Get user-specific job IDs with complex filtering for personalized job dashboard and user workflow management"

Business Logic Derivation:
  1. Used in JobService for personalized job dashboard and user-specific job listing operations
  2. Provides user-centric job filtering with comprehensive search criteria and user assignment validation
  3. Critical for user workflow management and personalized job dashboard functionality
  4. Used in job assignment workflows for user-specific job discovery and management
  5. Enables personalized job management with complex filtering and pagination for efficient user job operations

SQL Query: |
  SELECT DISTINCT j.id FROM jobs j
  INNER JOIN task_executions te ON j.id = te.jobs_id
  INNER JOIN task_execution_user_mapping teum ON te.id = teum.task_executions_id
  INNER JOIN checklists c ON j.checklists_id = c.id
  LEFT JOIN entity_objects eo ON j.entity_object_id = eo.id
  WHERE j.organisations_id = ?
    AND j.facilities_id = ?
    AND (? IS NULL OR c.use_cases_id = ?)
    AND j.state IN (?)
    AND te.state IN (?)
    AND teum.users_id = ?
    AND (? IS NULL OR LOWER(eo.external_id) LIKE LOWER(?))
    AND (? = false OR j.organisations_id = j.organisations_id) -- POM filter logic
    AND (? IS NULL OR j.checklist_ancestor_id = ?)
    AND (? IS NULL OR LOWER(c.name) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(j.code) LIKE LOWER(?))
  ORDER BY j.created_at DESC
  LIMIT ? OFFSET ?

Parameters:
  - organisationId: Long (Organisation identifier for scoping)
  - facilityId: Long (Facility identifier for scoping)
  - usecaseId: Long (Use case identifier for filtering, nullable)
  - jobStates: List<String> (Job states to include in results)
  - taskExecutionStates: List<String> (Task execution states to include in results)
  - userId: Long (User identifier for assignment filtering)
  - objectId: String (Entity object ID filter, nullable)
  - pom: boolean (Plant Operations Management filter flag)
  - checklistAncestorId: Long (Checklist ancestor identifier for filtering, nullable)
  - name: String (Process/checklist name filter, nullable)
  - code: String (Job code filter, nullable)
  - limit: int (Page size for pagination)
  - offset: long (Page offset for pagination)

Returns: List<IdView> (user-specific job ID views matching criteria)
Transaction: Not Required
Error Handling: Returns empty list if no jobs match user criteria
```

#### Method: countMyJob(Long organisationId, Long facilityId, Long usecaseId, List<String> jobStates, List<String> taskExecutionStates, Long userId, String objectId, boolean pom, Long checklistAncestorId, String name, String code)
```yaml
Signature: Long countMyJob(Long organisationId, Long facilityId, Long usecaseId, List<String> jobStates, List<String> taskExecutionStates, Long userId, String objectId, boolean pom, Long checklistAncestorId, String name, String code)
Purpose: "Count user-specific jobs matching filtering criteria for pagination support in personalized job dashboard"

Business Logic Derivation:
  1. Used in JobService for pagination support and total count display in user job dashboard
  2. Provides total count for user-specific job listing with same filtering criteria as getMyJobs
  3. Critical for pagination implementation and total result count display in user workflows
  4. Used in job dashboard for pagination and result count information for user job management
  5. Enables accurate pagination with total count information for user-specific job operations

SQL Query: |
  SELECT COUNT(DISTINCT j.id) FROM jobs j
  INNER JOIN task_executions te ON j.id = te.jobs_id
  INNER JOIN task_execution_user_mapping teum ON te.id = teum.task_executions_id
  INNER JOIN checklists c ON j.checklists_id = c.id
  LEFT JOIN entity_objects eo ON j.entity_object_id = eo.id
  WHERE j.organisations_id = ?
    AND j.facilities_id = ?
    AND (? IS NULL OR c.use_cases_id = ?)
    AND j.state IN (?)
    AND te.state IN (?)
    AND teum.users_id = ?
    AND (? IS NULL OR LOWER(eo.external_id) LIKE LOWER(?))
    AND (? = false OR j.organisations_id = j.organisations_id) -- POM filter logic
    AND (? IS NULL OR j.checklist_ancestor_id = ?)
    AND (? IS NULL OR LOWER(c.name) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(j.code) LIKE LOWER(?))

Parameters:
  - organisationId: Long (Organisation identifier for scoping)
  - facilityId: Long (Facility identifier for scoping)
  - usecaseId: Long (Use case identifier for filtering, nullable)
  - jobStates: List<String> (Job states to include in count)
  - taskExecutionStates: List<String> (Task execution states to include in count)
  - userId: Long (User identifier for assignment filtering)
  - objectId: String (Entity object ID filter, nullable)
  - pom: boolean (Plant Operations Management filter flag)
  - checklistAncestorId: Long (Checklist ancestor identifier for filtering, nullable)
  - name: String (Process/checklist name filter, nullable)
  - code: String (Job code filter, nullable)

Returns: Long (total count of user-specific jobs matching criteria)
Transaction: Not Required
Error Handling: Returns 0 if no jobs match user criteria
```

#### Method: findJobsByIdInOrderBy(Set<Long> ids)
```yaml
Signature: List<Job> findJobsByIdInOrderBy(Set<Long> ids)
Purpose: "Find jobs by IDs ordered by creation date for job reporting and bulk job operations"

Business Logic Derivation:
  1. Used in JobService for bulk job retrieval with consistent ordering for reporting operations
  2. Enables efficient bulk job loading with chronological ordering for job analysis
  3. Critical for job reporting workflows that need multiple jobs with temporal ordering
  4. Used in job dashboard and reporting operations that require ordered job lists
  5. Supports bulk job operations with consistent ordering for job management and reporting workflows

SQL Query: |
  SELECT j.* FROM jobs j 
  WHERE j.id IN (?) 
  ORDER BY j.created_at DESC

Parameters:
  - ids: Set<Long> (Job identifiers to retrieve)

Returns: List<Job> (jobs matching IDs ordered by creation date)
Transaction: Not Required
Error Handling: Returns empty list if no jobs found with specified IDs
```

#### Method: getChecklistIdByJobId(Long jobId)
```yaml
Signature: Long getChecklistIdByJobId(Long jobId)
Purpose: "Get checklist ID for job for job context operations and checklist validation"

Business Logic Derivation:
  1. Used in JobService for job context retrieval and checklist validation operations
  2. Provides checklist identifier for job-related operations that need checklist context
  3. Critical for job validation workflows and checklist-based operations
  4. Used in job reporting and validation operations that need checklist context
  5. Enables efficient checklist context retrieval without loading full job entity for performance optimization

SQL Query: |
  SELECT j.checklists_id
  FROM jobs j
  WHERE j.id = ?

Parameters:
  - jobId: Long (Job identifier to get checklist ID for)

Returns: Long (checklist identifier for the job)
Transaction: Not Required
Error Handling: Returns null if job not found
```

#### Method: getStateByJobId(Long jobId)
```yaml
Signature: State.Job getStateByJobId(Long jobId)
Purpose: "Get job state for job status validation and workflow control operations"

Business Logic Derivation:
  1. Used extensively in JobService, TaskExecutionService, and JobLogService for job state validation and workflow control
  2. Provides efficient job state retrieval for validation operations without loading full entity
  3. Critical for job workflow validation and job state-based business logic operations
  4. Used in job state transition validation and workflow progression control
  5. Enables efficient job state checking for workflow validation and business rule enforcement

SQL Query: |
  SELECT j.state FROM jobs j
  WHERE j.id = ?

Parameters:
  - jobId: Long (Job identifier to get state for)

Returns: State.Job (current job state)
Transaction: Not Required
Error Handling: Returns null if job not found
```

#### Method: findAllByIdIn(Set<Long> ids)
```yaml
Signature: List<Job> findAllByIdIn(Set<Long> ids)
Purpose: "Find jobs by multiple IDs for bulk job operations and job relationship management"

Business Logic Derivation:
  1. Used in JobService for bulk job retrieval and job relationship operations
  2. Enables efficient bulk job loading for job analysis and reporting workflows
  3. Critical for bulk job operations and job relationship management
  4. Used in job dashboard and reporting operations that need multiple job entities
  5. Supports efficient bulk job operations for job management and analysis workflows

SQL Query: |
  SELECT j.* FROM jobs j 
  WHERE j.id IN (?) 
  ORDER BY j.id DESC

Parameters:
  - ids: Set<Long> (Job identifiers to retrieve)

Returns: List<Job> (jobs matching the provided IDs)
Transaction: Not Required
Error Handling: Returns empty list if no jobs found with specified IDs
```

#### Method: getFacilityIdByJobId(Long jobId)
```yaml
Signature: Long getFacilityIdByJobId(Long jobId)
Purpose: "Get facility ID for job for facility context operations and access control validation"

Business Logic Derivation:
  1. Used in ParameterExecutionValidationService for facility context retrieval and access control validation
  2. Provides facility identifier for job-related operations that need facility context
  3. Critical for facility-based access control and facility context validation
  4. Used in parameter execution validation workflows that need facility context for security
  5. Enables efficient facility context retrieval for access control and validation operations

SQL Query: |
  SELECT j.facilities_id
  FROM jobs j
  WHERE j.id = ?

Parameters:
  - jobId: Long (Job identifier to get facility ID for)

Returns: Long (facility identifier for the job)
Transaction: Not Required
Error Handling: Returns null if job not found
```

### Key Repository Usage Patterns

#### Pattern: save() for Job Lifecycle Management
```yaml
Usage: jobRepository.save(job)
Purpose: "Create new jobs, update job states, and manage job lifecycle with comprehensive workflow control"

Business Logic Derivation:
  1. Used extensively throughout system for job lifecycle management and state transitions
  2. Handles job creation with proper checklist, facility, and organisation associations
  3. Updates job states during workflow progression and completion operations
  4. Critical for job lifecycle management and audit tracking with user attribution
  5. Supports complex job operations with relationship management and workflow control

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findAll(specification, pageable) for Dynamic Job Discovery
```yaml
Usage: jobRepository.findAll(specification, pageable)
Purpose: "Dynamic job discovery with complex filtering and pagination for job management operations"

Business Logic Derivation:
  1. Used extensively in JobService for advanced job search and listing operations
  2. Applies dynamic specifications for multi-criteria job filtering with business logic
  3. Supports pagination for large job datasets and job management operations
  4. Enables flexible job discovery and management operations with complex filtering
  5. Critical for job management APIs and job administration functionality

Transaction: Not Required
Error Handling: Returns empty page if no matches found
```

#### Pattern: findById() for Job Context Operations
```yaml
Usage: jobRepository.findById(jobId)
Purpose: "Retrieve job entity for job-specific operations and workflow management"

Business Logic Derivation:
  1. Used extensively throughout system for job context retrieval and job-specific operations
  2. Critical for job validation, job state management, and job-specific business logic
  3. Used in job workflow operations, job updates, and job completion workflows
  4. Essential for job context management and job-based workflow operations
  5. Enables job-centric operations with comprehensive job information and relationships

Transaction: Not Required
Error Handling: Throws ResourceNotFoundException if job not found
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAll(Specification), findByChecklistIdWhereStateNotIn
  - findJobProcessInfo, isJobExistsBySchedulerIdAndDateGreaterThanOrEqualToExpectedStartDate
  - getAllPendingForApprovalParameters, findAllByChecklistId, getMyJobs, countMyJob
  - findJobsByIdInOrderBy, getChecklistIdByJobId, getStateByJobId
  - findAllByIdIn, getFacilityIdByJobId, existsById, count
  - findOne(Specification), count(Specification)

Transactional Methods:
  - save, delete, deleteById, updateJobToUnassignedIfNoUserAssigned

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid checklists_id, facilities_id, organisations_id, use_cases_id, schedulers_id)
    * NOT NULL constraint violations (code, state, checklists_id, facilities_id, organisations_id, use_cases_id)
    * Invalid enum values for state field
    * Unique constraint violations on code field within organisation scope
  - EntityNotFoundException: Job not found by ID or criteria
  - OptimisticLockException: Concurrent job modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - ResourceNotFoundException: Job not found during operations

Validation Rules:
  - code: Required, max length 20 characters, should be unique within organisation scope
  - state: Required, must be valid Job enum value (ASSIGNED, IN_PROGRESS, COMPLETED, etc.)
  - checklist: Required, must reference existing checklist, immutable after creation
  - facility: Required, must reference existing facility, immutable after creation
  - organisation: Required, must reference existing organisation
  - useCase: Required, must reference existing use case, immutable after creation
  - startedAt: Optional, must be valid timestamp
  - endedAt: Optional, must be valid timestamp, should be after startedAt
  - scheduled: Defaults to false, indicates if job is scheduled
  - expectedStartDate: Optional, must be valid timestamp for scheduled jobs
  - expectedEndDate: Optional, must be valid timestamp for scheduled jobs
  - checklistAncestorId: Optional, must reference existing checklist for versioning

Business Constraints:
  - Cannot modify checklist, facility, or useCase associations after creation
  - Job state transitions must follow defined workflow (ASSIGNED -> IN_PROGRESS -> COMPLETED, etc.)
  - Cannot delete job with active task executions or parameter values
  - Job code should be unique within organisation scope
  - Scheduled jobs must have valid scheduler association and expected dates
  - Job assignment requires proper user assignment validation
  - Job state updates must maintain audit trail with proper user attribution
  - Job completion requires all mandatory tasks to be completed
  - Job archival should be used instead of deletion for data integrity
  - User assignments must respect facility-level security boundaries
  - Job scheduling must validate scheduler availability and resource constraints
  - Job state consistency must be maintained across task execution states
  - Bulk job operations should maintain transaction consistency
  - Job workflow progression must respect task dependencies and stage ordering
  - Job approval workflows must maintain proper approval state tracking
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Job repository without JPA/Hibernate dependencies.
