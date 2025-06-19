# ITaskExecutionAssigneeRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TaskExecutionUserMapping (extends UserAuditIdentifiableBase)
- **Primary Purpose**: Manages task execution user mapping entities for task assignment management with user/user group assignments, assignment validation, and task execution access control functionality
- **Key Relationships**: Links TaskExecution, User, and UserGroup entities for comprehensive task assignment management and execution access control
- **Performance Characteristics**: High query volume with assignment validation, user assignment operations, and assignment lifecycle management
- **Business Context**: Task execution assignment component that provides task assignee management, assignment validation, execution access control, and assignment lifecycle functionality for task execution and assignment workflows

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditIdentifiableBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| task_executions_id | taskExecutionsId / taskExecution.id | Long | false | null | Foreign key to task_executions |
| users_id | usersId / user.id | Long | false | null | Foreign key to users |
| user_groups_id | userGroup.id | Long | false | null | Foreign key to user_groups |
| action_performed | actionPerformed | boolean | false | false | Assignment action completion status |
| state | state | State.TaskExecutionAssignee | false | null | Assignment state enum |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | taskExecution | TaskExecution | EAGER | Associated task execution, not null |
| @ManyToOne | user | User | LAZY | Assigned user, not null |
| @ManyToOne | userGroup | UserGroup | LAZY | Assigned user group, not null |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(TaskExecutionUserMapping entity)`
- `saveAll(Iterable<TaskExecutionUserMapping> entities)`
- `deleteById(Long id)`
- `delete(TaskExecutionUserMapping entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (26 methods - ALL methods documented)

**Assignment Validation Methods (6 methods):**
- `isUserAssignedToAnyTask(Long jobId, Long userId)`
- `isAllTaskUnassigned(Long jobId)`
- `isUserAssignedToInProgressTasks(Long userId)`
- `existsByTaskExecutionIdAndUserId(Long taskExecutionId, Long currentUserId)`
- `existsByTaskExecutionIdAndUserGroupId(Long taskExecutionId, Long userGroupId)`
- `isUserGroupAssignedToInProgressTasks(Long userGroupId)`

**Assignee Retrieval Methods (14 methods):**
- `findByJobId(Long jobId, Integer totalExecutionIds)`
- `getJobAssignees(Set<Long> jobIds)`
- `getJobAssigneesCount(Long jobId)`
- `findByTaskExecutionIdAndUserIdIn(Long taskExecutionId, Set<Long> userIds)`
- `findByTaskExecutionIdInAndUserIdIn(Set<Long> taskExecutionIds, Set<Long> userIds)`
- `findByTaskExecutionIdIn(Set<Long> taskExecutionIds, Integer totalExecutionIds, boolean users, boolean userGroups)`
- `findByTaskExecutionAndUser(Long taskExecutionId, Long userId)`
- `findByTaskExecutionIdInAndUserGroupIdIn(Set<Long> taskExecutionIds, Set<Long> assignedUserGroupIds)`
- `getUserGroupAssignees(Long jobId, String query)`
- `getAllJobAssigneesUsersAndUserGroups(Long jobId, String query)`
- `getAllJobAssigneesUsersAndUserGroupsByRoles(Long jobId, String query, List<String> roles)`
- `findAllByTaskExecutionIdsIn(Set<Long> taskExecutionIds)`
- `findAllByTaskExecutionId(Long taskExecutionId)`

**Assignment Management Methods (6 methods):**
- `unassignUsersFromNonStartedAndInProgessTasks(Long userId)` (@Modifying)
- `unassignUsersByTaskExecutions(Set<Long> taskExecutionIds, Set<Long> userIds)` (@Modifying)
- `updateAssigneeState(String state, Long userId, Set<Long> taskExecutionIds, Long modifiedBy, Long modifiedAt)` (@Modifying)
- `unassignUserGroupIdsByTaskExecutions(Set<Long> taskExecutionIds, Set<Long> userGroupsId)` (@Modifying)
- `removeUserGroupAssignees(Long userGroupId)` (@Modifying)
- `deleteAllByTaskExecutionId(Long taskExecutionId)` (@Modifying)
- `updateUserAction(Set<Long> taskExecutionUserMappingList)` (@Modifying)

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<TaskExecutionUserMapping> findById(Long id)
List<TaskExecutionUserMapping> findAll()
TaskExecutionUserMapping save(TaskExecutionUserMapping entity)
List<TaskExecutionUserMapping> saveAll(Iterable<TaskExecutionUserMapping> entities)
void deleteById(Long id)
void delete(TaskExecutionUserMapping entity)
boolean existsById(Long id)
long count()
```

### Assignment Validation Methods

#### Method: isUserAssignedToAnyTask(Long jobId, Long userId)
```yaml
Signature: boolean isUserAssignedToAnyTask(Long jobId, Long userId)
Purpose: "Check if user is assigned to any task in job for job assignment validation and access control"

Business Logic Derivation:
  1. Used extensively across multiple services for user job assignment validation during job execution and access control operations
  2. Provides job-level user assignment validation for execution workflows enabling comprehensive access control and assignment functionality
  3. Critical for job access operations requiring user assignment validation for job execution and assignment management
  4. Used in job execution workflows for validating user job assignments for execution operations and access processing
  5. Enables job access control with user assignment validation for comprehensive job execution and assignment control

SQL Query: |
  SELECT EXISTS(
    SELECT 1 FROM task_execution_user_mapping teum
    INNER JOIN task_executions te ON teum.task_executions_id = te.id
    WHERE te.jobs_id = ? AND (teum.users_id = ? OR 
      teum.user_groups_id IN (
        SELECT ugm.groups_id FROM user_group_members ugm 
        WHERE ugm.users_id = ?
      ))
  )

Parameters:
  - jobId: Long (Job identifier for assignment validation context)
  - userId: Long (User identifier for assignment validation)

Returns: boolean (true if user is assigned to any task in job, false otherwise)
Transaction: Not Required
Error Handling: Returns false if user is not assigned to any task in job
```

#### Method: isAllTaskUnassigned(Long jobId)
```yaml
Signature: boolean isAllTaskUnassigned(Long jobId)
Purpose: "Check if all tasks in job are unassigned for job state management and assignment validation"

Business Logic Derivation:
  1. Used in JobAssignmentService for job state validation during job assignment management and state control operations
  2. Provides job-level assignment status validation for job workflows enabling comprehensive state management and assignment functionality
  3. Critical for job state operations requiring assignment status validation for job management and state control
  4. Used in job management workflows for validating job assignment status for state operations and management processing
  5. Enables job state management with assignment status validation for comprehensive job processing and state control

SQL Query: |
  SELECT NOT EXISTS(
    SELECT 1 FROM task_execution_user_mapping teum
    INNER JOIN task_executions te ON teum.task_executions_id = te.id
    WHERE te.jobs_id = ?
  )

Parameters:
  - jobId: Long (Job identifier for assignment status validation)

Returns: boolean (true if all tasks are unassigned, false otherwise)
Transaction: Not Required
Error Handling: Returns false if any task has assignments
```

#### Method: isUserAssignedToInProgressTasks(Long userId)
```yaml
Signature: Boolean isUserAssignedToInProgressTasks(Long userId)
Purpose: "Check if user is assigned to in-progress tasks for user lifecycle management and validation"

Business Logic Derivation:
  1. Used in UserService for user archive validation during user lifecycle management and validation operations
  2. Provides user in-progress task validation for user workflows enabling comprehensive lifecycle management and user functionality
  3. Critical for user lifecycle operations requiring in-progress task validation for user management and lifecycle control
  4. Used in user management workflows for validating user in-progress assignments for lifecycle operations and user processing
  5. Enables user lifecycle management with in-progress task validation for comprehensive user processing and lifecycle control

SQL Query: |
  SELECT EXISTS(
    SELECT 1 FROM task_execution_user_mapping teum
    INNER JOIN task_executions te ON teum.task_executions_id = te.id
    WHERE (teum.users_id = ? OR 
      teum.user_groups_id IN (
        SELECT ugm.groups_id FROM user_group_members ugm 
        WHERE ugm.users_id = ?
      ))
    AND te.state = 'IN_PROGRESS'
  )

Parameters:
  - userId: Long (User identifier for in-progress task validation)

Returns: Boolean (true if user is assigned to in-progress tasks, false otherwise)
Transaction: Not Required
Error Handling: Returns false if user is not assigned to any in-progress tasks
```

#### Method: existsByTaskExecutionIdAndUserId(Long taskExecutionId, Long currentUserId)
```yaml
Signature: boolean existsByTaskExecutionIdAndUserId(Long taskExecutionId, Long currentUserId)
Purpose: "Check if user is assigned to specific task execution for task execution access control and validation"

Business Logic Derivation:
  1. Used extensively across multiple services for task execution access validation during parameter execution and task control operations
  2. Provides task-specific user assignment validation for execution workflows enabling comprehensive access control and execution functionality
  3. Critical for task execution operations requiring user assignment validation for execution access and task management
  4. Used in task execution workflows for validating user task assignments for execution operations and access processing
  5. Enables task execution access control with user assignment validation for comprehensive execution processing and access control

SQL Query: |
  SELECT EXISTS(
    SELECT 1 FROM task_execution_user_mapping teum
    WHERE teum.task_executions_id = ? AND 
      (teum.users_id = ? OR 
       teum.user_groups_id IN (
         SELECT ugm.groups_id FROM user_group_members ugm 
         WHERE ugm.users_id = ?
       ))
  )

Parameters:
  - taskExecutionId: Long (Task execution identifier for assignment validation)
  - currentUserId: Long (User identifier for access validation)

Returns: boolean (true if user is assigned to task execution, false otherwise)
Transaction: Not Required
Error Handling: Returns false if user is not assigned to task execution
```

#### Method: existsByTaskExecutionIdAndUserGroupId(Long taskExecutionId, Long userGroupId)
```yaml
Signature: boolean existsByTaskExecutionIdAndUserGroupId(Long taskExecutionId, Long userGroupId)
Purpose: "Check if user group is assigned to specific task execution for group assignment validation and access control"

Business Logic Derivation:
  1. Used in JobAssignmentService for user group assignment validation during assignment management and access control operations
  2. Provides task-specific user group assignment validation for assignment workflows enabling comprehensive access control and assignment functionality
  3. Critical for assignment management operations requiring user group assignment validation for assignment access and task management
  4. Used in assignment management workflows for validating user group task assignments for assignment operations and access processing
  5. Enables assignment access control with user group assignment validation for comprehensive assignment processing and access control

SQL Query: |
  SELECT EXISTS(
    SELECT 1 FROM task_execution_user_mapping teum
    WHERE teum.task_executions_id = ? AND teum.user_groups_id = ?
  )

Parameters:
  - taskExecutionId: Long (Task execution identifier for assignment validation)
  - userGroupId: Long (User group identifier for access validation)

Returns: boolean (true if user group is assigned to task execution, false otherwise)
Transaction: Not Required
Error Handling: Returns false if user group is not assigned to task execution
```

#### Method: isUserGroupAssignedToInProgressTasks(Long userGroupId)
```yaml
Signature: Boolean isUserGroupAssignedToInProgressTasks(Long userGroupId)
Purpose: "Check if user group is assigned to in-progress tasks for user group lifecycle management and validation"

Business Logic Derivation:
  1. Used in UserGroupService for user group archive validation during user group lifecycle management and validation operations
  2. Provides user group in-progress task validation for user group workflows enabling comprehensive lifecycle management and group functionality
  3. Critical for user group lifecycle operations requiring in-progress task validation for group management and lifecycle control
  4. Used in user group management workflows for validating group in-progress assignments for lifecycle operations and group processing
  5. Enables user group lifecycle management with in-progress task validation for comprehensive group processing and lifecycle control

SQL Query: |
  SELECT EXISTS(
    SELECT 1 FROM task_execution_user_mapping teum
    INNER JOIN task_executions te ON teum.task_executions_id = te.id
    WHERE teum.user_groups_id = ? AND te.state = 'IN_PROGRESS'
  )

Parameters:
  - userGroupId: Long (User group identifier for in-progress task validation)

Returns: Boolean (true if user group is assigned to in-progress tasks, false otherwise)
Transaction: Not Required
Error Handling: Returns false if user group is not assigned to any in-progress tasks
```

### Assignee Retrieval Methods

#### Method: findByJobId(Long jobId, Integer totalExecutionIds)
```yaml
Signature: List<TaskExecutionAssigneeDetailsView> findByJobId(Long jobId, Integer totalExecutionIds)
Purpose: "Find task execution assignee details by job ID for job assignee reporting and management"

Business Logic Derivation:
  1. Used in JobService for job assignee retrieval during job reporting and assignee management operations
  2. Provides job-level assignee reporting for job workflows enabling comprehensive assignee management and job functionality
  3. Critical for job reporting operations requiring assignee details for job management and reporting control
  4. Used in job management workflows for accessing job assignees for reporting operations and job processing
  5. Enables job assignee management with comprehensive assignee reporting for job processing and assignee control

SQL Query: |
  SELECT DISTINCT 
    te.tasks_id as taskId,
    t.name as taskName,
    s.name as stageName,
    teum.users_id as userId,
    u.employee_id as employeeId,
    CONCAT(u.first_name, ' ', u.last_name) as userName,
    teum.user_groups_id as userGroupId,
    ug.name as userGroupName,
    teum.action_performed as actionPerformed,
    teum.state as state
  FROM task_execution_user_mapping teum
  INNER JOIN task_executions te ON teum.task_executions_id = te.id
  INNER JOIN tasks t ON te.tasks_id = t.id
  INNER JOIN stages s ON t.stages_id = s.id
  LEFT JOIN users u ON teum.users_id = u.id
  LEFT JOIN user_groups ug ON teum.user_groups_id = ug.id
  WHERE te.jobs_id = ?
  ORDER BY s.order_tree, t.order_tree

Parameters:
  - jobId: Long (Job identifier for assignee details retrieval)
  - totalExecutionIds: Integer (Total execution count for context)

Returns: List<TaskExecutionAssigneeDetailsView> (assignee details for job)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for job
```

#### Method: getJobAssignees(Set<Long> jobIds)
```yaml
Signature: List<JobAssigneeView> getJobAssignees(Set<Long> jobIds)
Purpose: "Get job assignees by multiple job IDs for bulk job assignee reporting and management"

Business Logic Derivation:
  1. Used extensively in multiple services for bulk job assignee retrieval during job reporting and assignee management operations
  2. Provides efficient bulk job assignee access for job workflows enabling comprehensive bulk assignee management and job functionality
  3. Critical for bulk job operations requiring assignee identification for bulk job processing and assignee management
  4. Used in bulk job workflows for accessing job assignees for bulk operations and job processing
  5. Enables bulk job assignee management with efficient assignee retrieval for comprehensive bulk job processing and assignee control

SQL Query: |
  SELECT DISTINCT
    te.jobs_id as jobId,
    teum.users_id as id,
    CONCAT(u.first_name, ' ', u.last_name) as name,
    'USER' as type
  FROM task_execution_user_mapping teum
  INNER JOIN task_executions te ON teum.task_executions_id = te.id
  INNER JOIN users u ON teum.users_id = u.id
  WHERE te.jobs_id IN (?, ?, ?, ...)
  UNION
  SELECT DISTINCT
    te.jobs_id as jobId,
    teum.user_groups_id as id,
    ug.name as name,
    'USER_GROUP' as type
  FROM task_execution_user_mapping teum
  INNER JOIN task_executions te ON teum.task_executions_id = te.id
  INNER JOIN user_groups ug ON teum.user_groups_id = ug.id
  WHERE te.jobs_id IN (?, ?, ?, ...)

Parameters:
  - jobIds: Set<Long> (Set of job identifiers for bulk assignee retrieval)

Returns: List<JobAssigneeView> (job assignees for specified jobs)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for jobs
```

#### Method: getJobAssigneesCount(Long jobId)
```yaml
Signature: Integer getJobAssigneesCount(Long jobId)
Purpose: "Get count of job assignees for job reporting and metrics"

Business Logic Derivation:
  1. Used in JobService for job assignee count retrieval during job reporting and metrics operations
  2. Provides job assignee count for job workflows enabling comprehensive metrics reporting and job functionality
  3. Critical for job metrics operations requiring assignee count for job reporting and metrics management
  4. Used in job reporting workflows for accessing assignee counts for metrics operations and job processing
  5. Enables job metrics management with assignee count reporting for comprehensive job processing and metrics control

SQL Query: |
  SELECT COUNT(DISTINCT 
    CASE WHEN teum.users_id IS NOT NULL THEN CONCAT('U_', teum.users_id)
         WHEN teum.user_groups_id IS NOT NULL THEN CONCAT('UG_', teum.user_groups_id)
    END
  )
  FROM task_execution_user_mapping teum
  INNER JOIN task_executions te ON teum.task_executions_id = te.id
  WHERE te.jobs_id = ?

Parameters:
  - jobId: Long (Job identifier for assignee count)

Returns: Integer (count of unique assignees for job)
Transaction: Not Required
Error Handling: Returns 0 if no assignees found for job
```

#### Method: findByTaskExecutionIdAndUserIdIn(Long taskExecutionId, Set<Long> userIds)
```yaml
Signature: List<TaskExecutionAssigneeBasicView> findByTaskExecutionIdAndUserIdIn(Long taskExecutionId, Set<Long> userIds)
Purpose: "Find task execution assignees by task execution and multiple user IDs for assignment management and validation"

Business Logic Derivation:
  1. Used for task execution assignment retrieval during assignment management and validation operations
  2. Provides task-specific user assignment access for assignment workflows enabling comprehensive assignment management and task functionality
  3. Critical for assignment management operations requiring user assignment identification for task assignment and management control
  4. Used in assignment management workflows for accessing task user assignments for assignment operations and task processing
  5. Enables assignment management with user assignment identification for comprehensive task processing and assignment control

SQL Query: |
  SELECT 
    teum.id as id,
    teum.task_executions_id as taskExecutionId,
    teum.users_id as userId,
    teum.user_groups_id as userGroupId,
    teum.action_performed as actionPerformed,
    teum.state as state
  FROM task_execution_user_mapping teum
  WHERE teum.task_executions_id = ? AND teum.users_id IN (?, ?, ?, ...)

Parameters:
  - taskExecutionId: Long (Task execution identifier for assignment context)
  - userIds: Set<Long> (Set of user identifiers for assignment retrieval)

Returns: List<TaskExecutionAssigneeBasicView> (task execution assignees for users)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for task execution and users
```

#### Method: findByTaskExecutionIdInAndUserIdIn(Set<Long> taskExecutionIds, Set<Long> userIds)
```yaml
Signature: List<TaskExecutionAssigneeBasicView> findByTaskExecutionIdInAndUserIdIn(Set<Long> taskExecutionIds, Set<Long> userIds)
Purpose: "Find task execution assignees by multiple task executions and user IDs for bulk assignment management"

Business Logic Derivation:
  1. Used for bulk task execution assignment retrieval during bulk assignment management and validation operations
  2. Provides efficient bulk assignment access for assignment workflows enabling comprehensive bulk assignment management and task functionality
  3. Critical for bulk assignment operations requiring assignment identification for bulk task assignment and management control
  4. Used in bulk assignment workflows for accessing multiple task user assignments for bulk operations and task processing
  5. Enables bulk assignment management with efficient assignment retrieval for comprehensive bulk task processing and assignment control

SQL Query: |
  SELECT 
    teum.id as id,
    teum.task_executions_id as taskExecutionId,
    teum.users_id as userId,
    teum.user_groups_id as userGroupId,
    teum.action_performed as actionPerformed,
    teum.state as state
  FROM task_execution_user_mapping teum
  WHERE teum.task_executions_id IN (?, ?, ?, ...) 
    AND teum.users_id IN (?, ?, ?, ...)

Parameters:
  - taskExecutionIds: Set<Long> (Set of task execution identifiers for bulk assignment context)
  - userIds: Set<Long> (Set of user identifiers for bulk assignment retrieval)

Returns: List<TaskExecutionAssigneeBasicView> (task execution assignees for task executions and users)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for task executions and users
```

#### Method: findByTaskExecutionIdIn(Set<Long> taskExecutionIds, Integer totalExecutionIds, boolean users, boolean userGroups)
```yaml
Signature: List<TaskExecutionAssigneeView> findByTaskExecutionIdIn(Set<Long> taskExecutionIds, Integer totalExecutionIds, boolean users, boolean userGroups)
Purpose: "Find task execution assignees by multiple task executions with type filtering for comprehensive assignment reporting"

Business Logic Derivation:
  1. Used in TaskExecutionService for filtered task execution assignee retrieval during assignment reporting and management operations
  2. Provides filtered assignee access for assignment workflows enabling comprehensive assignment reporting and task functionality
  3. Critical for assignment reporting operations requiring filtered assignee identification for assignment reporting and management control
  4. Used in assignment reporting workflows for accessing filtered task assignees for reporting operations and task processing
  5. Enables assignment reporting with filtered assignee identification for comprehensive task processing and assignment control

SQL Query: |
  SELECT 
    teum.task_executions_id as taskExecutionId,
    teum.users_id as userId,
    u.employee_id as employeeId,
    CONCAT(u.first_name, ' ', u.last_name) as userName,
    teum.user_groups_id as userGroupId,
    ug.name as userGroupName,
    teum.action_performed as actionPerformed,
    teum.state as state
  FROM task_execution_user_mapping teum
  LEFT JOIN users u ON teum.users_id = u.id AND ? = true
  LEFT JOIN user_groups ug ON teum.user_groups_id = ug.id AND ? = true
  WHERE teum.task_executions_id IN (?, ?, ?, ...)
    AND ((? = true AND teum.users_id IS NOT NULL) OR 
         (? = true AND teum.user_groups_id IS NOT NULL))

Parameters:
  - taskExecutionIds: Set<Long> (Set of task execution identifiers for assignee retrieval)
  - totalExecutionIds: Integer (Total execution count for context)
  - users: boolean (Include user assignees flag)
  - userGroups: boolean (Include user group assignees flag)

Returns: List<TaskExecutionAssigneeView> (filtered task execution assignees)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for criteria
```

#### Method: findByTaskExecutionAndUser(Long taskExecutionId, Long userId)
```yaml
Signature: Optional<List<TaskExecutionUserMapping>> findByTaskExecutionAndUser(Long taskExecutionId, Long userId)
Purpose: "Find task execution user mappings by task execution and user for assignment validation and management"

Business Logic Derivation:
  1. Used extensively across multiple services for user task execution assignment retrieval during execution validation and assignment operations
  2. Provides user-specific task assignment access for execution workflows enabling comprehensive assignment validation and execution functionality
  3. Critical for execution validation operations requiring user assignment identification for execution access and assignment management
  4. Used in execution validation workflows for accessing user task assignments for validation operations and execution processing
  5. Enables execution validation with user assignment identification for comprehensive execution processing and assignment control

SQL Query: |
  SELECT teum.*
  FROM task_execution_user_mapping teum
  WHERE teum.task_executions_id = ? AND 
    (teum.users_id = ? OR 
     teum.user_groups_id IN (
       SELECT ugm.groups_id FROM user_group_members ugm 
       WHERE ugm.users_id = ?
     ))

Parameters:
  - taskExecutionId: Long (Task execution identifier for assignment context)
  - userId: Long (User identifier for assignment retrieval)

Returns: Optional<List<TaskExecutionUserMapping>> (user mappings for task execution and user)
Transaction: Not Required
Error Handling: Returns empty Optional if no mappings found for task execution and user
```

#### Method: findByTaskExecutionIdInAndUserGroupIdIn(Set<Long> taskExecutionIds, Set<Long> assignedUserGroupIds)
```yaml
Signature: List<TaskExecutionAssigneeBasicView> findByTaskExecutionIdInAndUserGroupIdIn(Set<Long> taskExecutionIds, Set<Long> assignedUserGroupIds)
Purpose: "Find task execution assignees by multiple task executions and user group IDs for group assignment management"

Business Logic Derivation:
  1. Used in JobAssignmentService for user group assignment retrieval during assignment management and group assignment operations
  2. Provides group-specific assignment access for assignment workflows enabling comprehensive group assignment management and task functionality
  3. Critical for group assignment operations requiring group assignment identification for group assignment and management control
  4. Used in group assignment workflows for accessing task group assignments for assignment operations and task processing
  5. Enables group assignment management with group assignment identification for comprehensive task processing and assignment control

SQL Query: |
  SELECT 
    teum.id as id,
    teum.task_executions_id as taskExecutionId,
    teum.users_id as userId,
    teum.user_groups_id as userGroupId,
    teum.action_performed as actionPerformed,
    teum.state as state
  FROM task_execution_user_mapping teum
  WHERE teum.task_executions_id IN (?, ?, ?, ...) 
    AND teum.user_groups_id IN (?, ?, ?, ...)

Parameters:
  - taskExecutionIds: Set<Long> (Set of task execution identifiers for group assignment context)
  - assignedUserGroupIds: Set<Long> (Set of user group identifiers for group assignment retrieval)

Returns: List<TaskExecutionAssigneeBasicView> (task execution assignees for task executions and user groups)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for task executions and user groups
```

#### Method: getUserGroupAssignees(Long jobId, String query)
```yaml
Signature: List<UserGroupView> getUserGroupAssignees(Long jobId, String query)
Purpose: "Get user group assignees for job with query filtering for user group assignment reporting"

Business Logic Derivation:
  1. Used in ParameterVerificationService for user group assignee retrieval during verification reporting and group management operations
  2. Provides job-level user group assignee access for verification workflows enabling comprehensive group reporting and verification functionality
  3. Critical for verification reporting operations requiring user group assignee identification for verification management and reporting control
  4. Used in verification reporting workflows for accessing job user group assignees for reporting operations and verification processing
  5. Enables verification reporting with user group assignee identification for comprehensive verification processing and reporting control

SQL Query: |
  SELECT DISTINCT 
    ug.id as id, 
    ug.name as name
  FROM task_execution_user_mapping teum
  INNER JOIN task_executions te ON teum.task_executions_id = te.id
  INNER JOIN user_groups ug ON teum.user_groups_id = ug.id
  WHERE te.jobs_id = ? 
    AND (CAST(? AS VARCHAR) IS NULL OR ug.name ILIKE CONCAT('%', CAST(? AS VARCHAR), '%'))

Parameters:
  - jobId: Long (Job identifier for user group assignee retrieval)
  - query: String (Query filter for user group name filtering, nullable)

Returns: List<UserGroupView> (user group assignees for job matching query)
Transaction: Not Required
Error Handling: Returns empty list if no user group assignees found for job and query
```

#### Method: getAllJobAssigneesUsersAndUserGroups(Long jobId, String query)
```yaml
Signature: List<JobAssigneeView> getAllJobAssigneesUsersAndUserGroups(Long jobId, String query)
Purpose: "Get all job assignees including users and user groups with query filtering for comprehensive assignee reporting"

Business Logic Derivation:
  1. Used in JobService for comprehensive job assignee retrieval during job reporting and assignee management operations
  2. Provides complete job assignee access for job workflows enabling comprehensive assignee reporting and job functionality
  3. Critical for job reporting operations requiring complete assignee identification for job reporting and assignee management
  4. Used in job reporting workflows for accessing all job assignees for comprehensive reporting operations and job processing
  5. Enables comprehensive job assignee reporting with complete assignee identification for job processing and assignee control

SQL Query: |
  SELECT DISTINCT
    te.jobs_id as jobId,
    teum.users_id as id,
    CONCAT(u.first_name, ' ', u.last_name) as name,
    'USER' as type
  FROM task_execution_user_mapping teum
  INNER JOIN task_executions te ON teum.task_executions_id = te.id
  INNER JOIN users u ON teum.users_id = u.id
  WHERE te.jobs_id = ? 
    AND (CAST(? AS VARCHAR) IS NULL OR CONCAT(u.first_name, ' ', u.last_name) ILIKE CONCAT('%', CAST(? AS VARCHAR), '%'))
  UNION
  SELECT DISTINCT
    te.jobs_id as jobId,
    teum.user_groups_id as id,
    ug.name as name,
    'USER_GROUP' as type
  FROM task_execution_user_mapping teum
  INNER JOIN task_executions te ON teum.task_executions_id = te.id
  INNER JOIN user_groups ug ON teum.user_groups_id = ug.id
  WHERE te.jobs_id = ? 
    AND (CAST(? AS VARCHAR) IS NULL OR ug.name ILIKE CONCAT('%', CAST(? AS VARCHAR), '%'))

Parameters:
  - jobId: Long (Job identifier for assignee retrieval)
  - query: String (Query filter for assignee name filtering, nullable)

Returns: List<JobAssigneeView> (all job assignees matching query)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for job and query
```

#### Method: getAllJobAssigneesUsersAndUserGroupsByRoles(Long jobId, String query, List<String> roles)
```yaml
Signature: List<JobAssigneeView> getAllJobAssigneesUsersAndUserGroupsByRoles(Long jobId, String query, List<String> roles)
Purpose: "Get job assignees filtered by roles with query filtering for role-based assignee reporting"

Business Logic Derivation:
  1. Used in JobService for role-filtered job assignee retrieval during job reporting and role-based assignee management operations
  2. Provides role-filtered job assignee access for job workflows enabling comprehensive role-based assignee reporting and job functionality
  3. Critical for role-based job reporting operations requiring role-filtered assignee identification for job reporting and assignee management
  4. Used in role-based job reporting workflows for accessing role-filtered job assignees for reporting operations and job processing
  5. Enables role-based job assignee reporting with role-filtered assignee identification for job processing and assignee control

SQL Query: |
  SELECT DISTINCT
    te.jobs_id as jobId,
    teum.users_id as id,
    CONCAT(u.first_name, ' ', u.last_name) as name,
    'USER' as type
  FROM task_execution_user_mapping teum
  INNER JOIN task_executions te ON teum.task_executions_id = te.id
  INNER JOIN users u ON teum.users_id = u.id
  INNER JOIN user_roles_mapping urm ON u.id = urm.users_id
  INNER JOIN roles r ON urm.roles_id = r.id
  WHERE te.jobs_id = ? 
    AND (CAST(? AS VARCHAR) IS NULL OR CONCAT(u.first_name, ' ', u.last_name) ILIKE CONCAT('%', CAST(? AS VARCHAR), '%'))
    AND r.name IN (?, ?, ?, ...)

Parameters:
  - jobId: Long (Job identifier for role-filtered assignee retrieval)
  - query: String (Query filter for assignee name filtering, nullable)
  - roles: List<String> (List of role names for role filtering)

Returns: List<JobAssigneeView> (job assignees matching query and roles)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for job, query, and roles
```

#### Method: findAllByTaskExecutionIdsIn(Set<Long> taskExecutionIds)
```yaml
Signature: List<TaskExecutionAssigneeBasicView> findAllByTaskExecutionIdsIn(Set<Long> taskExecutionIds)
Purpose: "Find all task execution assignees by multiple task execution IDs for bulk assignee retrieval and management"

Business Logic Derivation:
  1. Used in TaskMapper for bulk task execution assignee retrieval during task mapping and assignee management operations
  2. Provides efficient bulk assignee access for task workflows enabling comprehensive bulk assignee management and task functionality
  3. Critical for bulk task operations requiring assignee identification for bulk task processing and assignee management
  4. Used in bulk task workflows for accessing task assignees for bulk operations and task processing
  5. Enables bulk task assignee management with efficient assignee retrieval for comprehensive bulk task processing and assignee control

SQL Query: |
  SELECT 
    teum.id as id,
    teum.task_executions_id as taskExecutionId,
    teum.users_id as userId,
    teum.user_groups_id as userGroupId,
    teum.action_performed as actionPerformed,
    teum.state as state
  FROM task_execution_user_mapping teum
  WHERE teum.task_executions_id IN (?, ?, ?, ...)

Parameters:
  - taskExecutionIds: Set<Long> (Set of task execution identifiers for bulk assignee retrieval)

Returns: List<TaskExecutionAssigneeBasicView> (task execution assignees for specified task executions)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for task executions
```

#### Method: findAllByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: List<TaskExecutionAssigneeBasicView> findAllByTaskExecutionId(Long taskExecutionId)
Purpose: "Find all task execution assignees by task execution ID for assignee management and notification"

Business Logic Derivation:
  1. Used in NotificationService for task execution assignee retrieval during notification and assignee management operations
  2. Provides task-specific assignee access for notification workflows enabling comprehensive assignee notification and task functionality
  3. Critical for notification operations requiring assignee identification for notification processing and assignee management
  4. Used in notification workflows for accessing task assignees for notification operations and task processing
  5. Enables notification management with assignee identification for comprehensive task processing and notification control

SQL Query: |
  SELECT 
    teum.id as id,
    teum.task_executions_id as taskExecutionId,
    teum.users_id as userId,
    teum.user_groups_id as userGroupId,
    teum.action_performed as actionPerformed,
    teum.state as state
  FROM task_execution_user_mapping teum
  WHERE teum.task_executions_id = ?

Parameters:
  - taskExecutionId: Long (Task execution identifier for assignee retrieval)

Returns: List<TaskExecutionAssigneeBasicView> (task execution assignees for task execution)
Transaction: Not Required
Error Handling: Returns empty list if no assignees found for task execution
```

### Assignment Management Methods

#### Method: unassignUsersFromNonStartedAndInProgessTasks(Long userId)
```yaml
Signature: Set<Long> unassignUsersFromNonStartedAndInProgessTasks(Long userId)
Purpose: "Unassign user from non-started and in-progress tasks for user lifecycle management and cleanup"

Business Logic Derivation:
  1. Used in UserService for user unassignment during user lifecycle management and cleanup operations
  2. Provides user unassignment functionality for user workflows enabling comprehensive user lifecycle management and cleanup functionality
  3. Critical for user lifecycle operations requiring user unassignment for user management and lifecycle control
  4. Used in user management workflows for unassigning users from tasks for lifecycle operations and user processing
  5. Enables user lifecycle management with user unassignment for comprehensive user processing and lifecycle control

SQL Query: |
  UPDATE task_execution_user_mapping 
  SET state = 'UNASSIGNED' 
  WHERE users_id = ? 
    AND task_executions_id IN (
      SELECT te.id FROM task_executions te 
      WHERE te.state IN ('NOT_STARTED', 'IN_PROGRESS')
    )
  RETURNING DISTINCT (
    SELECT te.jobs_id FROM task_executions te 
    WHERE te.id = task_executions_id
  )

Parameters:
  - userId: Long (User identifier for unassignment)

Returns: Set<Long> (set of job IDs affected by unassignment)
Transaction: Required (@Modifying and @Transactional annotations)
Error Handling: Returns empty set if no assignments found for user
```

#### Method: unassignUsersByTaskExecutions(Set<Long> taskExecutionIds, Set<Long> userIds)
```yaml
Signature: void unassignUsersByTaskExecutions(Set<Long> taskExecutionIds, Set<Long> userIds)
Purpose: "Unassign users from specific task executions for assignment management and cleanup"

Business Logic Derivation:
  1. Used in JobAssignmentService for user unassignment during assignment management and cleanup operations
  2. Provides targeted user unassignment functionality for assignment workflows enabling comprehensive assignment management and cleanup functionality
  3. Critical for assignment management operations requiring user unassignment for assignment control and cleanup management
  4. Used in assignment management workflows for unassigning users from task executions for assignment operations and cleanup processing
  5. Enables assignment management with targeted user unassignment for comprehensive assignment processing and cleanup control

SQL Query: |
  DELETE FROM task_execution_user_mapping 
  WHERE task_executions_id IN (?, ?, ?, ...) 
    AND users_id IN (?, ?, ?, ...)

Parameters:
  - taskExecutionIds: Set<Long> (Set of task execution identifiers for unassignment context)
  - userIds: Set<Long> (Set of user identifiers for unassignment)

Returns: void
Transaction: Required (@Modifying and @Transactional annotations)
Error Handling: No exception if no assignments found for task executions and users
```

#### Method: updateAssigneeState(String state, Long userId, Set<Long> taskExecutionIds, Long modifiedBy, Long modifiedAt)
```yaml
Signature: void updateAssigneeState(String state, Long userId, Set<Long> taskExecutionIds, Long modifiedBy, Long modifiedAt)
Purpose: "Update assignee state for specific task executions and user for state management and tracking"

Business Logic Derivation:
  1. Used in TaskExecutionService for assignee state updates during state management and tracking operations
  2. Provides assignee state management functionality for assignment workflows enabling comprehensive state tracking and assignment functionality
  3. Critical for state management operations requiring assignee state updates for assignment tracking and state control
  4. Used in state management workflows for updating assignee states for tracking operations and assignment processing
  5. Enables state management with assignee state updates for comprehensive assignment processing and state control

SQL Query: |
  UPDATE task_execution_user_mapping 
  SET state = ?, modified_by = ?, modified_at = ?
  WHERE users_id = ? 
    AND task_executions_id IN (?, ?, ?, ...)

Parameters:
  - state: String (New assignee state for state update)
  - userId: Long (User identifier for state update context)
  - taskExecutionIds: Set<Long> (Set of task execution identifiers for state update)
  - modifiedBy: Long (User identifier for modification tracking)
  - modifiedAt: Long (Timestamp for modification tracking)

Returns: void
Transaction: Required (@Modifying and @Transactional annotations)
Error Handling: No exception if no assignments found for user and task executions
```

#### Method: unassignUserGroupIdsByTaskExecutions(Set<Long> taskExecutionIds, Set<Long> userGroupsId)
```yaml
Signature: void unassignUserGroupIdsByTaskExecutions(Set<Long> taskExecutionIds, Set<Long> userGroupsId)
Purpose: "Unassign user groups from specific task executions for group assignment management and cleanup"

Business Logic Derivation:
  1. Used in JobAssignmentService for user group unassignment during assignment management and cleanup operations
  2. Provides targeted user group unassignment functionality for assignment workflows enabling comprehensive group assignment management and cleanup functionality
  3. Critical for group assignment management operations requiring user group unassignment for assignment control and cleanup management
  4. Used in group assignment management workflows for unassigning user groups from task executions for assignment operations and cleanup processing
  5. Enables group assignment management with targeted user group unassignment for comprehensive assignment processing and cleanup control

SQL Query: |
  DELETE FROM task_execution_user_mapping 
  WHERE task_executions_id IN (?, ?, ?, ...) 
    AND user_groups_id IN (?, ?, ?, ...)

Parameters:
  - taskExecutionIds: Set<Long> (Set of task execution identifiers for unassignment context)
  - userGroupsId: Set<Long> (Set of user group identifiers for unassignment)

Returns: void
Transaction: Required (@Modifying and @Transactional annotations)
Error Handling: No exception if no assignments found for task executions and user groups
```

#### Method: removeUserGroupAssignees(Long userGroupId)
```yaml
Signature: void removeUserGroupAssignees(Long userGroupId)
Purpose: "Remove all user group assignees for user group lifecycle management and cleanup"

Business Logic Derivation:
  1. Used in UserGroupService for user group assignment cleanup during user group lifecycle management and cleanup operations
  2. Provides complete user group assignment cleanup functionality for user group workflows enabling comprehensive group lifecycle management and cleanup functionality
  3. Critical for user group lifecycle operations requiring complete assignment cleanup for group management and lifecycle control
  4. Used in user group management workflows for removing all group assignments for lifecycle operations and group processing
  5. Enables user group lifecycle management with complete assignment cleanup for comprehensive group processing and lifecycle control

SQL Query: |
  DELETE FROM task_execution_user_mapping 
  WHERE user_groups_id = ?

Parameters:
  - userGroupId: Long (User group identifier for complete assignment cleanup)

Returns: void
Transaction: Required (@Modifying and @Transactional annotations)
Error Handling: No exception if no assignments found for user group
```

#### Method: deleteAllByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: void deleteAllByTaskExecutionId(Long taskExecutionId)
Purpose: "Delete all assignees for specific task execution for task execution lifecycle management and cleanup"

Business Logic Derivation:
  1. Used in TaskExecutionService for task execution assignment cleanup during task execution lifecycle management and cleanup operations
  2. Provides complete task execution assignment cleanup functionality for task execution workflows enabling comprehensive execution lifecycle management and cleanup functionality
  3. Critical for task execution lifecycle operations requiring complete assignment cleanup for execution management and lifecycle control
  4. Used in task execution management workflows for removing all task assignments for lifecycle operations and execution processing
  5. Enables task execution lifecycle management with complete assignment cleanup for comprehensive execution processing and lifecycle control

SQL Query: |
  DELETE FROM task_execution_user_mapping 
  WHERE task_executions_id = ?

Parameters:
  - taskExecutionId: Long (Task execution identifier for complete assignment cleanup)

Returns: void
Transaction: Required (@Modifying and @Transactional annotations)
Error Handling: No exception if no assignments found for task execution
```

#### Method: updateUserAction(Set<Long> taskExecutionUserMappingList)
```yaml
Signature: void updateUserAction(Set<Long> taskExecutionUserMappingList)
Purpose: "Update user action status for task execution user mappings for action tracking and completion management"

Business Logic Derivation:
  1. Used in ParameterExecutionService for user action status updates during action tracking and completion management operations
  2. Provides user action status management functionality for execution workflows enabling comprehensive action tracking and execution functionality
  3. Critical for action tracking operations requiring action status updates for execution tracking and action control
  4. Used in action tracking workflows for updating user action status for tracking operations and execution processing
  5. Enables action tracking with user action status updates for comprehensive execution processing and action control

SQL Query: |
  UPDATE task_execution_user_mapping 
  SET action_performed = TRUE 
  WHERE action_performed = FALSE 
    AND id IN (?, ?, ?, ...)

Parameters:
  - taskExecutionUserMappingList: Set<Long> (Set of task execution user mapping identifiers for action update)

Returns: void
Transaction: Required (@Modifying and @Transactional annotations)
Error Handling: No exception if no mappings found for action update
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Assignment Creation
```yaml
Usage: taskExecutionAssigneeRepository.saveAll(assignees)
Purpose: "Create task execution assignments in bulk for assignment management and workflow setup operations"

Business Logic Derivation:
  1. Used extensively in JobAssignmentService for bulk assignment creation during assignment setup and workflow management operations
  2. Provides efficient bulk assignment persistence for assignment workflows enabling comprehensive assignment creation and workflow functionality
  3. Critical for assignment setup operations requiring bulk assignment creation for workflow management and assignment control
  4. Used in assignment setup workflows for bulk assignment creation and workflow setup operations
  5. Enables assignment setup with efficient bulk operations for comprehensive workflow processing and assignment control

Transaction: Required
Error Handling: DataIntegrityViolationException for bulk constraint violations, assignment integrity issues
```

#### Pattern: save() for Individual Assignment Management
```yaml
Usage: taskExecutionAssigneeRepository.save(taskExecutionUserMapping)
Purpose: "Create and update individual task execution assignments for assignment management and action tracking"

Business Logic Derivation:
  1. Used in TaskExecutionService for individual assignment creation and updates during assignment management and action tracking operations
  2. Provides individual assignment persistence for assignment workflows enabling comprehensive assignment management and tracking functionality
  3. Critical for assignment management operations requiring individual assignment updates for assignment tracking and management control
  4. Used in assignment management workflows for individual assignment updates and action tracking operations
  5. Enables assignment management with individual assignment persistence for comprehensive assignment processing and tracking control

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations, assignment integrity issues
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Task Assignment Lifecycle Management
```yaml
Usage: Complete assignment lifecycle for task execution assignment management and access control
Purpose: "Manage task execution assignments for comprehensive assignment lifecycle functionality and execution processing"

Business Logic Derivation:
  1. Task assignment lifecycle management provides execution functionality through assignment creation, validation, and management operations
  2. Assignment lifecycle includes assignment creation, validation operations, state management, and cleanup workflows for execution control
  3. Assignment management operations require task execution processing for assignment lifecycle and execution control
  4. Task assignment operations enable comprehensive execution functionality with lifecycle capabilities and assignment management
  5. Assignment lifecycle management supports execution requirements and assignment functionality for task assignment processing

Common Usage Examples:
  - taskExecutionAssigneeRepository.isUserAssignedToAnyTask() across multiple services for job assignment validation
  - taskExecutionAssigneeRepository.existsByTaskExecutionIdAndUserId() for task execution access control validation
  - taskExecutionAssigneeRepository.saveAll() for bulk assignment creation during workflow setup
  - taskExecutionAssigneeRepository.updateAssigneeState() for assignment state management and tracking
  - Comprehensive task assignment management with lifecycle control and execution functionality

Transaction: Required for lifecycle operations and assignment management
Error Handling: Task assignment error handling and lifecycle validation verification
```

### Pattern: Job Assignment Reporting and Management
```yaml
Usage: Job-level assignment reporting for job assignee management and reporting functionality
Purpose: "Manage job assignments for comprehensive assignment reporting and job functionality"

Business Logic Derivation:
  1. Job assignment reporting operations require comprehensive assignment access for job-level reporting and assignment functionality
  2. Assignment reporting supports job requirements and reporting functionality for job processing workflows
  3. Job-level assignment operations ensure proper assignment reporting through assignment management and job control
  4. Job workflows coordinate assignment reporting with job processing for comprehensive job operations
  5. Assignment reporting supports job requirements and assignment functionality for comprehensive job assignment management

Common Usage Examples:
  - taskExecutionAssigneeRepository.findByJobId() for job assignee details retrieval and reporting
  - taskExecutionAssigneeRepository.getJobAssignees() for bulk job assignee retrieval and management
  - taskExecutionAssigneeRepository.getJobAssigneesCount() for job metrics and assignee counting
  - taskExecutionAssigneeRepository.getAllJobAssigneesUsersAndUserGroups() for comprehensive assignee reporting
  - Comprehensive job assignment reporting with assignee management and job functionality

Transaction: Not Required for reporting operations
Error Handling: Job assignment reporting error handling and assignee validation verification
```

### Pattern: User and User Group Lifecycle Integration
```yaml
Usage: User and user group lifecycle integration for assignment cleanup and lifecycle management functionality
Purpose: "Manage user and user group lifecycles for comprehensive assignment cleanup and lifecycle functionality"

Business Logic Derivation:
  1. User and user group lifecycle management enables assignment functionality through lifecycle integration and cleanup management
  2. Lifecycle integration supports user requirements and cleanup functionality for user processing workflows
  3. User lifecycle operations ensure proper assignment functionality through lifecycle management and cleanup control
  4. User workflows coordinate lifecycle integration with cleanup processing for comprehensive user operations
  5. Lifecycle management supports user requirements and assignment functionality for comprehensive user processing

Common Usage Examples:
  - taskExecutionAssigneeRepository.isUserAssignedToInProgressTasks() for user archive validation
  - taskExecutionAssigneeRepository.unassignUsersFromNonStartedAndInProgessTasks() for user cleanup
  - taskExecutionAssigneeRepository.isUserGroupAssignedToInProgressTasks() for user group validation
  - taskExecutionAssigneeRepository.removeUserGroupAssignees() for user group cleanup operations
  - Comprehensive user lifecycle management with assignment cleanup and lifecycle functionality

Transaction: Required for lifecycle operations and cleanup management
Error Handling: User lifecycle operation error handling and cleanup validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, isUserAssignedToAnyTask, isAllTaskUnassigned, isUserAssignedToInProgressTasks
  - findByJobId, getJobAssignees, getJobAssigneesCount, findByTaskExecutionIdAndUserIdIn
  - findByTaskExecutionIdInAndUserIdIn, findByTaskExecutionIdIn, findByTaskExecutionAndUser
  - findByTaskExecutionIdInAndUserGroupIdIn, getUserGroupAssignees, existsByTaskExecutionIdAndUserId
  - existsByTaskExecutionIdAndUserGroupId, isUserGroupAssignedToInProgressTasks
  - getAllJobAssigneesUsersAndUserGroups, getAllJobAssigneesUsersAndUserGroupsByRoles
  - findAllByTaskExecutionIdsIn, findAllByTaskExecutionId, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById, unassignUsersFromNonStartedAndInProgessTasks
  - unassignUsersByTaskExecutions, updateAssigneeState, unassignUserGroupIdsByTaskExecutions
  - removeUserGroupAssignees, deleteAllByTaskExecutionId, updateUserAction

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (task_executions_id, users_id, user_groups_id, state)
    * Foreign key violations (invalid task_executions_id, users_id, user_groups_id references)
    * Unique constraint violations for assignment combinations
    * Task execution assignment integrity constraint violations
  - EntityNotFoundException: Task execution user mapping not found by ID or criteria
  - OptimisticLockException: Concurrent task execution assignment modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or assignment context
  - ConstraintViolationException: Task execution assignment constraint violations

Validation Rules:
  - taskExecution: Required, must reference existing task execution for assignment context
  - user: Required, must reference existing user for user assignment
  - userGroup: Required, must reference existing user group for group assignment
  - actionPerformed: Boolean flag indicating assignment action completion, defaults to false
  - state: Required, assignment state enum for state tracking

Business Constraints:
  - Task execution assignments should be unique per task execution and user/user group combination for proper assignment integrity
  - Task execution, user, and user group references must be valid for assignment integrity and execution functionality
  - Task execution assignments must support execution workflow requirements and assignment functionality
  - Assignment lifecycle management must maintain referential integrity and execution workflow functionality consistency
  - Assignment management must ensure proper execution workflow control and assignment functionality
  - Assignment associations must support execution requirements and assignment functionality for execution processing
  - Assignment operations must maintain transaction consistency and constraint integrity for execution management
  - Assignment lifecycle management must maintain execution functionality and assignment consistency
  - Execution management must maintain assignment integrity and execution workflow requirements
  - Access control operations must ensure proper execution workflow management and assignment control
  - Either user or userGroup must be specified (not both null) for valid assignment
```

## Task Execution Assignment Considerations

### Execution Access Control Integration
```yaml
Access Control: Task execution assignments enable execution functionality through access control and assignment validation functionality
Assignment Management: Assignment associations enable execution functionality with comprehensive assignment capabilities
Assignment Lifecycle: Assignment lifecycle includes creation, validation, and management operations for execution functionality
Execution Management: Comprehensive execution management for assignment functionality and execution requirements during execution workflows
Validation Control: Assignment validation control for execution functionality and lifecycle management in execution processing
```

### User and Group Assignment Integration
```yaml
User Assignment: Individual user assignment for task execution functionality and user-specific execution management
Group Assignment: User group assignment for group-based task execution and comprehensive group execution functionality
Assignment Management: Task execution assignment management with user and group coordination for comprehensive execution assignment
User Integration: User assignment integration with task execution and user functionality for assignment workflows
Group Integration: User group assignment integration with task execution and group functionality for comprehensive execution management
```

### State and Action Integration
```yaml
State Management: Assignment state management for execution tracking and state management functionality
Action Tracking: Assignment action tracking with completion status and comprehensive action functionality
State Control: Assignment state control for execution completion and state management functionality
Action Completion: Assignment action completion tracking with state management and comprehensive completion functionality
Workflow Status: Execution workflow status management with assignment tracking and completion functionality for assignment workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TaskExecutionAssignee repository without JPA/Hibernate dependencies, focusing on task execution assignment management and access control patterns.
