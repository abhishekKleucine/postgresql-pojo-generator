package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for ParameterRule entity
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public final class ParameterRuleSql {

    public static final String FIND_ALL = """
        SELECT input, visibility, id, rules_id, operator
        FROM parameter_rules
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM parameter_rules
        """;

    public static final String FIND_BY_ID = """
        SELECT input, visibility, id, rules_id, operator
        FROM parameter_rules
        WHERE id = :id
        """;

    public static final String INSERT = """
        INSERT INTO parameter_rules (input, visibility, rules_id, operator)
        VALUES (:input, :visibility, :rulesId, :operator)
        RETURNING id
        """;

    public static final String UPDATE = """
        UPDATE parameter_rules SET
            input = :input,
            visibility = :visibility,
            rules_id = :rulesId,
            operator = :operator
        WHERE id = :id
        """;

    public static final String DELETE_BY_ID = """
        DELETE FROM parameter_rules
        WHERE id = :id
        """;

    public static final String EXISTS_BY_ID = """
        SELECT COUNT(*) FROM parameter_rules
        WHERE id = :id
        """;

    private ParameterRuleSql() {
        // Utility class
    }
}
