package com.tenghe.corebackend.device.api.dto.device;

import java.util.List;

public class DeviceExportResponse {
    private String fileName;
    private List<DeviceExportRow> rows;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<DeviceExportRow> getRows() {
        return rows;
    }

    public void setRows(List<DeviceExportRow> rows) {
        this.rows = rows;
    }
}
