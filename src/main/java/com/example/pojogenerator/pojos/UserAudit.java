package com.example.pojogenerator.pojos;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO class for table: user_audits
 * Generated by PostgreSQL POJO Generator
 * 
 * Table Information:
 * - Table Name: user_audits
 * - Primary Keys: id
 * 
 * Indexes:
 * - idxa8enhqqf54anje6jcvv2ymnh5: organisations_id
 * - idxrf922wo0pk10bldoh9si309v8: organisations_id, triggered_by
 */
public class UserAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Database column: id
     * Type: int8 NOT NULL
     * Primary Key
     */
    private Long id;

    /**
     * Database column: action
     * Type: varchar(255)
     */
    private String action;

    /**
     * Database column: details
     * Type: text(2147483647)
     */
    private String details;

    /**
     * Database column: organisations_id
     * Type: int8
     */
    private Long organisationsId;

    /**
     * Database column: severity
     * Type: varchar(255)
     */
    private String severity;

    /**
     * Database column: triggered_at
     * Type: int8
     */
    private Long triggeredAt;

    /**
     * Database column: triggered_by
     * Type: int8 NOT NULL
     */
    private Long triggeredBy;

    /**
     * Database column: facility_ids
     * Type: _text(2147483647)
     */
    private String facilityIds;

    /**
     * Default constructor
     */
    public UserAudit() {
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
     * Gets action
     * @return String
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Sets action
     * @param action the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setAction(String action) {
        if (action != null && action.length() > 255) {
            throw new IllegalArgumentException("action length cannot exceed 255 characters");
        }
        this.action = action;
    }

    /**
     * Gets details
     * @return String
     */
    public String getDetails() {
        return this.details;
    }

    /**
     * Sets details
     * @param details the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setDetails(String details) {
        if (details != null && details.length() > 2147483647) {
            throw new IllegalArgumentException("details length cannot exceed 2147483647 characters");
        }
        this.details = details;
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
     */
    public void setOrganisationsId(Long organisationsId) {
        this.organisationsId = organisationsId;
    }

    /**
     * Gets severity
     * @return String
     */
    public String getSeverity() {
        return this.severity;
    }

    /**
     * Sets severity
     * @param severity the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setSeverity(String severity) {
        if (severity != null && severity.length() > 255) {
            throw new IllegalArgumentException("severity length cannot exceed 255 characters");
        }
        this.severity = severity;
    }

    /**
     * Gets triggered_at
     * @return Long
     */
    public Long getTriggeredAt() {
        return this.triggeredAt;
    }

    /**
     * Sets triggered_at
     * @param triggeredAt the value to set
     */
    public void setTriggeredAt(Long triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    /**
     * Gets triggered_by
     * @return Long
     */
    public Long getTriggeredBy() {
        return this.triggeredBy;
    }

    /**
     * Sets triggered_by
     * @param triggeredBy the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setTriggeredBy(Long triggeredBy) {
        if (triggeredBy == null) {
            throw new IllegalArgumentException("triggered_by cannot be null");
        }
        this.triggeredBy = triggeredBy;
    }

    /**
     * Gets facility_ids
     * @return String
     */
    public String getFacilityIds() {
        return this.facilityIds;
    }

    /**
     * Sets facility_ids
     * @param facilityIds the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setFacilityIds(String facilityIds) {
        if (facilityIds != null && facilityIds.length() > 2147483647) {
            throw new IllegalArgumentException("facility_ids length cannot exceed 2147483647 characters");
        }
        this.facilityIds = facilityIds;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserAudit other = (UserAudit) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "UserAudit{" +
                "id=" + id +
                ", action=" + action +
                ", details=" + details +
                ", organisationsId=" + organisationsId +
                ", severity=" + severity +
                ", triggeredAt=" + triggeredAt +
                ", triggeredBy=" + triggeredBy +
                ", facilityIds=" + facilityIds +
                '}';
    }
}
