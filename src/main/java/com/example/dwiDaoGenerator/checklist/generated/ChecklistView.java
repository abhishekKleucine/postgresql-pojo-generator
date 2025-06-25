package com.example.dwiDaoGenerator.checklist.generated;

/**
 * Lightweight view for Checklist projections
 * Auto-generated based on method return types
 */
public class ChecklistView {

    private Long id;
    private String code;
    private String name;
    private String colorCode;

    public ChecklistView() {}

    public ChecklistView(Long id, String code, String name, String colorCode) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.colorCode = colorCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public String toString() {
        return "ChecklistView{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", colorCode='" + colorCode + '\'' +
                '}';
    }
}
