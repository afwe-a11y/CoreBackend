package com.tenghe.corebackend.iam.api.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织摘要信息 DTO
 * 用于其他微服务获取组织基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationSummary {
    private String id;
    private String name;
    private String code;
    private String status;
}
