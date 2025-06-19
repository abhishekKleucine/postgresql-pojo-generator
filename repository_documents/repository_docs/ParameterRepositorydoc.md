# IParameterRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Parameter
- **Primary Purpose**: Manages workflow parameters with complex validation, rules, auto-initialization, and cross-parameter dependencies
- **Key Relationships**: Child of Task and Checklist; complex relationships with validation rules, media, and parameter values
- **Performance Characteristics**: Very high query volume with complex JSON operations, validation queries, and dependency analysis
- **Business Context**: Core data capture and validation component that defines workflow inputs, validations, calculations, and business rules

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| type | type | Type.Parameter | false | null |
| target_entity_type | targetEntityType | Type.ParameterTargetEntityType | false | null |
| verification_type | verificationType | Type.VerificationType | false | NONE |
| label | label | String | true | null |
| description | description | String | true | null |
| order_tree | orderTree | Integer | false | null |
| is_mandatory | isMandatory | boolean | false | false |
| archived | archived | boolean | false | false |
| data | data | JsonNode | false | '{}' |
| tasks_id | task.id | Long | true | null |
| validations | validations | JsonNode | false | '[]' |
| checklists_id | checklistId | Long | false | null |
| is_auto_initialized | isAutoInitialized | boolean | false | false |
| auto_initialize | autoInitialize | JsonNode | true | null |
| rules | rules | JsonNode | true | null |
| hidden | hidden | boolean | false | false |
| metadata | metadata | JsonNode | true | '{}' |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @OneToMany | parameterValues | Set\<ParameterValue\> | LAZY | Parameter value history, cascade = ALL |
| @ManyToOne | task | Task | LAZY | Parent task, nullable |
| @ManyToOne | checklist | Checklist | LAZY | Parent checklist, not null, immutable |
| @OneToMany | medias | List\<ParameterMediaMapping\> | LAZY | Parameter media attachments, cascade = ALL |
| @OneToMany | impactedByRules | Set\<ParameterRuleMapping\> | LAZY | Parameters impacted by this parameter's rules, cascade = ALL |
| @OneToMany | triggeredByRules | Set\<ParameterRuleMapping\> | LAZY | Parameters that trigger rules on this parameter, cascade = ALL |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Parameter entity)`
- `deleteById(Long id)`
- `delete(Parameter entity)`
- `existsById(Long id)`
- `count()`

### Specification Methods
- `findAll(Specification<Parameter> spec)`
- `findAll(Specification<Parameter> spec, Pageable pageable)`
- `findAll(Specification<Parameter> spec, Sort sort)`
- `findOne(Specification<Parameter> spec)`
- `count(Specification<Parameter> spec)`

### Custom Query Methods (41 methods - ALL methods documented)

- `findByTaskIdInOrderByOrderTree(Set<Long> taskIds)`
- `getEnabledParametersCountByTypeAndIdIn(Set<Long> parameterIds, Set<Type.Parameter> types)`
- `getParametersByChecklistIdAndTargetEntityType(Long checklistId, Type.ParameterTargetEntityType targetEntityType)`
- `getArchivedParametersByReferencedParameterIds(List<Long> referencedParameterIds)`
- `updateParametersTargetEntityType(Long checklistId, Type.ParameterTargetEntityType targetEntityType, Type.ParameterTargetEntityType updatedTargetEntityType)`
- `getParametersCountByChecklistIdAndParameterIdInAndTargetEntityType(Long checklistId, Set<Long> parameterIds, Type.ParameterTargetEntityType targetEntityType)`
- `updateParametersTargetEntityType(Set<Long> parameterIds, Type.ParameterTargetEntityType targetEntityType)`
- `reorderParameter(Long parameterId, Integer order, Long userId, Long modifiedAt)`
- `updateParameterVisibility(Set<Long> hiddenParameterIds, Set<Long> visibleParameterIds)`
- `isLinkedParameterExistsByParameterId(Long checklistId, String parameterId)`
- `getChecklistIdsByObjectTypeInData(String objectTypeId)`
- `getResourceParametersByObjectTypeIdAndChecklistId(String objectTypeId, List<Long> checklistIds)`
- `findByChecklistIdAndArchived(Long checklistId, boolean isArchived)`
- `getAllParametersWhereParameterIsUsedInRules(String hideRulesJson, String showRulesJson, Long parameterId)`
- `getAllParametersWhereParameterIsUsedInPropertyFilters(String parameterId)`
- `getAllParametersWhereParameterIsUsedInPropertyValidations(String parameterId)`
- `getAllParametersWhereParameterIsUsedInResourceValidations(String parameterId)`
- `getNonHiddenAutoInitialisedParametersByTaskExecutionId(Long taskExecutionId)`
- `isParameterUsedInAutoInitialization(Long parameterId)`
- `getAllParametersWhereObjectTypePropertyIsUsedInPropertyFilters(String propertyId)`
- `getAllParametersWhereObjectTypePropertyIsUsedInValidation(String propertyId)`
- `getAllParametersWhereObjectTypePropertyIsUsedInPropertyValidation(String propertyId)`
- `getAllParametersWhereObjectTypeRelationIsUsedInPropertyFilters(String relationId)`
- `getChecklistAndTaskInfoByParameterId(Long parameterId)`
- `getChecklistAndTaskInfoByParameterIdForResourceValidation(Long parameterId)`
- `getParameterIdsByChecklistIdAndTargetEntityType(Long checklistId, Type.ParameterTargetEntityType targetEntityType)`
- `findAllByTypeAndChecklistIdInAndArchived(Type.Parameter type, Collection<Long> checklistId, boolean archived)`
- `getAllParameterIdsWhereParameterIsUsedInCalculation(String parameterId, Long checklistId)`
- `getAllParametersWhereParameterIsUsedInCreateObjectAutomations(String parameterId)`
- `getParameterIdWhereParameterIsUsedInLeastCount(String parameterId, Long checklistId)`
- `getAllParametersWherePropertyIdIsUsedIn(String propertyId)`
- `getParameterTargetEntityTypeByParameterIds(Set<Long> checklistIds)`
- `getParameterIdWhereParameterIsUsedInNumberCriteriaValidation(String parameterId, Long checklistId)`
- `existsByIdAndType(Long id, Type.Parameter parameter)`
- `findParameterIdsByTaskId(Long taskId)`
- `increaseOrderTreeByOneAfterParameter(Long taskId, Integer orderTree, Long parameterId)`
- `findAllByIdInAndArchived(Set<Long> parameterIds, boolean archived)`
- `getAllParametersWhereParameterIsUsedDateAndDateTimeValidations(String parameterId)`
- `findAllParametersWithRules()`
- `findAllParametersWithRulesForChecklistId(Long checklistId)`
- `existsByIdAndChecklistId(Long id, Long checklistId)`

## Method Documentation (Key Methods)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Parameter> findById(Long id)
List<Parameter> findAll()
Parameter save(Parameter entity)
void deleteById(Long id)
void delete(Parameter entity)
boolean existsById(Long id)
long count()
```

### Key Custom Query Methods

#### Method: findByTaskIdInOrderByOrderTree(Set<Long> taskIds)
```yaml
Signature: List<Parameter> findByTaskIdInOrderByOrderTree(Set<Long> taskIds)
Purpose: "Find all parameters within multiple tasks ordered by execution sequence"

Business Logic Derivation:
  1. Used extensively in checklist collaboration service for parameter processing
  2. Enables bulk parameter retrieval across multiple tasks in proper order
  3. Critical for cross-task parameter operations and workflow processing
  4. Supports task-aware parameter operations and batch processing
  5. Used in collaboration and audit operations for parameter context

SQL Query: |
  SELECT p.* FROM parameters p 
  WHERE p.tasks_id IN (?) AND p.archived = false
  ORDER BY p.order_tree

Parameters:
  - taskIds: Set<Long> (Task identifiers to find parameters for)

Returns: List<Parameter> (parameters from specified tasks in execution order)
Transaction: Not Required
Error Handling: Returns empty list if no parameters found in specified tasks
```

#### Method: getParametersByChecklistIdAndTargetEntityType(Long checklistId, Type.ParameterTargetEntityType targetEntityType)
```yaml
Signature: List<Parameter> getParametersByChecklistIdAndTargetEntityType(Long checklistId, Type.ParameterTargetEntityType targetEntityType)
Purpose: "Find parameters by checklist and target entity type for workflow operations"

Business Logic Derivation:
  1. Used extensively throughout system for parameter categorization
  2. Critical for distinguishing PROCESS vs TASK vs UNMAPPED parameters
  3. Enables checklist-scoped parameter operations by type
  4. Used in job creation, parameter mapping, and workflow execution
  5. Essential for parameter lifecycle management and categorization

SQL Query: |
  SELECT p.* FROM parameters p 
  WHERE p.checklists_id = ? AND p.target_entity_type = ? AND p.archived = false

Parameters:
  - checklistId: Long (Checklist identifier)
  - targetEntityType: Type.ParameterTargetEntityType (Parameter category filter)

Returns: List<Parameter> (parameters matching checklist and type)
Transaction: Not Required
Error Handling: Returns empty list if no parameters match criteria
```

#### Method: getAllParametersWhereParameterIsUsedInRules(String hideRulesJson, String showRulesJson, Long parameterId)
```yaml
Signature: List<IdView> getAllParametersWhereParameterIsUsedInRules(String hideRulesJson, String showRulesJson, Long parameterId)
Purpose: "Find parameters that reference this parameter in their visibility rules"

Business Logic Derivation:
  1. Used in parameter validation service for dependency analysis
  2. Critical for understanding parameter rule dependencies
  3. Enables impact analysis when modifying or deleting parameters
  4. Used to prevent deletion of parameters referenced in rules
  5. Essential for maintaining rule integrity and parameter dependencies

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND (p.rules::text LIKE '%"parameterId":"' || ? || '"%' 
         OR p.rules::text LIKE '%"parameters":["' || ? || '"]%'
         OR p.rules::text LIKE '%"parameters":[%"' || ? || '"%]%')

Parameters:
  - hideRulesJson: String (Hide rules JSON pattern)
  - showRulesJson: String (Show rules JSON pattern)
  - parameterId: Long (Parameter to find dependencies for)

Returns: List<IdView> (parameter IDs that reference this parameter in rules)
Transaction: Not Required
Error Handling: Returns empty list if no rule dependencies found
```

#### Method: isParameterUsedInAutoInitialization(Long parameterId)
```yaml
Signature: boolean isParameterUsedInAutoInitialization(Long parameterId)
Purpose: "Check if parameter is referenced in auto-initialization configurations"

Business Logic Derivation:
  1. Used in parameter execution handler for auto-initialization validation
  2. Critical for determining if parameter changes trigger auto-initialization
  3. Enables auto-initialization dependency management
  4. Used to prevent deletion of parameters used in auto-initialization
  5. Essential for maintaining auto-initialization integrity

SQL Query: |
  SELECT COUNT(*) > 0 FROM parameters p 
  WHERE p.archived = false
    AND p.is_auto_initialized = true
    AND p.auto_initialize::text LIKE '%"parameterId":"' || ? || '"%'

Parameters:
  - parameterId: Long (Parameter to check auto-initialization usage for)

Returns: boolean (true if parameter is used in auto-initialization)
Transaction: Not Required
Error Handling: Returns false if parameter not used in auto-initialization
```

#### Method: updateParameterVisibility(Set<Long> hiddenParameterIds, Set<Long> visibleParameterIds)
```yaml
Signature: void updateParameterVisibility(Set<Long> hiddenParameterIds, Set<Long> visibleParameterIds)
Purpose: "Bulk update parameter visibility for rule-based show/hide operations"

Business Logic Derivation:
  1. Used in parameter service for rule execution and visibility management
  2. Enables efficient bulk visibility updates for rule processing
  3. Critical for parameter rule execution and UI state management
  4. Supports dynamic parameter visibility based on rule conditions
  5. Used in workflow execution for conditional parameter display

SQL Query: |
  UPDATE parameters 
  SET hidden = CASE 
    WHEN id = ANY(?) THEN true 
    WHEN id = ANY(?) THEN false 
    ELSE hidden 
  END 
  WHERE id = ANY(? || ?)

Parameters:
  - hiddenParameterIds: Set<Long> (Parameters to hide)
  - visibleParameterIds: Set<Long> (Parameters to show)

Returns: void
Transaction: Required (uses @Modifying)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: getEnabledParametersCountByTypeAndIdIn(Set<Long> parameterIds, Set<Type.Parameter> types)
```yaml
Signature: Integer getEnabledParametersCountByTypeAndIdIn(Set<Long> parameterIds, Set<Type.Parameter> types)
Purpose: "Count parameters of specific types from given parameter IDs for validation"

Business Logic Derivation:
  1. Used in ChecklistService for parameter type validation during operations
  2. Validates that specific parameter sets contain only allowed parameter types
  3. Critical for ensuring parameter type compatibility in calculations and validations
  4. Used in parameter validation workflows to enforce type constraints
  5. Enables bulk parameter type validation for workflow integrity

SQL Query: |
  SELECT COUNT(*) FROM parameters p 
  WHERE p.id IN (?) AND p.type IN (?) AND p.archived = false

Parameters:
  - parameterIds: Set<Long> (Parameter identifiers to check)
  - types: Set<Type.Parameter> (Allowed parameter types)

Returns: Integer (count of parameters matching type criteria)
Transaction: Not Required
Error Handling: Returns 0 if no parameters match criteria
```

#### Method: getArchivedParametersByReferencedParameterIds(List<Long> referencedParameterIds)
```yaml
Signature: List<Parameter> getArchivedParametersByReferencedParameterIds(List<Long> referencedParameterIds)
Purpose: "Find archived parameters that reference specific parameter IDs in their configuration"

Business Logic Derivation:
  1. Used in ChecklistService for archived parameter validation
  2. Identifies archived parameters that still reference active parameters
  3. Critical for maintaining parameter reference integrity
  4. Used to prevent operations that would break archived parameter references
  5. Enables validation of parameter dependencies across archive states

SQL Query: |
  SELECT p.* FROM parameters p 
  WHERE p.archived = true
    AND (p.data::text ~ ('.*"referencedParameterId":"(' || array_to_string(?, '|') || ')".*')
         OR p.validations::text ~ ('.*"parameterId":"(' || array_to_string(?, '|') || ')".*')
         OR p.auto_initialize::text ~ ('.*"parameterId":"(' || array_to_string(?, '|') || ')".*'))

Parameters:
  - referencedParameterIds: List<Long> (Parameter IDs to find references for)

Returns: List<Parameter> (archived parameters referencing the given parameters)
Transaction: Not Required
Error Handling: Returns empty list if no archived references found
```

#### Method: updateParametersTargetEntityType(Long checklistId, Type.ParameterTargetEntityType targetEntityType, Type.ParameterTargetEntityType updatedTargetEntityType)
```yaml
Signature: void updateParametersTargetEntityType(Long checklistId, Type.ParameterTargetEntityType targetEntityType, Type.ParameterTargetEntityType updatedTargetEntityType)
Purpose: "Bulk update parameter target entity types for checklist-wide parameter remapping"

Business Logic Derivation:
  1. Used in ChecklistService for parameter mapping operations
  2. Enables bulk conversion of parameter categories (PROCESS to UNMAPPED, etc.)
  3. Critical for parameter lifecycle management during workflow changes
  4. Used in job parameter mapping workflows for category management
  5. Supports checklist-wide parameter categorization updates

SQL Query: |
  UPDATE parameters 
  SET target_entity_type = ? 
  WHERE checklists_id = ? AND target_entity_type = ? AND archived = false

Parameters:
  - checklistId: Long (Checklist to update parameters in)
  - targetEntityType: Type.ParameterTargetEntityType (Current target entity type)
  - updatedTargetEntityType: Type.ParameterTargetEntityType (New target entity type)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: getParametersCountByChecklistIdAndParameterIdInAndTargetEntityType(Long checklistId, Set<Long> parameterIds, Type.ParameterTargetEntityType targetEntityType)
```yaml
Signature: Integer getParametersCountByChecklistIdAndParameterIdInAndTargetEntityType(Long checklistId, Set<Long> parameterIds, Type.ParameterTargetEntityType targetEntityType)
Purpose: "Count parameters matching specific criteria for validation before mapping operations"

Business Logic Derivation:
  1. Used in ChecklistService for parameter mapping validation
  2. Validates parameter eligibility before mapping operations
  3. Critical for ensuring only correct parameters are included in mapping
  4. Used to verify parameter counts match expectations during mapping
  5. Enables validation of parameter mapping preconditions

SQL Query: |
  SELECT COUNT(*) FROM parameters p 
  WHERE p.checklists_id = ? AND p.id IN (?) AND p.target_entity_type = ? AND p.archived = false

Parameters:
  - checklistId: Long (Checklist identifier)
  - parameterIds: Set<Long> (Parameter identifiers to count)
  - targetEntityType: Type.ParameterTargetEntityType (Target entity type filter)

Returns: Integer (count of parameters matching all criteria)
Transaction: Not Required
Error Handling: Returns 0 if no parameters match criteria
```

#### Method: updateParametersTargetEntityType(Set<Long> parameterIds, Type.ParameterTargetEntityType targetEntityType)
```yaml
Signature: Integer updateParametersTargetEntityType(Set<Long> parameterIds, Type.ParameterTargetEntityType targetEntityType)
Purpose: "Update target entity type for specific parameter IDs during mapping operations"

Business Logic Derivation:
  1. Used in ChecklistService for selective parameter remapping
  2. Enables targeted parameter category changes for specific parameters
  3. Critical for parameter mapping workflows and job parameter assignment
  4. Used to promote UNMAPPED parameters to PROCESS parameters
  5. Supports fine-grained parameter categorization management

SQL Query: |
  UPDATE parameters 
  SET target_entity_type = ? 
  WHERE id IN (?) AND archived = false

Parameters:
  - parameterIds: Set<Long> (Parameter identifiers to update)
  - targetEntityType: Type.ParameterTargetEntityType (New target entity type)

Returns: Integer (number of parameters updated)
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: reorderParameter(Long parameterId, Integer order, Long userId, Long modifiedAt)
```yaml
Signature: void reorderParameter(Long parameterId, Integer order, Long userId, Long modifiedAt)
Purpose: "Update parameter order position and audit metadata for workflow reordering"

Business Logic Derivation:
  1. Used in ParameterService for parameter reordering operations
  2. Atomically updates order position and audit fields in single operation
  3. Critical for workflow reorganization and parameter sequence management
  4. Maintains audit trail during parameter reordering operations
  5. Enables efficient batch reordering without loading full entities

SQL Query: |
  UPDATE parameters 
  SET order_tree = ?, modified_by = ?, modified_at = ? 
  WHERE id = ?

Parameters:
  - parameterId: Long (Parameter identifier to reorder)
  - order: Integer (New order position for the parameter)
  - userId: Long (User performing the reorder operation)
  - modifiedAt: Long (Timestamp for modification tracking)

Returns: void
Transaction: Required (uses @Transactional and @Modifying)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: isLinkedParameterExistsByParameterId(Long checklistId, String parameterId)
```yaml
Signature: boolean isLinkedParameterExistsByParameterId(Long checklistId, String parameterId)
Purpose: "Check if parameter is linked or referenced by other parameters in the checklist"

Business Logic Derivation:
  1. Used in parameter validation service for dependency analysis
  2. Determines if parameter deletion would break links to other parameters
  3. Critical for maintaining parameter relationship integrity
  4. Used to prevent deletion of parameters with active dependencies
  5. Enables validation of parameter interconnections before modifications

SQL Query: |
  SELECT COUNT(*) > 0 FROM parameters p 
  WHERE p.checklists_id = ? AND p.archived = false
    AND (p.data::text LIKE '%"linkedParameterId":"' || ? || '"%'
         OR p.validations::text LIKE '%"linkedParameterId":"' || ? || '"%'
         OR p.auto_initialize::text LIKE '%"linkedParameterId":"' || ? || '"%')

Parameters:
  - checklistId: Long (Checklist identifier)
  - parameterId: String (Parameter ID to check for links)

Returns: boolean (true if linked parameter exists)
Transaction: Not Required
Error Handling: Returns false if no linked parameters found
```

#### Method: getChecklistIdsByObjectTypeInData(String objectTypeId)
```yaml
Signature: Set<Long> getChecklistIdsByObjectTypeInData(String objectTypeId)
Purpose: "Find checklist IDs containing parameters with specific object type in their data"

Business Logic Derivation:
  1. Used in ChecklistService for object type impact analysis
  2. Identifies checklists affected by object type changes
  3. Critical for understanding scope of object type modifications
  4. Used in object type validation and migration operations
  5. Enables checklist-level impact assessment for object type changes

SQL Query: |
  SELECT DISTINCT p.checklists_id FROM parameters p 
  WHERE p.archived = false
    AND p.data::text LIKE '%"objectTypeId":"' || ? || '"%'

Parameters:
  - objectTypeId: String (Object type identifier to find)

Returns: Set<Long> (checklist IDs containing the object type)
Transaction: Not Required
Error Handling: Returns empty set if no checklists contain the object type
```

#### Method: getResourceParametersByObjectTypeIdAndChecklistId(String objectTypeId, List<Long> checklistIds)
```yaml
Signature: List<ParameterView> getResourceParametersByObjectTypeIdAndChecklistId(String objectTypeId, List<Long> checklistIds)
Purpose: "Get resource parameters with specific object type across multiple checklists"

Business Logic Derivation:
  1. Used in ChecklistService for resource parameter analysis
  2. Retrieves resource parameters filtered by object type and checklist scope
  3. Critical for object type validation and resource parameter management
  4. Used in cross-checklist object type operations and validation
  5. Enables bulk resource parameter analysis for object type changes

SQL Query: |
  SELECT p.id, p.label, p.checklists_id as checklistId, p.type, p.data 
  FROM parameters p 
  WHERE p.checklists_id IN (?) AND p.archived = false
    AND p.type IN ('RESOURCE', 'MULTI_RESOURCE')
    AND p.data::text LIKE '%"objectTypeId":"' || ? || '"%'

Parameters:
  - objectTypeId: String (Object type identifier)
  - checklistIds: List<Long> (Checklist identifiers to search in)

Returns: List<ParameterView> (resource parameter projection views)
Transaction: Not Required
Error Handling: Returns empty list if no resource parameters found
```

#### Method: findByChecklistIdAndArchived(Long checklistId, boolean isArchived)
```yaml
Signature: List<Parameter> findByChecklistIdAndArchived(Long checklistId, boolean isArchived)
Purpose: "Find parameters by checklist and archive status for lifecycle operations"

Business Logic Derivation:
  1. Used in job log service and migration operations for parameter retrieval
  2. Enables checklist-scoped parameter operations with archive filtering
  3. Critical for parameter lifecycle management and migration workflows
  4. Used in job creation and parameter processing operations
  5. Supports checklist-wide parameter analysis and processing

SQL Query: |
  SELECT p.* FROM parameters p 
  WHERE p.checklists_id = ? AND p.archived = ?

Parameters:
  - checklistId: Long (Checklist identifier)
  - isArchived: boolean (Archive status filter)

Returns: List<Parameter> (parameters matching checklist and archive criteria)
Transaction: Not Required
Error Handling: Returns empty list if no parameters match criteria
```

#### Method: getAllParametersWhereParameterIsUsedInPropertyFilters(String parameterId)
```yaml
Signature: List<IdView> getAllParametersWhereParameterIsUsedInPropertyFilters(String parameterId)
Purpose: "Find parameters that reference this parameter in their property filter configurations"

Business Logic Derivation:
  1. Used in parameter validation service for property filter dependency analysis
  2. Critical for understanding parameter dependencies in resource filtering operations
  3. Enables impact analysis when modifying or deleting parameters used in filters
  4. Used to prevent deletion of parameters referenced in property filters
  5. Essential for maintaining property filter integrity and parameter dependencies

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND p.data::text LIKE '%"filterId":"' || ? || '"%'
    OR p.data::text LIKE '%"propertyFilters":%"parameterId":"' || ? || '"%'

Parameters:
  - parameterId: String (Parameter ID to find property filter dependencies for)

Returns: List<IdView> (parameter IDs that reference this parameter in property filters)
Transaction: Not Required
Error Handling: Returns empty list if no property filter dependencies found
```

#### Method: getAllParametersWhereParameterIsUsedInPropertyValidations(String parameterId)
```yaml
Signature: List<IdView> getAllParametersWhereParameterIsUsedInPropertyValidations(String parameterId)
Purpose: "Find parameters that reference this parameter in their property validation configurations"

Business Logic Derivation:
  1. Used in parameter validation service for property validation dependency analysis
  2. Critical for understanding parameter dependencies in validation operations
  3. Enables impact analysis when modifying or deleting parameters used in validations
  4. Used to prevent deletion of parameters referenced in property validations
  5. Essential for maintaining property validation integrity and parameter dependencies

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND (p.validations::text LIKE '%"propertyValidations":%"parameterId":"' || ? || '"%'
         OR p.data::text LIKE '%"propertyValidations":%"parameterId":"' || ? || '"%')

Parameters:
  - parameterId: String (Parameter ID to find property validation dependencies for)

Returns: List<IdView> (parameter IDs that reference this parameter in property validations)
Transaction: Not Required
Error Handling: Returns empty list if no property validation dependencies found
```

#### Method: getAllParametersWhereParameterIsUsedInResourceValidations(String parameterId)
```yaml
Signature: List<IdView> getAllParametersWhereParameterIsUsedInResourceValidations(String parameterId)
Purpose: "Find parameters that reference this parameter in their resource validation configurations"

Business Logic Derivation:
  1. Used in parameter validation service for resource validation dependency analysis
  2. Critical for understanding parameter dependencies in resource validation operations
  3. Enables impact analysis when modifying or deleting parameters used in resource validations
  4. Used to prevent deletion of parameters referenced in resource validations
  5. Essential for maintaining resource validation integrity and parameter dependencies

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND (p.validations::text LIKE '%"resourceValidations":%"parameterId":"' || ? || '"%'
         OR p.data::text LIKE '%"resourceValidations":%"parameterId":"' || ? || '"%')

Parameters:
  - parameterId: String (Parameter ID to find resource validation dependencies for)

Returns: List<IdView> (parameter IDs that reference this parameter in resource validations)
Transaction: Not Required
Error Handling: Returns empty list if no resource validation dependencies found
```

#### Method: getNonHiddenAutoInitialisedParametersByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: List<AutoInitializeParameterView> getNonHiddenAutoInitialisedParametersByTaskExecutionId(Long taskExecutionId)
Purpose: "Get visible auto-initialized parameters for a specific task execution"

Business Logic Derivation:
  1. Used in task execution service for auto-initialization processing
  2. Retrieves parameters that should be auto-initialized during task execution
  3. Critical for workflow automation and parameter auto-initialization logic
  4. Filters out hidden parameters to show only relevant auto-initialized parameters
  5. Enables task execution automation with proper parameter initialization

SQL Query: |
  SELECT p.id, p.label, p.type, p.auto_initialize, te.id as taskExecutionId
  FROM parameters p
  INNER JOIN tasks t ON p.tasks_id = t.id
  INNER JOIN task_executions te ON t.id = te.tasks_id
  WHERE te.id = ? AND p.archived = false AND p.hidden = false 
    AND p.is_auto_initialized = true

Parameters:
  - taskExecutionId: Long (Task execution identifier)

Returns: List<AutoInitializeParameterView> (auto-initialize parameter projection views)
Transaction: Not Required
Error Handling: Returns empty list if no auto-initialized parameters found for task execution
```

#### Method: getAllParametersWhereObjectTypePropertyIsUsedInPropertyFilters(String propertyId)
```yaml
Signature: List<IdView> getAllParametersWhereObjectTypePropertyIsUsedInPropertyFilters(String propertyId)
Purpose: "Find parameters that use specific object type property in their property filter configurations"

Business Logic Derivation:
  1. Used in object type service for property impact analysis
  2. Identifies parameters affected by object type property changes
  3. Critical for understanding scope of object type property modifications
  4. Used in object type property validation and migration operations
  5. Enables parameter-level impact assessment for property changes

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND p.data::text LIKE '%"propertyId":"' || ? || '"%'
    AND p.data::text LIKE '%"propertyFilters"%'

Parameters:
  - propertyId: String (Object type property identifier)

Returns: List<IdView> (parameter IDs using the property in filters)
Transaction: Not Required
Error Handling: Returns empty list if no parameters use the property in filters
```

#### Method: getAllParametersWhereObjectTypePropertyIsUsedInValidation(String propertyId)
```yaml
Signature: List<IdView> getAllParametersWhereObjectTypePropertyIsUsedInValidation(String propertyId)
Purpose: "Find parameters that use specific object type property in their validation configurations"

Business Logic Derivation:
  1. Used in object type service for property validation impact analysis
  2. Identifies parameters affected by object type property validation changes
  3. Critical for understanding scope of property validation modifications
  4. Used in object type property validation migration operations
  5. Enables parameter-level validation impact assessment for property changes

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND (p.validations::text LIKE '%"propertyId":"' || ? || '"%'
         OR p.data::text LIKE '%"validations":%"propertyId":"' || ? || '"%')

Parameters:
  - propertyId: String (Object type property identifier)

Returns: List<IdView> (parameter IDs using the property in validations)
Transaction: Not Required
Error Handling: Returns empty list if no parameters use the property in validations
```

#### Method: getAllParametersWhereObjectTypePropertyIsUsedInPropertyValidation(String propertyId)
```yaml
Signature: List<IdView> getAllParametersWhereObjectTypePropertyIsUsedInPropertyValidation(String propertyId)
Purpose: "Find parameters that use specific object type property in their property validation rules"

Business Logic Derivation:
  1. Used in object type service for property validation rule impact analysis
  2. Identifies parameters with property validation rules affected by property changes
  3. Critical for understanding property validation rule dependencies
  4. Used in object type property validation rule migration operations
  5. Enables detailed property validation impact assessment

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND p.validations::text LIKE '%"propertyValidations":%"propertyId":"' || ? || '"%'

Parameters:
  - propertyId: String (Object type property identifier)

Returns: List<IdView> (parameter IDs using the property in property validation rules)
Transaction: Not Required
Error Handling: Returns empty list if no parameters use the property in property validations
```

#### Method: getAllParametersWhereObjectTypeRelationIsUsedInPropertyFilters(String relationId)
```yaml
Signature: List<IdView> getAllParametersWhereObjectTypeRelationIsUsedInPropertyFilters(String relationId)
Purpose: "Find parameters that use specific object type relation in their property filter configurations"

Business Logic Derivation:
  1. Used in object type service for relation impact analysis
  2. Identifies parameters affected by object type relation changes
  3. Critical for understanding scope of object type relation modifications
  4. Used in object type relation validation and migration operations
  5. Enables parameter-level impact assessment for relation changes

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND p.data::text LIKE '%"relationId":"' || ? || '"%'
    AND p.data::text LIKE '%"propertyFilters"%'

Parameters:
  - relationId: String (Object type relation identifier)

Returns: List<IdView> (parameter IDs using the relation in property filters)
Transaction: Not Required
Error Handling: Returns empty list if no parameters use the relation in filters
```

#### Method: getChecklistAndTaskInfoByParameterId(Long parameterId)
```yaml
Signature: ObjectPropertyRelationChecklistView getChecklistAndTaskInfoByParameterId(Long parameterId)
Purpose: "Get checklist and task context information for a specific parameter"

Business Logic Derivation:
  1. Used in object type service for parameter context retrieval
  2. Provides checklist and task context for parameter operations
  3. Critical for parameter validation and error message generation
  4. Used in parameter dependency analysis and validation workflows
  5. Enables context-aware parameter operations and user feedback

SQL Query: |
  SELECT c.name as checklistName, c.id as checklistId, t.name as taskName, t.id as taskId,
         p.label as parameterLabel, p.id as parameterId
  FROM parameters p
  INNER JOIN checklists c ON p.checklists_id = c.id
  LEFT JOIN tasks t ON p.tasks_id = t.id
  WHERE p.id = ?

Parameters:
  - parameterId: Long (Parameter identifier)

Returns: ObjectPropertyRelationChecklistView (parameter context projection view)
Transaction: Not Required
Error Handling: Returns null if parameter not found
```

#### Method: getChecklistAndTaskInfoByParameterIdForResourceValidation(Long parameterId)
```yaml
Signature: ObjectPropertyRelationChecklistView getChecklistAndTaskInfoByParameterIdForResourceValidation(Long parameterId)
Purpose: "Get checklist and task context information for parameter resource validation operations"

Business Logic Derivation:
  1. Used in parameter validation service for resource validation context
  2. Provides specialized context for resource validation error messages
  3. Critical for resource validation parameter operations and user feedback
  4. Used in resource validation dependency analysis workflows
  5. Enables context-aware resource validation operations and messaging

SQL Query: |
  SELECT c.name as checklistName, c.id as checklistId, t.name as taskName, t.id as taskId,
         p.label as parameterLabel, p.id as parameterId
  FROM parameters p
  INNER JOIN checklists c ON p.checklists_id = c.id
  LEFT JOIN tasks t ON p.tasks_id = t.id
  WHERE p.id = ? AND p.type IN ('RESOURCE', 'MULTI_RESOURCE')

Parameters:
  - parameterId: Long (Parameter identifier for resource validation)

Returns: ObjectPropertyRelationChecklistView (parameter context projection view for resource validation)
Transaction: Not Required
Error Handling: Returns null if parameter not found or not a resource parameter
```

#### Method: getParameterIdsByChecklistIdAndTargetEntityType(Long checklistId, Type.ParameterTargetEntityType targetEntityType)
```yaml
Signature: Set<Long> getParameterIdsByChecklistIdAndTargetEntityType(Long checklistId, Type.ParameterTargetEntityType targetEntityType)
Purpose: "Get parameter IDs by checklist and target entity type for bulk operations"

Business Logic Derivation:
  1. Used in ChecklistService for efficient parameter ID retrieval
  2. Enables memory-efficient bulk parameter operations
  3. Critical for parameter mapping and categorization operations
  4. Used in job parameter mapping workflows for ID-based processing
  5. Supports efficient parameter identification without loading full entities

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.checklists_id = ? AND p.target_entity_type = ? AND p.archived = false

Parameters:
  - checklistId: Long (Checklist identifier)
  - targetEntityType: Type.ParameterTargetEntityType (Target entity type filter)

Returns: Set<Long> (parameter IDs matching criteria)
Transaction: Not Required
Error Handling: Returns empty set if no parameters match criteria
```

#### Method: findAllByTypeAndChecklistIdInAndArchived(Type.Parameter type, Collection<Long> checklistId, boolean archived)
```yaml
Signature: List<Parameter> findAllByTypeAndChecklistIdInAndArchived(Type.Parameter type, Collection<Long> checklistId, boolean archived)
Purpose: "Find parameters by type across multiple checklists with archive filtering"

Business Logic Derivation:
  1. Used in import/export service for material parameter processing
  2. Enables bulk parameter retrieval by type across multiple checklists
  3. Critical for cross-checklist parameter operations and migration
  4. Used in material parameter analysis and processing workflows
  5. Supports multi-checklist parameter operations with type filtering

SQL Query: |
  SELECT p.* FROM parameters p 
  WHERE p.type = ? AND p.checklists_id IN (?) AND p.archived = ?

Parameters:
  - type: Type.Parameter (Parameter type filter)
  - checklistId: Collection<Long> (Checklist identifiers)
  - archived: boolean (Archive status filter)

Returns: List<Parameter> (parameters matching type, checklists, and archive criteria)
Transaction: Not Required
Error Handling: Returns empty list if no parameters match criteria
```

#### Method: getAllParameterIdsWhereParameterIsUsedInCalculation(String parameterId, Long checklistId)
```yaml
Signature: List<IdView> getAllParameterIdsWhereParameterIsUsedInCalculation(String parameterId, Long checklistId)
Purpose: "Find parameters that reference this parameter in calculation configurations"

Business Logic Derivation:
  1. Used in parameter validation service for calculation dependency analysis
  2. Critical for understanding parameter dependencies in calculation operations
  3. Enables impact analysis when modifying or deleting parameters used in calculations
  4. Used to prevent deletion of parameters referenced in calculations
  5. Essential for maintaining calculation integrity and parameter dependencies

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.checklists_id = ? AND p.archived = false
    AND p.data::text LIKE '%"calculation":%"parameterId":"' || ? || '"%'

Parameters:
  - parameterId: String (Parameter ID to find calculation dependencies for)
  - checklistId: Long (Checklist scope for search)

Returns: List<IdView> (parameter IDs that reference this parameter in calculations)
Transaction: Not Required
Error Handling: Returns empty list if no calculation dependencies found
```

#### Method: getAllParametersWhereParameterIsUsedInCreateObjectAutomations(String parameterId)
```yaml
Signature: List<IdView> getAllParametersWhereParameterIsUsedInCreateObjectAutomations(String parameterId)
Purpose: "Find parameters that reference this parameter in create object automation configurations"

Business Logic Derivation:
  1. Used in parameter validation service for automation dependency analysis
  2. Critical for understanding parameter dependencies in object creation automations
  3. Enables impact analysis when modifying or deleting parameters used in automations
  4. Used to prevent deletion of parameters referenced in create object automations
  5. Essential for maintaining automation integrity and parameter dependencies

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND p.data::text LIKE '%"createObjectAutomation":%"parameterId":"' || ? || '"%'

Parameters:
  - parameterId: String (Parameter ID to find automation dependencies for)

Returns: List<IdView> (parameter IDs that reference this parameter in create object automations)
Transaction: Not Required
Error Handling: Returns empty list if no automation dependencies found
```

#### Method: getParameterIdWhereParameterIsUsedInLeastCount(String parameterId, Long checklistId)
```yaml
Signature: List<IdView> getParameterIdWhereParameterIsUsedInLeastCount(String parameterId, Long checklistId)
Purpose: "Find parameters that reference this parameter in least count configurations"

Business Logic Derivation:
  1. Used in parameter validation service for least count dependency analysis
  2. Critical for understanding parameter dependencies in least count operations
  3. Enables impact analysis when modifying or deleting parameters used in least count
  4. Used to prevent deletion of parameters referenced in least count calculations
  5. Essential for maintaining least count integrity and parameter dependencies

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.checklists_id = ? AND p.archived = false
    AND p.data::text LIKE '%"leastCount":%"parameterId":"' || ? || '"%'

Parameters:
  - parameterId: String (Parameter ID to find least count dependencies for)
  - checklistId: Long (Checklist scope for search)

Returns: List<IdView> (parameter IDs that reference this parameter in least count)
Transaction: Not Required
Error Handling: Returns empty list if no least count dependencies found
```

#### Method: getAllParametersWherePropertyIdIsUsedIn(String propertyId)
```yaml
Signature: List<IdView> getAllParametersWherePropertyIdIsUsedIn(String propertyId)
Purpose: "Find parameters that use specific property ID in their configurations"

Business Logic Derivation:
  1. Used in object type service for property usage analysis
  2. Identifies parameters affected by property changes
  3. Critical for understanding scope of property modifications
  4. Used in property validation and migration operations
  5. Enables parameter-level impact assessment for property changes

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND p.data::text LIKE '%"propertyId":"' || ? || '"%'

Parameters:
  - propertyId: String (Property identifier to find usage for)

Returns: List<IdView> (parameter IDs using the property)
Transaction: Not Required
Error Handling: Returns empty list if no parameters use the property
```

#### Method: getParameterTargetEntityTypeByParameterIds(Set<Long> checklistIds)
```yaml
Signature: List<Long> getParameterTargetEntityTypeByParameterIds(Set<Long> checklistIds)
Purpose: "Get parameter target entity types for multiple checklists for analysis"

Business Logic Derivation:
  1. Used in job service for parameter categorization analysis
  2. Provides parameter target entity type information across checklists
  3. Critical for cross-checklist parameter analysis and reporting
  4. Used in parameter categorization workflows and validation
  5. Enables bulk parameter categorization analysis

SQL Query: |
  SELECT DISTINCT CAST(p.target_entity_type AS INTEGER) FROM parameters p 
  WHERE p.checklists_id IN (?) AND p.archived = false

Parameters:
  - checklistIds: Set<Long> (Checklist identifiers to analyze)

Returns: List<Long> (distinct target entity type values as integers)
Transaction: Not Required
Error Handling: Returns empty list if no parameters found in checklists
```

#### Method: getParameterIdWhereParameterIsUsedInNumberCriteriaValidation(String parameterId, Long checklistId)
```yaml
Signature: List<IdView> getParameterIdWhereParameterIsUsedInNumberCriteriaValidation(String parameterId, Long checklistId)
Purpose: "Find parameters that reference this parameter in number criteria validation configurations"

Business Logic Derivation:
  1. Used in parameter validation service for number criteria dependency analysis
  2. Critical for understanding parameter dependencies in number validation operations
  3. Enables impact analysis when modifying or deleting parameters used in number criteria
  4. Used to prevent deletion of parameters referenced in number criteria validations
  5. Essential for maintaining number criteria validation integrity

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.checklists_id = ? AND p.archived = false
    AND p.validations::text LIKE '%"numberCriteriaValidation":%"parameterId":"' || ? || '"%'

Parameters:
  - parameterId: String (Parameter ID to find number criteria dependencies for)
  - checklistId: Long (Checklist scope for search)

Returns: List<IdView> (parameter IDs that reference this parameter in number criteria validations)
Transaction: Not Required
Error Handling: Returns empty list if no number criteria dependencies found
```

#### Method: existsByIdAndType(Long id, Type.Parameter parameter)
```yaml
Signature: boolean existsByIdAndType(Long id, Type.Parameter parameter)
Purpose: "Check if parameter exists with specific ID and type for validation"

Business Logic Derivation:
  1. Used in parameter execution handler for parameter type validation
  2. Validates parameter existence and type before operations
  3. Critical for parameter type checking and validation workflows
  4. Used to ensure parameter operations are performed on correct parameter types
  5. Enables type-safe parameter operations and validation

SQL Query: |
  SELECT COUNT(*) > 0 FROM parameters p 
  WHERE p.id = ? AND p.type = ? AND p.archived = false

Parameters:
  - id: Long (Parameter identifier)
  - parameter: Type.Parameter (Parameter type to validate)

Returns: boolean (true if parameter exists with specified type)
Transaction: Not Required
Error Handling: Returns false if parameter doesn't exist or has different type
```

#### Method: findParameterIdsByTaskId(Long taskId)
```yaml
Signature: Set<Long> findParameterIdsByTaskId(Long taskId)
Purpose: "Get parameter IDs for a specific task for bulk operations"

Business Logic Derivation:
  1. Used in element copy service for task copying operations
  2. Provides efficient parameter ID retrieval for bulk processing
  3. Critical for task-level operations that need parameter context
  4. Enables memory-efficient bulk parameter operations
  5. Used for parameter copying and dependency management

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.tasks_id = ? AND p.archived = false

Parameters:
  - taskId: Long (Task identifier to get parameter IDs for)

Returns: Set<Long> (parameter IDs within the task)
Transaction: Not Required
Error Handling: Returns empty set if no parameters found in task
```

#### Method: increaseOrderTreeByOneAfterParameter(Long taskId, Integer orderTree, Long parameterId)
```yaml
Signature: void increaseOrderTreeByOneAfterParameter(Long taskId, Integer orderTree, Long parameterId)
Purpose: "Shift parameter order positions to make room for new parameter insertion"

Business Logic Derivation:
  1. Used in element copy service for parameter duplication operations
  2. Creates space in parameter ordering sequence for new parameter insertion
  3. Critical for maintaining proper parameter order during parameter creation
  4. Enables parameter insertion at specific positions without order conflicts
  5. Supports parameter copying and workflow reorganization operations

SQL Query: |
  UPDATE parameters 
  SET order_tree = order_tree + 1 
  WHERE tasks_id = ? 
    AND order_tree > ? 
    AND id != ?

Parameters:
  - taskId: Long (Task to perform order shifting in)
  - orderTree: Integer (Order position threshold for shifting)
  - parameterId: Long (New parameter ID to exclude from shifting)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findAllByIdInAndArchived(Set<Long> parameterIds, boolean archived)
```yaml
Signature: List<Parameter> findAllByIdInAndArchived(Set<Long> parameterIds, boolean archived)
Purpose: "Find parameters by multiple IDs with archive status filtering"

Business Logic Derivation:
  1. Used in element copy service for parameter copying operations
  2. Enables bulk parameter retrieval with archive status filtering
  3. Critical for parameter copying workflows that need active parameters only
  4. Supports parameter lifecycle management and copying operations
  5. Used for filtering out archived parameters during copying operations

SQL Query: |
  SELECT p.* FROM parameters p 
  WHERE p.id IN (?) AND p.archived = ?

Parameters:
  - parameterIds: Set<Long> (Parameter identifiers to retrieve)
  - archived: boolean (Archive status filter)

Returns: List<Parameter> (parameters matching IDs and archive status)
Transaction: Not Required
Error Handling: Returns empty list if no parameters match criteria
```

#### Method: getAllParametersWhereParameterIsUsedDateAndDateTimeValidations(String parameterId)
```yaml
Signature: List<IdView> getAllParametersWhereParameterIsUsedDateAndDateTimeValidations(String parameterId)
Purpose: "Find parameters that reference this parameter in date and datetime validation configurations"

Business Logic Derivation:
  1. Used in parameter validation service for date/datetime dependency analysis
  2. Critical for understanding parameter dependencies in date/datetime validation operations
  3. Enables impact analysis when modifying or deleting parameters used in date validations
  4. Used to prevent deletion of parameters referenced in date/datetime validations
  5. Essential for maintaining date/datetime validation integrity

SQL Query: |
  SELECT p.id FROM parameters p 
  WHERE p.archived = false
    AND (p.validations::text LIKE '%"dateValidation":%"parameterId":"' || ? || '"%'
         OR p.validations::text LIKE '%"dateTimeValidation":%"parameterId":"' || ? || '"%')

Parameters:
  - parameterId: String (Parameter ID to find date/datetime validation dependencies for)

Returns: List<IdView> (parameter IDs that reference this parameter in date/datetime validations)
Transaction: Not Required
Error Handling: Returns empty list if no date/datetime validation dependencies found
```

#### Method: findAllParametersWithRules()
```yaml
Signature: List<Parameter> findAllParametersWithRules()
Purpose: "Find all parameters that have rule configurations for rule processing"

Business Logic Derivation:
  1. Used in rules migration for rule processing and analysis
  2. Identifies parameters with rule configurations across the system
  3. Critical for rule migration and rule processing workflows
  4. Used in rule validation and rule dependency analysis
  5. Enables system-wide rule processing and migration operations

SQL Query: |
  SELECT * FROM parameters 
  WHERE rules IS NOT NULL AND CAST(rules AS TEXT) <> 'null'

Parameters: None

Returns: List<Parameter> (parameters with rule configurations)
Transaction: Not Required
Error Handling: Returns empty list if no parameters have rules
```

#### Method: findAllParametersWithRulesForChecklistId(Long checklistId)
```yaml
Signature: List<Parameter> findAllParametersWithRulesForChecklistId(Long checklistId)
Purpose: "Find parameters with rule configurations within a specific checklist"

Business Logic Derivation:
  1. Used in rules migration for checklist-scoped rule processing
  2. Identifies parameters with rule configurations within specific checklist
  3. Critical for checklist-specific rule migration and processing workflows
  4. Used in checklist rule validation and rule dependency analysis
  5. Enables checklist-scoped rule processing and migration operations

SQL Query: |
  SELECT * FROM parameters 
  WHERE rules IS NOT NULL AND CAST(rules AS TEXT) <> 'null' AND checklists_id = ?

Parameters:
  - checklistId: Long (Checklist identifier to scope rule search)

Returns: List<Parameter> (parameters with rule configurations in the checklist)
Transaction: Not Required
Error Handling: Returns empty list if no parameters have rules in the checklist
```

#### Method: existsByIdAndChecklistId(Long id, Long checklistId)
```yaml
Signature: boolean existsByIdAndChecklistId(Long id, Long checklistId)
Purpose: "Check if parameter exists with specific ID within a checklist for validation"

Business Logic Derivation:
  1. Used in rules migration for parameter existence validation
  2. Validates parameter existence within specific checklist scope
  3. Critical for parameter validation and rule processing workflows
  4. Used to ensure parameter references are valid within checklist context
  5. Enables checklist-scoped parameter validation operations

SQL Query: |
  SELECT COUNT(*) > 0 FROM parameters p 
  WHERE p.id = ? AND p.checklists_id = ?

Parameters:
  - id: Long (Parameter identifier)
  - checklistId: Long (Checklist identifier)

Returns: boolean (true if parameter exists in the checklist)
Transaction: Not Required
Error Handling: Returns false if parameter doesn't exist in the checklist
```

### Key Repository Usage Patterns (Based on Codebase Analysis)

#### Pattern: save() for Parameter Lifecycle Management
```yaml
Usage: parameterRepository.save(parameter)
Purpose: "Create new parameters, update properties, and manage parameter lifecycle"

Business Logic Derivation:
  1. Used extensively in ParameterService for parameter creation and modification
  2. Handles parameter creation with proper task/checklist association
  3. Updates parameter properties, validations, rules, and configurations
  4. Critical for parameter lifecycle management and workflow building
  5. Supports complex parameter operations with media and rule relationships

Common Usage Examples:
  - Parameter creation: Save new parameter with task/checklist association
  - Property updates: Save parameter with modified validations or rules
  - Visibility changes: Save parameter with updated hidden flag
  - Auto-initialization: Save parameter with auto-initialization configuration

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findAll(specification, pageable) for Dynamic Parameter Queries
```yaml
Usage: parameterRepository.findAll(specification, pageable)
Purpose: "Dynamic parameter discovery with complex filtering and pagination"

Business Logic Derivation:
  1. Used in ParameterService for advanced parameter search operations
  2. Applies dynamic specifications for multi-criteria filtering
  3. Supports pagination for large parameter datasets
  4. Enables flexible parameter discovery and management operations
  5. Critical for parameter listing APIs and dashboard functionality

Transaction: Not Required
Error Handling: Returns empty page if no matches found
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAll(Specification), findByTaskIdInOrderByOrderTree
  - getParametersByChecklistIdAndTargetEntityType, getAllParametersWhereParameterIsUsedInRules
  - isParameterUsedInAutoInitialization, existsByIdAndType, existsByIdAndChecklistId
  - All validation and dependency query methods, existsById, count

Transactional Methods:
  - save, delete, deleteById, reorderParameter, updateParameterVisibility
  - updateParametersTargetEntityType, increaseOrderTreeByOneAfterParameter

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid tasks_id, checklists_id)
    * NOT NULL constraint violations (type, targetEntityType, orderTree, checklistId)
    * Invalid enum values for type, targetEntityType, verificationType fields
    * Malformed JSON in data, validations, autoInitialize, rules, metadata fields
  - EntityNotFoundException: Parameter not found by ID or criteria
  - OptimisticLockException: Concurrent parameter modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - JsonProcessingException: Invalid JSON format in JSON fields

Validation Rules:
  - type: Required, must be valid Parameter enum value
  - targetEntityType: Required, must be valid ParameterTargetEntityType enum value
  - verificationType: Required, defaults to NONE
  - label: Optional, max length 255 characters
  - orderTree: Required, must be positive integer, should be unique within task
  - checklist: Required, must reference existing checklist, immutable
  - task: Optional, must reference existing task if provided
  - data: Required, must be valid JSON (defaults to {})
  - validations: Required, must be valid JSON array (defaults to [])
  - rules: Optional, must be valid JSON if provided
  - metadata: Optional, must be valid JSON if provided

Business Constraints:
  - Cannot modify checklist association after creation
  - Order tree should be unique within task scope for proper sequencing
  - Cannot delete parameter with active parameter values or rule references
  - Parameter type changes must be compatible with existing data and validations
  - Auto-initialization configurations must reference valid parameters
  - Rule configurations must maintain dependency integrity
  - JSON field constraints must align with parameter type requirements
  - Target entity type changes must maintain workflow consistency
  - Parameter visibility changes must respect rule-based constraints
```

**Note: This repository contains 40+ custom methods. This documentation covers the most critical methods for DAO migration. The remaining methods follow similar patterns for validation queries, dependency analysis, and object type integration.**

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Parameter repository without JPA/Hibernate dependencies.
