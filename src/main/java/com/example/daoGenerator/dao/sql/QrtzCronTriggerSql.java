package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for QrtzCronTrigger entity
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public final class QrtzCronTriggerSql {

    public static final String FIND_ALL = """
        SELECT cron_expression, trigger_group, trigger_name, time_zone_id, sched_name
        FROM qrtz_cron_triggers
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM qrtz_cron_triggers
        """;

    public static final String FIND_BY_TRIGGER_NAME_AND_SCHED_NAME_AND_TRIGGER_GROUP = """
        SELECT cron_expression, trigger_group, trigger_name, time_zone_id, sched_name
        FROM qrtz_cron_triggers
        WHERE trigger_name = :triggerName AND sched_name = :schedName AND trigger_group = :triggerGroup
        """;

    public static final String INSERT = """
        INSERT INTO qrtz_cron_triggers (cron_expression, trigger_group, trigger_name, time_zone_id, sched_name)
        VALUES (:cronExpression, :triggerGroup, :triggerName, :timeZoneId, :schedName)
        """;

    public static final String UPDATE = """
        UPDATE qrtz_cron_triggers SET
            cron_expression = :cronExpression,
            trigger_group = :triggerGroup,
            trigger_name = :triggerName,
            time_zone_id = :timeZoneId,
            sched_name = :schedName
        WHERE trigger_name = :triggerName AND sched_name = :schedName AND trigger_group = :triggerGroup
        """;

    public static final String DELETE_BY_TRIGGER_NAME_AND_SCHED_NAME_AND_TRIGGER_GROUP = """
        DELETE FROM qrtz_cron_triggers
        WHERE trigger_name = :triggerName AND sched_name = :schedName AND trigger_group = :triggerGroup
        """;

    public static final String EXISTS_BY_TRIGGER_NAME_AND_SCHED_NAME_AND_TRIGGER_GROUP = """
        SELECT COUNT(*) FROM qrtz_cron_triggers
        WHERE trigger_name = :triggerName AND sched_name = :schedName AND trigger_group = :triggerGroup
        """;

    public static final String FIND_BY_TRIGGER_NAME = """
        SELECT cron_expression, trigger_group, trigger_name, time_zone_id, sched_name
        FROM qrtz_cron_triggers
        WHERE trigger_name = :triggerName
        """;

    public static final String FIND_BY_SCHED_NAME = """
        SELECT cron_expression, trigger_group, trigger_name, time_zone_id, sched_name
        FROM qrtz_cron_triggers
        WHERE sched_name = :schedName
        """;

    public static final String FIND_BY_TRIGGER_GROUP = """
        SELECT cron_expression, trigger_group, trigger_name, time_zone_id, sched_name
        FROM qrtz_cron_triggers
        WHERE trigger_group = :triggerGroup
        """;

    private QrtzCronTriggerSql() {
        // Utility class
    }
}
