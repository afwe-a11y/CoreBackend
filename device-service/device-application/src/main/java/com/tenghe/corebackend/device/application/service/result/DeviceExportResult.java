package com.tenghe.corebackend.device.application.service.result;

import java.util.List;

public class DeviceExportResult {
  private String fileName;
  private List<DeviceExportRowResult> rows;

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public List<DeviceExportRowResult> getRows() {
    return rows;
  }

  public void setRows(List<DeviceExportRowResult> rows) {
    this.rows = rows;
  }
}
