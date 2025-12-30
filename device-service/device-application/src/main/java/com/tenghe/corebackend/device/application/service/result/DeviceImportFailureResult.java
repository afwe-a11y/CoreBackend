package com.tenghe.corebackend.device.application.service.result;

public class DeviceImportFailureResult {
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
