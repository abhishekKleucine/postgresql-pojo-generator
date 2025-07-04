package com.example.pojogenerator.pojos;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO class for table: codes
 * Generated by PostgreSQL POJO Generator
 * 
 * Table Information:
 * - Table Name: codes
 * - Primary Keys: type, clause, organisations_id
 * 
 * Indexes:
 * - codes_organisations_id_type_clause_pk (UNIQUE): organisations_id, type, clause
 * 
 * Foreign Keys:
 * - organisations_id → organisations.id
 */
public class Code implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Database column: type
     * Type: varchar(255) NOT NULL
     * Primary Key
     */
    private String type;

    /**
     * Database column: clause
     * Type: int2(5) NOT NULL
     * Primary Key
     */
    private String clause;

    /**
     * Database column: counter
     * Type: int4 NOT NULL
     */
    private Integer counter;

    /**
     * Database column: organisations_id
     * Type: int8 NOT NULL
     * Primary Key
     * Foreign Key → organisations.id
     */
    private Long organisationsId;

    /**
     * Default constructor
     */
    public Code() {
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
        if (type != null && type.length() > 255) {
            throw new IllegalArgumentException("type length cannot exceed 255 characters");
        }
        this.type = type;
    }

    /**
     * Gets clause
     * @return String
     */
    public String getClause() {
        return this.clause;
    }

    /**
     * Sets clause
     * @param clause the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setClause(String clause) {
        if (clause == null) {
            throw new IllegalArgumentException("clause cannot be null");
        }
        if (clause != null && clause.length() > 5) {
            throw new IllegalArgumentException("clause length cannot exceed 5 characters");
        }
        this.clause = clause;
    }

    /**
     * Gets counter
     * @return Integer
     */
    public Integer getCounter() {
        return this.counter;
    }

    /**
     * Sets counter
     * @param counter the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setCounter(Integer counter) {
        if (counter == null) {
            throw new IllegalArgumentException("counter cannot be null");
        }
        this.counter = counter;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Code other = (Code) obj;
        return Objects.equals(this.type, other.type) && Objects.equals(this.clause, other.clause) && Objects.equals(this.organisationsId, other.organisationsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.clause, this.organisationsId);
    }

    @Override
    public String toString() {
        return "Code{" +
                "type=" + type +
                ", clause=" + clause +
                ", counter=" + counter +
                ", organisationsId=" + organisationsId +
                '}';
    }
}
