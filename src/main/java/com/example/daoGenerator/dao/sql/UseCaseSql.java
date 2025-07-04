package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for UseCase entity
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public final class UseCaseSql {

    public static final String FIND_ALL = """
        SELECT archived, created_at, metadata, order_tree, created_by, modified_at, name, description, modified_by, id, label
        FROM use_cases
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM use_cases
        """;

    public static final String FIND_BY_ID = """
        SELECT archived, created_at, metadata, order_tree, created_by, modified_at, name, description, modified_by, id, label
        FROM use_cases
        WHERE id = :id
        """;

    public static final String INSERT = """
        INSERT INTO use_cases (archived, created_at, metadata, order_tree, created_by, modified_at, name, description, modified_by, label)
        VALUES (:archived, :createdAt, :metadata::jsonb, :orderTree, :createdBy, :modifiedAt, :name, :description, :modifiedBy, :label)
        RETURNING id
        """;

    public static final String UPDATE = """
        UPDATE use_cases SET
            archived = :archived,
            created_at = :createdAt,
            metadata = :metadata::jsonb,
            order_tree = :orderTree,
            created_by = :createdBy,
            modified_at = :modifiedAt,
            name = :name,
            description = :description,
            modified_by = :modifiedBy,
            label = :label
        WHERE id = :id
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM use_cases
        WHERE id = :id
        """;

    public static final String EXISTS_BY_ID = """
        SELECT COUNT(*) FROM use_cases
        WHERE id = :id
        """;

    public static final String FIND_BY_MODIFIED_BY = """
        SELECT archived, created_at, metadata, order_tree, created_by, modified_at, name, description, modified_by, id, label
        FROM use_cases
        WHERE modified_by = :modifiedBy
        """;

    public static final String FIND_BY_CREATED_BY = """
        SELECT archived, created_at, metadata, order_tree, created_by, modified_at, name, description, modified_by, id, label
        FROM use_cases
        WHERE created_by = :createdBy
        """;

    private UseCaseSql() {
        // Utility class
    }
}
