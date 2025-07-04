package com.example.daoGenerator.dao.interfaces;

import java.util.List;
import java.util.Optional;
import com.example.pojogenerator.pojos.Version;

/**
 * Enhanced DAO interface for Version entity
 * Table: versions
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public interface VersionDao {

    // Single ID operations
    Optional<Version> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);

    // Common operations
    List<Version> findAll();
    Version save(Version entity);
    long count();

    // Foreign key based finders
    List<Version> findByModifiedBy(Long modifiedBy);
    List<Version> findByCreatedBy(Long createdBy);

}
