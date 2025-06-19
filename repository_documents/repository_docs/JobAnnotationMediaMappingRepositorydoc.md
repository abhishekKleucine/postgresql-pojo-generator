# IJobAnnotationMediaMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: JobAnnotationMediaMapping
- **Primary Purpose**: Manages job annotation media mapping entities for job annotation-media associations with job-scoped media management and annotation lifecycle control
- **Key Relationships**: Mapping entity linking JobAnnotation, Job, and Media with many-to-one relationships using composite key for comprehensive job annotation media management
- **Performance Characteristics**: Low to moderate query volume with job-based bulk deletion operations and media mapping lifecycle management
- **Business Context**: Job annotation media management component that provides job-scoped annotation media attachments, media mapping lifecycle control, and annotation media cleanup for job annotation functionality and media association management

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| jobs_id | jobAnnotationMediaId.jobId / job.id | Long | false | part of composite key |
| job_annotations_id | jobAnnotationMediaId.jobAnnotationId / jobAnnotation.id | Long | false | part of composite key |
| medias_id | jobAnnotationMediaId.mediaId / media.id | Long | false | part of composite key |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Composite Key Structure
- **JobAnnotationMediaCompositeKey**: Triple composite key containing jobId, jobAnnotationId, and mediaId for unique job annotation media associations

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | jobAnnotation | JobAnnotation | LAZY | Associated job annotation, not null, immutable |
| @ManyToOne | job | Job | LAZY | Associated job, not null, immutable |
| @ManyToOne | media | Media | LAZY | Associated media, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(JobAnnotationMediaCompositeKey id)`
- `findAll()`
- `save(JobAnnotationMediaMapping entity)`
- `deleteById(JobAnnotationMediaCompositeKey id)`
- `delete(JobAnnotationMediaMapping entity)`
- `existsById(JobAnnotationMediaCompositeKey id)`
- `count()`

### Custom Query Methods (1 method - ALL methods documented)

- `deleteAllByJobId(Long jobId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods with composite key
Optional<JobAnnotationMediaMapping> findById(JobAnnotationMediaCompositeKey id)
List<JobAnnotationMediaMapping> findAll()
JobAnnotationMediaMapping save(JobAnnotationMediaMapping entity)
void deleteById(JobAnnotationMediaCompositeKey id)
void delete(JobAnnotationMediaMapping entity)
boolean existsById(JobAnnotationMediaCompositeKey id)
long count()
```

### Custom Query Methods

#### Method: deleteAllByJobId(Long jobId)
```yaml
Signature: void deleteAllByJobId(Long jobId)
Purpose: "Delete all job annotation media mappings for specific job for job annotation cleanup and media mapping lifecycle management"

Business Logic Derivation:
  1. Used in JobAnnotationService for bulk deletion of job annotation media mappings during job annotation updates and cleanup operations
  2. Provides efficient bulk cleanup of job annotation media mappings when job annotations are being updated or deleted
  3. Critical for job annotation lifecycle management requiring removal of all media associations for specific jobs
  4. Used in job annotation management workflows for cleaning up existing media mappings before creating new associations
  5. Enables job annotation media lifecycle management with bulk cleanup operations for comprehensive media mapping control

SQL Query: |
  DELETE FROM job_annotation_media_mapping 
  WHERE jobs_id = ?

Parameters:
  - jobId: Long (Job identifier for job-scoped media mapping deletion)

Returns: void
Transaction: Required (@Transactional annotation on repository)
Error Handling: No exception if no matching records found for job
```

### Key Repository Usage Patterns

#### Pattern: save() for Job Annotation Media Association Management
```yaml
Usage: jobAnnotationMediaMappingRepository.save(mapping)
Purpose: "Create job annotation media associations for annotation media management and lifecycle control"

Business Logic Derivation:
  1. Used for job annotation media association creation with composite key management and annotation media lifecycle control
  2. Provides job annotation media mapping persistence with comprehensive association management and media lifecycle control
  3. Critical for job annotation media association lifecycle management and annotation media operations
  4. Used in job annotation media management workflows for association creation and media attachment operations
  5. Enables job annotation media association management with comprehensive media lifecycle control and association management

Transaction: Required (@Transactional annotation on repository)
Error Handling: DataIntegrityViolationException for constraint violations, composite key conflicts
```

#### Pattern: Bulk Job Annotation Media Cleanup Operations
```yaml
Usage: deleteAllByJobId() for bulk job annotation media mapping cleanup
Purpose: "Efficiently clean up all job annotation media mappings for specific jobs during annotation lifecycle operations"

Business Logic Derivation:
  1. Bulk cleanup operations enable efficient job annotation media mapping lifecycle management during annotation updates
  2. Job annotation update workflows require complete removal of existing media mappings before creating new associations
  3. Job annotation deletion workflows require comprehensive cleanup of all associated media mappings for data integrity
  4. Bulk deletion operations support job annotation lifecycle management and media association cleanup requirements
  5. Efficient bulk cleanup reduces database overhead for job annotation media management and lifecycle operations

Transaction: Required (@Transactional annotation on repository)
Error Handling: Bulk deletion operations with transaction consistency and constraint management
```

#### Pattern: Job Annotation Media Lifecycle Management
```yaml
Usage: Job annotation media mapping management for annotation functionality and media lifecycle control
Purpose: "Manage job annotation media associations for comprehensive annotation functionality and media lifecycle management"

Business Logic Derivation:
  1. Job annotation media mappings enable annotation functionality with media attachments and comprehensive annotation management
  2. Media association lifecycle management supports job annotation requirements and annotation media functionality
  3. Job annotation media mapping lifecycle includes creation, association management, and cleanup operations
  4. Annotation media management enables comprehensive job annotation functionality with media attachment capabilities
  5. Media mapping lifecycle control supports job annotation operations and annotation media management requirements

Transaction: Required for lifecycle operations and association management
Error Handling: Composite key validation and association integrity verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Job Annotation Media Management and Lifecycle
```yaml
Usage: Complete job annotation media mapping lifecycle for annotation functionality
Purpose: "Manage job annotation media mappings for comprehensive annotation functionality and media lifecycle control"

Business Logic Derivation:
  1. Job annotation media mappings provide annotation functionality with media attachments and comprehensive annotation management
  2. Annotation media lifecycle includes creation, association management, update operations, and cleanup workflows
  3. Job annotation update operations require complete cleanup of existing media mappings followed by new association creation
  4. Job annotation deletion operations require comprehensive cleanup of all associated media mappings for data integrity
  5. Media mapping lifecycle management supports job annotation requirements and annotation media functionality

Common Usage Examples:
  - jobAnnotationMediaMappingRepository.deleteAllByJobId(jobId) in JobAnnotationService for annotation media cleanup
  - Bulk media mapping deletion during job annotation updates and lifecycle management operations
  - Job annotation media mapping cleanup for annotation deletion and data integrity management
  - Media association lifecycle management for job annotation functionality and annotation requirements
  - Comprehensive job annotation media management with lifecycle control and association management

Transaction: Required for lifecycle operations and bulk cleanup
Error Handling: Bulk operation error handling and association integrity verification
```

### Pattern: Job Annotation Update and Media Association Management
```yaml
Usage: Job annotation update workflows with media mapping lifecycle management
Purpose: "Manage job annotation media associations during annotation updates and lifecycle operations"

Business Logic Derivation:
  1. Job annotation update workflows require complete cleanup of existing media mappings to ensure data consistency
  2. Media mapping update operations involve bulk deletion followed by new association creation for annotation functionality
  3. Job annotation media lifecycle management ensures proper association control and annotation media functionality
  4. Update workflows coordinate media mapping cleanup with new association creation for comprehensive annotation management
  5. Annotation media association management supports job annotation requirements and media lifecycle control

Common Update Patterns:
  - Bulk deletion of existing job annotation media mappings during annotation update operations
  - Complete media mapping cleanup before creating new associations for annotation functionality
  - Job annotation media lifecycle management with association update and media attachment control
  - Media mapping update workflows with bulk cleanup and new association creation operations
  - Comprehensive annotation media management during job annotation update and lifecycle operations

Transaction: Required for update workflows and association lifecycle management
Error Handling: Update operation error handling and media association validation
```

### Pattern: Job Annotation Deletion and Cleanup Operations
```yaml
Usage: Job annotation deletion workflows with comprehensive media mapping cleanup
Purpose: "Clean up all job annotation media associations during annotation deletion and lifecycle management"

Business Logic Derivation:
  1. Job annotation deletion operations require comprehensive cleanup of all associated media mappings for data integrity
  2. Media mapping cleanup operations ensure proper data cleanup and annotation lifecycle management
  3. Job annotation deletion workflows coordinate annotation removal with media mapping cleanup for data consistency
  4. Comprehensive cleanup operations support annotation lifecycle management and data integrity requirements
  5. Media mapping cleanup enables proper job annotation deletion and annotation lifecycle control

Common Deletion Patterns:
  - Complete cleanup of job annotation media mappings during annotation deletion operations
  - Job annotation deletion workflows with comprehensive media mapping cleanup and data integrity management
  - Media association cleanup for annotation lifecycle management and data consistency requirements
  - Bulk deletion operations for efficient annotation cleanup and media mapping lifecycle management
  - Comprehensive annotation deletion with media association cleanup and data integrity control

Transaction: Required for deletion workflows and cleanup operations
Error Handling: Deletion operation error handling and data integrity verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, existsById, count

Transactional Methods:
  - save, delete, deleteById, deleteAllByJobId

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback per repository annotation)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Composite key constraint violations (duplicate job-annotation-media combinations)
    * NOT NULL constraint violations (jobs_id, job_annotations_id, medias_id)
    * Foreign key violations (invalid jobs_id, job_annotations_id, medias_id references)
    * Unique constraint violations on composite key
  - EntityNotFoundException: Job annotation media mapping not found by composite key or criteria
  - OptimisticLockException: Concurrent job annotation media mapping modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or composite key operations
  - ConstraintViolationException: Job annotation media mapping constraint violations

Validation Rules:
  - jobAnnotationMediaId: Required, composite key containing jobId, jobAnnotationId, and mediaId for unique associations
  - jobAnnotation: Required, must reference existing job annotation, immutable after creation
  - job: Required, must reference existing job, immutable after creation
  - media: Required, must reference existing media, immutable after creation

Business Constraints:
  - Job annotation media mappings must be unique for proper association management and data integrity
  - Job, job annotation, and media references must be valid for association integrity and annotation functionality
  - Job annotation media mappings must support annotation functionality requirements and media lifecycle management
  - Association lifecycle management must maintain referential integrity and annotation functionality consistency
  - Media mapping cleanup must ensure proper data cleanup and annotation lifecycle management
  - Job annotation media associations must support annotation requirements and media attachment functionality
  - Bulk operations must maintain transaction consistency and constraint integrity for annotation management
  - Media mapping lifecycle management must maintain annotation functionality and association consistency
  - Association management must maintain job annotation integrity and media lifecycle requirements
  - Cleanup operations must ensure proper annotation lifecycle management and data integrity control
```

## Job Annotation Media Mapping Considerations

### Job Annotation Integration
```yaml
Annotation Functionality: Job annotation media mappings enable annotation functionality with media attachments
Job Association: Job association ensures annotation media mappings are properly scoped and managed within job context
Media Attachments: Media associations enable annotation functionality with comprehensive media attachment capabilities
Lifecycle Management: Annotation media mapping lifecycle supports annotation requirements and media functionality
Association Control: Comprehensive association control for annotation functionality and media lifecycle management
```

### Media Lifecycle Management
```yaml
Media Associations: Job annotation media associations for annotation functionality and media attachment capabilities
Association Lifecycle: Media mapping lifecycle includes creation, association management, and cleanup operations
Bulk Operations: Efficient bulk operations for annotation media management and lifecycle control
Media Cleanup: Comprehensive media mapping cleanup for annotation lifecycle and data integrity management
Association Management: Media association management for annotation functionality and lifecycle requirements
```

### Annotation Workflow Integration
```yaml
Update Workflows: Job annotation update workflows with media mapping lifecycle and association management
Deletion Workflows: Annotation deletion workflows with comprehensive media mapping cleanup and data integrity
Creation Workflows: Annotation creation workflows with media association management and annotation functionality
Lifecycle Control: Comprehensive annotation lifecycle control with media mapping management and association integrity
Workflow Coordination: Annotation workflow coordination with media mapping lifecycle and association management
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the JobAnnotationMediaMapping repository without JPA/Hibernate dependencies, focusing on job annotation media management and association lifecycle patterns.
