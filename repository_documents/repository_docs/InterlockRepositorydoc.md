# IInterlockRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Interlock
- **Primary Purpose**: Manages interlock entities for safety validation and conditional workflow control with JSON-based validation rules and target entity associations
- **Key Relationships**: Safety control entity linking to various target entities (primarily tasks) with complex validation logic for workflow safety and business rule enforcement
- **Performance Characteristics**: Moderate query volume with task-based interlock operations, parameter validation checks, and property usage validation
- **Business Context**: Safety and validation component that enforces safety interlocks, conditional workflow execution, parameter validation rules, and property usage validation for workflow safety and compliance

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| target_entity_type | targetEntityType | Type.InterlockTargetEntityType | false | null |
| target_entity_id | targetEntityId | Long | true | null |
| validations | validations | JsonNode | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Relationships

None - This entity uses foreign key references without JPA relationship mappings for flexible target entity association.

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Interlock entity)`
- `deleteById(Long id)`
- `delete(Interlock entity)`
- `existsById(Long id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<Interlock> spec)`
- `findAll(Specification<Interlock> spec, Pageable pageable)`
- `findAll(Specification<Interlock> spec, Sort sort)`
- `findOne(Specification<Interlock> spec)`
- `count(Specification<Interlock> spec)`

### Custom Query Methods (6 methods - ALL methods documented)

- `findFirstByTargetEntityTypeAndTargetEntityId(Type.InterlockTargetEntityType targetEntityType, Long targetEntityId)`
- `findByTargetEntityTypeAndTargetEntityId(Type.InterlockTargetEntityType targetEntityType, Long targetEntityId)`
- `findFirstByTargetEntityType(Type.InterlockTargetEntityType targetEntityType)`
- `getAllInterlockConditionsWhereObjectTypePropertyIsUsed(String propertyId)`
- `getChecklistAndTaskInfoByInterlockId(Long interlockId)`
- `getAllParameterWhereParameterIdUsedInInterlocks(String parameterId, Long checklistsId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Interlock> findById(Long id)
List<Interlock> findAll()
Interlock save(Interlock entity)
void deleteById(Long id)
void delete(Interlock entity)
boolean existsById(Long id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<Interlock> findAll(Specification<Interlock> spec)
Page<Interlock> findAll(Specification<Interlock> spec, Pageable pageable)
List<Interlock> findAll(Specification<Interlock> spec, Sort sort)
Optional<Interlock> findOne(Specification<Interlock> spec)
long count(Specification<Interlock> spec)
```

### Custom Query Methods

#### Method: findFirstByTargetEntityTypeAndTargetEntityId(Type.InterlockTargetEntityType targetEntityType, Long targetEntityId)
```yaml
Signature: Optional<Interlock> findFirstByTargetEntityTypeAndTargetEntityId(Type.InterlockTargetEntityType targetEntityType, Long targetEntityId)
Purpose: "Find interlock for specific target entity for interlock management and safety validation operations"

Business Logic Derivation:
  1. Used extensively in InterlockService for task-based interlock retrieval and interlock management operations
  2. Provides primary interlock lookup for target entities (primarily tasks) for safety validation and workflow control
  3. Critical for interlock validation operations requiring specific entity interlock configuration and safety rule enforcement
  4. Used in interlock creation, update, and deletion workflows for entity-specific safety management
  5. Enables entity-specific safety control with interlock configuration retrieval for workflow safety enforcement

SQL Query: |
  SELECT i.* FROM interlocks i
  WHERE i.target_entity_type = ?
    AND i.target_entity_id = ?
  LIMIT 1

Parameters:
  - targetEntityType: Type.InterlockTargetEntityType (Target entity type, typically TASK)
  - targetEntityId: Long (Target entity identifier for interlock lookup)

Returns: Optional<Interlock> (interlock for the specific target entity)
Transaction: Not Required
Error Handling: Returns empty Optional if no interlock found for target entity
```

#### Method: findByTargetEntityTypeAndTargetEntityId(Type.InterlockTargetEntityType targetEntityType, Long targetEntityId)
```yaml
Signature: List<Interlock> findByTargetEntityTypeAndTargetEntityId(Type.InterlockTargetEntityType targetEntityType, Long targetEntityId)
Purpose: "Find all interlocks for specific target entity for comprehensive interlock validation and management"

Business Logic Derivation:
  1. Used in InterlockService for comprehensive interlock retrieval when multiple interlocks may exist for a target entity
  2. Provides complete interlock listing for target entities requiring multiple safety validation rules
  3. Critical for interlock validation operations requiring all safety rules for a specific entity
  4. Used in complex safety validation workflows requiring multiple interlock conditions and validation rules
  5. Enables comprehensive safety control with complete interlock configuration for complex workflow safety requirements

SQL Query: |
  SELECT i.* FROM interlocks i
  WHERE i.target_entity_type = ?
    AND i.target_entity_id = ?
  ORDER BY i.created_at DESC

Parameters:
  - targetEntityType: Type.InterlockTargetEntityType (Target entity type for comprehensive lookup)
  - targetEntityId: Long (Target entity identifier for complete interlock retrieval)

Returns: List<Interlock> (all interlocks for the specific target entity)
Transaction: Not Required
Error Handling: Returns empty list if no interlocks found for target entity
```

#### Method: findFirstByTargetEntityType(Type.InterlockTargetEntityType targetEntityType)
```yaml
Signature: Optional<Interlock> findFirstByTargetEntityType(Type.InterlockTargetEntityType targetEntityType)
Purpose: "Find first interlock by target entity type for interlock template and configuration operations"

Business Logic Derivation:
  1. Used in InterlockService for interlock template retrieval and interlock configuration discovery
  2. Provides sample interlock configuration for target entity types for interlock template operations
  3. Critical for interlock configuration operations requiring template or example interlock structure
  4. Used in interlock setup workflows requiring reference interlock configuration for new entity creation
  5. Enables interlock template management with sample configuration retrieval for interlock setup and configuration

SQL Query: |
  SELECT i.* FROM interlocks i
  WHERE i.target_entity_type = ?
  LIMIT 1

Parameters:
  - targetEntityType: Type.InterlockTargetEntityType (Target entity type for template lookup)

Returns: Optional<Interlock> (first interlock matching the target entity type)
Transaction: Not Required
Error Handling: Returns empty Optional if no interlocks found for target entity type
```

#### Method: getAllInterlockConditionsWhereObjectTypePropertyIsUsed(String propertyId)
```yaml
Signature: List<IdView> getAllInterlockConditionsWhereObjectTypePropertyIsUsed(String propertyId)
Purpose: "Get interlock IDs using specific object type property for property usage validation and dependency management"

Business Logic Derivation:
  1. Used in ObjectTypeService for property usage validation during object type property deletion and modification operations
  2. Identifies interlock conditions that depend on specific object type properties for dependency validation
  3. Critical for property lifecycle management ensuring property deletion doesn't break interlock validation rules
  4. Used in property validation workflows to prevent deletion of properties used in active interlock conditions
  5. Enables property dependency management with interlock usage validation for data integrity and safety rule preservation

SQL Query: |
  SELECT i.id FROM interlocks i
  WHERE i.validations::text LIKE '%' || ? || '%'

Parameters:
  - propertyId: String (Object type property identifier to check usage for)

Returns: List<IdView> (interlock IDs that use the specified property)
Transaction: Not Required
Error Handling: Returns empty list if no interlocks use the specified property
```

#### Method: getChecklistAndTaskInfoByInterlockId(Long interlockId)
```yaml
Signature: ObjectPropertyRelationChecklistView getChecklistAndTaskInfoByInterlockId(Long interlockId)
Purpose: "Get checklist and task context information for interlock validation and relationship management"

Business Logic Derivation:
  1. Used in ParameterValidationService and ObjectTypeService for interlock context retrieval during validation operations
  2. Provides checklist and task context for interlock validation rules and interlock relationship management
  3. Critical for validation operations requiring complete context information for interlock rule evaluation
  4. Used in parameter and property validation workflows requiring interlock context for dependency validation
  5. Enables interlock context management with comprehensive relationship information for validation rule enforcement

SQL Query: |
  SELECT i.id as interlockId, t.id as taskId, t.name as taskName,
         c.id as checklistId, c.name as checklistName, c.code as checklistCode
  FROM interlocks i
  INNER JOIN tasks t ON i.target_entity_id = t.id AND i.target_entity_type = 'TASK'
  INNER JOIN stages s ON t.stages_id = s.id
  INNER JOIN checklists c ON s.checklists_id = c.id
  WHERE i.id = ?

Parameters:
  - interlockId: Long (Interlock identifier to get context information for)

Returns: ObjectPropertyRelationChecklistView (interlock context information projection view)
Transaction: Not Required
Error Handling: Returns null if interlock not found or no context available
```

#### Method: getAllParameterWhereParameterIdUsedInInterlocks(String parameterId, Long checklistsId)
```yaml
Signature: List<IdView> getAllParameterWhereParameterIdUsedInInterlocks(String parameterId, Long checklistsId)
Purpose: "Get interlock IDs using specific parameter for parameter usage validation and dependency management"

Business Logic Derivation:
  1. Used in ParameterValidationService for parameter usage validation during parameter deletion and modification operations
  2. Identifies interlock conditions that depend on specific parameters within checklist scope for dependency validation
  3. Critical for parameter lifecycle management ensuring parameter deletion doesn't break interlock validation rules
  4. Used in parameter validation workflows to prevent deletion of parameters used in active interlock conditions
  5. Enables parameter dependency management with interlock usage validation for data integrity and safety rule preservation

SQL Query: |
  SELECT i.id FROM interlocks i
  INNER JOIN tasks t ON i.target_entity_id = t.id AND i.target_entity_type = 'TASK'
  INNER JOIN stages s ON t.stages_id = s.id
  WHERE s.checklists_id = ?
    AND i.validations::text LIKE '%' || ? || '%'

Parameters:
  - parameterId: String (Parameter identifier to check usage for)
  - checklistsId: Long (Checklist identifier for scoping parameter usage validation)

Returns: List<IdView> (interlock IDs that use the specified parameter within checklist scope)
Transaction: Not Required
Error Handling: Returns empty list if no interlocks use the specified parameter in checklist
```

### Key Repository Usage Patterns

#### Pattern: save() for Interlock Lifecycle Management
```yaml
Usage: interlockRepository.save(interlock)
Purpose: "Create new interlocks, update interlock validation rules, and manage interlock lifecycle"

Business Logic Derivation:
  1. Used extensively for interlock creation and updates with target entity association and validation rule management
  2. Handles interlock validation rule updates including JSON configuration and safety rule modifications
  3. Updates interlock lifecycle information for safety management and workflow control
  4. Critical for interlock lifecycle management and safety rule enforcement in workflow operations
  5. Supports interlock operations with comprehensive validation rule management and safety control

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: Task-Based Interlock Operations
```yaml
Usage: findFirstByTargetEntityTypeAndTargetEntityId(TASK, taskId)
Purpose: "Manage task-specific interlocks for workflow safety and conditional execution control"

Business Logic Derivation:
  1. Task-based interlocks are the primary use case for safety validation and workflow control
  2. Task interlock management enables conditional task execution based on safety validation rules
  3. Task safety validation ensures workflow safety and compliance with business safety requirements
  4. Task interlock configuration provides flexible safety rule definition and enforcement
  5. Task-interlock relationship enables comprehensive workflow safety management and control

Transaction: Not Required for lookup, Required for modifications
Error Handling: ResourceNotFoundException for missing interlocks, validation errors for rule enforcement
```

#### Pattern: Dependency Validation Operations
```yaml
Usage: getAllInterlockConditionsWhereObjectTypePropertyIsUsed() and getAllParameterWhereParameterIdUsedInInterlocks()
Purpose: "Validate dependencies before deleting parameters or properties to prevent breaking interlock rules"

Business Logic Derivation:
  1. Dependency validation prevents deletion of parameters or properties used in active interlock conditions
  2. Interlock dependency checking ensures data integrity and safety rule preservation during entity lifecycle operations
  3. Parameter and property usage validation maintains interlock rule consistency and prevents validation rule breakage
  4. Dependency management enables safe entity deletion with comprehensive impact analysis for interlock rules
  5. Usage validation supports data integrity with comprehensive dependency checking for safety rule preservation

Transaction: Not Required
Error Handling: Returns comprehensive usage information for dependency impact analysis
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findFirstByTargetEntityTypeAndTargetEntityId, findByTargetEntityTypeAndTargetEntityId
  - findFirstByTargetEntityType, getAllInterlockConditionsWhereObjectTypePropertyIsUsed
  - getChecklistAndTaskInfoByInterlockId, getAllParameterWhereParameterIdUsedInInterlocks
  - findAll(Specification), existsById, count, findOne(Specification), count(Specification)

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
    * NOT NULL constraint violations (targetEntityType, validations)
    * Invalid enum values for targetEntityType field
    * Invalid JSON format in validations field
    * Foreign key constraint violations for targetEntityId references
  - EntityNotFoundException: Interlock not found by ID or criteria
  - OptimisticLockException: Concurrent interlock modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - ResourceNotFoundException: Interlock not found during operations
  - JsonProcessingException: Invalid JSON in validations field

Validation Rules:
  - targetEntityType: Required, must be valid InterlockTargetEntityType enum value (typically TASK)
  - targetEntityId: Optional, must reference valid target entity when specified
  - validations: Required, must be valid JSON containing interlock validation rules and conditions

Business Constraints:
  - Interlock validation rules must be valid JSON for proper validation rule processing
  - Target entity references must be valid when specified for interlock association
  - Interlock deletion requires validation of no active dependencies or safety rule usage
  - Validation rule changes must maintain safety compliance and workflow integrity
  - Parameter and property usage in interlock rules must be validated before entity deletion
  - Interlock conditions must be evaluable and consistent for safety rule enforcement
  - Target entity type must match actual target entity for proper interlock association
  - JSON validation rules must follow defined schema for consistency and rule evaluation
  - Interlock modifications must maintain safety compliance and workflow safety requirements
  - Dependency validation must prevent breaking active interlock conditions and safety rules
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Interlock repository without JPA/Hibernate dependencies.
