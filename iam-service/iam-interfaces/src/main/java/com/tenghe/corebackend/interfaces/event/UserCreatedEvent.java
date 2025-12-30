package com.tenghe.corebackend.interfaces.event;

import java.util.List;

/**
 * 用户创建事件
 * 当新用户被创建时发布
 */
public class UserCreatedEvent extends DomainEvent {
    public static final String EVENT_TYPE = "IAM.USER.CREATED";

    private final Long userId;
    private final String username;
    private final String email;
    private final List<Long> organizationIds;

    public UserCreatedEvent(Long userId, String username, String email, List<Long> organizationIds) {
        super(EVENT_TYPE);
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.organizationIds = organizationIds;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<Long> getOrganizationIds() {
        return organizationIds;
    }
}
