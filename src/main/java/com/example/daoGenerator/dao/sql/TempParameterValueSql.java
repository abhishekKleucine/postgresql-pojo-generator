package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for TempParameterValue entity
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public final class TempParameterValueSql {

    public static final String FIND_ALL = """
        SELECT reason, hidden, modified_at, verified, parameters_id, client_epoch, version, created_at, created_by, has_variations, impacted_by, modified_by, id, state, task_executions_id, choices, value, jobs_id, parameter_value_approval_id
        FROM temp_parameter_values
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM temp_parameter_values
        """;

    public static final String FIND_BY_ID = """
        SELECT reason, hidden, modified_at, verified, parameters_id, client_epoch, version, created_at, created_by, has_variations, impacted_by, modified_by, id, state, task_executions_id, choices, value, jobs_id, parameter_value_approval_id
        FROM temp_parameter_values
        WHERE id = :id
        """;

    public static final String INSERT = """
        INSERT INTO temp_parameter_values (reason, hidden, modified_at, verified, parameters_id, client_epoch, version, created_at, created_by, has_variations, impacted_by, modified_by, state, task_executions_id, choices, value, jobs_id, parameter_value_approval_id)
        VALUES (:reason, :hidden, :modifiedAt, :verified, :parametersId, :clientEpoch, :version, :createdAt, :createdBy, :hasVariations, :impactedBy::jsonb, :modifiedBy, :state, :taskExecutionsId, :choices::jsonb, :value, :jobsId, :parameterValueApprovalId)
        RETURNING id
        """;

    public static final String UPDATE = """
        UPDATE temp_parameter_values SET
            reason = :reason,
            hidden = :hidden,
            modified_at = :modifiedAt,
            verified = :verified,
            parameters_id = :parametersId,
            client_epoch = :clientEpoch,
            version = :version,
            created_at = :createdAt,
            created_by = :createdBy,
            has_variations = :hasVariations,
            impacted_by = :impactedBy::jsonb,
            modified_by = :modifiedBy,
            state = :state,
            task_executions_id = :taskExecutionsId,
            choices = :choices::jsonb,
            value = :value,
            jobs_id = :jobsId,
            parameter_value_approval_id = :parameterValueApprovalId
        WHERE id = :id
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM temp_parameter_values
        WHERE id = :id
        """;

    public static final String EXISTS_BY_ID = """
        SELECT COUNT(*) FROM temp_parameter_values
        WHERE id = :id
        """;

    public static final String FIND_BY_PARAMETERS_ID = """
        SELECT reason, hidden, modified_at, verified, parameters_id, client_epoch, version, created_at, created_by, has_variations, impacted_by, modified_by, id, state, task_executions_id, choices, value, jobs_id, parameter_value_approval_id
        FROM temp_parameter_values
        WHERE parameters_id = :parametersId
        """;

    public static final String FIND_BY_TASK_EXECUTIONS_ID = """
        SELECT reason, hidden, modified_at, verified, parameters_id, client_epoch, version, created_at, created_by, has_variations, impacted_by, modified_by, id, state, task_executions_id, choices, value, jobs_id, parameter_value_approval_id
        FROM temp_parameter_values
        WHERE task_executions_id = :taskExecutionsId
        """;

    public static final String FIND_BY_PARAMETER_VALUE_APPROVAL_ID = """
        SELECT reason, hidden, modified_at, verified, parameters_id, client_epoch, version, created_at, created_by, has_variations, impacted_by, modified_by, id, state, task_executions_id, choices, value, jobs_id, parameter_value_approval_id
        FROM temp_parameter_values
        WHERE parameter_value_approval_id = :parameterValueApprovalId
        """;

    public static final String FIND_BY_MODIFIED_BY = """
        SELECT reason, hidden, modified_at, verified, parameters_id, client_epoch, version, created_at, created_by, has_variations, impacted_by, modified_by, id, state, task_executions_id, choices, value, jobs_id, parameter_value_approval_id
        FROM temp_parameter_values
        WHERE modified_by = :modifiedBy
        """;

    public static final String FIND_BY_JOBS_ID = """
        SELECT reason, hidden, modified_at, verified, parameters_id, client_epoch, version, created_at, created_by, has_variations, impacted_by, modified_by, id, state, task_executions_id, choices, value, jobs_id, parameter_value_approval_id
        FROM temp_parameter_values
        WHERE jobs_id = :jobsId
        """;

    public static final String FIND_BY_CREATED_BY = """
        SELECT reason, hidden, modified_at, verified, parameters_id, client_epoch, version, created_at, created_by, has_variations, impacted_by, modified_by, id, state, task_executions_id, choices, value, jobs_id, parameter_value_approval_id
        FROM temp_parameter_values
        WHERE created_by = :createdBy
        """;

    private TempParameterValueSql() {
        // Utility class
    }
}
