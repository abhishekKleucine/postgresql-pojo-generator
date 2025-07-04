package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.QrtzSimpleTriggerDao;
import com.example.pojogenerator.pojos.QrtzSimpleTrigger;
import com.example.daoGenerator.dao.mapper.QrtzSimpleTriggerRowMapper;
import com.example.daoGenerator.dao.sql.QrtzSimpleTriggerSql;

/**
 * Enhanced JDBC implementation of QrtzSimpleTriggerDao
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcQrtzSimpleTriggerDao implements QrtzSimpleTriggerDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final QrtzSimpleTriggerRowMapper rowMapper;

    public JdbcQrtzSimpleTriggerDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new QrtzSimpleTriggerRowMapper();
    }

    @Override
    public Optional<QrtzSimpleTrigger> findByTriggerNameAndSchedNameAndTriggerGroup(String triggerName, String schedName, String triggerGroup) {
        try {
            QrtzSimpleTrigger result = jdbcTemplate.queryForObject(
                QrtzSimpleTriggerSql.FIND_BY_TRIGGER_NAME_AND_SCHED_NAME_AND_TRIGGER_GROUP,
                Map.of("triggerName", triggerName, "schedName", schedName, "triggerGroup", triggerGroup),
                rowMapper
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByTriggerNameAndSchedNameAndTriggerGroup(String triggerName, String schedName, String triggerGroup) {
        jdbcTemplate.update(QrtzSimpleTriggerSql.DELETE_BY_TRIGGER_NAME_AND_SCHED_NAME_AND_TRIGGER_GROUP, Map.of("triggerName", triggerName, "schedName", schedName, "triggerGroup", triggerGroup));
    }

    @Override
    public boolean existsByTriggerNameAndSchedNameAndTriggerGroup(String triggerName, String schedName, String triggerGroup) {
        Integer count = jdbcTemplate.queryForObject(
            QrtzSimpleTriggerSql.EXISTS_BY_TRIGGER_NAME_AND_SCHED_NAME_AND_TRIGGER_GROUP,
            Map.of("triggerName", triggerName, "schedName", schedName, "triggerGroup", triggerGroup),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<QrtzSimpleTrigger> findAll() {
        return jdbcTemplate.query(QrtzSimpleTriggerSql.FIND_ALL, rowMapper);
    }

    @Override
    public QrtzSimpleTrigger save(QrtzSimpleTrigger entity) {
        if (existsByTriggerNameAndSchedNameAndTriggerGroup(entity.getTriggerName(), entity.getSchedName(), entity.getTriggerGroup())) {
            return update(entity);
        } else {
            return insert(entity);
        }
    }

    private QrtzSimpleTrigger insert(QrtzSimpleTrigger entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();

        MapSqlParameterSource params = createParameterMap(entity);
        jdbcTemplate.update(QrtzSimpleTriggerSql.INSERT, params);
        return entity;
    }

    private QrtzSimpleTrigger update(QrtzSimpleTrigger entity) {
        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(QrtzSimpleTriggerSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(QrtzSimpleTriggerSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    @Override
    public List<QrtzSimpleTrigger> findByTriggerName(Long triggerName) {
        return jdbcTemplate.query(
            QrtzSimpleTriggerSql.FIND_BY_TRIGGER_NAME,
            Map.of("triggerName", triggerName),
            rowMapper
        );
    }

    @Override
    public List<QrtzSimpleTrigger> findBySchedName(Long schedName) {
        return jdbcTemplate.query(
            QrtzSimpleTriggerSql.FIND_BY_SCHED_NAME,
            Map.of("schedName", schedName),
            rowMapper
        );
    }

    @Override
    public List<QrtzSimpleTrigger> findByTriggerGroup(Long triggerGroup) {
        return jdbcTemplate.query(
            QrtzSimpleTriggerSql.FIND_BY_TRIGGER_GROUP,
            Map.of("triggerGroup", triggerGroup),
            rowMapper
        );
    }

    private MapSqlParameterSource createParameterMap(QrtzSimpleTrigger entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("triggerGroup", entity.getTriggerGroup());
        params.addValue("triggerName", entity.getTriggerName());
        params.addValue("repeatInterval", entity.getRepeatInterval());
        params.addValue("schedName", entity.getSchedName());
        params.addValue("timesTriggered", entity.getTimesTriggered());
        params.addValue("repeatCount", entity.getRepeatCount());

        return params;
    }

}
