package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for ActionFacilityMapping entity
 * Key Type: NO_PRIMARY_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public final class ActionFacilityMappingSql {

    public static final String FIND_ALL = """
        SELECT created_at, facilities_id, created_by, modified_at, actions_id, modified_by
        FROM action_facility_mapping
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM action_facility_mapping
        """;

    public static final String FIND_BY_ACTIONS_ID_AND_FACILITIES_ID = """
        SELECT created_at, facilities_id, created_by, modified_at, actions_id, modified_by
        FROM action_facility_mapping
        WHERE actions_id = :actionsId AND facilities_id = :facilitiesId
        """;

    public static final String INSERT = """
        INSERT INTO action_facility_mapping (created_at, facilities_id, created_by, modified_at, actions_id, modified_by)
        VALUES (:createdAt, :facilitiesId, :createdBy, :modifiedAt, :actionsId, :modifiedBy)
        """;

    public static final String UPDATE = """
        UPDATE action_facility_mapping SET
            created_at = :createdAt,
            facilities_id = :facilitiesId,
            created_by = :createdBy,
            modified_at = :modifiedAt,
            actions_id = :actionsId,
            modified_by = :modifiedBy
        WHERE actions_id = :actionsId AND facilities_id = :facilitiesId
        """;

    public static final String DELETE_BY_ACTIONS_ID_AND_FACILITIES_ID = """
        DELETE FROM action_facility_mapping
        WHERE actions_id = :actionsId AND facilities_id = :facilitiesId
        """;

    public static final String EXISTS_BY_ACTIONS_ID_AND_FACILITIES_ID = """
        SELECT COUNT(*) FROM action_facility_mapping
        WHERE actions_id = :actionsId AND facilities_id = :facilitiesId
        """;

    public static final String FIND_BY_ACTIONS_ID = """
        SELECT created_at, facilities_id, created_by, modified_at, actions_id, modified_by
        FROM action_facility_mapping
        WHERE actions_id = :actionsId
        """;

    public static final String FIND_BY_MODIFIED_BY = """
        SELECT created_at, facilities_id, created_by, modified_at, actions_id, modified_by
        FROM action_facility_mapping
        WHERE modified_by = :modifiedBy
        """;

    public static final String FIND_BY_FACILITIES_ID = """
        SELECT created_at, facilities_id, created_by, modified_at, actions_id, modified_by
        FROM action_facility_mapping
        WHERE facilities_id = :facilitiesId
        """;

    public static final String FIND_BY_CREATED_BY = """
        SELECT created_at, facilities_id, created_by, modified_at, actions_id, modified_by
        FROM action_facility_mapping
        WHERE created_by = :createdBy
        """;

    private ActionFacilityMappingSql() {
        // Utility class
    }
}
