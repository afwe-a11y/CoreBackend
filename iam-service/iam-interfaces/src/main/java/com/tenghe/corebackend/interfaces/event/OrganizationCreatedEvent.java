package com.tenghe.corebackend.interfaces.event;

/**
 * 组织创建事件
 * 当新组织被创建时发布
 */
public class OrganizationCreatedEvent extends DomainEvent {
    public static final String EVENT_TYPE = "IAM.ORGANIZATION.CREATED";

    private final Long organizationId;
    private final String name;
    private final String code;

    public OrganizationCreatedEvent(Long organizationId, String name, String code) {
        super(EVENT_TYPE);
        this.organizationId = organizationId;
        this.name = name;
        this.code = code;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
