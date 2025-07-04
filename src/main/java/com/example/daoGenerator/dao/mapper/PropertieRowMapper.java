package com.example.daoGenerator.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;
import com.example.pojogenerator.pojos.Propertie;

/**
 * Enhanced row mapper for Propertie entity
 * Generated by Enhanced JDBC DAO Generator
 */
public class PropertieRowMapper implements RowMapper<Propertie> {

    @Override
    public Propertie mapRow(ResultSet rs, int rowNum) throws SQLException {
        Propertie entity = new Propertie();

        entity.setOrderTree(rs.getInt("order_tree"));
        entity.setUseCasesId(rs.getLong("use_cases_id"));
        entity.setModifiedAt(rs.getLong("modified_at"));
        entity.setLabel(rs.getString("label"));
        entity.setType(rs.getString("type"));
        entity.setArchived(rs.getBoolean("archived"));
        entity.setCreatedAt(rs.getLong("created_at"));
        entity.setCreatedBy(rs.getLong("created_by"));
        entity.setName(rs.getString("name"));
        entity.setIsGlobal(rs.getBoolean("is_global"));
        entity.setModifiedBy(rs.getLong("modified_by"));
        entity.setId(rs.getLong("id"));
        entity.setPlaceHolder(rs.getString("place_holder"));

        return entity;
    }
}
