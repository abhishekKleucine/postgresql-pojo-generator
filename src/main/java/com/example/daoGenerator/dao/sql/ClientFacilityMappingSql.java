package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for ClientFacilityMapping entity
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public final class ClientFacilityMappingSql {

    public static final String FIND_ALL = """
        SELECT created_at, facilities_id, client_id, created_by, modified_at, modified_by
        FROM client_facility_mapping
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM client_facility_mapping
        """;

    public static final String FIND_BY_FACILITIES_ID_AND_CLIENT_ID = """
        SELECT created_at, facilities_id, client_id, created_by, modified_at, modified_by
        FROM client_facility_mapping
        WHERE facilities_id = :facilitiesId AND client_id = :clientId
        """;

    public static final String INSERT = """
        INSERT INTO client_facility_mapping (created_at, facilities_id, client_id, created_by, modified_at, modified_by)
        VALUES (:createdAt, :facilitiesId, :clientId, :createdBy, :modifiedAt, :modifiedBy)
        """;

    public static final String UPDATE = """
        UPDATE client_facility_mapping SET
            created_at = :createdAt,
            facilities_id = :facilitiesId,
            client_id = :clientId,
            created_by = :createdBy,
            modified_at = :modifiedAt,
            modified_by = :modifiedBy
        WHERE facilities_id = :facilitiesId AND client_id = :clientId
        """;

    public static final String DELETE_BY_FACILITIES_ID_AND_CLIENT_ID = """
        DELETE FROM client_facility_mapping
        WHERE facilities_id = :facilitiesId AND client_id = :clientId
        """;

    public static final String EXISTS_BY_FACILITIES_ID_AND_CLIENT_ID = """
        SELECT COUNT(*) FROM client_facility_mapping
        WHERE facilities_id = :facilitiesId AND client_id = :clientId
        """;

    public static final String FIND_BY_MODIFIED_BY = """
        SELECT created_at, facilities_id, client_id, created_by, modified_at, modified_by
        FROM client_facility_mapping
        WHERE modified_by = :modifiedBy
        """;

    public static final String FIND_BY_FACILITIES_ID = """
        SELECT created_at, facilities_id, client_id, created_by, modified_at, modified_by
        FROM client_facility_mapping
        WHERE facilities_id = :facilitiesId
        """;

    public static final String FIND_BY_CREATED_BY = """
        SELECT created_at, facilities_id, client_id, created_by, modified_at, modified_by
        FROM client_facility_mapping
        WHERE created_by = :createdBy
        """;

    public static final String FIND_BY_CLIENT_ID = """
        SELECT created_at, facilities_id, client_id, created_by, modified_at, modified_by
        FROM client_facility_mapping
        WHERE client_id = :clientId
        """;

    private ClientFacilityMappingSql() {
        // Utility class
    }
}
