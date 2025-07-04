package com.example.pojogenerator.pojos;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO class for table: email_templates
 * Generated by PostgreSQL POJO Generator
 * 
 * Table Information:
 * - Table Name: email_templates
 * 
 * Indexes:
 * - dd909f76a4d70a4d494e534a47de (UNIQUE): id
 * - email_templates_name_key (UNIQUE): name
 */
public class EmailTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Database column: id
     * Type: int8 NOT NULL
     */
    private Long id;

    /**
     * Database column: name
     * Type: varchar(255) NOT NULL
     */
    private String name;

    /**
     * Database column: content
     * Type: text(2147483647) NOT NULL
     */
    private String content;

    /**
     * Database column: archived
     * Type: bool NOT NULL DEFAULT: false
     */
    private Boolean archived;

    /**
     * Default constructor
     */
    public EmailTemplate() {
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
        if (name != null && name.length() > 255) {
            throw new IllegalArgumentException("name length cannot exceed 255 characters");
        }
        this.name = name;
    }

    /**
     * Gets content
     * @return String
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Sets content
     * @param content the value to set
     * @throws IllegalArgumentException if constraint validation fails
     */
    public void setContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("content cannot be null");
        }
        if (content != null && content.length() > 2147483647) {
            throw new IllegalArgumentException("content length cannot exceed 2147483647 characters");
        }
        this.content = content;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EmailTemplate other = (EmailTemplate) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name) && Objects.equals(this.content, other.content) && Objects.equals(this.archived, other.archived);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.content, this.archived);
    }

    @Override
    public String toString() {
        return "EmailTemplate{" +
                "id=" + id +
                ", name=" + name +
                ", content=" + content +
                ", archived=" + archived +
                '}';
    }
}
