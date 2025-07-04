package com.example.daoGenerator.dao.interfaces;

import java.util.List;
import java.util.Optional;
import com.example.pojogenerator.pojos.OrganisationFacilitiesMapping;

/**
 * Enhanced DAO interface for OrganisationFacilitiesMapping entity
 * Table: organisation_facilities_mapping
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public interface OrganisationFacilitiesMappingDao {

    // Composite key operations
    Optional<OrganisationFacilitiesMapping> findByOrganisationsIdAndFacilitiesId(Long organisationsId, Long facilitiesId);
    void deleteByOrganisationsIdAndFacilitiesId(Long organisationsId, Long facilitiesId);
    boolean existsByOrganisationsIdAndFacilitiesId(Long organisationsId, Long facilitiesId);

    // Common operations
    List<OrganisationFacilitiesMapping> findAll();
    OrganisationFacilitiesMapping save(OrganisationFacilitiesMapping entity);
    long count();

    // Foreign key based finders
    List<OrganisationFacilitiesMapping> findByOrganisationsId(Long organisationsId);
    List<OrganisationFacilitiesMapping> findByFacilitiesId(Long facilitiesId);
    List<OrganisationFacilitiesMapping> findByCreatedBy(Long createdBy);

}
