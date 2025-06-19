# IChecklistCollaboratorCommentsRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ChecklistCollaboratorComments
- **Primary Purpose**: Manages review comments from collaborators on checklists during approval processes
- **Key Relationships**: Links collaborator mappings to checklists with review states and comments
- **Performance Characteristics**: Low to medium write volume with bulk deletion operations
- **Business Context**: Collaboration component that captures reviewer feedback and approval states during checklist review workflows

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| checklist_collaborator_mappings_id | checklistCollaboratorMapping.id | Long | false | null |
| checklists_id | checklist.id | Long | false | null |
| review_state | reviewState | State.ChecklistCollaborator | false | null |
| comments | comments | String | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | checklistCollaboratorMapping | ChecklistCollaboratorMapping | EAGER | Parent collaborator mapping, updatable = false |
| @ManyToOne | checklist | Checklist | EAGER | Associated checklist, updatable = false, cascade = DETACH |
| @ManyToOne | createdBy | User | LAZY | User who created the comment |
| @ManyToOne | modifiedBy | User | LAZY | User who last modified the comment |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(ChecklistCollaboratorComments entity)`
- `deleteById(Long id)`
- `delete(ChecklistCollaboratorComments entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods
- `deleteByChecklistCollaboratorMappingId(Long checklistCollaboratorMappingId)`
- `deleteAllByChecklistId(Long checklistId)`

## Method Documentation

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<ChecklistCollaboratorComments> findById(Long id)
List<ChecklistCollaboratorComments> findAll()
ChecklistCollaboratorComments save(ChecklistCollaboratorComments entity)
void deleteById(Long id)
void delete(ChecklistCollaboratorComments entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: deleteByChecklistCollaboratorMappingId
```yaml
Signature: void deleteByChecklistCollaboratorMappingId(Long checklistCollaboratorMappingId)
Purpose: "Delete all comments associated with a specific collaborator mapping"

Business Logic Derivation:
  1. Used when removing collaborators from checklist review process
  2. Cleans up all comments made by specific collaborator before removing their access
  3. Called in ChecklistCollaboratorService when deleting collaborator mappings
  4. Ensures referential integrity by removing dependent comment records
  5. Uses native query with @Modifying for efficient bulk deletion

SQL Query: |
  DELETE FROM checklist_collaborator_comments 
  WHERE checklist_collaborator_mappings_id = ?

  BUSINESS LOGIC:
  1. Target the checklist_collaborator_comments table directly
  2. Filter by foreign key to specific collaborator mapping
  3. Remove all comment records associated with that mapping
  4. Ensures complete cleanup when removing collaborator access
  5. Maintains referential integrity during collaborator management
  6. Efficient bulk deletion for collaboration workflow cleanup

Parameters:
  - checklistCollaboratorMappingId: Long (Collaborator mapping to delete comments for)

Returns: void
Transaction: Required (uses @Transactional with rollbackFor = Exception.class)
Error Handling: DataIntegrityViolationException for constraint violations, rollback on any exception
```

#### Method: deleteAllByChecklistId
```yaml
Signature: void deleteAllByChecklistId(Long checklistId)
Purpose: "Delete all collaborator comments for a specific checklist"

Business Logic Derivation:
  1. Used during checklist cleanup operations and bulk deletion scenarios
  2. Removes all reviewer comments when checklist is being deleted or reset
  3. Called in ChecklistService during checklist management operations
  4. Ensures complete cleanup of collaboration data during checklist lifecycle
  5. Spring Data auto-generated method based on field naming convention

SQL Query: |
  DELETE FROM checklist_collaborator_comments 
  WHERE checklists_id = ?

Parameters:
  - checklistId: Long (Checklist identifier to delete all comments for)

Returns: void
Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

### Key Repository Usage Patterns (Based on Codebase Analysis)

#### Pattern: save() for Comment Creation
```yaml
Usage: checklistCollaboratorCommentsRepository.save(checklistCollaboratorComments)
Purpose: "Create new collaborator review comments with approval states"

Business Logic Derivation:
  1. Used extensively in ChecklistCollaboratorService for capturing reviewer feedback
  2. Creates comments during review submission and approval workflows
  3. Associates comments with specific collaborator mappings and review states
  4. Captures reviewer decisions (approve, reject, request changes) with detailed feedback
  5. Links comments to checklist context for review tracking and audit trails

Common Usage Examples:
  - Review submission: Creates comment with review state and feedback text
  - Approval workflow: Records reviewer decisions and rationale
  - Collaboration tracking: Maintains history of reviewer interactions

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, existsById, count

Transactional Methods:
  - save, delete, deleteById, deleteByChecklistCollaboratorMappingId, deleteAllByChecklistId

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid checklist_collaborator_mappings_id, checklists_id)
    * NOT NULL constraint violations (reviewState, comments)
    * Invalid enum values for reviewState field
  - EntityNotFoundException: Comment not found by ID
  - OptimisticLockException: Concurrent comment modifications

Validation Rules:
  - checklistCollaboratorMapping: Required, must reference existing collaborator mapping
  - checklist: Required, must reference existing checklist
  - reviewState: Required, must be valid ChecklistCollaborator enum value
  - comments: Required, text field with reviewer feedback
  - Both mapping and checklist associations are immutable (updatable = false)

Business Constraints:
  - Comments are linked to active collaborator mappings
  - Cannot modify checklist or collaborator mapping associations after creation
  - Review state must be consistent with collaboration workflow states
  - Comments must be preserved for audit trail until explicit deletion
  - Bulk deletion operations must maintain referential integrity
```

This comprehensive documentation provides everything needed to implement an exact DAO layer replacement for the ChecklistCollaboratorComments repository without JPA/Hibernate dependencies.
