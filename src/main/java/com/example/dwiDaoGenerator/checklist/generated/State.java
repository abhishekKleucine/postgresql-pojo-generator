package com.example.dwiDaoGenerator.checklist.generated;

/**
 * State enums for various entities
 * Auto-generated to support DAO operations
 */
public class State {

    /**
     * Checklist state enumeration
     */
    public enum Checklist {
        BEING_BUILT,
        PUBLISHED,
        ARCHIVED,
        DRAFT,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        RECALLED
    }

    /**
     * Task state enumeration
     */
    public enum Task {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        SKIPPED,
        FAILED
    }

    /**
     * Job state enumeration
     */
    public enum Job {
        CREATED,
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        FAILED
    }
}
