package com.tenghe.corebackend.device.application.command;

public class ToggleGatewayCommand {
    private Long gatewayId;
    private boolean enabled;

    public Long getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Long gatewayId) {
        this.gatewayId = gatewayId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
