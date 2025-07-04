package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.JobAuditDao;
import com.example.pojogenerator.pojos.JobAudit;
import com.example.daoGenerator.dao.mapper.JobAuditRowMapper;
import com.example.daoGenerator.dao.sql.JobAuditSql;

/**
 * Enhanced JDBC implementation of JobAuditDao
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcJobAuditDao implements JobAuditDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JobAuditRowMapper rowMapper;

    public JdbcJobAuditDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new JobAuditRowMapper();
    }

    @Override
    public Optional<JobAudit> findById(Long id) {
        try {
            JobAudit result = jdbcTemplate.queryForObject(
                JobAuditSql.FIND_BY_ID,
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
        jdbcTemplate.update(JobAuditSql.DELETE_BY_ID, Map.of("id", id));
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
            JobAuditSql.EXISTS_BY_ID,
            Map.of("id", id),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<JobAudit> findAll() {
        return jdbcTemplate.query(JobAuditSql.FIND_ALL, rowMapper);
    }

    @Override
    public JobAudit save(JobAudit entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private JobAudit insert(JobAudit entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();

        MapSqlParameterSource params = createParameterMap(entity);
        Long generatedId = jdbcTemplate.queryForObject(
            JobAuditSql.INSERT,
            params,
            Long.class
        );
        entity.setId(generatedId);
        return entity;
    }

    private JobAudit update(JobAudit entity) {
        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(JobAuditSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(JobAuditSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    private MapSqlParameterSource createParameterMap(JobAudit entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("organisationsId", entity.getOrganisationsId());
        params.addValue("tasksId", entity.getTasksId());
        params.addValue("action", entity.getAction());
        params.addValue("details", entity.getDetails());
        params.addValue("id", entity.getId());
        params.addValue("stagesId", entity.getStagesId());
        params.addValue("triggeredAt", entity.getTriggeredAt());
        params.addValue("jobsId", entity.getJobsId());
        if (entity.getParameters() != null) {
            params.addValue("parameters", entity.getParameters().toString());
        } else {
            params.addValue("parameters", null);
        }
        params.addValue("triggeredBy", entity.getTriggeredBy());

        return params;
    }

}
