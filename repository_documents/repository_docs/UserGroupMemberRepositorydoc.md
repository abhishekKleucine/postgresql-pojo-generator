# IUserGroupMemberRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: UserGroupMember (extends UserAuditBase) with UserGroupMemberCompositeKey
- **Primary Purpose**: Manages user group member entities for user group membership management with member assignment, user discovery, dynamic filtering, and user group association functionality
- **Key Relationships**: Links User and UserGroup entities through composite key association for comprehensive membership management and user group operations
- **Performance Characteristics**: High query volume with membership retrieval, bulk membership operations, user discovery, and member lifecycle management
- **Business Context**: User group membership management component that provides user group member assignment, member discovery operations, membership lifecycle management, and user group association functionality for assignment workflows and user group management

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| users_id | userGroupMemberCompositeKey.userId / user.id | Long | false | part of composite key | Foreign key to users, immutable |
| groups_id | userGroupMemberCompositeKey.groupId / userGroup.id | Long | false | part of composite key | Foreign key to user_groups, immutable |
| users_id | usersId | Long | false | derived | Convenience field, immutable |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Composite Key Structure
- **UserGroupMemberCompositeKey**: Composite key containing userId and groupId for unique user-group membership associations

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | user | User | LAZY | Associated user, @MapsId("userId"), immutable |
| @ManyToOne | userGroup | UserGroup | LAZY | Associated user group, @MapsId("groupId"), immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(UserGroupMemberCompositeKey id)`
- `findAll()`
- `save(UserGroupMember entity)`
- `saveAll(Iterable<UserGroupMember> entities)`
- `deleteById(UserGroupMemberCompositeKey id)`
- `delete(UserGroupMember entity)`
- `existsById(UserGroupMemberCompositeKey id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findOne(Specification<UserGroupMember> spec)`
- `findAll(Specification<UserGroupMember> spec)`
- `findAll(Specification<UserGroupMember> spec, Pageable pageable)` *(overridden)*
- `findAll(Specification<UserGroupMember> spec, Sort sort)`
- `count(Specification<UserGroupMember> spec)`
- `exists(Specification<UserGroupMember> spec)`

### Custom Query Methods (7 methods - ALL methods documented)

- `findAll(Specification<UserGroupMember> specification, Pageable pageable)` *(explicit override)*
- `deleteByUserGroupIdAndUserIdIn(Long userGroupId, Set<Long> userIds)`
- `findByUserGroupId(Long userGroupId)`
- `findAllUsersByUserGroupIds(Set<Long> userGroupIds)`
- `findByUserGroupIdIn(List<Long> userGroupIds)`
- `getAllUserIdsOfUserGroup(Long userGroupId)`
- `countByUserGroupId(Long userGroupId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods with composite key
Optional<UserGroupMember> findById(UserGroupMemberCompositeKey id)
List<UserGroupMember> findAll()
UserGroupMember save(UserGroupMember entity)
List<UserGroupMember> saveAll(Iterable<UserGroupMember> entities)
void deleteById(UserGroupMemberCompositeKey id)
void delete(UserGroupMember entity)
boolean existsById(UserGroupMemberCompositeKey id)
long count()
```

### Custom Query Methods

#### Method: findAll(Specification<UserGroupMember> specification, Pageable pageable)
```yaml
Signature: Page<UserGroupMember> findAll(Specification<UserGroupMember> specification, Pageable pageable)
Purpose: "Find user group members with dynamic filtering and pagination for membership management and member reporting"

Business Logic Derivation:
  1. Used extensively in UserGroupService for paginated member retrieval with dynamic filtering during membership management and reporting operations
  2. Provides flexible membership access for user group workflows enabling comprehensive member reporting and filtering functionality
  3. Critical for membership reporting operations requiring dynamic filtering for user group management and member analysis
  4. Used in membership management workflows for accessing member data with complex filtering for membership reporting and analysis operations
  5. Enables membership reporting with dynamic filtering capabilities for comprehensive user group management and member control

SQL Query: |
  SELECT ugm.* FROM user_group_members ugm
  WHERE [dynamic specification conditions]
  ORDER BY [pageable sort configuration]
  LIMIT [page size] OFFSET [page offset]

Parameters:
  - specification: Specification<UserGroupMember> (Dynamic query specification for filtering)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<UserGroupMember> (paginated member results with filtering applied)
Transaction: Not Required
Error Handling: Returns empty page if no members found matching specification criteria
```

#### Method: deleteByUserGroupIdAndUserIdIn(Long userGroupId, Set<Long> userIds)
```yaml
Signature: void deleteByUserGroupIdAndUserIdIn(Long userGroupId, Set<Long> userIds)
Purpose: "Delete user group members by group and users for bulk membership removal and member lifecycle management"

Business Logic Derivation:
  1. Used extensively in UserGroupService for bulk member removal during user group management and membership cleanup operations
  2. Provides efficient bulk membership cleanup for user group workflows enabling comprehensive membership lifecycle management and removal functionality
  3. Critical for membership lifecycle operations requiring bulk member removal for user group management and membership control
  4. Used in user group management workflows for bulk member removal and membership cleanup operations during member lifecycle management
  5. Enables membership lifecycle management with efficient bulk removal for comprehensive user group management and membership control

SQL Query: |
  DELETE FROM UserGroupMember ugm 
  WHERE ugm.userGroupMemberCompositeKey.groupId = ? 
  AND ugm.userGroupMemberCompositeKey.userId IN (?, ?, ?, ...)

Parameters:
  - userGroupId: Long (User group identifier for membership removal context)
  - userIds: Set<Long> (Set of user identifiers for bulk member removal)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found for user group and user combinations
```

#### Method: findByUserGroupId(Long userGroupId)
```yaml
Signature: List<UserGroupMember> findByUserGroupId(Long userGroupId)
Purpose: "Find user group members by group for membership retrieval and user group member management"

Business Logic Derivation:
  1. Used extensively in UserGroupService and UserGroupAuditService for member retrieval during user group management and audit operations
  2. Provides group-scoped member access for user group workflows enabling comprehensive membership management and member functionality
  3. Critical for membership management operations requiring group-based member access for user group management and member tracking
  4. Used in user group management workflows for accessing group members for membership operations and member processing
  5. Enables user group management with group-scoped member access for comprehensive membership tracking and member control

SQL Query: |
  SELECT ugm.* FROM user_group_members ugm
  WHERE ugm.groups_id = ?

Parameters:
  - userGroupId: Long (User group identifier for member retrieval)

Returns: List<UserGroupMember> (all members of the specified user group)
Transaction: Not Required
Error Handling: Returns empty list if no members found for user group
```

#### Method: findAllUsersByUserGroupIds(Set<Long> userGroupIds)
```yaml
Signature: Set<Long> findAllUsersByUserGroupIds(Set<Long> userGroupIds)
Purpose: "Find all user IDs from multiple user groups for member discovery and assignment operations"

Business Logic Derivation:
  1. Used extensively in ParameterVerificationService, JobAssignmentService, and NotificationService for user discovery during assignment and notification operations
  2. Provides efficient user discovery for assignment workflows enabling comprehensive user identification and assignment functionality
  3. Critical for assignment operations requiring user discovery for parameter verification, job assignment, and notification management
  4. Used in assignment workflows for discovering users across multiple user groups for assignment and notification operations
  5. Enables assignment management with efficient user discovery for comprehensive assignment processing and notification control

SQL Query: |
  SELECT DISTINCT ugm.users_id FROM user_group_members ugm
  WHERE ugm.groups_id IN (?, ?, ?, ...)

Parameters:
  - userGroupIds: Set<Long> (Set of user group identifiers for user discovery)

Returns: Set<Long> (distinct set of user IDs from all specified user groups)
Transaction: Not Required
Error Handling: Returns empty set if no users found in specified user groups
```

#### Method: findByUserGroupIdIn(List<Long> userGroupIds)
```yaml
Signature: List<UserGroupMember> findByUserGroupIdIn(List<Long> userGroupIds)
Purpose: "Find user group members by multiple groups for bulk membership retrieval and member management"

Business Logic Derivation:
  1. Used in UserGroupService for bulk member retrieval during user group management and bulk membership operations
  2. Provides efficient bulk membership access for user group workflows enabling comprehensive bulk member management and membership functionality
  3. Critical for bulk membership operations requiring multi-group member access for user group management and bulk member processing
  4. Used in user group management workflows for accessing members across multiple groups for bulk operations and member processing
  5. Enables bulk membership management with multi-group member access for comprehensive user group processing and member control

SQL Query: |
  SELECT ugm FROM UserGroupMember ugm 
  WHERE ugm.userGroup.id IN (?, ?, ?, ...)

Parameters:
  - userGroupIds: List<Long> (List of user group identifiers for bulk member retrieval)

Returns: List<UserGroupMember> (members from all specified user groups)
Transaction: Not Required
Error Handling: Returns empty list if no members found in specified user groups
```

#### Method: getAllUserIdsOfUserGroup(Long userGroupId)
```yaml
Signature: List<Long> getAllUserIdsOfUserGroup(Long userGroupId)
Purpose: "Get all user IDs of user group with user details for member identification and member management"

Business Logic Derivation:
  1. Used extensively in UserGroupService for user identification during user group management and member identification operations
  2. Provides detailed user identification for user group workflows enabling comprehensive member identification and user management functionality
  3. Critical for member identification operations requiring user details for user group management and member processing
  4. Used in user group management workflows for accessing user details for member identification and user management operations
  5. Enables user group management with detailed user identification for comprehensive member processing and user control

SQL Query: |
  SELECT DISTINCT ugm.users_id, u.first_name, u.last_name 
  FROM user_group_members ugm
  INNER JOIN users u ON u.id = ugm.users_id
  WHERE ugm.groups_id = ?
  ORDER BY u.first_name, u.last_name

Parameters:
  - userGroupId: Long (User group identifier for user identification)

Returns: List<Long> (user IDs with name ordering from user group)
Transaction: Not Required
Error Handling: Returns empty list if no users found in user group
```

#### Method: countByUserGroupId(Long userGroupId)
```yaml
Signature: Long countByUserGroupId(Long userGroupId)
Purpose: "Count user group members by group for membership statistics and user group management"

Business Logic Derivation:
  1. Used in UserGroupService for membership statistics during user group management and member counting operations
  2. Provides membership counting for user group workflows enabling comprehensive membership statistics and member tracking functionality
  3. Critical for membership statistics operations requiring member counting for user group management and membership tracking
  4. Used in user group management workflows for accessing membership counts for statistics and member tracking operations
  5. Enables user group management with membership counting for comprehensive member tracking and statistics control

SQL Query: |
  SELECT COUNT(ugm) FROM user_group_members ugm
  WHERE ugm.groups_id = ?

Parameters:
  - userGroupId: Long (User group identifier for member counting)

Returns: Long (count of members in the specified user group)
Transaction: Not Required
Error Handling: Returns 0 if no members found in user group
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Bulk Membership Assignment
```yaml
Usage: userGroupMemberRepository.saveAll(userGroupMembers)
Purpose: "Create user group member assignments in bulk for membership management and user assignment operations"

Business Logic Derivation:
  1. Used extensively in UserGroupService for bulk member assignment during user group membership setup and member management operations
  2. Provides efficient bulk membership persistence for operations creating multiple member assignments simultaneously
  3. Critical for membership management operations requiring bulk member assignment for comprehensive user group setup and management
  4. Used in user group configuration workflows for bulk member assignment and membership management setup operations
  5. Enables efficient bulk membership operations with transaction consistency for comprehensive user group management

Transaction: Required
Error Handling: DataIntegrityViolationException for bulk constraint violations, membership assignment conflicts
```

#### Pattern: Member Discovery and Assignment Operations
```yaml
Usage: Multiple discovery methods for user identification and assignment operations
Purpose: "Discover users and members for assignment workflows and user group management functionality"

Business Logic Derivation:
  1. Member discovery enables assignment functionality through user identification and member discovery for assignment processing
  2. User discovery supports assignment requirements and member functionality for assignment workflows and user group management
  3. Member discovery workflows depend on user identification for proper assignment processing and member management
  4. Assignment processing requires member discovery for comprehensive user group functionality and assignment control
  5. User group management processing requires comprehensive member discovery and assignment functionality for assignment processing

Transaction: Not Required for discovery operations
Error Handling: Discovery operation error handling and member identification verification
```

#### Pattern: Dynamic Membership Reporting and Management
```yaml
Usage: Specification-based member retrieval for reporting and membership management
Purpose: "Retrieve user group members with dynamic filtering for comprehensive membership reporting and management"

Business Logic Derivation:
  1. Dynamic membership filtering enables comprehensive membership reporting through specification-based query building and filtering functionality
  2. Membership reporting supports user group requirements and member functionality for membership management workflows
  3. Member filtering operations depend on specification support for proper membership analysis and reporting management
  4. Reporting workflows require dynamic filtering for comprehensive membership functionality and user group control
  5. Membership analysis processing requires comprehensive filtering and reporting functionality for user group management

Transaction: Not Required for reporting operations
Error Handling: Membership reporting error handling and specification validation
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: User Group Membership Management and Lifecycle Control
```yaml
Usage: Complete user group member lifecycle for membership management and user group operations
Purpose: "Manage user group members for comprehensive membership functionality and user group management"

Business Logic Derivation:
  1. User group members provide membership functionality through member assignment and management for user group operations
  2. Membership lifecycle includes member creation, assignment, discovery operations, and removal workflows for membership control
  3. Member management operations require user group member processing for membership tracking and user group control
  4. User group member operations enable comprehensive membership functionality with assignment capabilities and member tracking
  5. Member lifecycle management supports user group requirements and membership functionality for user group member processing

Common Usage Examples:
  - userGroupMemberRepository.saveAll() in UserGroupService for bulk member assignment during user group setup and management
  - userGroupMemberRepository.findByUserGroupId() for member retrieval during user group management and audit operations
  - userGroupMemberRepository.deleteByUserGroupIdAndUserIdIn() for bulk member removal during membership cleanup and lifecycle management
  - userGroupMemberRepository.findAllUsersByUserGroupIds() for user discovery during assignment and notification operations
  - Comprehensive membership management with member lifecycle control and assignment management for user group functionality

Transaction: Required for membership lifecycle operations and member assignment management
Error Handling: User group member processing error handling and membership lifecycle validation verification
```

### Pattern: Assignment and Notification User Discovery
```yaml
Usage: User group member discovery workflows for assignment and notification functionality
Purpose: "Discover users from user groups for comprehensive assignment and notification functionality"

Business Logic Derivation:
  1. User discovery operations require member identification for comprehensive assignment processing and notification functionality
  2. Assignment user discovery enables assignment workflows with member identification for comprehensive assignment functionality
  3. User group member discovery ensures proper assignment processing through member identification and user discovery control
  4. Discovery workflows coordinate member identification with assignment processing for comprehensive assignment operations
  5. Assignment processing supports discovery requirements and member functionality for comprehensive assignment management

Common Usage Examples:
  - userGroupMemberRepository.findAllUsersByUserGroupIds() in ParameterVerificationService for user discovery during verification assignment
  - userGroupMemberRepository.findAllUsersByUserGroupIds() in JobAssignmentService for user discovery during job assignment operations
  - userGroupMemberRepository.findAllUsersByUserGroupIds() in NotificationService for user discovery during notification delivery
  - User discovery workflows for assignment processing and notification functionality with member identification
  - Comprehensive assignment management with user discovery and member identification for assignment functionality

Transaction: Not Required for discovery and identification operations
Error Handling: User discovery operation error handling and member identification verification
```

### Pattern: Membership Reporting and Audit Integration
```yaml
Usage: User group member reporting workflows with audit integration and membership analysis
Purpose: "Report user group membership with comprehensive audit integration for membership analysis and tracking"

Business Logic Derivation:
  1. Membership reporting operations require audit integration for comprehensive membership analysis and audit tracking functionality
  2. Audit membership reporting enables audit workflows with member reporting for comprehensive audit functionality
  3. User group member reporting ensures proper audit integration through member reporting and audit control
  4. Reporting workflows coordinate member analysis with audit processing for comprehensive membership audit operations
  5. Audit processing supports reporting requirements and member functionality for comprehensive membership audit reporting

Common Usage Examples:
  - userGroupMemberRepository.findByUserGroupId() in UserGroupAuditService for member retrieval during audit tracking and analysis
  - userGroupMemberRepository.getAllUserIdsOfUserGroup() for detailed member identification during user group management and reporting
  - userGroupMemberRepository.countByUserGroupId() for membership statistics during user group management and member tracking
  - Specification-based member filtering for membership reporting and audit analysis functionality
  - Comprehensive membership reporting with audit integration and member analysis for user group audit functionality

Transaction: Not Required for reporting and audit operations
Error Handling: Membership reporting operation error handling and audit integration verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findOne, findAll (with Specification), findAll (with Sort)
  - findByUserGroupId, findAllUsersByUserGroupIds, findByUserGroupIdIn
  - getAllUserIdsOfUserGroup, countByUserGroupId, count, count (with Specification)
  - exists, existsById, exists (with Specification)

Transactional Methods:
  - save, saveAll, delete, deleteById, deleteByUserGroupIdAndUserIdIn

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Composite key constraint violations (duplicate user-group combinations)
    * NOT NULL constraint violations (users_id, groups_id)
    * Foreign key violations (invalid users_id, groups_id references)
    * Unique constraint violations on composite key
  - EntityNotFoundException: User group member not found by composite key or criteria
  - OptimisticLockException: Concurrent user group member modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or member context
  - ConstraintViolationException: User group member constraint violations

Validation Rules:
  - userGroupMemberCompositeKey: Required, composite key containing userId and groupId for unique user-group associations
  - user: Required, must reference existing user, immutable after creation, @MapsId("userId")
  - userGroup: Required, must reference existing user group, immutable after creation, @MapsId("groupId")
  - usersId: Derived from composite key and user relationship, convenience field for queries

Business Constraints:
  - User group members must be unique per user and group combination for proper membership integrity
  - User and user group references must be valid for membership integrity and user group functionality
  - User group members must support membership workflow requirements and assignment functionality
  - Membership lifecycle management must maintain referential integrity and user group functionality consistency
  - Member assignment management must ensure proper membership workflow control and user group member functionality
  - User group member associations must support assignment requirements and membership functionality for user group processing
  - Bulk operations must maintain transaction consistency and constraint integrity for membership management
  - Membership lifecycle management must maintain user group functionality and member assignment consistency
  - Assignment management must maintain user group member integrity and membership workflow requirements
  - Discovery operations must ensure proper membership identification and user group member access control
```

## User Group Member Considerations

### Membership Management Integration
```yaml
Member Assignment: User group members enable membership functionality through member assignment and membership management
Group Membership: Member associations enable user group functionality with comprehensive membership capabilities
Membership Lifecycle: Member assignment lifecycle includes creation, discovery, and removal operations
Membership Management: Comprehensive membership management for assignment functionality and user group requirements
Assignment Control: User group member assignment control for membership functionality and lifecycle management
```

### Assignment and Discovery Integration
```yaml
User Discovery: Member-based user discovery for assignment functionality and user identification
Assignment Processing: Membership discovery for assignment workflows and user group assignment functionality
Discovery Operations: User group member discovery for assignment processing and user identification functionality
Assignment Control: Member-based assignment control for user group functionality and assignment management
Discovery Management: Comprehensive user discovery management through membership identification and assignment functionality
```

### Audit and Reporting Integration
```yaml
Membership Reporting: User group member reporting for audit functionality and membership analysis
Audit Integration: Membership audit integration with member reporting and audit functionality
Reporting Control: Comprehensive membership reporting control for audit functionality and member management
Audit Operations: Membership audit operations for user group member lifecycle and audit functionality
Management Integration: Audit management for membership workflow and user group member functionality in audit processing
```

## Composite Key Usage Examples

### Common Membership Operations
```java
// Create composite key for membership operations
UserGroupMemberCompositeKey compositeKey = new UserGroupMemberCompositeKey(userId, groupId);

// Find specific membership
Optional<UserGroupMember> member = userGroupMemberRepository.findById(compositeKey);

// Create new membership
UserGroupMember newMember = new UserGroupMember(user, userGroup);
userGroupMemberRepository.save(newMember);

// Check membership existence
boolean exists = userGroupMemberRepository.existsById(compositeKey);

// Delete specific membership
userGroupMemberRepository.deleteById(compositeKey);
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the UserGroupMember repository without JPA/Hibernate dependencies, focusing on user group membership management and composite key handling patterns.
