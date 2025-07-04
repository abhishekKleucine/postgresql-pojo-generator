package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for Action entity
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public final class ActionSql {

    public static final String FIND_ALL = """
        SELECT code, modified_at, description, checklists_id, success_message, created_at, archived, created_by, name, modified_by, id, trigger_type, trigger_entity_id, failure_message
        FROM actions
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM actions
        """;

    public static final String FIND_BY_ID = """
        SELECT code, modified_at, description, checklists_id, success_message, created_at, archived, created_by, name, modified_by, id, trigger_type, trigger_entity_id, failure_message
        FROM actions
        WHERE id = :id
        """;

    public static final String INSERT = """
        INSERT INTO actions (code, modified_at, description, checklists_id, success_message, created_at, archived, created_by, name, modified_by, trigger_type, trigger_entity_id, failure_message)
        VALUES (:code, :modifiedAt, :description, :checklistsId, :successMessage, :createdAt, :archived, :createdBy, :name, :modifiedBy, :triggerType, :triggerEntityId, :failureMessage)
        RETURNING id
        """;

    public static final String UPDATE = """
        UPDATE actions SET
            code = :code,
            modified_at = :modifiedAt,
            description = :description,
            checklists_id = :checklistsId,
            success_message = :successMessage,
            created_at = :createdAt,
            archived = :archived,
            created_by = :createdBy,
            name = :name,
            modified_by = :modifiedBy,
            trigger_type = :triggerType,
            trigger_entity_id = :triggerEntityId,
            failure_message = :failureMessage
        WHERE id = :id
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM actions
        WHERE id = :id
        """;

    public static final String EXISTS_BY_ID = """
        SELECT COUNT(*) FROM actions
        WHERE id = :id
        """;

    public static final String FIND_BY_CHECKLISTS_ID = """
        SELECT code, modified_at, description, checklists_id, success_message, created_at, archived, created_by, name, modified_by, id, trigger_type, trigger_entity_id, failure_message
        FROM actions
        WHERE checklists_id = :checklistsId
        """;

    public static final String FIND_BY_MODIFIED_BY = """
        SELECT code, modified_at, description, checklists_id, success_message, created_at, archived, created_by, name, modified_by, id, trigger_type, trigger_entity_id, failure_message
        FROM actions
        WHERE modified_by = :modifiedBy
        """;

    public static final String FIND_BY_CREATED_BY = """
        SELECT code, modified_at, description, checklists_id, success_message, created_at, archived, created_by, name, modified_by, id, trigger_type, trigger_entity_id, failure_message
        FROM actions
        WHERE created_by = :createdBy
        """;

    private ActionSql() {
        // Utility class
    }
}
