package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.RoleDao;
import com.example.pojogenerator.pojos.Role;
import com.example.daoGenerator.dao.mapper.RoleRowMapper;
import com.example.daoGenerator.dao.sql.RoleSql;

/**
 * Enhanced JDBC implementation of RoleDao
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcRoleDao implements RoleDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRowMapper rowMapper;

    public JdbcRoleDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new RoleRowMapper();
    }

    @Override
    public Optional<Role> findById(Long id) {
        try {
            Role result = jdbcTemplate.queryForObject(
                RoleSql.FIND_BY_ID,
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
        jdbcTemplate.update(RoleSql.DELETE_BY_ID, Map.of("id", id));
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
            RoleSql.EXISTS_BY_ID,
            Map.of("id", id),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<Role> findAll() {
        return jdbcTemplate.query(RoleSql.FIND_ALL, rowMapper);
    }

    @Override
    public Role save(Role entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private Role insert(Role entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);

        MapSqlParameterSource params = createParameterMap(entity);
        Long generatedId = jdbcTemplate.queryForObject(
            RoleSql.INSERT,
            params,
            Long.class
        );
        entity.setId(generatedId);
        return entity;
    }

    private Role update(Role entity) {
        entity.setModifiedAt(System.currentTimeMillis());

        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(RoleSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(RoleSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    @Override
    public List<Role> findByServicesId(Long servicesId) {
        return jdbcTemplate.query(
            RoleSql.FIND_BY_SERVICES_ID,
            Map.of("servicesId", servicesId),
            rowMapper
        );
    }

    @Override
    public List<Role> findByModifiedBy(Long modifiedBy) {
        return jdbcTemplate.query(
            RoleSql.FIND_BY_MODIFIED_BY,
            Map.of("modifiedBy", modifiedBy),
            rowMapper
        );
    }

    @Override
    public List<Role> findByCreatedBy(Long createdBy) {
        return jdbcTemplate.query(
            RoleSql.FIND_BY_CREATED_BY,
            Map.of("createdBy", createdBy),
            rowMapper
        );
    }

    private MapSqlParameterSource createParameterMap(Role entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("archived", entity.getArchived());
        params.addValue("createdBy", entity.getCreatedBy());
        params.addValue("modifiedAt", entity.getModifiedAt());
        params.addValue("name", entity.getName());
        params.addValue("modifiedBy", entity.getModifiedBy());
        params.addValue("id", entity.getId());
        params.addValue("servicesId", entity.getServicesId());

        return params;
    }

}
