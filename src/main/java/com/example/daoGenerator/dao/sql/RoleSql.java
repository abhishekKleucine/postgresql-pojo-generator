package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for Role entity
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public final class RoleSql {

    public static final String FIND_ALL = """
        SELECT created_at, archived, created_by, modified_at, name, modified_by, id, services_id
        FROM roles
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM roles
        """;

    public static final String FIND_BY_ID = """
        SELECT created_at, archived, created_by, modified_at, name, modified_by, id, services_id
        FROM roles
        WHERE id = :id
        """;

    public static final String INSERT = """
        INSERT INTO roles (created_at, archived, created_by, modified_at, name, modified_by, services_id)
        VALUES (:createdAt, :archived, :createdBy, :modifiedAt, :name, :modifiedBy, :servicesId)
        RETURNING id
        """;

    public static final String UPDATE = """
        UPDATE roles SET
            created_at = :createdAt,
            archived = :archived,
            created_by = :createdBy,
            modified_at = :modifiedAt,
            name = :name,
            modified_by = :modifiedBy,
            services_id = :servicesId
        WHERE id = :id
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM roles
        WHERE id = :id
        """;

    public static final String EXISTS_BY_ID = """
        SELECT COUNT(*) FROM roles
        WHERE id = :id
        """;

    public static final String FIND_BY_SERVICES_ID = """
        SELECT created_at, archived, created_by, modified_at, name, modified_by, id, services_id
        FROM roles
        WHERE services_id = :servicesId
        """;

    public static final String FIND_BY_MODIFIED_BY = """
        SELECT created_at, archived, created_by, modified_at, name, modified_by, id, services_id
        FROM roles
        WHERE modified_by = :modifiedBy
        """;

    public static final String FIND_BY_CREATED_BY = """
        SELECT created_at, archived, created_by, modified_at, name, modified_by, id, services_id
        FROM roles
        WHERE created_by = :createdBy
        """;

    private RoleSql() {
        // Utility class
    }
}
