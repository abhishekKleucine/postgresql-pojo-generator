# ICorrectionRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Correction
- **Primary Purpose**: Manages correction entities for parameter value corrections with workflow state management and audit tracking
- **Key Relationships**: Central correction entity linking ParameterValue, TaskExecution, Job, and Facility with correction workflow management
- **Performance Characteristics**: Moderate query volume with correction retrieval operations, correction listing, and correction status management
- **Business Context**: Core correction workflow component that handles parameter value correction lifecycle, approval workflows, and correction audit tracking

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| code | code | String | false | null |
| old_value | oldValue | String | true | null |
| new_value | newValue | String | true | null |
| old_choices | oldChoices | JsonNode | true | null |
| new_choices | newChoices | JsonNode | true | null |
| parameter_values_id | parameterValue.id | Long | false | null |
| task_executions_id | taskExecution.id | Long | false | null |
| facilities_id | facility.id | Long | false | null |
| jobs_id | job.id | Long | false | null |
| status | Status | State.Correction | false | null |
| initiators_reason | initiatorsReason | String | true | null |
| correctors_reason | correctorsReason | String | true | null |
| reviewers_reason | reviewersReason | String | true | null |
| previous_state | previousState | State.ParameterExecution | true | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | parameterValue | ParameterValue | LAZY | Parent parameter value, not null, immutable |
| @ManyToOne | taskExecution | TaskExecution | LAZY | Parent task execution, not null, immutable, cascade = ALL |
| @ManyToOne | facility | Facility | LAZY | Parent facility, not null, immutable, cascade = ALL |
| @ManyToOne | job | Job | LAZY | Parent job, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Correction entity)`
- `deleteById(Long id)`
- `delete(Correction entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (6 methods - ALL methods documented)

- `findLatestCorrection(Long parameterValueId)`
- `getLatestCorrectionByParameterValueId(Long parameterValueId)`
- `getAllCorrections(Long userId, Long facilityId, Long useCaseId, String status, String parameterName, String processName, Long jobId, Long initiatedBy, int limit, long offset)`
- `getAllCorrectionsCount(Long userId, Long facilityId, Long useCaseId, String status, String parameterName, String processName, Long jobId, Long initiatedBy)`
- `getAllCorrectionsByParameterValueId(Long parameterValueId)`
- `isCorrectionPending(Long jobId, Long userId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Correction> findById(Long id)
List<Correction> findAll()
Correction save(Correction entity)
void deleteById(Long id)
void delete(Correction entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: findLatestCorrection(Long parameterValueId)
```yaml
Signature: CorrectionListViewProjection findLatestCorrection(Long parameterValueId)
Purpose: "Find latest correction projection for parameter value for display and mapping operations"

Business Logic Derivation:
  1. Used in IParameterMapper for correction information display and parameter mapping
  2. Retrieves latest correction information as projection for efficient display
  3. Critical for parameter value display with correction status and information
  4. Used in parameter mapping operations to show correction status
  5. Enables efficient correction information retrieval for UI display operations

SQL Query: |
  SELECT c.id, c.code, c.status, c.created_at as createdAt,
         c.old_value as oldValue, c.new_value as newValue,
         c.initiators_reason as initiatorsReason,
         c.correctors_reason as correctorsReason,
         c.reviewers_reason as reviewersReason
  FROM corrections c
  WHERE c.parameter_values_id = ?
  ORDER BY c.id DESC
  LIMIT 1

Parameters:
  - parameterValueId: Long (Parameter value identifier to get latest correction for)

Returns: CorrectionListViewProjection (latest correction projection view)
Transaction: Not Required
Error Handling: Returns null if no correction found for parameter value
```

#### Method: getLatestCorrectionByParameterValueId(Long parameterValueId)
```yaml
Signature: Correction getLatestCorrectionByParameterValueId(Long parameterValueId)
Purpose: "Get latest correction entity for parameter value for audit and workflow operations"

Business Logic Derivation:
  1. Used in JobAuditService for correction audit trail and job audit operations
  2. Retrieves latest correction entity for comprehensive audit information
  3. Critical for job audit operations that need complete correction information
  4. Used in audit workflows to track correction history and status
  5. Enables comprehensive correction audit with full entity information

SQL Query: |
  SELECT c.* FROM corrections c
  WHERE c.parameter_values_id = ?
  ORDER BY c.id DESC
  LIMIT 1

Parameters:
  - parameterValueId: Long (Parameter value identifier to get latest correction entity for)

Returns: Correction (latest correction entity)
Transaction: Not Required
Error Handling: Returns null if no correction found for parameter value
```

#### Method: getAllCorrections(Long userId, Long facilityId, Long useCaseId, String status, String parameterName, String processName, Long jobId, Long initiatedBy, int limit, long offset)
```yaml
Signature: List<CorrectionListViewProjection> getAllCorrections(Long userId, Long facilityId, Long useCaseId, String status, String parameterName, String processName, Long jobId, Long initiatedBy, int limit, long offset)
Purpose: "Get paginated corrections list with complex filtering for correction management dashboard"

Business Logic Derivation:
  1. Used in CorrectionService for correction listing and management dashboard
  2. Provides comprehensive correction filtering with multiple search criteria
  3. Critical for correction management workflows and correction dashboard display
  4. Used in correction administration for filtered correction retrieval and management
  5. Enables complex correction discovery with multi-criteria filtering and pagination

SQL Query: |
  SELECT c.id, c.code, c.status, c.created_at as createdAt,
         c.old_value as oldValue, c.new_value as newValue,
         c.initiators_reason as initiatorsReason,
         c.correctors_reason as correctorsReason,
         c.reviewers_reason as reviewersReason,
         pv.parameter_id as parameterId, p.label as parameterName,
         j.code as jobCode, ch.name as processName,
         u_initiator.first_name as initiatorFirstName,
         u_initiator.last_name as initiatorLastName
  FROM corrections c
  INNER JOIN parameter_values pv ON c.parameter_values_id = pv.id
  INNER JOIN parameters p ON pv.parameter_id = p.id
  INNER JOIN jobs j ON c.jobs_id = j.id
  INNER JOIN checklists ch ON j.checklists_id = ch.id
  INNER JOIN users u_initiator ON c.created_by = u_initiator.id
  WHERE c.facilities_id = ?
    AND (? IS NULL OR c.created_by = ?)
    AND (? IS NULL OR ch.use_cases_id = ?)
    AND (? IS NULL OR c.status = ?)
    AND (? IS NULL OR LOWER(p.label) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(ch.name) LIKE LOWER(?))
    AND (? IS NULL OR c.jobs_id = ?)
    AND (? IS NULL OR c.created_by = ?)
    AND EXISTS (
      SELECT 1 FROM user_facilities_mapping ufm 
      WHERE ufm.users_id = ? AND ufm.facilities_id = c.facilities_id
    )
  ORDER BY c.created_at DESC
  LIMIT ? OFFSET ?

Parameters:
  - userId: Long (Current user identifier for access control)
  - facilityId: Long (Facility identifier for scoping)
  - useCaseId: Long (Use case identifier for filtering, nullable)
  - status: String (Correction status for filtering, nullable)
  - parameterName: String (Parameter name filter, nullable)
  - processName: String (Process/checklist name filter, nullable)
  - jobId: Long (Job identifier for filtering, nullable)
  - initiatedBy: Long (Initiator user identifier for filtering, nullable)
  - limit: int (Page size for pagination)
  - offset: long (Page offset for pagination)

Returns: List<CorrectionListViewProjection> (filtered correction projection views)
Transaction: Not Required
Error Handling: Returns empty list if no corrections match criteria
```

#### Method: getAllCorrectionsCount(Long userId, Long facilityId, Long useCaseId, String status, String parameterName, String processName, Long jobId, Long initiatedBy)
```yaml
Signature: long getAllCorrectionsCount(Long userId, Long facilityId, Long useCaseId, String status, String parameterName, String processName, Long jobId, Long initiatedBy)
Purpose: "Get total count of corrections matching filtering criteria for pagination support"

Business Logic Derivation:
  1. Used in CorrectionService for pagination support and total count display
  2. Provides total count for correction listing with same filtering criteria as getAllCorrections
  3. Critical for pagination implementation and total result count display
  4. Used in correction dashboard for pagination and result count information
  5. Enables accurate pagination with total count information for correction management

SQL Query: |
  SELECT COUNT(*) FROM corrections c
  INNER JOIN parameter_values pv ON c.parameter_values_id = pv.id
  INNER JOIN parameters p ON pv.parameter_id = p.id
  INNER JOIN jobs j ON c.jobs_id = j.id
  INNER JOIN checklists ch ON j.checklists_id = ch.id
  WHERE c.facilities_id = ?
    AND (? IS NULL OR c.created_by = ?)
    AND (? IS NULL OR ch.use_cases_id = ?)
    AND (? IS NULL OR c.status = ?)
    AND (? IS NULL OR LOWER(p.label) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(ch.name) LIKE LOWER(?))
    AND (? IS NULL OR c.jobs_id = ?)
    AND (? IS NULL OR c.created_by = ?)
    AND EXISTS (
      SELECT 1 FROM user_facilities_mapping ufm 
      WHERE ufm.users_id = ? AND ufm.facilities_id = c.facilities_id
    )

Parameters:
  - userId: Long (Current user identifier for access control)
  - facilityId: Long (Facility identifier for scoping)
  - useCaseId: Long (Use case identifier for filtering, nullable)
  - status: String (Correction status for filtering, nullable)
  - parameterName: String (Parameter name filter, nullable)
  - processName: String (Process/checklist name filter, nullable)
  - jobId: Long (Job identifier for filtering, nullable)
  - initiatedBy: Long (Initiator user identifier for filtering, nullable)

Returns: long (total count of corrections matching criteria)
Transaction: Not Required
Error Handling: Returns 0 if no corrections match criteria
```

#### Method: getAllCorrectionsByParameterValueId(Long parameterValueId)
```yaml
Signature: List<CorrectionListViewProjection> getAllCorrectionsByParameterValueId(Long parameterValueId)
Purpose: "Get all corrections for parameter value in ascending order for correction history display"

Business Logic Derivation:
  1. Used in JobService for parameter value correction history display and tracking
  2. Retrieves complete correction history for parameter value in chronological order
  3. Critical for correction history display and parameter value audit trail
  4. Used in job detail views to show parameter correction history and progression
  5. Enables comprehensive correction tracking with chronological correction information

SQL Query: |
  SELECT c.id, c.code, c.status, c.created_at as createdAt,
         c.old_value as oldValue, c.new_value as newValue,
         c.initiators_reason as initiatorsReason,
         c.correctors_reason as correctorsReason,
         c.reviewers_reason as reviewersReason
  FROM corrections c
  WHERE c.parameter_values_id = ?
  ORDER BY c.id ASC

Parameters:
  - parameterValueId: Long (Parameter value identifier to get correction history for)

Returns: List<CorrectionListViewProjection> (all corrections for parameter value in chronological order)
Transaction: Not Required
Error Handling: Returns empty list if no corrections found for parameter value
```

#### Method: isCorrectionPending(Long jobId, Long userId)
```yaml
Signature: boolean isCorrectionPending(Long jobId, Long userId)
Purpose: "Check if user has pending corrections for job for workflow validation and dashboard status"

Business Logic Derivation:
  1. Used in JobService for job status validation and user workflow management
  2. Determines if user has pending correction actions for job completion validation
  3. Critical for job workflow validation and user task management
  4. Used in job completion checks and user dashboard status display
  5. Enables workflow validation with pending correction identification for user management

SQL Query: |
  SELECT COUNT(*) > 0 FROM corrections c
  INNER JOIN correctors co ON c.id = co.corrections_id
  WHERE c.jobs_id = ? 
    AND co.users_id = ?
    AND c.status IN ('INITIATED', 'CORRECTED')

Parameters:
  - jobId: Long (Job identifier to check for pending corrections)
  - userId: Long (User identifier to check for pending correction actions)

Returns: boolean (true if user has pending corrections for the job)
Transaction: Not Required
Error Handling: Returns false if no pending corrections found for user and job
```

### Key Repository Usage Patterns

#### Pattern: save() for Correction Lifecycle Management
```yaml
Usage: correctionRepository.save(correction)
Purpose: "Create new corrections, update correction status, and manage correction workflow"

Business Logic Derivation:
  1. Used extensively throughout system for correction management operations
  2. Handles correction creation, status updates, and workflow progression
  3. Updates correction information and manages correction lifecycle state
  4. Critical for correction workflow management and correction status tracking
  5. Supports correction operations with parameter value and job association management

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findById() for Correction Context Operations
```yaml
Usage: correctionRepository.findById(correctionId)
Purpose: "Retrieve correction entity for correction-specific operations and workflow management"

Business Logic Derivation:
  1. Used extensively throughout system for correction context retrieval
  2. Critical for correction validation, status updates, and correction-specific operations
  3. Used in correction workflow operations, correction updates, and correction management
  4. Essential for correction context management and correction-based business logic
  5. Enables correction-centric operations with comprehensive correction information

Transaction: Not Required
Error Handling: Throws ResourceNotFoundException if correction not found
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findLatestCorrection, getLatestCorrectionByParameterValueId
  - getAllCorrections, getAllCorrectionsCount, getAllCorrectionsByParameterValueId
  - isCorrectionPending, existsById, count

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
    * Foreign key violations (invalid parameter_values_id, task_executions_id, facilities_id, jobs_id)
    * NOT NULL constraint violations (code, status, parameter_values_id, task_executions_id, facilities_id, jobs_id)
    * Invalid enum values for status and previousState fields
  - EntityNotFoundException: Correction not found by ID or criteria
  - OptimisticLockException: Concurrent correction modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or filtering criteria
  - ResourceNotFoundException: Correction not found during operations

Validation Rules:
  - code: Required, max length 20 characters, unique correction identifier
  - oldValue: Optional, text field for previous parameter value
  - newValue: Optional, text field for corrected parameter value
  - oldChoices: Optional, JSON field for previous choice-based parameter values
  - newChoices: Optional, JSON field for corrected choice-based parameter values
  - status: Required, must be valid Correction enum value (INITIATED, CORRECTED, ACCEPTED, REJECTED)
  - initiatorsReason: Optional, text field for correction initiation reason
  - correctorsReason: Optional, text field for correction completion reason
  - reviewersReason: Optional, text field for correction review reason
  - previousState: Optional, must be valid ParameterExecution enum value

Business Constraints:
  - Cannot modify immutable associations (parameterValue, taskExecution, facility, job) after creation
  - Correction status transitions must follow defined workflow (INITIATED -> CORRECTED -> ACCEPTED/REJECTED)
  - Cannot delete correction with active workflow dependencies
  - Correction code should be unique for tracking and reference purposes
  - Old and new values should be consistent with parameter type and validation rules
  - Choice-based corrections should have valid JSON structure for oldChoices and newChoices
  - Correction workflow must maintain proper audit trail with user attribution
  - Correction status updates should validate proper workflow progression
  - User access to corrections should respect facility-level security boundaries
  - Correction history should preserve chronological order for audit purposes
  - Pending correction validation should consider user role and assignment context
  - Complex filtering operations should maintain performance with proper indexing
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Correction repository without JPA/Hibernate dependencies.
