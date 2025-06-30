package com.example.dwiDaoGenerator.checklist.generated;

/**
 * SQL constants for Checklist entity
 * Generated from repository documentation with intelligent SQL parsing
 * Parameters converted from positional (?) to named (:paramName) based on method signatures
 */
public final class ChecklistSql {

    // Standard CRUD queries with positional parameters
    public static final String FIND_BY_ID = """
        SELECT * FROM checklists WHERE id = ?
        """;

    public static final String FIND_ALL = """
        SELECT * FROM checklists ORDER BY id
        """;

    public static final String COUNT_ALL = """
        SELECT COUNT(*) FROM checklists
        """;

    public static final String EXISTS_BY_ID = """
        SELECT EXISTS(SELECT 1 FROM checklists WHERE id = ?)
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM checklists WHERE id = ?
        """;

    public static final String INSERT = """
        INSERT INTO checklists (released_at, code, organisations_id, modified_at, released_by, use_cases_id, review_cycle, description, job_log_columns, archived, created_at, created_by, name, is_global, modified_by, color_code, state, versions_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;

    public static final String UPDATE = """
        UPDATE checklists SET
            released_at = ?,
            code = ?,
            organisations_id = ?,
            modified_at = ?,
            released_by = ?,
            use_cases_id = ?,
            review_cycle = ?,
            description = ?,
            job_log_columns = ?::jsonb,
            archived = ?,
            created_at = ?,
            created_by = ?,
            name = ?,
            is_global = ?,
            modified_by = ?,
            color_code = ?,
            state = ?,
            versions_id = ?
        WHERE id = ?
        """;

    // Custom method queries from repository documentation

    // FIND_ALL - Dynamic query (handled in service layer)
    // Purpose: Override to provide paginated specification-based queries for checklists
    // Implementation: Use CriteriaBuilder or Specification pattern

    /**
     * Find checklists by multiple IDs with custom sorting
     * Parameters: id, sort
     */
    public static final String FIND_ALL_BY_ID_IN = """
        SELECT c.* FROM checklists c
        WHERE c.id IN (?)
        """;

    /**
     * Find checklist that contains a specific task
     * Parameters: taskId
     */
    public static final String FIND_BY_TASK_ID = """
        SELECT c.* FROM checklists c
        INNER JOIN stages s ON c.id = s.checklists_id
        INNER JOIN tasks t ON s.id = t.stages_id
        WHERE t.id = ?
        """;

    /**
     * Update checklist state for lifecycle management
     * Parameters: state, checklistId
     */
    public static final String UPDATE_STATE = """
        UPDATE checklists SET state = ? WHERE id = ?
        """;

    /**
     * Get checklist code for identifier and logging purposes
     * Parameters: checklistId
     */
    public static final String GET_CHECKLIST_CODE_BY_CHECKLIST_ID = """
        SELECT code FROM checklists WHERE id = ?
        """;

    /**
     * Remove checklist access from specific facilities
     * Parameters: checklistId, facilityIds
     */
    public static final String REMOVE_CHECKLIST_FACILITY_MAPPING = """
        DELETE FROM checklist_facility_mapping
        WHERE checklists_id = ? AND facilities_id IN (?)
        """;

    /**
     * Get checklist state by stage identifier for validation
     * Parameters: stageId
     */
    public static final String FIND_BY_STAGE_ID = """
        SELECT c.state FROM checklists c
        INNER JOIN stages s ON c.id = s.checklists_id
        WHERE s.id = ?
        """;

    /**
     * Find all checklists associated with a specific use case
     * Parameters: useCaseId
     */
    public static final String FIND_BY_USE_CASE_ID = """
        SELECT c.* FROM checklists c WHERE c.use_cases_id = ?
        """;

    /**
     * Find checklist IDs by multiple states for batch operations
     * Parameters: stateSet
     */
    public static final String FIND_BY_STATE_IN_ORDER_BY_STATE_DESC = """
        SELECT id FROM checklists WHERE state IN (?) ORDER BY state DESC
        """;

    /**
     * Find checklist IDs excluding a specific state
     * Parameters: state
     */
    public static final String FIND_BY_STATE_NOT = """
        SELECT id FROM checklists WHERE state != ?
        """;

    /**
     * Get lightweight checklist information for job log operations
     * Parameters: id
     */
    public static final String FIND_CHECKLIST_INFO_BY_ID = """
        SELECT c.id as id, c.name as name, c.code as code, c.state as state
        FROM checklists c
        WHERE id = ?
        """;

    /**
     * Update checklist state and metadata during recall operation
     * Parameters: checklistId, userId
     */
    public static final String UPDATE_CHECKLIST_DURING_RECALL = """
        UPDATE checklists
        SET state = 'BEING_BUILT', created_by = ?, modified_by = ?, review_cycle = 1
        WHERE id = ?
        """;

    /**
     * Find applicable checklists for facility and organization with object type filtering
     * Parameters: facilityId, organisationId, objectTypeId, useCaseId, name, archived
     */
    public static final String FIND_ALL_CHECKLIST_IDS_FOR_CURRENT_FACILITY_AND_ORGANISATION_BY_OBJECT_TYPE_IN_DATA = """
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
        """;

    /**
     * Get checklist view projections for multiple IDs
     * Parameters: checklistIds
     */
    public static final String GET_ALL_BY_IDS_IN = """
        SELECT c.id, c.code, c.name, c.color_code as colorCode
        FROM checklists c
        WHERE id IN (?)
        ORDER BY id DESC
        """;

    /**
     * Get lightweight checklist information for job operations
     * Parameters: checklistId
     */
    public static final String GET_CHECKLIST_JOB_LITE_DTO_BY_ID = """
        SELECT c.id, c.name, c.code
        FROM checklists c
        WHERE c.id = ?
        """;

    private ChecklistSql() {
        // Utility class
    }
}
