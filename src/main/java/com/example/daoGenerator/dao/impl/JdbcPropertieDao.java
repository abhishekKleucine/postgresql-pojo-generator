package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.PropertieDao;
import com.example.pojogenerator.pojos.Propertie;
import com.example.daoGenerator.dao.mapper.PropertieRowMapper;
import com.example.daoGenerator.dao.sql.PropertieSql;

/**
 * Enhanced JDBC implementation of PropertieDao
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcPropertieDao implements PropertieDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PropertieRowMapper rowMapper;

    public JdbcPropertieDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new PropertieRowMapper();
    }

    @Override
    public Optional<Propertie> findById(Long id) {
        try {
            Propertie result = jdbcTemplate.queryForObject(
                PropertieSql.FIND_BY_ID,
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
        jdbcTemplate.update(PropertieSql.DELETE_BY_ID, Map.of("id", id));
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
            PropertieSql.EXISTS_BY_ID,
            Map.of("id", id),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<Propertie> findAll() {
        return jdbcTemplate.query(PropertieSql.FIND_ALL, rowMapper);
    }

    @Override
    public Propertie save(Propertie entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private Propertie insert(Propertie entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);

        MapSqlParameterSource params = createParameterMap(entity);
        Long generatedId = jdbcTemplate.queryForObject(
            PropertieSql.INSERT,
            params,
            Long.class
        );
        entity.setId(generatedId);
        return entity;
    }

    private Propertie update(Propertie entity) {
        entity.setModifiedAt(System.currentTimeMillis());

        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(PropertieSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(PropertieSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    @Override
    public List<Propertie> findByUseCasesId(Long useCasesId) {
        return jdbcTemplate.query(
            PropertieSql.FIND_BY_USE_CASES_ID,
            Map.of("useCasesId", useCasesId),
            rowMapper
        );
    }

    @Override
    public List<Propertie> findByModifiedBy(Long modifiedBy) {
        return jdbcTemplate.query(
            PropertieSql.FIND_BY_MODIFIED_BY,
            Map.of("modifiedBy", modifiedBy),
            rowMapper
        );
    }

    @Override
    public List<Propertie> findByCreatedBy(Long createdBy) {
        return jdbcTemplate.query(
            PropertieSql.FIND_BY_CREATED_BY,
            Map.of("createdBy", createdBy),
            rowMapper
        );
    }

    private MapSqlParameterSource createParameterMap(Propertie entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("orderTree", entity.getOrderTree());
        params.addValue("useCasesId", entity.getUseCasesId());
        params.addValue("modifiedAt", entity.getModifiedAt());
        params.addValue("label", entity.getLabel());
        params.addValue("type", entity.getType());
        params.addValue("archived", entity.getArchived());
        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("createdBy", entity.getCreatedBy());
        params.addValue("name", entity.getName());
        params.addValue("isGlobal", entity.getIsGlobal());
        params.addValue("modifiedBy", entity.getModifiedBy());
        params.addValue("id", entity.getId());
        params.addValue("placeHolder", entity.getPlaceHolder());

        return params;
    }

}
