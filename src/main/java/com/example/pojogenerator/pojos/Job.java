package com.example.pojogenerator.pojos;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO class for table: jobs
 * Generated by PostgreSQL POJO Generator
 * 
 * Table Information:
 * - Table Name: jobs
 * - Primary Keys: id
 * 
 * Indexes:
 * - idx3de8f27a751f40bc9191a509: use_cases_id
 * - idxab176c90496d41cbad3767ae: state
 * - idxf8b4d5f7c5df4069b2b73652: organisations_id, facilities_id
 * 
 * Foreign Keys:
 * - checklists_id → checklists.id
 * - created_by → users.id
 * - modified_by → users.id
 * - facilities_id → facilities.id
 * - organisations_id → organisations.id
 * - use_cases_id → use_cases.id
 * - started_by → users.id
 * - ended_by → users.id
 * - schedulers_id → schedulers.id
 */
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Database column: id
     * Type: int8 NOT NULL
     * Primary Key
     */
    private Long id;

    /**
     * Database column: code
     * Type: varchar(50) NOT NULL
     */
    private String code;

    /**
     * Database column: state
     * Type: varchar(50) NOT NULL
     */
    private String state;

    /**
     * Database column: checklists_id
     * Type: int8 NOT NULL
     * Foreign Key → checklists.id
     */
    private Long checklistsId;

    /**
     * Database column: created_at
     * Type: int8 NOT NULL
     */
    private Long createdAt;

    /**
     * Database column: modified_at
     * Type: int8 NOT NULL
     */
    private Long modifiedAt;

    /**
     * Database column: created_by
     * Type: int8 NOT NULL
     * Foreign Key → users.id
     */
    private Long createdBy;

    /**
     * Database column: modified_by
     * Type: int8 NOT NULL
     * Foreign Key → users.id
     */
    private Long modifiedBy;

    /**
     * Database column: started_at
     * Type: int8
     */
    private Long startedAt;

    /**
     * Database column: ended_at
     * Type: int8
     */
    private Long endedAt;

    /**
     * Database column: facilities_id
     * Type: int8 NOT NULL
     * Foreign Key → facilities.id
     */
    private Long facilitiesId;

    /**
     * Database column: organisations_id
     * Type: int8 NOT NULL
     * Foreign Key → organisations.id
     */
    private Long organisationsId;

    /**
     * Database column: use_cases_id
     * Type: int8 NOT NULL
     * Foreign Key → use_cases.id
     */
    private Long useCasesId;

    /**
     * Database column: started_by
     * Type: int8
     * Foreign Key → users.id
     */
    private Long startedBy;

    /**
     * Database column: ended_by
     * Type: int8
     * Foreign Key → users.id
     */
    private Long endedBy;

    /**
     * Database column: is_scheduled
     * Type: bool DEFAULT: false
     */
    private Boolean isScheduled;

    /**
     * Database column: schedulers_id
     * Type: int8
     * Foreign Key → schedulers.id
     */
    private Long schedulersId;

    /**
     * Database column: expected_start_date
     * Type: int8
     */
    private Long expectedStartDate;

    /**
     * Database column: expected_end_date
     * Type: int8
     */
    private Long expectedEndDate;

    /**
     * Database column: checklist_ancestor_id
     * Type: int8
     */
    private Long checklistAncestorId;

    /**
     * Default constructor
     */
    public Job() {
    }

    /**
     * Gets id
     * @return Long
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets id
     * @param id the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
    }

    /**
     * Gets code
     * @return String
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Sets code
     * @param code the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("code cannot be null");
        }
        if (code != null && code.length() > 50) {
            throw new IllegalArgumentException("code length cannot exceed 50 characters");
        }
        this.code = code;
    }

    /**
     * Gets state
     * @return String
     */
    public String getState() {
        return this.state;
    }

    /**
     * Sets state
     * @param state the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setState(String state) {
        if (state == null) {
            throw new IllegalArgumentException("state cannot be null");
        }
        if (state != null && state.length() > 50) {
            throw new IllegalArgumentException("state length cannot exceed 50 characters");
        }
        this.state = state;
    }

    /**
     * Gets checklists_id
     * @return Long
     */
    public Long getChecklistsId() {
        return this.checklistsId;
    }

    /**
     * Sets checklists_id
     * @param checklistsId the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setChecklistsId(Long checklistsId) {
        if (checklistsId == null) {
            throw new IllegalArgumentException("checklists_id cannot be null");
        }
        this.checklistsId = checklistsId;
    }

    /**
     * Gets created_at
     * @return Long
     */
    public Long getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Sets created_at
     * @param createdAt the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setCreatedAt(Long createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("created_at cannot be null");
        }
        this.createdAt = createdAt;
    }

    /**
     * Gets modified_at
     * @return Long
     */
    public Long getModifiedAt() {
        return this.modifiedAt;
    }

    /**
     * Sets modified_at
     * @param modifiedAt the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setModifiedAt(Long modifiedAt) {
        if (modifiedAt == null) {
            throw new IllegalArgumentException("modified_at cannot be null");
        }
        this.modifiedAt = modifiedAt;
    }

    /**
     * Gets created_by
     * @return Long
     */
    public Long getCreatedBy() {
        return this.createdBy;
    }

    /**
     * Sets created_by
     * @param createdBy the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setCreatedBy(Long createdBy) {
        if (createdBy == null) {
            throw new IllegalArgumentException("created_by cannot be null");
        }
        this.createdBy = createdBy;
    }

    /**
     * Gets modified_by
     * @return Long
     */
    public Long getModifiedBy() {
        return this.modifiedBy;
    }

    /**
     * Sets modified_by
     * @param modifiedBy the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setModifiedBy(Long modifiedBy) {
        if (modifiedBy == null) {
            throw new IllegalArgumentException("modified_by cannot be null");
        }
        this.modifiedBy = modifiedBy;
    }

    /**
     * Gets started_at
     * @return Long
     */
    public Long getStartedAt() {
        return this.startedAt;
    }

    /**
     * Sets started_at
     * @param startedAt the value to set
     */
    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    /**
     * Gets ended_at
     * @return Long
     */
    public Long getEndedAt() {
        return this.endedAt;
    }

    /**
     * Sets ended_at
     * @param endedAt the value to set
     */
    public void setEndedAt(Long endedAt) {
        this.endedAt = endedAt;
    }

    /**
     * Gets facilities_id
     * @return Long
     */
    public Long getFacilitiesId() {
        return this.facilitiesId;
    }

    /**
     * Sets facilities_id
     * @param facilitiesId the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setFacilitiesId(Long facilitiesId) {
        if (facilitiesId == null) {
            throw new IllegalArgumentException("facilities_id cannot be null");
        }
        this.facilitiesId = facilitiesId;
    }

    /**
     * Gets organisations_id
     * @return Long
     */
    public Long getOrganisationsId() {
        return this.organisationsId;
    }

    /**
     * Sets organisations_id
     * @param organisationsId the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setOrganisationsId(Long organisationsId) {
        if (organisationsId == null) {
            throw new IllegalArgumentException("organisations_id cannot be null");
        }
        this.organisationsId = organisationsId;
    }

    /**
     * Gets use_cases_id
     * @return Long
     */
    public Long getUseCasesId() {
        return this.useCasesId;
    }

    /**
     * Sets use_cases_id
     * @param useCasesId the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setUseCasesId(Long useCasesId) {
        if (useCasesId == null) {
            throw new IllegalArgumentException("use_cases_id cannot be null");
        }
        this.useCasesId = useCasesId;
    }

    /**
     * Gets started_by
     * @return Long
     */
    public Long getStartedBy() {
        return this.startedBy;
    }

    /**
     * Sets started_by
     * @param startedBy the value to set
     */
    public void setStartedBy(Long startedBy) {
        this.startedBy = startedBy;
    }

    /**
     * Gets ended_by
     * @return Long
     */
    public Long getEndedBy() {
        return this.endedBy;
    }

    /**
     * Sets ended_by
     * @param endedBy the value to set
     */
    public void setEndedBy(Long endedBy) {
        this.endedBy = endedBy;
    }

    /**
     * Gets is_scheduled
     * @return Boolean
     */
    public Boolean getIsScheduled() {
        return this.isScheduled;
    }

    /**
     * Sets is_scheduled
     * @param isScheduled the value to set
     */
    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    /**
     * Gets schedulers_id
     * @return Long
     */
    public Long getSchedulersId() {
        return this.schedulersId;
    }

    /**
     * Sets schedulers_id
     * @param schedulersId the value to set
     */
    public void setSchedulersId(Long schedulersId) {
        this.schedulersId = schedulersId;
    }

    /**
     * Gets expected_start_date
     * @return Long
     */
    public Long getExpectedStartDate() {
        return this.expectedStartDate;
    }

    /**
     * Sets expected_start_date
     * @param expectedStartDate the value to set
     */
    public void setExpectedStartDate(Long expectedStartDate) {
        this.expectedStartDate = expectedStartDate;
    }

    /**
     * Gets expected_end_date
     * @return Long
     */
    public Long getExpectedEndDate() {
        return this.expectedEndDate;
    }

    /**
     * Sets expected_end_date
     * @param expectedEndDate the value to set
     */
    public void setExpectedEndDate(Long expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    /**
     * Gets checklist_ancestor_id
     * @return Long
     */
    public Long getChecklistAncestorId() {
        return this.checklistAncestorId;
    }

    /**
     * Sets checklist_ancestor_id
     * @param checklistAncestorId the value to set
     */
    public void setChecklistAncestorId(Long checklistAncestorId) {
        this.checklistAncestorId = checklistAncestorId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Job other = (Job) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", code=" + code +
                ", state=" + state +
                ", checklistsId=" + checklistsId +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                ", createdBy=" + createdBy +
                ", modifiedBy=" + modifiedBy +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", facilitiesId=" + facilitiesId +
                ", organisationsId=" + organisationsId +
                ", useCasesId=" + useCasesId +
                ", startedBy=" + startedBy +
                ", endedBy=" + endedBy +
                ", isScheduled=" + isScheduled +
                ", schedulersId=" + schedulersId +
                ", expectedStartDate=" + expectedStartDate +
                ", expectedEndDate=" + expectedEndDate +
                ", checklistAncestorId=" + checklistAncestorId +
                '}';
    }
}
