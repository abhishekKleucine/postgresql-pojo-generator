package com.example.daoGenerator.dao.sql;

/**
 * Enhanced SQL constants for QrtzFiredTrigger entity
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public final class QrtzFiredTriggerSql {

    public static final String FIND_ALL = """
        SELECT job_name, trigger_name, instance_name, requests_recovery, job_group, priority, is_nonconcurrent, entry_id, fired_time, trigger_group, sched_time, sched_name, state
        FROM qrtz_fired_triggers
        """;

    public static final String COUNT = """
        SELECT COUNT(*) FROM qrtz_fired_triggers
        """;

    public static final String FIND_BY_SCHED_NAME_AND_ENTRY_ID = """
        SELECT job_name, trigger_name, instance_name, requests_recovery, job_group, priority, is_nonconcurrent, entry_id, fired_time, trigger_group, sched_time, sched_name, state
        FROM qrtz_fired_triggers
        WHERE sched_name = :schedName AND entry_id = :entryId
        """;

    public static final String INSERT = """
        INSERT INTO qrtz_fired_triggers (job_name, trigger_name, instance_name, requests_recovery, job_group, priority, is_nonconcurrent, entry_id, fired_time, trigger_group, sched_time, sched_name, state)
        VALUES (:jobName, :triggerName, :instanceName, :requestsRecovery, :jobGroup, :priority, :isNonconcurrent, :entryId, :firedTime, :triggerGroup, :schedTime, :schedName, :state)
        """;

    public static final String UPDATE = """
        UPDATE qrtz_fired_triggers SET
            job_name = :jobName,
            trigger_name = :triggerName,
            instance_name = :instanceName,
            requests_recovery = :requestsRecovery,
            job_group = :jobGroup,
            priority = :priority,
            is_nonconcurrent = :isNonconcurrent,
            entry_id = :entryId,
            fired_time = :firedTime,
            trigger_group = :triggerGroup,
            sched_time = :schedTime,
            sched_name = :schedName,
            state = :state
        WHERE sched_name = :schedName AND entry_id = :entryId
        """;

    public static final String DELETE_BY_SCHED_NAME_AND_ENTRY_ID = """
        DELETE FROM qrtz_fired_triggers
        WHERE sched_name = :schedName AND entry_id = :entryId
        """;

    public static final String EXISTS_BY_SCHED_NAME_AND_ENTRY_ID = """
        SELECT COUNT(*) FROM qrtz_fired_triggers
        WHERE sched_name = :schedName AND entry_id = :entryId
        """;

    private QrtzFiredTriggerSql() {
        // Utility class
    }
}
