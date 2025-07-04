package com.example.pojogenerator.pojos;

import java.io.Serializable;
import java.util.Objects;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * POJO class for table: variations
 * Generated by PostgreSQL POJO Generator
 * 
 * Table Information:
 * - Table Name: variations
 * - Primary Keys: id
 * 
 * Indexes:
 * - pk_variations (UNIQUE): id
 * - e0d4f71ce7a6434d91f52f7afa (UNIQUE): config_id, parameter_values_id
 * 
 * Foreign Keys:
 * - parameter_values_id → parameter_values.id
 * - jobs_id → jobs.id
 */
public class Variation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Database column: id
     * Type: int8 NOT NULL
     * Primary Key
     */
    private Long id;

    /**
     * Database column: name
     * Type: text(2147483647) NOT NULL
     */
    private String name;

    /**
     * Database column: description
     * Type: text(2147483647)
     */
    private String description;

    /**
     * Database column: parameter_values_id
     * Type: int8 NOT NULL
     * Foreign Key → parameter_values.id
     */
    private Long parameterValuesId;

    /**
     * Database column: jobs_id
     * Type: int8 NOT NULL
     * Foreign Key → jobs.id
     */
    private Long jobsId;

    /**
     * Database column: new_details
     * Type: jsonb NOT NULL DEFAULT: '{}'::jsonb
     */
    private JsonNode newDetails;

    /**
     * Database column: old_details
     * Type: jsonb NOT NULL DEFAULT: '{}'::jsonb
     */
    private JsonNode oldDetails;

    /**
     * Database column: type
     * Type: varchar(50) NOT NULL
     */
    private String type;

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
     */
    private Long createdBy;

    /**
     * Database column: modified_by
     * Type: int8 NOT NULL
     */
    private Long modifiedBy;

    /**
     * Database column: variation_number
     * Type: text(2147483647) NOT NULL
     */
    private String variationNumber;

    /**
     * Database column: config_id
     * Type: text(2147483647) NOT NULL
     */
    private String configId;

    /**
     * Default constructor
     */
    public Variation() {
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
     * Gets name
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets name
     * @param name the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (name != null && name.length() > 2147483647) {
            throw new IllegalArgumentException("name length cannot exceed 2147483647 characters");
        }
        this.name = name;
    }

    /**
     * Gets description
     * @return String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets description
     * @param description the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setDescription(String description) {
        if (description != null && description.length() > 2147483647) {
            throw new IllegalArgumentException("description length cannot exceed 2147483647 characters");
        }
        this.description = description;
    }

    /**
     * Gets parameter_values_id
     * @return Long
     */
    public Long getParameterValuesId() {
        return this.parameterValuesId;
    }

    /**
     * Sets parameter_values_id
     * @param parameterValuesId the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setParameterValuesId(Long parameterValuesId) {
        if (parameterValuesId == null) {
            throw new IllegalArgumentException("parameter_values_id cannot be null");
        }
        this.parameterValuesId = parameterValuesId;
    }

    /**
     * Gets jobs_id
     * @return Long
     */
    public Long getJobsId() {
        return this.jobsId;
    }

    /**
     * Sets jobs_id
     * @param jobsId the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setJobsId(Long jobsId) {
        if (jobsId == null) {
            throw new IllegalArgumentException("jobs_id cannot be null");
        }
        this.jobsId = jobsId;
    }

    /**
     * Gets new_details
     * @return JsonNode
     */
    public JsonNode getNewDetails() {
        return this.newDetails;
    }

    /**
     * Sets new_details
     * @param newDetails the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setNewDetails(JsonNode newDetails) {
        if (newDetails == null) {
            throw new IllegalArgumentException("new_details cannot be null");
        }
        this.newDetails = newDetails;
    }

    /**
     * Gets old_details
     * @return JsonNode
     */
    public JsonNode getOldDetails() {
        return this.oldDetails;
    }

    /**
     * Sets old_details
     * @param oldDetails the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setOldDetails(JsonNode oldDetails) {
        if (oldDetails == null) {
            throw new IllegalArgumentException("old_details cannot be null");
        }
        this.oldDetails = oldDetails;
    }

    /**
     * Gets type
     * @return String
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets type
     * @param type the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (type != null && type.length() > 50) {
            throw new IllegalArgumentException("type length cannot exceed 50 characters");
        }
        this.type = type;
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
     * Gets variation_number
     * @return String
     */
    public String getVariationNumber() {
        return this.variationNumber;
    }

    /**
     * Sets variation_number
     * @param variationNumber the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setVariationNumber(String variationNumber) {
        if (variationNumber == null) {
            throw new IllegalArgumentException("variation_number cannot be null");
        }
        if (variationNumber != null && variationNumber.length() > 2147483647) {
            throw new IllegalArgumentException("variation_number length cannot exceed 2147483647 characters");
        }
        this.variationNumber = variationNumber;
    }

    /**
     * Gets config_id
     * @return String
     */
    public String getConfigId() {
        return this.configId;
    }

    /**
     * Sets config_id
     * @param configId the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setConfigId(String configId) {
        if (configId == null) {
            throw new IllegalArgumentException("config_id cannot be null");
        }
        if (configId != null && configId.length() > 2147483647) {
            throw new IllegalArgumentException("config_id length cannot exceed 2147483647 characters");
        }
        this.configId = configId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Variation other = (Variation) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Variation{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", parameterValuesId=" + parameterValuesId +
                ", jobsId=" + jobsId +
                ", newDetails=" + newDetails +
                ", oldDetails=" + oldDetails +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                ", createdBy=" + createdBy +
                ", modifiedBy=" + modifiedBy +
                ", variationNumber=" + variationNumber +
                ", configId=" + configId +
                '}';
    }
}
