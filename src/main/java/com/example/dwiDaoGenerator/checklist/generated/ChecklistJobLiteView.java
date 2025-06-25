package com.example.dwiDaoGenerator.checklist.generated;

/**
 * Lightweight view for ChecklistJobLite projections
 * Auto-generated based on method return types
 */
public class ChecklistJobLiteView {

    private Long id;
    private String name;
    private String code;

    public ChecklistJobLiteView() {}

    public ChecklistJobLiteView(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

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

    @Override
    public String toString() {
        return "ChecklistJobLiteView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
