# IMediaRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Media
- **Primary Purpose**: Manages media/file entities with file metadata, archival functionality, and media update operations
- **Key Relationships**: Central media entity linking to Organisation with various media-based operations throughout the system
- **Performance Characteristics**: Moderate query volume with media retrieval operations, bulk media loading, and media update operations
- **Business Context**: Core media management component that handles file uploads, media metadata management, and media lifecycle operations

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | true | null |
| description | description | String | true | null |
| original_filename | originalFilename | String | false | null |
| filename | filename | String | false | null |
| type | type | String | false | null |
| relative_path | relativePath | String | false | null |
| archived | archived | boolean | true | false |
| organisations_id | organisation.id | Long | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | organisation | Organisation | LAZY | Parent organisation, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Media entity)`
- `deleteById(Long id)`
- `delete(Media entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<Media> spec)`
- `findAll(Specification<Media> spec, Pageable pageable)`
- `findAll(Specification<Media> spec, Sort sort)`
- `findOne(Specification<Media> spec)`
- `count(Specification<Media> spec)`

### Custom Query Methods (2 methods - ALL methods documented)

- `findAll(Set<Long> mediaIds)`
- `updateByMediaId(Long mediaId, String name, String description)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Media> findById(Long id)
List<Media> findAll()
Media save(Media entity)
void deleteById(Long id)
void delete(Media entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<Media> findAll(Specification<Media> spec)
Page<Media> findAll(Specification<Media> spec, Pageable pageable)
List<Media> findAll(Specification<Media> spec, Sort sort)
Optional<Media> findOne(Specification<Media> spec)
long count(Specification<Media> spec)
```

### Custom Query Methods

#### Method: findAll(Set<Long> mediaIds)
```yaml
Signature: List<Media> findAll(Set<Long> mediaIds)
Purpose: "Find media entities by multiple IDs for bulk media operations and loading"

Business Logic Derivation:
  1. Used extensively throughout system for bulk media loading and operations
  2. Enables efficient bulk media retrieval for parameter executions, job annotations, and corrections
  3. Critical for bulk media operations and media relationship management
  4. Used in parameter execution workflows, job CWE details, correction workflows, and job annotations
  5. Supports efficient bulk media loading for various business operations involving file attachments

SQL Query: |
  SELECT m.* FROM medias m WHERE m.id IN (?)

Parameters:
  - mediaIds: Set<Long> (Media identifiers to retrieve)

Returns: List<Media> (media entities matching the provided IDs)
Transaction: Not Required
Error Handling: Returns empty list if no media found with specified IDs
```

#### Method: updateByMediaId(Long mediaId, String name, String description)
```yaml
Signature: void updateByMediaId(Long mediaId, String name, String description)
Purpose: "Update media name and description for media metadata management"

Business Logic Derivation:
  1. Used in CorrectionService and ParameterService for media metadata updates
  2. Enables efficient media metadata updates without loading full entity
  3. Critical for media correction workflows and parameter media management
  4. Used in correction workflows where media metadata needs to be corrected
  5. Supports efficient media metadata updates with minimal database operations

SQL Query: |
  UPDATE medias 
  SET name = ?, description = ? 
  WHERE id = ?

Parameters:
  - mediaId: Long (Media identifier to update)
  - name: String (New media name)
  - description: String (New media description)

Returns: void
Transaction: Required (uses @Modifying and @Transactional)
Error Handling: DataIntegrityViolationException for constraint violations
```

### Key Repository Usage Patterns

#### Pattern: save() for Media Lifecycle Management
```yaml
Usage: mediaRepository.save(media)
Purpose: "Create new media, update media information, and manage media lifecycle"

Business Logic Derivation:
  1. Used extensively throughout system for media management operations
  2. Handles media creation during file uploads and media updates
  3. Updates media information and manages media archival state
  4. Critical for media lifecycle management and file attachment operations
  5. Supports media operations with organisation association management

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findById() for Media Context Operations
```yaml
Usage: mediaRepository.findById(mediaId)
Purpose: "Retrieve media entity for media-specific operations and file access"

Business Logic Derivation:
  1. Used extensively throughout system for media context retrieval
  2. Critical for media validation, file access, and media-specific operations
  3. Used in media display, media updates, and media archival workflows
  4. Essential for media context management and file-based business logic
  5. Enables media-centric operations with comprehensive media information

Transaction: Not Required
Error Handling: Throws ResourceNotFoundException if media not found
```

#### Pattern: findAll(specification) for Dynamic Media Queries
```yaml
Usage: mediaRepository.findAll(specification, pageable)
Purpose: "Dynamic media discovery with complex filtering and pagination"

Business Logic Derivation:
  1. Used for advanced media search and listing operations
  2. Applies dynamic specifications for multi-criteria media filtering
  3. Supports pagination for large media datasets and media management
  4. Enables flexible media discovery and file management operations
  5. Critical for media management APIs and file administration functionality

Transaction: Not Required
Error Handling: Returns empty page if no matches found
```

#### Pattern: saveAll() for Bulk Media Operations
```yaml
Usage: mediaRepository.saveAll(mediaList)
Purpose: "Bulk media creation and updates for efficient multi-file operations"

Business Logic Derivation:
  1. Used in MediaService for bulk file upload operations
  2. Enables efficient bulk media creation with minimal database transactions
  3. Critical for multi-file upload workflows and bulk media processing
  4. Used in bulk file upload scenarios for performance optimization
  5. Supports efficient bulk media operations with transaction batching

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll(), findAll(Set<Long>), findAll(Specification)
  - existsById, count, findOne(Specification), count(Specification)

Transactional Methods:
  - save, delete, deleteById, saveAll, updateByMediaId

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid organisations_id)
    * NOT NULL constraint violations (originalFilename, filename, type, relativePath, organisations_id)
    * File system constraints and path validation issues
  - EntityNotFoundException: Media not found by ID or criteria
  - OptimisticLockException: Concurrent media modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - ResourceNotFoundException: Media not found during operations
  - FileSystemException: File system access and storage issues

Validation Rules:
  - name: Optional, max length 255 characters, user-friendly display name
  - description: Optional, text field for media description
  - originalFilename: Required, max length 255 characters, original uploaded filename
  - filename: Required, max length 255 characters, system-generated unique filename
  - type: Required, max length 255 characters, MIME type or file type
  - relativePath: Required, text field, relative file system path for storage
  - archived: Defaults to false, used for soft deletion of media files
  - organisation: Required, must reference existing organisation, immutable after creation

Business Constraints:
  - Cannot modify organisation association after media creation
  - Original filename should be preserved for audit and display purposes
  - System filename should be unique to prevent file conflicts
  - Relative path should be valid and accessible for file system operations
  - Media archival should be used instead of deletion for data integrity
  - Media updates should maintain audit trail with proper user attribution
  - File type should match actual file content for security
  - Media associations with parameters, tasks, jobs should be validated before deletion
  - File system storage should be consistent with database metadata
  - Media access should respect organisation-level security boundaries
  - Bulk operations should maintain transaction consistency
  - Media metadata updates should preserve file system integrity
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Media repository without JPA/Hibernate dependencies.
