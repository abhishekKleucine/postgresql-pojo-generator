# IQRParserRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: QRParser (MongoDB Document)
- **Primary Purpose**: Manages MongoDB QR parser entities for QR code parsing configuration management with parsing rule tracking, data extraction functionality, and QR code processing
- **Key Relationships**: References ObjectType entities for comprehensive QR parser configuration and object type integration
- **Performance Characteristics**: Low query volume with QR parser retrieval, parsing configuration operations, and QR code processing management
- **Business Context**: QR code processing component that provides QR code parsing configuration, data extraction rules, delimiter management, and QR processing functionality for data capture workflows

## Entity Mapping Documentation

### Field Mappings (MongoDB Document)

| MongoDB Field | Java Field | Type | Required | Index | Notes |
|---|---|---|---|---|---|
| _id | id | ObjectId | true | primary | MongoDB primary key |
| displayName | displayName | String | false | false | Human-readable parser name |
| externalId | externalId | String | false | compound | External identifier for parser |
| objectTypeId | objectTypeId | String | false | compound | Associated object type identifier |
| rawData | rawData | String | false | false | Raw QR data for parsing |
| delimiter | delimiter | String | false | false | Data delimiter for parsing |
| rules | rules | List<SplitDataRuleDto> | false | false | Embedded parsing rules |
| createdAt | createdAt | Long | false | false | Creation timestamp |
| modifiedAt | modifiedAt | Long | false | false | Modification timestamp |
| createdBy | createdBy | UserAuditDto | false | false | Creator user information |
| modifiedBy | modifiedBy | UserAuditDto | false | false | Modifier user information |
| usageStatus | usageStatus | int | false | false | Usage status (see UsageStatus enum) |

### MongoDB Configuration
- **Collection Name**: Defined by CollectionName.QR_PARSER constant
- **Compound Index**: Unique constraint on (externalId, objectTypeId) for parser uniqueness
- **Document Structure**: Complex document with embedded rules array for parsing configuration

## Available Repository Methods

### Standard MongoRepository Methods
- `findById(String id)`
- `findAll()`
- `save(QRParser entity)`
- `saveAll(Iterable<QRParser> entities)`
- `deleteById(String id)`
- `delete(QRParser entity)`
- `existsById(String id)`
- `count()`

### Custom Query Methods (1 method - ALL methods documented)

**QR Parser Retrieval Methods (1 method):**
- `findByObjectTypeIdAndExternalId(String objectTypeId, String externalId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard MongoRepository Operations
```java
// Standard MongoRepository methods
Optional<QRParser> findById(String id)
List<QRParser> findAll()
QRParser save(QRParser entity)
List<QRParser> saveAll(Iterable<QRParser> entities)
void deleteById(String id)
void delete(QRParser entity)
boolean existsById(String id)
long count()
```

### QR Parser Retrieval Methods

#### Method: findByObjectTypeIdAndExternalId(String objectTypeId, String externalId)
```yaml
Signature: QRParser findByObjectTypeIdAndExternalId(String objectTypeId, String externalId)
Purpose: "Find QR parser by object type ID and external ID for parser validation and retrieval"

Business Logic Derivation:
  1. Used in QRParserService for QR parser validation during parser creation and duplicate checking operations
  2. Provides QR parser uniqueness validation for parsing workflows enabling comprehensive parser management and validation functionality
  3. Critical for parser creation operations requiring duplicate validation for parser management and uniqueness control
  4. Used in parser workflows for accessing existing parsers for validation operations and parser processing
  5. Enables parser management with uniqueness validation for comprehensive parser processing and validation control

MongoDB Query: |
  db.qrParsers.findOne({
    objectTypeId: objectTypeId,
    externalId: externalId
  })

Parameters:
  - objectTypeId: String (Object type identifier for parser context)
  - externalId: String (External identifier for parser uniqueness checking)

Returns: QRParser (QR parser if found, null if not found)
Transaction: Not Required (MongoDB operations)
Error Handling: Returns null if no QR parser found for object type and external ID combination
```

### Key Repository Usage Patterns

#### Pattern: save() for QR Parser Management
```yaml
Usage: qrParserRepository.save(qrParser)
Purpose: "Create and update QR parsers for QR code parsing configuration and rule management"

Business Logic Derivation:
  1. Used extensively in QRParserService for QR parser persistence during parser creation, updates, rule management, and status operations
  2. Provides QR parser persistence for parsing workflows enabling comprehensive parser management and configuration functionality
  3. Critical for parser configuration operations requiring QR parser persistence for configuration management and parser control
  4. Used in parser configuration workflows for QR parser persistence and configuration management operations
  5. Enables parser configuration with QR parser persistence for comprehensive configuration processing and parser control

Transaction: Not Required (MongoDB document-level consistency)
Error Handling: MongoDB exceptions for persistence failures, validation errors
```

#### Pattern: findById() for QR Parser Retrieval
```yaml
Usage: qrParserRepository.findById(id)
Purpose: "Retrieve QR parsers by ID for parser configuration and status management"

Business Logic Derivation:
  1. Used in QRParserService for QR parser retrieval during parser activation, archiving, and rule update operations
  2. Provides QR parser access for configuration workflows enabling comprehensive parser configuration and management functionality
  3. Critical for parser management operations requiring parser access for configuration management and parser control
  4. Used in parser management workflows for accessing QR parsers for configuration operations and parser processing
  5. Enables parser management with QR parser access for comprehensive parser processing and configuration control

Transaction: Not Required (MongoDB read operations)
Error Handling: StreemException when QR parser not found for specified ID
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: QR Parser Configuration Management
```yaml
Usage: Complete QR parser configuration for parser management and QR code processing functionality
Purpose: "Manage QR parser configurations for comprehensive parsing functionality and QR processing"

Business Logic Derivation:
  1. QR parser configuration management provides parsing functionality through parser creation, rule management, and configuration operations
  2. Parser lifecycle includes parser creation, rule configuration, and status management for parser control
  3. Parser management operations require configuration processing for parser lifecycle and parsing control
  4. Configuration operations enable comprehensive parser functionality with parsing capabilities and management
  5. Parser lifecycle management supports configuration requirements and functionality for QR parser processing

Common Usage Examples:
  - qrParserRepository.findByObjectTypeIdAndExternalId() for duplicate checking during parser creation
  - qrParserRepository.save() for parser creation, activation, archiving, and rule updates
  - qrParserRepository.findById() for parser retrieval in all QRParserService operations
  - QR parser status management with usage status updates
  - Parser rule configuration with embedded SplitDataRuleDto management

Transaction: Not Required (MongoDB document-level consistency)
Error Handling: QR parser configuration error handling and validation verification
```

### Pattern: QR Code Processing and Data Extraction
```yaml
Usage: QR code processing and data extraction for parsing management and data capture functionality
Purpose: "Process QR codes for comprehensive data extraction functionality and parsing processing"

Business Logic Derivation:
  1. QR code processing management operations require comprehensive QR parser access for processing-level parsing management and extraction functionality
  2. Processing management supports extraction requirements and functionality for data capture workflows
  3. Processing-level parser operations ensure proper extraction functionality through parser management and processing control
  4. Data workflows coordinate processing management with parser processing for comprehensive data operations
  5. Processing management supports extraction requirements and functionality for comprehensive QR processing management

Common Usage Examples:
  - qrParserRepository.findByObjectTypeIdAndExternalId() for parser configuration retrieval
  - QR parser rule application for data extraction and parsing
  - Delimiter-based data splitting for QR code processing
  - Object type integration for parsed data validation

Transaction: Not Required for processing operations
Error Handling: Processing error handling and parser validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByObjectTypeIdAndExternalId, existsById, count

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
  - DocumentNotFoundException: QR parser not found for specified criteria
  - DuplicateParserException: Duplicate QR parser for object type and external ID combination
  - InvalidConfigurationException: Invalid parsing rules or configuration
  - StreemException: Custom application exceptions for parser not found scenarios

Validation Rules:
  - id: ObjectId identifier for QR parser identification
  - objectTypeId: Valid object type identifier for parser context
  - externalId: String identifier for parser naming and uniqueness
  - displayName: String for parser naming and identification
  - rawData: String containing raw QR data for parsing
  - delimiter: String defining data delimiter for parsing rules
  - rules: Valid SplitDataRuleDto array for parsing configuration
  - usageStatus: Valid integer status value for parser lifecycle management

Business Constraints:
  - QR parser external ID must be unique within object type context (compound unique index)
  - Object type reference should be valid for proper parser context
  - Parsing rules must be valid for data extraction functionality
  - Delimiter configuration must support parsing requirements
  - QR parser configuration must maintain referential integrity
  - Parser lifecycle must support usage status transitions (ACTIVE, DEPRECATED)
  - Raw data must be valid for parsing rule application
  - Embedded rules array must maintain consistency
  - Audit fields must be maintained for change tracking
  - External ID generation must follow camelCase conventions from display name
```

## MongoDB QR Parser Collection Considerations

### Document Structure and Configuration
```yaml
Document Design: Complex document with embedded rules array for comprehensive parsing definition
Index Strategy: Primary key index on _id, compound unique index on (externalId, objectTypeId) for uniqueness
Query Patterns: Single document retrieval, composite key lookup for duplicate checking
Performance: Optimized for QR parser configuration with embedded parsing rules
Scalability: Horizontal scaling support through MongoDB sharding on parser ID
```

### Parsing Rules and Configuration Management
```yaml
Embedded Arrays: Parsing rules stored as embedded arrays within QR parser documents
Rule Configuration: QR parser definitions enable dynamic parsing rule configuration
Data Extraction: Embedded rule definitions support flexible QR code data extraction
Delimiter Management: QR parser configuration enables delimiter-based data splitting
Parsing Flexibility: QR parser definitions support comprehensive data parsing customization
```

### Object Type Integration
```yaml
Schema Integration: QR parser definitions integrate with object type schemas for validation
Data Mapping: Parsing rules enable data mapping to object type properties for validation
Parser Validation: QR parser configuration validates against object type requirements
Data Processing: QR parsers enable structured data extraction for object type data capture
Configuration Management: QR parser management supports object type schema evolution and parser updates
```

### Usage Status Lifecycle
```yaml
Status Management: QR parser lifecycle includes ACTIVE and DEPRECATED status transitions
Activation: Parser activation enables QR code processing and data extraction
Archiving: Parser archiving (DEPRECATED) disables processing while maintaining configuration
Lifecycle Control: Status management enables parser versioning and configuration evolution
State Transitions: Usage status transitions support parser lifecycle management and version control
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the QRParser repository without MongoDB dependencies, focusing on QR code parsing configuration and data extraction patterns.
