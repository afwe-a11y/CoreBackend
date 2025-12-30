package com.tenghe.corebackend.device.api.dto.device;

public class CreateDeviceResponse {
    private Long id;
    private String deviceSecret;

    public CreateDeviceResponse() {
    }

    public CreateDeviceResponse(Long id, String deviceSecret) {
        this.id = id;
        this.deviceSecret = deviceSecret;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceSecret() {
        return deviceSecret;
    }

    public void setDeviceSecret(String deviceSecret) {
        this.deviceSecret = deviceSecret;
    }
}
