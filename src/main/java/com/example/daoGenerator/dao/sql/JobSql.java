package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for Job entity
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public final class JobSql {

    public static final String FIND_ALL = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM jobs
        """;

    public static final String FIND_BY_ID = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE id = :id
        """;

    public static final String INSERT = """
        INSERT INTO jobs (schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, state)
        VALUES (:schedulersId, :expectedStartDate, :facilitiesId, :code, :organisationsId, :startedBy, :modifiedAt, :useCasesId, :checklistAncestorId, :startedAt, :checklistsId, :endedBy, :isScheduled, :createdAt, :expectedEndDate, :createdBy, :endedAt, :modifiedBy, :state)
        RETURNING id
        """;

    public static final String UPDATE = """
        UPDATE jobs SET
            schedulers_id = :schedulersId,
            expected_start_date = :expectedStartDate,
            facilities_id = :facilitiesId,
            code = :code,
            organisations_id = :organisationsId,
            started_by = :startedBy,
            modified_at = :modifiedAt,
            use_cases_id = :useCasesId,
            checklist_ancestor_id = :checklistAncestorId,
            started_at = :startedAt,
            checklists_id = :checklistsId,
            ended_by = :endedBy,
            is_scheduled = :isScheduled,
            created_at = :createdAt,
            expected_end_date = :expectedEndDate,
            created_by = :createdBy,
            ended_at = :endedAt,
            modified_by = :modifiedBy,
            state = :state
        WHERE id = :id
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM jobs
        WHERE id = :id
        """;

    public static final String EXISTS_BY_ID = """
        SELECT COUNT(*) FROM jobs
        WHERE id = :id
        """;

    public static final String FIND_BY_USE_CASES_ID = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE use_cases_id = :useCasesId
        """;

    public static final String FIND_BY_CHECKLISTS_ID = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE checklists_id = :checklistsId
        """;

    public static final String FIND_BY_MODIFIED_BY = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE modified_by = :modifiedBy
        """;

    public static final String FIND_BY_ORGANISATIONS_ID = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE organisations_id = :organisationsId
        """;

    public static final String FIND_BY_FACILITIES_ID = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE facilities_id = :facilitiesId
        """;

    public static final String FIND_BY_ENDED_BY = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE ended_by = :endedBy
        """;

    public static final String FIND_BY_CREATED_BY = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE created_by = :createdBy
        """;

    public static final String FIND_BY_STARTED_BY = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE started_by = :startedBy
        """;

    public static final String FIND_BY_SCHEDULERS_ID = """
        SELECT schedulers_id, expected_start_date, facilities_id, code, organisations_id, started_by, modified_at, use_cases_id, checklist_ancestor_id, started_at, checklists_id, ended_by, is_scheduled, created_at, expected_end_date, created_by, ended_at, modified_by, id, state
        FROM jobs
        WHERE schedulers_id = :schedulersId
        """;

    private JobSql() {
        // Utility class
    }
}
