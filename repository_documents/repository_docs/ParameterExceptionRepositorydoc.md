# IParameterExceptionRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ParameterException
- **Primary Purpose**: Manages parameter exception entities for parameter validation rule violations with approval workflows, status tracking, and exception lifecycle management
- **Key Relationships**: Exception entity linking to ParameterValue, TaskExecution, Facility, Job with comprehensive exception workflow and approval process management
- **Performance Characteristics**: Moderate query volume with exception status checking, latest exception retrieval, and job-specific exception validation operations
- **Business Context**: Critical validation and compliance component that handles parameter validation rule violations, exception approval workflows, user-specific exception tracking, and CJF (Checklist Job Form) exception management for operational compliance and validation control

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| code | code | String | false | null |
| value | value | String | true | null |
| choices | choices | JsonNode | true | null |
| parameter_values_id | parameterValue.id | Long | false | null |
| task_executions_id | taskExecution.id | Long | false | null |
| facilities_id | facility.id | Long | false | null |
| jobs_id | job.id | Long | false | null |
| status | Status | State.ParameterException | false | null |
| initiators_reason | initiatorsReason | String | true | null |
| reviewers_reason | reviewersReason | String | true | null |
| previous_state | previousState | State.ParameterExecution | true | null |
| reason | reason | String | true | null |
| rules_id | ruleId | String | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | parameterValue | ParameterValue | LAZY | Parameter value with exception, not null, immutable |
| @ManyToOne | taskExecution | TaskExecution | LAZY | Task execution context, not null, cascade = ALL, immutable |
| @ManyToOne | facility | Facility | LAZY | Facility context, not null, cascade = ALL, immutable |
| @ManyToOne | job | Job | LAZY | Job context, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(ParameterException entity)`
- `deleteById(Long id)`
- `delete(ParameterException entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (4 methods - ALL methods documented)

- `findLatestException(Long parameterValueId)`
- `isExceptionPendingOnUser(Long jobId, Long userId)`
- `isCJFExceptionPendingOnUser(Long jobId)`
- `isExceptionRejectedOnCjf(Long jobId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<ParameterException> findById(Long id)
List<ParameterException> findAll()
ParameterException save(ParameterException entity)
void deleteById(Long id)
void delete(ParameterException entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: findLatestException(Long parameterValueId)
```yaml
Signature: List<ParameterException> findLatestException(Long parameterValueId)
Purpose: "Find latest parameter exceptions for specific parameter value for exception status determination and workflow management"

Business Logic Derivation:
  1. Used in ParameterExceptionService and IParameterMapper for retrieving latest exception information for parameter value state determination
  2. Provides latest exception status for parameter values enabling proper exception workflow management and status tracking
  3. Critical for parameter validation operations requiring current exception status for parameter value state calculation
  4. Used in parameter state determination workflows for calculating parameter execution status based on exception states
  5. Enables exception workflow management with latest exception information for parameter validation and approval processes

SQL Query: |
  SELECT pe.* FROM parameter_exception pe
  WHERE pe.parameter_values_id = ?
  ORDER BY pe.created_at DESC

Parameters:
  - parameterValueId: Long (Parameter value identifier to get latest exceptions for)

Returns: List<ParameterException> (latest exceptions for the parameter value ordered by creation time)
Transaction: Not Required
Error Handling: Returns empty list if no exceptions found for parameter value
```

#### Method: isExceptionPendingOnUser(Long jobId, Long userId)
```yaml
Signature: boolean isExceptionPendingOnUser(Long jobId, Long userId)
Purpose: "Check if user has pending parameter exceptions for specific job for user-specific exception workflow validation"

Business Logic Derivation:
  1. Used in JobService for user-specific exception status checking during job workflow operations and user task validation
  2. Provides user-specific exception pending status for job operations enabling proper workflow control and user validation
  3. Critical for job workflow operations requiring user-specific exception validation for workflow progression control
  4. Used in job status determination workflows for validating user-specific exception states and workflow permissions
  5. Enables user-specific exception workflow management with pending exception validation for proper job workflow control

SQL Query: |
  SELECT COUNT(*) > 0 FROM parameter_exception pe
  INNER JOIN parameter_exception_reviewers per ON pe.id = per.parameter_exception_id
  WHERE pe.jobs_id = ? 
    AND per.users_id = ?
    AND pe.status = 'PENDING'

Parameters:
  - jobId: Long (Job identifier to check exceptions for)
  - userId: Long (User identifier to check pending exceptions for)

Returns: boolean (true if user has pending exceptions for the job)
Transaction: Not Required
Error Handling: Returns false if no pending exceptions found for user and job
```

#### Method: isCJFExceptionPendingOnUser(Long jobId)
```yaml
Signature: boolean isCJFExceptionPendingOnUser(Long jobId)
Purpose: "Check if CJF (Checklist Job Form) exceptions are pending for specific job for CJF-specific exception workflow validation"

Business Logic Derivation:
  1. Used in JobService for CJF-specific exception status checking during job workflow operations and CJF validation
  2. Provides CJF-specific exception pending status for job operations enabling proper CJF workflow control and validation
  3. Critical for job workflow operations requiring CJF-specific exception validation for CJF workflow progression control
  4. Used in job status determination workflows for validating CJF-specific exception states and CJF workflow permissions
  5. Enables CJF-specific exception workflow management with pending exception validation for proper CJF job workflow control

SQL Query: |
  SELECT COUNT(*) > 0 FROM parameter_exception pe
  INNER JOIN parameter_values pv ON pe.parameter_values_id = pv.id
  INNER JOIN parameters p ON pv.parameters_id = p.id
  WHERE pe.jobs_id = ?
    AND p.target_entity_type = 'PROCESS'
    AND pe.status = 'PENDING'

Parameters:
  - jobId: Long (Job identifier to check CJF exceptions for)

Returns: boolean (true if CJF exceptions are pending for the job)
Transaction: Not Required
Error Handling: Returns false if no pending CJF exceptions found for job
```

#### Method: isExceptionRejectedOnCjf(Long jobId)
```yaml
Signature: boolean isExceptionRejectedOnCjf(Long jobId)
Purpose: "Check if CJF (Checklist Job Form) exceptions are rejected for specific job for CJF rejection status validation"

Business Logic Derivation:
  1. Used in JobService for CJF-specific exception rejection status checking during job workflow operations and CJF validation
  2. Provides CJF-specific exception rejection status for job operations enabling proper CJF workflow control and rejection handling
  3. Critical for job workflow operations requiring CJF-specific exception rejection validation for CJF workflow state management
  4. Used in job status determination workflows for validating CJF-specific exception rejection states and CJF workflow handling
  5. Enables CJF-specific exception rejection management with rejection status validation for proper CJF job workflow control

SQL Query: |
  SELECT EXISTS (SELECT pv.id
                 FROM parameter_values pv
                          INNER JOIN parameters p ON pv.parameters_id = p.id
                          INNER JOIN exceptions e ON pv.id = e.parameter_values_id
                 WHERE p.target_entity_type = 'PROCESS'
                   AND e.status = 'REJECTED' AND pv.jobs_id = ?) AS result

Parameters:
  - jobId: Long (Job identifier to check CJF exception rejections for)

Returns: boolean (true if CJF exceptions are rejected for the job)
Transaction: Not Required
Error Handling: Returns false if no rejected CJF exceptions found for job
```

### Key Repository Usage Patterns

#### Pattern: save() for Parameter Exception Lifecycle Management
```yaml
Usage: parameterExceptionRepository.save(parameterException)
Purpose: "Create new parameter exceptions, update exception status, and manage exception approval workflows"

Business Logic Derivation:
  1. Used extensively in ParameterExceptionService for exception creation, approval, rejection, and lifecycle management
  2. Provides parameter exception persistence with status management, approval workflows, and exception state tracking
  3. Critical for parameter validation operations requiring exception creation and exception workflow management
  4. Used in exception approval workflows for status updates, approval processing, and exception resolution
  5. Enables parameter exception lifecycle management with comprehensive workflow support and status tracking

Transaction: Required (@Transactional annotation on repository)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findById() for Exception Context Operations
```yaml
Usage: parameterExceptionRepository.findById(exceptionId)
Purpose: "Retrieve parameter exception entity for exception-specific operations and workflow management"

Business Logic Derivation:
  1. Used extensively in ParameterExceptionService for exception context retrieval during approval and rejection workflows
  2. Critical for exception validation, exception status access, and exception-specific business logic operations
  3. Used in exception approval workflows, exception update operations, and exception status management
  4. Essential for exception context management and exception-based workflow operations
  5. Enables exception-centric operations with comprehensive exception information and workflow context

Transaction: Not Required
Error Handling: Throws ResourceNotFoundException if parameter exception not found
```

#### Pattern: Exception Status Validation for Job Workflows
```yaml
Usage: Multiple status checking methods for job workflow validation
Purpose: "Validate exception states for proper job workflow progression and user validation"

Business Logic Derivation:
  1. Exception status validation enables proper job workflow control and user-specific validation
  2. CJF-specific exception checking ensures proper CJF workflow management and validation
  3. User-specific exception validation enables proper user workflow control and permission management
  4. Exception rejection status enables proper workflow handling and exception resolution tracking
  5. Comprehensive exception status validation supports job workflow integrity and exception management

Transaction: Not Required for status checking operations
Error Handling: Returns appropriate boolean values for status validation
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Parameter Value State Determination
```yaml
Usage: findLatestException(parameterValueId) for parameter state calculation
Purpose: "Determine parameter value execution state based on latest exception status"

Business Logic Derivation:
  1. Parameter value state depends on latest exception status for proper workflow state calculation
  2. Exception status affects parameter execution state requiring latest exception information for state determination
  3. Parameter validation workflows require current exception status for proper parameter state management
  4. Latest exception information enables accurate parameter state calculation for workflow operations
  5. Exception-based state determination supports parameter validation workflows and execution state management

Common Usage Examples:
  - parameterExceptionRepository.findLatestException(parameterValueId) in ParameterExceptionService for state determination
  - Parameter value state calculation based on exception status for workflow management
  - Exception status evaluation for parameter execution state determination and validation
  - Latest exception information for parameter state calculation and workflow progression
  - Parameter validation state management based on exception workflow status and approval states

Transaction: Not Required
Error Handling: Returns empty list for parameter values without exceptions
```

### Pattern: Job Workflow Exception Validation
```yaml
Usage: Multiple exception status methods for job workflow control
Purpose: "Validate exception states for proper job workflow progression and user access control"

Business Logic Derivation:
  1. Job workflow progression depends on exception status validation for proper workflow control and user validation
  2. User-specific exception validation enables proper user access control and workflow permission management
  3. CJF-specific exception validation ensures proper CJF workflow management and validation requirements
  4. Exception rejection status enables proper workflow handling and exception resolution tracking
  5. Comprehensive exception validation supports job workflow integrity and exception-based workflow control

Common Usage Examples:
  - parameterExceptionRepository.isExceptionPendingOnUser(jobId, userId) for user workflow validation
  - parameterExceptionRepository.isCJFExceptionPendingOnUser(jobId) for CJF workflow validation
  - parameterExceptionRepository.isExceptionRejectedOnCjf(jobId) for CJF rejection status validation
  - Job workflow control based on exception status validation and user-specific exception states
  - Exception-based job workflow management with comprehensive status validation and control

Transaction: Not Required for status validation operations
Error Handling: Returns appropriate boolean values for workflow validation
```

### Pattern: Exception Approval Workflow Management
```yaml
Usage: save() operations for exception approval and rejection workflows
Purpose: "Manage exception approval workflows with status updates and approval processing"

Business Logic Derivation:
  1. Exception approval workflows require status updates for proper exception lifecycle management
  2. Exception approval and rejection operations need status persistence for workflow tracking
  3. Exception workflow management enables proper parameter validation and approval processes
  4. Exception status updates support approval workflows and exception resolution tracking
  5. Comprehensive exception workflow management with approval processing and status tracking

Common Usage Examples:
  - Exception creation with PENDING status for parameter validation rule violations
  - Exception approval with ACCEPTED status for workflow resolution and parameter validation
  - Exception rejection with REJECTED status for workflow handling and exception resolution
  - Exception status updates for approval workflow management and tracking
  - Exception lifecycle management with comprehensive approval processing and status tracking

Transaction: Required for exception status persistence
Error Handling: DataIntegrityViolationException for workflow constraint violations
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findLatestException, isExceptionPendingOnUser
  - isCJFExceptionPendingOnUser, isExceptionRejectedOnCjf, existsById, count

Transactional Methods:
  - save, delete, deleteById (repository marked with @Transactional)

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback per repository annotation)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid parameter_values_id, task_executions_id, facilities_id, jobs_id)
    * NOT NULL constraint violations (code, parameter_values_id, task_executions_id, facilities_id, jobs_id, status, rules_id)
    * Invalid enum values for status and previousState fields
    * Invalid JSON format in choices field
    * Unique constraint violations on exception code
  - EntityNotFoundException: Parameter exception not found by ID or criteria
  - OptimisticLockException: Concurrent parameter exception modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or criteria
  - ResourceNotFoundException: Parameter exception not found during operations
  - JsonProcessingException: Invalid JSON in choices field

Validation Rules:
  - code: Required, max length 20 characters, unique exception identifier
  - value: Optional, text field for exception value information
  - choices: Optional, must be valid JSON for exception choice options
  - parameterValue: Required, must reference existing parameter value, immutable after creation
  - taskExecution: Required, must reference existing task execution, immutable after creation, cascade = ALL
  - facility: Required, must reference existing facility, immutable after creation, cascade = ALL
  - job: Required, must reference existing job, immutable after creation
  - Status: Required, must be valid ParameterException enum value (PENDING, ACCEPTED, REJECTED)
  - initiatorsReason: Optional, text field for exception initiation reason
  - reviewersReason: Optional, text field for exception review reason
  - previousState: Optional, must be valid ParameterExecution enum value for state tracking
  - reason: Optional, general reason field for exception context
  - ruleId: Required, rule identifier for exception rule tracking

Business Constraints:
  - Parameter exceptions must maintain referential integrity with related entities for exception context
  - Exception status must follow defined workflow states (PENDING -> ACCEPTED/REJECTED) for workflow integrity
  - Exception immutable fields cannot be modified after creation for exception integrity and audit trail
  - Exception approval workflows must maintain proper status transitions for workflow consistency
  - CJF exceptions must be properly identified and managed for CJF workflow requirements
  - User-specific exception validation must be accurate for proper workflow control and access management
  - Exception lifecycle must maintain audit trail and status history for compliance and tracking
  - Exception rule associations must be maintained for proper validation rule tracking and management
  - Exception workflow permissions must be enforced for proper approval process control
  - Exception status validation must be consistent across job workflow operations for workflow integrity
```

## Parameter Exception Workflow Considerations

### Exception Lifecycle Management
```yaml
Creation: Exceptions are created with PENDING status when parameter validation rules are violated
Approval: Exceptions can be approved (ACCEPTED) by authorized reviewers for workflow resolution
Rejection: Exceptions can be rejected (REJECTED) by reviewers for workflow handling and resolution
Status Tracking: Exception status affects parameter execution state and job workflow progression
Audit Trail: Exception lifecycle maintains audit trail for compliance and tracking requirements
```

### Workflow Integration
```yaml
Parameter Validation: Exceptions are created when parameter validation rules are violated
Job Workflow: Exception status affects job workflow progression and user access control
User Validation: User-specific exception validation enables proper workflow permission management
CJF Management: CJF-specific exceptions require specialized handling for CJF workflow requirements
Approval Process: Exception approval workflows enable proper validation and resolution processes
```

### Compliance and Validation
```yaml
Rule Compliance: Exceptions track validation rule violations for compliance and audit requirements
Approval Tracking: Exception approval workflows maintain compliance with validation requirements
Status Validation: Exception status validation ensures proper workflow control and compliance
Audit Requirements: Exception lifecycle maintains audit trail for regulatory compliance and tracking
Workflow Integrity: Exception management ensures workflow integrity and validation rule compliance
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the ParameterException repository without JPA/Hibernate dependencies, focusing on exception workflow management and validation compliance patterns.
