# IParameterRuleMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ParameterRuleMapping
- **Primary Purpose**: Manages parameter rule mapping entities for parameter rule associations with triggering parameter management, impacted parameter tracking, and rule-based parameter behavior control
- **Key Relationships**: Mapping entity linking ParameterRule and Parameter entities with many-to-one relationships using composite key for comprehensive parameter rule management and rule-based behavior control
- **Performance Characteristics**: Moderate query volume with rule-based parameter discovery, bulk rule operations, and parameter rule lifecycle management
- **Business Context**: Parameter rule management component that provides rule-based parameter behavior, triggering parameter associations, impacted parameter tracking, and parameter rule lifecycle control for dynamic parameter management and rule-based workflow automation

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| parameter_rules_id | parameterRuleMappingCompositeKey.parameterRuleId / parameterRule.id | Long | false | part of composite key |
| impacted_parameters_id | parameterRuleMappingCompositeKey.impactedParameterId / impactedParameter.id | Long | false | part of composite key |
| triggering_parameters_id | parameterRuleMappingCompositeKey.triggeringParameterId / triggeringParameter.id | Long | false | part of composite key |

### Composite Key Structure
- **ParameterRuleMappingCompositeKey**: Triple composite key containing parameterRuleId, impactedParameterId, and triggeringParameterId for unique parameter rule associations

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | parameterRule | ParameterRule | LAZY | Associated parameter rule, @MapsId |
| @ManyToOne | impactedParameter | Parameter | LAZY | Impacted parameter, @MapsId |
| @ManyToOne | triggeringParameter | Parameter | LAZY | Triggering parameter, @MapsId |

## Available Repository Methods

### Standard CRUD Methods
- `findById(ParameterRuleMappingCompositeKey id)`
- `findAll()`
- `save(ParameterRuleMapping entity)`
- `saveAll(Iterable<ParameterRuleMapping> entities)`
- `deleteById(ParameterRuleMappingCompositeKey id)`
- `delete(ParameterRuleMapping entity)`
- `existsById(ParameterRuleMappingCompositeKey id)`
- `count()`

### Custom Query Methods (3 methods - ALL methods documented)

- `deleteAllByTriggeringParameterId(Long triggeringParameterId)`
- `findAllByImpactedParameterId(Long impactedParameterId)`
- `findAllByImpactedParameterIdIn(Set<Long> impactedParameterIds)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods with composite key
Optional<ParameterRuleMapping> findById(ParameterRuleMappingCompositeKey id)
List<ParameterRuleMapping> findAll()
ParameterRuleMapping save(ParameterRuleMapping entity)
List<ParameterRuleMapping> saveAll(Iterable<ParameterRuleMapping> entities)
void deleteById(ParameterRuleMappingCompositeKey id)
void delete(ParameterRuleMapping entity)
boolean existsById(ParameterRuleMappingCompositeKey id)
long count()
```

### Custom Query Methods

#### Method: deleteAllByTriggeringParameterId(Long triggeringParameterId)
```yaml
Signature: void deleteAllByTriggeringParameterId(Long triggeringParameterId)
Purpose: "Delete all parameter rule mappings for specific triggering parameter for rule cleanup and parameter rule lifecycle management"

Business Logic Derivation:
  1. Used in ChecklistCollaboratorService and Rules migration for bulk deletion of parameter rule mappings during rule management operations
  2. Provides efficient bulk cleanup of parameter rule mappings when triggering parameters are being updated or rules are being modified
  3. Critical for parameter rule lifecycle management requiring removal of all rule associations for specific triggering parameters
  4. Used in rule management workflows for cleaning up existing rule mappings before creating new rule associations
  5. Enables parameter rule lifecycle management with bulk cleanup operations for comprehensive rule management control

SQL Query: |
  DELETE FROM parameter_rule_mapping 
  WHERE triggering_parameters_id = ?

Parameters:
  - triggeringParameterId: Long (Triggering parameter identifier for rule mapping cleanup)

Returns: void
Transaction: Required (@Transactional annotation)
Error Handling: No exception if no matching records found for triggering parameter
```

#### Method: findAllByImpactedParameterId(Long impactedParameterId)
```yaml
Signature: Set<Long> findAllByImpactedParameterId(Long impactedParameterId)
Purpose: "Find all triggering parameter IDs for specific impacted parameter for rule application and parameter behavior management"

Business Logic Derivation:
  1. Used in TaskExecutionService for finding triggering parameters during rule application and parameter behavior management operations
  2. Provides triggering parameter discovery for rule-based parameter behavior and rule application workflows
  3. Critical for rule application operations requiring triggering parameter identification for specific impacted parameters
  4. Used in parameter processing workflows for identifying which parameters trigger rules for specific impacted parameters
  5. Enables rule-based parameter behavior management with triggering parameter discovery for comprehensive rule processing

SQL Query: |
  SELECT DISTINCT prm.triggering_parameters_id as id
  FROM parameter_rule_mapping prm
  WHERE prm.impacted_parameters_id = ?

Parameters:
  - impactedParameterId: Long (Impacted parameter identifier to find triggering parameters for)

Returns: Set<Long> (set of triggering parameter IDs for the impacted parameter)
Transaction: Not Required
Error Handling: Returns empty set if no triggering parameters found for impacted parameter
```

#### Method: findAllByImpactedParameterIdIn(Set<Long> impactedParameterIds)
```yaml
Signature: Set<Parameter> findAllByImpactedParameterIdIn(Set<Long> impactedParameterIds)
Purpose: "Find all triggering parameters for multiple impacted parameters for bulk rule processing and parameter management"

Business Logic Derivation:
  1. Used in TaskExecutionService for bulk retrieval of triggering parameters during rule processing and parameter management operations
  2. Provides efficient bulk parameter discovery for rule-based processing and parameter behavior management workflows
  3. Critical for bulk rule processing operations requiring triggering parameter identification for multiple impacted parameters
  4. Used in parameter processing workflows for efficient bulk rule application and parameter behavior management
  5. Enables bulk rule processing with efficient triggering parameter discovery for comprehensive parameter rule management

SQL Query: |
  SELECT p FROM ParameterRuleMapping prm 
  INNER JOIN Parameter p ON p.id = prm.triggeringParameter.id 
  WHERE prm.impactedParameter.id IN (?, ?, ?, ...)

Parameters:
  - impactedParameterIds: Set<Long> (Set of impacted parameter identifiers for bulk triggering parameter discovery)

Returns: Set<Parameter> (set of triggering parameters for all specified impacted parameters)
Transaction: Not Required
Error Handling: Returns empty set if no triggering parameters found for any impacted parameters
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Bulk Parameter Rule Management
```yaml
Usage: parameterRuleMappingRepository.saveAll(mappings)
Purpose: "Create parameter rule mappings in bulk for rule management and parameter behavior configuration"

Business Logic Derivation:
  1. Used extensively in ChecklistCollaboratorService and Rules migration for bulk parameter rule mapping creation during rule configuration
  2. Provides efficient bulk rule mapping persistence for operations creating multiple rule associations simultaneously
  3. Critical for rule management operations requiring bulk rule mapping creation for parameter behavior configuration
  4. Used in rule configuration workflows for bulk mapping creation and parameter rule association operations
  5. Enables efficient bulk parameter rule operations with transaction consistency for comprehensive rule management

Transaction: Required
Error Handling: DataIntegrityViolationException for bulk constraint violations, composite key conflicts
```

#### Pattern: Rule-Based Parameter Discovery and Processing
```yaml
Usage: Multiple methods for discovering triggering parameters and processing rule-based behavior
Purpose: "Discover triggering parameters for rule application and parameter behavior management"

Business Logic Derivation:
  1. Rule-based parameter discovery enables parameter behavior management through triggering parameter identification
  2. Parameter rule processing requires efficient discovery of which parameters trigger rules for specific impacted parameters
  3. Bulk rule processing enables efficient parameter behavior management for multiple parameters simultaneously
  4. Rule application workflows depend on triggering parameter discovery for proper parameter behavior control
  5. Parameter rule management requires comprehensive triggering parameter discovery for rule-based workflow automation

Transaction: Not Required for discovery operations
Error Handling: Empty result handling for parameters without rule associations
```

#### Pattern: Parameter Rule Lifecycle Management
```yaml
Usage: Parameter rule mapping lifecycle management for rule configuration and behavior control
Purpose: "Manage parameter rule mapping lifecycle for comprehensive rule configuration and parameter behavior control"

Business Logic Derivation:
  1. Parameter rule mappings enable rule-based parameter behavior through triggering parameter associations and rule management
  2. Rule mapping lifecycle management supports parameter rule requirements and rule-based parameter behavior functionality
  3. Parameter rule mapping lifecycle includes creation, association management, and cleanup operations for rule control
  4. Rule management operations enable comprehensive parameter rule functionality with behavior control capabilities
  5. Rule mapping lifecycle control supports parameter rule operations and rule-based parameter management requirements

Transaction: Required for lifecycle operations and rule management
Error Handling: Composite key validation and rule association integrity verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Rule Configuration and Parameter Behavior Management
```yaml
Usage: Complete parameter rule mapping lifecycle for rule-based parameter behavior
Purpose: "Manage parameter rule mappings for comprehensive rule configuration and parameter behavior control"

Business Logic Derivation:
  1. Parameter rule mappings provide rule-based parameter behavior through triggering parameter associations and rule management
  2. Rule configuration lifecycle includes creation, association management, update operations, and cleanup workflows
  3. Parameter rule update operations require complete cleanup of existing rule mappings followed by new association creation
  4. Rule deletion operations require comprehensive cleanup of all associated rule mappings for data integrity
  5. Rule mapping lifecycle management supports parameter rule requirements and rule-based parameter behavior functionality

Common Usage Examples:
  - parameterRuleMappingRepository.deleteAllByTriggeringParameterId() for rule mapping cleanup during rule updates
  - parameterRuleMappingRepository.saveAll() for bulk rule mapping creation during rule configuration operations
  - Rule mapping lifecycle management for rule configuration and parameter behavior control operations
  - Parameter rule association management for rule-based parameter behavior and workflow automation
  - Comprehensive parameter rule management with lifecycle control and rule association management

Transaction: Required for lifecycle operations and bulk rule management
Error Handling: Bulk operation error handling and rule association integrity verification
```

### Pattern: Rule Application and Parameter Processing
```yaml
Usage: Rule application workflows with triggering parameter discovery and rule processing
Purpose: "Process parameter rules with triggering parameter discovery for rule-based parameter behavior"

Business Logic Derivation:
  1. Rule application workflows require triggering parameter discovery for proper rule processing and parameter behavior management
  2. Parameter processing operations involve triggering parameter identification for rule-based parameter behavior control
  3. Rule-based parameter processing ensures proper parameter behavior through rule application and triggering parameter management
  4. Processing workflows coordinate triggering parameter discovery with rule application for comprehensive parameter management
  5. Parameter rule processing supports parameter behavior requirements and rule-based workflow automation

Common Processing Patterns:
  - Triggering parameter discovery for rule application and parameter behavior management operations
  - Bulk triggering parameter retrieval for efficient rule processing and parameter behavior control
  - Parameter rule processing workflows with triggering parameter identification and rule application
  - Rule-based parameter behavior management through triggering parameter discovery and processing operations
  - Comprehensive parameter processing with rule application and parameter behavior control

Transaction: Not Required for discovery and processing operations
Error Handling: Processing operation error handling and parameter rule validation
```

### Pattern: Migration and Rule Management Operations
```yaml
Usage: Migration workflows with parameter rule management and rule configuration operations
Purpose: "Manage parameter rules during migration and rule configuration for parameter behavior control"

Business Logic Derivation:
  1. Migration operations require parameter rule management for rule configuration and parameter behavior setup
  2. Rule management workflows involve rule mapping cleanup and new rule association creation for parameter behavior control
  3. Parameter rule migration ensures proper rule configuration and parameter behavior management during system updates
  4. Migration workflows coordinate rule mapping lifecycle with parameter behavior configuration for comprehensive rule management
  5. Rule configuration management supports migration requirements and parameter behavior control functionality

Common Migration Patterns:
  - Complete cleanup of parameter rule mappings during migration and rule configuration operations
  - Bulk rule mapping creation for parameter behavior configuration and rule management during migration
  - Parameter rule lifecycle management with migration support and rule configuration control
  - Rule mapping migration workflows with parameter behavior configuration and rule association management
  - Comprehensive migration with parameter rule management and rule-based behavior configuration

Transaction: Required for migration workflows and rule configuration operations
Error Handling: Migration operation error handling and rule configuration validation
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllByImpactedParameterId, findAllByImpactedParameterIdIn
  - existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById, deleteAllByTriggeringParameterId

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Composite key constraint violations (duplicate parameter rule-parameter combinations)
    * NOT NULL constraint violations (parameter_rules_id, impacted_parameters_id, triggering_parameters_id)
    * Foreign key violations (invalid parameter_rules_id, impacted_parameters_id, triggering_parameters_id references)
    * Unique constraint violations on composite key
  - EntityNotFoundException: Parameter rule mapping not found by composite key or criteria
  - OptimisticLockException: Concurrent parameter rule mapping modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or composite key operations
  - ConstraintViolationException: Parameter rule mapping constraint violations

Validation Rules:
  - parameterRuleMappingCompositeKey: Required, composite key containing parameterRuleId, impactedParameterId, and triggeringParameterId
  - parameterRule: Required, must reference existing parameter rule, @MapsId annotation
  - impactedParameter: Required, must reference existing parameter for rule impact tracking
  - triggeringParameter: Required, must reference existing parameter for rule triggering behavior

Business Constraints:
  - Parameter rule mappings must be unique for proper rule association management and parameter behavior integrity
  - Parameter rule, impacted parameter, and triggering parameter references must be valid for rule integrity and parameter behavior
  - Parameter rule mappings must support rule-based parameter behavior requirements and rule management functionality
  - Rule mapping lifecycle management must maintain referential integrity and parameter rule functionality consistency
  - Rule mapping cleanup must ensure proper rule configuration and parameter behavior management
  - Parameter rule associations must support rule requirements and parameter behavior functionality
  - Bulk operations must maintain transaction consistency and constraint integrity for rule management
  - Rule mapping lifecycle management must maintain parameter rule functionality and rule association consistency
  - Rule association management must maintain parameter rule integrity and parameter behavior requirements
  - Cleanup operations must ensure proper rule lifecycle management and parameter behavior control
```

## Parameter Rule Mapping Considerations

### Rule-Based Parameter Behavior
```yaml
Triggering Parameters: Parameter rule mappings enable rule-based behavior through triggering parameter associations
Impacted Parameters: Impacted parameter tracking for rule application and parameter behavior management
Rule Associations: Parameter rule associations for rule-based parameter behavior and workflow automation
Behavior Control: Comprehensive parameter behavior control through rule-based associations and management
Rule Processing: Rule processing capabilities through triggering parameter discovery and rule application
```

### Rule Management Integration
```yaml
Rule Configuration: Parameter rule mapping configuration for rule-based parameter behavior management
Rule Lifecycle: Rule mapping lifecycle includes creation, association management, and cleanup operations
Bulk Operations: Efficient bulk operations for rule management and parameter behavior configuration
Rule Cleanup: Comprehensive rule mapping cleanup for rule lifecycle and parameter behavior management
Rule Association: Rule association management for parameter behavior and rule-based functionality
```

### Parameter Processing Integration
```yaml
Rule Application: Parameter rule application workflows with triggering parameter discovery and processing
Parameter Discovery: Triggering parameter discovery for rule processing and parameter behavior management
Bulk Processing: Efficient bulk parameter processing for rule application and behavior management
Rule Processing: Rule-based parameter processing for parameter behavior control and workflow automation
Behavior Management: Comprehensive parameter behavior management through rule application and processing
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the ParameterRuleMapping repository without JPA/Hibernate dependencies, focusing on rule-based parameter behavior management and parameter rule association patterns.
