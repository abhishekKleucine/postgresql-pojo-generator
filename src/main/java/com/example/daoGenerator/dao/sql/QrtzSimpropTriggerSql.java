package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for QrtzSimpropTrigger entity
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public final class QrtzSimpropTriggerSql {

    public static final String FIND_ALL = """
        SELECT str_prop3, str_prop2, str_prop1, trigger_name, int_prop1, trigger_group, bool_prop1, bool_prop2, int_prop2, dec_prop2, sched_name, dec_prop1, long_prop1, long_prop2
        FROM qrtz_simprop_triggers
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM qrtz_simprop_triggers
        """;

    public static final String FIND_BY_TRIGGER_NAME_AND_SCHED_NAME_AND_TRIGGER_GROUP = """
        SELECT str_prop3, str_prop2, str_prop1, trigger_name, int_prop1, trigger_group, bool_prop1, bool_prop2, int_prop2, dec_prop2, sched_name, dec_prop1, long_prop1, long_prop2
        FROM qrtz_simprop_triggers
        WHERE trigger_name = :triggerName AND sched_name = :schedName AND trigger_group = :triggerGroup
        """;

    public static final String INSERT = """
        INSERT INTO qrtz_simprop_triggers (str_prop3, str_prop2, str_prop1, trigger_name, int_prop1, trigger_group, bool_prop1, bool_prop2, int_prop2, dec_prop2, sched_name, dec_prop1, long_prop1, long_prop2)
        VALUES (:strProp3, :strProp2, :strProp1, :triggerName, :intProp1, :triggerGroup, :boolProp1, :boolProp2, :intProp2, :decProp2, :schedName, :decProp1, :longProp1, :longProp2)
        """;

    public static final String UPDATE = """
        UPDATE qrtz_simprop_triggers SET
            str_prop3 = :strProp3,
            str_prop2 = :strProp2,
            str_prop1 = :strProp1,
            trigger_name = :triggerName,
            int_prop1 = :intProp1,
            trigger_group = :triggerGroup,
            bool_prop1 = :boolProp1,
            bool_prop2 = :boolProp2,
            int_prop2 = :intProp2,
            dec_prop2 = :decProp2,
            sched_name = :schedName,
            dec_prop1 = :decProp1,
            long_prop1 = :longProp1,
            long_prop2 = :longProp2
        WHERE trigger_name = :triggerName AND sched_name = :schedName AND trigger_group = :triggerGroup
        """;

    public static final String DELETE_BY_TRIGGER_NAME_AND_SCHED_NAME_AND_TRIGGER_GROUP = """
        DELETE FROM qrtz_simprop_triggers
        WHERE trigger_name = :triggerName AND sched_name = :schedName AND trigger_group = :triggerGroup
        """;

    public static final String EXISTS_BY_TRIGGER_NAME_AND_SCHED_NAME_AND_TRIGGER_GROUP = """
        SELECT COUNT(*) FROM qrtz_simprop_triggers
        WHERE trigger_name = :triggerName AND sched_name = :schedName AND trigger_group = :triggerGroup
        """;

    public static final String FIND_BY_TRIGGER_NAME = """
        SELECT str_prop3, str_prop2, str_prop1, trigger_name, int_prop1, trigger_group, bool_prop1, bool_prop2, int_prop2, dec_prop2, sched_name, dec_prop1, long_prop1, long_prop2
        FROM qrtz_simprop_triggers
        WHERE trigger_name = :triggerName
        """;

    public static final String FIND_BY_SCHED_NAME = """
        SELECT str_prop3, str_prop2, str_prop1, trigger_name, int_prop1, trigger_group, bool_prop1, bool_prop2, int_prop2, dec_prop2, sched_name, dec_prop1, long_prop1, long_prop2
        FROM qrtz_simprop_triggers
        WHERE sched_name = :schedName
        """;

    public static final String FIND_BY_TRIGGER_GROUP = """
        SELECT str_prop3, str_prop2, str_prop1, trigger_name, int_prop1, trigger_group, bool_prop1, bool_prop2, int_prop2, dec_prop2, sched_name, dec_prop1, long_prop1, long_prop2
        FROM qrtz_simprop_triggers
        WHERE trigger_group = :triggerGroup
        """;

    private QrtzSimpropTriggerSql() {
        // Utility class
    }
}
