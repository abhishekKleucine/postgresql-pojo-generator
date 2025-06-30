package com.example.dwiDaoGenerator.checklist.generated;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Collection;
import com.example.pojogenerator.pojos.Checklist;
import com.example.dwiDaoGenerator.shared.PaginationTypes.*;

/**
 * Repository-Driven DAO interface for Checklist entity
 * Generated from: repository_documents/repository_docs/ChecklistRepositorydoc.md
 * Custom methods: 15
 */
public interface ChecklistDao {

    // Standard CRUD operations
    Optional<Checklist> findById(Long id);
    List<Checklist> findAll();
    Checklist save(Checklist entity);
    void deleteById(Long id);
    boolean existsById(Long id);
    long count();

    // Custom methods from repository documentation
    PageResult<Checklist> findAll(FilterCriteria specification, PageRequest pageable);
    List<Checklist> findAllByIdIn(Collection<Long> id, Sort sort);
    Optional<Checklist> findByTaskId(Long taskId);
    void updateState(State.Checklist state, Long checklistId);
    String getChecklistCodeByChecklistId(Long checklistId);
    void removeChecklistFacilityMapping(Long checklistId, Set<Long> facilityIds);
    State.Checklist findByStageId(Long stageId);
    List<Checklist> findByUseCaseId(Long useCaseId);
    List<Long> findByStateInOrderByStateDesc(Set<State.Checklist> stateSet);
    Set<Long> findByStateNot(State.Checklist state);
    JobLogMigrationChecklistView findChecklistInfoById(Long id);
    void updateChecklistDuringRecall(Long checklistId, Long userId);
    List<Long> findAllChecklistIdsForCurrentFacilityAndOrganisationByObjectTypeInData(Long facilityId, Long organisationId, String objectTypeId, Long useCaseId, String name, boolean archived);
    List<ChecklistView> getAllByIdsIn(List<Long> checklistIds);
    ChecklistJobLiteView getChecklistJobLiteDtoById(Long checklistId);
}
