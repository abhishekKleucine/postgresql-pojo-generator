# ICustomViewRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: CustomView (MongoDB Collection)
- **Primary Purpose**: Manages custom view configurations for dynamic UI customization with target-specific view definitions, column configurations, and filter management
- **Key Relationships**: MongoDB collection entity with process-scoped custom views and facility/use case associations for UI personalization
- **Performance Characteristics**: Low to moderate query volume with view retrieval, configuration management, and target-specific operations
- **Business Context**: UI customization component that provides user-defined view configurations, dynamic column management, and process-specific view personalization for enhanced user experience

## Entity Mapping Documentation

### Field Mappings (MongoDB Document)

| MongoDB Field | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| _id | id | String | false | auto-generated |
| facilityId | facilityId | String | true | null |
| useCaseId | useCaseId | String | true | null |
| targetType | targetType | Type.ConfigurableViewTargetType | true | null |
| processId | processId | String | true | null |
| label | label | String | true | null |
| columns | columns | List\<CustomViewColumn\> | true | null |
| filters | filters | List\<CustomViewFilter\> | true | null |
| archived | archived | boolean | false | false |
| createdAt | createdAt | Long | true | null |
| modifiedAt | modifiedAt | Long | true | null |
| createdBy | createdBy | String | true | null |
| modifiedBy | modifiedBy | String | true | null |

### Embedded Objects
- **CustomViewColumn**: Column configuration for view customization
- **CustomViewFilter**: Filter configuration for view data filtering

### Relationships
None - MongoDB document with embedded objects and string references to related entities.

## Available Repository Methods

### Standard MongoDB Methods (MongoRepository)
- `findById(String id)`
- `findAll()`
- `save(CustomView entity)`
- `saveAll(Iterable<CustomView> entities)`
- `deleteById(String id)`
- `delete(CustomView entity)`
- `existsById(String id)`
- `count()`

### Custom Query Methods (2 methods - ALL methods documented)

- `findByTargetType(Type.ConfigurableViewTargetType targetType, Pageable pageable)`
- `findByProcessIdAndTargetType(String processId, Type.ConfigurableViewTargetType targetType, Pageable pageable)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard MongoDB Operations
```java
// Standard MongoRepository methods
Optional<CustomView> findById(String id)
List<CustomView> findAll()
CustomView save(CustomView entity)
List<CustomView> saveAll(Iterable<CustomView> entities)
void deleteById(String id)
void delete(CustomView entity)
boolean existsById(String id)
long count()
```

### Custom Query Methods

#### Method: findByTargetType(Type.ConfigurableViewTargetType targetType, Pageable pageable)
```yaml
Signature: Page<CustomView> findByTargetType(Type.ConfigurableViewTargetType targetType, Pageable pageable)
Purpose: "Find custom views by target type with pagination for target-specific view management and discovery"

Business Logic Derivation:
  1. Used in CustomViewService for retrieving custom views by target type across all processes and facilities
  2. Provides target-specific view discovery for UI customization and view template management
  3. Critical for custom view management operations requiring target-type-based view filtering and organization
  4. Used in custom view listing workflows for administrative operations and view template discovery
  5. Enables target-specific view management with pagination for large view datasets and administrative operations

MongoDB Query: |
  db.custom_views.find({
    "targetType": ?
  }).skip(skip).limit(limit).sort(sort)

Parameters:
  - targetType: Type.ConfigurableViewTargetType (Target type for view filtering - typically PROCESS)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<CustomView> (paginated custom views matching target type)
Transaction: Not Required (MongoDB single document operations)
Error Handling: Returns empty page if no views found for target type
```

#### Method: findByProcessIdAndTargetType(String processId, Type.ConfigurableViewTargetType targetType, Pageable pageable)
```yaml
Signature: Page<CustomView> findByProcessIdAndTargetType(String processId, Type.ConfigurableViewTargetType targetType, Pageable pageable)
Purpose: "Find custom views by process and target type for process-specific view configuration and management"

Business Logic Derivation:
  1. Used extensively in CustomViewService for process-specific custom view retrieval and view configuration management
  2. Provides process-scoped custom view discovery for UI personalization and process-specific view customization
  3. Critical for custom view operations requiring process-specific view configuration and view lifecycle management
  4. Used in view reconfiguration workflows when process configurations change and views need updates
  5. Enables process-specific view management with comprehensive view configuration for process-centric UI customization

MongoDB Query: |
  db.custom_views.find({
    "processId": ?,
    "targetType": ?
  }).skip(skip).limit(limit).sort(sort)

Parameters:
  - processId: String (Process identifier for process-specific view filtering)
  - targetType: Type.ConfigurableViewTargetType (Target type for view classification)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<CustomView> (paginated custom views for specific process and target type)
Transaction: Not Required (MongoDB single document operations)
Error Handling: Returns empty page if no views found for process and target type combination
```

### Key Repository Usage Patterns

#### Pattern: save() for Custom View Lifecycle Management
```yaml
Usage: customViewRepository.save(customView)
Purpose: "Create new custom views, update view configurations, and manage view lifecycle"

Business Logic Derivation:
  1. Used extensively for custom view creation with column configuration, filter setup, and view personalization
  2. Handles custom view configuration updates including column changes, filter modifications, and view settings
  3. Updates custom view lifecycle information for view management and user personalization tracking
  4. Critical for custom view lifecycle management and UI customization operations
  5. Supports custom view operations with comprehensive configuration management and view personalization

Transaction: Not Required (MongoDB ACID operations for single documents)
Error Handling: WriteException for MongoDB constraint violations
```

#### Pattern: findById() for Custom View Context Operations
```yaml
Usage: customViewRepository.findById(customViewId)
Purpose: "Retrieve custom view entity for view-specific operations and configuration management"

Business Logic Derivation:
  1. Used extensively for custom view context retrieval in view management, configuration updates, and view operations
  2. Critical for custom view validation, view configuration access, and view-specific business logic
  3. Used in view update operations, view deletion workflows, and view configuration retrieval
  4. Essential for custom view context management and view-based UI operations
  5. Enables view-centric operations with comprehensive view configuration and personalization settings

Transaction: Not Required
Error Handling: Throws ResourceNotFoundException if custom view not found
```

#### Pattern: saveAll() for Bulk Custom View Operations
```yaml
Usage: customViewRepository.saveAll(customViews)
Purpose: "Bulk custom view updates for view reconfiguration and batch view management"

Business Logic Derivation:
  1. Used in CustomViewService for bulk custom view updates during view reconfiguration operations
  2. Enables efficient bulk view updates for view configuration changes and batch view management
  3. Critical for view reconfiguration workflows when process configurations change requiring view updates
  4. Used in view management operations requiring bulk view updates and configuration synchronization
  5. Supports efficient bulk view operations with MongoDB batch operations for view management workflows

Transaction: Not Required (MongoDB batch operations)
Error Handling: BulkWriteException for batch operation failures
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Process-Specific Custom View Management
```yaml
Usage: findByProcessIdAndTargetType(processId, PROCESS, pageable)
Purpose: "Manage process-specific custom views for process-centric UI customization and view configuration"

Business Logic Derivation:
  1. Process-specific custom views enable personalized UI configuration for different business processes
  2. Custom view management allows users to configure columns, filters, and view settings for specific processes
  3. View reconfiguration workflows coordinate custom view updates with process configuration changes
  4. Process-scoped view management ensures view configuration isolation and process-specific personalization
  5. Custom view lifecycle management supports view creation, updates, and deletion for process customization

Common Usage Examples:
  - customViewRepository.findByProcessIdAndTargetType(checklistId, PROCESS, null) for process view retrieval
  - Process-specific view configuration for checklist job logs and process data display
  - Custom view reconfiguration when process column configurations change
  - Process-centric view management for user personalization and UI customization
  - View synchronization with process configuration updates and column changes

Transaction: Not Required
Error Handling: Configuration validation, view consistency checks
```

### Pattern: Custom View Reconfiguration Workflows
```yaml
Usage: Bulk view updates for process configuration changes
Purpose: "Synchronize custom views with process configuration changes and column updates"

Business Logic Derivation:
  1. Custom view reconfiguration ensures view consistency when underlying process configurations change
  2. Column configuration changes require custom view updates to maintain view functionality and data integrity
  3. View synchronization workflows maintain custom view validity and prevent configuration drift
  4. Bulk view operations enable efficient view updates for large numbers of custom views
  5. Configuration management ensures custom view compatibility with current process configurations

Common Usage Examples:
  - Bulk custom view updates when process job log columns are reconfigured
  - View column synchronization with process configuration changes
  - Custom view validation and update workflows for configuration consistency
  - Automated view reconfiguration during process lifecycle operations
  - View maintenance operations for configuration integrity and user experience

Transaction: Not Required (MongoDB batch operations handle consistency)
Error Handling: Configuration validation, batch operation error handling
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByTargetType, findByProcessIdAndTargetType
  - existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById (MongoDB handles ACID for single/batch operations)

Isolation Level: MongoDB default (read committed equivalent)
Timeout: 30 seconds
Rollback: MongoDB transaction rollback for multi-document operations
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - ResourceNotFoundException: Custom view not found by ID or criteria
  - WriteException: MongoDB write operation failures
  - BulkWriteException: Batch operation failures in saveAll operations
  - InvalidDataAccessApiUsageException: Invalid query parameters or pagination settings
  - MongoException: General MongoDB operation failures

Validation Rules:
  - id: Auto-generated string identifier for MongoDB document
  - facilityId: Optional, string reference to facility for facility-scoped views
  - useCaseId: Optional, string reference to use case for use case classification
  - targetType: Optional, must be valid ConfigurableViewTargetType enum value
  - processId: Optional, string reference to process for process-specific views
  - label: Optional, descriptive label for custom view identification
  - columns: Optional, list of CustomViewColumn objects for column configuration
  - filters: Optional, list of CustomViewFilter objects for filter configuration
  - archived: Defaults to false, used for soft deletion of custom views

Business Constraints:
  - Custom view configurations must be valid and consistent with target entity schemas
  - Column configurations must reference valid data fields for view functionality
  - Filter configurations must be compatible with data types and field structures
  - View archival should be used instead of deletion for data integrity and user experience
  - Custom view modifications must maintain user personalization and configuration consistency
  - View reconfiguration must handle configuration changes gracefully without data loss
  - Process-specific views must be synchronized with process configuration changes
  - View column configurations must be validated against available data fields
  - Custom view operations must respect user permissions and access controls
  - View configuration changes must maintain backward compatibility where possible
```

## MongoDB-Specific Considerations

### Document Structure
```yaml
Collection: custom_views
Document Type: CustomView with embedded arrays (columns, filters)
Indexing Strategy:
  - Index on targetType for target-based queries
  - Compound index on (processId, targetType) for process-specific queries
  - Index on facilityId for facility-scoped operations
  - Index on archived for filtering archived views

Performance Optimization:
  - Embedded document structure for columns and filters reduces query complexity
  - Compound indexes optimize process-specific view retrieval
  - Pagination support for large view datasets
  - Aggregation pipeline optimization for complex view queries
```

### Data Consistency
```yaml
Consistency Model: Eventually consistent with MongoDB replication
Referential Integrity: Application-level enforcement for string references
Schema Validation: MongoDB schema validation for document structure
Migration Strategy: Document versioning for schema evolution and view compatibility
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the CustomView repository without MongoDB dependencies, focusing on custom view management and UI personalization patterns.
