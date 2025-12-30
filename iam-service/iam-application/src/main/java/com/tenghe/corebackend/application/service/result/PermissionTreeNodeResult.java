package com.tenghe.corebackend.application.service.result;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionTreeNodeResult {
    private Long id;
    private String permissionCode;
    private String permissionName;
    private String permissionType;
    private String status;
    private Integer sortOrder;
    private List<PermissionTreeNodeResult> children;
}
