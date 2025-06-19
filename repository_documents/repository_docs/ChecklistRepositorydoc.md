# IChecklistRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Checklist
- **Primary Purpose**: Manages checklist lifecycle, states, facility mappings, and core process workflow operations
- **Key Relationships**: Parent entity for stages, jobs, collaborators, property values, and relations; linked to organizations, use cases, and versions
- **Performance Characteristics**: High query volume with complex specification-based queries, pagination support, and bulk operations
- **Business Context**: Core process management entity that orchestrates multi-facility workflows, version control, collaboration, and process execution lifecycle

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| id | id | Long | false | auto-generated |
| name | name | String | true | null |
| state | state | State.Checklist | false | null |
| code | code | String | false | null |
| job_log_columns | jobLogColumns | JsonNode | false | '[]' |
| archived | archived | boolean | false | false |
| versions_id | version.id | Long | true | null |
| organisations_id | organisation.id | Long | false | null |
| use_cases_id | useCase.id | Long | false | null |
| review_cycle | reviewCycle | Integer | false | 1 |
| description | description | String | true | null |
| released_at | releasedAt | Long | true | null |
| released_by | releasedBy.id | Long | true | null |
| is_global | isGlobal | boolean | false | false |
| color_code | colorCode | String | true | null |
| created_at | createdAt | Long | false | current_timestamp |
| modified_at | modifiedAt | Long | false | current_timestamp |
| created_by | createdBy.id | Long | false | null |
| modified_by | modifiedBy.id | Long | false | null |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @OneToOne | version | Version | LAZY | Version control, cascade = DETACH |
| @OneToMany | facilities | Set\<ChecklistFacilityMapping\> | LAZY | Facility mappings, cascade = ALL, orphanRemoval = true |
| @ManyToOne | organisation | Organisation | LAZY | Parent organization, updatable = false |
| @ManyToOne | useCase | UseCase | EAGER | Associated use case, updatable = false |
| @OneToMany | stages | Set\<Stage\> | LAZY | Workflow stages, cascade = ALL, ordered by order_tree |
| @OneToMany | jobs | Set\<Job\> | LAZY | Process executions, cascade = ALL |
| @OneToMany | checklistPropertyValues | Set\<ChecklistPropertyValue\> | LAZY | Property values, cascade = ALL |
| @OneToMany | relations | Set\<Relation\> | LAZY | Object relations, cascade = ALL, ordered by order_tree |
| @OneToMany | collaborators | Set\<ChecklistCollaboratorMapping\> | LAZY | Collaboration mappings, cascade = PERSIST/MERGE/REFRESH |
| @ManyToOne | releasedBy | User | LAZY | Release user, cascade = DETACH |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(Checklist entity)`
- `deleteById(Long id)`
- `delete(Checklist entity)`
- `existsById(Long id)`
- `count()`

### Specification Methods
- `findAll(Specification<Checklist> spec)`
- `findAll(Specification<Checklist> spec, Pageable pageable)`
- `findAll(Specification<Checklist> spec, Sort sort)`
- `findOne(Specification<Checklist> spec)`
- `count(Specification<Checklist> spec)`

### Custom Query Methods
- `findAll(Specification specification, Pageable pageable)`
- `findAllByIdIn(Collection<Long> id, Sort sort)`
- `findByTaskId(Long taskId)`
- `updateState(State.Checklist state, Long checklistId)`
- `getChecklistCodeByChecklistId(Long checklistId)`
- `removeChecklistFacilityMapping(Long checklistId, Set<Long> facilityIds)`
- `findByStageId(Long stageId)`
- `findByUseCaseId(Long useCaseId)`
- `findByStateInOrderByStateDesc(Set<State.Checklist> stateSet)`
- `findByStateNot(State.Checklist state)`
- `findChecklistInfoById(Long id)`
- `updateChecklistDuringRecall(Long checklistId, Long userId)`
- `findAllChecklistIdsForCurrentFacilityAndOrganisationByObjectTypeInData(Long facilityId, Long organisationId, String objectTypeId, Long useCaseId, String name, boolean archived)`
- `getAllByIdsIn(List<Long> checklistIds)`
- `getChecklistJobLiteDtoById(Long checklistId)`

## Method Documentation

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<Checklist> findById(Long id)
List<Checklist> findAll()
Checklist save(Checklist entity)
void deleteById(Long id)
void delete(Checklist entity)
boolean existsById(Long id)
long count()
```

### Custom Query Methods

#### Method: findAll(Specification specification, Pageable pageable)
```yaml
Signature: Page<Checklist> findAll(Specification specification, Pageable pageable)
Purpose: "Override to provide paginated specification-based queries for checklists"

Business Logic Derivation:
  1. Used extensively in ChecklistService for dynamic filtering and pagination
  2. Supports complex search criteria with multiple filter conditions
  3. Enables efficient checklist discovery with pagination for large datasets
  4. Critical for checklist listing APIs and dashboard displays
  5. Integrates with Spring Data Specification pattern for flexible queries

SQL Query: |
  Dynamic SQL based on specification criteria with pagination
  Generated by Spring Data JPA Specification framework

Parameters:
  - specification: Specification (Dynamic filtering criteria)
  - pageable: Pageable (Pagination and sorting parameters)

Returns: Page<Checklist> (paginated checklist results)
Transaction: Not Required
Error Handling: Returns empty page if no checklists match criteria
```

#### Method: findAllByIdIn(Collection<Long> id, Sort sort)
```yaml
Signature: List<Checklist> findAllByIdIn(Collection<Long> id, Sort sort)
Purpose: "Find checklists by multiple IDs with custom sorting"

Business Logic Derivation:
  1. Used after specification queries to load full entities with relationships
  2. Optimizes performance by loading specific entities rather than full pagination
  3. Enables sorted retrieval of checklists matching specific criteria
  4. Critical for bulk operations and batch processing
  5. Supports custom sorting requirements for UI display

SQL Query: |
  SELECT c.* FROM checklists c 
  WHERE c.id IN (?)
  ORDER BY [sort criteria]

Parameters:
  - id: Collection<Long> (Checklist IDs to retrieve)
  - sort: Sort (Sorting criteria for results)

Returns: List<Checklist> (sorted list of matching checklists)
Transaction: Not Required
Error Handling: Returns empty list if no checklists found for IDs
```

#### Method: findByTaskId(Long taskId)
```yaml
Signature: Optional<Checklist> findByTaskId(Long taskId)
Purpose: "Find checklist that contains a specific task"

Business Logic Derivation:
  1. Used in task-centric operations to determine parent checklist context
  2. Critical for task management operations that need checklist validation
  3. Enables reverse lookup from task to parent checklist
  4. Used in permissions and authorization checks for task operations
  5. Essential for maintaining task-checklist relationship integrity

SQL Query: |
  SELECT c.* FROM checklists c 
  INNER JOIN stages s ON c.id = s.checklists_id 
  INNER JOIN tasks t ON s.id = t.stages_id 
  WHERE t.id = ?

  BUSINESS LOGIC:
  1. Join checklists with stages and tasks to traverse hierarchy
  2. Filter by specific task ID to find parent checklist
  3. Return checklist entity for task context operations
  4. Enables task-to-checklist navigation for workflow operations
  5. Critical for maintaining hierarchical data integrity

Parameters:
  - taskId: Long (Task identifier to find parent checklist for)

Returns: Optional<Checklist> (parent checklist or empty if task not found)
Transaction: Not Required
Error Handling: Returns empty Optional if task doesn't exist or has no parent
```

#### Method: updateState(State.Checklist state, Long checklistId)
```yaml
Signature: void updateState(State.Checklist state, Long checklistId)
Purpose: "Update checklist state for lifecycle management"

Business Logic Derivation:
  1. Used throughout ChecklistService for state transitions
  2. Critical for checklist lifecycle management (BEING_BUILT, PUBLISHED, DEPRECATED)
  3. Enables atomic state updates without loading full entity
  4. Used in workflow progression and approval processes
  5. Essential for checklist publishing and deprecation operations

SQL Query: |
  UPDATE checklists SET state = ? WHERE id = ?

  BUSINESS LOGIC:
  1. Directly update state column in checklists table
  2. Filter by checklist ID for precise state management
  3. Atomic operation for state transition without entity loading
  4. Ensures consistent state management across workflow operations
  5. Critical for checklist lifecycle and workflow progression

Parameters:
  - state: State.Checklist (New state for the checklist)
  - checklistId: Long (Checklist identifier to update)

Returns: void
Transaction: Required (uses @Modifying annotation)
Error Handling: DataIntegrityViolationException for invalid state or ID
```

#### Method: getChecklistCodeByChecklistId(Long checklistId)
```yaml
Signature: String getChecklistCodeByChecklistId(Long checklistId)
Purpose: "Get checklist code for identifier and logging purposes"

Business Logic Derivation:
  1. Used extensively for audit logging and identifier display
  2. Provides lightweight code retrieval without loading full entity
  3. Critical for audit trails and user-friendly identifiers
  4. Used in error messages and logging operations
  5. Enables efficient code-based operations and display

SQL Query: |
  SELECT code FROM checklists WHERE id = ?

Parameters:
  - checklistId: Long (Checklist identifier to get code for)

Returns: String (checklist code or null if not found)
Transaction: Not Required
Error Handling: Returns null if checklist not found
```

#### Method: removeChecklistFacilityMapping(Long checklistId, Set<Long> facilityIds)
```yaml
Signature: void removeChecklistFacilityMapping(Long checklistId, Set<Long> facilityIds)
Purpose: "Remove checklist access from specific facilities"

Business Logic Derivation:
  1. Used in facility management operations to control checklist access
  2. Enables bulk removal of facility associations for efficiency
  3. Critical for facility-based access control and checklist scoping
  4. Used when facilities are decommissioned or access is revoked
  5. Maintains facility-checklist relationship integrity

SQL Query: |
  DELETE FROM checklist_facility_mapping 
  WHERE checklists_id = ? AND facilities_id IN (?)

  BUSINESS LOGIC:
  1. Target checklist_facility_mapping table for relationship management
  2. Filter by checklist ID to scope operation to specific checklist
  3. Filter by facility IDs set to remove only specified facilities
  4. Bulk operation for efficient facility access management
  5. Maintains checklist accessibility and facility scoping integrity

Parameters:
  - checklistId: Long (Checklist to remove facility access for)
  - facilityIds: Set<Long> (Facilities to remove from checklist access)

Returns: void
Transaction: Required (uses @Transactional and @Modifying)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findByStageId(Long stageId)
```yaml
Signature: State.Checklist findByStageId(Long stageId)
Purpose: "Get checklist state by stage identifier for validation"

Business Logic Derivation:
  1. Used in stage operations to validate parent checklist state
  2. Critical for ensuring stage operations are allowed based on checklist state
  3. Enables stage-level validation without loading full checklist entity
  4. Used in authorization and workflow validation logic
  5. Essential for maintaining checklist-stage relationship integrity

SQL Query: |
  SELECT c.state FROM checklists c 
  INNER JOIN stages s ON c.id = s.checklists_id 
  WHERE s.id = ?

  BUSINESS LOGIC:
  1. Join checklists with stages to traverse hierarchy
  2. Filter by stage ID to find parent checklist state
  3. Return only state enum for efficient validation operations
  4. Enables stage-context checklist state checking
  5. Critical for workflow and permission validation

Parameters:
  - stageId: Long (Stage identifier to get parent checklist state for)

Returns: State.Checklist (parent checklist state)
Transaction: Not Required
Error Handling: Returns null if stage doesn't exist or has no parent
```

#### Method: findByUseCaseId(Long useCaseId)
```yaml
Signature: List<Checklist> findByUseCaseId(Long useCaseId)
Purpose: "Find all checklists associated with a specific use case"

Business Logic Derivation:
  1. Used for use case management and checklist categorization
  2. Enables use case-scoped checklist operations and reporting
  3. Critical for organizing checklists by business domain or purpose
  4. Used in use case lifecycle management and migration operations
  5. Supports use case-based access control and filtering

SQL Query: |
  SELECT c.* FROM checklists c WHERE c.use_cases_id = ?

Parameters:
  - useCaseId: Long (Use case identifier to find checklists for)

Returns: List<Checklist> (all checklists in the use case)
Transaction: Not Required
Error Handling: Returns empty list if no checklists found for use case
```

#### Method: findByStateInOrderByStateDesc(Set<State.Checklist> stateSet)
```yaml
Signature: List<Long> findByStateInOrderByStateDesc(Set<State.Checklist> stateSet)
Purpose: "Find checklist IDs by multiple states for batch operations"

Business Logic Derivation:
  1. Used in migration operations and batch processing tasks
  2. Enables efficient processing of checklists in specific states
  3. Critical for state-based bulk operations and system maintenance
  4. Used in job log migration and system cleanup operations
  5. Returns only IDs for memory-efficient batch processing

SQL Query: |
  SELECT id FROM checklists WHERE state IN (?) ORDER BY state DESC

  BUSINESS LOGIC:
  1. Query checklists table for multiple state filtering
  2. Use IN clause for efficient multi-state matching
  3. Order by state descending for processing priority
  4. Return only IDs for memory-efficient bulk operations
  5. Enables state-based batch processing and migration tasks

Parameters:
  - stateSet: Set<State.Checklist> (States to include in results)

Returns: List<Long> (checklist IDs matching the states)
Transaction: Not Required
Error Handling: Returns empty list if no checklists match states
```

#### Method: findByStateNot(State.Checklist state)
```yaml
Signature: Set<Long> findByStateNot(State.Checklist state)
Purpose: "Find checklist IDs excluding a specific state"

Business Logic Derivation:
  1. Used in migration operations to exclude checklists in specific states
  2. Enables processing of checklists that are not in draft/building state
  3. Critical for system-wide operations that need stable checklists
  4. Used in auto-initialization and relation filter migrations
  5. Returns IDs as Set for efficient membership checking

SQL Query: |
  SELECT id FROM checklists WHERE state != ?

Parameters:
  - state: State.Checklist (State to exclude from results)

Returns: Set<Long> (checklist IDs not in the specified state)
Transaction: Not Required
Error Handling: Returns empty set if all checklists are in the excluded state
```

#### Method: findChecklistInfoById(Long id)
```yaml
Signature: JobLogMigrationChecklistView findChecklistInfoById(Long id)
Purpose: "Get lightweight checklist information for job log operations"

Business Logic Derivation:
  1. Used in job log migration and display operations
  2. Provides essential checklist information without loading full entity
  3. Optimized for job-related operations and logging
  4. Critical for job log correlation and checklist identification
  5. Returns projection view for efficient data transfer

SQL Query: |
  SELECT c.id as id, c.name as name, c.code as code, c.state as state 
  FROM checklists c 
  WHERE id = ?

Parameters:
  - id: Long (Checklist identifier to get information for)

Returns: JobLogMigrationChecklistView (lightweight checklist information)
Transaction: Not Required
Error Handling: Returns null if checklist not found
```

#### Method: updateChecklistDuringRecall(Long checklistId, Long userId)
```yaml
Signature: void updateChecklistDuringRecall(Long checklistId, Long userId)
Purpose: "Update checklist state and metadata during recall operation"

Business Logic Derivation:
  1. Used in checklist recall operations to reset state and ownership
  2. Atomically updates state, user, and review cycle for recall workflow
  3. Critical for checklist lifecycle management and ownership transfer
  4. Enables efficient recall operation without loading full entity
  5. Maintains audit trail during checklist recall process

SQL Query: |
  UPDATE checklists 
  SET state = 'BEING_BUILT', created_by = ?, modified_by = ?, review_cycle = 1 
  WHERE id = ?

  BUSINESS LOGIC:
  1. Reset checklist state to BEING_BUILT for editing
  2. Update created_by and modified_by to reflect new ownership
  3. Reset review_cycle to 1 for fresh review process
  4. Filter by checklist ID for precise recall operation
  5. Atomic operation for consistent recall state management

Parameters:
  - checklistId: Long (Checklist to recall)
  - userId: Long (User performing the recall operation)

Returns: void
Transaction: Required (uses @Modifying annotation)
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Method: findAllChecklistIdsForCurrentFacilityAndOrganisationByObjectTypeInData
```yaml
Signature: List<Long> findAllChecklistIdsForCurrentFacilityAndOrganisationByObjectTypeInData(Long facilityId, Long organisationId, String objectTypeId, Long useCaseId, String name, boolean archived)
Purpose: "Find applicable checklists for facility and organization with object type filtering"

Business Logic Derivation:
  1. Used in checklist discovery for job creation and process selection
  2. Filters checklists by facility access, organization, and resource parameters
  3. Critical for determining which checklists are available for execution
  4. Enables resource-specific checklist filtering based on object types
  5. Supports multi-criteria filtering for precise checklist selection

SQL Query: |
  SELECT DISTINCT c.id
  FROM checklists c
  INNER JOIN checklist_facility_mapping cfm ON c.id = cfm.checklists_id
  INNER JOIN parameters p ON p.checklists_id = c.id
  WHERE (cfm.facilities_id = ? OR facilities_id = -1)
    AND c.organisations_id = ?
    AND p.type='RESOURCE'
    AND p.data->>'objectTypeId'= ?
    AND c.archived = ?
    AND c.use_cases_id = ?
    AND c.state = 'PUBLISHED'
    AND (CAST(? as varchar) IS NULL OR c.name ilike '%' || ? || '%')
  ORDER BY c.id DESC

  BUSINESS LOGIC:
  1. Join checklists with facility mappings and parameters for filtering
  2. Filter by facility access (specific facility or global access)
  3. Filter by organization, use case, and publication state
  4. Filter by resource parameters matching object type
  5. Support optional name filtering with ILIKE pattern matching
  6. Return distinct checklist IDs for efficient processing

Parameters:
  - facilityId: Long (Current facility identifier)
  - organisationId: Long (Organization identifier)
  - objectTypeId: String (Object type for resource parameter filtering)
  - useCaseId: Long (Use case identifier)
  - name: String (Optional name filter)
  - archived: boolean (Archive status filter)

Returns: List<Long> (applicable checklist IDs)
Transaction: Not Required
Error Handling: Returns empty list if no applicable checklists found
```

#### Method: getAllByIdsIn(List<Long> checklistIds)
```yaml
Signature: List<ChecklistView> getAllByIdsIn(List<Long> checklistIds)
Purpose: "Get checklist view projections for multiple IDs"

Business Logic Derivation:
  1. Used after filtering operations to get displayable checklist information
  2. Provides optimized data transfer for UI display and API responses
  3. Critical for checklist listing and selection operations
  4. Enables efficient bulk retrieval without loading full entities
  5. Returns projection views for performance optimization

SQL Query: |
  SELECT c.id, c.code, c.name, c.color_code as colorCode 
  FROM checklists c 
  WHERE id IN (?) 
  ORDER BY id DESC

Parameters:
  - checklistIds: List<Long> (Checklist IDs to get view data for)

Returns: List<ChecklistView> (projection views of checklists)
Transaction: Not Required
Error Handling: Returns empty list if no checklists found for IDs
```

#### Method: getChecklistJobLiteDtoById(Long checklistId)
```yaml
Signature: ChecklistJobLiteView getChecklistJobLiteDtoById(Long checklistId)
Purpose: "Get lightweight checklist information for job operations"

Business Logic Derivation:
  1. Used in job service operations for checklist context
  2. Provides essential checklist information for job processing
  3. Optimized for job-related operations and performance
  4. Critical for job creation and execution workflows
  5. Returns minimal data for efficient job operations

SQL Query: |
  SELECT c.id, c.name, c.code 
  FROM checklists c 
  WHERE c.id = ?

Parameters:
  - checklistId: Long (Checklist identifier for job context)

Returns: ChecklistJobLiteView (lightweight checklist information)
Transaction: Not Required
Error Handling: Returns null if checklist not found
```

### Key Repository Usage Patterns (Based on Codebase Analysis)

#### Pattern: save() for Lifecycle Management
```yaml
Usage: checklistRepository.save(checklist)
Purpose: "Manage checklist creation, updates, and lifecycle transitions"

Business Logic Derivation:
  1. Used extensively in ChecklistService for all checklist modifications
  2. Handles checklist creation, property updates, and state management
  3. Critical for checklist lifecycle operations and collaboration setup
  4. Maintains audit trail through entity modification tracking
  5. Supports complex checklist operations with relationship management

Common Usage Examples:
  - Checklist creation: Save new checklist with initial state and collaborators
  - Property updates: Save checklist with modified properties and metadata
  - State transitions: Save checklist after state changes and workflow progression
  - Facility mapping: Save checklist with updated facility associations

Transaction: Required
Error Handling: DataIntegrityViolationException for constraint violations
```

#### Pattern: findAll(specification, pageable) for Dynamic Queries
```yaml
Usage: checklistRepository.findAll(specification, pageable)
Purpose: "Dynamic checklist discovery with complex filtering and pagination"

Business Logic Derivation:
  1. Used in ChecklistService.getChecklists() for advanced search operations
  2. Applies dynamic specifications for multi-criteria filtering
  3. Supports pagination for large checklist datasets
  4. Enables flexible checklist discovery and management operations
  5. Critical for checklist listing APIs and dashboard functionality

Common Filter Criteria:
  - Organization and facility filtering
  - State-based filtering (published, deprecated, etc.)
  - Name and code pattern matching
  - Use case and collaboration filtering

Transaction: Not Required
Error Handling: Returns empty page if no matches found
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findAll(Specification), findAllByIdIn, findByTaskId
  - getChecklistCodeByChecklistId, findByStageId, findByUseCaseId
  - findByStateInOrderByStateDesc, findByStateNot, findChecklistInfoById
  - findAllChecklistIdsForCurrentFacilityAndOrganisationByObjectTypeInData
  - getAllByIdsIn, getChecklistJobLiteDtoById, existsById, count

Transactional Methods:
  - save, delete, deleteById, updateState, removeChecklistFacilityMapping
  - updateChecklistDuringRecall

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Exception.class (all exceptions trigger rollback)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * Foreign key violations (invalid organisations_id, use_cases_id, versions_id)
    * NOT NULL constraint violations (name, state, code, organisations_id, use_cases_id)
    * Invalid enum values for state field
    * Unique constraint violations for code uniqueness
  - EntityNotFoundException: Checklist not found by ID or criteria
  - OptimisticLockException: Concurrent checklist modifications
  - InvalidDataAccessApiUsageException: Invalid specification criteria or query parameters
  - JsonProcessingException: Invalid JSON format in jobLogColumns field

Validation Rules:
  - name: Required, max length based on column definition
  - state: Required, must be valid State.Checklist enum value
  - code: Required, should be unique, max 20 characters, immutable (updatable = false)
  - jobLogColumns: Required, must be valid JSON (defaults to '[]')
  - organisation: Required, must reference existing organisation, immutable
  - useCase: Required, must reference existing use case, immutable
  - reviewCycle: Required, defaults to 1, must be positive integer
  - isGlobal: Required, boolean flag for global access
  - archived: Required, defaults to false

Business Constraints:
  - Cannot modify code, organisation, or use case after creation
  - State transitions must follow valid checklist lifecycle sequences
  - Cannot delete checklist with active jobs or collaborations
  - Facility mappings must maintain access control integrity
  - Version relationships must be consistent with checklist history
  - Job log columns must contain valid JSON configuration
  - Review cycle must be managed through proper workflow operations
```

This comprehensive documentation provides everything needed to implement an exact DAO layer replacement for the Checklist repository without JPA/Hibernate dependencies.
