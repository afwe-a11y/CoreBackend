package com.tenghe.corebackend.model.enums;

public enum OrganizationStatusEnum {
    NORMAL,
    DISABLED;

    public static OrganizationStatusEnum fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        if ("正常".equals(value)) {
            return NORMAL;
        }
        if ("停用".equals(value)) {
            return DISABLED;
        }
        try {
            return OrganizationStatusEnum.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
