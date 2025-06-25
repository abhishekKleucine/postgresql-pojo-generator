package com.example.dwiDaoGenerator.checklist.generated;

/**
 * Lightweight view for JobLogMigrationChecklist projections
 * Auto-generated based on method return types
 */
public class JobLogMigrationChecklistView {

    private Long id;
    private String name;
    private String code;
    private String state;

    public JobLogMigrationChecklistView() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "JobLogMigrationChecklistView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
