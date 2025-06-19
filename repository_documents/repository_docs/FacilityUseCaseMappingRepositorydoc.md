# IFacilityUseCaseMappingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: FacilityUseCaseMapping
- **Primary Purpose**: Manages facility-use case mapping entities for facility-scoped use case associations with quota management and access control validation
- **Key Relationships**: Mapping entity linking Facility and UseCase with many-to-one relationships using composite key for facility-use case association management
- **Performance Characteristics**: Moderate query volume with facility-use case validation, specification-based filtering, and association management
- **Business Context**: Facility-use case association component that provides facility-scoped use case access, quota management, use case validation, and multi-tenant access control for facility-based use case permissions and capacity management

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| facilities_id | facilityUseCaseId.facilityId / facilityId | Long | false | part of composite key |
| use_cases_id | facilityUseCaseId.useCaseId / useCaseId | Long | false | part of composite key |
| quota | quota | Integer | false | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | true | null |
| modified_by | modifiedBy.id | Long | true | null |

### Composite Key Structure
- **FacilityUseCaseCompositeKey**: Composite key containing facilityId and useCaseId for unique facility-use case associations

### Entity Constants
- **FACILITY_ID**: "facilityId" - Facility identifier constant for filtering and validation
- **ORGANISATION_ID**: "organisationId" - Organization identifier constant for multi-tenant operations

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne | facility | Facility | LAZY | Associated facility, not null, immutable |
| @ManyToOne | useCase | UseCase | EAGER | Associated use case, not null, immutable |

## Available Repository Methods

### Standard CRUD Methods
- `findById(FacilityUseCaseCompositeKey id)`
- `findAll()`
- `save(FacilityUseCaseMapping entity)`
- `deleteById(FacilityUseCaseCompositeKey id)`
- `delete(FacilityUseCaseMapping entity)`
- `existsById(FacilityUseCaseCompositeKey id)`
- `count()`

### JpaSpecificationExecutor Methods
- `findAll(Specification<FacilityUseCaseMapping> spec)`
- `findAll(Specification<FacilityUseCaseMapping> spec, Pageable pageable)`
- `findAll(Specification<FacilityUseCaseMapping> spec, Sort sort)`
- `findOne(Specification<FacilityUseCaseMapping> spec)`
- `count(Specification<FacilityUseCaseMapping> spec)`

### Custom Query Methods (1 method - ALL methods documented)

- `findByFacilityIdAndUseCaseId(Long facilityId, Long useCaseId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods with composite key
Optional<FacilityUseCaseMapping> findById(FacilityUseCaseCompositeKey id)
List<FacilityUseCaseMapping> findAll()
FacilityUseCaseMapping save(FacilityUseCaseMapping entity)
void deleteById(FacilityUseCaseCompositeKey id)
void delete(FacilityUseCaseMapping entity)
boolean existsById(FacilityUseCaseCompositeKey id)
long count()
```

### JpaSpecificationExecutor Operations
```java
// Dynamic query support with specifications
List<FacilityUseCaseMapping> findAll(Specification<FacilityUseCaseMapping> spec)
Page<FacilityUseCaseMapping> findAll(Specification<FacilityUseCaseMapping> spec, Pageable pageable)
List<FacilityUseCaseMapping> findAll(Specification<FacilityUseCaseMapping> spec, Sort sort)
Optional<FacilityUseCaseMapping> findOne(Specification<FacilityUseCaseMapping> spec)
long count(Specification<FacilityUseCaseMapping> spec)
```

### Custom Query Methods

#### Method: findByFacilityIdAndUseCaseId(Long facilityId, Long useCaseId)
```yaml
Signature: FacilityUseCaseMapping findByFacilityIdAndUseCaseId(Long facilityId, Long useCaseId)
Purpose: "Find facility-use case mapping for specific facility and use case for validation and access control"

Business Logic Derivation:
  1. Used extensively in ChecklistService, CustomViewService, and ImportExportChecklistService for facility-use case validation
  2. Provides facility-use case association validation for access control and permission verification operations
  3. Critical for validation operations requiring facility-use case association verification for multi-tenant access control
  4. Used in checklist creation and custom view workflows for validating facility-use case permissions and associations
  5. Enables facility-scoped use case validation with association verification for comprehensive access control and permission management

SQL Query: |
  SELECT fucm.* FROM facility_use_case_mapping fucm
  WHERE fucm.facilities_id = ? AND fucm.use_cases_id = ?

Parameters:
  - facilityId: Long (Facility identifier for facility-scoped validation)
  - useCaseId: Long (Use case identifier for use case association validation)

Returns: FacilityUseCaseMapping (facility-use case mapping if association exists, null otherwise)
Transaction: Not Required
Error Handling: Returns null if no mapping found for facility and use case combination
```

### Key Repository Usage Patterns

#### Pattern: save() for Facility-Use Case Association Management
```yaml
Usage: facilityUseCaseMappingRepository.save(mapping)
Purpose: "Create and update facility-use case associations with quota management and access control"

Business Logic Derivation:
  1. Used for facility-use case association creation with quota configuration and access control management
  2. Provides facility-use case association persistence with quota management and multi-tenant access control
  3. Critical for facility-use case lifecycle management and access control operations requiring association management
  4. Used in facility-use case management workflows for association creation and quota configuration
  5. Enables facility-use case association management with comprehensive quota control and access permission management

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations, composite key conflicts
```

#### Pattern: Specification-Based Facility-Use Case Discovery
```yaml
Usage: findAll(specification) for advanced facility-use case filtering and discovery
Purpose: "Advanced facility-use case mapping discovery with complex filtering for use case management"

Business Logic Derivation:
  1. Used in UseCaseService for advanced facility-use case mapping discovery with specification-based filtering
  2. Provides complex facility-use case filtering capabilities for use case management and access control operations
  3. Critical for use case discovery operations requiring facility-scoped filtering and multi-tenant access control
  4. Used in use case listing workflows requiring facility-scoped use case discovery and permission management
  5. Enables advanced facility-use case discovery with complex filtering for comprehensive use case management operations

Transaction: Not Required
Error Handling: Returns empty list for specifications with no matching mappings
```

#### Pattern: Facility-Use Case Validation for Access Control
```yaml
Usage: findByFacilityIdAndUseCaseId() for permission validation and access control
Purpose: "Validate facility-use case associations for access control and permission verification"

Business Logic Derivation:
  1. Facility-use case validation enables proper access control and permission verification for multi-tenant operations
  2. Association validation ensures users can only access use cases available in their facility context
  3. Access control validation prevents unauthorized use case access and maintains multi-tenant data isolation
  4. Permission verification supports workflow operations requiring facility-use case association validation
  5. Multi-tenant access control depends on facility-use case association validation for proper permission management

Transaction: Not Required
Error Handling: Null validation for non-existent associations, access control verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Facility-Scoped Use Case Discovery and Management
```yaml
Usage: Specification-based use case discovery within facility context
Purpose: "Discover and manage use cases available within specific facility context for multi-tenant operations"

Business Logic Derivation:
  1. Facility-scoped use case discovery enables multi-tenant use case management with proper facility isolation
  2. Use case listing within facility context ensures users only see use cases available in their facility
  3. Facility-use case mapping enables proper use case organization and facility-specific use case management
  4. Use case ordering and filtering within facility context supports user interface and use case management operations
  5. Multi-tenant use case discovery maintains data isolation and access control through facility-use case associations

Common Usage Examples:
  - facilityUseCaseMappingRepository.findAll(specification) in UseCaseService for facility-scoped use case discovery
  - Use case listing with facility context for multi-tenant use case management and access control
  - Facility-scoped use case filtering for user interface and use case management operations
  - Use case ordering within facility context for proper use case organization and management
  - Multi-tenant use case discovery with facility isolation and access control validation

Transaction: Not Required
Error Handling: Empty list handling for facilities without use case associations
```

### Pattern: Use Case Access Validation for Workflow Operations
```yaml
Usage: Facility-use case validation for checklist, custom view, and import operations
Purpose: "Validate use case access within facility context for workflow operations and access control"

Business Logic Derivation:
  1. Use case access validation ensures workflow operations only proceed with valid facility-use case associations
  2. Checklist creation requires facility-use case validation to ensure proper access control and permission verification
  3. Custom view operations require use case validation within facility context for proper data access and security
  4. Import/export operations require facility-use case validation for data integrity and access control
  5. Workflow validation depends on facility-use case association verification for proper operation authorization

Common Usage Examples:
  - facilityUseCaseMappingRepository.findByFacilityIdAndUseCaseId() in ChecklistService for checklist creation validation
  - facilityUseCaseMappingRepository.findByFacilityIdAndUseCaseId() in CustomViewService for custom view access validation
  - facilityUseCaseMappingRepository.findByFacilityIdAndUseCaseId() in ImportExportChecklistService for import validation
  - Use case access validation for workflow operations and permission verification
  - Facility-use case association verification for multi-tenant access control and data integrity

Transaction: Not Required for validation operations
Error Handling: Null validation handling for invalid associations, access control verification
```

### Pattern: Quota Management and Capacity Control
```yaml
Usage: Facility-use case mappings for quota management and capacity control
Purpose: "Manage facility-use case quotas for capacity control and resource management"

Business Logic Derivation:
  1. Facility-use case mappings include quota configuration for capacity management and resource control
  2. Quota management enables facility-specific capacity control and resource allocation for use cases
  3. Capacity control supports business requirements for facility-specific use case limitations and management
  4. Resource management through quotas enables proper facility utilization and capacity planning
  5. Use case capacity control maintains facility resource management and operational efficiency

Common Quota Management Patterns:
  - Quota configuration for facility-use case associations and capacity management
  - Capacity control through facility-use case quota management and resource allocation
  - Resource management with facility-specific use case limitations and quota enforcement
  - Facility utilization management through use case quota configuration and capacity control
  - Operational efficiency through proper quota management and resource allocation

Transaction: Required for quota configuration and management operations
Error Handling: Quota validation and capacity constraint verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByFacilityIdAndUseCaseId, findAll(Specification)
  - existsById, count, findOne(Specification), count(Specification)

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
    * Composite key constraint violations (duplicate facility-use case combinations)
    * NOT NULL constraint violations (facilities_id, use_cases_id, quota)
    * Foreign key violations (invalid facilities_id, use_cases_id references)
    * Unique constraint violations on composite key
  - EntityNotFoundException: Facility-use case mapping not found by composite key or criteria
  - OptimisticLockException: Concurrent facility-use case mapping modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or composite key operations
  - ConstraintViolationException: Facility-use case association constraint violations

Validation Rules:
  - facilityUseCaseId: Required, composite key containing facilityId and useCaseId for unique associations
  - facility: Required, must reference existing facility, immutable after creation
  - facilityId: Derived from composite key and facility relationship for facility-scoped operations
  - useCase: Required, must reference existing use case, immutable after creation, EAGER fetch
  - useCaseId: Derived from composite key and use case relationship for use case association operations
  - quota: Required, integer for capacity management and resource allocation

Business Constraints:
  - Facility-use case associations must be unique for proper association management and data integrity
  - Facility and use case references must be valid for association integrity and access control
  - Quota values must be positive for proper capacity management and resource allocation
  - Facility-use case mappings must support multi-tenant access control and data isolation
  - Association lifecycle management must maintain referential integrity and access control consistency
  - Quota management must consider business requirements and capacity planning needs
  - Access control validation must ensure proper facility-use case permission verification
  - Multi-tenant isolation must be maintained through proper facility-use case association management
  - Resource allocation through quotas must support operational efficiency and capacity management
  - Association management must maintain workflow integrity and access control requirements
```

## Facility-Use Case Mapping Considerations

### Multi-Tenant Access Control
```yaml
Facility Scoping: Facility-use case mappings enable multi-tenant access control and data isolation
Association Validation: Facility-use case validation ensures proper access control and permission verification
Use Case Discovery: Facility-scoped use case discovery maintains multi-tenant data isolation and access control
Permission Management: Association-based permission management for workflow operations and access control
Access Verification: Comprehensive access verification through facility-use case association validation
```

### Capacity Management
```yaml
Quota Configuration: Quota management for facility-specific capacity control and resource allocation
Resource Allocation: Facility-use case quota management for operational efficiency and capacity planning
Capacity Control: Use case capacity limitations through quota configuration and management
Utilization Management: Facility utilization control through use case quota enforcement and monitoring
Operational Efficiency: Proper capacity management through quota configuration and resource allocation
```

### Workflow Integration
```yaml
Access Validation: Facility-use case validation for workflow operations and permission verification
Checklist Integration: Use case validation for checklist creation and management workflows
Custom View Support: Facility-use case validation for custom view operations and access control
Import/Export Control: Association validation for import/export operations and data integrity
Workflow Authorization: Comprehensive workflow authorization through facility-use case validation
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the FacilityUseCaseMapping repository without JPA/Hibernate dependencies, focusing on multi-tenant access control and facility-use case association management patterns.
