package com.tenghe.corebackend.device.api.dto.device;

import java.util.List;

public class DeviceImportCommitResponse {
  private int successCount;
  private int failureCount;
  private List<DeviceImportFailure> failures;

  public int getSuccessCount() {
    return successCount;
  }

  public void setSuccessCount(int successCount) {
    this.successCount = successCount;
  }

  public int getFailureCount() {
    return failureCount;
  }

  public void setFailureCount(int failureCount) {
    this.failureCount = failureCount;
  }

  public List<DeviceImportFailure> getFailures() {
    return failures;
  }

  public void setFailures(List<DeviceImportFailure> failures) {
    this.failures = failures;
  }
}
