# ICorrectionMediaMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: CorrectionMediaMapping
- **Primary Purpose**: Manages correction media mapping entities for correction-media associations with old/new media tracking, archived status management, and parameter value context
- **Key Relationships**: Mapping entity linking Correction, Media, and ParameterValue with many-to-one relationships for comprehensive correction media management and lifecycle control
- **Performance Characteristics**: High query volume with correction-based media retrieval, bulk media operations, and media status management
- **Business Context**: Correction media management component that provides correction-scoped media attachments, old/new media differentiation, archived media tracking, and parameter value context for correction workflow and media lifecycle management

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| corrections_id | correction.id / correctionId | Long | false | null |
| medias_id | media.id | Long | false | null |
| parameter_values_id | parameterValue.id | Long | false | null |
| is_old_media | isOldMedia | boolean | false | false |
| archived | archived | boolean | false | false |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | correction | Correction | LAZY | Parent correction, not null, cascade = ALL, immutable |
| @ManyToOne | media | Media | LAZY | Associated media, not null |
| @ManyToOne | parameterValue | ParameterValue | LAZY | Parameter value context, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(CorrectionMediaMapping entity)`
- `saveAll(Iterable<CorrectionMediaMapping> entities)`
- `deleteById(Long id)`
- `delete(CorrectionMediaMapping entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (8 methods - ALL methods documented)

- `findByCorrectionIdAndIsOldMedia(Long correctionId, boolean isOldMedia)`
- `findByCorrectionIdAndIsOldMediaAndArchived(Long correctionId, boolean isOldMedia, boolean isArchived)`
- `findAllByCorrectionIdInAndIsOldMedia(Set<Long> correctionIds, boolean isOldMedia)`
- `findAllByCorrectionIdInAndArchived(Set<Long> correctionIds, boolean isArchived)`
- `findAllByCorrectionIdInAndIsOldMediaAndArchived(Set<Long> correctionIds, boolean isOldMedia, boolean isArchived)`
- `updateArchiveStatusByMediaIdAndCorrectionId(Long correctionId, Long mediaId, boolean isArchived)`
- `findByCorrectionIdAndArchived(Long correctionId, boolean isArchived)`
- `findAllByCorrectionId(Long id)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<CorrectionMediaMapping> findById(Long id)
List<CorrectionMediaMapping> findAll()
CorrectionMediaMapping save(CorrectionMediaMapping entity)
List<CorrectionMediaMapping> saveAll(Iterable<CorrectionMediaMapping> entities)
void deleteById(Long id)
void delete(CorrectionMediaMapping entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: findByCorrectionIdAndIsOldMedia(Long correctionId, boolean isOldMedia)
```yaml
Signature: List<CorrectionMediaMapping> findByCorrectionIdAndIsOldMedia(Long correctionId, boolean isOldMedia)
Purpose: "Find correction media mappings by correction and old media status for media differentiation and management"

Business Logic Derivation:
  1. Used extensively in CorrectionService, JobService, and IParameterMapper for retrieving old/new media associated with corrections
  2. Provides media differentiation for corrections enabling proper media lifecycle management and correction workflow
  3. Critical for correction operations requiring old/new media distinction for parameter correction and media management
  4. Used in correction display workflows for showing appropriate media based on correction state and media lifecycle
  5. Enables correction media management with old/new media differentiation for comprehensive correction processing

SQL Query: |
  SELECT cmm.* FROM corrections_media_mapping cmm
  WHERE cmm.corrections_id = ? AND cmm.is_old_media = ?

Parameters:
  - correctionId: Long (Correction identifier to get media mappings for)
  - isOldMedia: boolean (Old media flag for media type differentiation)

Returns: List<CorrectionMediaMapping> (media mappings for correction with specified old media status)
Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty list if no media mappings found for correction and media type
```

#### Method: findByCorrectionIdAndIsOldMediaAndArchived(Long correctionId, boolean isOldMedia, boolean isArchived)
```yaml
Signature: List<CorrectionMediaMapping> findByCorrectionIdAndIsOldMediaAndArchived(Long correctionId, boolean isOldMedia, boolean isArchived)
Purpose: "Find correction media mappings with correction, old media status, and archived status for precise media filtering"

Business Logic Derivation:
  1. Used in IParameterMapper, CorrectionService, and JobService for precise media filtering with old/new and archived status
  2. Provides comprehensive media filtering for corrections enabling precise media lifecycle management and display control
  3. Critical for parameter operations requiring specific media states for signature parameters and correction workflow
  4. Used in correction processing workflows for accessing media with precise status combinations for parameter handling
  5. Enables precise correction media management with comprehensive status filtering for parameter correction operations

SQL Query: |
  SELECT cmm.* FROM corrections_media_mapping cmm
  WHERE cmm.corrections_id = ? AND cmm.is_old_media = ? AND cmm.archived = ?

Parameters:
  - correctionId: Long (Correction identifier to get media mappings for)
  - isOldMedia: boolean (Old media flag for media type differentiation)
  - isArchived: boolean (Archived status for media lifecycle filtering)

Returns: List<CorrectionMediaMapping> (media mappings matching all specified criteria)
Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty list if no media mappings match all criteria
```

#### Method: findAllByCorrectionIdInAndIsOldMedia(Set<Long> correctionIds, boolean isOldMedia)
```yaml
Signature: List<CorrectionMediaMapping> findAllByCorrectionIdInAndIsOldMedia(Set<Long> correctionIds, boolean isOldMedia)
Purpose: "Find media mappings for multiple corrections with old media status for bulk correction media operations"

Business Logic Derivation:
  1. Used in CorrectionService for bulk correction media retrieval during correction listing and bulk operations
  2. Provides efficient bulk media access for multiple corrections enabling bulk correction processing and media management
  3. Critical for bulk correction operations requiring media information for multiple corrections with media type filtering
  4. Used in correction listing workflows for retrieving media across multiple corrections with old/new media differentiation
  5. Enables bulk correction media operations with efficient multi-correction media retrieval and media type filtering

SQL Query: |
  SELECT cmm.* FROM corrections_media_mapping cmm
  WHERE cmm.corrections_id IN (?, ?, ?, ...) AND cmm.is_old_media = ?

Parameters:
  - correctionIds: Set<Long> (Set of correction identifiers for bulk media retrieval)
  - isOldMedia: boolean (Old media flag for media type differentiation)

Returns: List<CorrectionMediaMapping> (media mappings for all corrections with specified old media status)
Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty list if no media mappings found for corrections and media type
```

#### Method: findAllByCorrectionIdInAndArchived(Set<Long> correctionIds, boolean isArchived)
```yaml
Signature: List<CorrectionMediaMapping> findAllByCorrectionIdInAndArchived(Set<Long> correctionIds, boolean isArchived)
Purpose: "Find media mappings for multiple corrections with archived status for bulk archived media operations"

Business Logic Derivation:
  1. Used for bulk correction media retrieval with archived status filtering for correction processing and media lifecycle management
  2. Provides efficient bulk media access for multiple corrections with archived status filtering for media management
  3. Critical for bulk correction operations requiring archived media information for correction workflow and media lifecycle
  4. Used in correction processing workflows for retrieving archived media across multiple corrections for media management
  5. Enables bulk correction media operations with archived status filtering for comprehensive correction media management

SQL Query: |
  SELECT cmm.* FROM corrections_media_mapping cmm
  WHERE cmm.corrections_id IN (?, ?, ?, ...) AND cmm.archived = ?

Parameters:
  - correctionIds: Set<Long> (Set of correction identifiers for bulk media retrieval)
  - isArchived: boolean (Archived status for media lifecycle filtering)

Returns: List<CorrectionMediaMapping> (media mappings for all corrections with specified archived status)
Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty list if no media mappings found for corrections and archived status
```

#### Method: findAllByCorrectionIdInAndIsOldMediaAndArchived(Set<Long> correctionIds, boolean isOldMedia, boolean isArchived)
```yaml
Signature: List<CorrectionMediaMapping> findAllByCorrectionIdInAndIsOldMediaAndArchived(Set<Long> correctionIds, boolean isOldMedia, boolean isArchived)
Purpose: "Find media mappings for multiple corrections with comprehensive status filtering for precise bulk operations"

Business Logic Derivation:
  1. Used in CorrectionService for precise bulk correction media retrieval with comprehensive status filtering
  2. Provides efficient bulk media access with comprehensive filtering for precise correction processing and media management
  3. Critical for bulk correction operations requiring precise media filtering for correction workflow and media lifecycle
  4. Used in correction processing workflows for retrieving specific media types across multiple corrections with precise filtering
  5. Enables precise bulk correction media operations with comprehensive status filtering for advanced correction management

SQL Query: |
  SELECT cmm.* FROM corrections_media_mapping cmm
  WHERE cmm.corrections_id IN (?, ?, ?, ...) AND cmm.is_old_media = ? AND cmm.archived = ?

Parameters:
  - correctionIds: Set<Long> (Set of correction identifiers for bulk media retrieval)
  - isOldMedia: boolean (Old media flag for media type differentiation)
  - isArchived: boolean (Archived status for media lifecycle filtering)

Returns: List<CorrectionMediaMapping> (media mappings for all corrections matching all criteria)
Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty list if no media mappings match all criteria for any corrections
```

#### Method: updateArchiveStatusByMediaIdAndCorrectionId(Long correctionId, Long mediaId, boolean isArchived)
```yaml
Signature: void updateArchiveStatusByMediaIdAndCorrectionId(Long correctionId, Long mediaId, boolean isArchived)
Purpose: "Update archived status for specific media in correction for media lifecycle management"

Business Logic Derivation:
  1. Used in CorrectionService for updating archived status of specific media within corrections during media lifecycle operations
  2. Provides targeted media status updates for correction media management and media archival workflows
  3. Critical for media lifecycle operations requiring specific media archival within correction context
  4. Used in correction media management workflows for archiving specific media while maintaining correction association
  5. Enables targeted media archival operations with correction-scoped media status updates for media lifecycle control

SQL Query: |
  UPDATE corrections_media_mapping 
  SET archived = ? 
  WHERE corrections_id = ? AND medias_id = ?

Parameters:
  - correctionId: Long (Correction identifier for correction-scoped media update)
  - mediaId: Long (Media identifier for specific media targeting)
  - isArchived: boolean (New archived status for media lifecycle management)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found
```

#### Method: findByCorrectionIdAndArchived(Long correctionId, boolean isArchived)
```yaml
Signature: List<CorrectionMediaMapping> findByCorrectionIdAndArchived(Long correctionId, boolean isArchived)
Purpose: "Find correction media mappings by correction and archived status for media lifecycle management"

Business Logic Derivation:
  1. Used in IParameterMapper, CorrectionService, and JobService for retrieving media based on archived status within corrections
  2. Provides archived status filtering for correction media enabling media lifecycle management and display control
  3. Critical for correction operations requiring active/archived media distinction for parameter handling and correction workflow
  4. Used in correction display workflows for showing appropriate media based on archived status and correction state
  5. Enables correction media management with archived status filtering for comprehensive correction media processing

SQL Query: |
  SELECT cmm.* FROM corrections_media_mapping cmm
  WHERE cmm.corrections_id = ? AND cmm.archived = ?

Parameters:
  - correctionId: Long (Correction identifier to get media mappings for)
  - isArchived: boolean (Archived status for media lifecycle filtering)

Returns: List<CorrectionMediaMapping> (media mappings for correction with specified archived status)
Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty list if no media mappings found for correction and archived status
```

#### Method: findAllByCorrectionId(Long id)
```yaml
Signature: List<CorrectionMediaMapping> findAllByCorrectionId(Long id)
Purpose: "Find all media mappings for specific correction for complete correction media retrieval"

Business Logic Derivation:
  1. Used in JobAuditService for retrieving all media associated with corrections during audit operations and reporting
  2. Provides complete media listing for corrections enabling comprehensive correction media access and audit trail
  3. Critical for audit operations requiring complete media information for corrections for compliance and reporting
  4. Used in correction audit workflows for accessing all media associated with corrections for audit trail and documentation
  5. Enables complete correction media access for audit operations and comprehensive correction media management

SQL Query: |
  SELECT cmm.* FROM corrections_media_mapping cmm
  WHERE cmm.corrections_id = ?

Parameters:
  - id: Long (Correction identifier to get all media mappings for)

Returns: List<CorrectionMediaMapping> (all media mappings for the specified correction)
Transaction: Required (@Transactional annotation on repository)
Error Handling: Returns empty list if no media mappings found for correction
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Bulk Correction Media Operations
```yaml
Usage: correctionMediaMappingRepository.saveAll(mappings)
Purpose: "Bulk creation and updates of correction media mappings for efficient media management"

Business Logic Derivation:
  1. Used extensively in CorrectionService for bulk correction media mapping creation and updates during correction workflows
  2. Provides efficient bulk media mapping persistence for operations affecting multiple media simultaneously
  3. Critical for correction operations requiring bulk media association and mapping management
  4. Used in correction media management workflows for bulk mapping creation and media association operations
  5. Enables efficient bulk correction media operations with transaction consistency for comprehensive media management

Transaction: Required (@Transactional annotation on repository)
Error Handling: DataIntegrityViolationException for bulk constraint violations
```

#### Pattern: Old/New Media Differentiation for Correction Workflow
```yaml
Usage: Multiple methods for old/new media distinction in correction processing
Purpose: "Differentiate between old and new media in correction workflows for proper media lifecycle management"

Business Logic Derivation:
  1. Old/new media differentiation enables proper correction workflow with before/after media comparison and management
  2. Correction workflows require distinction between original media and corrected media for parameter correction processing
  3. Media lifecycle management depends on old/new media tracking for correction history and workflow control
  4. Parameter type-specific handling (especially signatures) requires precise old/new media differentiation for workflow integrity
  5. Correction display and processing workflows use old/new media distinction for proper user interface and workflow management

Transaction: Required (@Transactional annotation on repository)
Error Handling: Empty list handling for missing media mappings
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Parameter-Specific Correction Media Management
```yaml
Usage: Media mapping management for different parameter types with correction context
Purpose: "Manage correction media mappings for different parameter types with proper lifecycle and workflow control"

Business Logic Derivation:
  1. Different parameter types (SIGNATURE, MEDIA, FILE_UPLOAD) require different media management approaches in correction workflows
  2. Signature parameters have special handling with archived status consideration for correction media management
  3. Media and file upload parameters use old/new media differentiation for correction workflow and parameter processing
  4. Parameter value context enables proper media association and correction workflow management
  5. Media lifecycle management varies by parameter type requiring type-specific media mapping and processing approaches

Common Usage Examples:
  - findByCorrectionIdAndIsOldMediaAndArchived() for signature parameter media management
  - findByCorrectionIdAndArchived() for non-signature parameter media processing
  - findByCorrectionIdAndIsOldMedia() for old/new media differentiation in correction workflows
  - saveAll() for bulk media mapping creation during correction operations
  - updateArchiveStatusByMediaIdAndCorrectionId() for targeted media archival in corrections

Transaction: Required for all operations
Error Handling: Parameter type validation, media lifecycle verification
```

### Pattern: Bulk Correction Media Processing
```yaml
Usage: Bulk media operations for multiple corrections in listing and processing workflows
Purpose: "Efficiently process media for multiple corrections in bulk operations and correction listing"

Business Logic Derivation:
  1. Correction listing operations require bulk media retrieval for multiple corrections with efficient database access
  2. Bulk correction processing enables efficient media management across multiple corrections with comprehensive filtering
  3. Media mapping grouping by correction enables efficient correction display and processing workflows
  4. Bulk operations support correction management interfaces requiring media information for multiple corrections
  5. Efficient bulk retrieval reduces database overhead for correction listing and bulk processing operations

Common Usage Examples:
  - findAllByCorrectionIdInAndIsOldMedia() for bulk old media retrieval in correction listing
  - findAllByCorrectionIdInAndIsOldMediaAndArchived() for precise bulk media filtering
  - Correction media grouping for efficient correction display and processing workflows
  - Bulk media operations for correction management interfaces and reporting
  - Efficient multi-correction media retrieval for performance optimization

Transaction: Required for consistency
Error Handling: Bulk operation error handling and empty result management
```

### Pattern: Media Lifecycle Management in Corrections
```yaml
Usage: Media archival and lifecycle management within correction context
Purpose: "Manage media lifecycle within correction workflows with archival control and status management"

Business Logic Derivation:
  1. Media lifecycle management within corrections enables proper media archival and correction workflow control
  2. Archived status tracking enables media lifecycle management while maintaining correction association and context
  3. Media archival operations support correction workflow requirements and media lifecycle policies
  4. Correction-scoped media management enables targeted media operations within correction context
  5. Media status updates support correction processing workflows and media lifecycle requirements

Common Usage Examples:
  - updateArchiveStatusByMediaIdAndCorrectionId() for targeted media archival in corrections
  - findByCorrectionIdAndArchived() for archived media filtering in correction workflows
  - Media lifecycle management with correction context preservation and workflow integrity
  - Archived media handling in correction display and processing workflows
  - Media status management for correction workflow and lifecycle requirements

Transaction: Required for media lifecycle operations
Error Handling: Media lifecycle validation and correction context verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByCorrectionIdAndIsOldMedia, findByCorrectionIdAndIsOldMediaAndArchived
  - findAllByCorrectionIdInAndIsOldMedia, findAllByCorrectionIdInAndArchived
  - findAllByCorrectionIdInAndIsOldMediaAndArchived, findByCorrectionIdAndArchived
  - findAllByCorrectionId, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById, updateArchiveStatusByMediaIdAndCorrectionId

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback per repository annotation)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (corrections_id, medias_id, parameter_values_id)
    * Foreign key violations (invalid corrections_id, medias_id, parameter_values_id references)
    * Unique constraint violations on correction-media combinations
    * Cascade operation failures with correction relationship
  - EntityNotFoundException: Correction media mapping not found by ID or criteria
  - OptimisticLockException: Concurrent correction media mapping modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or bulk operations
  - ConstraintViolationException: Media mapping constraint violations

Validation Rules:
  - correction: Required, must reference existing correction, immutable after creation, cascade = ALL
  - correctionId: Derived from correction relationship for correction-scoped operations
  - media: Required, must reference existing media for media association
  - parameterValue: Required, must reference existing parameter value, immutable after creation
  - isOldMedia: Defaults to false, boolean for old/new media differentiation in correction workflows
  - archived: Defaults to false, boolean for media lifecycle management and archival control

Business Constraints:
  - Correction media mappings must maintain referential integrity with correction, media, and parameter value entities
  - Old/new media differentiation must be consistent with correction workflow requirements and parameter processing
  - Archived status must follow media lifecycle policies and correction workflow requirements
  - Media associations must be valid for correction context and parameter value requirements
  - Correction media mappings must support parameter type-specific handling and workflow requirements
  - Media lifecycle management must maintain correction association and workflow integrity
  - Bulk operations must maintain transaction consistency and constraint integrity
  - Parameter value context must be preserved for correction workflow and media association validity
  - Media mapping updates must maintain correction workflow integrity and media lifecycle consistency
  - Archival operations must consider correction workflow state and media lifecycle requirements
```

## Correction Media Mapping Considerations

### Media Lifecycle Integration
```yaml
Old/New Media: Boolean flag for correction workflow media differentiation and lifecycle tracking
Archived Status: Media archival control within correction context for lifecycle management
Parameter Context: Parameter value association for correction workflow and media validation
Correction Association: Correction relationship for media scoping and workflow integration
Media Management: Comprehensive media lifecycle management within correction workflows
```

### Workflow Integration
```yaml
Correction Workflow: Media mappings support correction workflow requirements and parameter processing
Parameter Types: Different parameter types require specific media handling and correction processing
Media Differentiation: Old/new media distinction for correction workflow and parameter correction
Status Management: Archived status management for media lifecycle and correction workflow control
Bulk Operations: Efficient bulk media operations for correction processing and management workflows
```

### Performance Optimization
```yaml
Bulk Retrieval: Efficient bulk media retrieval for multiple corrections and performance optimization
Index Strategy: Proper indexing on correction_id, is_old_media, and archived for query performance
Status Filtering: Efficient status-based filtering for media lifecycle and correction workflow operations
Parameter Association: Parameter value context for correction workflow and media validation
Transaction Management: Proper transaction handling for media lifecycle and correction workflow integrity
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the CorrectionMediaMapping repository without JPA/Hibernate dependencies, focusing on correction media management and workflow integration patterns.
