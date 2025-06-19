# IParameterValueMediaRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ParameterValueMediaMapping
- **Primary Purpose**: Manages parameter value media mapping entities for parameter value-media associations with archived status management, media lifecycle control, and parameter value media attachments
- **Key Relationships**: Mapping entity linking ParameterValue and Media with many-to-one relationships using composite key for comprehensive parameter value media management and media lifecycle control
- **Performance Characteristics**: Low to moderate query volume with parameter value-based media retrieval, archival management, and media mapping lifecycle operations
- **Business Context**: Parameter value media management component that provides parameter value-scoped media attachments, archived status management, media lifecycle control, and parameter value media integration for data migration and correction workflows

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| parameter_values_id | parameterValueMediaId.parameterValueId / parameterValue.id | Long | false | part of composite key |
| medias_id | parameterValueMediaId.mediaId / media.id | Long | false | part of composite key |
| archived | archived | boolean | false | false |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Composite Key Structure
- **ParameterValueMediaCompositeKey**: Composite key containing parameterValueId and mediaId for unique parameter value media associations

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | parameterValue | ParameterValue | LAZY | Associated parameter value, not null, immutable |
| @ManyToOne | media | Media | LAZY | Associated media, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(ParameterValueMediaCompositeKey id)`
- `findAll()`
- `save(ParameterValueMediaMapping entity)`
- `deleteById(ParameterValueMediaCompositeKey id)`
- `delete(ParameterValueMediaMapping entity)`
- `existsById(ParameterValueMediaCompositeKey id)`
- `count()`

### Custom Query Methods (2 methods - ALL methods documented)

- `findMediaByParameterValueId(Long parameterValueId)`
- `findMediaByParameterValueIdAndMediaId(Long parameterValueId, Long mediaId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods with composite key
Optional<ParameterValueMediaMapping> findById(ParameterValueMediaCompositeKey id)
List<ParameterValueMediaMapping> findAll()
ParameterValueMediaMapping save(ParameterValueMediaMapping entity)
void deleteById(ParameterValueMediaCompositeKey id)
void delete(ParameterValueMediaMapping entity)
boolean existsById(ParameterValueMediaCompositeKey id)
long count()
```

### Custom Query Methods

#### Method: findMediaByParameterValueId(Long parameterValueId)
```yaml
Signature: List<JobLogMigrationParameterValueMediaMapping> findMediaByParameterValueId(Long parameterValueId)
Purpose: "Find media details for parameter value for data migration and media information retrieval"

Business Logic Derivation:
  1. Used in JobLogs migration for retrieving media details associated with parameter values during data migration operations
  2. Provides media information retrieval for migration workflows enabling comprehensive data migration and media processing
  3. Critical for migration operations requiring media details for parameter values for data migration and processing workflows
  4. Used in data migration workflows for accessing media information associated with parameter values for migration processing
  5. Enables migration data processing with media information retrieval for comprehensive parameter value media management

SQL Query: |
  SELECT m.type as type, m.description as description, m.relative_path as relativePath, 
         m.filename as filename, m.name as name
  FROM parameter_value_media_mapping pvmm
  INNER JOIN medias m ON m.id = pvmm.medias_id
  WHERE pvmm.parameter_values_id = ?

Parameters:
  - parameterValueId: Long (Parameter value identifier to get media details for)

Returns: List<JobLogMigrationParameterValueMediaMapping> (media details projection for migration)
Transaction: Not Required
Error Handling: Returns empty list if no media found for parameter value
```

#### Method: findMediaByParameterValueIdAndMediaId(Long parameterValueId, Long mediaId)
```yaml
Signature: ParameterValueMediaMapping findMediaByParameterValueIdAndMediaId(Long parameterValueId, Long mediaId)
Purpose: "Find specific parameter value media mapping for correction management and media lifecycle operations"

Business Logic Derivation:
  1. Used in CorrectionService for retrieving specific parameter value media mappings during correction workflows and media management
  2. Provides precise media mapping access for correction operations enabling media lifecycle management and archival control
  3. Critical for correction operations requiring specific media mapping access for correction media management and lifecycle control
  4. Used in correction media management workflows for accessing specific media mappings for archival and correction processing
  5. Enables correction media management with precise mapping access for comprehensive correction workflow and media lifecycle control

SQL Query: |
  SELECT * FROM parameter_value_media_mapping 
  WHERE medias_id = ? AND parameter_values_id = ?

Parameters:
  - parameterValueId: Long (Parameter value identifier for specific mapping access)
  - mediaId: Long (Media identifier for specific mapping access)

Returns: ParameterValueMediaMapping (specific parameter value media mapping, null if not found)
Transaction: Not Required
Error Handling: Returns null if no mapping found for parameter value and media combination
```

### Key Repository Usage Patterns

#### Pattern: save() for Parameter Value Media Lifecycle Management
```yaml
Usage: parameterValueMediaRepository.save(mapping)
Purpose: "Create and update parameter value media mappings for media lifecycle management and archival control"

Business Logic Derivation:
  1. Used in CorrectionService for parameter value media mapping updates during correction workflows and media lifecycle management
  2. Provides parameter value media mapping persistence with archival status management and media lifecycle control
  3. Critical for parameter value media lifecycle management and correction media operations requiring mapping updates
  4. Used in correction media management workflows for mapping updates and archival status management operations
  5. Enables parameter value media lifecycle management with comprehensive mapping persistence and archival control

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations, composite key conflicts
```

#### Pattern: Migration and Data Processing Operations
```yaml
Usage: findMediaByParameterValueId() for migration data processing and media information retrieval
Purpose: "Retrieve media information for parameter values during migration and data processing operations"

Business Logic Derivation:
  1. Migration operations require parameter value media information for comprehensive data migration and processing workflows
  2. Media information retrieval enables migration workflows with complete parameter value media data and processing capabilities
  3. Data migration workflows depend on media information access for proper migration processing and data integrity
  4. Migration processing requires comprehensive media details for parameter values for complete data migration operations
  5. Parameter value media migration supports data processing requirements and migration workflow functionality

Transaction: Not Required for migration data retrieval
Error Handling: Empty result handling for parameter values without media associations
```

#### Pattern: Correction Media Management and Archival Control
```yaml
Usage: Correction workflows with parameter value media mapping management and archival operations
Purpose: "Manage parameter value media mappings during correction workflows with archival control and lifecycle management"

Business Logic Derivation:
  1. Correction workflows require parameter value media mapping management for correction media processing and lifecycle control
  2. Media archival operations enable correction workflow with media lifecycle management and archival status control
  3. Correction media management depends on parameter value media mapping access for proper correction processing and media control
  4. Parameter value media archival supports correction requirements and media lifecycle management functionality
  5. Correction media lifecycle management enables comprehensive correction processing with media archival and lifecycle control

Transaction: Required for correction media lifecycle operations
Error Handling: Correction operation error handling and media mapping validation
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Migration Data Processing and Media Information Retrieval
```yaml
Usage: Data migration workflows with parameter value media information retrieval
Purpose: "Process parameter value media information during migration for comprehensive data migration and processing"

Business Logic Derivation:
  1. Data migration workflows require comprehensive parameter value media information for complete migration processing and data integrity
  2. Migration media processing enables data migration with complete media information and parameter value media data
  3. Parameter value media migration ensures proper media information transfer during migration and data processing operations
  4. Migration workflows coordinate parameter value media retrieval with data processing for comprehensive migration operations
  5. Media information migration supports migration requirements and data processing functionality for complete data migration

Common Usage Examples:
  - parameterValueMediaRepository.findMediaByParameterValueId() in JobLogs migration for media information retrieval
  - Migration data processing with parameter value media information and comprehensive data migration workflows
  - Parameter value media migration for complete data transfer and migration processing operations
  - Media information processing during migration for comprehensive parameter value media management
  - Migration workflows with parameter value media data and complete migration processing capabilities

Transaction: Not Required for migration data retrieval
Error Handling: Migration data processing error handling and media information validation
```

### Pattern: Correction Media Management and Lifecycle Control
```yaml
Usage: Correction workflows with parameter value media mapping management and archival operations
Purpose: "Manage parameter value media mappings during correction workflows with comprehensive lifecycle and archival control"

Business Logic Derivation:
  1. Correction workflows require parameter value media mapping management for correction media processing and comprehensive lifecycle control
  2. Media archival operations enable correction workflow with media lifecycle management and archival status control for correction processing
  3. Correction media management ensures proper media mapping access and lifecycle control during correction operations and processing
  4. Parameter value media archival supports correction requirements and media lifecycle management for comprehensive correction functionality
  5. Correction media lifecycle management enables comprehensive correction processing with media archival control and lifecycle management

Common Usage Examples:
  - parameterValueMediaRepository.findMediaByParameterValueIdAndMediaId() in CorrectionService for specific mapping access
  - parameterValueMediaRepository.save() for media mapping archival status updates during correction processing
  - Correction media archival management with parameter value media mapping lifecycle control and archival operations
  - Media lifecycle management during correction workflows for comprehensive correction media processing and control
  - Parameter value media archival operations for correction workflow and media lifecycle management requirements

Transaction: Required for correction media lifecycle operations and archival management
Error Handling: Correction operation error handling and media mapping lifecycle validation
```

### Pattern: Parameter Value Media Association and Attachment Management
```yaml
Usage: Parameter value media mapping management for media attachment and association functionality
Purpose: "Manage parameter value media associations for comprehensive media attachment functionality and lifecycle control"

Business Logic Derivation:
  1. Parameter value media mappings enable media attachment functionality through parameter value associations and media management
  2. Media association lifecycle management supports parameter value requirements and media attachment functionality for parameter processing
  3. Parameter value media mapping lifecycle includes creation, association management, and archival operations for media control
  4. Media attachment management enables comprehensive parameter value functionality with media association capabilities and lifecycle control
  5. Media mapping lifecycle control supports parameter value operations and media attachment management requirements for parameter functionality

Common Attachment Patterns:
  - Parameter value media mapping creation for media attachment functionality and parameter value media associations
  - Media attachment lifecycle management with parameter value media mapping control and association management
  - Parameter value media archival operations for media lifecycle management and attachment control functionality
  - Media association management for parameter value functionality and media attachment requirements
  - Comprehensive parameter value media management with attachment functionality and lifecycle control operations

Transaction: Required for media attachment lifecycle operations and association management
Error Handling: Media attachment validation and parameter value media association verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findMediaByParameterValueId, findMediaByParameterValueIdAndMediaId
  - existsById, count

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
    * Composite key constraint violations (duplicate parameter value-media combinations)
    * NOT NULL constraint violations (parameter_values_id, medias_id)
    * Foreign key violations (invalid parameter_values_id, medias_id references)
    * Unique constraint violations on composite key
  - EntityNotFoundException: Parameter value media mapping not found by composite key or criteria
  - OptimisticLockException: Concurrent parameter value media mapping modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or composite key operations
  - ConstraintViolationException: Parameter value media mapping constraint violations

Validation Rules:
  - parameterValueMediaId: Required, composite key containing parameterValueId and mediaId for unique associations
  - parameterValue: Required, must reference existing parameter value, immutable after creation
  - media: Required, must reference existing media, immutable after creation
  - archived: Defaults to false, boolean for media lifecycle management and archival control

Business Constraints:
  - Parameter value media mappings must be unique for proper association management and data integrity
  - Parameter value and media references must be valid for association integrity and media attachment functionality
  - Parameter value media mappings must support media attachment functionality requirements and media lifecycle management
  - Association lifecycle management must maintain referential integrity and media attachment functionality consistency
  - Media archival management must ensure proper media lifecycle control and parameter value media functionality
  - Parameter value media associations must support parameter requirements and media attachment functionality
  - Archival operations must maintain parameter value media integrity and media lifecycle requirements
  - Media mapping lifecycle management must maintain parameter value functionality and media association consistency
  - Association management must maintain parameter value media integrity and media attachment requirements
  - Lifecycle operations must ensure proper parameter value media management and media attachment control
```

## Parameter Value Media Mapping Considerations

### Media Attachment Integration
```yaml
Parameter Value Association: Parameter value media mappings enable media attachment functionality with parameter value associations
Media Attachments: Media associations enable parameter value functionality with comprehensive media attachment capabilities
Attachment Lifecycle: Media mapping lifecycle includes creation, association management, and archival operations
Media Management: Comprehensive media management for parameter value functionality and media attachment requirements
Association Control: Parameter value media association control for media attachment functionality and lifecycle management
```

### Archival and Lifecycle Management
```yaml
Archival Status: Archived flag for media lifecycle management and parameter value media archival control
Media Lifecycle: Media mapping lifecycle includes creation, archival management, and lifecycle control operations
Lifecycle Control: Comprehensive media lifecycle control for parameter value media functionality and archival management
Archival Operations: Media archival operations for parameter value media lifecycle and archival control functionality
Status Management: Archival status management for media lifecycle and parameter value media functionality
```

### Migration and Data Processing Integration
```yaml
Migration Support: Parameter value media mapping migration for data processing and migration workflow functionality
Data Processing: Media information retrieval for migration workflows and data processing operations
Migration Workflows: Parameter value media migration with comprehensive data processing and migration capabilities
Information Retrieval: Media information access for migration processing and data migration requirements
Processing Integration: Migration data processing with parameter value media information and migration functionality
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the ParameterValueMediaMapping repository without JPA/Hibernate dependencies, focusing on parameter value media management and media attachment lifecycle patterns.
