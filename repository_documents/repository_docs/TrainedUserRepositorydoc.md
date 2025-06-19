# ITrainedUserRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TrainedUser (extends UserAuditIdentifiableBase)
- **Primary Purpose**: Manages trained user entities for training management with user/user group training assignment, checklist-facility training tracking, and task execution assignment functionality
- **Key Relationships**: Links Checklist, User, Facility, and UserGroup entities for comprehensive training management and task assignment control
- **Performance Characteristics**: High query volume with training validation, bulk assignment operations, and training lifecycle management
- **Business Context**: Training management component that provides user training tracking, checklist-facility training associations, task execution assignment validation, and training compliance functionality for workflow execution and training management

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditIdentifiableBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| checklists_id | checklistId / checklist.id | Long | false | null | Foreign key to checklists, immutable |
| users_id | userId / user.id | Long | true | null | Foreign key to users, immutable |
| facilities_id | facilityId / facility.id | Long | false | null | Foreign key to facilities, immutable |
| user_groups_id | userGroupId / userGroup.id | Long | true | null | Foreign key to user_groups, immutable |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | checklist | Checklist | LAZY | Associated checklist, not null, immutable |
| @ManyToOne | user | User | LAZY | Associated trained user, optional, immutable |
| @ManyToOne | facility | Facility | LAZY | Associated facility, not null, immutable |
| @ManyToOne | userGroup | UserGroup | LAZY | Associated user group, optional, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(TrainedUser entity)`
- `saveAll(Iterable<TrainedUser> entities)`
- `deleteById(Long id)`
- `delete(TrainedUser entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (20 methods - ALL methods documented)

**Training Validation Methods:**
- `isUserGroupAddedToChecklist(Long checklistId, Set<Long> assignedUserGroupIds)`
- `isUserAddedToChecklist(Long checklistId, Set<Long> assignedUserIds)`
- `verifyUserIsAssignedToTheChecklist(Long checklistId, Long userId)`
- `validateIfUsersAreTrainedUsersForChecklist(Long checklistId, Set<Long> assignedUserIds, Long facilityId)`
- `validateIfUserGroupsAreTrainedUserGroupsForChecklist(Long checklistId, Set<Long> assignedUserGroupIds, Long facilityId)`

**Training Retrieval Methods:**
- `findAllByChecklistIdAndFacilityIdAndUserGroupIdIn(Long checklistId, Long facilityId, Set<Long> userGroupIds)`
- `findAllByChecklistIdAndFacilityIdAndUserIdIn(Long checklistId, Long facilityId, Set<Long> userIds)`
- `findAllByChecklistIdAndUserIdIn(Long checklistId, Set<Long> userIds)`
- `findAllByChecklistIdAndUserGroupIdIn(Long checklistId, Collection<Long> userGroupIds)`
- `findAllByChecklistIdAndFacilityId(Long checklistId, Long facilityId)`

**Training Assignment Discovery Methods:**
- `findAllUserIdsByChecklistId(Long checklistId)`
- `findAllUserGroupIdsByChecklistId(Long checklistId)`
- `findAllUserIdsOfFacility(Long facilityId)`
- `findAllUserGroupIdsOfFacility(Long facilityId)`

**Training Cleanup Methods:**
- `deleteByChecklistIdAndFacilityIdAndUserGroupId(Long checklistId, Long facilityId, Set<Long> userGroupIds)`
- `deleteByChecklistIdAndFacilityIdAndUserId(Long checklistId, Long facilityId, Set<Long> userIds)`
- `deleteByChecklistIdAndFacilityIdAndUserIdIn(Long checklistId, Long facilityId, Set<Long> userIds)`
- `deleteByChecklistIdAndFacilityIdAndUserGroupIdIn(Long checklistId, Long facilityId, Set<Long> userGroupIds)`
- `deleteByUserGroupId(Long id)`

**Complex Training Operations:**
- `deleteByChecklistIdAndFacilityIdAndUserGroupId(Long checklistId, Long facilityId, Set<Long> unassignedUserIds, Set<Long> unassignedUserGroupIds, Set<Long> taskIds)` (with task mapping cleanup)

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<TrainedUser> findById(Long id)
List<TrainedUser> findAll()
TrainedUser save(TrainedUser entity)
List<TrainedUser> saveAll(Iterable<TrainedUser> entities)
void deleteById(Long id)
void delete(TrainedUser entity)
boolean existsById(Long id)
long count()
```

### Training Validation Methods

#### Method: isUserGroupAddedToChecklist(Long checklistId, Set<Long> assignedUserGroupIds)
```yaml
Signature: boolean isUserGroupAddedToChecklist(Long checklistId, Set<Long> assignedUserGroupIds)
Purpose: "Check if user groups are added to checklist for training validation and assignment verification"

Business Logic Derivation:
  1. Used for training validation during task assignment and checklist training verification operations
  2. Provides user group training validation for checklist workflows enabling training verification and assignment validation functionality
  3. Critical for training verification operations requiring user group training validation for checklist training and assignment management
  4. Used in training verification workflows for validating user group training assignments for checklist training requirements
  5. Enables training validation with user group verification for comprehensive training management and assignment validation

SQL Query: |
  SELECT EXISTS(
    SELECT 1
    FROM trained_users tu
    INNER JOIN trained_user_tasks_mapping tutm ON tu.id = tutm.trained_users_id
    WHERE tu.checklists_id = ? AND tu.user_groups_id IN (?, ?, ?, ...)
  )

Parameters:
  - checklistId: Long (Checklist identifier for training validation context)
  - assignedUserGroupIds: Set<Long> (Set of user group identifiers for training validation)

Returns: boolean (true if user groups are trained for checklist, false otherwise)
Transaction: Not Required
Error Handling: Returns false if no trained user groups found for checklist
```

#### Method: verifyUserIsAssignedToTheChecklist(Long checklistId, Long userId)
```yaml
Signature: boolean verifyUserIsAssignedToTheChecklist(Long checklistId, Long userId)
Purpose: "Verify user assignment to checklist for training validation and access control"

Business Logic Derivation:
  1. Used extensively in JobService for user assignment verification during job creation and checklist access validation
  2. Provides user training verification for checklist access enabling access control and training validation functionality
  3. Critical for access control operations requiring user training verification for checklist access and training management
  4. Used in job creation workflows for verifying user training assignments for checklist access and training requirements
  5. Enables access control with user training verification for comprehensive training management and access validation

SQL Query: |
  SELECT EXISTS(
    SELECT 1 FROM trained_users tu 
    WHERE tu.checklists_id = ? AND tu.users_id = ?
  )

Parameters:
  - checklistId: Long (Checklist identifier for training verification context)
  - userId: Long (User identifier for training verification)

Returns: boolean (true if user is trained for checklist, false otherwise)
Transaction: Not Required
Error Handling: Returns false if user is not trained for checklist
```

### Training Assignment Discovery Methods

#### Method: findAllUserIdsByChecklistId(Long checklistId)
```yaml
Signature: Set<Long> findAllUserIdsByChecklistId(Long checklistId)
Purpose: "Find all trained user IDs for checklist for task assignment and training management"

Business Logic Derivation:
  1. Used extensively in JobService and JobAssignmentService for user assignment discovery during task execution assignment operations
  2. Provides checklist-scoped user discovery for task assignment workflows enabling comprehensive task assignment and training management
  3. Critical for task assignment operations requiring trained user identification for task execution and assignment management
  4. Used in task assignment workflows for discovering trained users for task execution assignment and training requirements
  5. Enables task assignment with trained user discovery for comprehensive training management and task execution control

SQL Query: |
  SELECT DISTINCT tu.users_id
  FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.users_id IS NOT NULL

Parameters:
  - checklistId: Long (Checklist identifier for trained user discovery)

Returns: Set<Long> (set of trained user IDs for checklist)
Transaction: Not Required
Error Handling: Returns empty set if no trained users found for checklist
```

#### Method: findAllUserGroupIdsByChecklistId(Long checklistId)
```yaml
Signature: Set<Long> findAllUserGroupIdsByChecklistId(Long checklistId)
Purpose: "Find all trained user group IDs for checklist for task assignment and training management"

Business Logic Derivation:
  1. Used extensively in JobService and JobAssignmentService for user group assignment discovery during task execution assignment operations
  2. Provides checklist-scoped user group discovery for task assignment workflows enabling comprehensive task assignment and training management
  3. Critical for task assignment operations requiring trained user group identification for task execution and assignment management
  4. Used in task assignment workflows for discovering trained user groups for task execution assignment and training requirements
  5. Enables task assignment with trained user group discovery for comprehensive training management and task execution control

SQL Query: |
  SELECT DISTINCT tu.user_groups_id
  FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.user_groups_id IS NOT NULL

Parameters:
  - checklistId: Long (Checklist identifier for trained user group discovery)

Returns: Set<Long> (set of trained user group IDs for checklist)
Transaction: Not Required
Error Handling: Returns empty set if no trained user groups found for checklist
```

### Training Retrieval Methods

#### Method: findAllByChecklistIdAndFacilityIdAndUserGroupIdIn(Long checklistId, Long facilityId, Set<Long> userGroupIds)
```yaml
Signature: List<TrainedUser> findAllByChecklistIdAndFacilityIdAndUserGroupIdIn(Long checklistId, Long facilityId, Set<Long> userGroupIds)
Purpose: "Find trained users by checklist, facility, and user groups for training management and assignment operations"

Business Logic Derivation:
  1. Used for training management during user group assignment and training tracking operations
  2. Provides facility-scoped training retrieval for training workflows enabling comprehensive training management and assignment functionality
  3. Critical for training management operations requiring facility-based training access for training tracking and assignment management
  4. Used in training management workflows for accessing facility-based training data for training operations and assignment processing
  5. Enables training management with facility-scoped training access for comprehensive training tracking and assignment control

SQL Query: |
  SELECT tu.* FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.facilities_id = ? 
  AND tu.user_groups_id IN (?, ?, ?, ...)

Parameters:
  - checklistId: Long (Checklist identifier for training context)
  - facilityId: Long (Facility identifier for facility-scoped training)
  - userGroupIds: Set<Long> (Set of user group identifiers for training retrieval)

Returns: List<TrainedUser> (trained users for checklist, facility, and user groups)
Transaction: Not Required
Error Handling: Returns empty list if no trained users found for criteria
```

### Training Cleanup Methods

#### Method: deleteByChecklistIdAndFacilityIdAndUserGroupIdIn(Long checklistId, Long facilityId, Set<Long> userGroupIds)
```yaml
Signature: void deleteByChecklistIdAndFacilityIdAndUserGroupIdIn(Long checklistId, Long facilityId, Set<Long> userGroupIds)
Purpose: "Delete trained users by checklist, facility, and user groups for training lifecycle management and cleanup"

Business Logic Derivation:
  1. Used for training lifecycle management during user group unassignment and training cleanup operations
  2. Provides efficient training cleanup for training workflows enabling comprehensive training lifecycle management and cleanup functionality
  3. Critical for training lifecycle operations requiring training cleanup for training management and lifecycle control
  4. Used in training management workflows for cleaning up training assignments for training lifecycle and assignment management operations
  5. Enables training lifecycle management with efficient training cleanup for comprehensive training management and lifecycle control

SQL Query: |
  DELETE FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.facilities_id = ? 
  AND tu.user_groups_id IN (?, ?, ?, ...)

Parameters:
  - checklistId: Long (Checklist identifier for training cleanup context)
  - facilityId: Long (Facility identifier for facility-scoped cleanup)
  - userGroupIds: Set<Long> (Set of user group identifiers for training cleanup)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found for cleanup criteria
```

### Facility-Scoped Discovery Methods

#### Method: findAllUserIdsOfFacility(Long facilityId)
```yaml
Signature: Set<Long> findAllUserIdsOfFacility(Long facilityId)
Purpose: "Find all trained user IDs for facility for facility-based assignment and training management"

Business Logic Derivation:
  1. Used in JobAssignmentService for facility-based user discovery during task assignment and training management operations
  2. Provides facility-scoped user discovery for assignment workflows enabling comprehensive facility-based assignment and training management
  3. Critical for facility-based operations requiring trained user identification for facility assignment and training management
  4. Used in facility assignment workflows for discovering trained users for facility-based assignment and training requirements
  5. Enables facility-based assignment with trained user discovery for comprehensive training management and facility control

SQL Query: |
  SELECT DISTINCT tu.users_id
  FROM trained_users tu
  WHERE tu.facilities_id = ? AND tu.users_id IS NOT NULL

Parameters:
  - facilityId: Long (Facility identifier for facility-scoped user discovery)

Returns: Set<Long> (set of trained user IDs for facility)
Transaction: Not Required
Error Handling: Returns empty set if no trained users found for facility
```

#### Method: findAllUserGroupIdsOfFacility(Long facilityId)
```yaml
Signature: Set<Long> findAllUserGroupIdsOfFacility(Long facilityId)
Purpose: "Find all trained user group IDs for facility for facility-based assignment and training management"

Business Logic Derivation:
  1. Used in JobAssignmentService for facility-based user group discovery during task assignment and training management operations
  2. Provides facility-scoped user group discovery for assignment workflows enabling comprehensive facility-based assignment and training management
  3. Critical for facility-based operations requiring trained user group identification for facility assignment and training management
  4. Used in facility assignment workflows for discovering trained user groups for facility-based assignment and training requirements
  5. Enables facility-based assignment with trained user group discovery for comprehensive training management and facility control

SQL Query: |
  SELECT DISTINCT tu.user_groups_id
  FROM trained_users tu
  WHERE tu.facilities_id = ? AND tu.user_groups_id IS NOT NULL

Parameters:
  - facilityId: Long (Facility identifier for facility-scoped user group discovery)

Returns: Set<Long> (set of trained user group IDs for facility)
Transaction: Not Required
Error Handling: Returns empty set if no trained user groups found for facility
```

### Training Validation Methods (Continued)

#### Method: isUserAddedToChecklist(Long checklistId, Set<Long> assignedUserIds)
```yaml
Signature: boolean isUserAddedToChecklist(Long checklistId, Set<Long> assignedUserIds)
Purpose: "Check if users are added to checklist for training validation and assignment verification"

Business Logic Derivation:
  1. Used for training validation during task assignment and checklist training verification operations
  2. Provides user training validation for checklist workflows enabling training verification and assignment validation functionality
  3. Critical for training verification operations requiring user training validation for checklist training and assignment management
  4. Used in training verification workflows for validating user training assignments for checklist training requirements
  5. Enables training validation with user verification for comprehensive training management and assignment validation

SQL Query: |
  SELECT EXISTS(
    SELECT 1
    FROM trained_users tu
    WHERE tu.checklists_id = ? AND tu.users_id IN (?, ?, ?, ...)
  )

Parameters:
  - checklistId: Long (Checklist identifier for training validation context)
  - assignedUserIds: Set<Long> (Set of user identifiers for training validation)

Returns: boolean (true if users are trained for checklist, false otherwise)
Transaction: Not Required
Error Handling: Returns false if no trained users found for checklist
```

#### Method: validateIfUsersAreTrainedUsersForChecklist(Long checklistId, Set<Long> assignedUserIds, Long facilityId)
```yaml
Signature: Set<Long> validateIfUsersAreTrainedUsersForChecklist(Long checklistId, Set<Long> assignedUserIds, Long facilityId)
Purpose: "Validate if users are trained users for checklist with facility context for training verification"

Business Logic Derivation:
  1. Used for training validation during task assignment with facility-scoped training verification operations
  2. Provides facility-scoped user training validation for checklist workflows enabling comprehensive training verification functionality
  3. Critical for facility-based training verification operations requiring user training validation for checklist training and facility management
  4. Used in facility training workflows for validating user training assignments within facility context for training requirements
  5. Enables facility-based training validation with user verification for comprehensive training management and facility control

SQL Query: |
  SELECT FROM trained_users tu 
  WHERE tu.checklists_id = ? AND tu.users_id IN (?, ?, ?, ...) 
  AND tu.facilities_id = ?

Parameters:
  - checklistId: Long (Checklist identifier for training validation context)
  - assignedUserIds: Set<Long> (Set of user identifiers for training validation)
  - facilityId: Long (Facility identifier for facility-scoped validation)

Returns: Set<Long> (set of validated trained user IDs for checklist and facility)
Transaction: Not Required
Error Handling: Returns empty set if no trained users found for checklist and facility
```

#### Method: validateIfUserGroupsAreTrainedUserGroupsForChecklist(Long checklistId, Set<Long> assignedUserGroupIds, Long facilityId)
```yaml
Signature: Set<Long> validateIfUserGroupsAreTrainedUserGroupsForChecklist(Long checklistId, Set<Long> assignedUserGroupIds, Long facilityId)
Purpose: "Validate if user groups are trained user groups for checklist with facility context for training verification"

Business Logic Derivation:
  1. Used for training validation during task assignment with facility-scoped user group training verification operations
  2. Provides facility-scoped user group training validation for checklist workflows enabling comprehensive training verification functionality
  3. Critical for facility-based training verification operations requiring user group training validation for checklist training and facility management
  4. Used in facility training workflows for validating user group training assignments within facility context for training requirements
  5. Enables facility-based training validation with user group verification for comprehensive training management and facility control

SQL Query: |
  SELECT FROM trained_users tu 
  WHERE tu.checklists_id = ? AND tu.user_groups_id IN (?, ?, ?, ...) 
  AND tu.facilities_id = ?

Parameters:
  - checklistId: Long (Checklist identifier for training validation context)
  - assignedUserGroupIds: Set<Long> (Set of user group identifiers for training validation)
  - facilityId: Long (Facility identifier for facility-scoped validation)

Returns: Set<Long> (set of validated trained user group IDs for checklist and facility)
Transaction: Not Required
Error Handling: Returns empty set if no trained user groups found for checklist and facility
```

### Training Retrieval Methods (Continued)

#### Method: findAllByChecklistIdAndFacilityIdAndUserIdIn(Long checklistId, Long facilityId, Set<Long> userIds)
```yaml
Signature: List<TrainedUser> findAllByChecklistIdAndFacilityIdAndUserIdIn(Long checklistId, Long facilityId, Set<Long> userIds)
Purpose: "Find trained users by checklist, facility, and users for training management and assignment operations"

Business Logic Derivation:
  1. Used for training management during user assignment and training tracking operations
  2. Provides facility-scoped training retrieval for training workflows enabling comprehensive training management and assignment functionality
  3. Critical for training management operations requiring facility-based training access for training tracking and assignment management
  4. Used in training management workflows for accessing facility-based training data for training operations and assignment processing
  5. Enables training management with facility-scoped training access for comprehensive training tracking and assignment control

SQL Query: |
  SELECT tu.* FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.facilities_id = ? 
  AND tu.users_id IN (?, ?, ?, ...)

Parameters:
  - checklistId: Long (Checklist identifier for training context)
  - facilityId: Long (Facility identifier for facility-scoped training)
  - userIds: Set<Long> (Set of user identifiers for training retrieval)

Returns: List<TrainedUser> (trained users for checklist, facility, and users)
Transaction: Not Required
Error Handling: Returns empty list if no trained users found for criteria
```

#### Method: findAllByChecklistIdAndUserIdIn(Long checklistId, Set<Long> userIds)
```yaml
Signature: List<TrainedUser> findAllByChecklistIdAndUserIdIn(Long checklistId, Set<Long> userIds)
Purpose: "Find trained users by checklist and users for training management and assignment operations"

Business Logic Derivation:
  1. Used for training management during user assignment and training tracking operations across facilities
  2. Provides checklist-scoped training retrieval for training workflows enabling comprehensive training management and assignment functionality
  3. Critical for training management operations requiring checklist-based training access for training tracking and assignment management
  4. Used in training management workflows for accessing checklist-based training data for training operations and assignment processing
  5. Enables training management with checklist-scoped training access for comprehensive training tracking and assignment control

SQL Query: |
  SELECT tu.* FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.users_id IN (?, ?, ?, ...)

Parameters:
  - checklistId: Long (Checklist identifier for training context)
  - userIds: Set<Long> (Set of user identifiers for training retrieval)

Returns: List<TrainedUser> (trained users for checklist and users)
Transaction: Not Required
Error Handling: Returns empty list if no trained users found for criteria
```

#### Method: findAllByChecklistIdAndUserGroupIdIn(Long checklistId, Collection<Long> userGroupIds)
```yaml
Signature: List<TrainedUser> findAllByChecklistIdAndUserGroupIdIn(Long checklistId, Collection<Long> userGroupIds)
Purpose: "Find trained users by checklist and user groups for training management and assignment operations"

Business Logic Derivation:
  1. Used for training management during user group assignment and training tracking operations across facilities
  2. Provides checklist-scoped training retrieval for training workflows enabling comprehensive training management and assignment functionality
  3. Critical for training management operations requiring checklist-based training access for training tracking and assignment management
  4. Used in training management workflows for accessing checklist-based training data for training operations and assignment processing
  5. Enables training management with checklist-scoped training access for comprehensive training tracking and assignment control

SQL Query: |
  SELECT tu.* FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.user_groups_id IN (?, ?, ?, ...)

Parameters:
  - checklistId: Long (Checklist identifier for training context)
  - userGroupIds: Collection<Long> (Collection of user group identifiers for training retrieval)

Returns: List<TrainedUser> (trained users for checklist and user groups)
Transaction: Not Required
Error Handling: Returns empty list if no trained users found for criteria
```

#### Method: findAllByChecklistIdAndFacilityId(Long checklistId, Long facilityId)
```yaml
Signature: List<TrainedUser> findAllByChecklistIdAndFacilityId(Long checklistId, Long facilityId)
Purpose: "Find all trained users by checklist and facility for training management and facility-based operations"

Business Logic Derivation:
  1. Used for comprehensive training management during facility-based training retrieval and training tracking operations
  2. Provides complete facility-scoped training access for training workflows enabling comprehensive training management and facility functionality
  3. Critical for facility-based training management operations requiring complete training access for training tracking and facility management
  4. Used in facility training workflows for accessing all facility-based training data for training operations and facility processing
  5. Enables comprehensive facility training management with complete training access for facility-scoped training tracking and control

SQL Query: |
  SELECT tu.* FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.facilities_id = ?

Parameters:
  - checklistId: Long (Checklist identifier for training context)
  - facilityId: Long (Facility identifier for facility-scoped training)

Returns: List<TrainedUser> (all trained users for checklist and facility)
Transaction: Not Required
Error Handling: Returns empty list if no trained users found for checklist and facility
```

### Training Cleanup Methods (Continued)

#### Method: deleteByChecklistIdAndFacilityIdAndUserGroupId(Long checklistId, Long facilityId, Set<Long> userGroupIds) - Simple Version
```yaml
Signature: void deleteByChecklistIdAndFacilityIdAndUserGroupId(Long checklistId, Long facilityId, Set<Long> userGroupIds)
Purpose: "Delete trained users by checklist, facility, and user groups for training lifecycle management and cleanup"

Business Logic Derivation:
  1. Used for training lifecycle management during user group unassignment and training cleanup operations
  2. Provides efficient training cleanup for training workflows enabling comprehensive training lifecycle management and cleanup functionality
  3. Critical for training lifecycle operations requiring training cleanup for training management and lifecycle control
  4. Used in training management workflows for cleaning up training assignments for training lifecycle and assignment management operations
  5. Enables training lifecycle management with efficient training cleanup for comprehensive training management and lifecycle control

SQL Query: |
  DELETE FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.facilities_id = ? 
  AND tu.user_groups_id IN (?, ?, ?, ...)

Parameters:
  - checklistId: Long (Checklist identifier for training cleanup context)
  - facilityId: Long (Facility identifier for facility-scoped cleanup)
  - userGroupIds: Set<Long> (Set of user group identifiers for training cleanup)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found for cleanup criteria
```

#### Method: deleteByChecklistIdAndFacilityIdAndUserId(Long checklistId, Long facilityId, Set<Long> userIds)
```yaml
Signature: void deleteByChecklistIdAndFacilityIdAndUserId(Long checklistId, Long facilityId, Set<Long> userIds)
Purpose: "Delete trained users by checklist, facility, and users for training lifecycle management and cleanup"

Business Logic Derivation:
  1. Used for training lifecycle management during user unassignment and training cleanup operations
  2. Provides efficient training cleanup for training workflows enabling comprehensive training lifecycle management and cleanup functionality
  3. Critical for training lifecycle operations requiring training cleanup for training management and lifecycle control
  4. Used in training management workflows for cleaning up training assignments for training lifecycle and assignment management operations
  5. Enables training lifecycle management with efficient training cleanup for comprehensive training management and lifecycle control

SQL Query: |
  DELETE FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.facilities_id = ? 
  AND tu.users_id IN (?, ?, ?, ...)

Parameters:
  - checklistId: Long (Checklist identifier for training cleanup context)
  - facilityId: Long (Facility identifier for facility-scoped cleanup)
  - userIds: Set<Long> (Set of user identifiers for training cleanup)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found for cleanup criteria
```

#### Method: deleteByChecklistIdAndFacilityIdAndUserIdIn(Long checklistId, Long facilityId, Set<Long> userIds)
```yaml
Signature: void deleteByChecklistIdAndFacilityIdAndUserIdIn(Long checklistId, Long facilityId, Set<Long> userIds)
Purpose: "Delete trained users by checklist, facility, and users using IN clause for training lifecycle management and cleanup"

Business Logic Derivation:
  1. Used for training lifecycle management during user unassignment and training cleanup operations with IN clause optimization
  2. Provides efficient training cleanup for training workflows enabling comprehensive training lifecycle management and cleanup functionality
  3. Critical for training lifecycle operations requiring training cleanup for training management and lifecycle control
  4. Used in training management workflows for cleaning up training assignments for training lifecycle and assignment management operations
  5. Enables training lifecycle management with efficient training cleanup for comprehensive training management and lifecycle control

SQL Query: |
  DELETE FROM trained_users tu
  WHERE tu.checklists_id = ? AND tu.facilities_id = ? 
  AND tu.users_id IN (?, ?, ?, ...)

Parameters:
  - checklistId: Long (Checklist identifier for training cleanup context)
  - facilityId: Long (Facility identifier for facility-scoped cleanup)
  - userIds: Set<Long> (Set of user identifiers for training cleanup)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found for cleanup criteria
```

#### Method: deleteByUserGroupId(Long id)
```yaml
Signature: void deleteByUserGroupId(Long id)
Purpose: "Delete trained users by user group ID for user group lifecycle management and cleanup"

Business Logic Derivation:
  1. Used for user group lifecycle management during user group deletion and training cleanup operations
  2. Provides comprehensive training cleanup for user group workflows enabling complete user group lifecycle management and cleanup functionality
  3. Critical for user group lifecycle operations requiring complete training cleanup for user group management and lifecycle control
  4. Used in user group management workflows for cleaning up all training assignments for user group lifecycle and management operations
  5. Enables user group lifecycle management with complete training cleanup for comprehensive user group management and lifecycle control

SQL Query: |
  DELETE FROM trained_users tu
  WHERE tu.user_groups_id = ?

Parameters:
  - id: Long (User group identifier for complete training cleanup)

Returns: void
Transaction: Required
Error Handling: No exception if no matching records found for user group
```

### Complex Training Operations

#### Method: deleteByChecklistIdAndFacilityIdAndUserGroupId (with task mapping cleanup)
```yaml
Signature: void deleteByChecklistIdAndFacilityIdAndUserGroupId(Long checklistId, Long facilityId, Set<Long> unassignedUserIds, Set<Long> unassignedUserGroupIds, Set<Long> taskIds)
Purpose: "Delete trained user task mappings for checklist, facility, users, and user groups for complex training lifecycle management"

Business Logic Derivation:
  1. Used for complex training lifecycle management during training assignment cleanup with task mapping coordination
  2. Provides comprehensive training cleanup for training workflows enabling training lifecycle management with task mapping cleanup functionality
  3. Critical for training lifecycle operations requiring task mapping cleanup for training management and task assignment lifecycle control
  4. Used in training management workflows for coordinated cleanup of training assignments and task mappings for lifecycle management operations
  5. Enables comprehensive training lifecycle management with task mapping cleanup for training management and assignment lifecycle control

SQL Query: |
  DELETE FROM trained_user_tasks_mapping tutm 
  WHERE tutm.tasks_id IN (?, ?, ?, ...)
  AND EXISTS (
    SELECT 1 FROM trained_users tu
    WHERE tu.id = tutm.trained_users_id
    AND tu.checklists_id = ? AND tu.facilities_id = ?
    AND (tu.user_groups_id IN (?, ?, ?, ...) OR tu.users_id IN (?, ?, ?, ...))
  )

Parameters:
  - checklistId: Long (Checklist identifier for training cleanup context)
  - facilityId: Long (Facility identifier for facility-scoped cleanup)
  - unassignedUserIds: Set<Long> (Set of user identifiers for training cleanup)
  - unassignedUserGroupIds: Set<Long> (Set of user group identifiers for training cleanup)
  - taskIds: Set<Long> (Set of task identifiers for task mapping cleanup)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found for cleanup criteria
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Bulk Training Assignment
```yaml
Usage: trainedUserRepository.saveAll(trainedUsers)
Purpose: "Create trained user assignments in bulk for training management and user assignment operations"

Business Logic Derivation:
  1. Used for bulk training assignment creation during training setup and user assignment operations
  2. Provides efficient bulk training persistence for operations creating multiple training assignments simultaneously
  3. Critical for training management operations requiring bulk training assignment for comprehensive training setup and management
  4. Used in training configuration workflows for bulk training assignment and training management setup operations
  5. Enables efficient bulk training operations with transaction consistency for comprehensive training management

Transaction: Required
Error Handling: DataIntegrityViolationException for bulk constraint violations, training assignment conflicts
```

#### Pattern: Training Validation and Verification Operations
```yaml
Usage: Multiple validation methods for training verification and access control
Purpose: "Validate training assignments for access control and training compliance verification"

Business Logic Derivation:
  1. Training validation enables proper access control through training verification and compliance checking functionality
  2. Training verification supports access control requirements and training functionality for workflow execution and access management
  3. Training validation workflows depend on training verification for proper access control and training compliance management
  4. Access control requires training validation for comprehensive training functionality and compliance control
  5. Training compliance processing requires comprehensive training validation and verification for access control functionality

Transaction: Not Required for validation and verification operations
Error Handling: Validation operation error handling and training verification
```

#### Pattern: Training Discovery and Assignment Management
```yaml
Usage: Training discovery methods for task assignment and training management operations
Purpose: "Discover trained users and user groups for task assignment and training management functionality"

Business Logic Derivation:
  1. Training discovery enables task assignment functionality through trained user identification and assignment management
  2. Assignment management supports task execution requirements and training functionality for task assignment workflows
  3. Training discovery workflows depend on training identification for proper task assignment and training management
  4. Task assignment requires training discovery for comprehensive training functionality and assignment control
  5. Training management processing requires comprehensive training discovery and assignment functionality for task execution

Transaction: Not Required for discovery operations
Error Handling: Discovery operation error handling and training identification verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Task Assignment and Execution Management
```yaml
Usage: Complete trained user lifecycle for task assignment and execution management
Purpose: "Manage trained user assignments for comprehensive task assignment and execution functionality"

Business Logic Derivation:
  1. Trained user assignments provide task assignment functionality through training verification and assignment management
  2. Task assignment lifecycle includes training validation, user discovery, assignment operations, and access control workflows
  3. Training assignment operations require trained user management for task assignment and execution control
  4. Task assignment operations enable comprehensive execution functionality with training verification capabilities and access control
  5. Training lifecycle management supports task execution requirements and assignment functionality for task execution processing

Common Usage Examples:
  - trainedUserRepository.verifyUserIsAssignedToTheChecklist() in JobService for user training verification during job creation
  - trainedUserRepository.findAllUserIdsByChecklistId() and findAllUserGroupIdsByChecklistId() for task assignment discovery
  - trainedUserRepository.findAllUserIdsOfFacility() and findAllUserGroupIdsOfFacility() for facility-based assignment operations
  - Training validation methods for access control and training compliance verification during task assignment workflows
  - Comprehensive task assignment with training verification and assignment management for execution functionality

Transaction: Required for lifecycle operations and training assignment management
Error Handling: Task assignment error handling and training verification validation
```

### Pattern: Training Management and Lifecycle Control
```yaml
Usage: Training assignment lifecycle management for comprehensive training functionality and control
Purpose: "Manage training assignments for comprehensive training lifecycle functionality and management"

Business Logic Derivation:
  1. Training assignment lifecycle management enables comprehensive training functionality through assignment creation and management
  2. Training lifecycle management supports training requirements and assignment functionality for training processing workflows
  3. Training assignment lifecycle includes creation, validation, management operations, and cleanup workflows for training control
  4. Training management operations enable comprehensive training functionality with assignment capabilities and lifecycle control
  5. Training assignment lifecycle control supports training operations and assignment management requirements for training functionality

Common Usage Examples:
  - Training assignment creation and management for training lifecycle and assignment control operations
  - Training cleanup operations for training lifecycle management and assignment cleanup functionality
  - Training retrieval methods for training management and assignment access operations
  - Training validation operations for training compliance and assignment verification functionality
  - Comprehensive training management with lifecycle control and assignment management for training functionality

Transaction: Required for lifecycle operations and training management
Error Handling: Training management error handling and assignment lifecycle validation
```

### Pattern: Facility-Based Training and Assignment Operations
```yaml
Usage: Facility-scoped training management for facility-based assignment and training functionality
Purpose: "Manage facility-based training for comprehensive facility assignment functionality and training control"

Business Logic Derivation:
  1. Facility-based training operations enable facility assignment functionality through facility-scoped training management and assignment control
  2. Facility training management supports facility requirements and assignment functionality for facility processing workflows
  3. Facility-based training operations ensure proper facility assignment through facility training management and assignment control
  4. Facility assignment workflows coordinate facility training with assignment processing for comprehensive facility operations
  5. Facility training processing supports facility requirements and assignment functionality for comprehensive facility management

Common Usage Examples:
  - Facility-scoped user and user group discovery for facility-based assignment operations and training management
  - Facility training retrieval for facility assignment and training access operations
  - Facility-based training cleanup for facility lifecycle management and assignment cleanup functionality
  - Facility training validation for facility assignment and training compliance verification
  - Comprehensive facility training management with assignment functionality and facility control capabilities

Transaction: Required for facility training operations and assignment management
Error Handling: Facility operation error handling and training assignment verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, isUserGroupAddedToChecklist, isUserAddedToChecklist
  - findAllByChecklistIdAndFacilityIdAndUserGroupIdIn, findAllByChecklistIdAndFacilityIdAndUserIdIn
  - findAllByChecklistIdAndUserIdIn, findAllByChecklistIdAndUserGroupIdIn, findAllByChecklistIdAndFacilityId
  - findAllUserIdsByChecklistId, findAllUserGroupIdsByChecklistId, verifyUserIsAssignedToTheChecklist
  - findAllUserIdsOfFacility, findAllUserGroupIdsOfFacility, validateIfUsersAreTrainedUsersForChecklist
  - validateIfUserGroupsAreTrainedUserGroupsForChecklist, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById, deleteByChecklistIdAndFacilityIdAndUserGroupId
  - deleteByChecklistIdAndFacilityIdAndUserId, deleteByChecklistIdAndFacilityIdAndUserIdIn
  - deleteByChecklistIdAndFacilityIdAndUserGroupIdIn, deleteByUserGroupId
  - deleteByChecklistIdAndFacilityIdAndUserGroupId (with task mapping)

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (checklists_id, facilities_id)
    * Foreign key violations (invalid checklists_id, users_id, facilities_id, user_groups_id references)
    * Unique constraint violations for training combinations
    * Training assignment integrity constraint violations
  - EntityNotFoundException: Trained user not found by ID or criteria
  - OptimisticLockException: Concurrent trained user modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or training context
  - ConstraintViolationException: Trained user constraint violations

Validation Rules:
  - checklist: Required, must reference existing checklist, immutable after creation
  - checklistId: Derived from checklist relationship, immutable for training context integrity
  - user: Optional, must reference existing user if provided, immutable for training assignment
  - userId: Derived from user relationship, optional for user-specific training
  - facility: Required, must reference existing facility, immutable for facility-scoped training
  - facilityId: Derived from facility relationship, immutable for facility context integrity
  - userGroup: Optional, must reference existing user group if provided, immutable for group training
  - userGroupId: Derived from user group relationship, optional for group-based training

Business Constraints:
  - Trained users must be unique per checklist, facility, and user/user group combination for proper training integrity
  - Checklist, user, facility, and user group references must be valid for training integrity and training functionality
  - Trained users must support training workflow requirements and assignment functionality
  - Training assignment lifecycle management must maintain referential integrity and training workflow functionality consistency
  - Training assignment management must ensure proper training workflow control and trained user functionality
  - Trained user associations must support training requirements and assignment functionality for training processing
  - Bulk operations must maintain transaction consistency and constraint integrity for training management
  - Training lifecycle management must maintain training functionality and assignment consistency
  - Assignment management must maintain trained user integrity and training workflow requirements
  - Cleanup operations must ensure proper training lifecycle management and trained user assignment control
  - Either user or userGroup must be specified (not both null) for valid training assignment
```

## Trained User Considerations

### Training Assignment Integration
```yaml
User Training: Trained users enable training functionality through user training assignments and training management
Group Training: User group associations enable training functionality with comprehensive group training capabilities
Training Lifecycle: Training assignment lifecycle includes creation, validation, and cleanup operations
Training Management: Comprehensive training management for assignment functionality and training requirements
Assignment Control: Trained user assignment control for training functionality and lifecycle management
```

### Task Assignment and Execution Integration
```yaml
Task Assignment: Trained user validation for task assignment functionality and execution access control
Execution Control: Training verification for task execution access and assignment validation
Assignment Validation: Training assignment validation for task execution and assignment control functionality
Access Control: Training-based access control for task execution and assignment management
Execution Management: Task execution management with training verification and assignment control functionality
```

### Facility and Checklist Integration
```yaml
Facility Training: Facility-scoped training management for facility-based assignment and training functionality
Checklist Training: Checklist-based training assignments for checklist access and training management
Facility Control: Facility training control for facility-based assignment and training management functionality
Checklist Access: Training-based checklist access control for checklist functionality and training verification
Integration Management: Facility and checklist training integration for comprehensive training and assignment functionality
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TrainedUser repository without JPA/Hibernate dependencies, focusing on training management and task assignment functionality patterns.
