package com.tenghe.corebackend.device.application.service.result;

import java.util.List;

public class DeviceImportCommitResult {
  private int successCount;
  private int failureCount;
  private List<DeviceImportFailureResult> failures;

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

  public List<DeviceImportFailureResult> getFailures() {
    return failures;
  }

  public void setFailures(List<DeviceImportFailureResult> failures) {
    this.failures = failures;
  }
}
