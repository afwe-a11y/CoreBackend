package com.tenghe.corebackend.iam.interfaces.event;

import java.util.List;

/**
 * 用户角色变更事件
 * 当用户的角色分配发生变化时发布
 */
public class UserRoleChangedEvent extends DomainEvent {
    public static final String EVENT_TYPE = "IAM.USER.ROLE.CHANGED";

    private final Long userId;
    private final Long organizationId;
    private final List<String> roleCodes;

    public UserRoleChangedEvent(Long userId, Long organizationId, List<String> roleCodes) {
        super(EVENT_TYPE);
        this.userId = userId;
        this.organizationId = organizationId;
        this.roleCodes = roleCodes;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }
}
