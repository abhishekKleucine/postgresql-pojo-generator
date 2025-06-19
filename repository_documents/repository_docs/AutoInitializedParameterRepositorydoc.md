# IAutoInitializedParameterRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: AutoInitializedParameter
- **Primary Purpose**: Manages automatic parameter initialization rules and dependencies within checklists
- **Key Relationships**: Links parameters that trigger auto-initialization with target parameters to be initialized
- **Performance Characteristics**: Medium query volume with complex dependency resolution queries
- **Business Context**: Enables workflow automation by automatically initializing parameter values based on other parameter states and object relationships

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| auto_initialized_parameters_id | autoInitializedParameterId | Long | false | null |
| referenced_parameters_id | referencedParameter.id | Long | false | null |
| checklists_id | checklistId | Long | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @OneToOne | autoInitializedParameter | Parameter | LAZY | Target parameter to be auto-initialized, updatable = false |
| @ManyToOne | referencedParameter | Parameter | LAZY | Source parameter that triggers initialization, updatable = false |
| @ManyToOne | checklist | Checklist | LAZY | Parent checklist scope, updatable = false |
| @ManyToOne | createdBy | User | LAZY | User who created the auto-initialization rule |
| @ManyToOne | modifiedBy | User | LAZY | User who last modified the rule |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(AutoInitializedParameter entity)`
- `deleteById(Long id)`
- `delete(AutoInitializedParameter entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods
- `deleteByChecklistId(Long checklistId)`
- `findAllEligibleParameterIdsToAutoInitializeByReferencedParameterId(Long referencedParameterId, Set<Long> executedParameterIds, Long jobId)`
- `getAllAutoInitializedParametersWhereParameterIsUsed(Long parameterId)`
- `getAllAutoInitializedParametersWhereObjectTypePropertyIsUsed(String propertyId)`
- `getAllAutoInitializedParametersWhereObjectTypeRelationIsUsed(String relationId)`
- `existsByAutoInitializedParameterId(Long autoInitializedParameterId)`
- `getReferencedParameterIdByAutoInitializedParameterId(Long autoInitializedParameterId)`

## Method Documentation

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<AutoInitializedParameter> findById(Long id)
List<AutoInitializedParameter> findAll()
AutoInitializedParameter save(AutoInitializedParameter entity)
void deleteById(Long id)
void delete(AutoInitializedParameter entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: deleteByChecklistId
```yaml
Signature: void deleteByChecklistId(Long checklistId)
Purpose: "Delete all auto-initialization rules for a specific checklist"

Business Logic Derivation:
  1. Remove all auto-initialization parameter mappings associated with a checklist
  2. Used during checklist cleanup or reset operations
  3. Ensures referential integrity when checklist is deleted or modified
  4. Cascades deletion of dependent auto-initialization rules
  5. Prevents orphaned auto-initialization configurations

SQL Query: |
  DELETE FROM auto_initialized_parameters 
  WHERE checklists_id = ?

Parameters:
  - checklistId: Long (Checklist identifier to remove auto-initialization rules for)

Returns: void
Transaction: Required
Error Handling: DataIntegrityViolationException if foreign key constraints violated
```

#### Method: findAllEligibleParameterIdsToAutoInitializeByReferencedParameterId
```yaml
Signature: List<Long> findAllEligibleParameterIdsToAutoInitializeByReferencedParameterId(Long referencedParameterId, Set<Long> executedParameterIds, Long jobId)
Purpose: "Find all parameters eligible for auto-initialization when a referenced parameter is executed"

Business Logic Derivation:
  1. Query auto-initialization rules where referenced parameter matches input
  2. Filter out parameters already executed in the current job execution
  3. Apply job-specific context to determine eligible auto-initialization targets
  4. Return parameter IDs that should be automatically initialized next
  5. Enables cascade auto-initialization workflow execution

SQL Query: |
  SELECT aip.auto_initialized_parameters_id
  FROM auto_initialized_parameters aip
  INNER JOIN parameters p ON p.id = aip.auto_initialized_parameters_id
  INNER JOIN parameter_values pv ON pv.parameters_id = p.id
  LEFT JOIN tasks t ON p.tasks_id = t.id
  LEFT JOIN stages s ON t.stages_id = s.id
  LEFT JOIN task_executions te ON pv.task_executions_id = te.id
  WHERE aip.referenced_parameters_id = ?
    AND (te.state IN ('IN_PROGRESS', 'PAUSED') OR (p.target_entity_type = 'PROCESS'))
    AND pv.hidden = false
    AND p.id NOT IN (/* executedParameterIds set */)
    AND pv.jobs_id = ?
  ORDER BY s.order_tree, t.order_tree, p.order_tree

  BUSINESS LOGIC:
  1. Find auto-initialization mappings where referenced parameter matches trigger
  2. Join with parameters and parameter_values to get execution context
  3. Filter for active task executions (IN_PROGRESS, PAUSED) or process-level parameters
  4. Exclude hidden parameters and already executed parameters
  5. Order by stage -> task -> parameter sequence for proper execution flow
  6. Returns parameter IDs ready for auto-initialization in correct execution order

Parameters:
  - referencedParameterId: Long (Parameter that was just executed and triggers auto-initialization)
  - executedParameterIds: Set<Long> (Parameters already executed in current job to avoid duplicates)
  - jobId: Long (Current job execution context)

Returns: List<Long> (IDs of parameters eligible for auto-initialization)
Transaction: Not Required
Error Handling: Returns empty list if no eligible parameters found
```

#### Method: getAllAutoInitializedParametersWhereParameterIsUsed
```yaml
Signature: List<IdView> getAllAutoInitializedParametersWhereParameterIsUsed(Long parameterId)
Purpose: "Find all auto-initialization rules that reference a specific parameter"

Business Logic Derivation:
  1. Search auto-initialization configurations that depend on the given parameter
  2. Used for impact analysis when modifying or deleting parameters
  3. Identifies cascade effects of parameter changes on auto-initialization
  4. Returns lightweight ID projections for efficient dependency mapping
  5. Enables parameter usage tracking and validation

SQL Query: |
  SELECT aip.id as id
  FROM auto_initialized_parameters aip
  WHERE auto_initialized_parameters_id = ?
     OR referenced_parameters_id = ?

  BUSINESS LOGIC:
  1. Search auto_initialized_parameters table for parameter usage in either role
  2. Check if parameter is used as auto-initialized target (auto_initialized_parameters_id)
  3. Check if parameter is used as trigger source (referenced_parameters_id)
  4. Returns IDs of auto-initialization rules that would be affected by parameter changes
  5. Essential for impact analysis before parameter deletion or modification

Parameters:
  - parameterId: Long (Parameter to find auto-initialization dependencies for)

Returns: List<IdView> (ID projections of auto-initialization rules using the parameter)
Transaction: Not Required
Error Handling: Returns empty list if parameter is not used in any auto-initialization rules
```

#### Method: getAllAutoInitializedParametersWhereObjectTypePropertyIsUsed
```yaml
Signature: List<IdView> getAllAutoInitializedParametersWhereObjectTypePropertyIsUsed(String propertyId)
Purpose: "Find all auto-initialization rules that reference a specific object type property"

Business Logic Derivation:
  1. Search auto-initialization configurations that depend on object type properties
  2. Used for impact analysis when modifying object type schemas
  3. Identifies auto-initialization rules affected by property changes
  4. Returns ID projections for efficient dependency resolution
  5. Enables property usage tracking across auto-initialization system

SQL Query: |
  SELECT p.id as id
  FROM parameters p
  JOIN checklists c ON p.checklists_id = c.id
  WHERE c.state != 'DEPRECATED'
    AND c.archived = false
    AND p.archived = false
    AND auto_initialize != '{}'
    AND (auto_initialize -> 'property' ->> 'id') = ?

  BUSINESS LOGIC:
  1. Search parameters table for auto-initialization configurations using specific property
  2. Join with checklists to filter out deprecated and archived checklists
  3. Filter for active (non-archived) parameters only
  4. Check auto_initialize JSON field for property references
  5. Extract property ID from JSON structure and match against input parameter
  6. Returns parameter IDs that use the specified object type property in auto-initialization

Parameters:
  - propertyId: String (Object type property identifier to find dependencies for)

Returns: List<IdView> (ID projections of auto-initialization rules using the property)
Transaction: Not Required
Error Handling: Returns empty list if property is not used in any auto-initialization rules
```

#### Method: getAllAutoInitializedParametersWhereObjectTypeRelationIsUsed
```yaml
Signature: List<IdView> getAllAutoInitializedParametersWhereObjectTypeRelationIsUsed(String relationId)
Purpose: "Find all auto-initialization rules that reference a specific object type relation"

Business Logic Derivation:
  1. Search auto-initialization configurations that depend on object type relations
  2. Used for impact analysis when modifying object relationship schemas
  3. Identifies auto-initialization rules affected by relation changes
  4. Returns ID projections for efficient dependency resolution
  5. Enables relation usage tracking across auto-initialization system

SQL Query: |
  SELECT p.id as id
  FROM parameters p
  JOIN checklists c ON p.checklists_id = c.id
  WHERE c.state != 'DEPRECATED'
    AND c.archived = false
    AND p.archived = false
    AND auto_initialize != '{}'
    AND (auto_initialize -> 'relation' ->> 'id') = ?

  BUSINESS LOGIC:
  1. Search parameters table for auto-initialization configurations using specific relation
  2. Join with checklists to filter out deprecated and archived checklists
  3. Filter for active (non-archived) parameters only
  4. Check auto_initialize JSON field for relation references
  5. Extract relation ID from JSON structure and match against input parameter
  6. Returns parameter IDs that use the specified object type relation in auto-initialization

Parameters:
  - relationId: String (Object type relation identifier to find dependencies for)

Returns: List<IdView> (ID projections of auto-initialization rules using the relation)
Transaction: Not Required
Error Handling: Returns empty list if relation is not used in any auto-initialization rules
```

#### Method: existsByAutoInitializedParameterId
```yaml
Signature: boolean existsByAutoInitializedParameterId(Long autoInitializedParameterId)
Purpose: "Check if an auto-initialization rule exists for a specific parameter"

Business Logic Derivation:
  1. Query auto-initialization table for existence of specific parameter configuration
  2. Used for validation before creating new auto-initialization rules
  3. Prevents duplicate auto-initialization configurations for same parameter
  4. Enables parameter auto-initialization status checking
  5. Supports validation logic in auto-initialization management

SQL Query: |
  SELECT COUNT(*) > 0 FROM auto_initialized_parameters 
  WHERE auto_initialized_parameters_id = ?

Parameters:
  - autoInitializedParameterId: Long (Parameter to check for existing auto-initialization rule)

Returns: boolean (true if auto-initialization rule exists, false otherwise)
Transaction: Not Required
Error Handling: Returns false if parameter not found
```

#### Method: getReferencedParameterIdByAutoInitializedParameterId
```yaml
Signature: List<IdView> getReferencedParameterIdByAutoInitializedParameterId(Long autoInitializedParameterId)
Purpose: "Get all referenced parameters that trigger auto-initialization for a specific parameter"

Business Logic Derivation:
  1. Query auto-initialization rules to find all parameters that trigger the target parameter
  2. Used for dependency analysis and reverse lookup of auto-initialization triggers
  3. Enables understanding of what causes a parameter to be auto-initialized
  4. Returns ID projections for efficient dependency mapping
  5. Supports auto-initialization rule management and troubleshooting

SQL Query: |
  SELECT aip.referenced_parameters_id as id 
  FROM auto_initialized_parameters aip
  WHERE aip.auto_initialized_parameters_id = ?

  BUSINESS LOGIC:
  1. Query auto_initialized_parameters table for reverse dependency lookup
  2. Find all referenced parameters that trigger the specified auto-initialized parameter
  3. Return parameter IDs that serve as triggers for the target parameter
  4. Enables understanding of auto-initialization dependency chains
  5. Used for troubleshooting and managing auto-initialization workflows
  6. Essential for impact analysis when modifying trigger parameters

Parameters:
  - autoInitializedParameterId: Long (Auto-initialized parameter to find triggers for)

Returns: List<IdView> (ID projections of parameters that trigger the auto-initialization)
Transaction: Not Required
Error Handling: Returns empty list if no referenced parameters found
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllEligibleParameterIdsToAutoInitializeByReferencedParameterId
  - getAllAutoInitializedParametersWhereParameterIsUsed, getAllAutoInitializedParametersWhereObjectTypePropertyIsUsed
  - getAllAutoInitializedParametersWhereObjectTypeRelationIsUsed, existsByAutoInitializedParameterId
  - getReferencedParameterIdByAutoInitializedParameterId, existsById, count

Transactional Methods:
  - save, delete, deleteById, deleteByChecklistId

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid auto_initialized_parameters_id, referenced_parameters_id, checklists_id)
    * NOT NULL constraint violations
    * Unique constraint violations for duplicate auto-initialization rules
  - EntityNotFoundException: Auto-initialization rule not found by ID
  - InvalidDataAccessApiUsageException: Invalid query parameters or malformed native queries

Validation Rules:
  - autoInitializedParameterId: Required, must reference existing parameter
  - referencedParameter: Required, must reference existing parameter
  - checklistId: Required, must reference existing checklist
  - Cannot create self-referencing auto-initialization (parameter cannot auto-initialize itself)
  - Referenced and auto-initialized parameters must be in same checklist scope

Business Constraints:
  - Auto-initialization rules are immutable after creation (all foreign keys updatable = false)
  - Cannot delete auto-initialization rule if it would break parameter execution workflow
  - Auto-initialization must not create circular dependencies
  - Referenced parameter execution must precede auto-initialized parameter in workflow sequence
```

This comprehensive documentation provides everything needed to implement an exact DAO layer replacement for the AutoInitializedParameter repository without JPA/Hibernate dependencies.
