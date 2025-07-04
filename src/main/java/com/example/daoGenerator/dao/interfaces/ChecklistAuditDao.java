package com.example.daoGenerator.dao.interfaces;

import java.util.List;
import java.util.Optional;
import com.example.pojogenerator.pojos.ChecklistAudit;

/**
 * Enhanced DAO interface for ChecklistAudit entity
 * Table: checklist_audits
 * Key Type: SINGLE_ID
 * Generated by Enhanced JDBC DAO Generator
 */
public interface ChecklistAuditDao {

    // Single ID operations
    Optional<ChecklistAudit> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);

    // Common operations
    List<ChecklistAudit> findAll();
    ChecklistAudit save(ChecklistAudit entity);
    long count();

}
