package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.PermissionDao;
import com.example.pojogenerator.pojos.Permission;
import com.example.daoGenerator.dao.mapper.PermissionRowMapper;
import com.example.daoGenerator.dao.sql.PermissionSql;

/**
 * Enhanced JDBC implementation of PermissionDao
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcPermissionDao implements PermissionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PermissionRowMapper rowMapper;

    public JdbcPermissionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new PermissionRowMapper();
    }

    @Override
    public Optional<Permission> findById(Long id) {
        try {
            Permission result = jdbcTemplate.queryForObject(
                PermissionSql.FIND_BY_ID,
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
        jdbcTemplate.update(PermissionSql.DELETE_BY_ID, Map.of("id", id));
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
            PermissionSql.EXISTS_BY_ID,
            Map.of("id", id),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<Permission> findAll() {
        return jdbcTemplate.query(PermissionSql.FIND_ALL, rowMapper);
    }

    @Override
    public Permission save(Permission entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private Permission insert(Permission entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);

        MapSqlParameterSource params = createParameterMap(entity);
        Long generatedId = jdbcTemplate.queryForObject(
            PermissionSql.INSERT,
            params,
            Long.class
        );
        entity.setId(generatedId);
        return entity;
    }

    private Permission update(Permission entity) {
        entity.setModifiedAt(System.currentTimeMillis());

        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(PermissionSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(PermissionSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    @Override
    public List<Permission> findByServicesId(Long servicesId) {
        return jdbcTemplate.query(
            PermissionSql.FIND_BY_SERVICES_ID,
            Map.of("servicesId", servicesId),
            rowMapper
        );
    }

    @Override
    public List<Permission> findByModifiedBy(Long modifiedBy) {
        return jdbcTemplate.query(
            PermissionSql.FIND_BY_MODIFIED_BY,
            Map.of("modifiedBy", modifiedBy),
            rowMapper
        );
    }

    @Override
    public List<Permission> findByCreatedBy(Long createdBy) {
        return jdbcTemplate.query(
            PermissionSql.FIND_BY_CREATED_BY,
            Map.of("createdBy", createdBy),
            rowMapper
        );
    }

    private MapSqlParameterSource createParameterMap(Permission entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("archived", entity.getArchived());
        params.addValue("path", entity.getPath());
        params.addValue("method", entity.getMethod());
        params.addValue("createdBy", entity.getCreatedBy());
        params.addValue("modifiedAt", entity.getModifiedAt());
        params.addValue("name", entity.getName());
        params.addValue("description", entity.getDescription());
        params.addValue("modifiedBy", entity.getModifiedBy());
        params.addValue("id", entity.getId());
        params.addValue("servicesId", entity.getServicesId());

        return params;
    }

}
