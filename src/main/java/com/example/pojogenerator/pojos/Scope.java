package com.example.pojogenerator.pojos;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO class for table: scopes
 * Generated by PostgreSQL POJO Generator
 * 
 * Table Information:
 * - Table Name: scopes
 * - Primary Keys: id
 * 
 * Indexes:
 * - ukrbhy5v58w1gtysg0h9smgo0no (UNIQUE): name, scope_groups_id, archived
 * 
 * Foreign Keys:
 * - created_by → users.id
 * - modified_by → users.id
 * - scope_groups_id → scope_groups.id
 */
public class Scope implements Serializable {

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
     * Database column: archived
     * Type: bool NOT NULL DEFAULT: false
     */
    private Boolean archived;

    /**
     * Database column: name
     * Type: varchar(255)
     */
    private String name;

    /**
     * Database column: order_tree
     * Type: int4 NOT NULL
     */
    private Integer orderTree;

    /**
     * Database column: scope_groups_id
     * Type: int8 NOT NULL
     * Foreign Key → scope_groups.id
     */
    private Long scopeGroupsId;

    /**
     * Default constructor
     */
    public Scope() {
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
     * Gets archived
     * @return Boolean
     */
    public Boolean getArchived() {
        return this.archived;
    }

    /**
     * Sets archived
     * @param archived the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setArchived(Boolean archived) {
        if (archived == null) {
            throw new IllegalArgumentException("archived cannot be null");
        }
        this.archived = archived;
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
        if (name != null && name.length() > 255) {
            throw new IllegalArgumentException("name length cannot exceed 255 characters");
        }
        this.name = name;
    }

    /**
     * Gets order_tree
     * @return Integer
     */
    public Integer getOrderTree() {
        return this.orderTree;
    }

    /**
     * Sets order_tree
     * @param orderTree the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setOrderTree(Integer orderTree) {
        if (orderTree == null) {
            throw new IllegalArgumentException("order_tree cannot be null");
        }
        this.orderTree = orderTree;
    }

    /**
     * Gets scope_groups_id
     * @return Long
     */
    public Long getScopeGroupsId() {
        return this.scopeGroupsId;
    }

    /**
     * Sets scope_groups_id
     * @param scopeGroupsId the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setScopeGroupsId(Long scopeGroupsId) {
        if (scopeGroupsId == null) {
            throw new IllegalArgumentException("scope_groups_id cannot be null");
        }
        this.scopeGroupsId = scopeGroupsId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Scope other = (Scope) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Scope{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", modifiedAt=" + modifiedAt +
                ", modifiedBy=" + modifiedBy +
                ", archived=" + archived +
                ", name=" + name +
                ", orderTree=" + orderTree +
                ", scopeGroupsId=" + scopeGroupsId +
                '}';
    }
}
