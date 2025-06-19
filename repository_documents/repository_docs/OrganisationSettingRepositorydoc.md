# IOrganisationSettingRepository - Complete DAO Migration Documentation

## Repository Overview
- **Entity**: OrganisationSetting (extends BaseEntity)
- **Primary Purpose**: Manages organization setting entities for multi-tenant configuration management with security settings, timeout configuration, and branding functionality
- **Key Relationships**: One-to-one relationship with Organisation entity for comprehensive organization configuration and settings management
- **Performance Characteristics**: Very low query volume with organization-specific configuration retrieval and settings management operations
- **Business Context**: Organization configuration component that provides multi-tenant settings management, security configuration, timeout settings, and branding functionality for organization-specific customization

## Entity Mapping Documentation

### Field Mappings (Inherits from BaseEntity)

| Database Column | Java Field | Type | Nullable | Default | Notes |
|---|---|---|---|---|---|
| id | id | Long | false | auto-generated | Primary key |
| organisations_id | organisationId / organisation.id | Long | false | null | Foreign key to organisations |
| logo_url | logoUrl | String | true | null | Organization logo URL (max 255 chars) |
| session_idle_timeout | sessionIdleTimeout | Integer | true | 10 | Session timeout in minutes |
| registration_token_expiration | registrationTokenExpiration | Integer | true | 60 | Registration token expiry in minutes |
| password_reset_token_expiration | passwordResetTokenExpiration | Integer | true | 60 | Password reset token expiry in minutes |
| max_failed_login_attempts | maxFailedLoginAttempts | Integer | true | 3 | Maximum failed login attempts |
| max_failed_additional_verification_attempts | maxFailedAdditionalVerificationAttempts | Integer | true | 3 | Maximum failed additional verification attempts |
| max_failed_challenge_question_attempts | maxFailedChallengeQuestionAttempts | Integer | true | 3 | Maximum failed challenge question attempts |
| auto_unlock_after | autoUnlockAfter | Integer | true | 15 | Auto unlock time in minutes |
| created_at | createdAt | Long | false | current_timestamp | Creation timestamp (JsonIgnore) |
| modified_at | modifiedAt | Long | false | current_timestamp | Modification timestamp (JsonIgnore) |

### Relationships

| Relationship Type | Field | Target Entity | Fetch Type | Notes |
|---|---|---|---|---|
| @OneToOne | organisation | Organisation | LAZY | Associated organization, not null |

## Available Repository Methods

### Standard CRUD Methods
- `findById(Long id)`
- `findAll()`
- `save(OrganisationSetting entity)`
- `saveAll(Iterable<OrganisationSetting> entities)`
- `deleteById(Long id)`
- `delete(OrganisationSetting entity)`
- `existsById(Long id)`
- `count()`

### Custom Query Methods (2 methods - ALL methods documented)

**Organization Setting Retrieval Methods (2 methods):**
- `findByOrganisationId(Long organisationId)`
- `findLogoUrlByOrganisationId(Long organisationId)`

## Method Documentation (All Custom Methods - Full Detail)

### Standard CRUD Operations
```java
// Standard JpaRepository methods
Optional<OrganisationSetting> findById(Long id)
List<OrganisationSetting> findAll()
OrganisationSetting save(OrganisationSetting entity)
List<OrganisationSetting> saveAll(Iterable<OrganisationSetting> entities)
void deleteById(Long id)
void delete(OrganisationSetting entity)
boolean existsById(Long id)
long count()
```

### Organization Setting Retrieval Methods

#### Method: findByOrganisationId(Long organisationId)
```yaml
Signature: Optional<OrganisationSetting> findByOrganisationId(Long organisationId)
Purpose: "Find organization setting by organization ID for organization configuration and settings management"

Business Logic Derivation:
  1. Provides organization-specific settings retrieval for multi-tenant configuration management and organization customization functionality
  2. Used for organization configuration access during settings management enabling comprehensive organization configuration and settings functionality
  3. Critical for organization configuration operations requiring settings access for organization management and configuration control
  4. Used in organization configuration workflows for accessing organization settings for configuration operations and organization processing
  5. Enables organization configuration with settings access for comprehensive organization processing and configuration control

SQL Query: |
  SELECT os FROM OrganisationSetting os WHERE os.organisationId = ?

Parameters:
  - organisationId: Long (Organization identifier for settings retrieval)

Returns: Optional<OrganisationSetting> (organization settings if found, empty otherwise)
Transaction: Not Required (simple read operation)
Error Handling: Returns empty Optional if no settings found for organization
```

#### Method: findLogoUrlByOrganisationId(Long organisationId)
```yaml
Signature: Optional<String> findLogoUrlByOrganisationId(@Param("organisationId") Long organisationId)
Purpose: "Find organization logo URL by organization ID for branding and PDF generation"

Business Logic Derivation:
  1. Used in PdfGeneratorUtil for organization logo retrieval during PDF generation and branding operations
  2. Provides organization branding access for document generation workflows enabling comprehensive branding and document functionality
  3. Critical for document generation operations requiring logo access for branding management and document control
  4. Used in document generation workflows for accessing organization logos for branding operations and document processing
  5. Enables document generation with branding access for comprehensive document processing and branding control

JPQL Query: |
  SELECT os.logoUrl FROM OrganisationSetting os WHERE os.organisationId = :organisationId

Parameters:
  - organisationId: Long (Organization identifier for logo URL retrieval)

Returns: Optional<String> (logo URL if found, empty otherwise)
Transaction: Not Required (simple read operation)
Error Handling: Returns empty Optional if no logo URL found for organization
```

### Key Repository Usage Patterns

#### Pattern: Organization Configuration Management
```yaml
Usage: Organization-specific configuration retrieval for multi-tenant settings management and customization
Purpose: "Retrieve organization configurations for comprehensive settings functionality and customization processing"

Business Logic Derivation:
  1. Organization configuration management provides settings functionality through organization-specific configuration retrieval and settings access operations
  2. Settings lifecycle includes configuration retrieval, customization access, and organization-specific management for configuration control
  3. Configuration management operations require settings processing for organization lifecycle and customization control
  4. Organization operations enable comprehensive configuration functionality with settings capabilities and management
  5. Configuration lifecycle management supports organization requirements and functionality for organization settings processing

Common Usage Examples:
  - Organization settings retrieval for multi-tenant configuration
  - Security settings access for authentication and authorization
  - Timeout configuration for session management
  - Branding configuration for organization customization

Transaction: Not Required for configuration retrieval operations
Error Handling: Organization configuration error handling and settings validation verification
```

#### Pattern: Branding and Document Generation
```yaml
Usage: Organization branding retrieval for document generation and PDF customization functionality
Purpose: "Retrieve organization branding for comprehensive document functionality and customization processing"

Business Logic Derivation:
  1. Document generation operations require organization-specific branding access for document-level customization management and branding functionality
  2. Branding management supports document requirements and functionality for document generation workflows
  3. Document-level branding operations ensure proper customization functionality through organization management and branding control
  4. Document workflows coordinate branding management with generation processing for comprehensive document operations
  5. Branding management supports document requirements and functionality for comprehensive document branding management

Common Usage Examples:
  - organizationSettingRepository.findLogoUrlByOrganisationId() for PDF generation branding
  - Organization logo retrieval for document customization
  - Branding configuration for multi-tenant document generation

Transaction: Not Required for branding retrieval operations
Error Handling: Branding error handling and logo validation verification
```

## Actual Usage Patterns (Based on Business Layer Analysis)

### Pattern: Multi-Tenant Configuration Management
```yaml
Usage: Multi-tenant configuration management for organization-specific settings and customization functionality
Purpose: "Manage multi-tenant configurations for comprehensive organization functionality and settings processing"

Business Logic Derivation:
  1. Multi-tenant configuration provides organization functionality through organization-specific settings management, security configuration, and customization operations
  2. Organization lifecycle includes settings configuration, security management, and customization for organization control
  3. Organization management operations require configuration processing for organization lifecycle and settings control
  4. Configuration operations enable comprehensive organization functionality with settings capabilities and management
  5. Organization lifecycle management supports configuration requirements and functionality for organization settings processing

Common Usage Examples:
  - Organization settings retrieval for tenant-specific configuration
  - Security timeout configuration for organization-specific authentication
  - Registration and password reset token configuration
  - Failed attempt limits for organization security policies
  - Session management configuration for tenant isolation

Transaction: Not Required for configuration operations
Error Handling: Multi-tenant configuration error handling and settings validation verification
```

### Pattern: Security and Authentication Configuration
```yaml
Usage: Security and authentication configuration for organization-specific security settings and policy management functionality
Purpose: "Configure security settings for comprehensive security functionality and authentication processing"

Business Logic Derivation:
  1. Security configuration management operations require comprehensive organization settings access for security-level configuration management and authentication functionality
  2. Security management supports organization requirements and functionality for authentication processing workflows
  3. Organization-level security operations ensure proper authentication functionality through settings management and security control
  4. Security workflows coordinate settings management with authentication processing for comprehensive security operations
  5. Security management supports organization requirements and functionality for comprehensive organization security management

Common Usage Examples:
  - Session timeout configuration for security management
  - Failed login attempt limits for security policies
  - Token expiration settings for authentication security
  - Auto-unlock configuration for account management

Transaction: Not Required for security configuration operations
Error Handling: Security configuration error handling and authentication validation verification
```

## Transaction Requirements
```yaml
Read-Only Methods: 
  - findById, findAll, findByOrganisationId, findLogoUrlByOrganisationId, existsById, count

Transactional Methods:
  - save, saveAll, delete, deleteById

Isolation Level: READ_COMMITTED (default)
Timeout: 30 seconds
Rollback: Runtime exceptions
```

## Error Handling & Constraints
```yaml
Common Exceptions:
  - DataIntegrityViolationException: 
    * NOT NULL constraint violations (organisations_id)
    * Foreign key violations (invalid organisations_id reference)
    * Unique constraint violations
  - EntityNotFoundException: Organization setting not found by ID or organization ID
  - OptimisticLockException: Concurrent organization setting modifications
  - InvalidDataAccessApiUsageException: Invalid query parameters or organization context
  - ConstraintViolationException: Organization setting constraint violations

Validation Rules:
  - organisation: Required, must reference existing organization for settings context
  - organisationId: Required, must be valid organization identifier
  - logoUrl: Optional, maximum 255 characters for URL length
  - sessionIdleTimeout: Optional, positive integer for timeout in minutes, defaults to 10
  - registrationTokenExpiration: Optional, positive integer for expiration in minutes, defaults to 60
  - passwordResetTokenExpiration: Optional, positive integer for expiration in minutes, defaults to 60
  - maxFailedLoginAttempts: Optional, positive integer for attempt limits, defaults to 3
  - maxFailedAdditionalVerificationAttempts: Optional, positive integer for attempt limits, defaults to 3
  - maxFailedChallengeQuestionAttempts: Optional, positive integer for attempt limits, defaults to 3
  - autoUnlockAfter: Optional, positive integer for unlock time in minutes, defaults to 15

Business Constraints:
  - Organization reference must be valid for organization setting integrity and configuration functionality
  - One organization setting per organization (OneToOne relationship)
  - Organization settings must support multi-tenant requirements and functionality
  - Security settings must maintain reasonable defaults for organization functionality
  - Timeout values must be positive integers for proper configuration functionality
  - Failed attempt limits must be positive integers for security policy enforcement
  - Logo URL must be valid URL format for branding functionality
  - Organization setting lifecycle management must maintain referential integrity and configuration functionality consistency
  - Settings must support organization requirements and functionality for organization configuration
  - Configuration values must maintain security best practices and reasonable defaults
  - Organization setting operations must maintain transaction consistency and constraint integrity for configuration management
```

## Organization Setting Management Considerations

### Multi-Tenant Configuration Integration
```yaml
Tenant Isolation: Organization settings enable multi-tenant functionality through organization-specific configuration and settings isolation
Configuration Management: Organization settings management enables tenant functionality with comprehensive configuration capabilities
Settings Lifecycle: Organization settings lifecycle includes creation, configuration updates, and management operations for tenant functionality
Tenant Management: Comprehensive tenant management for organization settings functionality and configuration requirements during organization workflows
Security Control: Organization settings security control for tenant functionality and lifecycle management in configuration processing
```

### Security and Authentication Integration
```yaml
Authentication Configuration: Organization settings enable authentication functionality through security configuration and timeout management
Security Management: Security settings management with timeout configuration and comprehensive security functionality
Session Management: Organization session management with timeout configuration and comprehensive session functionality
Security Policies: Organization security policies with failed attempt limits and comprehensive security functionality
Token Management: Organization token management with expiration configuration and comprehensive token functionality for authentication workflows
```

### Branding and Customization Integration
```yaml
Branding Management: Organization branding management for customization functionality and logo configuration
Logo Configuration: Organization logo configuration with URL management and comprehensive branding functionality
Document Integration: Organization branding integration with document generation and comprehensive document functionality
Customization Management: Organization customization management with branding configuration and comprehensive customization functionality
Visual Identity: Organization visual identity management with logo and branding functionality for organization workflows
```

This comprehensive documentation provides the foundation needed to implement an exact DAO layer replacement for the OrganisationSetting repository without JPA/Hibernate dependencies, focusing on multi-tenant configuration and organization settings management patterns.
