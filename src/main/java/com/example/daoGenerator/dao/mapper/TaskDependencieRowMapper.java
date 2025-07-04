package com.example.daoGenerator.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;
import com.example.pojogenerator.pojos.TaskDependencie;

/**
 * Enhanced row mapper for TaskDependencie entity
 * Generated by Enhanced JDBC DAO Generator
 */
public class TaskDependencieRowMapper implements RowMapper<TaskDependencie> {

    @Override
    public TaskDependencie mapRow(ResultSet rs, int rowNum) throws SQLException {
        TaskDependencie entity = new TaskDependencie();

        entity.setCreatedAt(rs.getLong("created_at"));
        entity.setDependentTaskId(rs.getLong("dependent_task_id"));
        entity.setCreatedBy(rs.getLong("created_by"));
        entity.setModifiedAt(rs.getLong("modified_at"));
        entity.setPrerequisiteTaskId(rs.getLong("prerequisite_task_id"));
        entity.setModifiedBy(rs.getLong("modified_by"));
        entity.setId(rs.getLong("id"));

        return entity;
    }
}
