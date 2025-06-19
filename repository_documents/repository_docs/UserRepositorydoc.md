# IUserRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: User 
- **Primary Purpose**: Manages user entities for authentication, authorization, and user management with role-based access control and correction tracking functionality
- **Key Relationships**: Complex relationships with Role, Correction, UserGroup, and other user-related entities
- **Performance Characteristics**: Medium query volume with role validation, bulk user retrieval, and correction tracking operations
- **Business Context**: User management component providing authentication support, role-based authorization, user lookup, and audit trail functionality for workflow user management
- **Advanced Features**: Extends JpaSpecificationExecutor for dynamic query capabilities

## Entity Overview
- **Core Entity**: Central user management entity
- **Role Integration**: Complex role-based access control support
- **Correction Tracking**: User correction audit trail support
- **Archive Support**: Soft delete functionality with archived flag
- **Bulk Operations**: Optimized bulk user retrieval capabilities

## Available Repository Methods

### Standard CRUD Methods (JpaRepository + JpaSpecificationExecutor)
- `findById(Long id)`
- `findAll()`
- `save(User entity)`
- `saveAll(Iterable<User> entities)`
- `deleteById(Long id)`
- `delete(User entity)`
- `existsById(Long id)`
- `count()`
- **Dynamic Queries**: `findAll(Specification<User> spec)`, `findOne(Specification<User> spec)`, etc.

### Custom Query Methods (5 methods - ALL methods documented)

**User Audit and Correction Methods (1 method):**
- `getUserWhoCorrectedByCorrectionId(Long correctionId)`

**Role and Authorization Methods (2 methods):**
- `existsByRoles(Set<Long> userIds, List<String> userGroupRoles)`
- `getUserRoles(String userId)`

**Bulk User Retrieval Methods (2 methods):**
- `findAllByIdIn(Set<Long> ids)`
- `findAllByIdInAndArchivedFalse(Set<Long> ids)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<User> findById(Long id)
List<User> findAll()
User save(User entity)
List<User> saveAll(Iterable<User> entities)
void deleteById(Long id)
void delete(User entity)
boolean existsById(Long id)
long count()

// JpaSpecificationExecutor methods for dynamic queries
Optional<User> findOne(Specification<User> spec)
List<User> findAll(Specification<User> spec)
Page<User> findAll(Specification<User> spec, Pageable pageable)
List<User> findAll(Specification<User> spec, Sort sort)
long count(Specification<User> spec)
```

### User Audit and Correction Methods

#### Method: getUserWhoCorrectedByCorrectionId(Long correctionId)
```yaml
Signature: User getUserWhoCorrectedByCorrectionId(@Param("correctionId") Long correctionId)
Purpose: "Get user who performed correction for audit trail and correction tracking"

Business Logic Derivation:
  1. Used for correction audit trail tracking during correction management and user accountability operations
  2. Provides correction user tracking for audit workflows enabling comprehensive correction management and user functionality
  3. Critical for audit operations requiring user correction tracking for audit management and correction control
  4. Used in audit workflows for accessing correction users for tracking operations and audit processing
  5. Enables audit management with correction user tracking for comprehensive audit processing and correction control

Native SQL Query: |
  SELECT u.* FROM users u
  INNER JOIN corrections c ON u.id = c.corrected_by
  WHERE c.id = :correctionId

Parameters:
  - correctionId: Long (Correction identifier for user retrieval)

Returns: User (user who performed the correction)
Transaction: Required (@Transactional annotation at repository level)
Error Handling: Returns null if no user found for correction
```

### Role and Authorization Methods

#### Method: existsByRoles(Set<Long> userIds, List<String> userGroupRoles)
```yaml
Signature: boolean existsByRoles(@Param("userIds") Set<Long> userIds, @Param("roles") List<String> userGroupRoles)
Purpose: "Check if users exist with specific roles for authorization validation and role verification"

Business Logic Derivation:
  1. Used for role-based authorization validation during access control and permission checking operations
  2. Provides role validation for authorization workflows enabling comprehensive role management and user functionality
  3. Critical for authorization operations requiring role validation for access management and role control
  4. Used in authorization workflows for role checking and permission validation operations
  5. Enables access control with role validation for comprehensive authorization processing and permission control

Native SQL Query: |
  SELECT EXISTS(
    SELECT 1 FROM users u
    INNER JOIN user_roles_mapping urm ON u.id = urm.users_id
    INNER JOIN roles r ON urm.roles_id = r.id
    WHERE u.id IN (:userIds) AND r.name IN (:roles)
  )

Parameters:
  - userIds: Set<Long> (User identifiers for role checking)
  - userGroupRoles: List<String> (Role names for validation)

Returns: boolean (true if users exist with specified roles, false otherwise)
Transaction: Required (@Transactional annotation at repository level)
Error Handling: Returns false if no users found with specified roles
```

#### Method: getUserRoles(String userId)
```yaml
Signature: List<RoleBasicView> getUserRoles(@Param("userId") String userId)
Purpose: "Get user roles for authorization and role display functionality"

Business Logic Derivation:
  1. Used for user role retrieval during authorization and role display operations
  2. Provides role access for authorization workflows enabling comprehensive role management and user functionality
  3. Critical for authorization operations requiring role access for role management and authorization control
  4. Used in authorization workflows for accessing user roles for display operations and authorization processing
  5. Enables authorization management with role access for comprehensive authorization processing and role control

Native SQL Query: |
  SELECT r.id, r.name, r.description 
  FROM roles r
  INNER JOIN user_roles_mapping urm ON r.id = urm.roles_id
  WHERE urm.users_id = :userId

Parameters:
  - userId: String (User identifier for role retrieval)

Returns: List<RoleBasicView> (user roles in basic view format)
Transaction: Required (@Transactional annotation at repository level)
Error Handling: Returns empty list if no roles found for user
```

### Bulk User Retrieval Methods

#### Method: findAllByIdIn(Set<Long> ids)
```yaml
Signature: List<User> findAllByIdIn(@Param("ids") Set<Long> ids)
Purpose: "Retrieve multiple users by IDs for bulk user operations and user lookup"

Business Logic Derivation:
  1. Used extensively for bulk user retrieval during user management and bulk operations
  2. Provides efficient bulk user access for management workflows enabling comprehensive user management and bulk functionality
  3. Critical for management operations requiring bulk user access for user management and bulk control
  4. Used in management workflows for accessing multiple users for bulk operations and user processing
  5. Enables user management with efficient bulk access for comprehensive user processing and bulk control

SQL Query: |
  SELECT u FROM User u WHERE u.id IN (:ids)

Parameters:
  - ids: Set<Long> (User identifiers for bulk retrieval)

Returns: List<User> (users for specified IDs)
Transaction: Required (@Transactional annotation at repository level)
Error Handling: Returns empty list if no users found for specified IDs
```

#### Method: findAllByIdInAndArchivedFalse(Set<Long> ids)
```yaml
Signature: List<User> findAllByIdInAndArchivedFalse(Set<Long> ids)
Purpose: "Retrieve multiple active users by IDs excluding archived users for active user operations"

Business Logic Derivation:
  1. Used for active user retrieval during user management and active user operations
  2. Provides active user access for management workflows enabling comprehensive active user management and user functionality
  3. Critical for management operations requiring active user access for user management and active control
  4. Used in management workflows for accessing active users for operations and user processing
  5. Enables user management with active user access for comprehensive user processing and active control

SQL Query: |
  SELECT u FROM User u WHERE u.id IN (:ids) AND u.archived = false

Parameters:
  - ids: Set<Long> (User identifiers for active user retrieval)

Returns: List<User> (active users for specified IDs)
Transaction: Required (@Transactional annotation at repository level)
Error Handling: Returns empty list if no active users found for specified IDs
```

### Key Repository Usage Patterns

#### Pattern: JpaSpecificationExecutor for Dynamic Queries
```yaml
Usage: Dynamic user queries with complex criteria and filtering
Purpose: "Support complex user searches and filtering with dynamic query construction"

Business Logic Derivation:
  1. Enables complex user search functionality with dynamic criteria construction for advanced user management operations
  2. Provides flexible query capabilities for user workflows enabling comprehensive search functionality and user management
  3. Critical for advanced search operations requiring dynamic query construction for user management and search control
  4. Used in search workflows for dynamic user queries and advanced search operations
  5. Enables advanced user management with dynamic search capabilities for comprehensive user processing and search control

Common Usage Examples:
  - User search with multiple criteria (role, facility, status)
  - Filtered user listings with pagination and sorting
  - Complex user queries with joins and conditions
  - Dynamic user reporting with flexible criteria

Transaction: Required for query operations
Error Handling: Specification validation and query construction error handling
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: User Authentication and Authorization
```yaml
Usage: User authentication and role-based authorization for access control functionality
Purpose: "Manage user authentication for comprehensive authorization functionality and access processing"

Business Logic Derivation:
  1. User authentication management provides authorization functionality through user validation, role checking, and access control operations
  2. Authorization lifecycle includes user authentication, role validation, and access management for authorization control
  3. Authorization management operations require access processing for authorization lifecycle and user control
  4. Access operations enable comprehensive authorization functionality with user capabilities and management
  5. Authorization lifecycle management supports access requirements and functionality for user authorization processing

Common Usage Examples:
  - userRepository.getUserRoles() for user role retrieval during authorization
  - userRepository.existsByRoles() for role validation during access control
  - Dynamic user queries for authentication validation
  - User lookup for session management and authentication

Transaction: Required (repository-level @Transactional annotation)
Error Handling: User authentication error handling and authorization validation verification
```

### Pattern: User Management and Bulk Operations
```yaml
Usage: User management and bulk operations for user administration and management functionality
Purpose: "Manage users for comprehensive administration functionality and user processing"

Business Logic Derivation:
  1. User management operations require comprehensive user access for management-level user administration and user functionality
  2. Management supports administration requirements and functionality for user processing workflows
  3. Management-level user operations ensure proper administration functionality through user management and administration control
  4. User workflows coordinate management with administration processing for comprehensive user operations
  5. User management supports administration requirements and functionality for comprehensive user administration management

Common Usage Examples:
  - userRepository.findAllByIdIn() for bulk user retrieval operations
  - userRepository.findAllByIdInAndArchivedFalse() for active user operations
  - User bulk operations for administration and management
  - User lifecycle management with archive support

Transaction: Required for management operations
Error Handling: User management error handling and administration validation verification
```

### Pattern: Audit Trail and Correction Tracking
```yaml
Usage: Audit trail and correction tracking for user accountability and audit functionality
Purpose: "Track user actions for comprehensive audit functionality and accountability processing"

Business Logic Derivation:
  1. User audit tracking operations require comprehensive user access for audit-level accountability management and tracking functionality
  2. Audit management supports accountability requirements and functionality for tracking processing workflows
  3. Audit-level tracking operations ensure proper accountability functionality through user management and audit control
  4. Audit workflows coordinate tracking management with accountability processing for comprehensive audit operations
  5. Tracking management supports accountability requirements and functionality for comprehensive user audit management

Common Usage Examples:
  - userRepository.getUserWhoCorrectedByCorrectionId() for correction audit tracking
  - User action tracking for audit trail management
  - Correction accountability with user identification
  - Audit trail construction with user information

Transaction: Required for audit operations
Error Handling: Audit tracking error handling and accountability validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllByIdIn, findAllByIdInAndArchivedFalse, getUserWhoCorrectedByCorrectionId, existsByRoles, getUserRoles, existsById, count, JpaSpecificationExecutor methods

Transactional Methods:
  - save, saveAll, delete, deleteById

Repository-Level Transaction: @Transactional(rollbackFor = Exception.class)
Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations
    * Unique constraint violations (username, email)
    * User integrity constraint violations
  - EntityNotFoundException: User not found by ID or criteria
  - OptimisticLockException: Concurrent user modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or user context
  - ConstraintViolationException: User constraint violations

Validation Rules:
  - User must have valid authentication credentials
  - Role assignments must reference valid roles
  - User identifiers must be valid for lookup operations
  - Archive status must be properly managed for soft delete functionality
  - Bulk operations must handle empty or invalid ID sets

Business Constraints:
  - User authentication must support authorization workflows and functionality
  - Role-based access control must maintain security and authorization integrity
  - User lifecycle management must maintain referential integrity and user functionality consistency
  - User associations must support workflow requirements and functionality for user processing
  - User operations must maintain transaction consistency and constraint integrity for user management
  - Audit trail functionality must maintain user accountability and tracking consistency
  - Archive functionality must maintain user lifecycle and soft delete requirements
  - Bulk operations must maintain performance and consistency for user management
  - Dynamic queries must support complex search requirements and functionality
  - Authorization validation must maintain security integrity and access control
```

## User Management Considerations

### Authentication and Authorization Integration
```yaml
Authentication: Users enable authentication functionality through credential management and authentication validation
Authorization: User authorization enables workflow functionality with comprehensive role-based access control
Security: User security includes authentication validation, role management, and access control for security functionality
Access Control: User access control for workflow functionality and lifecycle management in user processing
Identity Management: User identity management with authentication coordination and comprehensive identity functionality for workflow processing
```

### Role-Based Access Control Integration
```yaml
Role Management: Users enable role functionality through role assignment and authorization management
Permission Control: User permission control with role coordination and comprehensive permission functionality
Access Validation: User access validation with role management and comprehensive validation functionality
Authorization Control: User authorization control with role management and comprehensive authorization functionality
Security Integration: User security integration with roles and comprehensive security functionality for workflow processing
```

### Audit Trail and Accountability Integration
```yaml
Audit Tracking: Users enable audit functionality through action tracking and accountability management
Correction Tracking: User correction tracking with audit coordination and comprehensive tracking functionality
Accountability: User accountability with audit management and comprehensive accountability functionality
Action Tracking: User action tracking with audit coordination and comprehensive tracking functionality
Compliance: User compliance tracking with audit management and comprehensive compliance functionality for workflow processing
```

### Dynamic Query and Search Integration
```yaml
Search Functionality: Users enable search functionality through JpaSpecificationExecutor and dynamic query construction
Filtering: User filtering with specification support and comprehensive filtering functionality
Reporting: User reporting with dynamic queries and comprehensive reporting functionality
Data Discovery: User data discovery with search coordination and comprehensive discovery functionality
Query Optimization: User query optimization with specification support and comprehensive optimization functionality for workflow processing
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the User repository without JPA/Hibernate dependencies, focusing on authentication, authorization, audit tracking, and dynamic query capabilities.
