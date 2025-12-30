package com.tenghe.corebackend.model;

public enum OrganizationStatus {
    NORMAL,
    DISABLED;

    public static OrganizationStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        if ("正常".equals(value)) {
            return NORMAL;
        }
        if ("停用".equals(value)) {
            return DISABLED;
        }
        return OrganizationStatus.valueOf(value);
    }
}
