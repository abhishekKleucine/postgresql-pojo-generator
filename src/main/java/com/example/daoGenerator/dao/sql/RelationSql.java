package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for Relation entity
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public final class RelationSql {

    public static final String FIND_ALL = """
        SELECT variables, object_type_id, order_tree, display_name, modified_at, external_id, checklists_id, collection, url_path, cardinality, created_at, created_by, modified_by, id, validations, is_mandatory
        FROM relations
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM relations
        """;

    public static final String FIND_BY_ID = """
        SELECT variables, object_type_id, order_tree, display_name, modified_at, external_id, checklists_id, collection, url_path, cardinality, created_at, created_by, modified_by, id, validations, is_mandatory
        FROM relations
        WHERE id = :id
        """;

    public static final String INSERT = """
        INSERT INTO relations (variables, object_type_id, order_tree, display_name, modified_at, external_id, checklists_id, collection, url_path, cardinality, created_at, created_by, modified_by, validations, is_mandatory)
        VALUES (:variables::jsonb, :objectTypeId, :orderTree, :displayName, :modifiedAt, :externalId, :checklistsId, :collection, :urlPath, :cardinality, :createdAt, :createdBy, :modifiedBy, :validations::jsonb, :isMandatory)
        RETURNING id
        """;

    public static final String UPDATE = """
        UPDATE relations SET
            variables = :variables::jsonb,
            object_type_id = :objectTypeId,
            order_tree = :orderTree,
            display_name = :displayName,
            modified_at = :modifiedAt,
            external_id = :externalId,
            checklists_id = :checklistsId,
            collection = :collection,
            url_path = :urlPath,
            cardinality = :cardinality,
            created_at = :createdAt,
            created_by = :createdBy,
            modified_by = :modifiedBy,
            validations = :validations::jsonb,
            is_mandatory = :isMandatory
        WHERE id = :id
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM relations
        WHERE id = :id
        """;

    public static final String EXISTS_BY_ID = """
        SELECT COUNT(*) FROM relations
        WHERE id = :id
        """;

    public static final String FIND_BY_CHECKLISTS_ID = """
        SELECT variables, object_type_id, order_tree, display_name, modified_at, external_id, checklists_id, collection, url_path, cardinality, created_at, created_by, modified_by, id, validations, is_mandatory
        FROM relations
        WHERE checklists_id = :checklistsId
        """;

    private RelationSql() {
        // Utility class
    }
}
