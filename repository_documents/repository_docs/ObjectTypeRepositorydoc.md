# IObjectTypeRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: ObjectType (MongoDB Document)
- **Primary Purpose**: Manages MongoDB object type entities for dynamic object definition management with property tracking, relation management, and object type configuration functionality
- **Key Relationships**: Contains embedded Property and Relation collections for comprehensive object type definition and metadata management
- **Performance Characteristics**: Medium to high query volume with complex property/relation queries, object type retrieval operations, and dynamic schema management
- **Business Context**: Dynamic schema component that provides object type definition management, property configuration, relation management, and dynamic object functionality for entity object workflows

## Entity Mapping Documentation

### Field Mappings (MongoDB Document)

| MongoDB Field | Java Field | Type | Required | Index | Notes |
|---|---|---|---|---|---|
| _id | id | ObjectId | true | primary | MongoDB primary key |
| version | version | Integer | false | false | Object type version |
| collection | collection | String | false | false | Associated collection name |
| externalId | externalId | String | false | false | External identifier |
| displayName | displayName | String | false | false | Human-readable name |
| pluralName | pluralName | String | false | false | Plural form of name |
| description | description | String | false | false | Object type description |
| properties | properties | List<Property> | false | false | Embedded property definitions |
| relations | relations | List<Relation> | false | false | Embedded relation definitions |
| usageStatus | usageStatus | int | false | false | Usage status (see UsageStatus enum) |
| modifiedAt | modifiedAt | Long | false | false | Modification timestamp |
| modifiedBy | modifiedBy | UserInfo | false | false | Modification user info |
| createdAt | createdAt | Long | false | false | Creation timestamp |
| createdBy | createdBy | UserInfo | false | false | Creation user info |
| flags | flags | Integer | false | false | Object type flags |

### MongoDB Configuration
- **Collection Name**: Defined by CollectionName.OBJECT_TYPES constant
- **Document Structure**: Complex document with embedded arrays for properties and relations
- **Embedded Collections**: Property and Relation objects with full schema definitions

## Available Repository Methods

### Custom Query Methods (7 methods - ALL methods documented)

**Object Type Retrieval Methods (3 methods):**
- `findAll()`
- `findAll(int usageStatus, String name, String filters, Pageable pageable)`
- `findById(String id)`

**Property Management Methods (2 methods):**
- `getAllObjectTypeProperties(String objectTypeId, int usageStatus, String name, Pageable pageable)`
- `findPropertyByIdAndObjectTypeExternalId(String objectTypeExternalId, ObjectId propertyId)`

**Relation Management Methods (1 method):**
- `getAllObjectTypeRelations(String objectTypeId, int usageStatus, String name, Pageable pageable)`

**Persistence Methods (1 method):**
- `save(ObjectType objectType)`

## Method Documentation (All Custom Methods - Full Detail)

### Object Type Retrieval Methods

#### Method: findAll()
```yaml
Signature: List<ObjectType> findAll()
Purpose: "Find all object types for system-wide object type management and configuration"

Business Logic Derivation:
  1. Used extensively across multiple services for complete object type retrieval during system configuration and object type listing operations
  2. Provides comprehensive object type access for system workflows enabling complete object type management and configuration functionality
  3. Critical for system configuration operations requiring all object type access for system management and configuration control
  4. Used in migration and configuration workflows for accessing all object types for system operations and configuration processing
  5. Enables system configuration with complete object type access for comprehensive system processing and configuration control

MongoDB Query: |
  db.objectTypes.find({})

Parameters: None

Returns: List<ObjectType> (all object types in system)
Transaction: Not Required (MongoDB operations)
Error Handling: Returns empty list if no object types exist
```

#### Method: findAll(int usageStatus, String name, String filters, Pageable pageable)
```yaml
Signature: Page<ObjectType> findAll(int usageStatus, String name, String filters, Pageable pageable) throws StreemException
Purpose: "Find object types with filtering and pagination for object type search and management"

Business Logic Derivation:
  1. Used in ObjectTypeService for paginated object type retrieval during object type search and filtering operations
  2. Provides filtered object type access for system workflows enabling comprehensive object type search and management functionality
  3. Critical for object type search operations requiring filtered access for object type management and search control
  4. Used in object type search workflows for accessing filtered object types for search operations and object type processing
  5. Enables object type search with filtered access for comprehensive object type processing and search control

MongoDB Query: |
  db.objectTypes.find({
    $and: [
      { usageStatus: usageStatus },
      { displayName: { $regex: name, $options: "i" } },
      // Additional filters based on filters parameter
    ]
  }).sort().skip().limit()

Parameters:
  - usageStatus: int (Usage status filter for object type filtering)
  - name: String (Name filter for object type search)
  - filters: String (Additional filters for complex search)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<ObjectType> (paginated filtered object types)
Transaction: Not Required (MongoDB operations)
Error Handling: Throws StreemException for filter parsing or query execution errors
```

#### Method: findById(String id)
```yaml
Signature: Optional<ObjectType> findById(String id)
Purpose: "Find object type by ID for object type retrieval and validation"

Business Logic Derivation:
  1. Used extensively across multiple services for object type retrieval during object type validation and entity object operations
  2. Provides object type access for entity workflows enabling comprehensive object type validation and entity functionality
  3. Critical for entity object operations requiring object type validation for entity management and object control
  4. Used in entity object workflows for accessing object types for validation operations and entity processing
  5. Enables entity object management with object type validation for comprehensive entity processing and object control

MongoDB Query: |
  db.objectTypes.findOne({ _id: ObjectId(id) })

Parameters:
  - id: String (Object type identifier for retrieval)

Returns: Optional<ObjectType> (object type if found, empty otherwise)
Transaction: Not Required (MongoDB operations)
Error Handling: Returns empty Optional if object type not found
```

### Property Management Methods

#### Method: getAllObjectTypeProperties(String objectTypeId, int usageStatus, String name, Pageable pageable)
```yaml
Signature: Page<Property> getAllObjectTypeProperties(String objectTypeId, int usageStatus, String name, Pageable pageable) throws StreemException
Purpose: "Get object type properties with filtering and pagination for property management"

Business Logic Derivation:
  1. Used in ObjectTypeService for property retrieval during object type property management and property listing operations
  2. Provides property access for object type workflows enabling comprehensive property management and object type functionality
  3. Critical for property management operations requiring property access for object type management and property control
  4. Used in property management workflows for accessing object type properties for property operations and object type processing
  5. Enables property management with filtered property access for comprehensive object type processing and property control

MongoDB Query: |
  db.objectTypes.aggregate([
    { $match: { _id: ObjectId(objectTypeId) } },
    { $unwind: "$properties" },
    { $match: {
        "properties.usageStatus": usageStatus,
        "properties.displayName": { $regex: name, $options: "i" }
      }
    },
    { $skip: skip },
    { $limit: limit }
  ])

Parameters:
  - objectTypeId: String (Object type identifier for property retrieval)
  - usageStatus: int (Usage status filter for property filtering)
  - name: String (Name filter for property search)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<Property> (paginated filtered properties)
Transaction: Not Required (MongoDB operations)
Error Handling: Throws StreemException for filter parsing or aggregation errors
```

#### Method: findPropertyByIdAndObjectTypeExternalId(String objectTypeExternalId, ObjectId propertyId)
```yaml
Signature: Optional<Property> findPropertyByIdAndObjectTypeExternalId(String objectTypeExternalId, ObjectId propertyId) throws StreemException
Purpose: "Find property by ID within specific object type external ID for property validation"

Business Logic Derivation:
  1. Used in TaskAutomationService for property validation during automation configuration and property verification operations
  2. Provides property validation for automation workflows enabling comprehensive property verification and automation functionality
  3. Critical for automation operations requiring property validation for automation management and property control
  4. Used in automation workflows for accessing specific properties for validation operations and automation processing
  5. Enables automation configuration with property validation for comprehensive automation processing and property control

MongoDB Query: |
  db.objectTypes.findOne({
    externalId: objectTypeExternalId,
    "properties._id": propertyId
  }, {
    "properties.$": 1
  })

Parameters:
  - objectTypeExternalId: String (Object type external identifier for property context)
  - propertyId: ObjectId (Property identifier for property retrieval)

Returns: Optional<Property> (property if found, empty otherwise)
Transaction: Not Required (MongoDB operations)
Error Handling: Throws StreemException for query execution errors, returns empty Optional if not found
```

### Relation Management Methods

#### Method: getAllObjectTypeRelations(String objectTypeId, int usageStatus, String name, Pageable pageable)
```yaml
Signature: Page<Relation> getAllObjectTypeRelations(String objectTypeId, int usageStatus, String name, Pageable pageable) throws StreemException
Purpose: "Get object type relations with filtering and pagination for relation management"

Business Logic Derivation:
  1. Used in ObjectTypeService for relation retrieval during object type relation management and relation listing operations
  2. Provides relation access for object type workflows enabling comprehensive relation management and object type functionality
  3. Critical for relation management operations requiring relation access for object type management and relation control
  4. Used in relation management workflows for accessing object type relations for relation operations and object type processing
  5. Enables relation management with filtered relation access for comprehensive object type processing and relation control

MongoDB Query: |
  db.objectTypes.aggregate([
    { $match: { _id: ObjectId(objectTypeId) } },
    { $unwind: "$relations" },
    { $match: {
        "relations.usageStatus": usageStatus,
        "relations.displayName": { $regex: name, $options: "i" }
      }
    },
    { $skip: skip },
    { $limit: limit }
  ])

Parameters:
  - objectTypeId: String (Object type identifier for relation retrieval)
  - usageStatus: int (Usage status filter for relation filtering)
  - name: String (Name filter for relation search)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<Relation> (paginated filtered relations)
Transaction: Not Required (MongoDB operations)
Error Handling: Throws StreemException for filter parsing or aggregation errors
```

### Persistence Methods

#### Method: save(ObjectType objectType)
```yaml
Signature: void save(ObjectType objectType)
Purpose: "Save object type for object type persistence and configuration management"

Business Logic Derivation:
  1. Used extensively in ObjectTypeService for object type persistence during object type creation, updates, and configuration operations
  2. Provides object type persistence for configuration workflows enabling comprehensive object type management and configuration functionality
  3. Critical for configuration operations requiring object type persistence for configuration management and object type control
  4. Used in configuration workflows for object type persistence and configuration management operations
  5. Enables configuration management with object type persistence for comprehensive configuration processing and object type control

MongoDB Query: |
  db.objectTypes.save(objectType) // Insert or update based on _id presence

Parameters:
  - objectType: ObjectType (Object type entity for persistence)

Returns: void
Transaction: Not Required (MongoDB operations)
Error Handling: Throws MongoDB exceptions for persistence failures, validation errors
```

### Key Repository Usage Patterns

#### Pattern: Object Type Configuration Management
```yaml
Usage: Complete object type configuration for object type management and definition functionality
Purpose: "Manage object type configurations for comprehensive object type functionality and definition processing"

Business Logic Derivation:
  1. Object type configuration management provides definition functionality through object type creation, property management, and relation configuration operations
  2. Object type lifecycle includes definition creation, property configuration, and relation management for object type control
  3. Object type management operations require configuration processing for object type lifecycle and definition control
  4. Configuration operations enable comprehensive object type functionality with definition capabilities and management
  5. Object type lifecycle management supports configuration requirements and functionality for object type definition processing

Common Usage Examples:
  - objectTypeRepository.findById() across multiple services for object type validation
  - objectTypeRepository.save() for object type creation and updates
  - objectTypeRepository.getAllObjectTypeProperties() for property management
  - objectTypeRepository.getAllObjectTypeRelations() for relation management
  - objectTypeRepository.findAll() for system configuration and migration

Transaction: Not Required (MongoDB document-level consistency)
Error Handling: Object type configuration error handling and definition validation verification
```

#### Pattern: Dynamic Schema Management
```yaml
Usage: Dynamic schema management for entity object configuration and metadata management functionality
Purpose: "Manage dynamic schemas for comprehensive entity object functionality and metadata processing"

Business Logic Derivation:
  1. Dynamic schema management operations require comprehensive object type access for entity-level configuration management and schema functionality
  2. Schema management supports entity requirements and functionality for entity object processing workflows
  3. Entity-level schema operations ensure proper entity functionality through object type management and schema control
  4. Entity workflows coordinate schema management with entity processing for comprehensive entity operations
  5. Schema management supports entity requirements and functionality for comprehensive entity schema management

Common Usage Examples:
  - objectTypeRepository.findById() for entity object validation and schema verification
  - Property and relation management for dynamic entity configuration
  - Object type validation for entity object creation and management

Transaction: Not Required (MongoDB operations)
Error Handling: Schema management error handling and entity validation verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Entity Object Configuration
```yaml
Usage: Entity object configuration and validation for entity management and object functionality
Purpose: "Configure entity objects for comprehensive entity functionality and object processing"

Business Logic Derivation:
  1. Entity object configuration provides entity functionality through object type validation, property verification, and schema management operations
  2. Entity lifecycle includes object type validation, property configuration, and schema verification for entity control
  3. Entity management operations require object type processing for entity lifecycle and configuration control
  4. Entity operations enable comprehensive configuration functionality with object type capabilities and management
  5. Entity lifecycle management supports configuration requirements and functionality for entity object processing

Common Usage Examples:
  - Entity object creation with object type validation
  - Property value validation against object type property definitions
  - Relation validation for entity object associations
  - Dynamic schema enforcement for entity object management

Transaction: Not Required for validation operations
Error Handling: Entity configuration error handling and object type validation verification
```

### Pattern: Parameter and Automation Integration
```yaml
Usage: Parameter and automation configuration with object type integration and property management functionality
Purpose: "Integrate parameters and automation for comprehensive configuration functionality and processing"

Business Logic Derivation:
  1. Parameter and automation integration operations require comprehensive object type access for automation-level configuration management and parameter functionality
  2. Integration management supports automation requirements and functionality for parameter processing workflows
  3. Automation-level integration operations ensure proper automation functionality through object type management and integration control
  4. Automation workflows coordinate integration management with parameter processing for comprehensive automation operations
  5. Integration management supports automation requirements and functionality for comprehensive automation integration management

Common Usage Examples:
  - objectTypeRepository.findPropertyByIdAndObjectTypeExternalId() for parameter property validation
  - Object type property access for automation configuration
  - Property validation for parameter metadata verification

Transaction: Not Required for configuration operations
Error Handling: Integration error handling and property validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findAll, findById, getAllObjectTypeProperties, getAllObjectTypeRelations, findPropertyByIdAndObjectTypeExternalId

Transactional Methods:
  - save (MongoDB document-level consistency)

Isolation Level: MongoDB default consistency
Timeout: MongoDB default timeout
Rollback: MongoDB document-level operations
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - StreemException: Custom application exceptions for filtering, aggregation, and query errors
  - MongoException: MongoDB connection and operation failures
  - DocumentNotFoundException: Object type not found for specified criteria
  - InvalidFilterException: Invalid filter parameters or query construction errors
  - AggregationException: MongoDB aggregation pipeline errors

Validation Rules:
  - id: MongoDB ObjectId format for object type identification
  - objectTypeId: Valid object type identifier for property and relation queries
  - objectTypeExternalId: Valid external identifier for property validation
  - propertyId: Valid ObjectId for property identification
  - usageStatus: Valid integer status value for filtering
  - name: String for name-based filtering (can be null/empty)
  - filters: Valid filter string for complex query construction
  - pageable: Valid pagination parameters

Business Constraints:
  - Object type must exist for property and relation operations
  - Properties and relations must maintain referential integrity within object type
  - Usage status values must correspond to valid enum values
  - Property and relation filtering must respect embedded document structure
  - Object type persistence must maintain schema consistency
  - External ID references must be unique within system context
  - Embedded property and relation arrays must maintain consistency
  - Object type versioning must be maintained for schema evolution
  - Collection name associations must be valid for object type context
```

## MongoDB Object Type Collection Considerations

### Document Structure and Schema
```yaml
Document Design: Complex document with embedded property and relation arrays for comprehensive object type definition
Index Strategy: Primary key index on _id, potential compound indexes on externalId and collection fields
Query Patterns: Single document retrieval, complex aggregation for embedded array filtering
Performance: Optimized for object type definition management with embedded schema definitions
Scalability: Horizontal scaling support through MongoDB sharding on object type ID
```

### Property and Relation Management
```yaml
Embedded Arrays: Properties and relations stored as embedded arrays within object type documents
Schema Evolution: Object type versioning support for schema changes and property evolution
Property Validation: Embedded property definitions enable dynamic validation and constraint enforcement
Relation Management: Embedded relation definitions support complex object relationships and associations
Dynamic Schema: Object type definitions enable runtime schema configuration and entity flexibility
```

### Entity Object Integration
```yaml
Schema Validation: Object type definitions provide schema validation for entity object creation and management
Property Enforcement: Property definitions enable dynamic property validation and constraint enforcement
Relation Validation: Relation definitions support entity object association validation and integrity
Metadata Management: Object type metadata enables comprehensive entity object configuration and management
Dynamic Configuration: Runtime object type configuration supports flexible entity object management
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the ObjectType repository without MongoDB dependencies, focusing on dynamic schema management and object type configuration patterns.
