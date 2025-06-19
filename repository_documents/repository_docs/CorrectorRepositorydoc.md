# ICorrectorRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Corrector (extends UserAuditIdentifiableBase)
- **Primary Purpose**: Manages corrector entities for correction workflow management with correction assignment tracking and corrector action management functionality
- **Key Relationships**: Links Correction, User, and UserGroup entities for comprehensive correction workflow management and corrector tracking
- **Performance Characteristics**: Low to medium query volume with correction retrieval, corrector management operations, and correction workflow tracking
- **Business Context**: Correction workflow component that provides corrector assignment management, correction tracking, action management, and correction workflow functionality for correction workflows

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditIdentifiableBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| corrections_id | correctionId / correction.id | Long | false | null | Foreign key to corrections |
| users_id | user.id | Long | false | null | Foreign key to users |
| user_groups_id | userGroup.id | Long | false | null | Foreign key to user_groups |
| action_performed | actionPerformed | boolean | false | false | Corrector action completion status |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | correction | Correction | LAZY | Associated correction, not null, cascade = ALL |
| @ManyToOne | user | User | LAZY | Assigned corrector user, not null |
| @ManyToOne | userGroup | UserGroup | LAZY | Assigned corrector user group, not null |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Corrector entity)`
- `saveAll(Iterable<Corrector> entities)`
- `deleteById(Long id)`
- `delete(Corrector entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (2 methods - ALL methods documented)

**Corrector Retrieval Methods (2 methods):**
- `findByCorrectionId(Long correctionId)`
- `findAllByCorrectionIdIn(Set<Long> correctionIdSet)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Corrector> findById(Long id)
List<Corrector> findAll()
Corrector save(Corrector entity)
List<Corrector> saveAll(Iterable<Corrector> entities)
void deleteById(Long id)
void delete(Corrector entity)
boolean existsById(Long id)
long count()
```

### Corrector Retrieval Methods

#### Method: findByCorrectionId(Long correctionId)
```yaml
Signature: List<Corrector> findByCorrectionId(Long correctionId)
Purpose: "Find correctors by correction ID for correction workflow management and corrector tracking"

Business Logic Derivation:
  1. Used extensively across multiple services for corrector retrieval during correction workflow management and corrector tracking operations
  2. Provides correction-specific corrector access for correction workflows enabling comprehensive corrector management and correction functionality
  3. Critical for correction workflow operations requiring corrector identification for correction management and workflow control
  4. Used in correction workflow workflows for accessing correction correctors for workflow operations and correction processing
  5. Enables correction workflow management with corrector identification for comprehensive correction processing and workflow control

SQL Query: |
  SELECT c FROM Corrector c WHERE c.correctionId = ?

Parameters:
  - correctionId: Long (Correction identifier for corrector retrieval)

Returns: List<Corrector> (correctors for specified correction)
Transaction: Required (@Transactional annotation at repository level)
Error Handling: Returns empty list if no correctors found for correction
```

#### Method: findAllByCorrectionIdIn(Set<Long> correctionIdSet)
```yaml
Signature: List<Corrector> findAllByCorrectionIdIn(Set<Long> correctionIdSet)
Purpose: "Find correctors by multiple correction IDs for bulk corrector retrieval and management"

Business Logic Derivation:
  1. Used in CorrectionService for bulk corrector retrieval during bulk correction workflow management and corrector tracking operations
  2. Provides efficient bulk corrector access for correction workflows enabling comprehensive bulk corrector management and correction functionality
  3. Critical for bulk correction operations requiring corrector identification for bulk correction management and workflow control
  4. Used in bulk correction workflows for accessing multiple correction correctors for bulk operations and correction processing
  5. Enables bulk correction workflow management with efficient corrector retrieval for comprehensive bulk correction processing and workflow control

SQL Query: |
  SELECT c FROM Corrector c WHERE c.correctionId IN (?, ?, ?, ...)

Parameters:
  - correctionIdSet: Set<Long> (Set of correction identifiers for bulk corrector retrieval)

Returns: List<Corrector> (correctors for specified corrections)
Transaction: Required (@Transactional annotation at repository level)
Error Handling: Returns empty list if no correctors found for corrections
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Bulk Corrector Management
```yaml
Usage: correctorRepository.saveAll(correctorList)
Purpose: "Create correctors in bulk for correction workflow setup and corrector assignment"

Business Logic Derivation:
  1. Used in CorrectionService for bulk corrector creation during correction workflow setup and corrector assignment operations
  2. Provides efficient bulk corrector persistence for correction workflows enabling comprehensive corrector creation and workflow functionality
  3. Critical for correction setup operations requiring bulk corrector creation for workflow management and corrector control
  4. Used in correction setup workflows for bulk corrector creation and workflow setup operations
  5. Enables correction setup with efficient bulk operations for comprehensive workflow processing and corrector control

Transaction: Required
Error Handling: DataIntegrityViolationException for bulk constraint violations, corrector integrity issues
```

#### Pattern: save() for Individual Corrector Action Management
```yaml
Usage: correctorRepository.save(corrector)
Purpose: "Update individual corrector action status for correction workflow tracking and action management"

Business Logic Derivation:
  1. Used in CorrectionService for individual corrector action updates during correction workflow management and action tracking operations
  2. Provides individual corrector persistence for correction workflows enabling comprehensive corrector management and tracking functionality
  3. Critical for correction management operations requiring individual corrector updates for corrector tracking and management control
  4. Used in correction management workflows for individual corrector updates and action tracking operations
  5. Enables correction management with individual corrector persistence for comprehensive correction processing and tracking control

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations, corrector integrity issues
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Correction Workflow Management
```yaml
Usage: Complete correction workflow for correction management and corrector tracking functionality
Purpose: "Manage correction workflows for comprehensive correction functionality and corrector processing"

Business Logic Derivation:
  1. Correction workflow management provides correction functionality through corrector creation, tracking, and management operations
  2. Corrector lifecycle includes corrector creation, action tracking, and workflow management for correction control
  3. Corrector management operations require correction processing for corrector lifecycle and workflow control
  4. Correction operations enable comprehensive workflow functionality with corrector capabilities and management
  5. Corrector lifecycle management supports correction requirements and functionality for correction workflow processing

Common Usage Examples:
  - correctorRepository.findByCorrectionId() across multiple services for correction workflow management
  - correctorRepository.saveAll() for bulk corrector creation during workflow setup
  - correctorRepository.save() for corrector action status updates and tracking
  - correctorRepository.findAllByCorrectionIdIn() for bulk correction workflow operations
  - Comprehensive correction workflow management with corrector tracking and workflow functionality

Transaction: Required for corrector persistence operations
Error Handling: Correction workflow error handling and corrector validation verification
```

### Pattern: Corrector Assignment and Action Tracking
```yaml
Usage: Corrector assignment and action tracking for correction workflow management and tracking functionality
Purpose: "Track corrector assignments for comprehensive correction management and workflow functionality"

Business Logic Derivation:
  1. Corrector assignment tracking operations require comprehensive corrector access for workflow-level correction management and corrector functionality
  2. Corrector tracking supports correction requirements and functionality for correction processing workflows
  3. Workflow-level corrector operations ensure proper correction functionality through corrector management and workflow control
  4. Correction workflows coordinate corrector tracking with correction processing for comprehensive correction operations
  5. Corrector tracking supports correction requirements and functionality for comprehensive correction corrector management

Common Usage Examples:
  - correctorRepository.findByCorrectionId() for correction corrector retrieval and tracking
  - Corrector action status management with comprehensive corrector reporting and correction functionality
  - Correction corrector analysis with corrector tracking and correction functionality

Transaction: Required for corrector management operations
Error Handling: Corrector tracking error handling and assignment validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByCorrectionId, findAllByCorrectionIdIn, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById

Repository-Level Transaction: @Transactional(rollbackFor = Exception.class)
Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (corrections_id, users_id, user_groups_id)
    * Foreign key violations (invalid corrections_id, users_id, user_groups_id references)
    * Corrector integrity constraint violations
  - EntityNotFoundException: Corrector not found by ID or criteria
  - OptimisticLockException: Concurrent corrector modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or corrector context
  - ConstraintViolationException: Corrector constraint violations

Validation Rules:
  - correction: Required, must reference existing correction for corrector context
  - user: Required, must reference existing user for corrector assignment
  - userGroup: Required, must reference existing user group for group corrector assignment
  - actionPerformed: Boolean flag indicating corrector action completion, defaults to false

Business Constraints:
  - Correction reference must be valid for corrector integrity and workflow functionality
  - User and user group references must be valid for corrector integrity and assignment functionality
  - Correctors must support correction workflow requirements and functionality
  - Corrector lifecycle management must maintain referential integrity and workflow functionality consistency
  - Corrector management must ensure proper workflow control and functionality
  - Corrector associations must support correction requirements and functionality for correction processing
  - Corrector operations must maintain transaction consistency and constraint integrity for workflow management
  - Action tracking must maintain corrector functionality and consistency
  - Workflow management must maintain corrector integrity and workflow requirements
  - Either user or userGroup must be specified for valid corrector assignment
```

## Corrector Management Considerations

### Correction Workflow Integration
```yaml
Workflow Management: Correctors enable correction functionality through corrector assignment and action tracking functionality
Corrector Assignment: Corrector assignment management enables workflow functionality with comprehensive corrector capabilities
Corrector Lifecycle: Corrector lifecycle includes creation, action tracking, and management operations for workflow functionality
Workflow Management: Comprehensive workflow management for corrector functionality and correction requirements during workflow workflows
Action Control: Corrector action control for workflow functionality and lifecycle management in correction processing
```

### User and Group Assignment Integration
```yaml
User Assignment: Individual user assignment for corrector functionality and user-specific correction management
Group Assignment: User group assignment for group-based correction and comprehensive group corrector functionality
Assignment Management: Corrector assignment management with user and group coordination for comprehensive correction assignment
User Integration: User assignment integration with correction and corrector functionality for assignment workflows
Group Integration: User group assignment integration with correction and corrector functionality for comprehensive workflow management
```

### Action and Status Integration
```yaml
Action Management: Corrector action management for workflow tracking and action management functionality
Status Tracking: Corrector status tracking with completion status and comprehensive action functionality
Action Control: Corrector action control for workflow completion and action management functionality
Action Completion: Corrector action completion tracking with status management and comprehensive completion functionality
Workflow Status: Correction workflow status management with corrector tracking and completion functionality for corrector workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Corrector repository without JPA/Hibernate dependencies, focusing on correction workflow management and corrector tracking patterns.
