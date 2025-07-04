package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.ActionFacilityMappingDao;
import com.example.pojogenerator.pojos.ActionFacilityMapping;
import com.example.daoGenerator.dao.mapper.ActionFacilityMappingRowMapper;
import com.example.daoGenerator.dao.sql.ActionFacilityMappingSql;

/**
 * Enhanced JDBC implementation of ActionFacilityMappingDao
 * Key Type: NO_PRIMARY_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcActionFacilityMappingDao implements ActionFacilityMappingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ActionFacilityMappingRowMapper rowMapper;

    public JdbcActionFacilityMappingDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new ActionFacilityMappingRowMapper();
    }

    @Override
    public Optional<ActionFacilityMapping> findByActionsIdAndFacilitiesId(Long actionsId, Long facilitiesId) {
        try {
            ActionFacilityMapping result = jdbcTemplate.queryForObject(
                ActionFacilityMappingSql.FIND_BY_ACTIONS_ID_AND_FACILITIES_ID,
                Map.of("actionsId", actionsId, "facilitiesId", facilitiesId),
                rowMapper
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByActionsIdAndFacilitiesId(Long actionsId, Long facilitiesId) {
        jdbcTemplate.update(ActionFacilityMappingSql.DELETE_BY_ACTIONS_ID_AND_FACILITIES_ID, Map.of("actionsId", actionsId, "facilitiesId", facilitiesId));
    }

    @Override
    public boolean existsByActionsIdAndFacilitiesId(Long actionsId, Long facilitiesId) {
        Integer count = jdbcTemplate.queryForObject(
            ActionFacilityMappingSql.EXISTS_BY_ACTIONS_ID_AND_FACILITIES_ID,
            Map.of("actionsId", actionsId, "facilitiesId", facilitiesId),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<ActionFacilityMapping> findAll() {
        return jdbcTemplate.query(ActionFacilityMappingSql.FIND_ALL, rowMapper);
    }

    @Override
    public ActionFacilityMapping save(ActionFacilityMapping entity) {
        // No primary key - always insert
        return insert(entity);
    }

    private ActionFacilityMapping insert(ActionFacilityMapping entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);

        MapSqlParameterSource params = createParameterMap(entity);
        jdbcTemplate.update(ActionFacilityMappingSql.INSERT, params);
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(ActionFacilityMappingSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    @Override
    public List<ActionFacilityMapping> findByActionsId(Long actionsId) {
        return jdbcTemplate.query(
            ActionFacilityMappingSql.FIND_BY_ACTIONS_ID,
            Map.of("actionsId", actionsId),
            rowMapper
        );
    }

    @Override
    public List<ActionFacilityMapping> findByModifiedBy(Long modifiedBy) {
        return jdbcTemplate.query(
            ActionFacilityMappingSql.FIND_BY_MODIFIED_BY,
            Map.of("modifiedBy", modifiedBy),
            rowMapper
        );
    }

    @Override
    public List<ActionFacilityMapping> findByFacilitiesId(Long facilitiesId) {
        return jdbcTemplate.query(
            ActionFacilityMappingSql.FIND_BY_FACILITIES_ID,
            Map.of("facilitiesId", facilitiesId),
            rowMapper
        );
    }

    @Override
    public List<ActionFacilityMapping> findByCreatedBy(Long createdBy) {
        return jdbcTemplate.query(
            ActionFacilityMappingSql.FIND_BY_CREATED_BY,
            Map.of("createdBy", createdBy),
            rowMapper
        );
    }

    private MapSqlParameterSource createParameterMap(ActionFacilityMapping entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("facilitiesId", entity.getFacilitiesId());
        params.addValue("createdBy", entity.getCreatedBy());
        params.addValue("modifiedAt", entity.getModifiedAt());
        params.addValue("actionsId", entity.getActionsId());
        params.addValue("modifiedBy", entity.getModifiedBy());

        return params;
    }

}
