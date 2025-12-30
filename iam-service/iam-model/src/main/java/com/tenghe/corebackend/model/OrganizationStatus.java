package com.tenghe.corebackend.model;

public enum OrganizationStatus {
    NORMAL,
    DISABLED;

    public static OrganizationStatus fromValue(String value) {
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
            return OrganizationStatus.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
