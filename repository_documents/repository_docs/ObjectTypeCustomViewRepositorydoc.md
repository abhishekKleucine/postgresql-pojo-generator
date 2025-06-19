# IObjectTypeCustomViewRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ObjectTypeCustomView (MongoDB Document)
- **Primary Purpose**: Manages MongoDB object type custom view entities for custom view configuration management with column definition tracking, filter management, and view customization functionality
- **Key Relationships**: References ObjectType, Facility, and UseCase entities for comprehensive custom view configuration and context management
- **Performance Characteristics**: Low to medium query volume with custom view retrieval, view configuration operations, and custom view management
- **Business Context**: Custom view component that provides object type view customization, column configuration, filter management, and view personalization functionality for object type data presentation

## Entity Mapping Documentation

### Field Mappings (MongoDB Document)

| MongoDB Field | Java Field | Type | Required | Index | Notes |
|---|---|---|---|---|---|
| _id | id | String | true | primary | MongoDB primary key |
| facilityId | facilityId | String | false | false | Associated facility identifier |
| useCaseId | useCaseId | String | false | false | Associated use case identifier |
| objectTypeId | objectTypeId | String | false | false | Associated object type identifier |
| label | label | String | false | false | Custom view label/name |
| columns | columns | List<CustomViewColumn> | false | false | Embedded column definitions |
| filters | filters | List<CustomViewFilter> | false | false | Embedded filter definitions |
| archived | archived | boolean | false | false | Archive status, defaults to false |
| createdAt | createdAt | Long | false | false | Creation timestamp |
| modifiedAt | modifiedAt | Long | false | false | Modification timestamp |
| createdBy | createdBy | String | false | false | Creator identifier |
| modifiedBy | modifiedBy | String | false | false | Modifier identifier |

### MongoDB Configuration
- **Collection Name**: Defined by CollectionName.OBJECT_TYPE_CUSTOM_VIEWS constant
- **Document Structure**: Complex document with embedded arrays for columns and filters
- **Embedded Collections**: CustomViewColumn and CustomViewFilter objects with view configuration

## Available Repository Methods

### Standard MongoRepository Methods
- `findById(String id)`
- `findAll()`
- `save(ObjectTypeCustomView entity)`
- `saveAll(Iterable<ObjectTypeCustomView> entities)`
- `deleteById(String id)`
- `delete(ObjectTypeCustomView entity)`
- `existsById(String id)`
- `count()`

### Custom Query Methods (1 method - ALL methods documented)

**Custom View Retrieval Methods (1 method):**
- `findByObjectTypeIdAndLabel(String objectTypeId, String label)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard MongoRepository Operations
```java
// Standard MongoRepository methods
Optional<ObjectTypeCustomView> findById(String id)
List<ObjectTypeCustomView> findAll()
ObjectTypeCustomView save(ObjectTypeCustomView entity)
List<ObjectTypeCustomView> saveAll(Iterable<ObjectTypeCustomView> entities)
void deleteById(String id)
void delete(ObjectTypeCustomView entity)
boolean existsById(String id)
long count()
```

### Custom View Retrieval Methods

#### Method: findByObjectTypeIdAndLabel(String objectTypeId, String label)
```yaml
Signature: ObjectTypeCustomView findByObjectTypeIdAndLabel(String objectTypeId, String label)
Purpose: "Find custom view by object type ID and label for custom view validation and duplicate checking"

Business Logic Derivation:
  1. Used in CustomViewService for custom view validation during custom view creation and duplicate prevention operations
  2. Provides custom view uniqueness validation for view workflows enabling comprehensive custom view management and validation functionality
  3. Critical for custom view creation operations requiring duplicate validation for view management and uniqueness control
  4. Used in custom view workflows for accessing existing views for validation operations and view processing
  5. Enables custom view management with uniqueness validation for comprehensive view processing and validation control

MongoDB Query: |
  db.objectTypeCustomViews.findOne({
    objectTypeId: objectTypeId,
    label: label
  })

Parameters:
  - objectTypeId: String (Object type identifier for custom view context)
  - label: String (Custom view label for uniqueness checking)

Returns: ObjectTypeCustomView (custom view if found, null if not found)
Transaction: Not Required (MongoDB operations)
Error Handling: Returns null if no custom view found for object type and label combination
```

### Key Repository Usage Patterns

#### Pattern: save() for Custom View Management
```yaml
Usage: objectTypeCustomViewRepository.save(objectTypeCustomView)
Purpose: "Create and update custom views for object type view customization and configuration"

Business Logic Derivation:
  1. Used extensively in CustomViewService for custom view persistence during view creation, updates, and configuration operations
  2. Provides custom view persistence for configuration workflows enabling comprehensive view management and configuration functionality
  3. Critical for view configuration operations requiring custom view persistence for configuration management and view control
  4. Used in view configuration workflows for custom view persistence and configuration management operations
  5. Enables view configuration with custom view persistence for comprehensive configuration processing and view control

Transaction: Not Required (MongoDB document-level consistency)
Error Handling: MongoDB exceptions for persistence failures, validation errors
```

#### Pattern: findById() for Custom View Retrieval
```yaml
Usage: objectTypeCustomViewRepository.findById(customViewId)
Purpose: "Retrieve custom views by ID for view configuration and data presentation"

Business Logic Derivation:
  1. Used in CustomViewService and JobLogService for custom view retrieval during view configuration and data presentation operations
  2. Provides custom view access for presentation workflows enabling comprehensive view configuration and presentation functionality
  3. Critical for data presentation operations requiring view configuration for presentation management and view control
  4. Used in presentation workflows for accessing custom views for configuration operations and data processing
  5. Enables data presentation with view configuration for comprehensive presentation processing and view control

Transaction: Not Required (MongoDB read operations)
Error Handling: ResourceNotFoundException when custom view not found for specified ID
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Custom View Configuration Management
```yaml
Usage: Complete custom view configuration for view management and customization functionality
Purpose: "Manage custom view configurations for comprehensive view functionality and customization processing"

Business Logic Derivation:
  1. Custom view configuration management provides view functionality through view creation, column management, and filter configuration operations
  2. View lifecycle includes view creation, configuration updates, and customization management for view control
  3. View management operations require configuration processing for view lifecycle and customization control
  4. Configuration operations enable comprehensive view functionality with customization capabilities and management
  5. View lifecycle management supports configuration requirements and functionality for view customization processing

Common Usage Examples:
  - objectTypeCustomViewRepository.findByObjectTypeIdAndLabel() for duplicate checking during view creation
  - objectTypeCustomViewRepository.save() for view creation and updates in CustomViewService
  - objectTypeCustomViewRepository.findById() for view retrieval in CustomViewService and JobLogService
  - Custom view archive management with status updates
  - View configuration with column and filter management

Transaction: Not Required (MongoDB document-level consistency)
Error Handling: Custom view configuration error handling and validation verification
```

### Pattern: Data Presentation and View Management
```yaml
Usage: Data presentation and view management for object type data customization and presentation functionality
Purpose: "Manage data presentation for comprehensive customization functionality and view processing"

Business Logic Derivation:
  1. Data presentation management operations require comprehensive custom view access for presentation-level customization management and view functionality
  2. Presentation management supports customization requirements and functionality for data processing workflows
  3. Presentation-level view operations ensure proper customization functionality through view management and presentation control
  4. Data workflows coordinate presentation management with view processing for comprehensive data operations
  5. Presentation management supports customization requirements and functionality for comprehensive data presentation management

Common Usage Examples:
  - objectTypeCustomViewRepository.findById() for view configuration retrieval in data presentation
  - Custom view column and filter application for data presentation
  - View-based data filtering and column selection for object type data

Transaction: Not Required for presentation operations
Error Handling: Presentation error handling and view validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByObjectTypeIdAndLabel, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById (MongoDB document-level consistency)

Isolation Level: MongoDB default consistency
Timeout: MongoDB default timeout
Rollback: MongoDB document-level operations
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - MongoException: MongoDB connection and operation failures
  - DocumentNotFoundException: Custom view not found for specified criteria
  - DuplicateViewException: Duplicate custom view for object type and label combination
  - InvalidConfigurationException: Invalid column or filter configuration
  - ResourceNotFoundException: Custom view not found for specified ID (used in service layer)

Validation Rules:
  - id: String identifier for custom view identification
  - objectTypeId: Valid object type identifier for custom view context
  - label: String label for custom view naming and uniqueness
  - facilityId: Valid facility identifier for custom view scope
  - useCaseId: Valid use case identifier for custom view context
  - columns: Valid CustomViewColumn array for view configuration
  - filters: Valid CustomViewFilter array for view filtering
  - archived: Boolean flag for custom view status management

Business Constraints:
  - Custom view label must be unique within object type context
  - Object type reference must be valid for custom view integrity
  - Facility and use case references should be valid for proper scoping
  - Column definitions must reference valid object type properties
  - Filter definitions must reference valid object type properties
  - Custom view configuration must maintain referential integrity
  - View customization must support object type schema requirements
  - Archive status must be properly managed for view lifecycle
  - Audit fields must be maintained for change tracking
  - Embedded column and filter arrays must maintain consistency
```

## MongoDB Custom View Collection Considerations

### Document Structure and Configuration
```yaml
Document Design: Complex document with embedded arrays for columns and filters for comprehensive view definition
Index Strategy: Primary key index on _id, potential compound indexes on objectTypeId and label for uniqueness
Query Patterns: Single document retrieval, composite key lookup for duplicate checking
Performance: Optimized for custom view configuration with embedded view definitions
Scalability: Horizontal scaling support through MongoDB sharding on custom view ID
```

### Column and Filter Management
```yaml
Embedded Arrays: Columns and filters stored as embedded arrays within custom view documents
View Configuration: Custom view definitions enable dynamic column and filter configuration
Column Management: Embedded column definitions support flexible view customization and presentation
Filter Management: Embedded filter definitions enable dynamic data filtering and view customization
View Customization: Custom view definitions support comprehensive data presentation customization
```

### Object Type Integration
```yaml
Schema Integration: Custom view definitions integrate with object type schemas for view validation
Property References: Column and filter definitions reference object type properties for data presentation
View Validation: Custom view configuration validates against object type property definitions
Data Presentation: Custom views enable flexible object type data presentation and customization
Configuration Management: Custom view management supports object type schema evolution and view updates
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the ObjectTypeCustomView repository without MongoDB dependencies, focusing on custom view configuration and data presentation patterns.
