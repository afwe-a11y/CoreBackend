package com.tenghe.corebackend.device.application.service.result;

import java.util.List;

public class DeviceImportPreviewResult {
    private int total;
    private int createCount;
    private int updateCount;
    private int invalidCount;
    private List<DeviceImportPreviewItemResult> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCreateCount() {
        return createCount;
    }

    public void setCreateCount(int createCount) {
        this.createCount = createCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(int invalidCount) {
        this.invalidCount = invalidCount;
    }

    public List<DeviceImportPreviewItemResult> getItems() {
        return items;
    }

    public void setItems(List<DeviceImportPreviewItemResult> items) {
        this.items = items;
    }
}
