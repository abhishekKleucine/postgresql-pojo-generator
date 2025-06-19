# ITaskMediaMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TaskMediaMapping
- **Primary Purpose**: Manages task media mapping entities for task-media associations with media attachment management, task media lifecycle control, and media association functionality
- **Key Relationships**: Mapping entity linking Task and Media with many-to-one relationships using composite key for comprehensive task media management and media attachment control
- **Performance Characteristics**: Low to moderate query volume with task-based media operations, bulk checklist media retrieval, and media mapping lifecycle management
- **Business Context**: Task media management component that provides task-scoped media attachments, media association lifecycle control, bulk media operations, and task media functionality for task documentation and media attachment capabilities

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| tasks_id | taskMediaId.taskId / task.id | Long | false | part of composite key |
| medias_id | taskMediaId.mediaId / media.id | Long | false | part of composite key |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Composite Key Structure
- **TaskMediaCompositeKey**: Composite key containing taskId and mediaId for unique task media associations

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | task | Task | LAZY | Associated task, not null, immutable |
| @ManyToOne | media | Media | LAZY | Associated media, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(TaskMediaCompositeKey id)`
- `findAll()`
- `save(TaskMediaMapping entity)`
- `deleteById(TaskMediaCompositeKey id)`
- `delete(TaskMediaMapping entity)`
- `existsById(TaskMediaCompositeKey id)`
- `count()`

### Custom Query Methods (4 methods - ALL methods documented)

- `deleteByTaskIdAndMediaId(Long taskId, Long mediaId)`
- `getByTaskIdAndMediaId(Long taskId, Long mediaId)`
- `findAllByChecklistIdsIn(List<Long> checklistIds)`
- `findByIdWithMedia(Long taskId, Long mediaId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods with composite key
Optional<TaskMediaMapping> findById(TaskMediaCompositeKey id)
List<TaskMediaMapping> findAll()
TaskMediaMapping save(TaskMediaMapping entity)
void deleteById(TaskMediaCompositeKey id)
void delete(TaskMediaMapping entity)
boolean existsById(TaskMediaCompositeKey id)
long count()
```

### Custom Query Methods

#### Method: deleteByTaskIdAndMediaId(Long taskId, Long mediaId)
```yaml
Signature: void deleteByTaskIdAndMediaId(Long taskId, Long mediaId)
Purpose: "Delete specific task media mapping for media lifecycle management and task media cleanup"

Business Logic Derivation:
  1. Used in TaskService for removing specific task media mappings during task media management and lifecycle operations
  2. Provides precise media mapping deletion for task media lifecycle management and media attachment cleanup operations
  3. Critical for task media management operations requiring specific mapping removal for media lifecycle and task management
  4. Used in task media management workflows for cleaning up specific media associations and mapping lifecycle control
  5. Enables task media lifecycle management with precise mapping deletion for comprehensive media management and cleanup

SQL Query: |
  DELETE FROM task_media_mapping 
  WHERE tasks_id = ? AND medias_id = ?

Parameters:
  - taskId: Long (Task identifier for specific media mapping deletion)
  - mediaId: Long (Media identifier for specific mapping deletion)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found for task and media combination
```

#### Method: getByTaskIdAndMediaId(Long taskId, Long mediaId)
```yaml
Signature: Optional<TaskMediaMapping> getByTaskIdAndMediaId(Long taskId, Long mediaId)
Purpose: "Find specific task media mapping for task media management and validation operations"

Business Logic Derivation:
  1. Used in TaskService for retrieving specific task media mappings during task media validation and management operations
  2. Provides precise media mapping access for task media operations enabling media validation and management functionality
  3. Critical for task media operations requiring specific mapping access for media validation and task media management
  4. Used in task media management workflows for accessing specific mappings for validation and media management operations
  5. Enables task media management with precise mapping access for comprehensive media validation and management control

SQL Query: |
  SELECT tmm.* FROM task_media_mapping tmm
  WHERE tmm.tasks_id = ? AND tmm.medias_id = ?

Parameters:
  - taskId: Long (Task identifier for specific mapping access)
  - mediaId: Long (Media identifier for specific mapping access)

Returns: Optional<TaskMediaMapping> (specific task media mapping wrapped in Optional)
Transaction: Not Required
Error Handling: Returns empty Optional if no mapping found for task and media combination
```

#### Method: findAllByChecklistIdsIn(List<Long> checklistIds)
```yaml
Signature: List<TaskMediaMapping> findAllByChecklistIdsIn(List<Long> checklistIds)
Purpose: "Find all task media mappings for multiple checklists for bulk operations and import/export processing"

Business Logic Derivation:
  1. Used in ImportExportChecklistService for bulk retrieval of task media mappings during import/export operations and bulk processing
  2. Provides efficient bulk media mapping access for import/export workflows enabling comprehensive checklist media processing
  3. Critical for bulk operations requiring task media mappings for multiple checklists for import/export and bulk processing workflows
  4. Used in import/export workflows for accessing task media mappings across multiple checklists for bulk media processing
  5. Enables bulk checklist operations with efficient media mapping retrieval for comprehensive import/export and bulk processing

SQL Query: |
  SELECT tmm.* FROM task_media_mapping tmm
  INNER JOIN tasks t ON t.id = tmm.tasks_id
  INNER JOIN stages s ON t.stages_id = s.id
  INNER JOIN checklists c ON c.id = s.checklists_id
  WHERE c.id IN (?, ?, ?, ...)
  AND t.archived = false

Parameters:
  - checklistIds: List<Long> (List of checklist identifiers for bulk media mapping retrieval)

Returns: List<TaskMediaMapping> (task media mappings for all tasks in specified checklists)
Transaction: Not Required
Error Handling: Returns empty list if no media mappings found for any checklist identifiers
```

#### Method: findByIdWithMedia(Long taskId, Long mediaId)
```yaml
Signature: TaskMediaMapping findByIdWithMedia(Long taskId, Long mediaId)
Purpose: "Find task media mapping with media details for mapping operations and media information access"

Business Logic Derivation:
  1. Used in ITaskMediaMapper for retrieving task media mappings with media details during mapping operations and media processing
  2. Provides task media mapping access with media information for mapping workflows enabling comprehensive media data processing
  3. Critical for mapping operations requiring task media mappings with media details for mapping processing and media management
  4. Used in media mapping workflows for accessing task media mappings with complete media information for mapping operations
  5. Enables mapping operations with comprehensive media information access for task media mapping and media processing workflows

SQL Query: |
  SELECT tmm.medias_id, tmm.tasks_id, tmm.created_at, tmm.modified_at, 
         tmm.created_by, tmm.modified_by,
         m.name AS media_name, m.filename, m.description, m.type, 
         m.archived, m.original_filename, m.relative_path
  FROM task_media_mapping tmm
  JOIN medias m ON tmm.medias_id = m.id
  WHERE tmm.tasks_id = ? AND tmm.medias_id = ?

Parameters:
  - taskId: Long (Task identifier for mapping with media details)
  - mediaId: Long (Media identifier for mapping with media details)

Returns: TaskMediaMapping (task media mapping with media details, null if not found)
Transaction: Not Required
Error Handling: Returns null if no mapping found for task and media combination
```

### Key Repository Usage Patterns

#### Pattern: save() for Task Media Association Management
```yaml
Usage: taskMediaMappingRepository.save(mapping)
Purpose: "Create task media associations for media attachment management and task media lifecycle control"

Business Logic Derivation:
  1. Used for task media association creation with composite key management and media attachment lifecycle control
  2. Provides task media mapping persistence with comprehensive association management and media lifecycle control
  3. Critical for task media association lifecycle management and media attachment operations requiring mapping creation
  4. Used in task media management workflows for association creation and media attachment operations
  5. Enables task media association management with comprehensive media lifecycle control and attachment management

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations, composite key conflicts
```

#### Pattern: Task Media Validation and Management Operations
```yaml
Usage: Task media mapping validation and management for media attachment functionality
Purpose: "Validate and manage task media mappings for comprehensive media attachment functionality and lifecycle control"

Business Logic Derivation:
  1. Task media validation enables proper media attachment functionality through mapping validation and management
  2. Media mapping management supports task media requirements and media attachment functionality for task processing
  3. Task media validation workflows depend on mapping access for proper media attachment validation and management
  4. Media attachment management requires mapping validation for comprehensive task media functionality and control
  5. Task media processing requires comprehensive mapping validation and management for media attachment functionality

Transaction: Not Required for validation operations, Required for management operations
Error Handling: Validation error handling and media mapping verification
```

#### Pattern: Bulk Media Operations and Import/Export Processing
```yaml
Usage: Bulk task media mapping operations for import/export and bulk processing workflows
Purpose: "Process task media mappings in bulk for import/export operations and comprehensive bulk media processing"

Business Logic Derivation:
  1. Bulk media operations enable efficient import/export processing with comprehensive task media mapping access
  2. Import/export workflows require bulk media mapping retrieval for complete checklist media processing and data transfer
  3. Bulk processing operations support import/export requirements and bulk media management functionality
  4. Checklist-based bulk operations enable efficient media processing for import/export and bulk management workflows
  5. Bulk media processing supports import/export functionality and comprehensive bulk media management operations

Transaction: Not Required for bulk retrieval operations
Error Handling: Bulk operation error handling and media mapping validation
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Task Media Management and Lifecycle Control
```yaml
Usage: Complete task media mapping lifecycle for media attachment functionality
Purpose: "Manage task media mappings for comprehensive media attachment functionality and lifecycle control"

Business Logic Derivation:
  1. Task media mappings provide media attachment functionality through task media associations and media management
  2. Media attachment lifecycle includes creation, validation, management operations, and cleanup workflows
  3. Task media validation operations require mapping access for media attachment validation and management functionality
  4. Media deletion operations require precise mapping cleanup for media lifecycle and task media management
  5. Media mapping lifecycle management supports task media requirements and media attachment functionality

Common Usage Examples:
  - taskMediaMappingRepository.getByTaskIdAndMediaId() in TaskService for media validation and management
  - taskMediaMappingRepository.deleteByTaskIdAndMediaId() for specific media mapping cleanup
  - Task media validation workflows for media attachment functionality and management operations
  - Media mapping lifecycle management for task media functionality and media attachment control
  - Comprehensive task media management with lifecycle control and media association management

Transaction: Required for lifecycle operations and media management
Error Handling: Media management error handling and mapping validation verification
```

### Pattern: Import/Export and Bulk Media Processing
```yaml
Usage: Bulk task media mapping operations for import/export workflows and bulk processing
Purpose: "Process task media mappings in bulk for comprehensive import/export operations and bulk media management"

Business Logic Derivation:
  1. Import/export workflows require bulk task media mapping retrieval for comprehensive checklist media processing and data transfer
  2. Bulk media processing enables import/export operations with complete task media mapping access and processing capabilities
  3. Checklist-based bulk operations ensure comprehensive media processing for import/export and bulk management workflows
  4. Bulk media operations coordinate task media mapping retrieval with import/export processing for comprehensive data transfer
  5. Media processing supports import/export requirements and bulk media management functionality for comprehensive operations

Common Usage Examples:
  - taskMediaMappingRepository.findAllByChecklistIdsIn() in ImportExportChecklistService for bulk media retrieval
  - Bulk task media processing for import/export operations and comprehensive checklist media management
  - Import/export workflows with task media mapping access and bulk media processing capabilities
  - Bulk media operations for comprehensive checklist processing and import/export functionality
  - Comprehensive import/export with task media mapping processing and bulk media management operations

Transaction: Not Required for bulk retrieval and import/export processing
Error Handling: Import/export operation error handling and bulk media processing validation
```

### Pattern: Media Mapping and Information Access Operations
```yaml
Usage: Task media mapping access with media information for mapping operations and media processing
Purpose: "Access task media mappings with comprehensive media information for mapping operations and media processing"

Business Logic Derivation:
  1. Mapping operations require task media mapping access with media information for comprehensive mapping processing and media management
  2. Media information access enables mapping workflows with complete media details for mapping operations and processing
  3. Task media mapping operations ensure proper media information access and mapping functionality during processing operations
  4. Mapping workflows coordinate media information retrieval with mapping processing for comprehensive media operations
  5. Media processing supports mapping requirements and media information access functionality for comprehensive mapping operations

Common Usage Examples:
  - taskMediaMappingRepository.findByIdWithMedia() in ITaskMediaMapper for mapping operations with media details
  - Media mapping operations with comprehensive media information access and mapping processing capabilities
  - Task media mapping workflows with media information retrieval and mapping processing functionality
  - Media information access for mapping operations and comprehensive media processing requirements
  - Comprehensive mapping operations with media information and task media mapping processing capabilities

Transaction: Not Required for mapping access and media information operations
Error Handling: Mapping operation error handling and media information verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, getByTaskIdAndMediaId, findAllByChecklistIdsIn
  - findByIdWithMedia, existsById, count

Transactional Methods:
  - save, delete, deleteById, deleteByTaskIdAndMediaId

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Composite key constraint violations (duplicate task-media combinations)
    * NOT NULL constraint violations (tasks_id, medias_id)
    * Foreign key violations (invalid tasks_id, medias_id references)
    * Unique constraint violations on composite key
  - EntityNotFoundException: Task media mapping not found by composite key or criteria
  - OptimisticLockException: Concurrent task media mapping modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or composite key operations
  - ConstraintViolationException: Task media mapping constraint violations

Validation Rules:
  - taskMediaId: Required, composite key containing taskId and mediaId for unique associations
  - task: Required, must reference existing task, immutable after creation
  - media: Required, must reference existing media, immutable after creation

Business Constraints:
  - Task media mappings must be unique for proper association management and media attachment integrity
  - Task and media references must be valid for association integrity and media attachment functionality
  - Task media mappings must support media attachment functionality requirements and media lifecycle management
  - Association lifecycle management must maintain referential integrity and media attachment functionality consistency
  - Media mapping cleanup must ensure proper media lifecycle control and task media functionality
  - Task media associations must support task requirements and media attachment functionality
  - Bulk operations must maintain transaction consistency and constraint integrity for media management
  - Media mapping lifecycle management must maintain task media functionality and media association consistency
  - Association management must maintain task media integrity and media attachment requirements
  - Cleanup operations must ensure proper media lifecycle management and task media attachment control
```

## Task Media Mapping Considerations

### Media Attachment Integration
```yaml
Task Association: Task media mappings enable media attachment functionality through task media associations
Media Attachments: Media associations enable task functionality with comprehensive media attachment capabilities
Attachment Lifecycle: Media mapping lifecycle includes creation, validation, and cleanup operations
Media Management: Comprehensive media management for task functionality and media attachment requirements
Association Control: Task media association control for media attachment functionality and lifecycle management
```

### Bulk Operations and Processing
```yaml
Bulk Retrieval: Efficient bulk media mapping retrieval for import/export and bulk processing operations
Checklist Processing: Checklist-based bulk operations for comprehensive media processing and management
Import/Export Support: Bulk media operations for import/export workflows and data transfer functionality
Processing Integration: Bulk processing integration for comprehensive media management and operations
Bulk Management: Comprehensive bulk media management for import/export and bulk processing requirements
```

### Validation and Management Integration
```yaml
Media Validation: Task media validation workflows with mapping access and validation functionality
Management Operations: Media management operations for task media functionality and attachment control
Lifecycle Control: Comprehensive media lifecycle control for task media functionality and management
Validation Processing: Media validation processing for task media functionality and attachment management
Management Integration: Media management integration for task functionality and media attachment requirements
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TaskMediaMapping repository without JPA/Hibernate dependencies, focusing on task media management and media attachment functionality patterns.
