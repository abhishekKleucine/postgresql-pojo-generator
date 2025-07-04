package com.example.pojogenerator.pojos;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO class for table: checklist_property_values
 * Generated by PostgreSQL POJO Generator
 * 
 * Table Information:
 * - Table Name: checklist_property_values
 * - Primary Keys: facility_use_case_property_mapping_id, checklists_id
 * 
 * Foreign Keys:
 * - facility_use_case_property_mapping_id → facility_use_case_property_mapping.id
 * - checklists_id → checklists.id
 * - created_by → users.id
 * - modified_by → users.id
 */
public class ChecklistPropertyValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Database column: value
     * Type: varchar(255)
     */
    private String value;

    /**
     * Database column: facility_use_case_property_mapping_id
     * Type: int8 NOT NULL
     * Primary Key
     * Foreign Key → facility_use_case_property_mapping.id
     */
    private Long facilityUseCasePropertyMappingId;

    /**
     * Database column: checklists_id
     * Type: int8 NOT NULL
     * Primary Key
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
     * Default constructor
     */
    public ChecklistPropertyValue() {
    }

    /**
     * Gets value
     * @return String
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets value
     * @param value the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setValue(String value) {
        if (value != null && value.length() > 255) {
            throw new IllegalArgumentException("value length cannot exceed 255 characters");
        }
        this.value = value;
    }

    /**
     * Gets facility_use_case_property_mapping_id
     * @return Long
     */
    public Long getFacilityUseCasePropertyMappingId() {
        return this.facilityUseCasePropertyMappingId;
    }

    /**
     * Sets facility_use_case_property_mapping_id
     * @param facilityUseCasePropertyMappingId the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setFacilityUseCasePropertyMappingId(Long facilityUseCasePropertyMappingId) {
        if (facilityUseCasePropertyMappingId == null) {
            throw new IllegalArgumentException("facility_use_case_property_mapping_id cannot be null");
        }
        this.facilityUseCasePropertyMappingId = facilityUseCasePropertyMappingId;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChecklistPropertyValue other = (ChecklistPropertyValue) obj;
        return Objects.equals(this.facilityUseCasePropertyMappingId, other.facilityUseCasePropertyMappingId) && Objects.equals(this.checklistsId, other.checklistsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.facilityUseCasePropertyMappingId, this.checklistsId);
    }

    @Override
    public String toString() {
        return "ChecklistPropertyValue{" +
                "value=" + value +
                ", facilityUseCasePropertyMappingId=" + facilityUseCasePropertyMappingId +
                ", checklistsId=" + checklistsId +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                ", createdBy=" + createdBy +
                ", modifiedBy=" + modifiedBy +
                '}';
    }
}
