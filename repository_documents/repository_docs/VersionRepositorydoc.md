# IVersionRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Version
- **Primary Purpose**: Manages version control entities for checklist versioning with hierarchical version management, deprecation tracking, and user permission validation
- **Key Relationships**: Version control entity linking checklist entities with version hierarchy (ancestor, parent, self) and comprehensive version lifecycle management
- **Performance Characteristics**: Moderate query volume with version hierarchy operations, version validation, and version lifecycle management
- **Business Context**: Core version control component that manages checklist versioning, revision tracking, deprecation workflows, and user permission validation for version control operations

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| ancestor | ancestor | Long | false | null |
| parent | parent | Long | true | null |
| self | self | Long | true | null |
| versioned_at | versionedAt | Long | true | null |
| deprecated_at | deprecatedAt | Long | true | null |
| version | version | Integer | true | null |
| type | type | Type.EntityType | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

None - This entity uses foreign key references without JPA relationship mappings for version hierarchy management.

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Version entity)`
- `deleteById(Long id)`
- `delete(Version entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<Version> spec)`
- `findAll(Specification<Version> spec, Pageable pageable)`
- `findAll(Specification<Version> spec, Sort sort)`
- `findOne(Specification<Version> spec)`
- `count(Specification<Version> spec)`

### Custom Query Methods (6 methods - ALL methods documented)

- `findAllByAncestorOrderByVersionDesc(Long ancestor)`
- `findRecentVersionByAncestor(Long ancestor)`
- `deprecateVersion(Long deprecatedAt, Long parent)`
- `findPrototypeChecklistIdsByAncestor(Long ancestor)`
- `wasUserRestrictedFromRecallingOrRevisingChecklist(Long userId, Long checklistId)`
- `findVersionBySelf(Long selfId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Version> findById(Long id)
List<Version> findAll()
Version save(Version entity)
void deleteById(Long id)
void delete(Version entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<Version> findAll(Specification<Version> spec)
Page<Version> findAll(Specification<Version> spec, Pageable pageable)
List<Version> findAll(Specification<Version> spec, Sort sort)
Optional<Version> findOne(Specification<Version> spec)
long count(Specification<Version> spec)
```

### Custom Query Methods

#### Method: findAllByAncestorOrderByVersionDesc(Long ancestor)
```yaml
Signature: List<Version> findAllByAncestorOrderByVersionDesc(Long ancestor)
Purpose: "Get all versions in a version hierarchy ordered by version number descending for version history display"

Business Logic Derivation:
  1. Used in ChecklistService and VersionService for version history retrieval and version hierarchy display
  2. Provides complete version history for a checklist ancestor in reverse chronological order
  3. Critical for version management operations requiring complete version history and version comparison
  4. Used in checklist version listing workflows for version history display and version selection
  5. Enables version history management with proper chronological ordering for version control operations

SQL Query: |
  SELECT v.* FROM versions v
  WHERE v.ancestor = ?
  ORDER BY v.version DESC

Parameters:
  - ancestor: Long (Ancestor identifier to get version history for)

Returns: List<Version> (all versions in hierarchy ordered by version number descending)
Transaction: Not Required
Error Handling: Returns empty list if no versions found for ancestor
```

#### Method: findRecentVersionByAncestor(Long ancestor)
```yaml
Signature: Integer findRecentVersionByAncestor(Long ancestor)
Purpose: "Get the most recent version number for version increment operations and version validation"

Business Logic Derivation:
  1. Used in ChecklistService and VersionService for version number calculation and version increment operations
  2. Provides latest version number for calculating next version number in version creation workflows
  3. Critical for version numbering operations requiring sequential version number generation
  4. Used in checklist versioning workflows for proper version number assignment and validation
  5. Enables version numbering consistency with sequential version number management for version control

SQL Query: |
  SELECT MAX(v.version) FROM versions v
  WHERE v.ancestor = ?

Parameters:
  - ancestor: Long (Ancestor identifier to get latest version number for)

Returns: Integer (most recent version number in the hierarchy)
Transaction: Not Required
Error Handling: Returns null if no versions found for ancestor
```

#### Method: deprecateVersion(Long deprecatedAt, Long parent)
```yaml
Signature: void deprecateVersion(Long deprecatedAt, Long parent)
Purpose: "Deprecate a version by setting deprecation timestamp for version lifecycle management"

Business Logic Derivation:
  1. Used in ChecklistService and VersionService for version deprecation operations and version lifecycle management
  2. Marks a version as deprecated with timestamp for version lifecycle tracking and deprecation workflows
  3. Critical for version lifecycle management requiring proper deprecation tracking and version status management
  4. Used in checklist publishing workflows when new versions supersede previous versions
  5. Enables version deprecation management with timestamp tracking for version lifecycle and audit operations

SQL Query: |
  UPDATE versions 
  SET deprecated_at = ? 
  WHERE parent = ?

Parameters:
  - deprecatedAt: Long (Deprecation timestamp for version lifecycle tracking)
  - parent: Long (Parent version identifier to deprecate)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findPrototypeChecklistIdsByAncestor(Long ancestor)
```yaml
Signature: List<Long> findPrototypeChecklistIdsByAncestor(Long ancestor)
Purpose: "Get prototype checklist IDs for version validation and prototype management operations"

Business Logic Derivation:
  1. Used in VersionService for prototype validation and prototype checklist management during version operations
  2. Identifies existing prototype checklists in version hierarchy for prototype validation and management
  3. Critical for version validation operations ensuring only one prototype exists per ancestor hierarchy
  4. Used in checklist revision workflows for prototype validation and version control business rules
  5. Enables prototype management with version hierarchy validation for proper version control workflow enforcement

SQL Query: |
  SELECT v.self FROM versions v
  INNER JOIN checklists c ON v.self = c.id
  WHERE v.ancestor = ? 
    AND c.state = 'BEING_BUILT'

Parameters:
  - ancestor: Long (Ancestor identifier to find prototype checklists for)

Returns: List<Long> (checklist IDs that are prototypes in the version hierarchy)
Transaction: Not Required
Error Handling: Returns empty list if no prototype checklists found for ancestor
```

#### Method: wasUserRestrictedFromRecallingOrRevisingChecklist(Long userId, Long checklistId)
```yaml
Signature: boolean wasUserRestrictedFromRecallingOrRevisingChecklist(Long userId, Long checklistId)
Purpose: "Check if user was approver/reviewer to restrict recall/revision operations for version control permissions"

Business Logic Derivation:
  1. Used in ChecklistService and ChecklistRevisionService for user permission validation during recall and revision operations
  2. Validates user permissions based on previous approval/review roles to prevent conflicts of interest
  3. Critical for version control security ensuring approvers/reviewers cannot recall or revise checklists they approved
  4. Used in checklist recall and revision workflows for permission validation and access control
  5. Enables version control security with role-based permission validation for proper governance and compliance

SQL Query: |
  SELECT COUNT(*) > 0 FROM (
    SELECT 1 FROM parameter_value_approvals pva
    INNER JOIN parameter_values pv ON pva.parameter_values_id = pv.id
    INNER JOIN jobs j ON pv.jobs_id = j.id
    WHERE j.checklists_id = ? AND pva.created_by = ?
    UNION
    SELECT 1 FROM reviewers r
    INNER JOIN corrections c ON r.corrections_id = c.id
    INNER JOIN parameter_values pv ON c.parameter_values_id = pv.id
    INNER JOIN jobs j ON pv.jobs_id = j.id
    WHERE j.checklists_id = ? AND r.users_id = ?
  ) AS restricted_users

Parameters:
  - userId: Long (User identifier to check restrictions for)
  - checklistId: Long (Checklist identifier to check restrictions against)

Returns: boolean (true if user was approver/reviewer and is restricted)
Transaction: Not Required
Error Handling: Returns false if user has no restrictions
```

#### Method: findVersionBySelf(Long selfId)
```yaml
Signature: Version findVersionBySelf(Long selfId)
Purpose: "Find version by self identifier for version context operations and version information retrieval"

Business Logic Derivation:
  1. Used in ChecklistService for version context retrieval and version information access during checklist operations
  2. Provides version entity based on self identifier for version-specific operations and version context
  3. Critical for version context operations requiring complete version information and version hierarchy access
  4. Used in checklist versioning workflows for version context validation and version information retrieval
  5. Enables version context management with self-identifier lookup for version-specific business logic operations

SQL Query: |
  SELECT v.* FROM versions v
  WHERE v.self = ?

Parameters:
  - selfId: Long (Self identifier to find version for)

Returns: Version (version entity with self identifier)
Transaction: Not Required
Error Handling: Returns null if no version found with self identifier
```

### Key Repository Usage Patterns

#### Pattern: save() for Version Lifecycle Management
```yaml
Usage: versionRepository.save(version)
Purpose: "Create new versions, update version information, and manage version lifecycle"

Business Logic Derivation:
  1. Used extensively for version creation with proper hierarchy and version number assignment
  2. Handles version information updates including versioning timestamps and version metadata
  3. Updates version lifecycle information for version management and tracking
  4. Critical for version lifecycle management and version hierarchy maintenance
  5. Supports version operations with hierarchy management and version control workflows

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: Version Hierarchy Management
```yaml
Usage: Multiple methods for hierarchy operations
Purpose: "Manage version hierarchy with ancestor, parent, self relationships for comprehensive version control"

Business Logic Derivation:
  1. Version hierarchy enables complete version lineage tracking and version relationship management
  2. Ancestor provides root version identifier for version family grouping and version history
  3. Parent provides immediate parent version for version inheritance and version progression
  4. Self provides current version identifier for version-specific operations and version context
  5. Enables comprehensive version control with hierarchical version management and version relationship tracking

Transaction: Varies by operation
Error Handling: Comprehensive hierarchy validation and constraint enforcement
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllByAncestorOrderByVersionDesc, findRecentVersionByAncestor
  - findPrototypeChecklistIdsByAncestor, wasUserRestrictedFromRecallingOrRevisingChecklist
  - findVersionBySelf, findAll(Specification), existsById, count
  - findOne(Specification), count(Specification)

Transactional Methods:
  - save, delete, deleteById, deprecateVersion

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid ancestor, parent, self references)
    * NOT NULL constraint violations (ancestor, type)
    * Invalid enum values for type field
    * Version hierarchy consistency violations
  - EntityNotFoundException: Version not found by ID or criteria
  - OptimisticLockException: Concurrent version modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - ResourceNotFoundException: Version not found during operations

Validation Rules:
  - ancestor: Required, must reference root version identifier for version family
  - parent: Optional, must reference valid parent version for version hierarchy
  - self: Optional, must reference current entity identifier for version context
  - versionedAt: Optional, must be valid timestamp for version lifecycle tracking
  - deprecatedAt: Optional, must be valid timestamp for deprecation tracking
  - version: Optional, must be positive integer for version numbering
  - type: Required, must be valid EntityType enum value

Business Constraints:
  - Version hierarchy must maintain consistency with ancestor, parent, self relationships
  - Version numbers must be sequential within ancestor hierarchy for proper versioning
  - Only one prototype checklist can exist per ancestor hierarchy at any time
  - Version deprecation must follow proper lifecycle progression and workflow rules
  - User permission restrictions must be enforced for recall and revision operations
  - Version lifecycle timestamps must be consistent and properly ordered
  - Version hierarchy operations must maintain referential integrity and version consistency
  - Deprecated versions cannot be modified or used for new version creation
  - Version control permissions must prevent conflicts of interest in approval workflows
  - Version numbering must be unique within ancestor hierarchy for version identification
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Version repository without JPA/Hibernate dependencies.
