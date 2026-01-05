package com.tenghe.corebackend.iam.interfaces.event;

/**
 * 用户禁用事件
 * 当用户被禁用时发布
 */
public class UserDisabledEvent extends DomainEvent {
  public static final String EVENT_TYPE = "IAM.USER.DISABLED";

  private final Long userId;
  private final String username;

  public UserDisabledEvent(Long userId, String username) {
    super(EVENT_TYPE);
    this.userId = userId;
    this.username = username;
  }

  public Long getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }
}
