package com.tenghe.corebackend.device.api.dto.model;

import java.util.List;

public class DeviceModelPointDto {
    private String identifier;
    private String name;
    private String type;
    private String dataType;
    private List<String> enumItems;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<String> getEnumItems() {
        return enumItems;
    }

    public void setEnumItems(List<String> enumItems) {
        this.enumItems = enumItems;
    }
}
