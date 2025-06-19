# IEntityObjectRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: EntityObject (MongoDB Collections - Dynamic)
- **Primary Purpose**: Manages dynamic entity objects across multiple MongoDB collections with property values, relations, usage status management, and facility scoping
- **Key Relationships**: Dynamic MongoDB collections entity with embedded PropertyValue, MappedRelation objects and ObjectType associations for flexible data modeling
- **Performance Characteristics**: High query volume with complex filtering, property-based searches, partial object retrieval, and bulk operations
- **Business Context**: Core dynamic data management component that provides flexible entity storage, property management, relation handling, and facility-scoped data operations for configurable business objects

## Entity Mapping Documentation

### Field Mappings (MongoDB Document - Dynamic Collections)

| MongoDB Field | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| _id | id | ObjectId | false | auto-generated |
| objectTypeId | objectTypeId | ObjectId | true | null |
| version | version | Integer | true | null |
| collection | collection | String | true | null |
| displayName | displayName | String | true | null |
| externalId | externalId | String | true | null |
| objectType | objectType | PartialObjectType | true | null |
| properties | properties | List\<PropertyValue\> | false | empty list |
| relations | relations | List\<MappedRelation\> | false | empty list |
| modifiedAt | modifiedAt | Long | true | null |
| createdAt | createdAt | Long | true | null |
| createdBy | createdBy | UserInfo | true | null |
| modifiedBy | modifiedBy | UserInfo | true | null |
| usageStatus | usageStatus | int | false | 0 |
| facilityId | facilityId | String | true | null |
| shortCode | shortCode | String | true | null |
| searchable | searchable | Map\<ObjectId, Object\> | false | empty map |

### Embedded Objects
- **PropertyValue**: Property values for dynamic object properties
- **MappedRelation**: Relation mappings for object relationships
- **PartialObjectType**: Object type information for type context
- **UserInfo**: User information for audit tracking

### Relationships
None - MongoDB document with embedded objects and dynamic collection structure.

## Available Repository Methods

### Custom Repository Interface
Note: This repository does not extend any Spring repository - it's a fully custom interface with specialized entity object management methods.

### Custom Query Methods (11 methods - ALL methods documented)

- `findById(String collectionName, String id)`
- `findByObjectTypeId(String collectionName, String id)`
- `findAll(String collectionName)`
- `findPartialById(String collectionName, String id)`
- `findByIds(String collectionName, List<String> ids)`
- `findPartialByIds(String collectionName, List<String> ids)`
- `findPartialByIdsAndUsageStatus(String collectionName, List<String> ids, int usageStatus)`
- `findAllByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, String filters, Pageable pageable)`
- `findPartialByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, String filters, String query, Pageable pageable)`
- `save(EntityObject entityObject, String id)`
- `saveAll(List<EntityObject> entityObjects, String collectionName)`
- `findByExternalIdAndUsageStatusAndFacilityId(String collectionName, String externalId, int usageStatus, String facilityId)`

## Method Documentation (All Custom Methods - Full Detail)

### Custom Query Methods

#### Method: findById(String collectionName, String id)
```yaml
Signature: Optional<EntityObject> findById(String collectionName, String id)
Purpose: "Find entity object by ID in specific collection for entity context operations and object retrieval"

Business Logic Derivation:
  1. Used extensively across services for entity object context retrieval in parameter validation, interlock operations, and object management
  2. Provides primary entity object lookup for specific collections enabling dynamic object access and validation
  3. Critical for entity object validation operations requiring complete object information and property access
  4. Used in object-specific workflows including parameter execution, property validation, and relation management
  5. Enables collection-specific object retrieval with complete entity information for business logic operations

MongoDB Query: |
  db.[collectionName].findOne({"_id": ObjectId(?)})

Parameters:
  - collectionName: String (Dynamic collection name for object type-specific storage)
  - id: String (Entity object identifier for specific object retrieval)

Returns: Optional<EntityObject> (complete entity object with properties and relations)
Transaction: Not Required (MongoDB single document operations)
Error Handling: Returns empty Optional if entity object not found in collection
```

#### Method: findByObjectTypeId(String collectionName, String id)
```yaml
Signature: List<EntityObject> findByObjectTypeId(String collectionName, String id)
Purpose: "Find all entity objects by object type ID for type-specific operations and bulk object management"

Business Logic Derivation:
  1. Used in ObjectTypeService for type-specific entity object operations during object type updates and property management
  2. Provides complete entity object listing for object types enabling bulk operations and type-specific management
  3. Critical for object type lifecycle operations requiring all entity objects of a specific type for updates
  4. Used in object type property and relation update workflows requiring bulk entity object modifications
  5. Enables type-specific object management with complete object information for bulk operations and type updates

MongoDB Query: |
  db.[collectionName].find({"objectTypeId": ObjectId(?)})

Parameters:
  - collectionName: String (Dynamic collection name for object type-specific storage)
  - id: String (Object type identifier for type-specific object retrieval)

Returns: List<EntityObject> (all entity objects of the specified object type)
Transaction: Not Required
Error Handling: Returns empty list if no entity objects found for object type
```

#### Method: findAll(String collectionName)
```yaml
Signature: List<EntityObject> findAll(String collectionName)
Purpose: "Find all entity objects in collection for export operations and collection-wide management"

Business Logic Derivation:
  1. Used in EntityObjectExportImportService and EntityObjectService for collection-wide operations and data export
  2. Provides complete collection listing for export operations, migration workflows, and collection management
  3. Critical for data export operations requiring all entity objects in a collection for external processing
  4. Used in migration workflows and collection management operations requiring complete object access
  5. Enables collection-wide operations with complete entity object information for export and management workflows

MongoDB Query: |
  db.[collectionName].find({})

Parameters:
  - collectionName: String (Dynamic collection name for complete collection retrieval)

Returns: List<EntityObject> (all entity objects in the specified collection)
Transaction: Not Required
Error Handling: Returns empty list if no entity objects found in collection
```

#### Method: findPartialById(String collectionName, String id)
```yaml
Signature: PartialEntityObject findPartialById(String collectionName, String id)
Purpose: "Find partial entity object by ID for performance-optimized operations requiring limited object information"

Business Logic Derivation:
  1. Used for performance-optimized entity object retrieval when complete object information is not required
  2. Provides partial object information for operations requiring minimal data transfer and reduced memory usage
  3. Critical for performance optimization in high-volume operations requiring basic object information
  4. Used in listing operations and display workflows requiring minimal object data for user interface operations
  5. Enables performance-optimized object access with reduced data transfer for efficient operations

MongoDB Query: |
  db.[collectionName].findOne({"_id": ObjectId(?)}, {projection: partial_fields})

Parameters:
  - collectionName: String (Dynamic collection name for object type-specific storage)
  - id: String (Entity object identifier for specific partial object retrieval)

Returns: PartialEntityObject (partial entity object with limited fields)
Transaction: Not Required
Error Handling: Returns null if entity object not found in collection
```

#### Method: findByIds(String collectionName, List<String> ids)
```yaml
Signature: List<EntityObject> findByIds(String collectionName, List<String> ids)
Purpose: "Find multiple entity objects by IDs for bulk object operations and batch processing"

Business Logic Derivation:
  1. Used for bulk entity object retrieval enabling efficient multi-object operations and batch processing
  2. Provides multiple complete entity objects for bulk operations requiring full object information
  3. Critical for batch operations requiring multiple entity objects with complete property and relation information
  4. Used in bulk validation workflows and multi-object processing operations requiring complete object data
  5. Enables efficient bulk object retrieval with complete entity information for batch processing workflows

MongoDB Query: |
  db.[collectionName].find({"_id": {"$in": [ObjectId(id1), ObjectId(id2), ...]}})

Parameters:
  - collectionName: String (Dynamic collection name for object type-specific storage)
  - ids: List<String> (List of entity object identifiers for bulk retrieval)

Returns: List<EntityObject> (complete entity objects matching the provided IDs)
Transaction: Not Required
Error Handling: Returns empty list if no entity objects found for provided IDs
```

#### Method: findPartialByIds(String collectionName, List<String> ids)
```yaml
Signature: List<PartialEntityObject> findPartialByIds(String collectionName, List<String> ids)
Purpose: "Find multiple partial entity objects by IDs for performance-optimized bulk operations"

Business Logic Derivation:
  1. Used in JobLogService and EntityObjectService for performance-optimized bulk entity object retrieval
  2. Provides multiple partial entity objects for bulk operations requiring minimal data transfer
  3. Critical for performance optimization in bulk operations requiring basic object information without full data
  4. Used in display workflows and listing operations requiring minimal object data for user interface operations
  5. Enables performance-optimized bulk object retrieval with reduced data transfer for efficient batch operations

MongoDB Query: |
  db.[collectionName].find({"_id": {"$in": [ObjectId(id1), ObjectId(id2), ...]}}, {projection: partial_fields})

Parameters:
  - collectionName: String (Dynamic collection name for object type-specific storage)
  - ids: List<String> (List of entity object identifiers for bulk partial retrieval)

Returns: List<PartialEntityObject> (partial entity objects matching the provided IDs)
Transaction: Not Required
Error Handling: Returns empty list if no entity objects found for provided IDs
```

#### Method: findPartialByIdsAndUsageStatus(String collectionName, List<String> ids, int usageStatus)
```yaml
Signature: List<PartialEntityObject> findPartialByIdsAndUsageStatus(String collectionName, List<String> ids, int usageStatus)
Purpose: "Find partial entity objects by IDs filtered by usage status for active object operations"

Business Logic Derivation:
  1. Used in EntityObjectService for usage status-filtered partial entity object retrieval ensuring only active objects
  2. Provides multiple partial entity objects filtered by usage status for operations requiring active object validation
  3. Critical for operations requiring active entity objects with usage status validation and performance optimization
  4. Used in validation workflows requiring active object checking with minimal data transfer for efficiency
  5. Enables usage status-filtered bulk object retrieval with performance optimization for active object operations

MongoDB Query: |
  db.[collectionName].find({
    "_id": {"$in": [ObjectId(id1), ObjectId(id2), ...]},
    "usageStatus": ?
  }, {projection: partial_fields})

Parameters:
  - collectionName: String (Dynamic collection name for object type-specific storage)
  - ids: List<String> (List of entity object identifiers for bulk retrieval)
  - usageStatus: int (Usage status filter - typically ACTIVE status)

Returns: List<PartialEntityObject> (partial entity objects matching IDs and usage status)
Transaction: Not Required
Error Handling: Returns empty list if no active entity objects found for provided IDs
```

#### Method: findAllByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, String filters, Pageable pageable)
```yaml
Signature: Page<EntityObject> findAllByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, String filters, Pageable pageable)
Purpose: "Find entity objects with complex filtering by usage status, properties, and facility for advanced search operations"

Business Logic Derivation:
  1. Used in EntityObjectService for advanced entity object search with multiple filtering criteria and pagination
  2. Provides complex filtering capabilities for entity object discovery with property, facility, and status filtering
  3. Critical for advanced search operations requiring multi-criteria filtering and facility-scoped object discovery
  4. Used in user interface search workflows requiring comprehensive filtering and pagination for object discovery
  5. Enables advanced object search with complex filtering criteria for comprehensive object management operations

MongoDB Query: |
  db.[collectionName].find({
    "usageStatus": ?,
    "facilityId": ?,
    "properties.externalId": ?,
    "properties.value": ?,
    [additional filters from filters parameter]
  }).skip(skip).limit(limit).sort(sort)

Parameters:
  - collectionName: String (Dynamic collection name for object type-specific storage)
  - usageStatus: int (Usage status filter for object lifecycle filtering)
  - propertyExternalId: String (Property external ID for property-based filtering)
  - propertyValue: String (Property value for property-based filtering)
  - facilityId: Long (Facility ID for facility-scoped object filtering)
  - filters: String (Additional filter criteria for complex filtering)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<EntityObject> (paginated entity objects matching all filter criteria)
Transaction: Not Required
Error Handling: Returns empty page if no entity objects match filter criteria
```

#### Method: findPartialByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, String filters, String query, Pageable pageable)
```yaml
Signature: Page<PartialEntityObject> findPartialByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, String filters, String query, Pageable pageable)
Purpose: "Find partial entity objects with complex filtering and text search for performance-optimized advanced search"

Business Logic Derivation:
  1. Used in EntityObjectService for performance-optimized advanced entity object search with text query capabilities
  2. Provides complex filtering with text search and partial object retrieval for efficient search operations
  3. Critical for advanced search operations requiring text search, complex filtering, and performance optimization
  4. Used in user interface search workflows requiring comprehensive search capabilities with minimal data transfer
  5. Enables advanced search with performance optimization for complex object discovery and search operations

MongoDB Query: |
  db.[collectionName].find({
    "usageStatus": ?,
    "facilityId": ?,
    "properties.externalId": ?,
    "properties.value": ?,
    "$text": {"$search": ?},
    [additional filters from filters parameter]
  }, {projection: partial_fields}).skip(skip).limit(limit).sort(sort)

Parameters:
  - collectionName: String (Dynamic collection name for object type-specific storage)
  - usageStatus: int (Usage status filter for object lifecycle filtering)
  - propertyExternalId: String (Property external ID for property-based filtering)
  - propertyValue: String (Property value for property-based filtering)
  - facilityId: Long (Facility ID for facility-scoped object filtering)
  - filters: String (Additional filter criteria for complex filtering)
  - query: String (Text search query for full-text search capabilities)
  - pageable: Pageable (Pagination and sorting configuration)

Returns: Page<PartialEntityObject> (paginated partial entity objects matching all criteria)
Transaction: Not Required
Error Handling: Returns empty page if no entity objects match search criteria
```

#### Method: save(EntityObject entityObject, String id)
```yaml
Signature: EntityObject save(EntityObject entityObject, String id)
Purpose: "Save entity object to specific collection for entity lifecycle management and data persistence"

Business Logic Derivation:
  1. Used extensively across services for entity object creation, updates, and lifecycle management operations
  2. Provides entity object persistence with collection-specific storage and complete object information management
  3. Critical for entity object lifecycle operations requiring data persistence and object state management
  4. Used in object management workflows including property updates, relation management, and object lifecycle operations
  5. Enables entity object persistence with comprehensive data management and collection-specific storage operations

MongoDB Query: |
  db.[id].save(entityObject) or
  db.[id].replaceOne({"_id": entityObject._id}, entityObject, {upsert: true})

Parameters:
  - entityObject: EntityObject (Complete entity object for persistence)
  - id: String (Collection identifier for collection-specific storage)

Returns: EntityObject (saved entity object with updated information)
Transaction: Required for consistency (MongoDB single document ACID)
Error Handling: WriteException for MongoDB constraint violations, validation errors
```

#### Method: saveAll(List<EntityObject> entityObjects, String collectionName)
```yaml
Signature: List<EntityObject> saveAll(List<EntityObject> entityObjects, String collectionName)
Purpose: "Save multiple entity objects for bulk operations and batch persistence"

Business Logic Derivation:
  1. Used in EntityObjectService and migration operations for bulk entity object persistence and batch operations
  2. Provides bulk entity object persistence for efficient batch operations and data management workflows
  3. Critical for bulk operations requiring multiple entity object persistence with transaction consistency
  4. Used in migration workflows and bulk data operations requiring efficient multi-object persistence
  5. Enables bulk entity object persistence with batch operations for efficient data management and migration workflows

MongoDB Query: |
  db.[collectionName].insertMany(entityObjects) or
  db.[collectionName].bulkWrite([...upsert operations...])

Parameters:
  - entityObjects: List<EntityObject> (List of entity objects for bulk persistence)
  - collectionName: String (Collection name for collection-specific bulk storage)

Returns: List<EntityObject> (saved entity objects with updated information)
Transaction: Required for batch consistency
Error Handling: BulkWriteException for batch operation failures, validation errors
```

#### Method: findByExternalIdAndUsageStatusAndFacilityId(String collectionName, String externalId, int usageStatus, String facilityId)
```yaml
Signature: Optional<EntityObject> findByExternalIdAndUsageStatusAndFacilityId(String collectionName, String externalId, int usageStatus, String facilityId)
Purpose: "Find entity object by external ID with usage status and facility filtering for external reference operations"

Business Logic Derivation:
  1. Used for entity object lookup by external identifier with usage status and facility validation
  2. Provides external reference-based object retrieval with facility scoping and usage status validation
  3. Critical for external integration operations requiring object lookup by external identifiers
  4. Used in external system integration workflows requiring facility-scoped object access with external references
  5. Enables external reference-based object retrieval with comprehensive validation and facility scoping

MongoDB Query: |
  db.[collectionName].findOne({
    "externalId": ?,
    "usageStatus": ?,
    "facilityId": ?
  })

Parameters:
  - collectionName: String (Dynamic collection name for object type-specific storage)
  - externalId: String (External identifier for external reference-based lookup)
  - usageStatus: int (Usage status filter for object lifecycle validation)
  - facilityId: String (Facility ID for facility-scoped access control)

Returns: Optional<EntityObject> (entity object matching external ID, usage status, and facility)
Transaction: Not Required
Error Handling: Returns empty Optional if no entity object matches all criteria
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findByObjectTypeId, findAll, findPartialById, findByIds
  - findPartialByIds, findPartialByIdsAndUsageStatus, findAllByUsageStatus
  - findPartialByUsageStatus, findByExternalIdAndUsageStatusAndFacilityId

Transactional Methods:
  - save, saveAll (MongoDB handles ACID for single/batch operations)

Isolation Level: MongoDB default (read committed equivalent)
Timeout: 60 seconds (longer for complex operations)
Rollback: MongoDB transaction rollback for multi-document operations
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - ResourceNotFoundException: Entity object not found by ID or criteria
  - WriteException: MongoDB write operation failures
  - BulkWriteException: Batch operation failures in saveAll operations
  - InvalidDataAccessApiUsageException: Invalid query parameters or collection names
  - MongoException: General MongoDB operation failures
  - ValidationException: Property or relation validation failures

Validation Rules:
  - id: Auto-generated ObjectId for MongoDB document identification
  - objectTypeId: Optional, must reference existing object type
  - collection: Must be valid collection name for dynamic storage
  - externalId: Optional, external system identifier for integration
  - properties: List of PropertyValue objects for dynamic property storage
  - relations: List of MappedRelation objects for relationship management
  - usageStatus: Integer status for object lifecycle management (ACTIVE, ARCHIVED, etc.)
  - facilityId: Optional, facility identifier for facility-scoped access control
  - searchable: Map for search optimization and full-text search capabilities

Business Constraints:
  - Entity objects must belong to valid object types for schema consistency
  - Property values must match object type property definitions for data integrity
  - Relations must reference valid target objects for relationship consistency
  - Usage status must follow defined lifecycle states for proper object management
  - Facility scoping must be enforced for multi-tenant data isolation
  - External IDs must be unique within facility scope for external integration
  - Property validation must enforce object type schema constraints
  - Relation validation must ensure referential integrity for object relationships
  - Search optimization must be maintained for performance requirements
  - Collection naming must follow conventions for dynamic storage management
```

## MongoDB-Specific Considerations

### Dynamic Collection Structure
```yaml
Collections: Dynamic based on object type external IDs
Document Type: EntityObject with embedded PropertyValue and MappedRelation arrays
Indexing Strategy:
  - Index on objectTypeId for type-based queries
  - Index on externalId for external reference lookups
  - Index on usageStatus for lifecycle filtering
  - Index on facilityId for facility-scoped operations
  - Compound index on (externalId, usageStatus, facilityId) for external lookups
  - Text index on searchable fields for full-text search
  - Index on properties.externalId for property-based filtering

Performance Optimization:
  - Embedded document structure for properties and relations reduces joins
  - Partial object projections reduce data transfer for performance
  - Compound indexes optimize complex filtering operations
  - Text search capabilities for advanced search operations
  - Pagination support for large datasets
```

### Data Consistency
```yaml
Consistency Model: Eventually consistent with MongoDB replication
Referential Integrity: Application-level enforcement for object type and relation references
Schema Validation: MongoDB schema validation for document structure and property types
Migration Strategy: Document versioning for schema evolution and object type changes
Search Optimization: Searchable field maintenance for performance and full-text search
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the EntityObject repository without MongoDB dependencies, focusing on dynamic object management and complex filtering patterns.
