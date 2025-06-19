# IChecklistCollaboratorMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ChecklistCollaboratorMapping
- **Primary Purpose**: Manages collaborator assignments and workflow states for checklist review and approval processes
- **Key Relationships**: Links users to checklists with specific roles, phases, and approval states
- **Performance Characteristics**: Medium to high query volume with complex filtering and state management operations
- **Business Context**: Core collaboration engine that orchestrates multi-phase review workflows with authors, reviewers, and sign-off users

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| checklists_id | checklist.id | Long | false | null |
| type | type | Type.Collaborator | false | null |
| users_id | user.id | Long | false | null |
| state | state | State.ChecklistCollaborator | false | null |
| phase_type | phaseType | State.ChecklistCollaboratorPhaseType | false | null |
| phase | phase | Integer | false | 1 |
| order_tree | orderTree | Integer | false | 1 |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | checklist | Checklist | EAGER | Associated checklist, updatable = false |
| @ManyToOne | user | User | EAGER | Collaborator user, updatable = false, cascade = DETACH |
| @OneToMany | comments | List\<ChecklistCollaboratorComments\> | LAZY | Collaborator comments, cascade = ALL, ordered by createdAt |
| @ManyToOne | createdBy | User | LAZY | User who created the mapping |
| @ManyToOne | modifiedBy | User | LAZY | User who last modified the mapping |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(ChecklistCollaboratorMapping entity)`
- `deleteById(Long id)`
- `delete(ChecklistCollaboratorMapping entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods
- `deleteAll(Long checklistId, Integer phase, Set<Long> userIds)`
- `findAllByChecklistIdAndPhaseType(Long checklistId, State.ChecklistCollaboratorPhaseType phaseType)`
- `findAllByChecklistIdAndType(Long checklistId, String type)`
- `findAllByTypeOrderByOrderTreeAndModifiedAt(Long checklistId, String type)`
- `findAllByChecklistIdAndTypeIn(Long checklistId, List<String> types)`
- `findByChecklistAndPhaseTypeAndPhaseAndUser(Checklist checklist, State.ChecklistCollaboratorPhaseType phaseType, Integer phase, User user)`
- `findFirstByChecklistAndPhaseTypeAndUserAndStateOrderByOrderTreeAsc(Checklist checklist, State.ChecklistCollaboratorPhaseType phaseType, User user, State.ChecklistCollaborator State)`
- `isCollaboratorMappingExistsByChecklistAndUserIdAndCollaboratorType(Long checklistId, Long userId, Set<Type.Collaborator> types)`
- `deleteAuthors(Long checklistId, Set<Long> userIds)`
- `findAllByChecklistId(Long checklistId)`
- `deleteAllByChecklistIdAndTypeNot(Long checklist_id, Type.Collaborator type)`
- `updatePrimaryAuthor(Long userId, Long checklistId)`
- `findByChecklistAndPhaseTypeAndTypeAndPhase(Long checklistId, String phaseType, String type, Integer phase)`

## Method Documentation

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<ChecklistCollaboratorMapping> findById(Long id)
List<ChecklistCollaboratorMapping> findAll()
ChecklistCollaboratorMapping save(ChecklistCollaboratorMapping entity)
void deleteById(Long id)
void delete(ChecklistCollaboratorMapping entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: deleteAll
```yaml
Signature: void deleteAll(Long checklistId, Integer phase, Set<Long> userIds)
Purpose: "Remove specific reviewers from a checklist phase during collaboration management"

Business Logic Derivation:
  1. Used in ChecklistCollaboratorService for removing reviewers from review cycles
  2. Targets specific users in specific review phases for precise collaboration control
  3. Maintains review workflow integrity by removing only intended collaborators
  4. Uses native query for efficient bulk deletion with complex filtering criteria
  5. Supports dynamic reviewer management during active review processes

SQL Query: |
  DELETE FROM checklist_collaborator_mapping 
  WHERE checklists_id = ? AND phase = ? AND users_id IN (?)

  BUSINESS LOGIC:
  1. Target checklist_collaborator_mapping table for precise removal
  2. Filter by checklist ID to scope operation to specific checklist
  3. Filter by phase to target specific review cycle iteration
  4. Filter by user IDs set to remove only specified reviewers
  5. Preserves other collaborators and other phases during selective removal
  6. Enables dynamic reviewer management during active review processes

Parameters:
  - checklistId: Long (Checklist to remove reviewers from)
  - phase: Integer (Review phase/cycle to target)
  - userIds: Set<Long> (Specific users to remove from collaboration)

Returns: void
Transaction: Required (uses @Transactional with rollbackFor = Exception.class)
Error Handling: DataIntegrityViolationException for constraint violations, rollback on any exception
```

#### Method: findAllByChecklistIdAndPhaseType
```yaml
Signature: List<ChecklistCollaboratorView> findAllByChecklistIdAndPhaseType(Long checklistId, State.ChecklistCollaboratorPhaseType phaseType)
Purpose: "Get all collaborators for specific checklist and collaboration phase type"

Business Logic Derivation:
  1. Used to retrieve collaborators filtered by phase type (REVIEW, SIGN_OFF, etc.)
  2. Supports phase-specific collaboration management and workflow orchestration
  3. Returns projection views optimized for collaboration UI displays
  4. Enables phase-aware collaboration queries for workflow state management
  5. Critical for displaying appropriate collaborators based on current workflow phase

SQL Query: |
  SELECT u.id, u.first_name as firstName, u.last_name as lastName, u.employee_id as employeeId, 
         crm.state, crm.order_tree as orderTree, crm.type, crm.modified_at as modifiedAt 
  FROM checklist_collaborator_mapping crm 
  JOIN users u ON crm.users_id = u.id 
  WHERE crm.checklists_id = ? 
    AND crm.phase_type = ? 
    AND crm.phase = (SELECT MAX(phase) FROM checklists WHERE id = ?)

  BUSINESS LOGIC:
  1. Join collaborator mappings with users to get complete collaborator information
  2. Filter by checklist ID and specific phase type (REVIEW, SIGN_OFF, etc.)
  3. Use subquery to get the maximum phase for the checklist (latest review cycle)
  4. Return projection view with user details and collaboration state information
  5. Enables phase-specific collaboration management and UI display
  6. Critical for showing current active collaborators in specific workflow phases

Parameters:
  - checklistId: Long (Checklist identifier)
  - phaseType: State.ChecklistCollaboratorPhaseType (Phase type filter)

Returns: List<ChecklistCollaboratorView> (projection views of collaborators)
Transaction: Not Required
Error Handling: Returns empty list if no collaborators found for phase
```

#### Method: findByChecklistAndPhaseTypeAndPhaseAndUser
```yaml
Signature: Optional<ChecklistCollaboratorMapping> findByChecklistAndPhaseTypeAndPhaseAndUser(Checklist checklist, State.ChecklistCollaboratorPhaseType phaseType, Integer phase, User user)
Purpose: "Find specific user's collaboration mapping for exact phase context"

Business Logic Derivation:
  1. Used extensively in ChecklistCollaboratorService for precise user-phase lookups
  2. Critical for state management and permission validation in collaboration workflows
  3. Enables exact matching of user, checklist, phase type, and phase number
  4. Used before state transitions to verify user's current collaboration context
  5. Essential for maintaining collaboration workflow integrity and user permissions

SQL Query: |
  SELECT ccm.* FROM checklist_collaborator_mapping ccm 
  WHERE ccm.checklists_id = ? AND ccm.phase_type = ? AND ccm.phase = ? AND ccm.users_id = ?

Parameters:
  - checklist: Checklist (Checklist entity for collaboration)
  - phaseType: State.ChecklistCollaboratorPhaseType (Phase type context)
  - phase: Integer (Specific phase number)
  - user: User (User entity to find mapping for)

Returns: Optional<ChecklistCollaboratorMapping> (exact mapping or empty)
Transaction: Not Required
Error Handling: Returns empty Optional if no matching mapping found
```

#### Method: isCollaboratorMappingExistsByChecklistAndUserIdAndCollaboratorType
```yaml
Signature: boolean isCollaboratorMappingExistsByChecklistAndUserIdAndCollaboratorType(Long checklistId, Long userId, Set<Type.Collaborator> types)
Purpose: "Check if user has any of the specified collaborator types for a checklist"

Business Logic Derivation:
  1. Used in ChecklistService for authorization validation before operations
  2. Validates if user is authorized as author, reviewer, or sign-off user
  3. Supports type-based permission checking with multiple collaborator types
  4. Critical for security and access control in checklist operations
  5. Enables efficient permission validation without loading full entities

SQL Query: |
  SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END 
  FROM checklist_collaborator_mapping c 
  WHERE c.checklists_id = ? AND c.users_id = ? AND c.type IN (?)

  BUSINESS LOGIC:
  1. Query checklist_collaborator_mapping table for user-checklist-type combinations
  2. Check if user has any of the specified collaborator types for the checklist
  3. Use COUNT to determine existence and return boolean result
  4. Enables efficient permission checking without loading full entity data
  5. Supports authorization validation with multiple collaborator type checking
  6. Critical for security and access control in checklist operations

Parameters:
  - checklistId: Long (Checklist to check permissions for)
  - userId: Long (User to validate permissions for)
  - types: Set<Type.Collaborator> (Collaborator types to check)

Returns: boolean (true if user has any of the specified collaborator types)
Transaction: Not Required
Error Handling: Returns false if no matching collaborator types found
```

#### Method: findAllByChecklistIdAndType
```yaml
Signature: List<ChecklistCollaboratorView> findAllByChecklistIdAndType(Long checklistId, String type)
Purpose: "Get all collaborators of a specific type for a checklist"

Business Logic Derivation:
  1. Used to retrieve collaborators filtered by specific type (AUTHOR, REVIEWER, SIGN_OFF_USER)
  2. Supports type-specific collaboration management and role-based queries
  3. Returns projection views optimized for collaboration UI displays
  4. Enables role-aware collaboration queries for permission and workflow management
  5. Critical for displaying collaborators grouped by their roles in the workflow

SQL Query: |
  SELECT u.id, u.first_name as firstName, u.last_name as lastName, u.email as email, u.employee_id as employeeId, 
         crm.state, crm.order_tree as orderTree, crm.type, crm.modified_at as modifiedAt
  FROM checklist_collaborator_mapping crm 
  JOIN users u ON crm.users_id = u.id 
  WHERE crm.checklists_id = ? AND crm.type = ? 
    AND crm.phase = (SELECT MAX(crmi.phase) FROM checklist_collaborator_mapping crmi 
                     WHERE crmi.checklists_id = ? AND crmi.type = ?)

  BUSINESS LOGIC:
  1. Join collaborator mappings with users to get complete user information
  2. Filter by checklist ID and specific collaborator type
  3. Use subquery to get the maximum phase for the checklist and type combination
  4. Return projection view with user details and collaboration state
  5. Ensures only latest phase collaborators are returned for each type
  6. Essential for role-based collaboration management and UI display

Parameters:
  - checklistId: Long (Checklist identifier)
  - type: String (Collaborator type filter)

Returns: List<ChecklistCollaboratorView> (projection views of collaborators for the type)
Transaction: Not Required
Error Handling: Returns empty list if no collaborators found for the type
```

#### Method: findAllByTypeOrderByOrderTreeAndModifiedAt
```yaml
Signature: List<ChecklistCollaboratorView> findAllByTypeOrderByOrderTreeAndModifiedAt(Long checklistId, String type)
Purpose: "Get all collaborators of a specific type ordered by position and modification time"

Business Logic Derivation:
  1. Used to retrieve collaborators with specific ordering for workflow display
  2. Orders results by order_tree (position) and then by modification time for consistency
  3. Critical for displaying collaborators in their intended sequence for reviews
  4. Supports ordered collaboration workflows where sequence matters
  5. Used in UI components that need to show collaborators in specific order

SQL Query: |
  SELECT u.id, u.first_name as firstName, u.last_name as lastName, u.email as email, u.employee_id as employeeId, 
         crm.state, crm.order_tree as orderTree, crm.type, crm.modified_at as modifiedAt
  FROM checklist_collaborator_mapping crm 
  JOIN users u ON crm.users_id = u.id 
  WHERE crm.checklists_id = ? AND crm.type = ? 
    AND crm.phase = (SELECT MAX(phase) FROM checklist_collaborator_mapping 
                     WHERE checklists_id = ? AND type = ?)
  ORDER BY crm.order_tree, crm.modified_at DESC

  BUSINESS LOGIC:
  1. Join collaborator mappings with users for complete information
  2. Filter by checklist ID and collaborator type
  3. Use subquery to ensure only latest phase collaborators are selected
  4. Order by order_tree first (position sequence) then by modification time
  5. Ensures consistent ordering for collaboration workflow display
  6. Critical for maintaining proper review sequence and user experience

Parameters:
  - checklistId: Long (Checklist identifier)
  - type: String (Collaborator type to order)

Returns: List<ChecklistCollaboratorView> (ordered projection views of collaborators)
Transaction: Not Required
Error Handling: Returns empty list if no collaborators found
```

#### Method: findAllByChecklistIdAndTypeIn
```yaml
Signature: List<ChecklistCollaboratorView> findAllByChecklistIdAndTypeIn(Long checklistId, List<String> types)
Purpose: "Get all collaborators matching any of the specified types for a checklist"

Business Logic Derivation:
  1. Used to retrieve collaborators with multiple type filtering (e.g., AUTHORS and REVIEWERS)
  2. Supports multi-role collaboration queries for comprehensive collaboration management
  3. Returns projection views for efficient UI display across multiple collaborator types
  4. Enables bulk collaboration operations across different collaborator roles
  5. Critical for displaying all relevant collaborators regardless of specific type

SQL Query: |
  SELECT u.id, u.first_name as firstName, u.last_name as lastName, u.email as email, u.employee_id as employeeId, 
         crm.state, crm.order_tree as orderTree, crm.type
  FROM checklist_collaborator_mapping crm 
  JOIN users u ON crm.users_id = u.id 
  WHERE crm.checklists_id = ? AND crm.type IN (?) 
    AND crm.phase = (SELECT MAX(phase) FROM checklist_collaborator_mapping 
                     WHERE checklists_id = ? AND type IN (?))

  BUSINESS LOGIC:
  1. Join collaborator mappings with users for complete information
  2. Filter by checklist ID and multiple collaborator types using IN clause
  3. Use subquery to ensure only latest phase collaborators are selected
  4. Return projection view with essential collaboration information
  5. Enables multi-type collaboration queries for comprehensive management
  6. Essential for operations that need to work across multiple collaborator roles

Parameters:
  - checklistId: Long (Checklist identifier)
  - types: List<String> (List of collaborator types to include)

Returns: List<ChecklistCollaboratorView> (projection views of matching collaborators)
Transaction: Not Required
Error Handling: Returns empty list if no collaborators found for any of the types
```

#### Method: findFirstByChecklistAndPhaseTypeAndUserAndStateOrderByOrderTreeAsc
```yaml
Signature: Optional<ChecklistCollaboratorMapping> findFirstByChecklistAndPhaseTypeAndUserAndStateOrderByOrderTreeAsc(Checklist checklist, State.ChecklistCollaboratorPhaseType phaseType, User user, State.ChecklistCollaborator state)
Purpose: "Find the first collaboration mapping for a user in specific phase and state"

Business Logic Derivation:
  1. Used to find user's first/primary collaboration mapping in specific workflow state
  2. Orders by order_tree to get the first position mapping when user has multiple roles
  3. Critical for state-specific collaboration validation and workflow progression
  4. Enables precise user state checking within complex collaboration hierarchies
  5. Used for determining user's primary collaboration context in multi-role scenarios

SQL Query: |
  SELECT ccm.* FROM checklist_collaborator_mapping ccm 
  WHERE ccm.checklists_id = ? AND ccm.phase_type = ? 
    AND ccm.users_id = ? AND ccm.state = ?
  ORDER BY ccm.order_tree ASC
  LIMIT 1

Parameters:
  - checklist: Checklist (Checklist entity for collaboration)
  - phaseType: State.ChecklistCollaboratorPhaseType (Phase type context)
  - user: User (User entity to find mapping for)
  - state: State.ChecklistCollaborator (Specific collaboration state to match)

Returns: Optional<ChecklistCollaboratorMapping> (first matching mapping or empty)
Transaction: Not Required
Error Handling: Returns empty Optional if no matching mapping found
```

#### Method: deleteAuthors
```yaml
Signature: void deleteAuthors(Long checklistId, Set<Long> userIds)
Purpose: "Remove author collaborators from a checklist"

Business Logic Derivation:
  1. Used to remove specific authors from checklist collaboration
  2. Targets only AUTHOR and PRIMARY_AUTHOR types for precise removal
  3. Maintains non-author collaborators during author management operations
  4. Uses native query for efficient bulk deletion with type filtering
  5. Critical for author reassignment and checklist ownership management

SQL Query: |
  DELETE FROM checklist_collaborator_mapping 
  WHERE checklists_id = ? AND users_id IN (?) AND type IN ('AUTHOR','PRIMARY_AUTHOR')

  BUSINESS LOGIC:
  1. Target checklist_collaborator_mapping table for author removal
  2. Filter by checklist ID to scope operation to specific checklist
  3. Filter by user IDs set to remove only specified authors
  4. Filter by author types (AUTHOR, PRIMARY_AUTHOR) to preserve other collaborators
  5. Enables precise author management without affecting reviewers or sign-off users
  6. Essential for checklist ownership transitions and author role management

Parameters:
  - checklistId: Long (Checklist to remove authors from)
  - userIds: Set<Long> (Specific author users to remove)

Returns: void
Transaction: Required (uses @Transactional with rollbackFor = Exception.class)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findAllByChecklistId
```yaml
Signature: List<ChecklistCollaboratorMapping> findAllByChecklistId(Long checklistId)
Purpose: "Get all collaboration mappings for a specific checklist"

Business Logic Derivation:
  1. Used to retrieve complete collaboration structure for a checklist
  2. Returns full entity objects for comprehensive collaboration analysis
  3. Enables bulk collaboration operations and complete collaboration state queries
  4. Critical for collaboration export, reporting, and comprehensive management
  5. Used when full collaboration context is needed rather than projections

SQL Query: |
  SELECT ccm.* FROM checklist_collaborator_mapping ccm 
  WHERE ccm.checklists_id = ?

Parameters:
  - checklistId: Long (Checklist identifier to get all collaborations for)

Returns: List<ChecklistCollaboratorMapping> (all collaboration mappings for the checklist)
Transaction: Not Required
Error Handling: Returns empty list if no collaborations exist for the checklist
```

#### Method: deleteAllByChecklistIdAndTypeNot
```yaml
Signature: void deleteAllByChecklistIdAndTypeNot(Long checklistId, Type.Collaborator type)
Purpose: "Delete all collaborators except those of a specific type"

Business Logic Derivation:
  1. Used to remove all collaborators except a preserved type (e.g., keep only AUTHORS)
  2. Enables bulk collaboration cleanup while preserving essential collaborator types
  3. Critical for collaboration reset operations and workflow state management
  4. Uses Spring Data method naming for automatic query generation
  5. Supports collaboration lifecycle management and bulk operations

SQL Query: |
  DELETE FROM checklist_collaborator_mapping 
  WHERE checklists_id = ? AND type <> ?

Parameters:
  - checklistId: Long (Checklist to clean up collaborations for)
  - type: Type.Collaborator (Collaborator type to preserve)

Returns: void
Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: updatePrimaryAuthor
```yaml
Signature: void updatePrimaryAuthor(Long userId, Long checklistId)
Purpose: "Update the primary author assignment for a checklist"

Business Logic Derivation:
  1. Used in ChecklistService during checklist ownership transfer operations
  2. Changes primary author designation while maintaining collaboration history
  3. Critical for checklist ownership management and author reassignment
  4. Uses native query for efficient direct update of author designation
  5. Supports checklist lifecycle management and ownership transitions

SQL Query: |
  UPDATE checklist_collaborator_mapping 
  SET created_by = ?, modified_by = ?, users_id = ? 
  WHERE checklists_id = ? AND type = 'PRIMARY_AUTHOR'

  BUSINESS LOGIC:
  1. Update existing PRIMARY_AUTHOR record for the specified checklist
  2. Change the users_id to reassign primary authorship to new user
  3. Update audit fields (created_by, modified_by) to reflect ownership transfer
  4. Filter by type = 'PRIMARY_AUTHOR' to ensure only primary author record is affected
  5. Maintains collaboration history while transferring checklist ownership
  6. Essential for checklist lifecycle management and ownership transitions

Parameters:
  - userId: Long (New primary author user ID)
  - checklistId: Long (Checklist to update primary author for)

Returns: void
Transaction: Required (uses @Transactional with rollbackFor = Exception.class)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findByChecklistAndPhaseTypeAndTypeAndPhase
```yaml
Signature: List<ChecklistCollaboratorMapping> findByChecklistAndPhaseTypeAndTypeAndPhase(Long checklistId, String phaseType, String type, Integer phase)
Purpose: "Find collaborators matching exact phase type, collaborator type, and phase number"

Business Logic Derivation:
  1. Used for precise collaboration queries with multiple filtering criteria
  2. Enables exact matching of collaboration context for workflow validation
  3. Critical for phase-specific and type-specific collaboration operations
  4. Supports complex collaboration state management and validation
  5. Used when precise collaboration context matching is required

SQL Query: |
  SELECT ccm.* FROM checklist_collaborator_mapping ccm 
  WHERE ccm.checklists_id = ? AND ccm.phase_type = ? 
    AND ccm.type = ? AND ccm.phase = ?

  BUSINESS LOGIC:
  1. Query checklist_collaborator_mapping table with compound filtering
  2. Filter by checklist ID to scope to specific checklist
  3. Filter by phase type to target specific workflow phase
  4. Filter by collaborator type to target specific role
  5. Filter by phase number to target specific iteration
  6. Returns exact collaboration mappings matching all criteria

Parameters:
  - checklistId: Long (Checklist identifier)
  - phaseType: String (Phase type filter)
  - type: String (Collaborator type filter)
  - phase: Integer (Specific phase number)

Returns: List<ChecklistCollaboratorMapping> (exact matching collaboration mappings)
Transaction: Not Required
Error Handling: Returns empty list if no collaborations match all criteria
```

### Key Repository Usage Patterns (Based on Codebase Analysis)

#### Pattern: save() for State Management
```yaml
Usage: checklistCollaboratorMappingRepository.save(checklistCollaboratorMapping)
Purpose: "Update collaboration states during workflow transitions"

Business Logic Derivation:
  1. Used extensively in ChecklistCollaboratorService for state transitions
  2. Updates collaborator states (NOT_STARTED, IN_PROGRESS, COMPLETED, etc.)
  3. Manages workflow progression through review and approval phases
  4. Maintains collaboration audit trail through state changes
  5. Critical for workflow orchestration and progress tracking

Common Usage Examples:
  - Review submission: Updates state from NOT_STARTED to IN_PROGRESS
  - Review completion: Updates state to COMPLETED or REQUESTED_CHANGES
  - Sign-off workflow: Manages sign-off user state progressions

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: saveAll() for Bulk Collaborator Operations
```yaml
Usage: checklistCollaboratorMappingRepository.saveAll(newChecklistCollaboratorMapping)
Purpose: "Bulk create collaborator mappings during review setup"

Business Logic Derivation:
  1. Used when adding multiple reviewers or collaborators to checklists
  2. Optimizes database operations for bulk collaborator assignment
  3. Ensures transactional consistency for multi-user collaboration setup
  4. Creates collaboration mappings with appropriate states and phase information
  5. Supports efficient collaboration workflow initialization

Transaction: Required
Error Handling: Batch operation rollback on any failure
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllByChecklistIdAndPhaseType, findAllByChecklistIdAndType
  - findAllByTypeOrderByOrderTreeAndModifiedAt, findAllByChecklistIdAndTypeIn
  - findByChecklistAndPhaseTypeAndPhaseAndUser, findFirstByChecklistAndPhaseTypeAndUserAndStateOrderByOrderTreeAsc
  - isCollaboratorMappingExistsByChecklistAndUserIdAndCollaboratorType, findAllByChecklistId
  - findByChecklistAndPhaseTypeAndTypeAndPhase, existsById, count

Transactional Methods:
  - save, delete, deleteById, deleteAll, deleteAuthors, deleteAllByChecklistIdAndTypeNot, updatePrimaryAuthor

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid checklists_id, users_id)
    * NOT NULL constraint violations (type, state, phaseType)
    * Invalid enum values for type, state, or phaseType fields
    * Unique constraint violations for duplicate collaborator assignments
  - EntityNotFoundException: Collaborator mapping not found by ID or criteria
  - OptimisticLockException: Concurrent collaborator state modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or malformed native queries

Validation Rules:
  - checklist: Required, must reference existing checklist, immutable (updatable = false)
  - user: Required, must reference existing user, immutable (updatable = false)
  - type: Required, must be valid Type.Collaborator enum value
  - state: Required, must be valid ChecklistCollaborator enum value
  - phaseType: Required, must be valid ChecklistCollaboratorPhaseType enum value
  - phase: Required, defaults to 1, must be positive integer
  - orderTree: Required, defaults to 1, must be positive integer

Business Constraints:
  - Cannot modify checklist or user associations after creation
  - State transitions must follow valid collaboration workflow sequences
  - Primary author designation must be unique per checklist
  - Phase and phaseType combinations must be consistent with workflow rules
  - Collaborator types must align with phase types (reviewers in REVIEW phase, etc.)
  - Cannot delete collaborators with active comments or pending state transitions
```

This comprehensive documentation provides everything needed to implement an exact DAO layer replacement for the ChecklistCollaboratorMapping repository without JPA/Hibernate dependencies.
