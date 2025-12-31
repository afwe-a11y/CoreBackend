package com.tenghe.corebackend.iam.api.dto.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色摘要信息 DTO
 * 用于其他微服务获取角色基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleSummary {
    private String id;
    private String roleName;
    private String roleCode;
    private Long appId;
    private String appName;
}
