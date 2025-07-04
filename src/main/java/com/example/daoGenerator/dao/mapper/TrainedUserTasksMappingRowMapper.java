package com.example.daoGenerator.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;
import com.example.pojogenerator.pojos.TrainedUserTasksMapping;

/**
 * Enhanced row mapper for TrainedUserTasksMapping entity
 * Generated by Enhanced JDBC DAO Generator
 */
public class TrainedUserTasksMappingRowMapper implements RowMapper<TrainedUserTasksMapping> {

    @Override
    public TrainedUserTasksMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
        TrainedUserTasksMapping entity = new TrainedUserTasksMapping();

        entity.setTrainedUsersId(rs.getLong("trained_users_id"));
        entity.setCreatedAt(rs.getLong("created_at"));
        entity.setCreatedBy(rs.getLong("created_by"));
        entity.setTasksId(rs.getLong("tasks_id"));
        entity.setModifiedAt(rs.getLong("modified_at"));
        entity.setModifiedBy(rs.getLong("modified_by"));
        entity.setId(rs.getLong("id"));

        return entity;
    }
}
