package com.tenghe.corebackend.api.dto.permission;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionTreeNode {
    private String id;
    private String permissionCode;
    private String permissionName;
    private String permissionType;
    private String status;
    private Integer sortOrder;
    private List<PermissionTreeNode> children;
}
