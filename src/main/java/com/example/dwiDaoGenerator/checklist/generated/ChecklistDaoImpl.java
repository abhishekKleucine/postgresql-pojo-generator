package com.example.dwiDaoGenerator.checklist.generated;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Array;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import javax.sql.DataSource;
import com.example.pojogenerator.pojos.Checklist;
import com.example.dwiDaoGenerator.shared.PaginationTypes.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pure JDBC implementation for ChecklistDao
 * Generated with Pure JDBC implementation from documentation
 * No Spring dependencies - uses manual resource management
 */
public class ChecklistDaoImpl implements ChecklistDao {

    private final DataSource dataSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChecklistDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Standard CRUD implementations with Pure JDBC

    @Override
    public Optional<Checklist> findById(Long id) {
        String sql = ChecklistSql.FIND_BY_ID;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToChecklist(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding checklist by id: " + id, e);
        }
    }

    @Override
    public List<Checklist> findAll() {
        String sql = ChecklistSql.FIND_ALL;
        List<Checklist> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                results.add(mapRowToChecklist(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all checklists", e);
        }
    }

    @Override
    public Checklist save(Checklist entity) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            
            Checklist result;
            if (entity.getId() == null) {
                result = insert(entity, conn);
            } else {
                result = update(entity, conn);
            }
            
            conn.commit();
            return result;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw new RuntimeException("Error saving checklist: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log error but don't throw
                }
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = ChecklistSql.DELETE_BY_ID;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No checklist found with id: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting checklist with id: " + id, e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = ChecklistSql.EXISTS_BY_ID;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence of checklist with id: " + id, e);
        }
    }

    @Override
    public long count() {
        String sql = ChecklistSql.COUNT_ALL;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting checklists", e);
        }
    }

    private Checklist insert(Checklist entity, Connection conn) throws SQLException {
        String sql = ChecklistSql.INSERT;
        
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(stmt, entity);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Insert failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                    return entity;
                } else {
                    throw new SQLException("Insert failed, no generated key obtained");
                }
            }
        }
    }

    private Checklist update(Checklist entity, Connection conn) throws SQLException {
        String sql = ChecklistSql.UPDATE;
        entity.setModifiedAt(System.currentTimeMillis());
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setUpdateParameters(stmt, entity);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Entity not found for update");
            }
            return entity;
        }
    }

    private void setInsertParameters(PreparedStatement stmt, Checklist entity) throws SQLException {
        int paramIndex = 1;
        
        if (entity.getReleasedAt() != null) {
            stmt.setLong(paramIndex++, entity.getReleasedAt());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        stmt.setString(paramIndex++, entity.getCode());
        if (entity.getOrganisationsId() != null) {
            stmt.setLong(paramIndex++, entity.getOrganisationsId());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getModifiedAt() != null) {
            stmt.setLong(paramIndex++, entity.getModifiedAt());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getReleasedBy() != null) {
            stmt.setLong(paramIndex++, entity.getReleasedBy());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getUseCasesId() != null) {
            stmt.setLong(paramIndex++, entity.getUseCasesId());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getReviewCycle() != null) {
            stmt.setInt(paramIndex++, entity.getReviewCycle());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.INTEGER);
        }
        stmt.setString(paramIndex++, entity.getDescription());
        if (entity.getJobLogColumns() != null) {
            stmt.setString(paramIndex++, entity.getJobLogColumns().toString());
        } else {
            stmt.setString(paramIndex++, "[]");
        }
        stmt.setBoolean(paramIndex++, entity.getArchived());
        if (entity.getCreatedAt() != null) {
            stmt.setLong(paramIndex++, entity.getCreatedAt());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getCreatedBy() != null) {
            stmt.setLong(paramIndex++, entity.getCreatedBy());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        stmt.setString(paramIndex++, entity.getName());
        stmt.setBoolean(paramIndex++, entity.getIsGlobal());
        if (entity.getModifiedBy() != null) {
            stmt.setLong(paramIndex++, entity.getModifiedBy());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        stmt.setString(paramIndex++, entity.getColorCode());
        stmt.setString(paramIndex++, entity.getState());
        if (entity.getVersionsId() != null) {
            stmt.setLong(paramIndex++, entity.getVersionsId());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
    }

    private void setUpdateParameters(PreparedStatement stmt, Checklist entity) throws SQLException {
        int paramIndex = 1;
        
        if (entity.getReleasedAt() != null) {
            stmt.setLong(paramIndex++, entity.getReleasedAt());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        stmt.setString(paramIndex++, entity.getCode());
        if (entity.getOrganisationsId() != null) {
            stmt.setLong(paramIndex++, entity.getOrganisationsId());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getModifiedAt() != null) {
            stmt.setLong(paramIndex++, entity.getModifiedAt());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getReleasedBy() != null) {
            stmt.setLong(paramIndex++, entity.getReleasedBy());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getUseCasesId() != null) {
            stmt.setLong(paramIndex++, entity.getUseCasesId());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getReviewCycle() != null) {
            stmt.setInt(paramIndex++, entity.getReviewCycle());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.INTEGER);
        }
        stmt.setString(paramIndex++, entity.getDescription());
        if (entity.getJobLogColumns() != null) {
            stmt.setString(paramIndex++, entity.getJobLogColumns().toString());
        } else {
            stmt.setString(paramIndex++, "[]");
        }
        stmt.setBoolean(paramIndex++, entity.getArchived());
        if (entity.getCreatedAt() != null) {
            stmt.setLong(paramIndex++, entity.getCreatedAt());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        if (entity.getCreatedBy() != null) {
            stmt.setLong(paramIndex++, entity.getCreatedBy());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        stmt.setString(paramIndex++, entity.getName());
        stmt.setBoolean(paramIndex++, entity.getIsGlobal());
        if (entity.getModifiedBy() != null) {
            stmt.setLong(paramIndex++, entity.getModifiedBy());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        stmt.setString(paramIndex++, entity.getColorCode());
        stmt.setString(paramIndex++, entity.getState());
        if (entity.getVersionsId() != null) {
            stmt.setLong(paramIndex++, entity.getVersionsId());
        } else {
            stmt.setNull(paramIndex++, java.sql.Types.BIGINT);
        }
        stmt.setLong(paramIndex++, entity.getId());
    }

    /**
     * Map ResultSet row to Checklist entity
     */
    private Checklist mapRowToChecklist(ResultSet rs) throws SQLException {
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

    /**
     * Map ResultSet row to Long value
     */
    private Long mapRowToLong(ResultSet rs) throws SQLException {
        return rs.getLong(1);
    }

    /**
     * Map ResultSet row to String value
     */
    private String mapRowToString(ResultSet rs) throws SQLException {
        return rs.getString(1);
    }

    /**
     * Map ResultSet row to ChecklistView
     */
    private ChecklistView mapRowToChecklistView(ResultSet rs) throws SQLException {
        return new ChecklistView(
            rs.getLong("id"),
            rs.getString("code"),
            rs.getString("name"),
            rs.getString("color_code")
        );
    }

    /**
     * Map ResultSet row to ChecklistJobLiteView
     */
    private ChecklistJobLiteView mapRowToChecklistJobLiteView(ResultSet rs) throws SQLException {
        return new ChecklistJobLiteView(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("code")
        );
    }

    /**
     * Map ResultSet row to JobLogMigrationChecklistView
     */
    private JobLogMigrationChecklistView mapRowToJobLogMigrationChecklistView(ResultSet rs) throws SQLException {
        return new JobLogMigrationChecklistView(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("code"),
            rs.getString("state")
        );
    }

    /**
     * Parse JSON string to JsonNode
     */
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

    /**
     * Build ORDER BY clause from Sort parameter
     */
    private String buildOrderByClause(Sort sort) {
        StringBuilder orderBy = new StringBuilder();
        boolean first = true;
        for (Sort.Order order : sort) {
            if (!first) orderBy.append(", ");
            orderBy.append("c.").append(order.getProperty());
            orderBy.append(" ").append(order.getDirection().name());
            first = false;
        }
        return orderBy.toString();
    }

    // Custom method implementations from repository documentation

    @Override
    public PageResult<Checklist> findAll(FilterCriteria specification, PageRequest pageable) {
        // TODO: Implement paged findAll with Specification
        // This requires dynamic SQL generation based on Specification criteria
        // For now, return empty page
        throw new UnsupportedOperationException("Paged findAll with Specification not implemented yet");
    }

    @Override
    public List<Checklist> findAllByIdIn(Collection<Long> id, Sort sort) {
        String sql = ChecklistSql.FIND_ALL_BY_ID_IN;
        
        // Apply dynamic sorting
        if (sort != null && sort.isSorted()) {
            sql += " ORDER BY " + buildOrderByClause(sort);
        } else {
            sql += " ORDER BY c.id ASC";  // Default sort
        }
        
        List<Checklist> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Convert Collection<Long> to SQL Array
            Array idArray = conn.createArrayOf("BIGINT", id.toArray());
            stmt.setArray(1, idArray);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRowToChecklist(rs));
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Error in findAllByIdIn query execution", e);
        }
    }

    @Override
    public Optional<Checklist> findByTaskId(Long taskId) {
        String sql = ChecklistSql.FIND_BY_TASK_ID;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, taskId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToChecklist(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
    }

    @Override
    public void updateState(State.Checklist state, Long checklistId) {
        String sql = ChecklistSql.UPDATE_STATE;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, state.name());
            stmt.setLong(2, checklistId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows affected by update operation");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in update execution", e);
        }
    }

    @Override
    public String getChecklistCodeByChecklistId(Long checklistId) {
        String sql = ChecklistSql.GET_CHECKLIST_CODE_BY_CHECKLIST_ID;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, checklistId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
    }

    @Override
    public void removeChecklistFacilityMapping(Long checklistId, Set<Long> facilityIds) {
        String sql = ChecklistSql.REMOVE_CHECKLIST_FACILITY_MAPPING;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, checklistId);
            Array facilityIdsArray = conn.createArrayOf("BIGINT", facilityIds.toArray());
            stmt.setArray(2, facilityIdsArray);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows affected by update operation");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in update execution", e);
        }
    }

    @Override
    public State.Checklist findByStageId(Long stageId) {
        String sql = ChecklistSql.FIND_BY_STAGE_ID;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, stageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String stateValue = rs.getString(1);
                    return State.Checklist.valueOf(stateValue);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
    }

    @Override
    public List<Checklist> findByUseCaseId(Long useCaseId) {
        String sql = ChecklistSql.FIND_BY_USE_CASE_ID;
        List<Checklist> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, useCaseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRowToChecklist(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
        
        return results;
    }

    @Override
    public List<Long> findByStateInOrderByStateDesc(Set<State.Checklist> stateSet) {
        String sql = ChecklistSql.FIND_BY_STATE_IN_ORDER_BY_STATE_DESC;
        List<Long> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String[] stateNames = stateSet.stream().map(Enum::name).toArray(String[]::new);
            Array stateArray = conn.createArrayOf("VARCHAR", stateNames);
            stmt.setArray(1, stateArray);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRowToLong(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
        
        return results;
    }

    @Override
    public Set<Long> findByStateNot(State.Checklist state) {
        String sql = ChecklistSql.FIND_BY_STATE_NOT;
        Set<Long> results = new HashSet<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, state.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
        
        return results;
    }

    @Override
    public JobLogMigrationChecklistView findChecklistInfoById(Long id) {
        String sql = ChecklistSql.FIND_CHECKLIST_INFO_BY_ID;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToJobLogMigrationChecklistView(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
    }

    @Override
    public void updateChecklistDuringRecall(Long checklistId, Long userId) {
        String sql = ChecklistSql.UPDATE_CHECKLIST_DURING_RECALL;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Parameter 1: created_by = userId
            stmt.setLong(1, userId);
            // Parameter 2: modified_by = userId
            stmt.setLong(2, userId);
            // Parameter 3: WHERE id = checklistId
            stmt.setLong(3, checklistId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows affected by update operation");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in update execution", e);
        }
    }

    @Override
    public List<Long> findAllChecklistIdsForCurrentFacilityAndOrganisationByObjectTypeInData(Long facilityId, Long organisationId, String objectTypeId, Long useCaseId, String name, boolean archived) {
        String sql = ChecklistSql.FIND_ALL_CHECKLIST_IDS_FOR_CURRENT_FACILITY_AND_ORGANISATION_BY_OBJECT_TYPE_IN_DATA;
        List<Long> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Parameter 1: cfm.facilities_id = facilityId
            stmt.setLong(1, facilityId);
            // Parameter 2: c.organisations_id = organisationId
            stmt.setLong(2, organisationId);
            // Parameter 3: p.data->>'objectTypeId'= objectTypeId (String)
            stmt.setString(3, objectTypeId);
            // Parameter 4: c.archived = archived
            stmt.setBoolean(4, archived);
            // Parameter 5: c.use_cases_id = useCaseId
            stmt.setLong(5, useCaseId);
            // Parameter 6: CAST(? as varchar) for name check
            stmt.setString(6, name);
            // Parameter 7: || ? || for name pattern matching
            stmt.setString(7, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRowToLong(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
        
        return results;
    }

    @Override
    public List<ChecklistView> getAllByIdsIn(List<Long> checklistIds) {
        String sql = ChecklistSql.GET_ALL_BY_IDS_IN;
        List<ChecklistView> results = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            Array checklistIdsArray = conn.createArrayOf("BIGINT", checklistIds.toArray());
            stmt.setArray(1, checklistIdsArray);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRowToChecklistView(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
        
        return results;
    }

    @Override
    public ChecklistJobLiteView getChecklistJobLiteDtoById(Long checklistId) {
        String sql = ChecklistSql.GET_CHECKLIST_JOB_LITE_DTO_BY_ID;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, checklistId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToChecklistJobLiteView(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in query execution", e);
        }
    }

}
