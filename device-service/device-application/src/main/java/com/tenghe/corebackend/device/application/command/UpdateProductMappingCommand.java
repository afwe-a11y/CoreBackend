package com.tenghe.corebackend.device.application.command;

import java.util.Map;

public class UpdateProductMappingCommand {
    private Long productId;
    private Map<String, String> protocolMapping;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Map<String, String> getProtocolMapping() {
        return protocolMapping;
    }

    public void setProtocolMapping(Map<String, String> protocolMapping) {
        this.protocolMapping = protocolMapping;
    }
}
