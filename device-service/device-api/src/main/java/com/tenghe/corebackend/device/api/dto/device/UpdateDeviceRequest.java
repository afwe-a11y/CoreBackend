package com.tenghe.corebackend.device.api.dto.device;

import java.util.Map;

public class UpdateDeviceRequest {
    private String name;
    private Long gatewayId;
    private Map<String, Object> dynamicAttributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Long gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Map<String, Object> getDynamicAttributes() {
        return dynamicAttributes;
    }

    public void setDynamicAttributes(Map<String, Object> dynamicAttributes) {
        this.dynamicAttributes = dynamicAttributes;
    }
}
