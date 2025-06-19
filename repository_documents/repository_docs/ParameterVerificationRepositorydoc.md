# IParameterVerificationRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ParameterVerification
- **Primary Purpose**: Manages parameter verification entities for parameter value verification workflows with peer and self verification support
- **Key Relationships**: Central verification entity linking ParameterValue, Job, User, and UserGroup with verification workflow management
- **Performance Characteristics**: Moderate to high query volume with verification retrieval operations, verification filtering, and verification status management
- **Business Context**: Core verification workflow component that handles parameter value verification lifecycle, peer/self verification processes, and verification audit tracking

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| parameter_values_id | parameterValue.id | Long | false | null |
| jobs_id | job.id | Long | false | null |
| users_id | user.id | Long | false | null |
| verification_type | verificationType | Type.VerificationType | false | null |
| verification_status | verificationStatus | State.ParameterVerification | false | null |
| comments | comments | String | true | null |
| user_groups_id | userGroup.id | Long | true | null |
| is_bulk | isBulk | boolean | false | false |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | parameterValue | ParameterValue | LAZY | Parent parameter value, not null, immutable |
| @ManyToOne | job | Job | LAZY | Parent job, not null, immutable |
| @ManyToOne | user | User | LAZY | User assigned for verification, not null, immutable |
| @ManyToOne | userGroup | UserGroup | LAZY | User group for bulk verification, optional, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(ParameterVerification entity)`
- `deleteById(Long id)`
- `delete(ParameterVerification entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<ParameterVerification> spec)`
- `findAll(Specification<ParameterVerification> spec, Pageable pageable)`
- `findAll(Specification<ParameterVerification> spec, Sort sort)`
- `findOne(Specification<ParameterVerification> spec)`
- `count(Specification<ParameterVerification> spec)`

### Custom Query Methods (10 methods - ALL methods documented)

- `findByJobIdAndParameterValueIdAndVerificationTypeAndUserId(Long jobId, Long parameterValueId, String verificationType, Long userId)`
- `findLatestSelfAndPeerVerificationOfParametersInJob(Long jobId)`
- `findLatestSelfAndPeerVerificationOfParameterValueId(Long parameterValueId)`
- `findByJobIdAndParameterIdAndVerificationType(Long jobId, Long parameterId, String verificationType)`
- `findByJobIdAndParameterValueIdIn(Long jobId, List<Long> parameterValueIds)`
- `getVerificationFilterView(String status, Long jobId, Long requestedTo, Long requestedBy, String parameterName, String processName, String objectId, int limit, long offset, Long facilityId, Long useCaseId)`
- `getVerificationFilterViewCount(String status, Long jobId, Long requestedTo, Long requestedBy, String parameterName, String processName, String objectId, Long facilityId, Long useCaseId)`
- `deleteStaleEntriesByParameterValueIdAndVerificationType(Long parameterValueId, String verificationType, String verificationStatus)`
- `isVerificationPendingOnUser(Long jobId, Long userId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<ParameterVerification> findById(Long id)
List<ParameterVerification> findAll()
ParameterVerification save(ParameterVerification entity)
void deleteById(Long id)
void delete(ParameterVerification entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<ParameterVerification> findAll(Specification<ParameterVerification> spec)
Page<ParameterVerification> findAll(Specification<ParameterVerification> spec, Pageable pageable)
List<ParameterVerification> findAll(Specification<ParameterVerification> spec, Sort sort)
Optional<ParameterVerification> findOne(Specification<ParameterVerification> spec)
long count(Specification<ParameterVerification> spec)
```

### Custom Query Methods

#### Method: findByJobIdAndParameterValueIdAndVerificationTypeAndUserId(Long jobId, Long parameterValueId, String verificationType, Long userId)
```yaml
Signature: ParameterVerification findByJobIdAndParameterValueIdAndVerificationTypeAndUserId(Long jobId, Long parameterValueId, String verificationType, Long userId)
Purpose: "Find specific parameter verification by job, parameter value, verification type, and user for verification workflow operations"

Business Logic Derivation:
  1. Used extensively in ParameterVerificationService for verification workflow management and validation
  2. Retrieves specific verification record for user and parameter value combination
  3. Critical for verification workflow validation and verification state checking
  4. Used in verification completion workflows to check existing verification status
  5. Enables user-specific verification management with parameter value context

SQL Query: |
  SELECT pv.* FROM parameter_verifications pv
  WHERE pv.jobs_id = ? 
    AND pv.parameter_values_id = ?
    AND pv.verification_type = ?
    AND pv.users_id = ?

Parameters:
  - jobId: Long (Job identifier for scoping)
  - parameterValueId: Long (Parameter value identifier for verification)
  - verificationType: String (Verification type - SELF or PEER)
  - userId: Long (User identifier for verification assignment)

Returns: ParameterVerification (specific verification record matching criteria)
Transaction: Not Required
Error Handling: Returns null if no verification found matching criteria
```

#### Method: findLatestSelfAndPeerVerificationOfParametersInJob(Long jobId)
```yaml
Signature: List<ParameterVerification> findLatestSelfAndPeerVerificationOfParametersInJob(Long jobId)
Purpose: "Find latest self and peer verifications for all parameters in job for verification status overview"

Business Logic Derivation:
  1. Used in ParameterVerificationService for job-level verification status retrieval and reporting
  2. Retrieves latest verification status for all parameters in job for dashboard display
  3. Critical for job verification overview and verification completion tracking
  4. Used in job verification status operations for comprehensive verification reporting
  5. Enables job-level verification management with latest verification status information

SQL Query: |
  SELECT DISTINCT ON (pv.parameter_values_id, pv.verification_type) pv.*
  FROM parameter_verifications pv
  WHERE pv.jobs_id = ?
    AND pv.verification_type IN ('SELF', 'PEER')
  ORDER BY pv.parameter_values_id, pv.verification_type, pv.id DESC

Parameters:
  - jobId: Long (Job identifier to get latest verifications for)

Returns: List<ParameterVerification> (latest self and peer verifications for job parameters)
Transaction: Not Required
Error Handling: Returns empty list if no verifications found for job
```

#### Method: findLatestSelfAndPeerVerificationOfParameterValueId(Long parameterValueId)
```yaml
Signature: List<ParameterVerification> findLatestSelfAndPeerVerificationOfParameterValueId(Long parameterValueId)
Purpose: "Find latest self and peer verifications for specific parameter value for verification audit and migration"

Business Logic Derivation:
  1. Used in JobLogs migration service for parameter verification audit trail retrieval
  2. Retrieves latest verification status for specific parameter value for audit purposes
  3. Critical for parameter verification audit and migration operations
  4. Used in job log migration workflows to capture verification history
  5. Enables parameter-specific verification audit with latest verification information

SQL Query: |
  SELECT DISTINCT ON (pv.verification_type) pv.*
  FROM parameter_verifications pv
  WHERE pv.parameter_values_id = ?
    AND pv.verification_type IN ('SELF', 'PEER')
  ORDER BY pv.verification_type, pv.id DESC

Parameters:
  - parameterValueId: Long (Parameter value identifier to get latest verifications for)

Returns: List<ParameterVerification> (latest self and peer verifications for parameter value)
Transaction: Not Required
Error Handling: Returns empty list if no verifications found for parameter value
```

#### Method: findByJobIdAndParameterIdAndVerificationType(Long jobId, Long parameterId, String verificationType)
```yaml
Signature: ParameterVerification findByJobIdAndParameterIdAndVerificationType(Long jobId, Long parameterId, String verificationType)
Purpose: "Find parameter verification by job, parameter, and verification type for parameter execution validation"

Business Logic Derivation:
  1. Used in ParameterExecutionService for parameter execution validation and verification checking
  2. Retrieves verification record for parameter template and verification type combination
  3. Critical for parameter execution workflow validation and verification requirement checking
  4. Used in parameter execution validation to check verification completion status
  5. Enables parameter-level verification validation with verification type specificity

SQL Query: |
  SELECT pv.* FROM parameter_verifications pv
  INNER JOIN parameter_values pval ON pv.parameter_values_id = pval.id
  WHERE pv.jobs_id = ? 
    AND pval.parameter_id = ?
    AND pv.verification_type = ?
  ORDER BY pv.id DESC
  LIMIT 1

Parameters:
  - jobId: Long (Job identifier for scoping)
  - parameterId: Long (Parameter template identifier)
  - verificationType: String (Verification type - SELF or PEER)

Returns: ParameterVerification (verification record for parameter and type)
Transaction: Not Required
Error Handling: Returns null if no verification found for parameter and type
```

#### Method: findByJobIdAndParameterValueIdIn(Long jobId, List<Long> parameterValueIds)
```yaml
Signature: List<ParameterVerification> findByJobIdAndParameterValueIdIn(Long jobId, List<Long> parameterValueIds)
Purpose: "Find verifications for multiple parameter values in job for bulk verification operations"

Business Logic Derivation:
  1. Used in TaskExecutionService for bulk verification retrieval and task execution management
  2. Enables efficient bulk verification loading for task execution verification operations
  3. Critical for task execution verification management and bulk verification processing
  4. Used in task execution workflows that need verification status for multiple parameters
  5. Supports efficient bulk verification operations for task execution management

SQL Query: |
  SELECT pv.* FROM parameter_verifications pv
  WHERE pv.jobs_id = ? 
    AND pv.parameter_values_id IN (?)

Parameters:
  - jobId: Long (Job identifier for scoping)
  - parameterValueIds: List<Long> (Parameter value identifiers to get verifications for)

Returns: List<ParameterVerification> (verifications for specified parameter values)
Transaction: Not Required
Error Handling: Returns empty list if no verifications found for parameter values
```

#### Method: getVerificationFilterView(String status, Long jobId, Long requestedTo, Long requestedBy, String parameterName, String processName, String objectId, int limit, long offset, Long facilityId, Long useCaseId)
```yaml
Signature: List<ParameterVerificationListViewProjection> getVerificationFilterView(String status, Long jobId, Long requestedTo, Long requestedBy, String parameterName, String processName, String objectId, int limit, long offset, Long facilityId, Long useCaseId)
Purpose: "Get paginated verifications list with complex filtering for verification management dashboard"

Business Logic Derivation:
  1. Used in verification management service for verification listing and dashboard operations
  2. Provides comprehensive verification filtering with multiple search criteria
  3. Critical for verification management workflows and verification dashboard display
  4. Used in verification administration for filtered verification retrieval and management
  5. Enables complex verification discovery with multi-criteria filtering and pagination

SQL Query: |
  SELECT pv.id, pv.verification_status as status, pv.created_at as createdAt,
         pv.verification_type as verificationType, pv.comments,
         pval.parameter_id as parameterId, p.label as parameterName,
         j.code as jobCode, ch.name as processName,
         u_requested_to.first_name as requestedToFirstName,
         u_requested_to.last_name as requestedToLastName,
         u_requested_by.first_name as requestedByFirstName,
         u_requested_by.last_name as requestedByLastName,
         eo.external_id as objectId
  FROM parameter_verifications pv
  INNER JOIN parameter_values pval ON pv.parameter_values_id = pval.id
  INNER JOIN parameters p ON pval.parameter_id = p.id
  INNER JOIN jobs j ON pv.jobs_id = j.id
  INNER JOIN checklists ch ON j.checklists_id = ch.id
  INNER JOIN users u_requested_to ON pv.users_id = u_requested_to.id
  LEFT JOIN users u_requested_by ON pv.created_by = u_requested_by.id
  LEFT JOIN entity_objects eo ON pval.entity_object_id = eo.id
  WHERE pv.facilities_id = ?
    AND (? IS NULL OR ch.use_cases_id = ?)
    AND (? IS NULL OR pv.verification_status = ?)
    AND (? IS NULL OR pv.jobs_id = ?)
    AND (? IS NULL OR pv.users_id = ?)
    AND (? IS NULL OR pv.created_by = ?)
    AND (? IS NULL OR LOWER(p.label) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(ch.name) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(eo.external_id) LIKE LOWER(?))
  ORDER BY pv.created_at DESC
  LIMIT ? OFFSET ?

Parameters:
  - status: String (Verification status for filtering, nullable)
  - jobId: Long (Job identifier for filtering, nullable)
  - requestedTo: Long (User identifier for verification assignment filtering, nullable)
  - requestedBy: Long (User identifier for verification requester filtering, nullable)
  - parameterName: String (Parameter name filter, nullable)
  - processName: String (Process/checklist name filter, nullable)
  - objectId: String (Entity object ID filter, nullable)
  - limit: int (Page size for pagination)
  - offset: long (Page offset for pagination)
  - facilityId: Long (Facility identifier for scoping)
  - useCaseId: Long (Use case identifier for filtering, nullable)

Returns: List<ParameterVerificationListViewProjection> (filtered verification projection views)
Transaction: Not Required
Error Handling: Returns empty list if no verifications match criteria
```

#### Method: getVerificationFilterViewCount(String status, Long jobId, Long requestedTo, Long requestedBy, String parameterName, String processName, String objectId, Long facilityId, Long useCaseId)
```yaml
Signature: long getVerificationFilterViewCount(String status, Long jobId, Long requestedTo, Long requestedBy, String parameterName, String processName, String objectId, Long facilityId, Long useCaseId)
Purpose: "Get total count of verifications matching filtering criteria for pagination support"

Business Logic Derivation:
  1. Used in verification management service for pagination support and total count display
  2. Provides total count for verification listing with same filtering criteria as getVerificationFilterView
  3. Critical for pagination implementation and total result count display
  4. Used in verification dashboard for pagination and result count information
  5. Enables accurate pagination with total count information for verification management

SQL Query: |
  SELECT COUNT(*) FROM parameter_verifications pv
  INNER JOIN parameter_values pval ON pv.parameter_values_id = pval.id
  INNER JOIN parameters p ON pval.parameter_id = p.id
  INNER JOIN jobs j ON pv.jobs_id = j.id
  INNER JOIN checklists ch ON j.checklists_id = ch.id
  LEFT JOIN entity_objects eo ON pval.entity_object_id = eo.id
  WHERE pv.facilities_id = ?
    AND (? IS NULL OR ch.use_cases_id = ?)
    AND (? IS NULL OR pv.verification_status = ?)
    AND (? IS NULL OR pv.jobs_id = ?)
    AND (? IS NULL OR pv.users_id = ?)
    AND (? IS NULL OR pv.created_by = ?)
    AND (? IS NULL OR LOWER(p.label) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(ch.name) LIKE LOWER(?))
    AND (? IS NULL OR LOWER(eo.external_id) LIKE LOWER(?))

Parameters:
  - status: String (Verification status for filtering, nullable)
  - jobId: Long (Job identifier for filtering, nullable)
  - requestedTo: Long (User identifier for verification assignment filtering, nullable)
  - requestedBy: Long (User identifier for verification requester filtering, nullable)
  - parameterName: String (Parameter name filter, nullable)
  - processName: String (Process/checklist name filter, nullable)
  - objectId: String (Entity object ID filter, nullable)
  - facilityId: Long (Facility identifier for scoping)
  - useCaseId: Long (Use case identifier for filtering, nullable)

Returns: long (total count of verifications matching criteria)
Transaction: Not Required
Error Handling: Returns 0 if no verifications match criteria
```

#### Method: deleteStaleEntriesByParameterValueIdAndVerificationType(Long parameterValueId, String verificationType, String verificationStatus)
```yaml
Signature: void deleteStaleEntriesByParameterValueIdAndVerificationType(Long parameterValueId, String verificationType, String verificationStatus)
Purpose: "Delete stale verification entries for parameter value cleanup and verification workflow management"

Business Logic Derivation:
  1. Used extensively in ParameterVerificationService for verification workflow cleanup and state management
  2. Removes stale verification entries during verification workflow transitions
  3. Critical for verification workflow cleanup and verification state consistency
  4. Used in verification status updates to clean up pending or recalled verifications
  5. Enables verification workflow management with stale entry cleanup for data consistency

SQL Query: |
  DELETE FROM parameter_verifications 
  WHERE parameter_values_id = ? 
    AND verification_type = ?
    AND verification_status = ?

Parameters:
  - parameterValueId: Long (Parameter value identifier for cleanup scope)
  - verificationType: String (Verification type for cleanup - SELF or PEER)
  - verificationStatus: String (Verification status for cleanup - PENDING, RECALLED, etc.)

Returns: void
Transaction: Required (uses @Modifying)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: isVerificationPendingOnUser(Long jobId, Long userId)
```yaml
Signature: boolean isVerificationPendingOnUser(Long jobId, Long userId)
Purpose: "Check if user has pending verifications for job for workflow validation and dashboard status"

Business Logic Derivation:
  1. Used in JobService for job status validation and user workflow management
  2. Determines if user has pending verification actions for job completion validation
  3. Critical for job workflow validation and user task management
  4. Used in job completion checks and user dashboard status display
  5. Enables workflow validation with pending verification identification for user management

SQL Query: |
  SELECT COUNT(*) > 0 FROM parameter_verifications pv
  WHERE pv.jobs_id = ? 
    AND pv.users_id = ?
    AND pv.verification_status = 'PENDING'

Parameters:
  - jobId: Long (Job identifier to check for pending verifications)
  - userId: Long (User identifier to check for pending verification actions)

Returns: boolean (true if user has pending verifications for the job)
Transaction: Not Required
Error Handling: Returns false if no pending verifications found for user and job
```

### Key Repository Usage Patterns

#### Pattern: save() for Verification Lifecycle Management
```yaml
Usage: parameterVerificationRepository.save(parameterVerification)
Purpose: "Create new verifications, update verification status, and manage verification workflow"

Business Logic Derivation:
  1. Used extensively throughout system for verification management operations
  2. Handles verification creation, status updates, and workflow progression
  3. Updates verification information and manages verification lifecycle state
  4. Critical for verification workflow management and verification status tracking
  5. Supports verification operations with parameter value and job association management

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: saveAll() for Bulk Verification Operations
```yaml
Usage: parameterVerificationRepository.saveAll(parameterVerifications)
Purpose: "Bulk verification creation and updates for efficient multi-verification operations"

Business Logic Derivation:
  1. Used in ParameterVerificationService and TaskExecutionService for bulk verification operations
  2. Enables efficient bulk verification creation with minimal database transactions
  3. Critical for bulk verification workflows and verification batch processing
  4. Used in peer verification scenarios where multiple users need verification assignments
  5. Supports efficient bulk verification operations with transaction batching

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findAll(specification) for Dynamic Verification Queries
```yaml
Usage: parameterVerificationRepository.findAll(specification, pageable)
Purpose: "Dynamic verification discovery with complex filtering and pagination"

Business Logic Derivation:
  1. Used for advanced verification search and listing operations
  2. Applies dynamic specifications for multi-criteria verification filtering
  3. Supports pagination for large verification datasets and verification management
  4. Enables flexible verification discovery and management operations
  5. Critical for verification management APIs and verification administration functionality

Transaction: Not Required
Error Handling: Returns empty page if no matches found
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByJobIdAndParameterValueIdAndVerificationTypeAndUserId
  - findLatestSelfAndPeerVerificationOfParametersInJob, findLatestSelfAndPeerVerificationOfParameterValueId
  - findByJobIdAndParameterIdAndVerificationType, findByJobIdAndParameterValueIdIn
  - getVerificationFilterView, getVerificationFilterViewCount, isVerificationPendingOnUser
  - existsById, count, findAll(Specification), findOne(Specification), count(Specification)

Transactional Methods:
  - save, delete, deleteById, saveAll, deleteStaleEntriesByParameterValueIdAndVerificationType

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid parameter_values_id, jobs_id, users_id, user_groups_id)
    * NOT NULL constraint violations (parameter_values_id, jobs_id, users_id, verification_type, verification_status)
    * Invalid enum values for verificationType and verificationStatus fields
  - EntityNotFoundException: ParameterVerification not found by ID or criteria
  - OptimisticLockException: Concurrent verification modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or filtering criteria
  - ResourceNotFoundException: ParameterVerification not found during operations

Validation Rules:
  - parameterValue: Required, must reference existing parameter value, immutable after creation
  - job: Required, must reference existing job, immutable after creation
  - user: Required, must reference existing user assigned for verification, immutable after creation
  - verificationType: Required, must be valid VerificationType enum value (SELF, PEER, NONE)
  - verificationStatus: Required, must be valid ParameterVerification enum value (PENDING, ACCEPTED, REJECTED, RECALLED)
  - comments: Optional, text field for verification comments and feedback
  - userGroup: Optional, must reference existing user group for bulk verification operations
  - isBulk: Defaults to false, indicates if verification is part of bulk operation

Business Constraints:
  - Cannot modify immutable associations (parameterValue, job, user, userGroup) after creation
  - Verification status transitions must follow defined workflow (PENDING -> ACCEPTED/REJECTED/RECALLED)
  - Cannot delete verification with active workflow dependencies
  - User must have appropriate permissions for verification assignment
  - Self verification can only be performed by parameter value creator or assigned user
  - Peer verification requires different user than parameter value creator
  - Bulk verification operations should maintain consistency across multiple verifications
  - Verification workflow must maintain proper audit trail with user attribution
  - Verification status updates should validate proper workflow progression
  - User access to verifications should respect facility-level security boundaries
  - Verification history should preserve chronological order for audit purposes
  - Pending verification validation should consider user role and assignment context
  - Complex filtering operations should maintain performance with proper indexing
  - Stale entry cleanup should maintain verification data integrity
  - Verification type consistency should be maintained across parameter verification requirements
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the ParameterVerification repository without JPA/Hibernate dependencies.
