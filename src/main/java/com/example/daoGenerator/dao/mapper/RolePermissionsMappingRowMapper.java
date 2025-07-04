package com.example.daoGenerator.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;
import com.example.pojogenerator.pojos.RolePermissionsMapping;

/**
 * Enhanced row mapper for RolePermissionsMapping entity
 * Generated by Enhanced JDBC DAO Generator
 */
public class RolePermissionsMappingRowMapper implements RowMapper<RolePermissionsMapping> {

    @Override
    public RolePermissionsMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
        RolePermissionsMapping entity = new RolePermissionsMapping();

        entity.setCreatedAt(rs.getLong("created_at"));
        entity.setCreatedBy(rs.getLong("created_by"));
        entity.setPermissionsId(rs.getLong("permissions_id"));
        entity.setRolesId(rs.getLong("roles_id"));

        return entity;
    }
}
