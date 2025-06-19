# ITempParameterMediaMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TempParameterValueMediaMapping
- **Primary Purpose**: Manages temporary parameter value media mapping entities for correction workflow media associations with archival management, bulk cleanup operations, and correction media lifecycle control
- **Key Relationships**: Mapping entity linking TempParameterValue and Media with many-to-one relationships using composite key for comprehensive correction media management and archival control
- **Performance Characteristics**: Low to moderate query volume with bulk deletion operations, media archival management, and correction workflow media processing
- **Business Context**: Correction media management component that provides temporary parameter value media associations, media archival control, bulk cleanup operations, and correction workflow media functionality for correction processing and media lifecycle management

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| temp_parameter_values_id | parameterValueMediaId.tempParameterValueId / tempParameterValue.id | Long | false | part of composite key |
| medias_id | parameterValueMediaId.mediaId / media.id | Long | false | part of composite key |
| archived | archived | boolean | false | false |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Composite Key Structure
- **TempParameterValueMediaCompositeKey**: Composite key containing tempParameterValueId and mediaId for unique temporary parameter value media associations

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | tempParameterValue | TempParameterValue | LAZY | Associated temp parameter value, not null, immutable |
| @ManyToOne | media | Media | LAZY | Associated media, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(TempParameterValueMediaCompositeKey id)`
- `findAll()`
- `save(TempParameterValueMediaMapping entity)`
- `deleteById(TempParameterValueMediaCompositeKey id)`
- `delete(TempParameterValueMediaMapping entity)`
- `existsById(TempParameterValueMediaCompositeKey id)`
- `count()`

### Custom Query Methods (2 methods - ALL methods documented)

- `deleteAllByTempParameterValueIdIn(List<Long> tempParameterValueIds)`
- `archiveMediaByTempParameterValueIdAndMediaIdIn(Long tempParameterValueId, Set<Long> archivedMediaIds)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods with composite key
Optional<TempParameterValueMediaMapping> findById(TempParameterValueMediaCompositeKey id)
List<TempParameterValueMediaMapping> findAll()
TempParameterValueMediaMapping save(TempParameterValueMediaMapping entity)
void deleteById(TempParameterValueMediaCompositeKey id)
void delete(TempParameterValueMediaMapping entity)
boolean existsById(TempParameterValueMediaCompositeKey id)
long count()
```

### Custom Query Methods

#### Method: deleteAllByTempParameterValueIdIn(List<Long> tempParameterValueIds)
```yaml
Signature: void deleteAllByTempParameterValueIdIn(List<Long> tempParameterValueIds)
Purpose: "Delete all temporary parameter value media mappings for multiple temp parameter values for bulk cleanup and correction lifecycle management"

Business Logic Derivation:
  1. Used in TaskExecutionService for bulk deletion of temp parameter value media mappings during correction cleanup and lifecycle operations
  2. Provides efficient bulk media mapping cleanup for correction workflows enabling comprehensive correction media lifecycle management
  3. Critical for correction lifecycle operations requiring bulk media mapping cleanup for correction processing and media management
  4. Used in correction cleanup workflows for removing media mappings associated with multiple temp parameter values for bulk cleanup operations
  5. Enables correction lifecycle management with bulk media mapping cleanup for comprehensive correction processing and media lifecycle control

SQL Query: |
  DELETE FROM temp_parameter_value_media_mapping 
  WHERE temp_parameter_values_id IN (?, ?, ?, ...)

Parameters:
  - tempParameterValueIds: List<Long> (List of temp parameter value identifiers for bulk media mapping deletion)

Returns: void
Transaction: Not explicitly required (but recommended for bulk operations)
Error Handling: No exception if no matching records found for temp parameter value identifiers
```

#### Method: archiveMediaByTempParameterValueIdAndMediaIdIn(Long tempParameterValueId, Set<Long> archivedMediaIds)
```yaml
Signature: void archiveMediaByTempParameterValueIdAndMediaIdIn(Long tempParameterValueId, Set<Long> archivedMediaIds)
Purpose: "Archive media mappings for temporary parameter value and specific media IDs for correction media archival management and lifecycle control"

Business Logic Derivation:
  1. Used in TaskExecutionService for archiving media mappings during correction processing and media lifecycle management operations
  2. Provides efficient media archival for correction workflows enabling correction media lifecycle management and archival control
  3. Critical for correction processing operations requiring media archival for correction media management and lifecycle control
  4. Used in correction processing workflows for archiving media mappings associated with temp parameter values for media lifecycle management
  5. Enables correction media management with efficient media archival for comprehensive correction processing and media lifecycle control

SQL Query: |
  UPDATE temp_parameter_value_media_mapping 
  SET archived = true, modified_at = EXTRACT(EPOCH FROM NOW()) * 1000
  WHERE temp_parameter_values_id = ? AND medias_id IN (?, ?, ?, ...)

Parameters:
  - tempParameterValueId: Long (Temp parameter value identifier for media archival context)
  - archivedMediaIds: Set<Long> (Set of media identifiers for archival operations)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found for temp parameter value and media combinations
```

### Key Repository Usage Patterns

#### Pattern: save() for Temporary Parameter Media Association Management
```yaml
Usage: tempParameterMediaMappingRepository.save(mapping)
Purpose: "Create temporary parameter value media associations for correction media management and media attachment lifecycle control"

Business Logic Derivation:
  1. Used for temp parameter media association creation with composite key management and correction media lifecycle control
  2. Provides temp parameter media mapping persistence with comprehensive association management and media lifecycle control
  3. Critical for correction media association lifecycle management and media attachment operations requiring mapping creation
  4. Used in correction media management workflows for association creation and media attachment operations during correction processing
  5. Enables correction media association management with comprehensive media lifecycle control and attachment management for correction workflows

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations, composite key conflicts
```

#### Pattern: Bulk Cleanup and Archival Operations
```yaml
Usage: Bulk cleanup and archival operations for correction workflow media management
Purpose: "Manage temporary parameter value media mappings in bulk for correction processing cleanup and media archival control"

Business Logic Derivation:
  1. Bulk cleanup operations enable efficient correction workflow processing with comprehensive media mapping cleanup and lifecycle management
  2. Media archival operations support correction workflows with media lifecycle management and archival control for correction processing
  3. Correction processing workflows depend on bulk operations for proper media management and lifecycle control during correction operations
  4. Media archival management requires bulk operations for comprehensive correction media functionality and archival control
  5. Correction media processing requires comprehensive bulk operations and archival management for media lifecycle functionality

Transaction: Required for archival operations, Recommended for bulk cleanup operations
Error Handling: Bulk operation error handling and media mapping validation
```

#### Pattern: Correction Workflow Media Lifecycle Management
```yaml
Usage: Correction workflow media lifecycle management for media attachment and archival functionality
Purpose: "Manage temporary parameter value media lifecycle for comprehensive correction processing and media attachment control"

Business Logic Derivation:
  1. Correction workflow media lifecycle enables proper media attachment functionality through temp parameter media associations and management
  2. Media lifecycle management supports correction workflow requirements and media attachment functionality for correction processing
  3. Correction media lifecycle workflows depend on media mapping management for proper media attachment validation and lifecycle control
  4. Media attachment management requires lifecycle operations for comprehensive correction media functionality and attachment control
  5. Correction processing requires comprehensive media lifecycle management and attachment functionality for media management operations

Transaction: Required for lifecycle operations and media management
Error Handling: Media lifecycle error handling and correction media validation verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Correction Processing Media Management and Archival Control
```yaml
Usage: Complete temporary parameter media mapping lifecycle for correction workflows and media archival management
Purpose: "Manage temporary parameter media mappings for comprehensive correction processing and media archival functionality"

Business Logic Derivation:
  1. Temporary parameter media mappings provide correction workflow processing through media association storage and archival management
  2. Correction media processing lifecycle includes creation, association management, archival operations, and cleanup workflows
  3. Media archival operations require temp parameter media mapping management for correction processing and media lifecycle control
  4. Correction workflow operations enable comprehensive media attachment functionality with correction processing capabilities and archival control
  5. Media mapping lifecycle management supports correction requirements and media attachment functionality for correction processing and archival management

Common Usage Examples:
  - tempParameterMediaMappingRepository.archiveMediaByTempParameterValueIdAndMediaIdIn() in TaskExecutionService for media archival during correction processing
  - tempParameterMediaMappingRepository.deleteAllByTempParameterValueIdIn() for bulk cleanup during correction lifecycle management (commented out but available)
  - Correction media archival workflows for media lifecycle management and correction processing control
  - Media mapping lifecycle management for correction functionality and media attachment control with archival operations
  - Comprehensive correction processing with media lifecycle control and archival management for correction workflow functionality

Transaction: Required for lifecycle operations and media archival management
Error Handling: Correction processing error handling and media archival validation verification
```

### Pattern: Bulk Media Operations and Cleanup Management
```yaml
Usage: Bulk temporary parameter media mapping operations for correction cleanup and lifecycle management
Purpose: "Process temporary parameter media mappings in bulk for comprehensive correction cleanup and media lifecycle management"

Business Logic Derivation:
  1. Bulk media operations enable efficient correction processing with comprehensive temp parameter media mapping cleanup and lifecycle management
  2. Correction cleanup workflows require bulk media mapping operations for complete correction processing and media lifecycle control
  3. Media mapping cleanup operations support correction requirements and bulk media management functionality for correction workflows
  4. Bulk cleanup operations enable efficient media processing for correction lifecycle and bulk management workflows with archival control
  5. Media cleanup processing supports correction functionality and comprehensive bulk media management operations with lifecycle control

Common Usage Examples:
  - Bulk temp parameter media mapping deletion for correction cleanup and media lifecycle management operations
  - Correction media cleanup workflows with bulk media mapping operations and comprehensive lifecycle management
  - Media mapping bulk operations for correction processing and comprehensive media lifecycle control with cleanup functionality
  - Bulk media operations for comprehensive correction processing and media lifecycle management requirements
  - Comprehensive correction cleanup with media mapping processing and bulk media management operations for lifecycle control

Transaction: Recommended for bulk cleanup operations and media lifecycle management
Error Handling: Bulk operation error handling and media cleanup validation verification
```

### Pattern: Media Attachment and Association Management for Correction Workflows
```yaml
Usage: Temporary parameter media mapping association management for correction media attachment and lifecycle functionality
Purpose: "Manage temporary parameter media associations for comprehensive correction media attachment functionality and lifecycle control"

Business Logic Derivation:
  1. Media association management enables correction workflow processing with temp parameter media attachment functionality and association control
  2. Correction media attachment operations involve media mapping management for correction workflows and media association lifecycle control
  3. Temp parameter media mapping operations ensure proper correction media functionality through media association management and lifecycle control
  4. Media attachment workflows coordinate media mapping management with correction processing for comprehensive media operations and association control
  5. Correction media management supports media attachment requirements and correction processing functionality for comprehensive media operations

Common Usage Examples:
  - Temp parameter media mapping creation for correction media attachment functionality and media association management
  - Media attachment lifecycle management with temp parameter media mapping control and association management for correction workflows
  - Correction media association operations for media attachment functionality and comprehensive media lifecycle management
  - Media association management for correction functionality and media attachment requirements with lifecycle control
  - Comprehensive correction media management with attachment functionality and media association control for correction processing

Transaction: Required for media attachment lifecycle operations and association management
Error Handling: Media attachment validation and correction media association verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, existsById, count

Transactional Methods:
  - save, delete, deleteById, archiveMediaByTempParameterValueIdAndMediaIdIn
  - deleteAllByTempParameterValueIdIn (recommended for consistency)

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Composite key constraint violations (duplicate temp parameter value-media combinations)
    * NOT NULL constraint violations (temp_parameter_values_id, medias_id)
    * Foreign key violations (invalid temp_parameter_values_id, medias_id references)
    * Unique constraint violations on composite key
  - EntityNotFoundException: Temp parameter media mapping not found by composite key or criteria
  - OptimisticLockException: Concurrent temp parameter media mapping modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or composite key operations
  - ConstraintViolationException: Temp parameter media mapping constraint violations

Validation Rules:
  - parameterValueMediaId: Required, composite key containing tempParameterValueId and mediaId for unique associations
  - tempParameterValue: Required, must reference existing temp parameter value, immutable after creation
  - media: Required, must reference existing media, immutable after creation
  - archived: Defaults to false, boolean for media lifecycle management and archival control

Business Constraints:
  - Temp parameter media mappings must be unique for proper association management and correction media integrity
  - Temp parameter value and media references must be valid for association integrity and correction media functionality
  - Temp parameter media mappings must support correction media functionality requirements and media lifecycle management
  - Association lifecycle management must maintain referential integrity and correction media functionality consistency
  - Media archival management must ensure proper media lifecycle control and correction parameter media functionality
  - Temp parameter media associations must support correction requirements and media attachment functionality
  - Bulk operations must maintain transaction consistency and constraint integrity for media management and correction processing
  - Media mapping lifecycle management must maintain correction functionality and media association consistency
  - Association management must maintain temp parameter media integrity and media attachment requirements for correction workflows
  - Cleanup operations must ensure proper media lifecycle management and correction media attachment control
```

## Temporary Parameter Media Mapping Considerations

### Correction Media Integration
```yaml
Parameter Association: Temp parameter media mappings enable correction media functionality through temp parameter value media associations
Media Attachments: Media associations enable correction parameter functionality with comprehensive media attachment capabilities for correction workflows
Attachment Lifecycle: Media mapping lifecycle includes creation, association management, archival operations, and cleanup for correction processing
Media Management: Comprehensive media management for correction parameter functionality and media attachment requirements during correction workflows
Association Control: Temp parameter media association control for media attachment functionality and lifecycle management in correction processing
```

### Archival and Lifecycle Management
```yaml
Archival Status: Archived flag for media lifecycle management and correction parameter media archival control during correction processing
Media Lifecycle: Media mapping lifecycle includes creation, archival management, and lifecycle control operations for correction workflows
Lifecycle Control: Comprehensive media lifecycle control for correction parameter media functionality and archival management
Archival Operations: Media archival operations for correction parameter media lifecycle and archival control functionality during correction processing
Status Management: Archival status management for media lifecycle and correction parameter media functionality in correction workflows
```

### Bulk Operations and Processing
```yaml
Bulk Cleanup: Efficient bulk media mapping cleanup for correction lifecycle and bulk processing operations during correction workflows
Cleanup Operations: Bulk cleanup operations for comprehensive media processing and management during correction lifecycle operations
Bulk Management: Comprehensive bulk media management for correction processing and bulk media operations with lifecycle control
Processing Integration: Bulk processing integration for comprehensive media management and operations during correction workflow processing
Cleanup Integration: Media cleanup integration for correction functionality and media lifecycle management during correction processing
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TempParameterMediaMapping repository without JPA/Hibernate dependencies, focusing on correction workflow media management and temporary parameter media association patterns.
