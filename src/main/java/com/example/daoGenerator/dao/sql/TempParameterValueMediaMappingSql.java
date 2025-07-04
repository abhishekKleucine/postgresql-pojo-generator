package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for TempParameterValueMediaMapping entity
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public final class TempParameterValueMediaMappingSql {

    public static final String FIND_ALL = """
        SELECT archived, created_at, temp_parameter_values_id, medias_id, created_by, modified_at, modified_by
        FROM temp_parameter_value_media_mapping
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM temp_parameter_value_media_mapping
        """;

    public static final String FIND_BY_TEMP_PARAMETER_VALUES_ID_AND_MEDIAS_ID = """
        SELECT archived, created_at, temp_parameter_values_id, medias_id, created_by, modified_at, modified_by
        FROM temp_parameter_value_media_mapping
        WHERE temp_parameter_values_id = :tempParameterValuesId AND medias_id = :mediasId
        """;

    public static final String INSERT = """
        INSERT INTO temp_parameter_value_media_mapping (archived, created_at, temp_parameter_values_id, medias_id, created_by, modified_at, modified_by)
        VALUES (:archived, :createdAt, :tempParameterValuesId, :mediasId, :createdBy, :modifiedAt, :modifiedBy)
        """;

    public static final String UPDATE = """
        UPDATE temp_parameter_value_media_mapping SET
            archived = :archived,
            created_at = :createdAt,
            temp_parameter_values_id = :tempParameterValuesId,
            medias_id = :mediasId,
            created_by = :createdBy,
            modified_at = :modifiedAt,
            modified_by = :modifiedBy
        WHERE temp_parameter_values_id = :tempParameterValuesId AND medias_id = :mediasId
        """;

    public static final String DELETE_BY_TEMP_PARAMETER_VALUES_ID_AND_MEDIAS_ID = """
        DELETE FROM temp_parameter_value_media_mapping
        WHERE temp_parameter_values_id = :tempParameterValuesId AND medias_id = :mediasId
        """;

    public static final String EXISTS_BY_TEMP_PARAMETER_VALUES_ID_AND_MEDIAS_ID = """
        SELECT COUNT(*) FROM temp_parameter_value_media_mapping
        WHERE temp_parameter_values_id = :tempParameterValuesId AND medias_id = :mediasId
        """;

    public static final String FIND_BY_TEMP_PARAMETER_VALUES_ID = """
        SELECT archived, created_at, temp_parameter_values_id, medias_id, created_by, modified_at, modified_by
        FROM temp_parameter_value_media_mapping
        WHERE temp_parameter_values_id = :tempParameterValuesId
        """;

    public static final String FIND_BY_MODIFIED_BY = """
        SELECT archived, created_at, temp_parameter_values_id, medias_id, created_by, modified_at, modified_by
        FROM temp_parameter_value_media_mapping
        WHERE modified_by = :modifiedBy
        """;

    public static final String FIND_BY_MEDIAS_ID = """
        SELECT archived, created_at, temp_parameter_values_id, medias_id, created_by, modified_at, modified_by
        FROM temp_parameter_value_media_mapping
        WHERE medias_id = :mediasId
        """;

    public static final String FIND_BY_CREATED_BY = """
        SELECT archived, created_at, temp_parameter_values_id, medias_id, created_by, modified_at, modified_by
        FROM temp_parameter_value_media_mapping
        WHERE created_by = :createdBy
        """;

    private TempParameterValueMediaMappingSql() {
        // Utility class
    }
}
