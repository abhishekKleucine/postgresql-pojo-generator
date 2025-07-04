package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.RolePermissionsMappingDao;
import com.example.pojogenerator.pojos.RolePermissionsMapping;
import com.example.daoGenerator.dao.mapper.RolePermissionsMappingRowMapper;
import com.example.daoGenerator.dao.sql.RolePermissionsMappingSql;

/**
 * Enhanced JDBC implementation of RolePermissionsMappingDao
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcRolePermissionsMappingDao implements RolePermissionsMappingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RolePermissionsMappingRowMapper rowMapper;

    public JdbcRolePermissionsMappingDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new RolePermissionsMappingRowMapper();
    }

    @Override
    public Optional<RolePermissionsMapping> findByPermissionsIdAndRolesId(Long permissionsId, Long rolesId) {
        try {
            RolePermissionsMapping result = jdbcTemplate.queryForObject(
                RolePermissionsMappingSql.FIND_BY_PERMISSIONS_ID_AND_ROLES_ID,
                Map.of("permissionsId", permissionsId, "rolesId", rolesId),
                rowMapper
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByPermissionsIdAndRolesId(Long permissionsId, Long rolesId) {
        jdbcTemplate.update(RolePermissionsMappingSql.DELETE_BY_PERMISSIONS_ID_AND_ROLES_ID, Map.of("permissionsId", permissionsId, "rolesId", rolesId));
    }

    @Override
    public boolean existsByPermissionsIdAndRolesId(Long permissionsId, Long rolesId) {
        Integer count = jdbcTemplate.queryForObject(
            RolePermissionsMappingSql.EXISTS_BY_PERMISSIONS_ID_AND_ROLES_ID,
            Map.of("permissionsId", permissionsId, "rolesId", rolesId),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<RolePermissionsMapping> findAll() {
        return jdbcTemplate.query(RolePermissionsMappingSql.FIND_ALL, rowMapper);
    }

    @Override
    public RolePermissionsMapping save(RolePermissionsMapping entity) {
        if (existsByPermissionsIdAndRolesId(entity.getPermissionsId(), entity.getRolesId())) {
            return update(entity);
        } else {
            return insert(entity);
        }
    }

    private RolePermissionsMapping insert(RolePermissionsMapping entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }

        MapSqlParameterSource params = createParameterMap(entity);
        jdbcTemplate.update(RolePermissionsMappingSql.INSERT, params);
        return entity;
    }

    private RolePermissionsMapping update(RolePermissionsMapping entity) {
        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(RolePermissionsMappingSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(RolePermissionsMappingSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    @Override
    public List<RolePermissionsMapping> findByPermissionsId(Long permissionsId) {
        return jdbcTemplate.query(
            RolePermissionsMappingSql.FIND_BY_PERMISSIONS_ID,
            Map.of("permissionsId", permissionsId),
            rowMapper
        );
    }

    @Override
    public List<RolePermissionsMapping> findByRolesId(Long rolesId) {
        return jdbcTemplate.query(
            RolePermissionsMappingSql.FIND_BY_ROLES_ID,
            Map.of("rolesId", rolesId),
            rowMapper
        );
    }

    @Override
    public List<RolePermissionsMapping> findByCreatedBy(Long createdBy) {
        return jdbcTemplate.query(
            RolePermissionsMappingSql.FIND_BY_CREATED_BY,
            Map.of("createdBy", createdBy),
            rowMapper
        );
    }

    private MapSqlParameterSource createParameterMap(RolePermissionsMapping entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("createdBy", entity.getCreatedBy());
        params.addValue("permissionsId", entity.getPermissionsId());
        params.addValue("rolesId", entity.getRolesId());

        return params;
    }

}
