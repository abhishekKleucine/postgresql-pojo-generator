package com.example.pojogenerator.pojos;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO class for table: process_permissions
 * Generated by PostgreSQL POJO Generator
 * 
 * Table Information:
 * - Table Name: process_permissions
 * - Primary Keys: id
 */
public class ProcessPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Database column: id
     * Type: int8 NOT NULL
     * Primary Key
     */
    private Long id;

    /**
     * Database column: type
     * Type: varchar(50) NOT NULL
     */
    private String type;

    /**
     * Database column: description
     * Type: text(2147483647)
     */
    private String description;

    /**
     * Default constructor
     */
    public ProcessPermission() {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProcessPermission other = (ProcessPermission) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "ProcessPermission{" +
                "id=" + id +
                ", type=" + type +
                ", description=" + description +
                '}';
    }
}
