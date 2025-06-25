package com.example.dwiDaoGenerator.checklist.generated;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.springframework.jdbc.core.RowMapper;
import com.example.pojogenerator.pojos.Checklist;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Enhanced row mapper for Checklist entity
 * Generated with complete field mapping for all Java types
 */
public class ChecklistRowMapper implements RowMapper<Checklist> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Checklist mapRow(ResultSet rs, int rowNum) throws SQLException {
        Checklist entity = new Checklist();

        entity.setReleasedAt(rs.getLong("released_at"));
        entity.setCode(rs.getString("code"));
        entity.setOrganisationsId(rs.getLong("organisations_id"));
        entity.setModifiedAt(rs.getLong("modified_at"));
        entity.setReleasedBy(rs.getLong("released_by"));
        entity.setUseCasesId(rs.getLong("use_cases_id"));
        entity.setReviewCycle(rs.getInt("review_cycle"));
        entity.setDescription(rs.getString("description"));
        String jobLogColumnsJson = rs.getString("job_log_columns");
        if (jobLogColumnsJson != null) {
            entity.setJobLogColumns(parseJsonNode(jobLogColumnsJson));
        }
        entity.setArchived(rs.getBoolean("archived"));
        entity.setCreatedAt(rs.getLong("created_at"));
        entity.setCreatedBy(rs.getLong("created_by"));
        entity.setName(rs.getString("name"));
        entity.setIsGlobal(rs.getBoolean("is_global"));
        entity.setModifiedBy(rs.getLong("modified_by"));
        entity.setColorCode(rs.getString("color_code"));
        entity.setId(rs.getLong("id"));
        entity.setState(rs.getString("state"));
        entity.setVersionsId(rs.getLong("versions_id"));

        return entity;
    }

    private JsonNode parseJsonNode(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON: " + json, e);
        }
    }
}
