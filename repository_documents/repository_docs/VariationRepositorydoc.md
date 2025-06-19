# IVariationRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Variation (extends UserAuditIdentifiableBase)
- **Primary Purpose**: Manages variation entities for process variation management with parameter value variations, job-level variation tracking, and variation lifecycle functionality
- **Key Relationships**: Links ParameterValue, Job, and VariationMediaMapping entities for comprehensive variation management and change tracking control
- **Performance Characteristics**: High query volume with variation creation, validation operations, and variation lifecycle management
- **Business Context**: Process variation management component that provides parameter value variations, job variation tracking, variation validation, and change management functionality for process control and variation workflows

## Entity Mapping Documentation

### Field Mappings (Inherits from UserAuditIdentifiableBase)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| parameter_values_id | parameterValueId / parameterValue.id | Long | false | null | Foreign key to parameter_values, immutable |
| jobs_id | jobId / job.id | Long | false | null | Foreign key to jobs, immutable |
| new_details | newDetails | JsonNode | false | '{}' | New variation data in JSONB format |
| old_details | oldDetails | JsonNode | false | '{}' | Original variation data in JSONB format |
| type | type | Action.Variation | false | null | Variation type enum (VALIDATION, SHOULD_BE) |
| name | name | String | false | null | Variation name, text field |
| description | description | String | false | null | Variation description, text field |
| variation_number | variationNumber | String | true | null | Variation number identifier |
| config_id | configId | String | false | null | Configuration identifier for variation |
| created_at | createdAt | Long | false | current_timestamp | |
| modified_at | modifiedAt | Long | false | current_timestamp | |
| created_by | createdBy.id | Long | true | null | |
| modified_by | modifiedBy.id | Long | true | null | |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | parameterValue | ParameterValue | LAZY | Associated parameter value, not null |
| @ManyToOne | job | Job | LAZY | Associated job, not null, immutable |
| @OneToMany | medias | VariationMediaMapping | LAZY | Variation media mappings, cascade ALL |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Variation entity)`
- `saveAll(Iterable<Variation> entities)`
- `deleteById(Long id)`
- `delete(Variation entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (8 methods - ALL methods documented)

**Variation Retrieval Methods:**
- `findAllByJobIdAndParameterName(Long jobId, String parameterName, Pageable pageable)`
- `findVariationsByParameterValueId(Long parameterValueId)`
- `findAllByParameterValueIdAndType(Long parameterValueId, Action.Variation type)`
- `findByTaskExecutionId(Long previousTaskExecutionId)`

**Variation Validation Methods:**
- `existsAllByVariationNumberOrNameForJob(String variationNumber, String name, Long jobId, Long taskId)`
- `existsByConfigIdsForParameterValueId(List<String> configIds, Long parameterValueId)`

**Variation Lifecycle Methods:**
- `reconfigureVariationsOfParameterValue(Long parameterValueId)` (@Modifying)
- `deleteByVariationId(Long variationId)` (@Modifying)

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Variation> findById(Long id)
List<Variation> findAll()
Variation save(Variation entity)
List<Variation> saveAll(Iterable<Variation> entities)
void deleteById(Long id)
void delete(Variation entity)
boolean existsById(Long id)
long count()
```

### Variation Retrieval Methods

#### Method: findAllByJobIdAndParameterName(Long jobId, String parameterName, Pageable pageable)
```yaml
Signature: Page<VariationView> findAllByJobIdAndParameterName(Long jobId, String parameterName, Pageable pageable)
Purpose: "Find variations by job and parameter name with pagination for variation reporting and job-level variation management"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionService for variation reporting during job variation analysis and variation management operations
  2. Provides paginated job-level variation access for variation workflows enabling comprehensive variation reporting and job functionality
  3. Critical for variation reporting operations requiring job-based variation access for variation analysis and reporting management
  4. Used in variation reporting workflows for accessing job variations with parameter filtering for variation analysis and reporting operations
  5. Enables variation reporting with paginated access for comprehensive job variation management and analysis control

SQL Query: |
  SELECT v.name AS name,
         v.id AS id,
         v.type AS type,
         v.description AS description,
         p.label AS parameterName,
         p.type AS parameterType,
         t.order_tree AS taskOrderTree,
         te.order_tree AS taskExecutionOrderTree,
         CAST(v.new_details AS text) AS newVariation,
         s.order_tree AS stageOrderTree,
         v.jobs_id AS jobId,
         pv.parameters_id AS parameterId,
         v.variation_number AS variationNumber,
         CAST(v.old_details AS text) AS oldVariation
  FROM variations v
  INNER JOIN jobs j ON j.id = v.jobs_id
  INNER JOIN parameter_values pv ON pv.id = v.parameter_values_id
  INNER JOIN task_executions te ON pv.task_executions_id = te.id
  INNER JOIN parameters p ON p.id = pv.parameters_id
  INNER JOIN tasks t ON p.tasks_id = t.id
  INNER JOIN stages s ON s.id = t.stages_id
  WHERE v.jobs_id = ? AND (CAST(? AS VARCHAR) IS NULL OR p.label ILIKE CONCAT('%', CAST(? AS VARCHAR), '%'))
  ORDER BY s.order_tree, t.order_tree, p.order_tree, pv.id

Parameters:
  - jobId: Long (Job identifier for job-specific variation retrieval)
  - parameterName: String (Parameter name filter for variation filtering, nullable)
  - pageable: Pageable (Pagination configuration for variation reporting)

Returns: Page<VariationView> (paginated variation views for job and parameter criteria)
Transaction: Not Required
Error Handling: Returns empty page if no variations found for job and parameter criteria
```

#### Method: findVariationsByParameterValueId(Long parameterValueId)
```yaml
Signature: List<VariationView> findVariationsByParameterValueId(Long parameterValueId)
Purpose: "Find variations by parameter value ID for parameter-specific variation management and variation retrieval"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionService for parameter value variation retrieval during variation management and parameter processing operations
  2. Provides parameter-specific variation access for parameter workflows enabling comprehensive variation management and parameter functionality
  3. Critical for parameter variation operations requiring variation access for parameter processing and variation management
  4. Used in parameter processing workflows for accessing parameter variations for variation operations and parameter management
  5. Enables parameter variation management with comprehensive variation access for parameter processing and variation control

SQL Query: |
  SELECT v.name AS name,
         v.id AS id,
         v.type AS type,
         v.description AS description,
         p.label AS parameterName,
         p.type AS parameterType,
         t.order_tree AS taskOrderTree,
         CAST(v.new_details AS text) AS newVariation,
         s.order_tree AS stageOrderTree,
         v.jobs_id AS jobId,
         pv.parameters_id AS parameterId,
         v.variation_number AS variationNumber,
         CAST(v.old_details AS text) AS oldVariation
  FROM variations v
  INNER JOIN jobs j ON j.id = v.jobs_id
  INNER JOIN parameter_values pv ON pv.id = v.parameter_values_id
  INNER JOIN parameters p ON p.id = pv.parameters_id
  INNER JOIN tasks t ON p.tasks_id = t.id
  INNER JOIN stages s ON s.id = t.stages_id
  WHERE pv.id = ?
  ORDER BY id DESC

Parameters:
  - parameterValueId: Long (Parameter value identifier for parameter-specific variation retrieval)

Returns: List<VariationView> (variation views for parameter value)
Transaction: Not Required
Error Handling: Returns empty list if no variations found for parameter value
```

#### Method: findAllByParameterValueIdAndType(Long parameterValueId, Action.Variation type)
```yaml
Signature: List<Variation> findAllByParameterValueIdAndType(Long parameterValueId, Action.Variation type)
Purpose: "Find variations by parameter value ID and type for type-specific variation management and variation processing"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionValidationService and ParameterExecutionService for type-specific variation retrieval during validation and execution operations
  2. Provides type-filtered variation access for parameter workflows enabling comprehensive type-based variation management and parameter functionality
  3. Critical for parameter processing operations requiring type-specific variation access for validation processing and parameter management
  4. Used in parameter validation workflows for accessing type-specific variations for validation operations and parameter processing
  5. Enables type-based variation management with filtered variation access for comprehensive parameter processing and validation control

SQL Query: |
  SELECT v.* FROM variations v
  WHERE v.parameter_values_id = ? AND v.type = ?

Parameters:
  - parameterValueId: Long (Parameter value identifier for variation context)
  - type: Action.Variation (Variation type for type-specific filtering - VALIDATION, SHOULD_BE)

Returns: List<Variation> (variations for parameter value and type)
Transaction: Not Required
Error Handling: Returns empty list if no variations found for parameter value and type
```

#### Method: findByTaskExecutionId(Long previousTaskExecutionId)
```yaml
Signature: List<Variation> findByTaskExecutionId(Long previousTaskExecutionId)
Purpose: "Find variations by task execution ID for task execution variation management and variation copying"

Business Logic Derivation:
  1. Used in TaskExecutionService for variation retrieval during task execution copying and variation replication operations
  2. Provides task execution-scoped variation access for task workflows enabling comprehensive variation copying and task functionality
  3. Critical for task execution operations requiring variation replication for task processing and variation management
  4. Used in task execution workflows for accessing task variations for variation copying and task management operations
  5. Enables task execution variation management with comprehensive variation access for task processing and variation control

SQL Query: |
  SELECT v.* FROM variations v
  INNER JOIN parameter_values pv ON pv.id = v.parameter_values_id
  INNER JOIN task_executions te ON te.id = pv.task_executions_id
  WHERE te.id = ?

Parameters:
  - previousTaskExecutionId: Long (Task execution identifier for task-specific variation retrieval)

Returns: List<Variation> (variations associated with task execution)
Transaction: Not Required
Error Handling: Returns empty list if no variations found for task execution
```

### Variation Validation Methods

#### Method: existsAllByVariationNumberOrNameForJob(String variationNumber, String name, Long jobId, Long taskId)
```yaml
Signature: boolean existsAllByVariationNumberOrNameForJob(String variationNumber, String name, Long jobId, Long taskId)
Purpose: "Check if variation number or name exists for job to prevent duplicate variations and ensure variation uniqueness"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionService for variation uniqueness validation during variation creation and duplicate prevention operations
  2. Provides variation uniqueness validation for variation workflows enabling comprehensive duplicate prevention and variation functionality
  3. Critical for variation creation operations requiring uniqueness validation for variation management and duplicate prevention
  4. Used in variation creation workflows for validating variation uniqueness for variation operations and management processing
  5. Enables variation uniqueness management with comprehensive validation for variation creation and duplicate control

SQL Query: |
  SELECT CASE WHEN count(v.id) > 0 THEN true ELSE false END
  FROM variations v
  INNER JOIN parameter_values pv ON pv.id = v.parameter_values_id
  INNER JOIN task_executions te ON te.id = pv.task_executions_id
  INNER JOIN tasks t ON t.id = te.tasks_id
  WHERE v.jobs_id = ? AND t.id <> ? 
  AND (v.variation_number = ? OR v.name = ?)

Parameters:
  - variationNumber: String (Variation number to check for uniqueness)
  - name: String (Variation name to check for uniqueness)
  - jobId: Long (Job identifier for job-scoped uniqueness check)
  - taskId: Long (Task identifier to exclude from uniqueness check)

Returns: boolean (true if variation number or name exists for job, false otherwise)
Transaction: Not Required
Error Handling: Returns false if no duplicate variations found
```

#### Method: existsByConfigIdsForParameterValueId(List<String> configIds, Long parameterValueId)
```yaml
Signature: boolean existsByConfigIdsForParameterValueId(List<String> configIds, Long parameterValueId)
Purpose: "Check if variations exist for config IDs and parameter value to validate configuration-based variation existence"

Business Logic Derivation:
  1. Used in ParameterExecutionService for configuration-based variation validation during variation creation and configuration management operations
  2. Provides configuration-based variation validation for parameter workflows enabling comprehensive config validation and variation functionality
  3. Critical for variation creation operations requiring configuration validation for variation management and config control
  4. Used in variation configuration workflows for validating config-based variations for variation operations and configuration processing
  5. Enables configuration-based variation management with comprehensive validation for variation creation and config control

SQL Query: |
  SELECT EXISTS(
    SELECT 1 FROM variations v
    WHERE v.parameter_values_id = ? AND v.config_id IN (?, ?, ?, ...)
  )

Parameters:
  - configIds: List<String> (Configuration identifiers for config-based validation)
  - parameterValueId: Long (Parameter value identifier for parameter-specific validation)

Returns: boolean (true if variations exist for config IDs and parameter value, false otherwise)
Transaction: Not Required
Error Handling: Returns false if no variations found for config IDs and parameter value
```

### Variation Lifecycle Methods

#### Method: reconfigureVariationsOfParameterValue(Long parameterValueId)
```yaml
Signature: void reconfigureVariationsOfParameterValue(Long parameterValueId)
Purpose: "Reconfigure parameter value variation status based on existing variations for variation lifecycle management"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionService for parameter value variation status reconfiguration during variation lifecycle management operations
  2. Provides parameter value variation status management for parameter workflows enabling comprehensive variation lifecycle and parameter functionality
  3. Critical for variation lifecycle operations requiring parameter value status updates for variation management and lifecycle control
  4. Used in variation management workflows for reconfiguring parameter value variation status for variation operations and lifecycle processing
  5. Enables variation lifecycle management with parameter value status updates for comprehensive variation processing and lifecycle control

SQL Query: |
  UPDATE parameter_values pv
  SET has_variations = (
    SELECT CASE
      WHEN COUNT(v.id) > 0 THEN true
      ELSE false
    END
    FROM variations v
    WHERE v.parameter_values_id = ?
  )
  WHERE pv.id = ?

Parameters:
  - parameterValueId: Long (Parameter value identifier for variation status reconfiguration)

Returns: void
Transaction: Required (@Modifying annotation)
Error Handling: No exception if parameter value not found
```

#### Method: deleteByVariationId(Long variationId)
```yaml
Signature: void deleteByVariationId(Long variationId)
Purpose: "Delete variation by variation ID for variation lifecycle management and variation cleanup"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionService for variation deletion during variation lifecycle management and cleanup operations
  2. Provides variation deletion functionality for variation workflows enabling comprehensive variation cleanup and lifecycle functionality
  3. Critical for variation lifecycle operations requiring variation deletion for variation management and cleanup control
  4. Used in variation management workflows for deleting variations for variation operations and lifecycle processing
  5. Enables variation lifecycle management with variation deletion for comprehensive variation processing and cleanup control

SQL Query: |
  DELETE FROM variations WHERE id = ?

Parameters:
  - variationId: Long (Variation identifier for variation deletion)

Returns: void
Transaction: Required (@Modifying and @Transactional annotations)
Error Handling: No exception if variation not found
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Variation Creation and Replication
```yaml
Usage: variationRepository.saveAll(variations)
Purpose: "Create variations in bulk for variation management and variation replication operations"

Business Logic Derivation:
  1. Used extensively in ParameterExecutionService and TaskExecutionService for bulk variation creation during variation management and replication operations
  2. Provides efficient bulk variation persistence for variation workflows enabling comprehensive variation creation and replication functionality
  3. Critical for variation management operations requiring bulk variation creation for variation processing and replication management
  4. Used in variation creation workflows for bulk variation persistence and variation replication operations
  5. Enables variation management with efficient bulk operations for comprehensive variation processing and replication control

Transaction: Required
Error Handling: DataIntegrityViolationException for bulk constraint violations, variation integrity issues
```

#### Pattern: getReferenceById() for Variation Entity Access
```yaml
Usage: variationRepository.getReferenceById(variationId)
Purpose: "Get variation reference for variation management and variation processing operations"

Business Logic Derivation:
  1. Used in JobAuditService and ParameterExecutionService for variation reference retrieval during variation processing and audit operations
  2. Provides efficient variation reference access for variation workflows enabling variation processing and audit functionality
  3. Critical for variation processing operations requiring variation entity access for variation management and processing control
  4. Used in variation processing workflows for accessing variation entities for variation operations and processing management
  5. Enables variation processing with efficient entity access for comprehensive variation management and processing control

Transaction: Not Required
Error Handling: EntityNotFoundException if variation not found
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Parameter Variation Lifecycle Management
```yaml
Usage: Complete variation lifecycle for parameter value variation management and processing
Purpose: "Manage parameter variations for comprehensive variation lifecycle functionality and parameter processing"

Business Logic Derivation:
  1. Parameter variation lifecycle management provides variation functionality through variation creation, validation, and processing management
  2. Variation lifecycle includes variation creation, type-based retrieval, validation operations, and cleanup workflows for parameter control
  3. Variation management operations require parameter variation processing for variation lifecycle and parameter control
  4. Parameter variation operations enable comprehensive variation functionality with lifecycle capabilities and processing management
  5. Variation lifecycle management supports parameter requirements and variation functionality for parameter variation processing

Common Usage Examples:
  - variationRepository.findAllByParameterValueIdAndType() in validation services for type-specific variation retrieval
  - variationRepository.saveAll() for bulk variation creation during parameter processing and variation setup
  - variationRepository.existsAllByVariationNumberOrNameForJob() for variation uniqueness validation during creation
  - variationRepository.reconfigureVariationsOfParameterValue() for parameter value status updates after variation changes
  - Comprehensive parameter variation management with lifecycle control and processing functionality

Transaction: Required for lifecycle operations and variation management
Error Handling: Parameter variation error handling and lifecycle validation verification
```

### Pattern: Job-Level Variation Reporting and Analysis
```yaml
Usage: Job-scoped variation reporting for variation analysis and job-level variation management
Purpose: "Manage job variations for comprehensive variation reporting and analysis functionality"

Business Logic Derivation:
  1. Job variation reporting operations require comprehensive variation access for job-level analysis and reporting functionality
  2. Variation reporting supports job requirements and analysis functionality for job processing workflows
  3. Job-level variation operations ensure proper variation analysis through reporting management and variation control
  4. Job workflows coordinate variation reporting with analysis processing for comprehensive job operations
  5. Variation analysis supports job requirements and reporting functionality for comprehensive job variation management

Common Usage Examples:
  - variationRepository.findAllByJobIdAndParameterName() for paginated job variation reporting and analysis
  - Job-level variation filtering for variation analysis and reporting operations
  - Parameter-specific variation reporting for job analysis and variation management functionality
  - Variation analysis operations for job reporting and variation tracking functionality
  - Comprehensive job variation reporting with analysis functionality and reporting control

Transaction: Not Required for reporting and analysis operations
Error Handling: Variation reporting error handling and analysis validation verification
```

### Pattern: Task Execution Variation Replication and Management
```yaml
Usage: Task execution variation copying for variation replication and task management functionality
Purpose: "Manage task execution variations for comprehensive variation replication and task functionality"

Business Logic Derivation:
  1. Task execution variation replication enables task functionality through variation copying and replication management
  2. Variation replication supports task execution requirements and replication functionality for task processing workflows
  3. Task execution variation operations ensure proper task functionality through variation management and replication control
  4. Task workflows coordinate variation replication with execution processing for comprehensive task operations
  5. Variation replication supports task requirements and execution functionality for comprehensive task variation management

Common Usage Examples:
  - variationRepository.findByTaskExecutionId() for task-specific variation retrieval during replication operations
  - Variation copying for task execution replication and variation management functionality
  - Task execution variation management for task processing and replication operations
  - Variation replication operations for task execution and variation management functionality
  - Comprehensive task execution variation replication with management functionality and replication control

Transaction: Required for replication operations and task execution management
Error Handling: Task execution operation error handling and variation replication validation
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllByJobIdAndParameterName, findVariationsByParameterValueId
  - findAllByParameterValueIdAndType, findByTaskExecutionId, existsAllByVariationNumberOrNameForJob
  - existsByConfigIdsForParameterValueId, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById, reconfigureVariationsOfParameterValue, deleteByVariationId

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (parameter_values_id, jobs_id, new_details, old_details, type, name, description, config_id)
    * Foreign key violations (invalid parameter_values_id, jobs_id references)
    * Unique constraint violations for variation combinations
    * Variation integrity constraint violations
  - EntityNotFoundException: Variation not found by ID or criteria
  - OptimisticLockException: Concurrent variation modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or variation context
  - ConstraintViolationException: Variation constraint violations

Validation Rules:
  - parameterValue: Required, must reference existing parameter value for variation context
  - parameterValueId: Derived from parameter value relationship, immutable for variation context integrity
  - job: Required, must reference existing job for job-specific variation context
  - jobId: Derived from job relationship, immutable for job context integrity
  - newDetails: Required, JSONB data for new variation state
  - oldDetails: Required, JSONB data for original variation state
  - type: Required, variation type enum (VALIDATION, SHOULD_BE)
  - name: Required, variation name for identification
  - description: Required, variation description for documentation
  - variationNumber: Optional, variation number for tracking
  - configId: Required, configuration identifier for variation management

Business Constraints:
  - Variations should be unique per job for variation number and name combinations for proper variation integrity
  - Parameter value and job references must be valid for variation integrity and parameter functionality
  - Variations must support parameter workflow requirements and variation functionality
  - Variation lifecycle management must maintain referential integrity and parameter workflow functionality consistency
  - Variation management must ensure proper parameter workflow control and variation functionality
  - Variation associations must support parameter requirements and variation functionality for parameter processing
  - Variation operations must maintain transaction consistency and constraint integrity for parameter management
  - Variation lifecycle management must maintain parameter functionality and variation consistency
  - Parameter management must maintain variation integrity and parameter workflow requirements
  - Processing operations must ensure proper parameter workflow management and variation control
```

## Variation Considerations

### Parameter Value Integration
```yaml
Parameter Integration: Variations enable parameter functionality through parameter value variation management and processing functionality
Parameter Management: Variation associations enable parameter functionality with comprehensive parameter variation capabilities
Parameter Lifecycle: Variation lifecycle includes creation, validation, and processing operations for parameter functionality
Parameter Management: Comprehensive parameter management for variation functionality and parameter requirements during parameter workflows
Processing Control: Variation processing control for parameter functionality and lifecycle management in parameter processing
```

### Job Context and Workflow Integration
```yaml
Job Context: Job-specific variation management for job-scoped parameter processing and variation functionality
Job Integration: Job variation integration with parameter processing and variation functionality for job workflows
Job Processing: Job-specific variation processing for parameter validation and job functionality with variation control
Workflow Integration: Parameter workflow integration for job context and variation functionality in parameter processing
Context Management: Job context management for parameter variation functionality and job-specific variation validation
```

### Variation Type and Configuration Management
```yaml
Type Management: Variation type management with VALIDATION and SHOULD_BE types for comprehensive variation functionality
Configuration Management: Variation configuration management with config ID tracking and configuration functionality
Type Control: Variation type control for variation processing and type management functionality
Configuration Integration: Configuration integration for variation management and configuration functionality with variation control
Type Processing: Variation type processing with configuration management and type functionality for variation workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Variation repository without JPA/Hibernate dependencies, focusing on process variation management and parameter value variation patterns.
