# ICodeRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: Code
- **Primary Purpose**: Manages code generation entities for unique identifier generation with organization-scoped, entity-type-specific, and clause-based counter management
- **Key Relationships**: Code generation entity linking to Organisation with composite key structure for unique code generation across entity types
- **Performance Characteristics**: Low query volume with code generation operations and counter retrieval for unique identifier generation
- **Business Context**: Code generation component that provides unique identifier generation for various entity types within organizational scope using counter-based sequential numbering

## Entity Mapping Documentation

### Field Mappings

| Database Column | Java Field | Type | Nullable | Default |
|---|---|---|---|---|
| type (composite key) | codeId.type | Type.EntityType | false | null |
| clause (composite key) | codeId.clause | short | false | null |
| organisations_id (composite key) | codeId.organisation.id | Long | false | null |
| counter | counter | Integer | true | null |

### Composite Key Structure (CodeCompositeKey)
- **type**: Entity type for code generation (CHECKLIST, JOB, SCHEDULER, etc.)
- **clause**: Additional clause identifier for code classification
- **organisation**: Organisation reference for organization-scoped code generation

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @ManyToOne (in composite key) | codeId.organisation | Organisation | LAZY | Parent organisation for code scoping, not null |

## Available Repository Methods

### Custom Repository Interface
Note: This repository does not extend JpaRepository - it's a custom interface with specialized code generation methods.

### Custom Query Methods (1 method - ALL methods documented)

- `getCode(Long organisationId, Type.EntityType type, Integer clause)`

## Method Documentation (All Custom Methods - Full Detail)

### Custom Query Methods

#### Method: getCode(Long organisationId, Type.EntityType type, Integer clause)
```yaml
Signature: Code getCode(Long organisationId, Type.EntityType type, Integer clause)
Purpose: "Get code generation entity for unique identifier generation with organization, entity type, and clause scoping"

Business Logic Derivation:
  1. Used in CodeService for unique code generation across different entity types within organizational boundaries
  2. Provides counter-based sequential numbering for entity identifier generation with organization-scoped uniqueness
  3. Critical for code generation operations requiring unique, sequential identifiers for business entities
  4. Used in entity creation workflows for generating unique codes with predictable, sequential numbering patterns
  5. Enables organization-scoped code generation with entity-type-specific counter management for unique identifier creation

SQL Query: |
  SELECT c.* FROM codes c
  WHERE c.organisations_id = ?
    AND c.type = ?
    AND c.clause = ?

Parameters:
  - organisationId: Long (Organisation identifier for scoping code generation)
  - type: Type.EntityType (Entity type for code generation - CHECKLIST, JOB, SCHEDULER, etc.)
  - clause: Integer (Clause identifier for additional code classification)

Returns: Code (code generation entity with counter for unique identifier generation)
Transaction: Required (typically involves counter increment for thread-safe code generation)
Error Handling: Returns null if no code generation entity found, requires handling for new code creation
```

### Key Repository Usage Patterns

#### Pattern: Sequential Code Generation
```yaml
Usage: codeRepository.getCode(organisationId, entityType, clause)
Purpose: "Generate unique sequential codes for business entities within organizational scope"

Business Logic Derivation:
  1. Code generation provides unique, predictable identifiers for business entities across different types
  2. Organization-scoped code generation ensures uniqueness within tenant boundaries for multi-tenant operations
  3. Entity-type-specific counters enable different numbering sequences for different business entity types
  4. Clause-based classification allows sub-categorization of codes within entity types for business requirements
  5. Counter-based generation ensures sequential, predictable numbering for business operations and reporting

Common Usage Examples:
  - Checklist code generation: "CHK-20231215-001", "CHK-20231215-002", etc.
  - Job code generation: "JOB-20231215-001", "JOB-20231215-002", etc.
  - Scheduler code generation: "SCH-20231215-001", "SCH-20231215-002", etc.
  - Organization-scoped uniqueness ensuring no code conflicts across tenants
  - Date-based code formatting with sequential counter for temporal organization

Transaction: Required (counter increment must be atomic)
Error Handling: Thread-safe counter management, entity creation for new code sequences
```

#### Pattern: Thread-Safe Counter Management
```yaml
Usage: Counter increment and retrieval operations
Purpose: "Ensure thread-safe, atomic counter increment for concurrent code generation"

Business Logic Derivation:
  1. Code generation must be thread-safe to prevent duplicate codes in concurrent environments
  2. Atomic counter increment ensures unique code generation under high concurrency loads
  3. Database-level locking or atomic operations prevent race conditions in code generation
  4. Counter state management maintains sequence integrity across application restarts
  5. Error handling ensures recovery from failed code generation attempts with proper rollback

Common Implementation Patterns:
  - Database row-level locking for atomic counter increment
  - Optimistic locking with retry logic for concurrent access handling
  - Transactional counter increment with rollback on failure
  - Sequence or stored procedure-based counter management for database optimization
  - Cache management for high-frequency code generation scenarios

Transaction: Required with appropriate isolation level
Error Handling: Deadlock detection, retry logic, sequence integrity validation
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Entity Code Generation in CodeService
```yaml
Signature: Used in CodeService.generateCode() for entity identifier creation
Purpose: "Generate formatted entity codes with organization scope, entity type, and sequential numbering"

Business Logic Derivation:
  1. CodeService uses this repository for generating formatted codes like "CHK-20231215-001" for business entities
  2. Code format combines entity type prefix, date component, and sequential counter for readable, unique identifiers
  3. Organization-scoped code generation ensures tenant isolation and prevents cross-organization code conflicts
  4. Counter-based sequential numbering provides predictable, ordered identifier generation for business operations
  5. Entity-type-specific code generation enables different numbering sequences for different business contexts

Common Usage Examples:
  - codeRepository.getCode(organisationId, EntityType.CHECKLIST, clause) for checklist code generation
  - Code formatting: entityType.getCode() + "-" + formattedDate + "-" + counter for business-readable identifiers
  - Organization-scoped code uniqueness for multi-tenant code generation requirements
  - Date-based code organization for temporal code management and reporting
  - Sequential counter management for predictable identifier generation patterns

Transaction: Required for atomic counter operations
Error Handling: Counter creation for new sequences, thread-safe increment operations
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - None (getCode typically involves counter increment)

Transactional Methods:
  - getCode (requires atomic counter increment for thread-safe code generation)

Isolation Level: SERIALIZABLE or READ_COMMITTED with row-level locking
Timeout: 10 seconds (short timeout for quick code generation)
Rollback: Exception.class (all exceptions trigger rollback for counter integrity)
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - EntityNotFoundException: Code generation entity not found for organization/type/clause combination
  - OptimisticLockException: Concurrent access to counter during code generation
  - DataIntegrityViolationException: Constraint violations in composite key or counter operations
  - DeadlockException: Database deadlock during concurrent counter increment operations
  - IllegalStateException: Invalid counter state or sequence integrity issues

Validation Rules:
  - organisationId: Required, must reference existing organisation for code scoping
  - type: Required, must be valid EntityType enum value for entity classification
  - clause: Required, must be valid integer for code sub-classification
  - counter: Must be positive integer, incremented atomically for sequential generation

Business Constraints:
  - Code generation must be thread-safe for concurrent access in multi-user environments
  - Counter increment must be atomic to prevent duplicate code generation
  - Organization scope must be maintained for tenant isolation in multi-tenant environments
  - Entity type classification must be consistent for predictable code generation patterns
  - Code sequences must be recoverable and maintain integrity across application restarts
  - Counter overflow must be handled appropriately for long-running code sequences
  - Error recovery must maintain sequence integrity without gaps in critical numbering
  - Performance optimization required for high-frequency code generation scenarios
  - Audit trail may be required for code generation tracking and compliance
  - Backup and recovery procedures must maintain counter state and sequence integrity
```

## Implementation Notes

### Thread Safety Considerations
```yaml
Concurrency Control:
  - Database row-level locking or SELECT FOR UPDATE for atomic counter increment
  - Optimistic locking with version fields for concurrent access management
  - Retry logic for handling concurrent access conflicts and deadlock scenarios
  - Transaction isolation level tuning for performance vs. consistency trade-offs

Performance Optimization:
  - Connection pooling for high-frequency code generation operations
  - Counter caching strategies for reducing database load in high-volume scenarios
  - Batch code generation for bulk entity creation operations
  - Sequence or stored procedure optimization for database-native counter management
```

### Database Schema Considerations
```yaml
Primary Key: Composite key (organisations_id, type, clause)
Indexes: 
  - Primary composite index on (organisations_id, type, clause)
  - Optional index on type for entity-type-based queries
  
Counter Management:
  - Counter field with appropriate data type for expected sequence length
  - Default value handling for new code sequence initialization
  - Overflow handling for long-running sequences approaching numeric limits
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the Code repository without JPA/Hibernate dependencies, focusing on thread-safe code generation patterns.
