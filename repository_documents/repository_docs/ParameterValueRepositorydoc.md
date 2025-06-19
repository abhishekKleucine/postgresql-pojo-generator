# IParameterValueRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ParameterValue
- **Primary Purpose**: Manages parameter value instances within task executions with state tracking, verification workflows, corrections, and visibility management
- **Key Relationships**: Central data capture entity linking Parameter, TaskExecution, and Job with verification and media attachments
- **Performance Characteristics**: Extremely high query volume with complex state management, verification workflows, auto-initialization logic, and cross-parameter dependencies
- **Business Context**: Core data capture component that stores actual parameter values during workflow execution with comprehensive audit trails and workflow control

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| state | state | State.ParameterExecution | false | null |
| verified | verified | boolean | false | false |
| reason | reason | String | true | null |
| value | value | String | true | null |
| choices | choices | JsonNode | true | null |
| parameters_id | parameter.id | Long | false | null |
| jobs_id | job.id | Long | false | null |
| task_executions_id | taskExecution.id | Long | false | null |
| parameter_value_approval_id | parameterValueApproval.id | Long | true | null |
| hidden | hidden | boolean | false | false |
| client_epoch | clientEpoch | Long | false | null |
| version | version | Long | true | null |
| impacted_by | impactedBy | JsonNode | true | null |
| has_variations | hasVariations | boolean | false | false |
| has_corrections | hasCorrections | boolean | false | false |
| has_exceptions | hasExceptions | boolean | false | false |
| has_active_exception | hasActiveException | boolean | false | false |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | parameter | Parameter | LAZY | Parent parameter definition, not null, immutable |
| @ManyToOne | job | Job | LAZY | Parent job instance, not null, immutable |
| @ManyToOne | taskExecution | TaskExecution | LAZY | Parent task execution, not null, immutable |
| @OneToOne | parameterValueApproval | ParameterValueApproval | LAZY | Approval workflow data, cascade = ALL |
| @OneToMany | parameterVerifications | List\<ParameterVerification\> | LAZY | Verification history, cascade = ALL |
| @OneToMany | medias | List\<ParameterValueMediaMapping\> | LAZY | Media attachments, cascade = ALL |
| @OneToMany | variations | List\<Variation\> | LAZY | Parameter variations, cascade = ALL |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(ParameterValue entity)`
- `deleteById(Long id)`
- `delete(ParameterValue entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (39 methods - ALL methods documented)

- `findByParameterIdAndTaskExecutionId(Long parameterId, Long taskExecutionId)`
- `findByTaskExecutionIdAndParameterIdIn(Long taskExecutionId, List<Long> parameterIds)`
- `readByJobIdAndStageId(Long jobId, Long stageId)`
- `findIncompleteMandatoryParameterValueIdsByJobIdAndTaskExecutionId(Long jobId, Long taskExecutionId)`
- `findIncompleteMandatoryParameterShouldBePendingForApprovalValueIdsByJobIdAndTaskExecutionId(Long jobId, Long taskExecutionId)`
- `findExecutableParameterIdsByTaskId(Long taskId)`
- `updateParameterValues(Long taskExecutionId, Long parameterId, String state, String value, String choices, String reason, Long modifiedBy, Long modifiedAt)`
- `findByJobIdAndTaskIdParameterTypeIn(Long jobId, List<Long> taskIds, List<Type.Parameter> parameterTypes)`
- `findByJobIdAndParameterTargetEntityTypeIn(Long jobId, List<Type.ParameterTargetEntityType> targetEntityTypes)`
- `findByJobIdAndIdsIn(Long jobId, Set<Long> ids)`
- `updateParameterValueVisibility(Set<Long> parameterValueIds, boolean visibility)`
- `getJobIdsByTargetEntityTypeAndObjectInChoices(String targetEntityType, String objectId)`
- `getJobIdsByObjectInChoices(String objectId)`
- `findAllByJobId(Long jobId)`
- `findVerificationIncompleteParameterExecutionIdsByTaskExecutionId(Long taskExecutionId)`
- `findLatestByJobIdAndParameterId(Long jobId, Long parameterId)`
- `findIncompleteParametersByJobId(Long jobId)`
- `countAllByTaskExecutionId(Long taskExecutionId)`
- `countAllByTaskExecutionIdWithHidden(Long taskExecutionId, boolean hidden)`
- `countParameterValueByParameterIdAndJobId(Long parameterId, Long jobId)`
- `findByTaskExecutionIdAndParameterId(Long taskExecutionId, Long parameterId)`
- `getAllParametersAvailableForVariations(Long jobId, String parameterName, Pageable pageable)`
- `getMasterTaskParameterValue(Long parameterId, Long jobId)`
- `findParametersEligibleForAutoInitialization(Long jobId, Set<Long> showParameterExecutionIds, Set<Long> executedParameterIds)`
- `checkIfLatestReferencedParameterIsExecuted(Long jobId, Long parameterId)`
- `findByJobIdAndParameterIdWithCorrectionEnabled(Long jobId, Long parameterId)`
- `findPendingApprovalParameterValueIdsByJobIdAndTaskExecutionId(Long taskExecutionId)`
- `areAllInitiatedParametersCompletedWithCorrection(Long taskExecutionId)`
- `findAllByJobIdAndTargetEntityType(Set<Long> jobIds, Type.ParameterTargetEntityType targetEntityType)`
- `findAllExecutionDataByTaskExecutionId(Long taskExecutionId)`
- `areAllParameterValuesHiddenByTaskExecutionId(Long taskExecutionId)`
- `findAllMediaDetailsByTaskExecutionId(Long taskExecutionId)`
- `findAllParameterValueDataByJobId(Long jobId)`
- `findAllByTaskExecutionId(Long taskExecutionId)`
- `recallVerificationStateForHiddenParameterValues(Set<Long> hideIds)`
- `recallVerificationStateForHiddenParameterValuesWithExceptions(Set<Long> hideIds)`
- `checkIfResourceParameterHasActiveExceptions(Long jobId, Long parameterId)`
- `getParameterPartialDataByIds(Set<Long> parameterIds, Long jobId)`
- `getParameterPartialDataFOrMasterByIds(Set<Long> parameterIds, Long jobId)`

## Method Documentation (Key Methods)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<ParameterValue> findById(Long id)
List<ParameterValue> findAll()
ParameterValue save(ParameterValue entity)
void deleteById(Long id)
void delete(ParameterValue entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: findByParameterIdAndTaskExecutionId(Long parameterId, Long taskExecutionId)
```yaml
Signature: Optional<ParameterValue> findByParameterIdAndTaskExecutionId(Long parameterId, Long taskExecutionId)
Purpose: "Find parameter value for specific parameter within a task execution"

Business Logic Derivation:
  1. Used extensively throughout system for parameter value retrieval during execution
  2. Enables parameter-specific operations within task execution context
  3. Critical for parameter value lookup and execution workflows
  4. Used in parameter verification, correction, and value update operations
  5. Essential for task execution parameter management and validation

SQL Query: |
  SELECT pv.* FROM parameter_values pv 
  WHERE pv.parameters_id = ? AND pv.task_executions_id = ?

Parameters:
  - parameterId: Long (Parameter identifier)
  - taskExecutionId: Long (Task execution identifier)

Returns: Optional<ParameterValue> (parameter value if found)
Transaction: Not Required
Error Handling: Returns empty if parameter value not found
```

#### Method: findLatestByJobIdAndParameterId(Long jobId, Long parameterId)
```yaml
Signature: ParameterValue findLatestByJobIdAndParameterId(Long jobId, Long parameterId)
Purpose: "Find the most recent parameter value for a parameter within a job"

Business Logic Derivation:
  1. Used extensively for getting latest parameter state across recurring/repeated tasks
  2. Critical for parameter dependency validation and rule execution
  3. Used in automation workflows that need current parameter values
  4. Essential for parameter validation and cross-parameter operations
  5. Enables latest value retrieval for rule processing and validations

SQL Query: |
  SELECT pv.* FROM parameter_values pv 
  WHERE pv.jobs_id = ? AND pv.parameters_id = ?
  ORDER BY pv.id DESC 
  LIMIT 1

Parameters:
  - jobId: Long (Job identifier)
  - parameterId: Long (Parameter identifier)

Returns: ParameterValue (latest parameter value for the parameter in job)
Transaction: Not Required
Error Handling: Returns null if no parameter value found
```

#### Method: updateParameterValues(Long taskExecutionId, Long parameterId, String state, String value, String choices, String reason, Long modifiedBy, Long modifiedAt)
```yaml
Signature: void updateParameterValues(Long taskExecutionId, Long parameterId, String state, String value, String choices, String reason, Long modifiedBy, Long modifiedAt)
Purpose: "Bulk update parameter value properties for correction workflows"

Business Logic Derivation:
  1. Used in TaskExecutionService for correction workflow parameter updates
  2. Enables efficient bulk parameter value updates without loading entities
  3. Critical for correction workflows that update multiple parameter properties
  4. Used in correction completion workflows for state and value updates
  5. Supports efficient parameter correction with audit trail maintenance

SQL Query: |
  UPDATE parameter_values 
  SET state = ?, value = ?, choices = ?, reason = ?, modified_by = ?, modified_at = ?
  WHERE task_executions_id = ? AND parameters_id = ?

Parameters:
  - taskExecutionId: Long (Task execution identifier)
  - parameterId: Long (Parameter identifier)
  - state: String (New parameter execution state)
  - value: String (Updated parameter value)
  - choices: String (Updated parameter choices JSON)
  - reason: String (Reason for update)
  - modifiedBy: Long (User performing update)
  - modifiedAt: Long (Timestamp for update)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: updateParameterValueVisibility(Set<Long> parameterValueIds, boolean visibility)
```yaml
Signature: void updateParameterValueVisibility(Set<Long> parameterValueIds, boolean visibility)
Purpose: "Bulk update parameter value visibility for rule-based show/hide operations"

Business Logic Derivation:
  1. Used in RulesExecutionService for rule-based parameter visibility management
  2. Enables efficient bulk visibility updates for rule processing
  3. Critical for rule execution workflows that control parameter display
  4. Used in parameter rule processing for dynamic visibility control
  5. Supports rule-based workflow control with efficient bulk operations

SQL Query: |
  UPDATE parameter_values 
  SET hidden = ? 
  WHERE id = ANY(?)

Parameters:
  - parameterValueIds: Set<Long> (Parameter value identifiers to update)
  - visibility: boolean (Visibility flag - true for hidden, false for visible)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findIncompleteMandatoryParameterValueIdsByJobIdAndTaskExecutionId(Long jobId, Long taskExecutionId)
```yaml
Signature: List<Long> findIncompleteMandatoryParameterValueIdsByJobIdAndTaskExecutionId(Long jobId, Long taskExecutionId)
Purpose: "Find incomplete mandatory parameter value IDs for task completion validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for task completion validation
  2. Identifies mandatory parameters that prevent task completion
  3. Critical for task execution workflow validation and completion checks
  4. Used in task completion validation to ensure all mandatory parameters are filled
  5. Enables task completion validation with incomplete parameter identification

SQL Query: |
  SELECT pv.id FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pv.jobs_id = ? AND pv.task_executions_id = ?
    AND p.is_mandatory = true
    AND pv.state NOT IN ('EXECUTED', 'COMPLETED', 'SKIPPED')

Parameters:
  - jobId: Long (Job identifier)
  - taskExecutionId: Long (Task execution identifier)

Returns: List<Long> (parameter value IDs that are incomplete and mandatory)
Transaction: Not Required
Error Handling: Returns empty list if all mandatory parameters are complete
```

#### Method: findParametersEligibleForAutoInitialization(Long jobId, Set<Long> showParameterExecutionIds, Set<Long> executedParameterIds)
```yaml
Signature: Set<Long> findParametersEligibleForAutoInitialization(Long jobId, Set<Long> showParameterExecutionIds, Set<Long> executedParameterIds)
Purpose: "Find parameters eligible for auto-initialization based on visibility and execution state"

Business Logic Derivation:
  1. Used in ParameterExecutionHandler for auto-initialization processing
  2. Identifies parameters that can be auto-initialized based on dependencies
  3. Critical for parameter auto-initialization workflows and dependency management
  4. Used in workflow automation for automatic parameter value initialization
  5. Enables efficient auto-initialization with dependency validation

SQL Query: |
  SELECT DISTINCT p.id FROM parameters p
  INNER JOIN parameter_values pv ON p.id = pv.parameters_id
  WHERE pv.jobs_id = ?
    AND p.is_auto_initialized = true
    AND (pv.id IN (?) OR pv.hidden = false)
    AND p.id NOT IN (?)
    AND pv.state = 'NOT_STARTED'

Parameters:
  - jobId: Long (Job identifier)
  - showParameterExecutionIds: Set<Long> (Parameter execution IDs that should be shown)
  - executedParameterIds: Set<Long> (Parameter IDs that are already executed)

Returns: Set<Long> (parameter IDs eligible for auto-initialization)
Transaction: Not Required
Error Handling: Returns empty set if no parameters are eligible
```

#### Method: getParameterPartialDataByIds(Set<Long> parameterIds, Long jobId)
```yaml
Signature: List<ParameterValueView> getParameterPartialDataByIds(Set<Long> parameterIds, Long jobId)
Purpose: "Get latest parameter value data with parameter context for rule execution"

Business Logic Derivation:
  1. Used in RulesExecutionService for rule processing with parameter context
  2. Provides efficient parameter data retrieval for rule evaluation
  3. Critical for rule execution workflows that need parameter context and values
  4. Used in rule processing for parameter dependency evaluation
  5. Enables efficient rule execution with minimal data loading

SQL Query: |
  SELECT id, value, choices, data, type, label, taskId, parameterValueId, 
         taskExecutionId, hidden, parameterId, taskExecutionState, impactedBy
  FROM (SELECT p.id AS id, pv.value AS value, pv.parameters_id AS parameterId,
               CAST(pv.choices AS TEXT) AS choices, CAST(p.data AS TEXT) AS data,
               p.type AS type, p.label AS label, p.tasks_id AS taskId,
               pv.id AS parameterValueId, pv.task_executions_id AS taskExecutionId,
               pv.hidden AS hidden, CAST(pv.impacted_by AS TEXT) AS impactedBy,
               te.state AS taskExecutionState,
               ROW_NUMBER() OVER (PARTITION BY pv.parameters_id ORDER BY pv.id DESC) AS rn
        FROM parameter_values pv
        LEFT JOIN task_executions te ON te.id = pv.task_executions_id
        INNER JOIN parameters p ON pv.parameters_id = p.id
        WHERE p.id IN (?) AND pv.jobs_id = ?) ranked
  WHERE rn = 1

Parameters:
  - parameterIds: Set<Long> (Parameter identifiers to get data for)
  - jobId: Long (Job identifier for scoping)

Returns: List<ParameterValueView> (parameter value projection views with latest values)
Transaction: Not Required
Error Handling: Returns empty list if no parameter values found
```

#### Method: areAllParameterValuesHiddenByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: boolean areAllParameterValuesHiddenByTaskExecutionId(Long taskExecutionId)
Purpose: "Check if all parameter values in a task execution are hidden for validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for task execution validation
  2. Validates that not all parameters are hidden before task operations
  3. Critical for task execution workflow validation and user experience
  4. Used in task completion validation to ensure at least some parameters are visible
  5. Enables task execution validation with visibility checking

SQL Query: |
  SELECT COUNT(*) = SUM(CASE WHEN hidden = true THEN 1 ELSE 0 END)
  FROM parameter_values 
  WHERE task_executions_id = ?

Parameters:
  - taskExecutionId: Long (Task execution identifier)

Returns: boolean (true if all parameter values are hidden)
Transaction: Not Required
Error Handling: Returns false if no parameter values found
```

## Method Documentation (All Remaining Methods - Full Detail)

#### Method: findByTaskExecutionIdAndParameterIdIn(Long taskExecutionId, List<Long> parameterIds)
```yaml
Signature: List<ParameterValue> findByTaskExecutionIdAndParameterIdIn(Long taskExecutionId, List<Long> parameterIds)
Purpose: "Get parameter values for specific parameters in task execution for bulk operations"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionService for bulk parameter value retrieval
  2. Enables efficient bulk parameter loading for task execution operations
  3. Critical for task execution parameter management and bulk processing
  4. Used in parameter execution workflows that need multiple parameter values
  5. Supports efficient bulk parameter operations for task execution management

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  WHERE pv.task_executions_id = ? 
    AND pv.parameters_id IN (?)

Parameters:
  - taskExecutionId: Long (Task execution identifier for scoping)
  - parameterIds: List<Long> (Parameter identifiers to retrieve values for)

Returns: List<ParameterValue> (parameter values for specified parameters in task execution)
Transaction: Not Required
Error Handling: Returns empty list if no parameter values found
```

#### Method: readByJobIdAndStageId(Long jobId, Long stageId)
```yaml
Signature: List<ParameterValue> readByJobIdAndStageId(Long jobId, Long stageId)
Purpose: "Get parameter values for job and stage with complete parameter context"

Business Logic Derivation:
  1. Used in JobService for stage-based parameter retrieval and display
  2. Provides comprehensive parameter values for stage-level operations
  3. Critical for stage-based parameter display and stage management workflows
  4. Used in stage data operations for parameter value retrieval with stage context
  5. Enables stage-scoped parameter operations with comprehensive parameter information

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  INNER JOIN tasks t ON p.tasks_id = t.id
  WHERE pv.jobs_id = ? AND t.stages_id = ?
  ORDER BY t.order_tree, p.order_tree

Parameters:
  - jobId: Long (Job identifier for scoping)
  - stageId: Long (Stage identifier for filtering)

Returns: List<ParameterValue> (parameter values for the stage ordered by task and parameter order)
Transaction: Not Required
Error Handling: Returns empty list if no parameter values found for stage
```

#### Method: findByJobIdAndTaskIdParameterTypeIn(Long jobId, List<Long> taskIds, List<Type.Parameter> parameterTypes)
```yaml
Signature: List<ParameterValue> findByJobIdAndTaskIdParameterTypeIn(Long jobId, List<Long> taskIds, List<Type.Parameter> parameterTypes)
Purpose: "Get parameter values by job, tasks, and parameter types for type-specific operations"

Business Logic Derivation:
  1. Used in ParameterExecutionService for type-specific parameter retrieval and processing
  2. Enables efficient parameter filtering by type for specialized operations
  3. Critical for parameter type-specific workflows and parameter processing
  4. Used in parameter operations that need type-specific parameter values
  5. Supports type-aware parameter operations with task-level scoping

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pv.jobs_id = ? 
    AND p.tasks_id IN (?)
    AND p.type IN (?)

Parameters:
  - jobId: Long (Job identifier for scoping)
  - taskIds: List<Long> (Task identifiers for filtering)
  - parameterTypes: List<Type.Parameter> (Parameter types for filtering)

Returns: List<ParameterValue> (parameter values matching job, tasks, and types)
Transaction: Not Required
Error Handling: Returns empty list if no parameter values found matching criteria
```

#### Method: findByJobIdAndParameterTargetEntityTypeIn(Long jobId, List<Type.ParameterTargetEntityType> targetEntityTypes)
```yaml
Signature: List<ParameterValue> findByJobIdAndParameterTargetEntityTypeIn(Long jobId, List<Type.ParameterTargetEntityType> targetEntityTypes)
Purpose: "Get parameter values by target entity types for entity-specific operations"

Business Logic Derivation:
  1. Used in ParameterExecutionService for entity-specific parameter retrieval and processing
  2. Enables parameter filtering by target entity type for specialized operations
  3. Critical for entity-specific parameter workflows and entity management
  4. Used in entity operations that need entity-specific parameter values
  5. Supports entity-aware parameter operations with target entity type filtering

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pv.jobs_id = ? 
    AND p.target_entity_type IN (?)

Parameters:
  - jobId: Long (Job identifier for scoping)
  - targetEntityTypes: List<Type.ParameterTargetEntityType> (Target entity types for filtering)

Returns: List<ParameterValue> (parameter values matching job and target entity types)
Transaction: Not Required
Error Handling: Returns empty list if no parameter values found for target entity types
```

#### Method: findByJobIdAndIdsIn(Long jobId, Set<Long> ids)
```yaml
Signature: List<ParameterValue> findByJobIdAndIdsIn(Long jobId, Set<Long> ids)
Purpose: "Get parameter values by job and specific IDs for targeted operations"

Business Logic Derivation:
  1. Used in ParameterExecutionService for specific parameter value retrieval and processing
  2. Enables targeted parameter value loading for specific operations
  3. Critical for parameter-specific workflows and parameter management
  4. Used in parameter operations that need specific parameter value instances
  5. Supports targeted parameter operations with job-level scoping

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  WHERE pv.jobs_id = ? AND pv.id IN (?)

Parameters:
  - jobId: Long (Job identifier for scoping)
  - ids: Set<Long> (Parameter value identifiers to retrieve)

Returns: List<ParameterValue> (parameter values matching job and IDs)
Transaction: Not Required
Error Handling: Returns empty list if no parameter values found with specified IDs
```

#### Method: findAllByJobId(Long jobId)
```yaml
Signature: List<ParameterValue> findAllByJobId(Long jobId)
Purpose: "Get all parameter values for a job for comprehensive job operations"

Business Logic Derivation:
  1. Used in JobService for comprehensive job parameter retrieval and processing
  2. Provides complete parameter value dataset for job-level operations
  3. Critical for job-level parameter management and job processing workflows
  4. Used in job operations that need all parameter values for comprehensive processing
  5. Enables complete job parameter operations with all parameter value information

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  WHERE pv.jobs_id = ?
  ORDER BY pv.id

Parameters:
  - jobId: Long (Job identifier to get all parameter values for)

Returns: List<ParameterValue> (all parameter values for the job)
Transaction: Not Required
Error Handling: Returns empty list if no parameter values found for job
```

#### Method: findVerificationIncompleteParameterExecutionIdsByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: List<Long> findVerificationIncompleteParameterExecutionIdsByTaskExecutionId(Long taskExecutionId)
Purpose: "Find verification incomplete parameter execution IDs for verification workflow validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for verification workflow validation and completion checking
  2. Identifies parameter executions that have incomplete verification for task completion
  3. Critical for task completion validation with verification requirement checking
  4. Used in task completion workflows to ensure all required verifications are complete
  5. Enables verification completion validation with incomplete verification identification

SQL Query: |
  SELECT pv.id FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pv.task_executions_id = ?
    AND p.verification_type != 'NONE'
    AND pv.verified = false
    AND pv.state = 'EXECUTED'

Parameters:
  - taskExecutionId: Long (Task execution identifier to check verification completion)

Returns: List<Long> (parameter value IDs that have incomplete verification)
Transaction: Not Required
Error Handling: Returns empty list if all verifications are complete
```

#### Method: findIncompleteParametersByJobId(Long jobId)
```yaml
Signature: List<ParameterValue> findIncompleteParametersByJobId(Long jobId)
Purpose: "Get incomplete parameters for job validation and completion checking"

Business Logic Derivation:
  1. Used in JobService for job completion validation and incomplete parameter identification
  2. Identifies parameters that are not completed for job completion validation
  3. Critical for job completion workflow validation and job state management
  4. Used in job completion checks to ensure all required parameters are completed
  5. Enables job completion validation with incomplete parameter identification

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  WHERE pv.jobs_id = ?
    AND pv.state NOT IN ('EXECUTED', 'COMPLETED', 'SKIPPED')

Parameters:
  - jobId: Long (Job identifier to check for incomplete parameters)

Returns: List<ParameterValue> (parameter values that are not completed)
Transaction: Not Required
Error Handling: Returns empty list if all parameters are completed
```

#### Method: countAllByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: Integer countAllByTaskExecutionId(Long taskExecutionId)
Purpose: "Count all parameter values in task execution for metrics and validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for task execution metrics and parameter count validation
  2. Provides total parameter count for task execution reporting and validation
  3. Critical for task execution metrics and parameter count validation
  4. Used in task execution validation to ensure parameter count consistency
  5. Enables task execution metrics with comprehensive parameter count information

SQL Query: |
  SELECT COUNT(*) FROM parameter_values pv
  WHERE pv.task_executions_id = ?

Parameters:
  - taskExecutionId: Long (Task execution identifier to count parameters for)

Returns: Integer (total count of parameter values in task execution)
Transaction: Not Required
Error Handling: Returns 0 if no parameter values found
```

#### Method: countAllByTaskExecutionIdWithHidden(Long taskExecutionId, boolean hidden)
```yaml
Signature: Integer countAllByTaskExecutionIdWithHidden(Long taskExecutionId, boolean hidden)
Purpose: "Count parameter values with visibility filter for UI display validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for UI display validation and parameter visibility management
  2. Provides parameter count with visibility filtering for display validation
  3. Critical for UI display validation and parameter visibility management
  4. Used in UI validation to ensure proper parameter display and visibility
  5. Enables UI validation with parameter visibility filtering and count validation

SQL Query: |
  SELECT COUNT(*) FROM parameter_values pv
  WHERE pv.task_executions_id = ? 
    AND pv.hidden = ?

Parameters:
  - taskExecutionId: Long (Task execution identifier to count parameters for)
  - hidden: boolean (Visibility filter - true for hidden, false for visible)

Returns: Integer (count of parameter values matching visibility filter)
Transaction: Not Required
Error Handling: Returns 0 if no parameter values match criteria
```

#### Method: countParameterValueByParameterIdAndJobId(Long parameterId, Long jobId)
```yaml
Signature: Integer countParameterValueByParameterIdAndJobId(Long parameterId, Long jobId)
Purpose: "Count parameter values for parameter in job for instance tracking"

Business Logic Derivation:
  1. Used in ParameterExecutionService for parameter instance counting and validation
  2. Provides parameter instance count for parameter execution validation
  3. Critical for parameter execution validation and instance management
  4. Used in parameter operations to validate parameter instance counts
  5. Enables parameter instance tracking with parameter-specific count validation

SQL Query: |
  SELECT COUNT(*) FROM parameter_values pv
  WHERE pv.parameters_id = ? AND pv.jobs_id = ?

Parameters:
  - parameterId: Long (Parameter identifier to count values for)
  - jobId: Long (Job identifier for scoping)

Returns: Integer (count of parameter values for parameter in job)
Transaction: Not Required
Error Handling: Returns 0 if no parameter values found
```

#### Method: findByTaskExecutionIdAndParameterId(Long taskExecutionId, Long parameterId)
```yaml
Signature: ParameterValue findByTaskExecutionIdAndParameterId(Long taskExecutionId, Long parameterId)
Purpose: "Find parameter value by task execution and parameter for direct access"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionService for direct parameter value access
  2. Provides direct parameter value retrieval for parameter-specific operations
  3. Critical for parameter value access and parameter execution workflows
  4. Used in parameter operations that need direct parameter value access
  5. Enables direct parameter value operations with task execution context

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  WHERE pv.task_executions_id = ? AND pv.parameters_id = ?

Parameters:
  - taskExecutionId: Long (Task execution identifier for scoping)
  - parameterId: Long (Parameter identifier for direct access)

Returns: ParameterValue (parameter value for parameter in task execution)
Transaction: Not Required
Error Handling: Returns null if parameter value not found
```

#### Method: getAllParametersAvailableForVariations(Long jobId, String parameterName, Pageable pageable)
```yaml
Signature: Page<ParameterValue> getAllParametersAvailableForVariations(Long jobId, String parameterName, Pageable pageable)
Purpose: "Get parameters available for variation creation with pagination and filtering"

Business Logic Derivation:
  1. Used in VariationService for variation creation and parameter selection
  2. Provides parameter selection for variation workflows with filtering and pagination
  3. Critical for variation creation workflows and parameter selection
  4. Used in variation operations that need parameter selection for variation creation
  5. Enables variation creation with parameter selection and filtering capabilities

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pv.jobs_id = ?
    AND p.allow_variations = true
    AND pv.state = 'EXECUTED'
    AND (? IS NULL OR LOWER(p.label) LIKE LOWER(?))
  ORDER BY p.label, pv.id DESC

Parameters:
  - jobId: Long (Job identifier for scoping)
  - parameterName: String (Parameter name filter for search, nullable)
  - pageable: Pageable (Pagination parameters)

Returns: Page<ParameterValue> (paginated parameter values available for variations)
Transaction: Not Required
Error Handling: Returns empty page if no parameters available for variations
```

#### Method: getMasterTaskParameterValue(Long parameterId, Long jobId)
```yaml
Signature: ParameterValue getMasterTaskParameterValue(Long parameterId, Long jobId)
Purpose: "Get parameter value from master task execution for master data operations"

Business Logic Derivation:
  1. Used in ParameterExecutionService for master task parameter retrieval and processing
  2. Provides master task parameter value for parameter dependency operations
  3. Critical for parameter dependency validation and master task operations
  4. Used in parameter operations that need master task parameter values
  5. Enables master task parameter operations with parameter-specific context

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  INNER JOIN task_executions te ON pv.task_executions_id = te.id
  WHERE pv.parameters_id = ? AND pv.jobs_id = ?
    AND te.type = 'MASTER'
  ORDER BY pv.id DESC
  LIMIT 1

Parameters:
  - parameterId: Long (Parameter identifier for master task value)
  - jobId: Long (Job identifier for scoping)

Returns: ParameterValue (parameter value from master task execution)
Transaction: Not Required
Error Handling: Returns null if no master task parameter value found
```

#### Method: checkIfLatestReferencedParameterIsExecuted(Long jobId, Long parameterId)
```yaml
Signature: boolean checkIfLatestReferencedParameterIsExecuted(Long jobId, Long parameterId)
Purpose: "Check if referenced parameter is executed for dependency validation"

Business Logic Derivation:
  1. Used in ParameterExecutionService for parameter dependency validation and execution checking
  2. Validates that referenced parameters are executed before dependent parameter operations
  3. Critical for parameter dependency validation and execution workflow management
  4. Used in parameter execution validation to ensure dependency requirements are met
  5. Enables parameter dependency validation with execution state checking

SQL Query: |
  SELECT pv.state = 'EXECUTED' FROM parameter_values pv
  WHERE pv.jobs_id = ? AND pv.parameters_id = ?
  ORDER BY pv.id DESC
  LIMIT 1

Parameters:
  - jobId: Long (Job identifier for scoping)
  - parameterId: Long (Referenced parameter identifier to check execution)

Returns: boolean (true if latest referenced parameter is executed)
Transaction: Not Required
Error Handling: Returns false if parameter not found or not executed
```

#### Method: findByJobIdAndParameterIdWithCorrectionEnabled(Long jobId, Long parameterId)
```yaml
Signature: ParameterValue findByJobIdAndParameterIdWithCorrectionEnabled(Long jobId, Long parameterId)
Purpose: "Find parameter value with correction enabled for correction workflow operations"

Business Logic Derivation:
  1. Used in CorrectionService for correction workflow management and validation
  2. Retrieves parameter value that has correction enabled for correction operations
  3. Critical for correction workflow validation and correction management
  4. Used in correction operations that need correction-enabled parameter values
  5. Enables correction workflow operations with correction-enabled parameter identification

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  INNER JOIN task_executions te ON pv.task_executions_id = te.id
  WHERE pv.jobs_id = ? AND pv.parameters_id = ?
    AND te.correction_enabled = true
  ORDER BY pv.id DESC
  LIMIT 1

Parameters:
  - jobId: Long (Job identifier for scoping)
  - parameterId: Long (Parameter identifier to find correction-enabled value)

Returns: ParameterValue (parameter value with correction enabled)
Transaction: Not Required
Error Handling: Returns null if no correction-enabled parameter value found
```

#### Method: findIncompleteMandatoryParameterShouldBePendingForApprovalValueIdsByJobIdAndTaskExecutionId(Long jobId, Long taskExecutionId)
```yaml
Signature: List<Long> findIncompleteMandatoryParameterShouldBePendingForApprovalValueIdsByJobIdAndTaskExecutionId(Long jobId, Long taskExecutionId)
Purpose: "Find incomplete mandatory parameters that should be pending for approval"

Business Logic Derivation:
  1. Used in TaskExecutionService for approval workflow validation and task completion checking
  2. Identifies mandatory parameters that require approval before task completion
  3. Critical for task completion validation with approval requirement checking
  4. Used in task completion workflows to ensure all required approvals are initiated
  5. Enables approval workflow validation with incomplete mandatory parameter identification

SQL Query: |
  SELECT pv.id FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pv.jobs_id = ? AND pv.task_executions_id = ?
    AND p.is_mandatory = true
    AND p.approval_required = true
    AND pv.state NOT IN ('EXECUTED', 'COMPLETED', 'SKIPPED')
    AND pv.parameter_value_approval_id IS NULL

Parameters:
  - jobId: Long (Job identifier for scoping)
  - taskExecutionId: Long (Task execution identifier for approval checking)

Returns: List<Long> (parameter value IDs that need approval initiation)
Transaction: Not Required
Error Handling: Returns empty list if all required approvals are initiated
```

#### Method: findExecutableParameterIdsByTaskId(Long taskId)
```yaml
Signature: List<Long> findExecutableParameterIdsByTaskId(Long taskId)
Purpose: "Get executable parameter IDs for task for parameter execution planning"

Business Logic Derivation:
  1. Used in TaskExecutionService for parameter execution planning and task management
  2. Identifies parameters that can be executed for task execution operations
  3. Critical for task execution planning and parameter execution workflow management
  4. Used in task execution operations to plan parameter execution sequence
  5. Enables task execution planning with executable parameter identification

SQL Query: |
  SELECT p.id FROM parameters p
  WHERE p.tasks_id = ?
    AND p.type NOT IN ('INSTRUCTION', 'INFORMATION')
    AND p.is_deleted = false

Parameters:
  - taskId: Long (Task identifier to get executable parameters for)

Returns: List<Long> (parameter IDs that are executable for the task)
Transaction: Not Required
Error Handling: Returns empty list if no executable parameters found
```

#### Method: findPendingApprovalParameterValueIdsByJobIdAndTaskExecutionId(Long taskExecutionId)
```yaml
Signature: List<Long> findPendingApprovalParameterValueIdsByJobIdAndTaskExecutionId(Long taskExecutionId)
Purpose: "Find pending approval parameter value IDs for approval workflow validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for approval workflow validation and task completion checking
  2. Identifies parameter values that have pending approvals for task completion validation
  3. Critical for task completion validation with approval workflow checking
  4. Used in task completion workflows to ensure all pending approvals are resolved
  5. Enables approval workflow validation with pending approval identification

SQL Query: |
  SELECT pv.id FROM parameter_values pv
  INNER JOIN parameter_value_approvals pva ON pv.parameter_value_approval_id = pva.id
  WHERE pv.task_executions_id = ?
    AND pva.approval_status = 'PENDING'

Parameters:
  - taskExecutionId: Long (Task execution identifier to check pending approvals)

Returns: List<Long> (parameter value IDs with pending approvals)
Transaction: Not Required
Error Handling: Returns empty list if no pending approvals found
```

#### Method: areAllInitiatedParametersCompletedWithCorrection(Long taskExecutionId)
```yaml
Signature: boolean areAllInitiatedParametersCompletedWithCorrection(Long taskExecutionId)
Purpose: "Check if all initiated parameters are completed with correction for correction validation"

Business Logic Derivation:
  1. Used in TaskExecutionService for correction workflow validation and completion checking
  2. Validates that all initiated parameters are completed including corrections
  3. Critical for correction workflow completion validation and task completion checking
  4. Used in correction completion workflows to ensure all corrections are resolved
  5. Enables correction completion validation with comprehensive completion checking

SQL Query: |
  SELECT COUNT(*) = SUM(CASE WHEN pv.state IN ('EXECUTED', 'COMPLETED') 
                             OR pv.has_corrections = true THEN 1 ELSE 0 END)
  FROM parameter_values pv
  WHERE pv.task_executions_id = ?
    AND pv.state != 'NOT_STARTED'

Parameters:
  - taskExecutionId: Long (Task execution identifier to check completion with corrections)

Returns: boolean (true if all initiated parameters are completed with corrections)
Transaction: Not Required
Error Handling: Returns false if any initiated parameters are not completed
```

#### Method: findAllByJobIdAndTargetEntityType(Set<Long> jobIds, Type.ParameterTargetEntityType targetEntityType)
```yaml
Signature: List<ParameterValue> findAllByJobIdAndTargetEntityType(Set<Long> jobIds, Type.ParameterTargetEntityType targetEntityType)
Purpose: "Get parameter values by jobs and target entity type for entity-specific bulk operations"

Business Logic Derivation:
  1. Used in EntityObjectService for entity-specific parameter retrieval and processing
  2. Enables bulk parameter value loading for entity-specific operations across jobs
  3. Critical for entity-specific workflows and entity management operations
  4. Used in entity operations that need entity-specific parameter values across multiple jobs
  5. Supports entity-aware bulk operations with target entity type filtering

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pv.jobs_id IN (?)
    AND p.target_entity_type = ?

Parameters:
  - jobIds: Set<Long> (Job identifiers for bulk retrieval)
  - targetEntityType: Type.ParameterTargetEntityType (Target entity type for filtering)

Returns: List<ParameterValue> (parameter values matching jobs and target entity type)
Transaction: Not Required
Error Handling: Returns empty list if no parameter values found for jobs and entity type
```

#### Method: findAllExecutionDataByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: List<ParameterValueExecutionView> findAllExecutionDataByTaskExecutionId(Long taskExecutionId)
Purpose: "Get execution data for task execution reporting and audit operations"

Business Logic Derivation:
  1. Used in TaskExecutionService for task execution reporting and audit data retrieval
  2. Provides comprehensive execution data for task execution audit and reporting
  3. Critical for task execution audit trail and execution data reporting
  4. Used in task execution reporting workflows for comprehensive execution information
  5. Enables task execution audit with complete execution data and context

SQL Query: |
  SELECT pv.id as parameterValueId, pv.value, pv.state, pv.verified,
         p.id as parameterId, p.label as parameterName, p.type as parameterType,
         pv.created_at as executedAt, pv.reason
  FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pv.task_executions_id = ?
  ORDER BY p.order_tree

Parameters:
  - taskExecutionId: Long (Task execution identifier to get execution data for)

Returns: List<ParameterValueExecutionView> (execution data projection views)
Transaction: Not Required
Error Handling: Returns empty list if no execution data found
```

#### Method: findAllByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: List<ParameterValue> findAllByTaskExecutionId(Long taskExecutionId)
Purpose: "Get all parameter values for task execution for comprehensive task operations"

Business Logic Derivation:
  1. Used extensively in TaskExecutionService for comprehensive task execution parameter management
  2. Provides complete parameter value dataset for task execution operations
  3. Critical for task execution parameter management and task processing workflows
  4. Used in task execution operations that need all parameter values for comprehensive processing
  5. Enables complete task execution parameter operations with all parameter value information

SQL Query: |
  SELECT pv.* FROM parameter_values pv
  WHERE pv.task_executions_id = ?
  ORDER BY pv.id

Parameters:
  - taskExecutionId: Long (Task execution identifier to get all parameter values for)

Returns: List<ParameterValue> (all parameter values for the task execution)
Transaction: Not Required
Error Handling: Returns empty list if no parameter values found for task execution
```

#### Method: recallVerificationStateForHiddenParameterValues(Set<Long> hideIds)
```yaml
Signature: void recallVerificationStateForHiddenParameterValues(Set<Long> hideIds)
Purpose: "Recall verification state for hidden parameter values for verification workflow cleanup"

Business Logic Derivation:
  1. Used in ParameterExecutionService for verification workflow cleanup when parameters are hidden
  2. Recalls verification state for hidden parameters to maintain workflow consistency
  3. Critical for verification workflow management and parameter visibility handling
  4. Used in parameter visibility operations to clean up verification state
  5. Enables verification workflow cleanup with parameter visibility management

SQL Query: |
  UPDATE parameter_values 
  SET verified = false 
  WHERE id IN (?) AND hidden = true

Parameters:
  - hideIds: Set<Long> (Parameter value identifiers to recall verification for)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: recallVerificationStateForHiddenParameterValuesWithExceptions(Set<Long> hideIds)
```yaml
Signature: void recallVerificationStateForHiddenParameterValuesWithExceptions(Set<Long> hideIds)
Purpose: "Recall verification state for hidden parameter values with exception handling for complex verification workflows"

Business Logic Derivation:
  1. Used in ParameterExecutionService for verification workflow cleanup with exception considerations
  2. Recalls verification state for hidden parameters while maintaining exception workflow integrity
  3. Critical for verification workflow management with exception handling and parameter visibility
  4. Used in parameter visibility operations to clean up verification state with exception awareness
  5. Enables verification workflow cleanup with exception handling and parameter visibility management

SQL Query: |
  UPDATE parameter_values 
  SET verified = false 
  WHERE id IN (?) 
    AND hidden = true
    AND has_active_exception = false

Parameters:
  - hideIds: Set<Long> (Parameter value identifiers to recall verification for with exception checking)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: getJobIdsByTargetEntityTypeAndObjectInChoices(String targetEntityType, String objectId)
```yaml
Signature: List<Long> getJobIdsByTargetEntityTypeAndObjectInChoices(String targetEntityType, String objectId)
Purpose: "Find job IDs by target entity type and object in parameter choices for entity-based job discovery"

Business Logic Derivation:
  1. Used in EntityObjectService for entity-based job discovery and entity management operations
  2. Identifies jobs that have parameters with specific entity objects in choices
  3. Critical for entity-based job tracking and entity usage analysis
  4. Used in entity management workflows to find jobs using specific entity objects
  5. Enables entity-aware job discovery with target entity type and object filtering

SQL Query: |
  SELECT DISTINCT pv.jobs_id FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE p.target_entity_type = ?
    AND JSON_EXTRACT(pv.choices, '$') LIKE CONCAT('%', ?, '%')

Parameters:
  - targetEntityType: String (Target entity type for filtering)
  - objectId: String (Object identifier to search in parameter choices)

Returns: List<Long> (job IDs that have parameters with the object in choices)
Transaction: Not Required
Error Handling: Returns empty list if no jobs found with object in choices
```

#### Method: getJobIdsByObjectInChoices(String objectId)
```yaml
Signature: List<Long> getJobIdsByObjectInChoices(String objectId)
Purpose: "Find job IDs by object in parameter choices for simplified entity-based job discovery"

Business Logic Derivation:
  1. Used in EntityObjectService for simplified entity-based job discovery and entity management
  2. Identifies jobs that have parameters with specific objects in choices regardless of entity type
  3. Critical for entity-based job tracking and simplified entity usage analysis
  4. Used in entity management workflows to find all jobs using specific objects
  5. Enables simplified entity-aware job discovery with object filtering

SQL Query: |
  SELECT DISTINCT pv.jobs_id FROM parameter_values pv
  WHERE JSON_EXTRACT(pv.choices, '$') LIKE CONCAT('%', ?, '%')

Parameters:
  - objectId: String (Object identifier to search in parameter choices)

Returns: List<Long> (job IDs that have parameters with the object in choices)
Transaction: Not Required
Error Handling: Returns empty list if no jobs found with object in choices
```

#### Method: checkIfResourceParameterHasActiveExceptions(Long jobId, Long parameterId)
```yaml
Signature: boolean checkIfResourceParameterHasActiveExceptions(Long jobId, Long parameterId)
Purpose: "Check if resource parameter has active exceptions for exception workflow validation"

Business Logic Derivation:
  1. Used in ParameterExecutionService for exception workflow validation and resource parameter checking
  2. Validates that resource parameters don't have active exceptions before operations
  3. Critical for exception workflow validation and resource parameter management
  4. Used in parameter operations to ensure resource parameters are not in exception state
  5. Enables exception workflow validation with resource parameter exception checking

SQL Query: |
  SELECT COUNT(*) > 0 FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pv.jobs_id = ? 
    AND pv.parameters_id = ?
    AND p.target_entity_type = 'RESOURCE'
    AND pv.has_active_exception = true

Parameters:
  - jobId: Long (Job identifier for scoping)
  - parameterId: Long (Parameter identifier to check for active exceptions)

Returns: boolean (true if resource parameter has active exceptions)
Transaction: Not Required
Error Handling: Returns false if parameter not found or no active exceptions
```

#### Method: findAllMediaDetailsByTaskExecutionId(Long taskExecutionId)
```yaml
Signature: List<ParameterValueMediaView> findAllMediaDetailsByTaskExecutionId(Long taskExecutionId)
Purpose: "Get media details for task execution for media reporting and display operations"

Business Logic Derivation:
  1. Used in TaskExecutionService for task execution media reporting and media detail retrieval
  2. Provides comprehensive media information for task execution media display
  3. Critical for task execution media audit trail and media detail reporting
  4. Used in task execution reporting workflows for media information display
  5. Enables task execution media audit with complete media details and context

SQL Query: |
  SELECT pv.id as parameterValueId, p.label as parameterName,
         m.id as mediaId, m.name as mediaName, m.filename,
         m.original_filename as originalFilename, m.type as mediaType
  FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  INNER JOIN parameter_value_media_mapping pvmm ON pv.id = pvmm.parameter_values_id
  INNER JOIN medias m ON pvmm.medias_id = m.id
  WHERE pv.task_executions_id = ?
    AND m.archived = false
  ORDER BY p.order_tree, m.name

Parameters:
  - taskExecutionId: Long (Task execution identifier to get media details for)

Returns: List<ParameterValueMediaView> (media detail projection views)
Transaction: Not Required
Error Handling: Returns empty list if no media details found for task execution
```

#### Method: findAllParameterValueDataByJobId(Long jobId)
```yaml
Signature: List<ParameterValueDataView> findAllParameterValueDataByJobId(Long jobId)
Purpose: "Get all parameter value data for job reporting and comprehensive job analysis"

Business Logic Derivation:
  1. Used in JobService for comprehensive job parameter value reporting and job data analysis
  2. Provides complete parameter value dataset for job-level reporting and analysis
  3. Critical for job reporting workflows and comprehensive job data retrieval
  4. Used in job analysis operations that need all parameter value data
  5. Enables comprehensive job reporting with all parameter value information and context

SQL Query: |
  SELECT pv.id as parameterValueId, pv.value, pv.state, pv.verified,
         pv.hidden, pv.has_variations, pv.has_corrections, pv.has_exceptions,
         p.id as parameterId, p.label as parameterName, p.type as parameterType,
         t.id as taskId, t.name as taskName, s.name as stageName,
         te.id as taskExecutionId, te.state as taskExecutionState
  FROM parameter_values pv
  INNER JOIN parameters p ON pv.parameters_id = p.id
  INNER JOIN tasks t ON p.tasks_id = t.id
  INNER JOIN stages s ON t.stages_id = s.id
  INNER JOIN task_executions te ON pv.task_executions_id = te.id
  WHERE pv.jobs_id = ?
  ORDER BY s.order_tree, t.order_tree, p.order_tree, te.order_tree

Parameters:
  - jobId: Long (Job identifier to get all parameter value data for)

Returns: List<ParameterValueDataView> (comprehensive parameter value data projection views)
Transaction: Not Required
Error Handling: Returns empty list if no parameter value data found for job
```

#### Method: getParameterPartialDataFOrMasterByIds(Set<Long> parameterIds, Long jobId)
```yaml
Signature: List<ParameterValueView> getParameterPartialDataFOrMasterByIds(Set<Long> parameterIds, Long jobId)
Purpose: "Get master task parameter data for specific parameters for master data operations"

Business Logic Derivation:
  1. Used in RulesExecutionService for master task parameter data retrieval and rule processing
  2. Provides efficient master task parameter data for rule evaluation with master context
  3. Critical for rule execution workflows that need master task parameter context and values
  4. Used in rule processing for master task parameter dependency evaluation
  5. Enables efficient rule execution with master task parameter data loading

SQL Query: |
  SELECT id, value, choices, data, type, label, taskId, parameterValueId, 
         taskExecutionId, hidden, parameterId, taskExecutionState, impactedBy
  FROM (SELECT p.id AS id, pv.value AS value, pv.parameters_id AS parameterId,
               CAST(pv.choices AS TEXT) AS choices, CAST(p.data AS TEXT) AS data,
               p.type AS type, p.label AS label, p.tasks_id AS taskId,
               pv.id AS parameterValueId, pv.task_executions_id AS taskExecutionId,
               pv.hidden AS hidden, CAST(pv.impacted_by AS TEXT) AS impactedBy,
               te.state AS taskExecutionState, te.type AS taskExecutionType,
               ROW_NUMBER() OVER (PARTITION BY pv.parameters_id ORDER BY pv.id DESC) AS rn
        FROM parameter_values pv
        LEFT JOIN task_executions te ON te.id = pv.task_executions_id
        INNER JOIN parameters p ON pv.parameters_id = p.id
        WHERE p.id IN (?) AND pv.jobs_id = ? AND te.type = 'MASTER') ranked
  WHERE rn = 1

Parameters:
  - parameterIds: Set<Long> (Parameter identifiers to get master data for)
  - jobId: Long (Job identifier for scoping)

Returns: List<ParameterValueView> (master task parameter value projection views)
Transaction: Not Required
Error Handling: Returns empty list if no master task parameter values found
```

### Key Repository Usage Patterns

#### Pattern: save() for Parameter Value Lifecycle Management
```yaml
Usage: parameterValueRepository.save(parameterValue)
Purpose: "Create new parameter values, update states, and manage value lifecycle"

Business Logic Derivation:
  1. Used extensively throughout system for parameter value state management
  2. Handles parameter value creation with proper parameter and task execution association
  3. Updates parameter values during workflow execution and correction
  4. Critical for parameter value lifecycle management and audit tracking
  5. Supports complex parameter operations with verification and media management

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByParameterIdAndTaskExecutionId, findByTaskExecutionIdAndParameterIdIn
  - readByJobIdAndStageId, findIncompleteMandatoryParameterValueIdsByJobIdAndTaskExecutionId
  - findIncompleteMandatoryParameterShouldBePendingForApprovalValueIdsByJobIdAndTaskExecutionId
  - findExecutableParameterIdsByTaskId, findByJobIdAndTaskIdParameterTypeIn
  - findByJobIdAndParameterTargetEntityTypeIn, findByJobIdAndIdsIn
  - getJobIdsByTargetEntityTypeAndObjectInChoices, getJobIdsByObjectInChoices
  - findAllByJobId, findVerificationIncompleteParameterExecutionIdsByTaskExecutionId
  - findLatestByJobIdAndParameterId, findIncompleteParametersByJobId
  - countAllByTaskExecutionId, countAllByTaskExecutionIdWithHidden
  - countParameterValueByParameterIdAndJobId, findByTaskExecutionIdAndParameterId
  - getAllParametersAvailableForVariations, getMasterTaskParameterValue
  - findParametersEligibleForAutoInitialization, checkIfLatestReferencedParameterIsExecuted
  - findByJobIdAndParameterIdWithCorrectionEnabled, findPendingApprovalParameterValueIdsByJobIdAndTaskExecutionId
  - areAllInitiatedParametersCompletedWithCorrection, findAllByJobIdAndTargetEntityType
  - findAllExecutionDataByTaskExecutionId, areAllParameterValuesHiddenByTaskExecutionId
  - findAllMediaDetailsByTaskExecutionId, findAllParameterValueDataByJobId
  - findAllByTaskExecutionId, checkIfResourceParameterHasActiveExceptions
  - getParameterPartialDataByIds, getParameterPartialDataFOrMasterByIds
  - existsById, count

Transactional Methods:
  - save, delete, deleteById, updateParameterValues, updateParameterValueVisibility
  - recallVerificationStateForHiddenParameterValues, recallVerificationStateForHiddenParameterValuesWithExceptions

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid parameters_id, jobs_id, task_executions_id)
    * NOT NULL constraint violations (state, parameters_id, jobs_id, task_executions_id, clientEpoch)
    * Invalid enum values for state field
    * JSON format violations in choices and impactedBy fields
  - EntityNotFoundException: ParameterValue not found by ID or criteria
  - OptimisticLockException: Concurrent parameter value modifications (version conflict)
  - InvalidDataAccessApiUsageException: Invalid query parameters
  - ResourceNotFoundException: ParameterValue not found during operations

Validation Rules:
  - state: Required, must be valid ParameterExecution enum value
  - parameter: Required, must reference existing parameter, immutable after creation
  - job: Required, must reference existing job, immutable after creation
  - taskExecution: Required, must reference existing task execution, immutable after creation
  - clientEpoch: Required, must be valid timestamp
  - value: Optional, format depends on parameter type (number, text, date, etc.)
  - choices: Optional, must be valid JSON when provided
  - hidden: Defaults to false, controlled by rule execution
  - verified: Defaults to false, updated through verification workflows
  - version: Managed by JPA for optimistic locking

Business Constraints:
  - Cannot modify parameter, job, or taskExecution associations after creation
  - Parameter value state transitions must follow defined workflow
  - Cannot delete parameter value with active verifications or corrections
  - Parameter value updates must maintain audit trail with proper user attribution
  - Visibility changes must respect rule-based constraints
  - Verification workflows must maintain proper state transitions
  - Correction workflows must preserve previous states for rollback
  - Auto-initialization must respect parameter dependencies and visibility rules
  - Media attachments must maintain proper association and archival state
  - Variation creation must respect parameter type and execution constraints
  - JSON fields must contain valid format for parameter type operations
  - Client epoch must be provided for optimistic locking and conflict resolution
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the ParameterValue repository without JPA/Hibernate dependencies.
