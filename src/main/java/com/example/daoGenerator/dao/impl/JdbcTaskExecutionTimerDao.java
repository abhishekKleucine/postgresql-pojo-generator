package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.TaskExecutionTimerDao;
import com.example.pojogenerator.pojos.TaskExecutionTimer;
import com.example.daoGenerator.dao.mapper.TaskExecutionTimerRowMapper;
import com.example.daoGenerator.dao.sql.TaskExecutionTimerSql;

/**
 * Enhanced JDBC implementation of TaskExecutionTimerDao
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcTaskExecutionTimerDao implements TaskExecutionTimerDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TaskExecutionTimerRowMapper rowMapper;

    public JdbcTaskExecutionTimerDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new TaskExecutionTimerRowMapper();
    }

    @Override
    public Optional<TaskExecutionTimer> findById(Long id) {
        try {
            TaskExecutionTimer result = jdbcTemplate.queryForObject(
                TaskExecutionTimerSql.FIND_BY_ID,
                Map.of("id", id),
                rowMapper
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(TaskExecutionTimerSql.DELETE_BY_ID, Map.of("id", id));
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
            TaskExecutionTimerSql.EXISTS_BY_ID,
            Map.of("id", id),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<TaskExecutionTimer> findAll() {
        return jdbcTemplate.query(TaskExecutionTimerSql.FIND_ALL, rowMapper);
    }

    @Override
    public TaskExecutionTimer save(TaskExecutionTimer entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private TaskExecutionTimer insert(TaskExecutionTimer entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);

        MapSqlParameterSource params = createParameterMap(entity);
        Long generatedId = jdbcTemplate.queryForObject(
            TaskExecutionTimerSql.INSERT,
            params,
            Long.class
        );
        entity.setId(generatedId);
        return entity;
    }

    private TaskExecutionTimer update(TaskExecutionTimer entity) {
        entity.setModifiedAt(System.currentTimeMillis());

        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(TaskExecutionTimerSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(TaskExecutionTimerSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    @Override
    public List<TaskExecutionTimer> findByTaskExecutionsId(Long taskExecutionsId) {
        return jdbcTemplate.query(
            TaskExecutionTimerSql.FIND_BY_TASK_EXECUTIONS_ID,
            Map.of("taskExecutionsId", taskExecutionsId),
            rowMapper
        );
    }

    @Override
    public List<TaskExecutionTimer> findByModifiedBy(Long modifiedBy) {
        return jdbcTemplate.query(
            TaskExecutionTimerSql.FIND_BY_MODIFIED_BY,
            Map.of("modifiedBy", modifiedBy),
            rowMapper
        );
    }

    @Override
    public List<TaskExecutionTimer> findByCreatedBy(Long createdBy) {
        return jdbcTemplate.query(
            TaskExecutionTimerSql.FIND_BY_CREATED_BY,
            Map.of("createdBy", createdBy),
            rowMapper
        );
    }

    private MapSqlParameterSource createParameterMap(TaskExecutionTimer entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("reason", entity.getReason());
        params.addValue("createdBy", entity.getCreatedBy());
        params.addValue("modifiedAt", entity.getModifiedAt());
        params.addValue("resumedAt", entity.getResumedAt());
        params.addValue("comment", entity.getComment());
        params.addValue("pausedAt", entity.getPausedAt());
        params.addValue("modifiedBy", entity.getModifiedBy());
        params.addValue("id", entity.getId());
        params.addValue("taskExecutionsId", entity.getTaskExecutionsId());

        return params;
    }

}
