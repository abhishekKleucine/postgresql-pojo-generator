# ITrainedUserTaskMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TrainedUserTaskMapping
- **Primary Purpose**: Manages trained user task mappings with complex user/group assignment, materialized view management, and facility-scoped training functionality
- **Key Relationships**: Complex relationships with TrainedUser, Task, User, UserGroup, Checklist, and Facility entities
- **Performance Characteristics**: High query volume with materialized views, complex joins, and performance optimization
- **Business Context**: Training management component providing task assignment, user training tracking, and performance-optimized trained user management for workflow execution

## Entity Mapping Documentation

### Field Mappings (TrainedUserTaskMapping Entity)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| trained_users_id | trainedUser.id | Long | false | null | Foreign key to trained_users |
| tasks_id | task.id | Long | false | null | Foreign key to tasks |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | trainedUser | TrainedUser | LAZY | Associated trained user, not null |
| @ManyToOne | task | Task | LAZY | Associated task, not null |

## Entity Overview
- **Mapping Entity**: Links TrainedUser and Task entities for task assignment
- **Facility Scoped**: All operations scoped by facility for multi-tenant isolation
- **User/Group Support**: Handles both individual users and user groups through TrainedUser
- **Materialized Views**: Advanced performance optimization with custom materialized views

## Available Repository Methods

### Standard CRUD Methods (JpaRepository)
- `findById(Long id)`
- `findAll()`
- `save(TrainedUserTaskMapping entity)`
- `saveAll(Iterable<TrainedUserTaskMapping> entities)`
- `deleteById(Long id)`
- `delete(TrainedUserTaskMapping entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (23 methods - ALL methods documented)

**User/Group Retrieval Methods (4 methods):**
- `findUserIdsByChecklistIdAndFacilityId(Long checklistId, Long facilityId)`
- `findUserGroupIdsByChecklistIdAndFacilityId(Long checklistId, Long facilityId)`
- `findTaskIdsByChecklistIdAndUserIdAndFacilityId(Long checklistId, Set<Long> userIds, Long facilityId)`
- `findTaskIdsByChecklistIdAndUserGroupIdAndFacilityId(Long checklistId, Long userGroupId, Long facilityId)`

**Trained User Management Methods (6 methods):**
- `findAllByChecklistIdAndFacilityId(Long checklistId, Long facilityId, Boolean isUser, Boolean isUserGroup, String query, int limit, int offset)`
- `findAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId(...)`
- `countAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId(...)`
- `findByFacilityIdAndChecklistId(Long facilityId, Long checklistId)`
- `getTrainedUserTaskMappingByChecklistIdAndFacilityId(Long checklistId, Long facilityId, String query)` (2 variants)

**Non-Trained User Management Methods (4 methods):**
- `findAllNonTrainedUsersByChecklistIdAndFacilityId(...)`
- `countAllNonTrainedUsersByChecklistIdAndFacilityId(...)`
- `findAllNonTrainedUserGroupsByChecklistIdAndFacilityId(...)`
- `countAllNonTrainedUserGroupsByChecklistIdAndFacilityId(...)`

**Assignment Management Methods (3 methods):**
- `deleteByChecklistIdAndUserIdInAndTaskIdIn(Long checklistId, Set<Long> unassignedIds, Set<Long> assignedTaskIds)`
- `deleteByChecklistIdAndUserGroupIdInAndTaskIdIn(Long checklistId, Set<Long> unassignedUserGroupIds, Set<Long> assignedTaskIds)`
- `deleteByTaskId(Long taskId)`

**Validation Methods (2 methods):**
- `existsByChecklistIdAndTaskIdAndUserIdAndFacilityId(...)`
- `existsByChecklistIdAndTaskIdAndUserGroupIdAndFacilityId(...)`

**Materialized View Management Methods (4 methods):**
- `createMaterializedViewForChecklistId(String viewName, Long checklistId, Long facilityId)`
- `addIndexInTrainedUserMaterialisedViewOnUserGroupId(String viewName)`
- `addIndexInTrainedUserMaterialisedViewOnUserId(String viewName)`
- `refreshMaterialisedViewForChecklistId(String viewName)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<TrainedUserTaskMapping> findById(Long id)
List<TrainedUserTaskMapping> findAll()
TrainedUserTaskMapping save(TrainedUserTaskMapping entity)
List<TrainedUserTaskMapping> saveAll(Iterable<TrainedUserTaskMapping> entities)
void deleteById(Long id)
void delete(TrainedUserTaskMapping entity)
boolean existsById(Long id)
long count()
```

## Key Method Documentation (All 23 Custom Methods - Complete Detail)

### Core User/Task Assignment Methods

#### Method: findUserIdsByChecklistIdAndFacilityId(Long checklistId, Long facilityId)
```yaml
Signature: Set<Long> findUserIdsByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("facilityId") Long facilityId)
Purpose: "Find all user IDs with task assignments for checklist and facility"

Business Logic Derivation:
  1. Used for user ID retrieval during checklist execution and user discovery operations
  2. Provides user access for execution workflows enabling comprehensive user management and execution functionality
  3. Critical for execution operations requiring user access for execution management and user control
  4. Used in execution workflows for accessing user IDs for execution operations and user processing
  5. Enables execution management with user access for comprehensive execution processing and user control

Native SQL Query: |
  SELECT tu.users_id
  FROM trained_user_tasks_mapping tutm
  INNER JOIN trained_users tu ON tutm.trained_users_id = tu.id
  WHERE tu.checklists_id = :checklistId
    AND tu.facilities_id = :facilityId
    AND tu.users_id IS NOT NULL

Parameters:
  - checklistId: Long (Checklist identifier for user retrieval)
  - facilityId: Long (Facility identifier for user scoping)

Returns: Set<Long> (user IDs with task assignments)
Transaction: Not Required (read operation)
Error Handling: Returns empty set if no users found
```

#### Method: findTaskIdsByChecklistIdAndUserIdAndFacilityId(Long checklistId, Set<Long> userIds, Long facilityId)
```yaml
Signature: Set<TrainedUsersView> findTaskIdsByChecklistIdAndUserIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("userIds") Set<Long> userIds, @Param("facilityId") Long facilityId)
Purpose: "Find task-user assignments for execution validation and assignment tracking"

Business Logic Derivation:
  1. Used for task-user assignment retrieval during execution validation and assignment tracking operations
  2. Provides assignment tracking for execution workflows enabling comprehensive assignment management and execution functionality
  3. Critical for execution operations requiring assignment tracking for execution management and assignment control
  4. Used in execution workflows for accessing task-user assignments for tracking operations and execution processing
  5. Enables execution management with assignment tracking for comprehensive execution processing and assignment control

Native SQL Query: |
  SELECT tutm.tasks_id AS taskId, tu.users_id AS userId
  FROM trained_user_tasks_mapping tutm
  INNER JOIN trained_users tu ON tutm.trained_users_id = tu.id
  WHERE tu.checklists_id = :checklistId
    AND tu.users_id IN (:userIds)
    AND tu.facilities_id = :facilityId

Parameters:
  - checklistId: Long (Checklist identifier for assignment retrieval)
  - userIds: Set<Long> (User identifiers for assignment filtering)
  - facilityId: Long (Facility identifier for assignment scoping)

Returns: Set<TrainedUsersView> (task-user assignment mappings)
Transaction: Not Required (read operation)
Error Handling: Returns empty set if no assignments found
```

#### Method: findAllByChecklistIdAndFacilityId(Long checklistId, Long facilityId, Boolean isUser, Boolean isUserGroup, String query, int limit, int offset)
```yaml
Signature: List<TrainedUsersView> findAllByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("facilityId") Long facilityId, @Param("isUser") Boolean isUser, @Param("isUserGroup") Boolean isUserGroup, @Param("query") String query, @Param("limit") int limit, @Param("offset") int offset)
Purpose: "Find all trained users with filtering, pagination, and user/group type support"

Business Logic Derivation:
  1. Used for trained user retrieval during training management and user discovery operations with comprehensive filtering
  2. Provides filtered user access for training workflows enabling comprehensive training management and user functionality
  3. Critical for training operations requiring filtered user access for training management and user control
  4. Used in training workflows for accessing trained users with filtering for training operations and user processing
  5. Enables training management with filtered user access for comprehensive training processing and user control

Native SQL Query: Uses complex native query from Queries.FIND_ALL_TRAINED_USERS_BY_CHECKLIST_ID_AND_FACILITYID

Parameters:
  - checklistId: Long (Checklist identifier for user retrieval)
  - facilityId: Long (Facility identifier for user scoping)
  - isUser: Boolean (Filter for individual users)
  - isUserGroup: Boolean (Filter for user groups)
  - query: String (Search query for user filtering)
  - limit: int (Pagination limit)
  - offset: int (Pagination offset)

Returns: List<TrainedUsersView> (filtered trained users)
Transaction: Not Required (read operation)
Error Handling: Returns empty list if no trained users found
```

#### Method: findUserGroupIdsByChecklistIdAndFacilityId(Long checklistId, Long facilityId)
```yaml
Signature: Set<Long> findUserGroupIdsByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("facilityId") Long facilityId)
Purpose: "Find all user group IDs with task assignments for checklist and facility"

Business Logic Derivation:
  1. Used for user group ID retrieval during checklist execution and group discovery operations
  2. Provides group access for execution workflows enabling comprehensive group management and execution functionality
  3. Critical for execution operations requiring group access for execution management and group control
  4. Used in execution workflows for accessing user group IDs for execution operations and group processing
  5. Enables execution management with group access for comprehensive execution processing and group control

Native SQL Query: |
  SELECT DISTINCT tu.user_groups_id
  FROM trained_user_tasks_mapping tutm
  INNER JOIN trained_users tu ON tutm.trained_users_id = tu.id
  WHERE tu.checklists_id = :checklistId
    AND tu.facilities_id = :facilityId
    AND tu.user_groups_id IS NOT NULL

Parameters:
  - checklistId: Long (Checklist identifier for group retrieval)
  - facilityId: Long (Facility identifier for group scoping)

Returns: Set<Long> (user group IDs with task assignments)
Transaction: Not Required (read operation)
Error Handling: Returns empty set if no user groups found
```

#### Method: findTaskIdsByChecklistIdAndUserGroupIdAndFacilityId(Long checklistId, Long userGroupId, Long facilityId)
```yaml
Signature: Set<String> findTaskIdsByChecklistIdAndUserGroupIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("userGroupId") Long userGroupId, @Param("facilityId") Long facilityId)
Purpose: "Find task IDs assigned to user group for checklist and facility"

Business Logic Derivation:
  1. Used for task ID retrieval during user group assignment and task discovery operations
  2. Provides task access for group workflows enabling comprehensive task management and group functionality
  3. Critical for group operations requiring task access for group management and task control
  4. Used in group workflows for accessing task IDs for group operations and task processing
  5. Enables group management with task access for comprehensive group processing and task control

JPQL Query: |
  SELECT tutm.task.id FROM TrainedUserTaskMapping tutm 
  INNER JOIN TrainedUser tu ON tutm.trainedUser.id = tu.id 
  WHERE tu.checklist.id = :checklistId 
    AND tu.userGroupId = :userGroupId 
    AND tu.facility.id = :facilityId

Parameters:
  - checklistId: Long (Checklist identifier for task retrieval)
  - userGroupId: Long (User group identifier for task filtering)
  - facilityId: Long (Facility identifier for task scoping)

Returns: Set<String> (task IDs assigned to user group)
Transaction: Not Required (read operation)
Error Handling: Returns empty set if no tasks found
```

### Assignment Management Methods

#### Method: deleteByChecklistIdAndUserIdInAndTaskIdIn(Long checklistId, Set<Long> unassignedIds, Set<Long> assignedTaskIds)
```yaml
Signature: void deleteByChecklistIdAndUserIdInAndTaskIdIn(@Param("checklistId") Long checklistId, @Param("userIds") Set<Long> unassignedIds, @Param("taskIds") Set<Long> assignedTaskIds)
Purpose: "Remove user task assignments for bulk unassignment operations"

Business Logic Derivation:
  1. Used for bulk user unassignment during training management and assignment removal operations
  2. Provides bulk removal for training workflows enabling comprehensive assignment management and training functionality
  3. Critical for training operations requiring bulk removal for training management and assignment control
  4. Used in training workflows for bulk user unassignment and assignment removal operations
  5. Enables training management with bulk removal for comprehensive training processing and assignment control

Annotation: @Transactional @Modifying(clearAutomatically = true)
Native SQL Query: Uses complex native query from Queries.DELETE_BY_CHECKLIST_ID_AND_USER_ID_IN_AND_TASK_ID_IN

Parameters:
  - checklistId: Long (Checklist identifier for assignment removal)
  - unassignedIds: Set<Long> (User identifiers for unassignment)
  - assignedTaskIds: Set<Long> (Task identifiers for assignment removal)

Returns: void (bulk delete operation)
Transaction: Required (@Transactional annotation)
Error Handling: Transaction rollback on failure, clears persistence context
```

#### Method: deleteByChecklistIdAndUserGroupIdInAndTaskIdIn(Long checklistId, Set<Long> unassignedUserGroupIds, Set<Long> assignedTaskIds)
```yaml
Signature: void deleteByChecklistIdAndUserGroupIdInAndTaskIdIn(@Param("checklistId") Long checklistId, @Param("userGroupIds") Set<Long> unassignedUserGroupIds, @Param("taskIds") Set<Long> assignedTaskIds)
Purpose: "Remove user group task assignments for bulk unassignment operations"

Business Logic Derivation:
  1. Used for bulk user group unassignment during training management and group assignment removal operations
  2. Provides bulk group removal for training workflows enabling comprehensive assignment management and training functionality
  3. Critical for training operations requiring bulk group removal for training management and assignment control
  4. Used in training workflows for bulk user group unassignment and assignment removal operations
  5. Enables training management with bulk group removal for comprehensive training processing and assignment control

Annotation: @Transactional @Modifying(clearAutomatically = true)
Native SQL Query: Uses complex native query from Queries.DELETE_BY_CHECKLIST_ID_AND_USER_GROUP_ID_IN_AND_TASK_ID_IN

Parameters:
  - checklistId: Long (Checklist identifier for assignment removal)
  - unassignedUserGroupIds: Set<Long> (User group identifiers for unassignment)
  - assignedTaskIds: Set<Long> (Task identifiers for assignment removal)

Returns: void (bulk delete operation)
Transaction: Required (@Transactional annotation)
Error Handling: Transaction rollback on failure, clears persistence context
```

#### Method: deleteByTaskId(Long taskId)
```yaml
Signature: void deleteByTaskId(@Param("taskId") Long taskId)
Purpose: "Delete all training assignments for specific task during task cleanup"

Business Logic Derivation:
  1. Used for task cleanup during task deletion and assignment removal operations
  2. Provides task-based cleanup for task workflows enabling comprehensive task management and cleanup functionality
  3. Critical for task operations requiring assignment cleanup for task management and cleanup control
  4. Used in task workflows for assignment cleanup and task removal operations
  5. Enables task management with assignment cleanup for comprehensive task processing and cleanup control

Annotation: @Transactional @Modifying
Native SQL Query: Uses native query from Queries.DELETE_BY_TASK_ID

Parameters:
  - taskId: Long (Task identifier for assignment cleanup)

Returns: void (delete operation)
Transaction: Required (@Transactional annotation)
Error Handling: Transaction rollback on failure
```

### Validation Methods

#### Method: existsByChecklistIdAndTaskIdAndUserIdAndFacilityId(Long checklistId, Long taskId, Long userId, Long facilityId)
```yaml
Signature: boolean existsByChecklistIdAndTaskIdAndUserIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("taskId") Long taskId, @Param("userId") Long userId, @Param("facilityId") Long currentFacilityId)
Purpose: "Check if user task assignment exists for validation and duplicate prevention"

Business Logic Derivation:
  1. Used for assignment validation during training assignment and existence checking operations
  2. Provides existence validation for training workflows enabling comprehensive validation management and training functionality
  3. Critical for validation operations requiring existence checking for validation management and training control
  4. Used in validation workflows for assignment existence checking and validation operations
  5. Enables validation management with existence checking for comprehensive validation processing and training control

Native SQL Query: Uses native query from Queries.EXISTS_BY_CHECKLIST_ID_AND_TASK_ID_AND_USER_ID_AND_FACILITY_ID

Parameters:
  - checklistId: Long (Checklist identifier for assignment validation)
  - taskId: Long (Task identifier for assignment validation)
  - userId: Long (User identifier for assignment validation)
  - facilityId: Long (Facility identifier for assignment scoping)

Returns: boolean (true if assignment exists, false otherwise)
Transaction: Not Required (read operation)
Error Handling: Returns false if assignment not found
```

#### Method: existsByChecklistIdAndTaskIdAndUserGroupIdAndFacilityId(Long checklistId, Long taskId, Long userGroupId, Long facilityId)
```yaml
Signature: boolean existsByChecklistIdAndTaskIdAndUserGroupIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("taskId") Long taskId, @Param("userGroupId") Long userGroupId, @Param("facilityId") Long currentFacilityId)
Purpose: "Check if user group task assignment exists for validation and duplicate prevention"

Business Logic Derivation:
  1. Used for group assignment validation during training assignment and existence checking operations
  2. Provides group existence validation for training workflows enabling comprehensive validation management and training functionality
  3. Critical for validation operations requiring group existence checking for validation management and training control
  4. Used in validation workflows for group assignment existence checking and validation operations
  5. Enables validation management with group existence checking for comprehensive validation processing and training control

Native SQL Query: Uses native query from Queries.EXISTS_BY_CHECKLIST_ID_AND_TASK_ID_AND_USER_GROUP_ID_AND_FACILITY_ID

Parameters:
  - checklistId: Long (Checklist identifier for assignment validation)
  - taskId: Long (Task identifier for assignment validation)
  - userGroupId: Long (User group identifier for assignment validation)
  - facilityId: Long (Facility identifier for assignment scoping)

Returns: boolean (true if group assignment exists, false otherwise)
Transaction: Not Required (read operation)
Error Handling: Returns false if group assignment not found
```

### Materialized View Optimization Methods

#### Method: createMaterializedViewForChecklistId(String viewName, Long checklistId, Long facilityId)
```yaml
Signature: void createMaterializedViewForChecklistId(@Param("viewName") String viewName, @Param("checklistId") Long checklistId, @Param("facilityId") Long currentFacilityId)
Purpose: "Create performance-optimized materialized view for checklist training data"

Business Logic Derivation:
  1. Used for performance optimization during training data analysis and query optimization operations
  2. Provides materialized view creation for optimization workflows enabling comprehensive performance management and training functionality
  3. Critical for optimization operations requiring view creation for optimization management and performance control
  4. Used in optimization workflows for materialized view creation and performance optimization operations
  5. Enables optimization management with view creation for comprehensive optimization processing and performance control

Native SQL Query: Uses complex DDL query from Queries.TRAINED_USER_MATERIALIZED_VIEW

Parameters:
  - viewName: String (Materialized view name for creation)
  - checklistId: Long (Checklist identifier for view scoping)
  - facilityId: Long (Facility identifier for view scoping)

Returns: void (DDL operation)
Transaction: Not Required (DDL operation)
Error Handling: SQLException for DDL failures, requires elevated database permissions
```

#### Method: addIndexInTrainedUserMaterialisedViewOnUserGroupId(String viewName)
```yaml
Signature: void addIndexInTrainedUserMaterialisedViewOnUserGroupId(@Param("viewName") String viewName)
Purpose: "Add index on user_group_id for materialized view query optimization"

Business Logic Derivation:
  1. Used for index creation during materialized view optimization and performance tuning operations
  2. Provides index creation for optimization workflows enabling comprehensive performance management and view functionality
  3. Critical for optimization operations requiring index creation for optimization management and performance control
  4. Used in optimization workflows for index creation and performance tuning operations
  5. Enables optimization management with index creation for comprehensive optimization processing and performance control

Native SQL Query: Uses DDL query from Queries.ADD_INDEX_IN_TRAINED_USER_MATERIALISED_VIEW_ON_USER_GROUP_ID

Parameters:
  - viewName: String (Materialized view name for index creation)

Returns: void (DDL operation)
Transaction: Not Required (DDL operation)
Error Handling: SQLException for index creation failures
```

#### Method: addIndexInTrainedUserMaterialisedViewOnUserId(String viewName)
```yaml
Signature: void addIndexInTrainedUserMaterialisedViewOnUserId(@Param("viewName") String viewName)
Purpose: "Add index on user_id for materialized view query optimization"

Business Logic Derivation:
  1. Used for index creation during materialized view optimization and performance tuning operations
  2. Provides user index creation for optimization workflows enabling comprehensive performance management and view functionality
  3. Critical for optimization operations requiring user index creation for optimization management and performance control
  4. Used in optimization workflows for user index creation and performance tuning operations
  5. Enables optimization management with user index creation for comprehensive optimization processing and performance control

Native SQL Query: Uses DDL query from Queries.ADD_INDEX_IN_TRAINED_USER_MATERIALISED_VIEW_ON_USER_ID

Parameters:
  - viewName: String (Materialized view name for index creation)

Returns: void (DDL operation)
Transaction: Not Required (DDL operation)
Error Handling: SQLException for index creation failures
```

#### Method: refreshMaterialisedViewForChecklistId(String viewName)
```yaml
Signature: void refreshMaterialisedViewForChecklistId(@Param("viewName") String viewName)
Purpose: "Refresh materialized view data for updated training assignments"

Business Logic Derivation:
  1. Used for view refresh during materialized view maintenance and data synchronization operations
  2. Provides view refresh for maintenance workflows enabling comprehensive data management and view functionality
  3. Critical for maintenance operations requiring view refresh for maintenance management and data control
  4. Used in maintenance workflows for view refresh and data synchronization operations
  5. Enables maintenance management with view refresh for comprehensive maintenance processing and data control

Native SQL Query: Uses DDL query from Queries.REFRESH_MATERIALISED_TRAINED_USER_VIEW_FOR_CHECKLIST_ID

Parameters:
  - viewName: String (Materialized view name for refresh)

Returns: void (DDL operation)
Transaction: Not Required (DDL operation)
Error Handling: SQLException for refresh failures
```

### Additional Trained User Management Methods

#### Method: findAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId(Long checklistId, Long facilityId, Boolean isUser, Boolean isUserGroup, Set<String> trainedUserIds)
```yaml
Signature: List<TrainedUsersView> findAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("facilityId") Long facilityId, @Param("isUser") Boolean isUser, @Param("isUserGroup") Boolean isUserGroup, @Param("trainedUserIds") Set<String> trainedUserIds)
Purpose: "Find all trained users with assigned tasks for comprehensive training management"

Native SQL Query: Uses complex native query from Queries.FIND_ALL_TRAINED_USERS_WITH_TASK_ID_BY_CHECKLIST_ID_AND_FACILITY_ID

Returns: List<TrainedUsersView> (trained users with task assignments)
Transaction: Not Required (read operation)
```

#### Method: countAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId(Long checklistId, Long facilityId, Boolean isUser, Boolean isUserGroup, String query)
```yaml
Signature: Long countAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("facilityId") Long facilityId, @Param("isUser") Boolean isUser, @Param("isUserGroup") Boolean isUserGroup, @Param("query") String query)
Purpose: "Count all trained users with assigned tasks for pagination support"

Native SQL Query: Uses complex native query from Queries.COUNT_ALL_TRAINED_USERS_WITH_ASSIGNED_TASKS_BY_CHECKLIST_ID_AND_FACILITY_ID

Returns: Long (count of trained users with task assignments)
Transaction: Not Required (read operation)
```

#### Method: findByFacilityIdAndChecklistId(Long facilityId, Long checklistId)
```yaml
Signature: List<TrainedUserTaskMapping> findByFacilityIdAndChecklistId(@Param("facilityId") Long facilityId, @Param("checklistId") Long checklistId)
Purpose: "Find training mappings by facility and checklist for comprehensive mapping retrieval"

Native SQL Query: |
  SELECT tutm.* FROM trained_user_tasks_mapping tutm 
  INNER JOIN trained_users tu ON tutm.trained_users_id = tu.id 
  INNER JOIN tasks t ON tutm.tasks_id = t.id
  WHERE tu.checklists_id = :checklistId AND tu.facilities_id = :facilityId AND t.archived = false

Returns: List<TrainedUserTaskMapping> (training mappings for facility and checklist)
Transaction: Not Required (read operation)
```

### Non-Trained User Management Methods

#### Method: findAllNonTrainedUsersByChecklistIdAndFacilityId(Long checklistId, Long facilityId, String query, int limit, int offset)
```yaml
Signature: List<TrainedUsersView> findAllNonTrainedUsersByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("facilityId") Long facilityId, @Param("query") String query, @Param("limit") int limit, @Param("offset") int offset)
Purpose: "Find all non-trained users for training assignment and user discovery"

Native SQL Query: Uses complex native query from Queries.GET_ALL_NON_TRAINED_USERS_BY_CHECKLIST_ID_AND_FACILITY_ID

Returns: List<TrainedUsersView> (non-trained users)
Transaction: Not Required (read operation)
```

#### Method: countAllNonTrainedUsersByChecklistIdAndFacilityId(Long checklistId, Long facilityId, String query)
```yaml
Signature: long countAllNonTrainedUsersByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("facilityId") Long facilityId, @Param("query") String query)
Purpose: "Count all non-trained users for pagination support and user discovery"

Native SQL Query: Uses complex native query from Queries.GET_ALL_NON_TRAINED_USERS_COUNT_BY_CHECKLIST_ID_AND_FACILITY_ID

Returns: long (count of non-trained users)
Transaction: Not Required (read operation)
```

#### Method: findAllNonTrainedUserGroupsByChecklistIdAndFacilityId(Long checklistId, Long facilityId, String query, int limit, int offset)
```yaml
Signature: List<TrainedUsersView> findAllNonTrainedUserGroupsByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("currentFacilityId") Long facilityId, @Param("query") String query, @Param("limit") int limit, @Param("offset") int offset)
Purpose: "Find all non-trained user groups for training assignment and group discovery"

Native SQL Query: Uses complex native query from Queries.GET_ALL_NON_TRAINED_USER_GROUPS_BY_CHECKLIST_ID_AND_FACILITY_ID

Returns: List<TrainedUsersView> (non-trained user groups)
Transaction: Not Required (read operation)
```

#### Method: countAllNonTrainedUserGroupsByChecklistIdAndFacilityId(Long checklistId, Long facilityId, String query)
```yaml
Signature: long countAllNonTrainedUserGroupsByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("currentFacilityId") Long facilityId, @Param("query") String query)
Purpose: "Count all non-trained user groups for pagination support and group discovery"

Native SQL Query: Uses complex native query from Queries.GET_ALL_NON_TRAINED_USER_GROUP_COUNT

Returns: long (count of non-trained user groups)
Transaction: Not Required (read operation)
```

### Additional Query Methods

#### Method: getTrainedUserTaskMappingByChecklistIdAndFacilityId(Long checklistId, Long facilityId, String query) - Variant 1
```yaml
Signature: List<TrainedUsersView> getTrainedUserTaskMappingByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("facilityId") Long facilityId, @Param("query") String query)
Purpose: "Get trained user task mappings with assigned users and groups"

Native SQL Query: Uses complex native query from Queries.GET_ALL_ASSIGNED_TRAINED_USERS_OR_GROUPS

Returns: List<TrainedUsersView> (trained user task mappings)
Transaction: Not Required (read operation)
```

#### Method: getTrainedUserTaskMappingByChecklistIdAndFacilityId(String viewName, String query) - Variant 2
```yaml
Signature: List<TrainedUsersView> getTrainedUserTaskMappingByChecklistIdAndFacilityId(@Param("viewName") String viewName, @Param("query") String query)
Purpose: "Get trained user task mappings from materialized view for optimized performance"

Native SQL Query: Uses materialized view query from Queries.GET_ALL_ASSIGNED_TRAINED_USER

Returns: List<TrainedUsersView> (trained user task mappings from materialized view)
Transaction: Not Required (read operation)
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Complete Training Assignment Lifecycle Management
```yaml
Usage: Complete training assignment lifecycle for comprehensive training management and assignment functionality
Purpose: "Manage training assignments for comprehensive training functionality and assignment processing"

Business Logic Derivation:
  1. Training assignment management provides training functionality through assignment creation, user management, and training operations
  2. Assignment lifecycle includes user assignment, task mapping, and training coordination for assignment control
  3. Assignment management operations require training processing for assignment lifecycle and user control
  4. Training operations enable comprehensive assignment functionality with user capabilities and management
  5. Assignment lifecycle management supports training requirements and functionality for training assignment processing

Common Usage Examples:
  - trainedUserTaskMappingRepository.findUserIdsByChecklistIdAndFacilityId() for user discovery
  - trainedUserTaskMappingRepository.deleteByChecklistIdAndUserIdInAndTaskIdIn() for bulk unassignment
  - trainedUserTaskMappingRepository.existsByChecklistIdAndTaskIdAndUserIdAndFacilityId() for validation
  - Training assignment creation and removal with comprehensive validation
  - User and group assignment management with facility isolation

Transaction: Required for assignment modification operations
Error Handling: Training assignment error handling and validation verification
```

### Pattern: Performance Optimization with Materialized Views
```yaml
Usage: Performance optimization with materialized views for large-scale training data and query functionality
Purpose: "Optimize training queries for comprehensive performance functionality and optimization processing"

Business Logic Derivation:
  1. Performance optimization management operations require comprehensive training access for optimization-level performance management and query functionality
  2. Optimization management supports performance requirements and functionality for query processing workflows
  3. Optimization-level training operations ensure proper performance functionality through query management and optimization control
  4. Training workflows coordinate optimization management with performance processing for comprehensive training operations
  5. Optimization management supports performance requirements and functionality for comprehensive training optimization management

Common Usage Examples:
  - trainedUserTaskMappingRepository.createMaterializedViewForChecklistId() for view creation
  - trainedUserTaskMappingRepository.addIndexInTrainedUserMaterialisedViewOnUserId() for index optimization
  - trainedUserTaskMappingRepository.refreshMaterialisedViewForChecklistId() for data synchronization
  - Dynamic view creation with checklist/facility scoping
  - Index management for optimal query performance

Transaction: Not Required for DDL operations
Error Handling: Materialized view error handling and optimization verification
```

### Pattern: Multi-Tenant Training Management
```yaml
Usage: Multi-tenant training management for facility-scoped training and isolation functionality
Purpose: "Manage multi-tenant training for comprehensive isolation functionality and tenant processing"

Business Logic Derivation:
  1. Multi-tenant training management operations require comprehensive training access for tenant-level training management and isolation functionality
  2. Tenant management supports isolation requirements and functionality for training processing workflows
  3. Tenant-level training operations ensure proper isolation functionality through training management and tenant control
  4. Training workflows coordinate tenant management with isolation processing for comprehensive training operations
  5. Tenant management supports isolation requirements and functionality for comprehensive multi-tenant training management

Common Usage Examples:
  - Facility-scoped user and group assignment management
  - Cross-facility data isolation with comprehensive security
  - Tenant-specific training assignment tracking
  - Multi-tenant user discovery and assignment validation
  - Facility-based materialized view optimization

Transaction: Required for tenant-specific operations
Error Handling: Multi-tenant error handling and isolation verification
```

## Business Usage Patterns

### Pattern: Checklist Training Assignment Management
```yaml
Usage: Complete training assignment lifecycle management
Operations:
  - User/group assignment validation
  - Task assignment creation and removal
  - Training status tracking
  - Facility-scoped isolation
Key Methods:
  - findUserIdsByChecklistIdAndFacilityId()
  - deleteByChecklistIdAndUserIdInAndTaskIdIn()
  - existsByChecklistIdAndTaskIdAndUserIdAndFacilityId()
```

### Pattern: Performance Optimization with Materialized Views
```yaml
Usage: Large-scale training data optimization
Operations:
  - Materialized view creation for complex training queries
  - Index creation for optimized access patterns
  - Data refresh for consistency
Key Methods:
  - createMaterializedViewForChecklistId()
  - addIndexInTrainedUserMaterialisedViewOnUserId()
  - refreshMaterialisedViewForChecklistId()
```

### Pattern: Training Status and User Management
```yaml
Usage: Training status tracking and user discovery
Operations:
  - Find trained vs non-trained users
  - User/group training assignment tracking
  - Training completion validation
Key Methods:
  - findAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId()
  - findAllNonTrainedUsersByChecklistIdAndFacilityId()
  - countAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId()
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findUserIdsByChecklistIdAndFacilityId, findTaskIdsByChecklistIdAndUserIdAndFacilityId, findAllByChecklistIdAndFacilityId
  - findUserGroupIdsByChecklistIdAndFacilityId, findTaskIdsByChecklistIdAndUserGroupIdAndFacilityId
  - findAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId, countAllTrainedUsersWithAssignedTasksByChecklistIdAndFacilityId
  - findByFacilityIdAndChecklistId, findAllNonTrainedUsersByChecklistIdAndFacilityId
  - countAllNonTrainedUsersByChecklistIdAndFacilityId, findAllNonTrainedUserGroupsByChecklistIdAndFacilityId
  - countAllNonTrainedUserGroupsByChecklistIdAndFacilityId, getTrainedUserTaskMappingByChecklistIdAndFacilityId (both variants)
  - existsByChecklistIdAndTaskIdAndUserIdAndFacilityId, existsByChecklistIdAndTaskIdAndUserGroupIdAndFacilityId

Transactional Methods:
  - deleteByChecklistIdAndUserIdInAndTaskIdIn (@Transactional @Modifying(clearAutomatically = true))
  - deleteByChecklistIdAndUserGroupIdInAndTaskIdIn (@Transactional @Modifying(clearAutomatically = true))
  - deleteByTaskId (@Transactional @Modifying)

DDL Operations (No Transaction Required):
  - createMaterializedViewForChecklistId, addIndexInTrainedUserMaterialisedViewOnUserGroupId
  - addIndexInTrainedUserMaterialisedViewOnUserId, refreshMaterialisedViewForChecklistId

Special Considerations:
  - clearAutomatically = true for bulk delete operations to clear persistence context
  - Materialized view operations require elevated database permissions
  - Performance-critical operations require careful transaction management
  - DDL operations are auto-commit and cannot be rolled back
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: Foreign key violations, constraint violations
  - EntityNotFoundException: Training assignments not found
  - SQLException: Materialized view operations, DDL failures
  - ConstraintViolationException: Duplicate assignments, invalid facility scoping

Validation Rules:
  - All operations must be facility-scoped for multi-tenant isolation
  - User and user group assignments are mutually exclusive within TrainedUser
  - Task assignments require valid trained user associations
  - Materialized view names must be unique and follow naming conventions

Business Constraints:
  - Facility isolation must be maintained across all operations
  - Training assignments must maintain referential integrity
  - User/group assignments cannot overlap within same checklist/facility
  - Materialized views require periodic refresh for data consistency
  - Performance optimization critical for large-scale training operations
```

## Training Assignment Management Considerations

### Complex Query Integration
```yaml
Native Queries: Repository uses complex native SQL queries from centralized Queries class for consistency
Query Optimization: Native queries optimized for specific training assignment use cases
Performance Tuning: Complex joins optimized for training data access patterns
Query Maintenance: Centralized query management in Queries class for easier maintenance
SQL Complexity: Advanced SQL features including CTEs, window functions, and complex joins
```

### Assignment Validation and Integrity
```yaml
Existence Checking: Comprehensive validation for duplicate assignment prevention
Facility Isolation: All operations maintain strict facility-based isolation
User/Group Separation: Mutually exclusive user and user group assignment validation
Task Assignment Integrity: Validation ensures proper task-user relationships
Assignment Lifecycle: Complete validation throughout assignment creation and removal
```

### Bulk Operations and Performance
```yaml
Bulk Assignment: Efficient bulk user and group assignment operations
Bulk Removal: Optimized bulk deletion with proper transaction management
Persistence Context Management: clearAutomatically = true for bulk operations
Transaction Coordination: Proper transaction management for bulk operations
Performance Optimization: Bulk operations designed for large-scale training management
```

### Materialized View Performance Optimization
```yaml
Dynamic View Creation: Parameterized materialized view creation for specific checklist/facility combinations
Index Management: Custom index creation for optimal query performance on user_id and user_group_id
Data Refresh: Manual refresh capability for maintaining data consistency
Query Optimization: Materialized views significantly improve performance for complex training queries
View Lifecycle: Complete materialized view lifecycle management including creation, indexing, and refresh
```

### Multi-Tenant Architecture Integration
```yaml
Facility Scoping: All operations strictly scoped by facility for complete tenant isolation
Cross-Tenant Security: Prevention of cross-facility data access through consistent facility filtering
Tenant Performance: Optimized queries with facility-based indexes for multi-tenant performance
Isolation Validation: Comprehensive validation ensures proper tenant isolation
Security Model: Facility-based security model with complete data isolation
```

### Training Status and User Discovery
```yaml
Training Status Tracking: Comprehensive tracking of trained vs non-trained users and groups
User Discovery: Advanced user and group discovery with filtering and pagination
Training Analytics: Support for training completion analysis and reporting
Status Management: Complete training status management with validation
Discovery Performance: Optimized queries for user and group discovery operations
```

### Assignment Workflow Integration
```yaml
Assignment Creation: Support for comprehensive training assignment creation workflows
Assignment Removal: Efficient assignment removal with proper cleanup
Assignment Validation: Complete validation throughout assignment workflows
Workflow Coordination: Integration with training workflow management systems
Assignment Tracking: Comprehensive tracking of assignment changes and updates
```

This comprehensive documentation provides the foundation for implementing an exact DAO layer replacement for the TrainedUserTaskMapping repository without JPA/Hibernate dependencies, focusing on complex training management, materialized view optimization, and facility-scoped multi-tenant training assignment patterns.
