package com.tenghe.corebackend.device.api.dto.device;

public class DeviceImportFailure {
    private int rowIndex;
    private String message;

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
