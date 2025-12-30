package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.service.result.PermissionTreeNodeResult;
import java.util.List;

public interface PermissionApplicationService {

    List<PermissionTreeNodeResult> getPermissionTree();

    void togglePermissionStatus(Long permissionId, String status);
}
