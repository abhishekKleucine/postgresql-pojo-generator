# IAutomationRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Automation
- **Primary Purpose**: Manages workflow automation rules and actions that execute automatically based on triggers
- **Key Relationships**: Standalone entity with audit relationships to User entities
- **Performance Characteristics**: Low to medium query volume with complex JSON-based configuration queries
- **Business Context**: Core automation engine component that defines automated behaviors, actions, and triggers in workflow processes

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| type | type | Type.AutomationType | false | null |
| action_type | actionType | Type.AutomationActionType | false | null |
| target_entity_type | targetEntityType | Type.TargetEntityType | false | null |
| action_details | actionDetails | JsonNode | false | {} |
| trigger_type | triggerType | Type.AutomationTriggerType | false | null |
| trigger_details | triggerDetails | JsonNode | false | {} |
| archived | archived | boolean | false | false |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | createdBy | User | LAZY | User who created the automation rule |
| @ManyToOne | modifiedBy | User | LAZY | User who last modified the automation rule |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Automation entity)`
- `deleteById(Long id)`
- `delete(Automation entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods
- `getAllParametersWhereParameterIdUsedInAutomation(String parameterId)`
- `getAllAutomationsWhereObjectTypePropertyIsUsed(String propertyId)`
- `getAllAutomationsWhereObjectTypeRelationIsUsed(String relationId)`

## Method Documentation

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Automation> findById(Long id)
List<Automation> findAll()
Automation save(Automation entity)
void deleteById(Long id)
void delete(Automation entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: getAllParametersWhereParameterIdUsedInAutomation
```yaml
Signature: List<IdView> getAllParametersWhereParameterIdUsedInAutomation(String parameterId)
Purpose: "Find all automation rules that reference a specific parameter in their configuration"

Business Logic Derivation:
  1. Search automation configurations where parameter ID is referenced in action or trigger details
  2. Parse JSON fields (actionDetails, triggerDetails) to find parameter references
  3. Used for impact analysis when modifying or deleting parameters
  4. Identifies automation rules that would be affected by parameter changes
  5. Returns ID projections for efficient dependency mapping and validation

SQL Query: |
  SELECT a.id as id
  FROM automations a
  WHERE (action_details ->> 'parameterId' = ?
      OR action_details ->> 'referencedParameterId' = ?
      OR action_details ->> 'offsetParameterId' = ?
      OR EXISTS (SELECT 1
                 FROM jsonb_array_elements(a.action_details -> 'configuration') config
                 WHERE config ->> 'parameterId' = ?))
    AND a.archived = false

  BUSINESS LOGIC:
  1. Search automations table for parameter references in JSON action_details field
  2. Check multiple JSON properties where parameter IDs might be referenced
  3. Look in parameterId, referencedParameterId, and offsetParameterId fields
  4. Use jsonb_array_elements to search within configuration arrays
  5. Filter out archived automations to focus on active rules
  6. Returns automation IDs that would be affected by parameter changes

Parameters:
  - parameterId: String (Parameter identifier to find automation dependencies for)

Returns: List<IdView> (ID projections of automation rules using the parameter)
Transaction: Not Required
Error Handling: Returns empty list if parameter is not used in any automation rules
```

#### Method: getAllAutomationsWhereObjectTypePropertyIsUsed
```yaml
Signature: List<IdView> getAllAutomationsWhereObjectTypePropertyIsUsed(String propertyId)
Purpose: "Find all automation rules that reference a specific object type property"

Business Logic Derivation:
  1. Search automation configurations where object type property is referenced in JSON details
  2. Parse actionDetails and triggerDetails JSON fields for property references
  3. Used for impact analysis when modifying object type schemas and properties
  4. Identifies automation rules affected by property changes or deletions
  5. Enables property usage tracking across automation system for schema validation

SQL Query: |
  SELECT a.id as id
  FROM automations a
  JOIN task_automation_mapping tam ON a.id = tam.automations_id
  JOIN tasks t ON tam.tasks_id = t.id
  JOIN stages s ON t.stages_id = s.id
  JOIN checklists c ON s.checklists_id = c.id
  WHERE a.archived = false
    AND t.archived = false
    AND s.archived = false
    AND c.state != 'DEPRECATED'
    AND c.archived = false
    AND a.action_details ->> 'propertyId' = ?

  BUSINESS LOGIC:
  1. Join automations with task_automation_mapping to get task associations
  2. Join through tasks, stages, checklists to validate context hierarchy
  3. Filter for active (non-archived) automations and associated entities
  4. Exclude deprecated checklists to focus on current workflows
  5. Search action_details JSON field for specific propertyId references
  6. Returns automation IDs that use the specified object type property

Parameters:
  - propertyId: String (Object type property identifier to find dependencies for)

Returns: List<IdView> (ID projections of automation rules using the property)
Transaction: Not Required
Error Handling: Returns empty list if property is not used in any automation rules
```

#### Method: getAllAutomationsWhereObjectTypeRelationIsUsed
```yaml
Signature: List<IdView> getAllAutomationsWhereObjectTypeRelationIsUsed(String relationId)
Purpose: "Find all automation rules that reference a specific object type relation"

Business Logic Derivation:
  1. Search automation configurations where object type relation is referenced in JSON details
  2. Parse actionDetails and triggerDetails JSON fields for relation references
  3. Used for impact analysis when modifying object relationship schemas
  4. Identifies automation rules affected by relation changes or deletions
  5. Enables relation usage tracking across automation system for schema integrity

SQL Query: |
  SELECT a.id as id
  FROM automations a
  JOIN task_automation_mapping tam ON a.id = tam.automations_id
  JOIN tasks t ON tam.tasks_id = t.id
  JOIN stages s ON t.stages_id = s.id
  JOIN checklists c ON s.checklists_id = c.id
  WHERE a.archived = false
    AND t.archived = false
    AND s.archived = false
    AND c.state != 'DEPRECATED'
    AND c.archived = false
    AND a.action_details ->> 'relationId' = ?

  BUSINESS LOGIC:
  1. Join automations with task_automation_mapping to get task associations
  2. Join through tasks, stages, checklists to validate context hierarchy
  3. Filter for active (non-archived) automations and associated entities
  4. Exclude deprecated checklists to focus on current workflows
  5. Search action_details JSON field for specific relationId references
  6. Returns automation IDs that use the specified object type relation

Parameters:
  - relationId: String (Object type relation identifier to find dependencies for)

Returns: List<IdView> (ID projections of automation rules using the relation)
Transaction: Not Required
Error Handling: Returns empty list if relation is not used in any automation rules
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, getAllParametersWhereParameterIdUsedInAutomation
  - getAllAutomationsWhereObjectTypePropertyIsUsed, getAllAutomationsWhereObjectTypeRelationIsUsed
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
    * NOT NULL constraint violations (type, actionType, targetEntityType, triggerType)
    * Invalid enum values for automation type fields
    * Malformed JSON in actionDetails or triggerDetails fields
  - EntityNotFoundException: Automation rule not found by ID
  - InvalidDataAccessApiUsageException: Invalid query parameters or malformed native queries
  - JsonProcessingException: Invalid JSON format in actionDetails or triggerDetails

Validation Rules:
  - type: Required, must be valid AutomationType enum value
  - actionType: Required, must be valid AutomationActionType enum value
  - targetEntityType: Required, must be valid TargetEntityType enum value
  - triggerType: Required, must be valid AutomationTriggerType enum value
  - actionDetails: Required, must be valid JSON object (defaults to {})
  - triggerDetails: Required, must be valid JSON object (defaults to {})
  - JSON fields must contain valid configuration data matching automation type requirements

Business Constraints:
  - Automation configuration must be consistent (trigger and action types must be compatible)
  - JSON configuration fields must contain valid automation rule definitions
  - Cannot delete automation if it would break active workflow executions
  - Archive status should be managed through proper lifecycle methods
  - Automation rules must reference valid and accessible entities in their JSON configurations
```

This comprehensive documentation provides everything needed to implement an exact DAO layer replacement for the Automation repository without JPA/Hibernate dependencies.
