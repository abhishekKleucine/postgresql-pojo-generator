package com.example.daoGenerator.dao.interfaces;

import java.util.List;
import java.util.Optional;
import com.example.pojogenerator.pojos.Service;

/**
 * Enhanced DAO interface for Service entity
 * Table: services
 * Key Type: CUSTOM_SINGLE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public interface ServiceDao {

    // Custom single key operations
    Optional<Service> findById(String id);
    void deleteById(String id);
    boolean existsById(String id);

    // Common operations
    List<Service> findAll();
    Service save(Service entity);
    long count();

    // Foreign key based finders
    List<Service> findByModifiedBy(Long modifiedBy);
    List<Service> findByCreatedBy(Long createdBy);

}
