package com.tenghe.corebackend.iam.interfaces.event;

import java.util.Set;

/**
 * 角色权限变更事件
 * 当角色的权限配置发生变化时发布
 */
public class RolePermissionChangedEvent extends DomainEvent {
  public static final String EVENT_TYPE = "IAM.ROLE.PERMISSION.CHANGED";

  private final Long roleId;
  private final String roleCode;
  private final Long appId;
  private final Set<Long> permissionIds;

  public RolePermissionChangedEvent(Long roleId, String roleCode, Long appId, Set<Long> permissionIds) {
    super(EVENT_TYPE);
    this.roleId = roleId;
    this.roleCode = roleCode;
    this.appId = appId;
    this.permissionIds = permissionIds;
  }

  public Long getRoleId() {
    return roleId;
  }

  public String getRoleCode() {
    return roleCode;
  }

  public Long getAppId() {
    return appId;
  }

  public Set<Long> getPermissionIds() {
    return permissionIds;
  }
}
