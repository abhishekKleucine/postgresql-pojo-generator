package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.AutomationDao;
import com.example.pojogenerator.pojos.Automation;
import com.example.daoGenerator.dao.mapper.AutomationRowMapper;
import com.example.daoGenerator.dao.sql.AutomationSql;

/**
 * Enhanced JDBC implementation of AutomationDao
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcAutomationDao implements AutomationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AutomationRowMapper rowMapper;

    public JdbcAutomationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new AutomationRowMapper();
    }

    @Override
    public Optional<Automation> findById(Long id) {
        try {
            Automation result = jdbcTemplate.queryForObject(
                AutomationSql.FIND_BY_ID,
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
        jdbcTemplate.update(AutomationSql.DELETE_BY_ID, Map.of("id", id));
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
            AutomationSql.EXISTS_BY_ID,
            Map.of("id", id),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<Automation> findAll() {
        return jdbcTemplate.query(AutomationSql.FIND_ALL, rowMapper);
    }

    @Override
    public Automation save(Automation entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private Automation insert(Automation entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);

        MapSqlParameterSource params = createParameterMap(entity);
        Long generatedId = jdbcTemplate.queryForObject(
            AutomationSql.INSERT,
            params,
            Long.class
        );
        entity.setId(generatedId);
        return entity;
    }

    private Automation update(Automation entity) {
        entity.setModifiedAt(System.currentTimeMillis());

        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(AutomationSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(AutomationSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    private MapSqlParameterSource createParameterMap(Automation entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("actionType", entity.getActionType());
        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("archived", entity.getArchived());
        params.addValue("targetEntityType", entity.getTargetEntityType());
        params.addValue("createdBy", entity.getCreatedBy());
        if (entity.getTriggerDetails() != null) {
            params.addValue("triggerDetails", entity.getTriggerDetails().toString());
        } else {
            params.addValue("triggerDetails", null);
        }
        params.addValue("modifiedAt", entity.getModifiedAt());
        params.addValue("modifiedBy", entity.getModifiedBy());
        params.addValue("id", entity.getId());
        params.addValue("triggerType", entity.getTriggerType());
        if (entity.getActionDetails() != null) {
            params.addValue("actionDetails", entity.getActionDetails().toString());
        } else {
            params.addValue("actionDetails", null);
        }
        params.addValue("type", entity.getType());

        return params;
    }

}
