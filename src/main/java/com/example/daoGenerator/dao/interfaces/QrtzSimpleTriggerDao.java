package com.example.daoGenerator.dao.interfaces;

import java.util.List;
import java.util.Optional;
import com.example.pojogenerator.pojos.QrtzSimpleTrigger;

/**
 * Enhanced DAO interface for QrtzSimpleTrigger entity
 * Table: qrtz_simple_triggers
 * Key Type: COMPOSITE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public interface QrtzSimpleTriggerDao {

    // Composite key operations
    Optional<QrtzSimpleTrigger> findByTriggerNameAndSchedNameAndTriggerGroup(String triggerName, String schedName, String triggerGroup);
    void deleteByTriggerNameAndSchedNameAndTriggerGroup(String triggerName, String schedName, String triggerGroup);
    boolean existsByTriggerNameAndSchedNameAndTriggerGroup(String triggerName, String schedName, String triggerGroup);

    // Common operations
    List<QrtzSimpleTrigger> findAll();
    QrtzSimpleTrigger save(QrtzSimpleTrigger entity);
    long count();

    // Foreign key based finders
    List<QrtzSimpleTrigger> findByTriggerName(Long triggerName);
    List<QrtzSimpleTrigger> findBySchedName(Long schedName);
    List<QrtzSimpleTrigger> findByTriggerGroup(Long triggerGroup);

}
