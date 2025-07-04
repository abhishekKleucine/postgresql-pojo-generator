package com.example.daoGenerator.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;
import com.example.pojogenerator.pojos.UserGroup;

/**
 * Enhanced row mapper for UserGroup entity
 * Generated by Enhanced JDBC DAO Generator
 */
public class UserGroupRowMapper implements RowMapper<UserGroup> {

    @Override
    public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserGroup entity = new UserGroup();

        entity.setCreatedAt(rs.getLong("created_at"));
        entity.setFacilityId(rs.getLong("facility_id"));
        entity.setCreatedBy(rs.getLong("created_by"));
        entity.setModifiedAt(rs.getLong("modified_at"));
        entity.setName(rs.getString("name"));
        entity.setDescription(rs.getString("description"));
        entity.setActive(rs.getBoolean("active"));
        entity.setModifiedBy(rs.getLong("modified_by"));
        entity.setId(rs.getLong("id"));

        return entity;
    }
}
