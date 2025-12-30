package com.tenghe.corebackend.interfaces.event;

/**
 * 组织删除事件
 * 当组织被软删除时发布
 */
public class OrganizationDeletedEvent extends DomainEvent {
    public static final String EVENT_TYPE = "IAM.ORGANIZATION.DELETED";

    private final Long organizationId;
    private final String name;

    public OrganizationDeletedEvent(Long organizationId, String name) {
        super(EVENT_TYPE);
        this.organizationId = organizationId;
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }
}
