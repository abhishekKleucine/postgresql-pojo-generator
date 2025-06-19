# IReviewerRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Reviewer (extends UserAuditIdentifiableBase)
- **Primary Purpose**: Manages reviewer entities for correction review management with reviewer assignment, correction review tracking, and review action functionality
- **Key Relationships**: Links User, UserGroup, and Correction entities for comprehensive correction review management and reviewer workflow control
- **Performance Characteristics**: Moderate query volume with correction-based retrieval and reviewer lifecycle management operations
- **Business Context**: Correction review management component that provides correction reviewer assignment, review workflow tracking, reviewer action management, and correction approval functionality for correction processing and quality control workflows

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditIdentifiableBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| users_id | user.id | Long | false | null | Foreign key to users, reviewer assignment |
| user_groups_id | userGroup.id | Long | false | null | Foreign key to user_groups, group reviewer assignment |
| corrections_id | correctionId / correction.id | Long | false | null | Foreign key to corrections, immutable |
| action_performed | actionPerformed | boolean | false | false | Review action completion status |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | user | User | LAZY | Assigned reviewer user, not null |
| @ManyToOne | userGroup | UserGroup | LAZY | Assigned reviewer user group, not null |
| @ManyToOne | correction | Correction | LAZY | Associated correction, not null, cascade ALL, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Reviewer entity)`
- `saveAll(Iterable<Reviewer> entities)`
- `deleteById(Long id)`
- `delete(Reviewer entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (2 methods - ALL methods documented)

- `findByCorrectionId(Long correctionId)`
- `findAllByCorrectionIdIn(Set<Long> correctionIdSet)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Reviewer> findById(Long id)
List<Reviewer> findAll()
Reviewer save(Reviewer entity)
List<Reviewer> saveAll(Iterable<Reviewer> entities)
void deleteById(Long id)
void delete(Reviewer entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: findByCorrectionId(Long correctionId)
```yaml
Signature: List<Reviewer> findByCorrectionId(Long correctionId)
Purpose: "Find reviewers by correction ID for correction review management and reviewer workflow processing"

Business Logic Derivation:
  1. Used extensively in CorrectionService for reviewer retrieval during correction review workflows and reviewer management operations
  2. Provides correction-specific reviewer access for correction workflows enabling comprehensive review management and reviewer functionality
  3. Critical for correction review operations requiring reviewer identification for correction processing and review management
  4. Used in correction management workflows for accessing correction reviewers for review operations and reviewer processing
  5. Enables correction review management with reviewer identification for comprehensive correction processing and review control

SQL Query: |
  SELECT r.* FROM reviewers r
  WHERE r.corrections_id = ?

Parameters:
  - correctionId: Long (Correction identifier for reviewer retrieval)

Returns: List<Reviewer> (reviewers assigned to the correction)
Transaction: Required (class-level @Transactional annotation)
Error Handling: Returns empty list if no reviewers found for correction
```

#### Method: findAllByCorrectionIdIn(Set<Long> correctionIdSet)
```yaml
Signature: List<Reviewer> findAllByCorrectionIdIn(Set<Long> correctionIdSet)
Purpose: "Find reviewers by multiple correction IDs for bulk correction review management and batch reviewer operations"

Business Logic Derivation:
  1. Used in CorrectionService for bulk reviewer retrieval during batch correction processing and bulk review management operations
  2. Provides efficient bulk reviewer access for correction workflows enabling comprehensive batch review management and reviewer functionality
  3. Critical for bulk correction operations requiring multiple reviewer identification for batch correction processing and review management
  4. Used in batch correction workflows for accessing multiple correction reviewers for bulk review operations and reviewer processing
  5. Enables bulk correction review management with efficient reviewer retrieval for comprehensive batch processing and review control

SQL Query: |
  SELECT r.* FROM reviewers r
  WHERE r.corrections_id IN (?, ?, ?, ...)

Parameters:
  - correctionIdSet: Set<Long> (Set of correction identifiers for bulk reviewer retrieval)

Returns: List<Reviewer> (reviewers assigned to the corrections in the set)
Transaction: Required (class-level @Transactional annotation)
Error Handling: Returns empty list if no reviewers found for any corrections in the set
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Reviewer Assignment Management
```yaml
Usage: reviewerRepository.saveAll(reviewerList)
Purpose: "Create reviewer assignments in bulk for correction review management and reviewer workflow setup"

Business Logic Derivation:
  1. Used in CorrectionService for bulk reviewer assignment creation during correction review setup and reviewer management operations
  2. Provides efficient bulk reviewer persistence for correction workflows enabling comprehensive review assignment and reviewer functionality
  3. Critical for correction review operations requiring bulk reviewer assignment for correction processing and review management
  4. Used in correction management workflows for bulk reviewer assignment and review workflow setup operations
  5. Enables correction review management with efficient reviewer assignment for comprehensive correction processing and review control

Transaction: Required (class-level @Transactional annotation)
Error Handling: DataIntegrityViolationException for bulk constraint violations, reviewer assignment conflicts
```

#### Pattern: save() for Review Action Management
```yaml
Usage: reviewerRepository.save(reviewer)
Purpose: "Update reviewer action status for correction review management and review workflow tracking"

Business Logic Derivation:
  1. Used extensively in CorrectionService for reviewer action status updates during correction review processing and workflow management
  2. Provides reviewer status persistence for correction workflows enabling comprehensive review tracking and reviewer functionality
  3. Critical for correction review operations requiring reviewer action tracking for correction processing and review management
  4. Used in review workflow processing for updating reviewer action status and review completion operations
  5. Enables correction review management with reviewer action tracking for comprehensive correction processing and review control

Transaction: Required (class-level @Transactional annotation)
Error Handling: DataIntegrityViolationException for constraint violations, reviewer status conflicts
```

#### Pattern: Correction Review Workflow Management
```yaml
Usage: Reviewer retrieval and management for correction review workflows and reviewer processing
Purpose: "Manage correction reviewers for comprehensive review workflow functionality and correction approval processing"

Business Logic Derivation:
  1. Correction review workflows enable proper correction processing through reviewer management and review validation functionality
  2. Review workflow management supports correction requirements and reviewer functionality for correction processing workflows
  3. Reviewer management operations depend on correction-specific access for proper review workflow and correction management
  4. Correction processing requires reviewer management for comprehensive correction functionality and review control
  5. Review processing requires comprehensive reviewer access and workflow functionality for correction management

Transaction: Required for correction review operations and reviewer management
Error Handling: Correction review error handling and reviewer workflow validation
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Correction Review Lifecycle Management
```yaml
Usage: Complete reviewer lifecycle for correction review and approval management
Purpose: "Manage correction reviewers for comprehensive review lifecycle functionality and correction approval processing"

Business Logic Derivation:
  1. Correction review lifecycle management provides approval functionality through reviewer assignment and review workflow management
  2. Review lifecycle includes reviewer assignment, review processing, action tracking, and completion workflows for correction control
  3. Reviewer management operations require correction review processing for approval management and correction control
  4. Correction review operations enable comprehensive approval functionality with reviewer capabilities and workflow management
  5. Review lifecycle management supports correction requirements and approval functionality for correction review processing

Common Usage Examples:
  - reviewerRepository.findByCorrectionId() in CorrectionService for reviewer retrieval during correction review workflows
  - reviewerRepository.saveAll() for bulk reviewer assignment during correction review setup and workflow initialization
  - reviewerRepository.save() for reviewer action status updates during review processing and completion operations
  - reviewerRepository.findAllByCorrectionIdIn() for bulk reviewer retrieval during batch correction processing and review management
  - Comprehensive correction review with reviewer lifecycle management for approval functionality and workflow control

Transaction: Required for lifecycle operations and reviewer management
Error Handling: Correction review error handling and reviewer lifecycle validation
```

### Pattern: Bulk Correction Review and Batch Processing
```yaml
Usage: Bulk reviewer management for batch correction processing and bulk review workflows
Purpose: "Manage bulk correction reviewers for comprehensive batch review functionality and bulk correction processing"

Business Logic Derivation:
  1. Bulk correction review operations require efficient reviewer management for comprehensive batch processing and review functionality
  2. Batch review management supports bulk correction requirements and reviewer functionality for batch processing workflows
  3. Bulk reviewer operations ensure proper batch correction processing through reviewer management and review control
  4. Batch correction workflows coordinate reviewer management with correction processing for comprehensive batch operations
  5. Bulk processing supports correction requirements and reviewer functionality for comprehensive batch correction management

Common Usage Examples:
  - Bulk reviewer retrieval for batch correction processing and review management operations
  - Batch reviewer assignment for bulk correction review workflows and reviewer management functionality
  - Bulk review status tracking for batch correction processing and reviewer workflow management
  - Batch correction approval processing with reviewer management and review functionality
  - Comprehensive batch review management with bulk reviewer operations for correction processing

Transaction: Required for bulk operations and batch reviewer management
Error Handling: Bulk operation error handling and batch reviewer validation
```

### Pattern: Review Action and Status Management
```yaml
Usage: Reviewer action tracking for review completion and status management functionality
Purpose: "Manage reviewer actions for comprehensive review status tracking and completion functionality"

Business Logic Derivation:
  1. Review action management enables correction completion functionality through reviewer action tracking and status management
  2. Action tracking supports review completion requirements and reviewer functionality for review processing workflows
  3. Reviewer action operations ensure proper review completion through action management and status control
  4. Review workflows coordinate action tracking with completion processing for comprehensive review operations
  5. Action management supports review requirements and completion functionality for comprehensive reviewer processing

Common Usage Examples:
  - Reviewer action status updates for review completion tracking and workflow management operations
  - Review completion validation with reviewer action verification and status management functionality
  - Reviewer workflow tracking for correction review processing and action management operations
  - Review status management for correction approval and reviewer action tracking functionality
  - Comprehensive review action management with status tracking and completion functionality for reviewer workflows

Transaction: Required for action management operations and status tracking
Error Handling: Action management error handling and review status validation
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByCorrectionId, findAllByCorrectionIdIn, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (users_id, user_groups_id, corrections_id)
    * Foreign key violations (invalid users_id, user_groups_id, corrections_id references)
    * Unique constraint violations for reviewer assignments
    * Reviewer assignment integrity constraint violations
  - EntityNotFoundException: Reviewer not found by ID or criteria
  - OptimisticLockException: Concurrent reviewer modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or correction context
  - ConstraintViolationException: Reviewer constraint violations

Validation Rules:
  - user: Required, must reference existing user for reviewer assignment
  - userGroup: Required, must reference existing user group for group reviewer assignment
  - correction: Required, must reference existing correction, immutable after creation
  - correctionId: Derived from correction relationship, immutable for correction context integrity
  - actionPerformed: Boolean flag indicating review action completion, defaults to false

Business Constraints:
  - Reviewers should be unique per correction and user/user group combination for proper review integrity
  - User, user group, and correction references must be valid for reviewer integrity and correction functionality
  - Reviewers must support correction workflow requirements and review functionality
  - Review lifecycle management must maintain referential integrity and correction workflow functionality consistency
  - Reviewer assignment management must ensure proper correction workflow control and review functionality
  - Reviewer associations must support correction requirements and review functionality for correction processing
  - Review operations must maintain transaction consistency and constraint integrity for correction management
  - Review lifecycle management must maintain correction functionality and reviewer consistency
  - Correction management must maintain reviewer integrity and correction workflow requirements
  - Approval operations must ensure proper correction workflow management and reviewer control
```

## Reviewer Considerations

### Correction Review Integration
```yaml
Review Integration: Reviewers enable correction functionality through review management and approval functionality
Review Management: Reviewer associations enable correction functionality with comprehensive review capabilities
Review Lifecycle: Reviewer lifecycle includes assignment, review processing, and completion operations for correction functionality
Correction Management: Comprehensive correction management for review functionality and correction requirements during correction workflows
Approval Control: Reviewer approval control for correction functionality and lifecycle management in correction processing
```

### User and Group Assignment Integration
```yaml
User Assignment: Individual user reviewer assignment for correction review functionality and user-specific review management
Group Assignment: User group reviewer assignment for group-based correction review and comprehensive group review functionality
Assignment Management: Reviewer assignment management with user and group coordination for comprehensive review assignment
User Integration: User reviewer integration with correction review and user functionality for reviewer workflows
Group Integration: User group reviewer integration with correction review and group functionality for comprehensive review management
```

### Review Action and Status Integration
```yaml
Action Tracking: Reviewer action tracking for review completion and status management functionality
Status Management: Review status management with action completion tracking and comprehensive status functionality
Action Control: Reviewer action control for review completion and action management functionality
Completion Tracking: Review completion tracking with action status and comprehensive completion functionality
Workflow Status: Review workflow status management with action tracking and completion functionality for reviewer workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Reviewer repository without JPA/Hibernate dependencies, focusing on correction review management and reviewer workflow patterns.
