# ITaskAutomationMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: TaskAutomationMapping
- **Primary Purpose**: Manages task automation mapping entities for task-automation associations with trigger type management, automation ordering, and workflow automation control
- **Key Relationships**: Mapping entity linking Task and Automation with many-to-one relationships using composite key for comprehensive task automation management and workflow automation control
- **Performance Characteristics**: Moderate query volume with task-based automation discovery, automation existence validation, and automation lifecycle management
- **Business Context**: Task automation management component that provides task-scoped automation associations, trigger type management, automation ordering control, and workflow automation functionality for automated task processing and workflow automation capabilities

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| tasks_id | taskAutomationId.taskId / taskId | Long | false | part of composite key |
| automations_id | taskAutomationId.automationId / automationId | Long | false | part of composite key |
| order_tree | orderTree | Integer | false | null |
| display_name | displayName | String | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Composite Key Structure
- **TaskAutomationCompositeKey**: Composite key containing taskId and automationId for unique task automation associations

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | task | Task | LAZY | Associated task, not null, immutable |
| @ManyToOne | automation | Automation | LAZY | Associated automation, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(TaskAutomationCompositeKey id)`
- `findAll()`
- `save(TaskAutomationMapping entity)`
- `saveAll(Iterable<TaskAutomationMapping> entities)`
- `deleteById(TaskAutomationCompositeKey id)`
- `delete(TaskAutomationMapping entity)`
- `existsById(TaskAutomationCompositeKey id)`
- `count()`

### Custom Query Methods (7 methods - ALL methods documented)

- `deleteByTaskIdAndAutomationId(Long taskId, Long automationId)`
- `findAllAutomationsByTaskIdAndTriggerType(Long taskId, Type.AutomationTriggerType triggerType)`
- `automationExistsByTaskIdAndTriggerTypeAndAutomationActionTypes(Long taskId, Type.AutomationTriggerType triggerType, List<Type.AutomationActionType> actionTypes)`
- `findByTaskIdAndAutomationId(Long taskId, Long automationId)`
- `findByAutomationId(Long automationId)`
- `getChecklistAndTaskInfoByAutomationId(Long automationId)`
- `findTaskAutomationMappingByAutomationIdIn(List<Long> automationIds)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods with composite key
Optional<TaskAutomationMapping> findById(TaskAutomationCompositeKey id)
List<TaskAutomationMapping> findAll()
TaskAutomationMapping save(TaskAutomationMapping entity)
List<TaskAutomationMapping> saveAll(Iterable<TaskAutomationMapping> entities)
void deleteById(TaskAutomationCompositeKey id)
void delete(TaskAutomationMapping entity)
boolean existsById(TaskAutomationCompositeKey id)
long count()
```

### Custom Query Methods

#### Method: deleteByTaskIdAndAutomationId(Long taskId, Long automationId)
```yaml
Signature: void deleteByTaskIdAndAutomationId(Long taskId, Long automationId)
Purpose: "Delete specific task automation mapping for automation lifecycle management and task automation cleanup"

Business Logic Derivation:
  1. Used in TaskAutomationService for removing specific task automation mappings during automation management and lifecycle operations
  2. Provides precise automation mapping deletion for automation lifecycle management and task automation cleanup operations
  3. Critical for automation management operations requiring specific mapping removal for automation lifecycle and task management
  4. Used in automation management workflows for cleaning up specific automation associations and mapping lifecycle control
  5. Enables automation lifecycle management with precise mapping deletion for comprehensive automation management and cleanup

SQL Query: |
  DELETE FROM task_automation_mapping 
  WHERE tasks_id = ? AND automations_id = ?

Parameters:
  - taskId: Long (Task identifier for specific automation mapping deletion)
  - automationId: Long (Automation identifier for specific mapping deletion)

Returns: void
Transaction: Required (@Transactional and @Modifying annotations)
Error Handling: No exception if no matching records found for task and automation combination
```

#### Method: findAllAutomationsByTaskIdAndTriggerType(Long taskId, Type.AutomationTriggerType triggerType)
```yaml
Signature: List<Automation> findAllAutomationsByTaskIdAndTriggerType(Long taskId, Type.AutomationTriggerType triggerType)
Purpose: "Find all automations for specific task and trigger type for automation execution and workflow processing"

Business Logic Derivation:
  1. Used in TaskAutomationService for retrieving automations by task and trigger type during automation execution and workflow processing
  2. Provides trigger-specific automation discovery for task automation execution and workflow automation functionality
  3. Critical for automation execution operations requiring trigger-specific automation identification for task processing workflows
  4. Used in task automation processing workflows for executing automations based on trigger types and task automation requirements
  5. Enables task automation execution with trigger-specific automation discovery for comprehensive workflow automation processing

SQL Query: |
  SELECT a FROM Automation a
  INNER JOIN TaskAutomationMapping tam ON tam.automationId = a.id
  WHERE a.triggerType = ? AND tam.taskId = ?
  ORDER BY tam.orderTree

Parameters:
  - taskId: Long (Task identifier to find automations for)
  - triggerType: Type.AutomationTriggerType (Trigger type for automation filtering)

Returns: List<Automation> (automations for task with specified trigger type, ordered by order tree)
Transaction: Not Required
Error Handling: Returns empty list if no automations found for task and trigger type
```

#### Method: automationExistsByTaskIdAndTriggerTypeAndAutomationActionTypes(Long taskId, Type.AutomationTriggerType triggerType, List<Type.AutomationActionType> actionTypes)
```yaml
Signature: Boolean automationExistsByTaskIdAndTriggerTypeAndAutomationActionTypes(Long taskId, Type.AutomationTriggerType triggerType, List<Type.AutomationActionType> actionTypes)
Purpose: "Check if automation exists for task with specific trigger and action types for validation and automation management"

Business Logic Derivation:
  1. Used in TaskAutomationService for validating automation existence during automation management and validation operations
  2. Provides automation existence validation for task automation management and workflow automation validation workflows
  3. Critical for automation validation operations requiring existence checks for specific automation types and task automation requirements
  4. Used in automation management workflows for preventing duplicate automation creation and automation validation requirements
  5. Enables automation validation with existence checks for comprehensive automation management and duplicate prevention

SQL Query: |
  SELECT CASE WHEN COUNT(a.id) > 0 THEN true ELSE false END
  FROM TaskAutomationMapping tam 
  INNER JOIN tam.automation a 
  WHERE tam.taskId = ? AND a.triggerType = ? AND a.actionType IN (?, ?, ?, ...)

Parameters:
  - taskId: Long (Task identifier for automation existence check)
  - triggerType: Type.AutomationTriggerType (Trigger type for automation filtering)
  - actionTypes: List<Type.AutomationActionType> (Action types for automation validation)

Returns: Boolean (true if automation exists, false otherwise)
Transaction: Not Required
Error Handling: Returns false if no automations found matching criteria
```

#### Method: findByTaskIdAndAutomationId(Long taskId, Long automationId)
```yaml
Signature: TaskAutomationMapping findByTaskIdAndAutomationId(Long taskId, Long automationId)
Purpose: "Find specific task automation mapping for automation management and mapping access operations"

Business Logic Derivation:
  1. Used in TaskAutomationService for retrieving specific task automation mappings during automation management and mapping operations
  2. Provides precise mapping access for automation management operations enabling automation mapping management and lifecycle control
  3. Critical for automation management operations requiring specific mapping access for automation processing and management workflows
  4. Used in automation management workflows for accessing specific mappings for automation processing and mapping management operations
  5. Enables automation management with precise mapping access for comprehensive automation processing and mapping lifecycle control

SQL Query: |
  SELECT tam.* FROM task_automation_mapping tam
  WHERE tam.tasks_id = ? AND tam.automations_id = ?

Parameters:
  - taskId: Long (Task identifier for specific mapping access)
  - automationId: Long (Automation identifier for specific mapping access)

Returns: TaskAutomationMapping (specific task automation mapping, null if not found)
Transaction: Not Required
Error Handling: Returns null if no mapping found for task and automation combination
```

#### Method: findByAutomationId(Long automationId)
```yaml
Signature: TaskAutomationMapping findByAutomationId(Long automationId)
Purpose: "Find task automation mapping by automation for automation-centric operations and mapping discovery"

Business Logic Derivation:
  1. Used for automation-centric mapping discovery during automation management and automation-focused operations
  2. Provides automation-based mapping access for automation management operations and automation-centric workflow processing
  3. Critical for automation operations requiring mapping discovery based on automation identifier for automation management workflows
  4. Used in automation management workflows for automation-based mapping access and automation-centric processing operations
  5. Enables automation-centric operations with mapping discovery for comprehensive automation management and processing workflows

SQL Query: |
  SELECT tam.* FROM task_automation_mapping tam
  WHERE tam.automations_id = ?

Parameters:
  - automationId: Long (Automation identifier for mapping discovery)

Returns: TaskAutomationMapping (task automation mapping for automation, null if not found)
Transaction: Not Required
Error Handling: Returns null if no mapping found for automation
```

#### Method: getChecklistAndTaskInfoByAutomationId(Long automationId)
```yaml
Signature: ObjectPropertyRelationChecklistView getChecklistAndTaskInfoByAutomationId(Long automationId)
Purpose: "Get checklist and task information for automation for validation and context information retrieval"

Business Logic Derivation:
  1. Used in ObjectTypeService and ParameterValidationService for retrieving checklist and task context information for automation validation operations
  2. Provides context information retrieval for automation validation workflows enabling comprehensive validation and context management
  3. Critical for validation operations requiring checklist and task context information for automation validation and context processing
  4. Used in automation validation workflows for accessing context information for validation processing and context management operations
  5. Enables automation validation with context information access for comprehensive validation processing and context management workflows

SQL Query: |
  SELECT DISTINCT
      c.id AS checklistId,
      c.name AS checklistName,
      c.code AS checklistCode,
      t.id AS taskId,
      t.name AS taskName,
      s.name AS stageName
  FROM task_automation_mapping tam
  JOIN tasks t ON tam.tasks_id = t.id
  JOIN stages s ON t.stages_id = s.id
  JOIN checklists c ON s.checklists_id = c.id
  WHERE tam.automations_id = ?

Parameters:
  - automationId: Long (Automation identifier for context information retrieval)

Returns: ObjectPropertyRelationChecklistView (checklist and task context information projection)
Transaction: Not Required
Error Handling: Returns null if no context information found for automation
```

#### Method: findTaskAutomationMappingByAutomationIdIn(List<Long> automationIds)
```yaml
Signature: List<TaskAutomationMapping> findTaskAutomationMappingByAutomationIdIn(List<Long> automationIds)
Purpose: "Find task automation mappings for multiple automations for bulk automation processing and mapping operations"

Business Logic Derivation:
  1. Used in TaskAutomationService for bulk retrieval of task automation mappings during bulk automation processing and mapping operations
  2. Provides efficient bulk mapping access for automation processing operations enabling bulk automation management and processing workflows
  3. Critical for bulk automation operations requiring mapping information for multiple automations for bulk processing and management workflows
  4. Used in bulk automation processing workflows for efficient mapping retrieval and bulk automation management operations
  5. Enables bulk automation processing with efficient mapping access for comprehensive automation management and bulk processing workflows

SQL Query: |
  SELECT tam.* FROM task_automation_mapping tam
  WHERE tam.automations_id IN (?, ?, ?, ...)

Parameters:
  - automationIds: List<Long> (List of automation identifiers for bulk mapping retrieval)

Returns: List<TaskAutomationMapping> (task automation mappings for specified automations)
Transaction: Not Required
Error Handling: Returns empty list if no mappings found for any automation identifiers
```

### Key Repository Usage Patterns

#### Pattern: saveAll() for Bulk Task Automation Management
```yaml
Usage: taskAutomationMappingRepository.saveAll(mappings)
Purpose: "Create task automation mappings in bulk for automation management and workflow automation configuration"

Business Logic Derivation:
  1. Used extensively in ChecklistRevisionService for bulk task automation mapping creation during revision and automation management operations
  2. Provides efficient bulk automation mapping persistence for operations creating multiple automation associations simultaneously
  3. Critical for automation management operations requiring bulk mapping creation for workflow automation configuration and management
  4. Used in automation configuration workflows for bulk mapping creation and task automation association operations
  5. Enables efficient bulk task automation operations with transaction consistency for comprehensive automation management

Transaction: Required
Error Handling: DataIntegrityViolationException for bulk constraint violations, composite key conflicts
```

#### Pattern: Automation Discovery and Execution Processing
```yaml
Usage: Multiple methods for automation discovery, existence validation, and execution processing
Purpose: "Discover automations for task execution and validate automation configurations for workflow automation processing"

Business Logic Derivation:
  1. Automation discovery enables task automation execution through trigger-specific automation identification and processing
  2. Automation existence validation prevents duplicate automation creation and ensures proper automation configuration management
  3. Task automation execution workflows depend on automation discovery for proper workflow automation processing and execution
  4. Automation validation workflows require existence checks and context information for comprehensive automation management
  5. Task automation processing requires comprehensive automation discovery and validation for workflow automation functionality

Transaction: Not Required for discovery and validation operations
Error Handling: Empty result handling for tasks without automation associations, validation error handling
```

#### Pattern: Automation Lifecycle Management and Context Operations
```yaml
Usage: Task automation mapping lifecycle management for automation configuration and context processing
Purpose: "Manage task automation mapping lifecycle for comprehensive automation configuration and context management"

Business Logic Derivation:
  1. Task automation mappings enable workflow automation functionality through task automation associations and automation management
  2. Automation mapping lifecycle management supports task automation requirements and workflow automation functionality
  3. Task automation mapping lifecycle includes creation, association management, and cleanup operations for automation control
  4. Automation management operations enable comprehensive task automation functionality with workflow automation capabilities
  5. Automation mapping lifecycle control supports task automation operations and workflow automation management requirements

Transaction: Required for lifecycle operations and automation management
Error Handling: Composite key validation and automation association integrity verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Workflow Automation Configuration and Management
```yaml
Usage: Complete task automation mapping lifecycle for workflow automation functionality
Purpose: "Manage task automation mappings for comprehensive workflow automation configuration and automation management"

Business Logic Derivation:
  1. Task automation mappings provide workflow automation functionality through task automation associations and automation configuration management
  2. Automation configuration lifecycle includes creation, association management, validation operations, and cleanup workflows
  3. Task automation validation operations require automation existence checks and context information for automation configuration management
  4. Automation deletion operations require precise mapping cleanup for automation lifecycle and workflow automation management
  5. Automation mapping lifecycle management supports task automation requirements and workflow automation functionality

Common Usage Examples:
  - taskAutomationMappingRepository.saveAll() in ChecklistRevisionService for bulk automation mapping creation
  - taskAutomationMappingRepository.deleteByTaskIdAndAutomationId() for specific automation mapping cleanup
  - taskAutomationMappingRepository.automationExistsByTaskIdAndTriggerTypeAndAutomationActionTypes() for automation validation
  - Automation mapping lifecycle management for workflow automation configuration and automation management operations
  - Comprehensive task automation management with lifecycle control and automation association management

Transaction: Required for lifecycle operations and bulk automation management
Error Handling: Bulk operation error handling and automation association integrity verification
```

### Pattern: Automation Execution and Workflow Processing
```yaml
Usage: Automation execution workflows with task automation discovery and trigger-based processing
Purpose: "Execute task automations with trigger-based discovery for workflow automation processing and execution"

Business Logic Derivation:
  1. Automation execution workflows require task automation discovery for proper automation processing and workflow execution
  2. Trigger-based automation processing involves automation identification based on trigger types for workflow automation execution
  3. Task automation execution ensures proper workflow automation through trigger-specific automation discovery and processing
  4. Execution workflows coordinate automation discovery with workflow processing for comprehensive task automation execution
  5. Task automation processing supports workflow automation requirements and automation execution functionality

Common Execution Patterns:
  - Trigger-based automation discovery for workflow automation execution and automation processing operations
  - Task automation execution with trigger-specific automation identification and workflow automation processing
  - Automation execution workflows with task automation discovery and workflow automation functionality
  - Workflow automation processing through task automation discovery and automation execution operations
  - Comprehensive automation execution with workflow automation functionality and automation processing capabilities

Transaction: Not Required for execution discovery and processing operations
Error Handling: Execution operation error handling and automation discovery validation
```

### Pattern: Validation and Context Management Operations
```yaml
Usage: Automation validation workflows with context information retrieval and validation processing
Purpose: "Validate task automations with context information for comprehensive validation and automation management"

Business Logic Derivation:
  1. Automation validation operations require context information for proper validation processing and automation management
  2. Context information retrieval enables validation workflows with checklist and task context for comprehensive validation processing
  3. Automation validation ensures proper automation configuration and context management during validation operations and processing
  4. Validation workflows coordinate context information retrieval with validation processing for comprehensive automation validation
  5. Context management supports automation validation requirements and validation processing functionality

Common Validation Patterns:
  - Context information retrieval for automation validation and validation processing operations
  - Automation validation with checklist and task context for comprehensive validation and context management
  - Validation processing workflows with context information access and automation validation functionality
  - Context management operations for automation validation and validation processing requirements
  - Comprehensive automation validation with context information and validation processing capabilities

Transaction: Not Required for validation and context operations
Error Handling: Validation operation error handling and context information verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAllAutomationsByTaskIdAndTriggerType, automationExistsByTaskIdAndTriggerTypeAndAutomationActionTypes
  - findByTaskIdAndAutomationId, findByAutomationId, getChecklistAndTaskInfoByAutomationId
  - findTaskAutomationMappingByAutomationIdIn, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById, deleteByTaskIdAndAutomationId

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Composite key constraint violations (duplicate task-automation combinations)
    * NOT NULL constraint violations (tasks_id, automations_id, order_tree, display_name)
    * Foreign key violations (invalid tasks_id, automations_id references)
    * Unique constraint violations on composite key
  - EntityNotFoundException: Task automation mapping not found by composite key or criteria
  - OptimisticLockException: Concurrent task automation mapping modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or composite key operations
  - ConstraintViolationException: Task automation mapping constraint violations

Validation Rules:
  - taskAutomationId: Required, composite key containing taskId and automationId for unique associations
  - task: Required, must reference existing task, immutable after creation
  - taskId: Derived from composite key and task relationship for task-scoped operations
  - automation: Required, must reference existing automation, immutable after creation
  - automationId: Derived from composite key and automation relationship for automation association operations
  - orderTree: Required, integer for automation execution ordering and workflow control
  - displayName: Required, text for automation identification and display management

Business Constraints:
  - Task automation mappings must be unique for proper association management and workflow automation integrity
  - Task and automation references must be valid for association integrity and workflow automation functionality
  - Task automation mappings must support workflow automation functionality requirements and automation lifecycle management
  - Association lifecycle management must maintain referential integrity and workflow automation functionality consistency
  - Automation mapping cleanup must ensure proper automation lifecycle control and task automation functionality
  - Task automation associations must support automation requirements and workflow automation functionality
  - Bulk operations must maintain transaction consistency and constraint integrity for automation management
  - Automation mapping lifecycle management must maintain workflow automation functionality and automation association consistency
  - Association management must maintain task automation integrity and workflow automation requirements
  - Cleanup operations must ensure proper automation lifecycle management and workflow automation control
```

## Task Automation Mapping Considerations

### Workflow Automation Integration
```yaml
Task Association: Task automation mappings enable workflow automation functionality through task automation associations
Automation Execution: Automation associations enable task functionality with comprehensive workflow automation capabilities
Trigger Management: Trigger type management for automation execution and workflow automation control
Automation Ordering: Order tree management for automation execution sequence and workflow automation control
Workflow Control: Comprehensive workflow automation control through task automation associations and management
```

### Automation Lifecycle Management
```yaml
Automation Configuration: Task automation mapping configuration for workflow automation functionality and automation management
Automation Lifecycle: Automation mapping lifecycle includes creation, association management, and cleanup operations
Bulk Operations: Efficient bulk operations for automation management and workflow automation configuration
Automation Cleanup: Comprehensive automation mapping cleanup for automation lifecycle and workflow automation management
Association Management: Automation association management for workflow automation functionality and automation requirements
```

### Validation and Context Integration
```yaml
Automation Validation: Task automation validation workflows with existence checks and context information retrieval
Context Management: Context information access for automation validation and validation processing operations
Validation Processing: Automation validation with checklist and task context for comprehensive validation functionality
Context Integration: Context information integration for automation validation and workflow automation management
Validation Control: Comprehensive automation validation control through context management and validation processing
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the TaskAutomationMapping repository without JPA/Hibernate dependencies, focusing on workflow automation management and task automation association patterns.
