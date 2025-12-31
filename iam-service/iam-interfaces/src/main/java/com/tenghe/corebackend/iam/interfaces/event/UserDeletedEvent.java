package com.tenghe.corebackend.iam.interfaces.event;

/**
 * 用户删除事件
 * 当用户被软删除时发布
 */
public class UserDeletedEvent extends DomainEvent {
    public static final String EVENT_TYPE = "IAM.USER.DELETED";

    private final Long userId;
    private final String username;

    public UserDeletedEvent(Long userId, String username) {
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
