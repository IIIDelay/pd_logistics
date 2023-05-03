package org.iiidev.pinda.authority.enumeration.core;

public enum OrgEnum {
    BRANCH_OFFICE(1, "分公司"),
    TOP_TRANSFER_CENTER(2, "一级转运中心"),
    SECONDARY_TRANSFER_CENTER(3, "二级转运中心"),
    BUSINESS_HALL(4, "网点");
    
    private Integer type;
    private String name;

    OrgEnum(Integer type, String name) {
        this.name = name;
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public static OrgEnum getEnumByType(Integer type) {
        OrgEnum[] values;
        if (null == type) {
            return null;
        }
        for (OrgEnum temp : values()) {
            if (temp.getType().equals(type)) {
                return temp;
            }
        }
        return null;
    }
}
