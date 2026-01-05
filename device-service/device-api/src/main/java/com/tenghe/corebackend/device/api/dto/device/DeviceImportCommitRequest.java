package com.tenghe.corebackend.device.api.dto.device;

import java.util.List;

public class DeviceImportCommitRequest {
  private List<DeviceImportRowDto> rows;

  public List<DeviceImportRowDto> getRows() {
    return rows;
  }

  public void setRows(List<DeviceImportRowDto> rows) {
    this.rows = rows;
  }
}
