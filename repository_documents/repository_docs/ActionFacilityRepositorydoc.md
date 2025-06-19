# IActionFacilityRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ActionFacilityMapping
- **Primary Purpose**: Manages the many-to-many relationship mapping between Actions and Facilities
- **Key Relationships**: Links Action entities to Facility entities for access control and scoping
- **Performance Characteristics**: Low to medium query volume, simple lookup operations
- **Business Context**: Controls which actions are available in which facilities, enabling facility-scoped action permissions and workflow management

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| actions_id | actionFacilityCompositeKey.actionId | Long | false | null |
| facilities_id | actionFacilityCompositeKey.facilityId | Long | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | action | Action | LAZY | Parent action entity |
| @ManyToOne | facility | Facility | LAZY | Target facility entity |
| @ManyToOne | createdBy | User | LAZY | User who created the mapping |
| @ManyToOne | modifiedBy | User | LAZY | User who last modified the mapping |

## Available Repository Methods

### Standard CRUD Methods
- `findById(ActionFacilityCompositeKey id)`
- `findAll()`
- `save(ActionFacilityMapping entity)`
- `deleteById(ActionFacilityCompositeKey id)`
- `delete(ActionFacilityMapping entity)`
- `existsById(ActionFacilityCompositeKey id)`
- `count()`

### Custom Query Methods
- `findActionFacilityMappingByAction_Id(Long actionId)`

## Method Documentation

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<ActionFacilityMapping> findById(ActionFacilityCompositeKey id)
List<ActionFacilityMapping> findAll()
ActionFacilityMapping save(ActionFacilityMapping entity)
void deleteById(ActionFacilityCompositeKey id)
void delete(ActionFacilityMapping entity)
boolean existsById(ActionFacilityCompositeKey id)
long count()
```

### Custom Query Methods

#### Method: findActionFacilityMappingByAction_Id
```yaml
Signature: List<ActionFacilityMapping> findActionFacilityMappingByAction_Id(Long actionId)
Purpose: "Find all facility mappings for a specific action"

Business Logic Derivation:
  1. Query action_facility_mapping table for specific action ID
  2. Return all facility associations for that action
  3. Used for determining action availability scope within facilities
  4. Enables facility-based access control for actions

SQL Query: |
  SELECT afm.* FROM action_facility_mapping afm 
  WHERE afm.actions_id = ?

Parameters:
  - actionId: Long (Action identifier to find facility mappings for)

Returns: List<ActionFacilityMapping> (all facility mappings for the action)
Transaction: Not Required
Error Handling: Returns empty list if no mappings found
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findActionFacilityMappingByAction_Id
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
    * Duplicate primary key (same action-facility combination)
    * Foreign key violations (invalid action_id or facility_id)
    * NOT NULL constraint violations
  - EntityNotFoundException: Mapping not found by composite key

Validation Rules:
  - actionId: Required, must reference existing action
  - facilityId: Required, must reference existing facility
  - Composite key (actionId, facilityId) must be unique

Business Constraints:
  - Cannot create duplicate action-facility mappings
  - Cannot delete mapping if it would break action accessibility requirements
  - Both action and facility must exist before creating mapping
```

This comprehensive documentation provides everything needed to implement an exact DAO layer replacement for the ActionFacilityMapping repository without JPA/Hibernate dependencies.
