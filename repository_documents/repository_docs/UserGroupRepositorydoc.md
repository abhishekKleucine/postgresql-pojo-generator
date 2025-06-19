# IUserGroupRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: UserGroup
- **Primary Purpose**: Manages user group entities for user organization and access control with facility-scoped group management, active status tracking, and user membership management
- **Key Relationships**: Group entity linking to Facility with one-to-one relationship and UserGroupMember associations for comprehensive user group and membership management
- **Performance Characteristics**: Moderate query volume with user group validation, bulk group retrieval, group lifecycle operations, and membership management
- **Business Context**: User management and access control component that provides facility-scoped user groups, group membership management, active status control, and user organization for role-based access control and team management

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | false | null |
| description | description | String | false | null |
| active | active | boolean | false | null |
| facility_id | facility.id / facilityId | Long | true | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @OneToOne | facility | Facility | LAZY | Facility context, cascade = ALL |
| @OneToMany | userGroupMembers | List\<UserGroupMember\> | LAZY | Group member associations |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `findAllById(Iterable<Long> ids)`
- `save(UserGroup entity)`
- `deleteById(Long id)`
- `delete(UserGroup entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<UserGroup> spec)`
- `findAll(Specification<UserGroup> spec, Pageable pageable)`
- `findAll(Specification<UserGroup> spec, Sort sort)`
- `findOne(Specification<UserGroup> spec)`
- `count(Specification<UserGroup> spec)`

### Custom Query Methods (3 methods - ALL methods documented)

- `findAll(Specification specification, Pageable pageable)` (Override)
- `existsByNameAndFacilityIdAndActive(String name, Long facilityId, boolean active, Long id)`
- `existsByIdAndActive(Long id, boolean b)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<UserGroup> findById(Long id)
List<UserGroup> findAll()
List<UserGroup> findAllById(Iterable<Long> ids)
UserGroup save(UserGroup entity)
void deleteById(Long id)
void delete(UserGroup entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<UserGroup> findAll(Specification<UserGroup> spec)
Page<UserGroup> findAll(Specification<UserGroup> spec, Pageable pageable)
List<UserGroup> findAll(Specification<UserGroup> spec, Sort sort)
Optional<UserGroup> findOne(Specification<UserGroup> spec)
long count(Specification<UserGroup> spec)
```

### Custom Query Methods

#### Method: findAll(Specification specification, Pageable pageable) [Override]
```yaml
Signature: Page<UserGroup> findAll(Specification specification, Pageable pageable)
Purpose: "Find user groups with specification filtering and pagination for advanced user group discovery and management"

Business Logic Derivation:
  1. Used in UserGroupService for advanced user group search with facility-scoped filtering and pagination support
  2. Provides complex user group discovery capabilities for user group management and access control operations
  3. Critical for user group management operations requiring multi-criteria filtering and large user group dataset handling
  4. Used in user group listing workflows requiring comprehensive user group search and discovery capabilities
  5. Enables advanced user group discovery with complex filtering for comprehensive user group management operations

SQL Query: |
  SELECT ug.* FROM user_groups ug
  WHERE [dynamic specification criteria]
  ORDER BY [pageable sort criteria]
  LIMIT ? OFFSET ?

Parameters:
  - specification: Specification (Dynamic specification criteria for user group filtering)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<UserGroup> (paginated user groups matching specification criteria)
Transaction: Not Required
Error Handling: Returns empty page if no user groups match specification criteria
```

#### Method: existsByNameAndFacilityIdAndActive(String name, Long facilityId, boolean active, Long id)
```yaml
Signature: boolean existsByNameAndFacilityIdAndActive(String name, Long facilityId, boolean active, Long id)
Purpose: "Check for user group name uniqueness within facility scope for user group validation and conflict prevention"

Business Logic Derivation:
  1. Used in UserGroupService for user group name uniqueness validation during user group creation and update operations
  2. Provides facility-scoped name uniqueness checking for user group validation and conflict prevention
  3. Critical for user group validation operations requiring name uniqueness within facility scope for data integrity
  4. Used in user group management workflows for preventing duplicate user group names within facility scope
  5. Enables user group name validation with facility scoping for comprehensive user group conflict prevention

SQL Query: |
  SELECT EXISTS(
    SELECT 1
    FROM user_groups ug
    WHERE ug.name = ?
      AND ug.facility_id = ?
      AND ug.active = ?
      AND (? is null or ug.id <> ?)
  )

Parameters:
  - name: String (User group name to check for uniqueness)
  - facilityId: Long (Facility ID for facility-scoped uniqueness checking)
  - active: boolean (Active status filter for active user group validation)
  - id: Long (User group ID to exclude from uniqueness check, null for new groups)

Returns: boolean (true if user group name exists with specified criteria)
Transaction: Not Required
Error Handling: Returns false if no conflicting user group found
```

#### Method: existsByIdAndActive(Long id, boolean active)
```yaml
Signature: boolean existsByIdAndActive(Long id, boolean active)
Purpose: "Check if user group exists with specific active status for user group validation and status verification"

Business Logic Derivation:
  1. Used in UserGroupService for user group existence and active status validation during user group operations
  2. Provides user group existence verification with active status checking for user group validation and access control
  3. Critical for user group validation operations requiring active status verification for user group access control
  4. Used in user group management workflows for validating user group existence with active status for operation control
  5. Enables user group status validation with existence checking for comprehensive user group access control

SQL Query: |
  SELECT COUNT(*) > 0 FROM user_groups ug
  WHERE ug.id = ? AND ug.active = ?

Parameters:
  - id: Long (User group identifier to check existence for)
  - active: boolean (Active status to verify for user group validation)

Returns: boolean (true if user group exists with specified active status)
Transaction: Not Required
Error Handling: Returns false if user group not found or status doesn't match
```

### Key Repository Usage Patterns

#### Pattern: save() for User Group Lifecycle Management
```yaml
Usage: userGroupRepository.save(userGroup)
Purpose: "Create new user groups, update group information, and manage group lifecycle with active status control"

Business Logic Derivation:
  1. Used extensively in UserGroupService for user group creation, updates, activation, and deactivation operations
  2. Provides user group persistence with group information, facility associations, and active status management
  3. Critical for user group lifecycle management and access control operations requiring group status control
  4. Used in user group management workflows for group creation, modification, and status management
  5. Enables user group lifecycle management with comprehensive group information and status tracking

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findById() for User Group Context Operations
```yaml
Usage: userGroupRepository.findById(id)
Purpose: "Retrieve user group entity for group-specific operations and validation"

Business Logic Derivation:
  1. Used extensively across services for user group context retrieval during group operations and validation
  2. Critical for user group validation, group information access, and group-specific business logic operations
  3. Used in user group management workflows, assignment operations, and group validation processes
  4. Essential for user group context management and group-based operations across multiple services
  5. Enables group-centric operations with comprehensive user group information and context

Transaction: Not Required
Error Handling: Throws ResourceNotFoundException if user group not found
```

#### Pattern: findAllById() for Bulk User Group Operations
```yaml
Usage: userGroupRepository.findAllById(userGroupIds)
Purpose: "Retrieve multiple user groups for bulk operations and batch processing"

Business Logic Derivation:
  1. Used extensively across services for bulk user group retrieval during assignment and membership operations
  2. Provides efficient bulk user group access for operations requiring multiple group information simultaneously
  3. Critical for bulk operations in ChecklistTrainedUserService, CorrectionService, ParameterExceptionService, and others
  4. Used in assignment workflows, membership management, and bulk group operations requiring multiple group access
  5. Enables efficient bulk user group operations with comprehensive group information for batch processing

Transaction: Not Required
Error Handling: Returns empty list if no user groups found for provided IDs
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: User Group Management and Lifecycle
```yaml
Usage: Complete user group lifecycle management with status control
Purpose: "Manage user group creation, updates, activation, and deactivation for access control and team management"

Business Logic Derivation:
  1. User group lifecycle includes creation with facility scoping, information updates, and active status management
  2. Group activation and deactivation workflows support access control and group lifecycle management
  3. User group validation ensures name uniqueness within facility scope and active status verification
  4. Group management workflows coordinate group information updates with facility associations and member management
  5. User group lifecycle management supports team organization and access control requirements

Common Usage Examples:
  - userGroupRepository.save(userGroup) in UserGroupService for group creation, updates, and status changes
  - userGroupRepository.findById(id) for group context retrieval and validation operations
  - userGroupRepository.existsByNameAndFacilityIdAndActive() for name uniqueness validation
  - userGroupRepository.existsByIdAndActive() for group existence and status verification
  - User group activation and deactivation workflows with audit trail management

Transaction: Required for persistence operations
Error Handling: ResourceNotFoundException, DataIntegrityViolationException, validation errors
```

### Pattern: Bulk User Group Operations for Assignments
```yaml
Usage: Bulk user group retrieval for assignment and membership operations
Purpose: "Efficiently manage multiple user groups for assignment workflows and membership management"

Business Logic Derivation:
  1. Bulk user group operations enable efficient assignment workflows and membership management across services
  2. Multiple services require bulk user group access for checklist training, corrections, exceptions, and job assignments
  3. User group bulk operations support team assignment workflows and group-based access control
  4. Efficient bulk retrieval enables performance optimization for operations involving multiple user groups
  5. Bulk user group management supports comprehensive assignment workflows and team management operations

Common Usage Examples:
  - userGroupRepository.findAllById() in ChecklistTrainedUserService for training assignments
  - userGroupRepository.findAllById() in CorrectionService for corrector and reviewer group assignments
  - userGroupRepository.findAllById() in ParameterExceptionService for exception reviewer assignments
  - userGroupRepository.findAllById() in JobAssignmentService for task execution assignments
  - userGroupRepository.findAllById() in ChecklistService for checklist user group assignments

Transaction: Not Required for retrieval operations
Error Handling: Empty list handling for missing user groups
```

### Pattern: Advanced User Group Discovery and Filtering
```yaml
Usage: Specification-based user group search with facility scoping and pagination
Purpose: "Advanced user group discovery with complex filtering for user group management and access control"

Business Logic Derivation:
  1. Advanced user group search enables complex filtering with facility scoping and active status filtering
  2. Specification-based filtering supports user group discovery with multiple criteria and pagination for large datasets
  3. Facility-scoped user group search ensures proper multi-tenant access control and group isolation
  4. Advanced filtering enables user group management operations requiring comprehensive group discovery
  5. Pagination support enables efficient handling of large user group datasets for management operations

Common Usage Examples:
  - userGroupRepository.findAll(specification, pageable) in UserGroupService for advanced group search
  - Facility-scoped user group filtering for multi-tenant access control and group management
  - Active status filtering for user group discovery and management operations
  - Complex specification-based filtering for user group administration and management workflows
  - Paginated user group listing for efficient management interface and group discovery

Transaction: Not Required
Error Handling: Returns empty page for search criteria with no matching groups
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllById, findAll(Specification), existsByNameAndFacilityIdAndActive
  - existsByIdAndActive, existsById, count, findOne(Specification), count(Specification)

Transactional Methods:
  - save, delete, deleteById

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (name, description, active)
    * Foreign key violations (invalid facility_id references)
    * Unique constraint violations on name within facility scope
    * Cascade operation failures with facility relationship
  - EntityNotFoundException: User group not found by ID or criteria
  - OptimisticLockException: Concurrent user group modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - ResourceNotFoundException: User group not found during operations

Validation Rules:
  - name: Required, string identifier for user group identification within facility scope
  - description: Required, text description for user group purpose and information
  - active: Required, boolean for user group active status and access control
  - facility: Optional, must reference existing facility when specified for facility scoping
  - facilityId: Derived from facility relationship for facility-scoped operations
  - userGroupMembers: Optional, list of UserGroupMember objects for membership management

Business Constraints:
  - User group names must be unique within facility scope for proper group identification
  - Active status must be properly managed for access control and group lifecycle operations
  - Facility associations must be maintained for multi-tenant access control and group scoping
  - User group deletion must handle member relationship cascades for data integrity
  - Group membership management must maintain referential integrity for access control
  - User group status changes must maintain audit trail for compliance and tracking
  - Facility scoping must be enforced for multi-tenant user group isolation and access control
  - Group validation must ensure name uniqueness and active status consistency
  - User group operations must respect facility permissions and access control requirements
  - Group lifecycle management must maintain member associations and access control consistency
```

## User Group-Specific Considerations

### Access Control and Security
```yaml
Facility Scoping: User groups are scoped to facilities for multi-tenant access control
Active Status: Boolean active status controls group accessibility and operations
Name Uniqueness: Group names must be unique within facility scope for proper identification
Member Management: Group membership associations enable role-based access control
Validation: Comprehensive validation ensures group integrity and access control consistency
```

### Team Management and Organization
```yaml
Group Creation: Groups are created with facility associations and descriptive information
Member Associations: Group membership enables team organization and access control
Status Management: Active status controls group availability and member access
Group Discovery: Advanced search capabilities enable group management and administration
Lifecycle Management: Group activation, deactivation, and modification support team organization
```

### Multi-Tenant Support
```yaml
Facility Isolation: Groups are isolated by facility for multi-tenant access control
Scoped Operations: All group operations respect facility scoping for tenant isolation
Permission Management: Group permissions are enforced within facility boundaries
Data Integrity: Facility associations ensure proper multi-tenant data isolation
Access Control: Group-based access control respects facility scoping and permissions
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the UserGroup repository without JPA/Hibernate dependencies, focusing on user group management and access control patterns.
