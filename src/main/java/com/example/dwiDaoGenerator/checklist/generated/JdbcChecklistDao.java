package com.example.dwiDaoGenerator.checklist.generated;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Collection;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.pojogenerator.pojos.Checklist;

/**
 * Repository-Driven JDBC implementation for ChecklistDao
 * Generated with complete SQL implementations from documentation
 */
@Repository
@Transactional(rollbackFor = Exception.class)
public class JdbcChecklistDao implements ChecklistDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ChecklistRowMapper rowMapper;

    public JdbcChecklistDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new ChecklistRowMapper();
    }

    // Standard CRUD implementations

    @Override
    public Optional<Checklist> findById(Long id) {
        try {
            Checklist result = jdbcTemplate.queryForObject(
                ChecklistSql.FIND_BY_ID,
                Map.of("id", id),
                rowMapper
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Checklist> findAll() {
        return jdbcTemplate.query(ChecklistSql.FIND_ALL, rowMapper);
    }

    @Override
    @Transactional
    public Checklist save(Checklist entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jdbcTemplate.update(ChecklistSql.DELETE_BY_ID, Map.of("id", id));
    }

    @Override
    public boolean existsById(Long id) {
        Boolean result = jdbcTemplate.queryForObject(
            ChecklistSql.EXISTS_BY_ID,
            Map.of("id", id),
            Boolean.class
        );
        return result != null && result;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(ChecklistSql.COUNT_ALL, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    private Checklist insert(Checklist entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);

        MapSqlParameterSource params = createParameterMap(entity);
        Long generatedId = jdbcTemplate.queryForObject(
            ChecklistSql.INSERT,
            params,
            Long.class
        );
        entity.setId(generatedId);
        return entity;
    }

    private Checklist update(Checklist entity) {
        entity.setModifiedAt(System.currentTimeMillis());

        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(ChecklistSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    private MapSqlParameterSource createParameterMap(Checklist entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("releasedAt", entity.getReleasedAt());
        params.addValue("code", entity.getCode());
        params.addValue("organisationsId", entity.getOrganisationsId());
        params.addValue("modifiedAt", entity.getModifiedAt());
        params.addValue("releasedBy", entity.getReleasedBy());
        params.addValue("useCasesId", entity.getUseCasesId());
        params.addValue("reviewCycle", entity.getReviewCycle());
        params.addValue("description", entity.getDescription());
        if (entity.getJobLogColumns() != null) {
            params.addValue("jobLogColumns", entity.getJobLogColumns().toString());
        } else {
            params.addValue("jobLogColumns", null);
        }
        params.addValue("archived", entity.getArchived());
        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("createdBy", entity.getCreatedBy());
        params.addValue("name", entity.getName());
        params.addValue("isGlobal", entity.getIsGlobal());
        params.addValue("modifiedBy", entity.getModifiedBy());
        params.addValue("colorCode", entity.getColorCode());
        params.addValue("id", entity.getId());
        params.addValue("state", entity.getState());
        params.addValue("versionsId", entity.getVersionsId());

        return params;
    }

    // Custom method implementations from repository documentation

    @Override
    public Page<Checklist> findAll(Specification specification, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("specification", specification);
        params.addValue("pageable", pageable);

        return jdbcTemplate.queryForObject(
            ChecklistSql.FIND_ALL,
            params,
            String.class
        );
    }

    @Override
    public List<Checklist> findAllByIdIn(Collection<Long> id, Sort sort) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("sort", sort);

        return jdbcTemplate.query(
            ChecklistSql.FIND_ALL_BY_ID_IN,
            params,
            rowMapper
        );
    }

    @Override
    public Optional<Checklist> findByTaskId(Long taskId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("taskId", taskId);

        try {
            Checklist result = jdbcTemplate.queryForObject(
                ChecklistSql.FIND_BY_TASK_ID,
                params,
                rowMapper
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public void updateState(State.Checklist state, Long checklistId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("state", state);
        params.addValue("checklistId", checklistId);

        jdbcTemplate.update(
            ChecklistSql.UPDATE_STATE,
            params
        );
    }

    @Override
    public String getChecklistCodeByChecklistId(Long checklistId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("checklistId", checklistId);

        return jdbcTemplate.queryForObject(
            ChecklistSql.GET_CHECKLIST_CODE_BY_CHECKLIST_ID,
            params,
            String.class
        );
    }

    @Transactional
    @Override
    public void removeChecklistFacilityMapping(Long checklistId, Set<Long> facilityIds) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("checklistId", checklistId);
        params.addValue("facilityIds", facilityIds);

        jdbcTemplate.update(
            ChecklistSql.REMOVE_CHECKLIST_FACILITY_MAPPING,
            params
        );
    }

    @Override
    public State.Checklist findByStageId(Long stageId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("stageId", stageId);

        return jdbcTemplate.queryForObject(
            ChecklistSql.FIND_BY_STAGE_ID,
            params,
            String.class
        );
    }

    @Override
    public List<Checklist> findByUseCaseId(Long useCaseId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("useCaseId", useCaseId);

        return jdbcTemplate.query(
            ChecklistSql.FIND_BY_USE_CASE_ID,
            params,
            rowMapper
        );
    }

    @Override
    public List<Long> findByStateInOrderByStateDesc(Set<State.Checklist> stateSet) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("stateSet", stateSet);

        return jdbcTemplate.query(
            ChecklistSql.FIND_BY_STATE_IN_ORDER_BY_STATE_DESC,
            params,
            rowMapper
        );
    }

    @Override
    public Set<Long> findByStateNot(State.Checklist state) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("state", state);

        return jdbcTemplate.queryForObject(
            ChecklistSql.FIND_BY_STATE_NOT,
            params,
            String.class
        );
    }

    @Override
    public JobLogMigrationChecklistView findChecklistInfoById(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return jdbcTemplate.queryForObject(
            ChecklistSql.FIND_CHECKLIST_INFO_BY_ID,
            params,
            String.class
        );
    }

    @Transactional
    @Override
    public void updateChecklistDuringRecall(Long checklistId, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("checklistId", checklistId);
        params.addValue("userId", userId);

        jdbcTemplate.update(
            ChecklistSql.UPDATE_CHECKLIST_DURING_RECALL,
            params
        );
    }

    @Override
    public List<Long> findAllChecklistIdsForCurrentFacilityAndOrganisationByObjectTypeInData(Long facilityId, Long organisationId, String objectTypeId, Long useCaseId, String name, boolean archived) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("facilityId", facilityId);
        params.addValue("organisationId", organisationId);
        params.addValue("objectTypeId", objectTypeId);
        params.addValue("useCaseId", useCaseId);
        params.addValue("name", name);
        params.addValue("archived", archived);

        return jdbcTemplate.query(
            ChecklistSql.FIND_ALL_CHECKLIST_IDS_FOR_CURRENT_FACILITY_AND_ORGANISATION_BY_OBJECT_TYPE_IN_DATA,
            params,
            rowMapper
        );
    }

    @Override
    public List<ChecklistView> getAllByIdsIn(List<Long> checklistIds) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("checklistIds", checklistIds);

        return jdbcTemplate.query(
            ChecklistSql.GET_ALL_BY_IDS_IN,
            params,
            rowMapper
        );
    }

    @Override
    public ChecklistJobLiteView getChecklistJobLiteDtoById(Long checklistId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("checklistId", checklistId);

        return jdbcTemplate.queryForObject(
            ChecklistSql.GET_CHECKLIST_JOB_LITE_DTO_BY_ID,
            params,
            String.class
        );
    }

}
