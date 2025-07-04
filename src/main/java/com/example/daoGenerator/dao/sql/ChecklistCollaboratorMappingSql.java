package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for ChecklistCollaboratorMapping entity
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public final class ChecklistCollaboratorMappingSql {

    public static final String FIND_ALL = """
        SELECT phase, created_at, order_tree, created_by, modified_at, users_id, modified_by, checklists_id, id, state, type, phase_type
        FROM checklist_collaborator_mapping
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM checklist_collaborator_mapping
        """;

    public static final String FIND_BY_ID = """
        SELECT phase, created_at, order_tree, created_by, modified_at, users_id, modified_by, checklists_id, id, state, type, phase_type
        FROM checklist_collaborator_mapping
        WHERE id = :id
        """;

    public static final String INSERT = """
        INSERT INTO checklist_collaborator_mapping (phase, created_at, order_tree, created_by, modified_at, users_id, modified_by, checklists_id, state, type, phase_type)
        VALUES (:phase, :createdAt, :orderTree, :createdBy, :modifiedAt, :usersId, :modifiedBy, :checklistsId, :state, :type, :phaseType)
        RETURNING id
        """;

    public static final String UPDATE = """
        UPDATE checklist_collaborator_mapping SET
            phase = :phase,
            created_at = :createdAt,
            order_tree = :orderTree,
            created_by = :createdBy,
            modified_at = :modifiedAt,
            users_id = :usersId,
            modified_by = :modifiedBy,
            checklists_id = :checklistsId,
            state = :state,
            type = :type,
            phase_type = :phaseType
        WHERE id = :id
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM checklist_collaborator_mapping
        WHERE id = :id
        """;

    public static final String EXISTS_BY_ID = """
        SELECT COUNT(*) FROM checklist_collaborator_mapping
        WHERE id = :id
        """;

    public static final String FIND_BY_CHECKLISTS_ID = """
        SELECT phase, created_at, order_tree, created_by, modified_at, users_id, modified_by, checklists_id, id, state, type, phase_type
        FROM checklist_collaborator_mapping
        WHERE checklists_id = :checklistsId
        """;

    public static final String FIND_BY_MODIFIED_BY = """
        SELECT phase, created_at, order_tree, created_by, modified_at, users_id, modified_by, checklists_id, id, state, type, phase_type
        FROM checklist_collaborator_mapping
        WHERE modified_by = :modifiedBy
        """;

    public static final String FIND_BY_USERS_ID = """
        SELECT phase, created_at, order_tree, created_by, modified_at, users_id, modified_by, checklists_id, id, state, type, phase_type
        FROM checklist_collaborator_mapping
        WHERE users_id = :usersId
        """;

    public static final String FIND_BY_CREATED_BY = """
        SELECT phase, created_at, order_tree, created_by, modified_at, users_id, modified_by, checklists_id, id, state, type, phase_type
        FROM checklist_collaborator_mapping
        WHERE created_by = :createdBy
        """;

    private ChecklistCollaboratorMappingSql() {
        // Utility class
    }
}
