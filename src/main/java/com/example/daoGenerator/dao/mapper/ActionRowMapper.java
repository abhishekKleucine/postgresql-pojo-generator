package com.example.daoGenerator.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;
import com.example.pojogenerator.pojos.Action;

/**
 * Enhanced row mapper for Action entity
 * Generated by Enhanced JDBC DAO Generator
 */
public class ActionRowMapper implements RowMapper<Action> {

    @Override
    public Action mapRow(ResultSet rs, int rowNum) throws SQLException {
        Action entity = new Action();

        entity.setCode(rs.getString("code"));
        entity.setModifiedAt(rs.getLong("modified_at"));
        entity.setDescription(rs.getString("description"));
        entity.setChecklistsId(rs.getLong("checklists_id"));
        entity.setSuccessMessage(rs.getString("success_message"));
        entity.setCreatedAt(rs.getLong("created_at"));
        entity.setArchived(rs.getBoolean("archived"));
        entity.setCreatedBy(rs.getLong("created_by"));
        entity.setName(rs.getString("name"));
        entity.setModifiedBy(rs.getLong("modified_by"));
        entity.setId(rs.getLong("id"));
        entity.setTriggerType(rs.getString("trigger_type"));
        entity.setTriggerEntityId(rs.getLong("trigger_entity_id"));
        entity.setFailureMessage(rs.getString("failure_message"));

        return entity;
    }
}
