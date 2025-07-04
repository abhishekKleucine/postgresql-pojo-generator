package com.example.daoGenerator.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.daoGenerator.dao.interfaces.JobPropertyValueDao;
import com.example.pojogenerator.pojos.JobPropertyValue;
import com.example.daoGenerator.dao.mapper.JobPropertyValueRowMapper;
import com.example.daoGenerator.dao.sql.JobPropertyValueSql;

/**
 * Enhanced JDBC implementation of JobPropertyValueDao
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
@Repository
public class JdbcJobPropertyValueDao implements JobPropertyValueDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JobPropertyValueRowMapper rowMapper;

    public JdbcJobPropertyValueDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new JobPropertyValueRowMapper();
    }

    @Override
    public Optional<JobPropertyValue> findByFacilityUseCasePropertyMappingIdAndJobsId(Long facilityUseCasePropertyMappingId, Long jobsId) {
        try {
            JobPropertyValue result = jdbcTemplate.queryForObject(
                JobPropertyValueSql.FIND_BY_FACILITY_USE_CASE_PROPERTY_MAPPING_ID_AND_JOBS_ID,
                Map.of("facilityUseCasePropertyMappingId", facilityUseCasePropertyMappingId, "jobsId", jobsId),
                rowMapper
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByFacilityUseCasePropertyMappingIdAndJobsId(Long facilityUseCasePropertyMappingId, Long jobsId) {
        jdbcTemplate.update(JobPropertyValueSql.DELETE_BY_FACILITY_USE_CASE_PROPERTY_MAPPING_ID_AND_JOBS_ID, Map.of("facilityUseCasePropertyMappingId", facilityUseCasePropertyMappingId, "jobsId", jobsId));
    }

    @Override
    public boolean existsByFacilityUseCasePropertyMappingIdAndJobsId(Long facilityUseCasePropertyMappingId, Long jobsId) {
        Integer count = jdbcTemplate.queryForObject(
            JobPropertyValueSql.EXISTS_BY_FACILITY_USE_CASE_PROPERTY_MAPPING_ID_AND_JOBS_ID,
            Map.of("facilityUseCasePropertyMappingId", facilityUseCasePropertyMappingId, "jobsId", jobsId),
            Integer.class
        );
        return count != null && count > 0;
    }

    @Override
    public List<JobPropertyValue> findAll() {
        return jdbcTemplate.query(JobPropertyValueSql.FIND_ALL, rowMapper);
    }

    @Override
    public JobPropertyValue save(JobPropertyValue entity) {
        if (existsByFacilityUseCasePropertyMappingIdAndJobsId(entity.getFacilityUseCasePropertyMappingId(), entity.getJobsId())) {
            return update(entity);
        } else {
            return insert(entity);
        }
    }

    private JobPropertyValue insert(JobPropertyValue entity) {
        // Set audit fields if they exist
        long now = System.currentTimeMillis();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setModifiedAt(now);

        MapSqlParameterSource params = createParameterMap(entity);
        jdbcTemplate.update(JobPropertyValueSql.INSERT, params);
        return entity;
    }

    private JobPropertyValue update(JobPropertyValue entity) {
        entity.setModifiedAt(System.currentTimeMillis());

        MapSqlParameterSource params = createParameterMap(entity);
        int rowsAffected = jdbcTemplate.update(JobPropertyValueSql.UPDATE, params);
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Entity not found for update");
        }
        return entity;
    }

    @Override
    public long count() {
        Long result = jdbcTemplate.queryForObject(JobPropertyValueSql.COUNT, Map.of(), Long.class);
        return result != null ? result : 0L;
    }

    @Override
    public List<JobPropertyValue> findByFacilityUseCasePropertyMappingId(Long facilityUseCasePropertyMappingId) {
        return jdbcTemplate.query(
            JobPropertyValueSql.FIND_BY_FACILITY_USE_CASE_PROPERTY_MAPPING_ID,
            Map.of("facilityUseCasePropertyMappingId", facilityUseCasePropertyMappingId),
            rowMapper
        );
    }

    @Override
    public List<JobPropertyValue> findByModifiedBy(Long modifiedBy) {
        return jdbcTemplate.query(
            JobPropertyValueSql.FIND_BY_MODIFIED_BY,
            Map.of("modifiedBy", modifiedBy),
            rowMapper
        );
    }

    @Override
    public List<JobPropertyValue> findByJobsId(Long jobsId) {
        return jdbcTemplate.query(
            JobPropertyValueSql.FIND_BY_JOBS_ID,
            Map.of("jobsId", jobsId),
            rowMapper
        );
    }

    @Override
    public List<JobPropertyValue> findByCreatedBy(Long createdBy) {
        return jdbcTemplate.query(
            JobPropertyValueSql.FIND_BY_CREATED_BY,
            Map.of("createdBy", createdBy),
            rowMapper
        );
    }

    private MapSqlParameterSource createParameterMap(JobPropertyValue entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("createdAt", entity.getCreatedAt());
        params.addValue("createdBy", entity.getCreatedBy());
        params.addValue("modifiedAt", entity.getModifiedAt());
        params.addValue("modifiedBy", entity.getModifiedBy());
        params.addValue("facilityUseCasePropertyMappingId", entity.getFacilityUseCasePropertyMappingId());
        params.addValue("value", entity.getValue());
        params.addValue("jobsId", entity.getJobsId());

        return params;
    }

}
