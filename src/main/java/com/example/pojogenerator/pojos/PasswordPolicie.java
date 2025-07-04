package com.example.pojogenerator.pojos;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO class for table: password_policies
 * Generated by PostgreSQL POJO Generator
 * 
 * Table Information:
 * - Table Name: password_policies
 * - Primary Keys: id
 * 
 * Indexes:
 * - uk_i31gwblxtrvf9xw9020ve5hpg (UNIQUE): organisations_id
 * 
 * Foreign Keys:
 * - created_by → users.id
 * - modified_by → users.id
 * - organisations_id → organisations.id
 */
public class PasswordPolicie implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Database column: id
     * Type: int8 NOT NULL
     * Primary Key
     */
    private Long id;

    /**
     * Database column: created_at
     * Type: int8 NOT NULL
     */
    private Long createdAt;

    /**
     * Database column: created_by
     * Type: int8
     * Foreign Key → users.id
     */
    private Long createdBy;

    /**
     * Database column: modified_at
     * Type: int8 NOT NULL
     */
    private Long modifiedAt;

    /**
     * Database column: modified_by
     * Type: int8
     * Foreign Key → users.id
     */
    private Long modifiedBy;

    /**
     * Database column: allow_password_similar_to_username_or_email
     * Type: bool DEFAULT: false
     */
    private Boolean allowPasswordSimilarToUsernameOrEmail;

    /**
     * Database column: maximum_password_age
     * Type: int4
     */
    private Integer maximumPasswordAge;

    /**
     * Database column: minimum_lowercase_characters
     * Type: int4
     */
    private Integer minimumLowercaseCharacters;

    /**
     * Database column: minimum_numeric_characters
     * Type: int4
     */
    private Integer minimumNumericCharacters;

    /**
     * Database column: minimum_password_history
     * Type: int4
     */
    private Integer minimumPasswordHistory;

    /**
     * Database column: minimum_password_length
     * Type: int4
     */
    private Integer minimumPasswordLength;

    /**
     * Database column: minimum_special_characters
     * Type: int4
     */
    private Integer minimumSpecialCharacters;

    /**
     * Database column: minimum_uppercase_characters
     * Type: int4
     */
    private Integer minimumUppercaseCharacters;

    /**
     * Database column: organisations_id
     * Type: int8 NOT NULL
     * Foreign Key → organisations.id
     */
    private Long organisationsId;

    /**
     * Database column: password_expiration
     * Type: int4
     */
    private Integer passwordExpiration;

    /**
     * Default constructor
     */
    public PasswordPolicie() {
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
     * Gets created_by
     * @return Long
     */
    public Long getCreatedBy() {
        return this.createdBy;
    }

    /**
     * Sets created_by
     * @param createdBy the value to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
     * Gets modified_by
     * @return Long
     */
    public Long getModifiedBy() {
        return this.modifiedBy;
    }

    /**
     * Sets modified_by
     * @param modifiedBy the value to set
     */
    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Gets allow_password_similar_to_username_or_email
     * @return Boolean
     */
    public Boolean getAllowPasswordSimilarToUsernameOrEmail() {
        return this.allowPasswordSimilarToUsernameOrEmail;
    }

    /**
     * Sets allow_password_similar_to_username_or_email
     * @param allowPasswordSimilarToUsernameOrEmail the value to set
     */
    public void setAllowPasswordSimilarToUsernameOrEmail(Boolean allowPasswordSimilarToUsernameOrEmail) {
        this.allowPasswordSimilarToUsernameOrEmail = allowPasswordSimilarToUsernameOrEmail;
    }

    /**
     * Gets maximum_password_age
     * @return Integer
     */
    public Integer getMaximumPasswordAge() {
        return this.maximumPasswordAge;
    }

    /**
     * Sets maximum_password_age
     * @param maximumPasswordAge the value to set
     */
    public void setMaximumPasswordAge(Integer maximumPasswordAge) {
        this.maximumPasswordAge = maximumPasswordAge;
    }

    /**
     * Gets minimum_lowercase_characters
     * @return Integer
     */
    public Integer getMinimumLowercaseCharacters() {
        return this.minimumLowercaseCharacters;
    }

    /**
     * Sets minimum_lowercase_characters
     * @param minimumLowercaseCharacters the value to set
     */
    public void setMinimumLowercaseCharacters(Integer minimumLowercaseCharacters) {
        this.minimumLowercaseCharacters = minimumLowercaseCharacters;
    }

    /**
     * Gets minimum_numeric_characters
     * @return Integer
     */
    public Integer getMinimumNumericCharacters() {
        return this.minimumNumericCharacters;
    }

    /**
     * Sets minimum_numeric_characters
     * @param minimumNumericCharacters the value to set
     */
    public void setMinimumNumericCharacters(Integer minimumNumericCharacters) {
        this.minimumNumericCharacters = minimumNumericCharacters;
    }

    /**
     * Gets minimum_password_history
     * @return Integer
     */
    public Integer getMinimumPasswordHistory() {
        return this.minimumPasswordHistory;
    }

    /**
     * Sets minimum_password_history
     * @param minimumPasswordHistory the value to set
     */
    public void setMinimumPasswordHistory(Integer minimumPasswordHistory) {
        this.minimumPasswordHistory = minimumPasswordHistory;
    }

    /**
     * Gets minimum_password_length
     * @return Integer
     */
    public Integer getMinimumPasswordLength() {
        return this.minimumPasswordLength;
    }

    /**
     * Sets minimum_password_length
     * @param minimumPasswordLength the value to set
     */
    public void setMinimumPasswordLength(Integer minimumPasswordLength) {
        this.minimumPasswordLength = minimumPasswordLength;
    }

    /**
     * Gets minimum_special_characters
     * @return Integer
     */
    public Integer getMinimumSpecialCharacters() {
        return this.minimumSpecialCharacters;
    }

    /**
     * Sets minimum_special_characters
     * @param minimumSpecialCharacters the value to set
     */
    public void setMinimumSpecialCharacters(Integer minimumSpecialCharacters) {
        this.minimumSpecialCharacters = minimumSpecialCharacters;
    }

    /**
     * Gets minimum_uppercase_characters
     * @return Integer
     */
    public Integer getMinimumUppercaseCharacters() {
        return this.minimumUppercaseCharacters;
    }

    /**
     * Sets minimum_uppercase_characters
     * @param minimumUppercaseCharacters the value to set
     */
    public void setMinimumUppercaseCharacters(Integer minimumUppercaseCharacters) {
        this.minimumUppercaseCharacters = minimumUppercaseCharacters;
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
     * Gets password_expiration
     * @return Integer
     */
    public Integer getPasswordExpiration() {
        return this.passwordExpiration;
    }

    /**
     * Sets password_expiration
     * @param passwordExpiration the value to set
     */
    public void setPasswordExpiration(Integer passwordExpiration) {
        this.passwordExpiration = passwordExpiration;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PasswordPolicie other = (PasswordPolicie) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "PasswordPolicie{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", modifiedAt=" + modifiedAt +
                ", modifiedBy=" + modifiedBy +
                ", allowPasswordSimilarToUsernameOrEmail=" + allowPasswordSimilarToUsernameOrEmail +
                ", maximumPasswordAge=" + maximumPasswordAge +
                ", minimumLowercaseCharacters=" + minimumLowercaseCharacters +
                ", minimumNumericCharacters=" + minimumNumericCharacters +
                ", minimumPasswordHistory=" + minimumPasswordHistory +
                ", minimumPasswordLength=" + minimumPasswordLength +
                ", minimumSpecialCharacters=" + minimumSpecialCharacters +
                ", minimumUppercaseCharacters=" + minimumUppercaseCharacters +
                ", organisationsId=" + organisationsId +
                ", passwordExpiration=" + passwordExpiration +
                '}';
    }
}
