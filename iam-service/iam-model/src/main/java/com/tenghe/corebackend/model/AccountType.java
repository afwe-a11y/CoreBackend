package com.tenghe.corebackend.model;

public enum AccountType {
    MANAGEMENT,
    APPLICATION;

    public static AccountType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        if ("管理端".equals(value)) {
            return MANAGEMENT;
        }
        if ("应用端".equals(value)) {
            return APPLICATION;
        }
        try {
            return AccountType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
