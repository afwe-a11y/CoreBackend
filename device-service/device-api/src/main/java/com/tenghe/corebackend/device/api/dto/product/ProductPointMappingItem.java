package com.tenghe.corebackend.device.api.dto.product;

public class ProductPointMappingItem {
    private String pointIdentifier;
    private String protocolAddress;

    public String getPointIdentifier() {
        return pointIdentifier;
    }

    public void setPointIdentifier(String pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }

    public String getProtocolAddress() {
        return protocolAddress;
    }

    public void setProtocolAddress(String protocolAddress) {
        this.protocolAddress = protocolAddress;
    }
}
