# IActionRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Action
- **Primary Purpose**: Manages workflow actions and automation triggers within checklists
- **Key Relationships**: Child of Checklist, associated with ActionFacilityMapping for facility scoping
- **Performance Characteristics**: Medium query volume with pagination support for large action sets
- **Business Context**: Core automation component that defines triggered behaviors and responses in workflow processes

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | false | null |
| description | description | String | true | null |
| code | code | String | false | null |
| trigger_type | triggerType | ActionTriggerType | false | null |
| trigger_entity_id | triggerEntityId | Long | false | null |
| archived | archived | boolean | false | false |
| success_message | successMessage | String | true | null |
| failure_message | failureMessage | String | true | null |
| checklists_id | checklistId | Long | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | checklist | Checklist | LAZY | Parent checklist, updatable = false |
| @ManyToOne | createdBy | User | LAZY | User who created the action |
| @ManyToOne | modifiedBy | User | LAZY | User who last modified the action |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Action entity)`
- `deleteById(Long id)`
- `delete(Action entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods
- `findByTriggerEntityId(Long triggerEntityId, Pageable pageable)`
- `findByChecklistId(Long checklistId, Pageable pageable)`
- `findByChecklistIdAndArchived(Long checklistId, boolean archived, Pageable pageable)`

## Method Documentation

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Action> findById(Long id)
List<Action> findAll()
Action save(Action entity)
void deleteById(Long id)
void delete(Action entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: findByTriggerEntityId
```yaml
Signature: Page<Action> findByTriggerEntityId(Long triggerEntityId, Pageable pageable)
Purpose: "Find all actions triggered by a specific entity with pagination support"

Business Logic Derivation:
  1. Query actions table filtering by trigger_entity_id column
  2. Apply pagination to handle large result sets efficiently
  3. Used to identify all automation actions associated with a specific triggering entity
  4. Enables entity-centric action discovery for workflow automation
  5. Returns paginated results for UI display and performance optimization

SQL Query: |
  SELECT a.* FROM actions a 
  WHERE a.trigger_entity_id = ? 
  ORDER BY a.created_at DESC
  LIMIT ? OFFSET ?

Parameters:
  - triggerEntityId: Long (Entity identifier that triggers these actions)
  - pageable: Pageable (Pagination and sorting parameters)

Returns: Page<Action> (paginated list of actions for the trigger entity)
Transaction: Not Required
Error Handling: Returns empty page if no actions found for the trigger entity
```

#### Method: findByChecklistId
```yaml
Signature: Page<Action> findByChecklistId(Long checklistId, Pageable pageable)
Purpose: "Find all actions within a specific checklist with pagination support"

Business Logic Derivation:
  1. Query actions table filtering by checklists_id foreign key
  2. Apply pagination for efficient handling of large action collections
  3. Used to retrieve all automation actions defined within a checklist scope
  4. Enables checklist-level action management and configuration
  5. Supports UI pagination for action listing and administration

SQL Query: |
  SELECT a.* FROM actions a 
  WHERE a.checklists_id = ? 
  ORDER BY a.created_at DESC
  LIMIT ? OFFSET ?

Parameters:
  - checklistId: Long (Checklist identifier to find actions for)
  - pageable: Pageable (Pagination and sorting parameters)

Returns: Page<Action> (paginated list of actions within the checklist)
Transaction: Not Required
Error Handling: Returns empty page if no actions exist in the checklist
```

#### Method: findByChecklistIdAndArchived
```yaml
Signature: Page<Action> findByChecklistIdAndArchived(Long checklistId, boolean archived, Pageable pageable)
Purpose: "Find actions in a checklist filtered by archive status with pagination"

Business Logic Derivation:
  1. Query actions table with compound filter on checklists_id and archived status
  2. Apply pagination to manage large filtered result sets
  3. Used to separate active vs archived actions within checklist scope
  4. Enables archive-aware action management and lifecycle operations
  5. Supports filtered UI views showing only active or archived actions

SQL Query: |
  SELECT a.* FROM actions a 
  WHERE a.checklists_id = ? AND a.archived = ? 
  ORDER BY a.created_at DESC
  LIMIT ? OFFSET ?

Parameters:
  - checklistId: Long (Checklist identifier to find actions for)
  - archived: boolean (Archive status filter - true for archived, false for active)
  - pageable: Pageable (Pagination and sorting parameters)

Returns: Page<Action> (paginated list of actions matching checklist and archive criteria)
Transaction: Not Required
Error Handling: Returns empty page if no actions match the filter criteria
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByTriggerEntityId
  - findByChecklistId, findByChecklistIdAndArchived
  - existsById, count

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
    * Foreign key violations (invalid checklists_id)
    * NOT NULL constraint violations (name, code, trigger_type, trigger_entity_id)
    * Unique constraint violations if code uniqueness required
  - EntityNotFoundException: Action not found by ID
  - InvalidDataAccessApiUsageException: Invalid pagination parameters

Validation Rules:
  - name: Required, max length based on column definition
  - code: Required, should be unique within checklist scope
  - triggerType: Required, must be valid ActionTriggerType enum value
  - triggerEntityId: Required, must reference existing entity
  - checklistId: Required, must reference existing checklist

Business Constraints:
  - Cannot modify checklist association after creation (updatable = false)
  - Cannot delete action if it has dependent automation rules
  - Archive status should be managed through proper lifecycle methods
  - Trigger entity must exist and be accessible in the checklist scope
```

This comprehensive documentation provides everything needed to implement an exact DAO layer replacement for the Action repository without JPA/Hibernate dependencies.
