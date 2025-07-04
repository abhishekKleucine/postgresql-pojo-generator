package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.UseCaseDao;
import com.example.pojogenerator.pojos.UseCase;
import com.example.daoGenerator.dao.mapper.UseCaseRowMapper;
import com.example.daoGenerator.dao.sql.UseCaseSql;

/**
 * Enhanced JDBC implementation of UseCaseDao
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcUseCaseDao implements UseCaseDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UseCaseRowMapper rowMapper;

    public JdbcUseCaseDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new UseCaseRowMapper();
    }

    @Override
    public Optional<UseCase> findById(Long id) {
        try {
            UseCase result = jdbcTemplate.queryForObject(
                UseCaseSql.FIND_BY_ID,
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
        jdbcTemplate.update(UseCaseSql.DELETE_BY_ID, Map.of("id", id));
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
            UseCaseSql.EXISTS_BY_ID,
            Map.of("id", id),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<UseCase> findAll() {
        return jdbcTemplate.query(UseCaseSql.FIND_ALL, rowMapper);
    }

    @Override
    public UseCase save(UseCase entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private UseCase insert(UseCase entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);

        MapSqlParameterSource params = createParameterMap(entity);
        Long generatedId = jdbcTemplate.queryForObject(
            UseCaseSql.INSERT,
            params,
            Long.class
        );
        entity.setId(generatedId);
        return entity;
    }

    private UseCase update(UseCase entity) {
        entity.setModifiedAt(System.currentTimeMillis());

        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(UseCaseSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(UseCaseSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    @Override
    public List<UseCase> findByModifiedBy(Long modifiedBy) {
        return jdbcTemplate.query(
            UseCaseSql.FIND_BY_MODIFIED_BY,
            Map.of("modifiedBy", modifiedBy),
            rowMapper
        );
    }

    @Override
    public List<UseCase> findByCreatedBy(Long createdBy) {
        return jdbcTemplate.query(
            UseCaseSql.FIND_BY_CREATED_BY,
            Map.of("createdBy", createdBy),
            rowMapper
        );
    }

    private MapSqlParameterSource createParameterMap(UseCase entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("archived", entity.getArchived());
        params.addValue("createdAt", entity.getCreatedAt());
        if (entity.getMetadata() != null) {
            params.addValue("metadata", entity.getMetadata().toString());
        } else {
            params.addValue("metadata", null);
        }
        params.addValue("orderTree", entity.getOrderTree());
        params.addValue("createdBy", entity.getCreatedBy());
        params.addValue("modifiedAt", entity.getModifiedAt());
        params.addValue("name", entity.getName());
        params.addValue("description", entity.getDescription());
        params.addValue("modifiedBy", entity.getModifiedBy());
        params.addValue("id", entity.getId());
        params.addValue("label", entity.getLabel());

        return params;
    }

}
