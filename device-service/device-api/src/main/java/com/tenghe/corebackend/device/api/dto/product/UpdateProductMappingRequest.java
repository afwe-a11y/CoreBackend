package com.tenghe.corebackend.device.api.dto.product;

import java.util.Map;

public class UpdateProductMappingRequest {
    private Map<String, String> protocolMapping;

    public Map<String, String> getProtocolMapping() {
        return protocolMapping;
    }

    public void setProtocolMapping(Map<String, String> protocolMapping) {
        this.protocolMapping = protocolMapping;
    }
}
