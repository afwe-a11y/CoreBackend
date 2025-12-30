package com.tenghe.corebackend.interfaces.event;

import java.time.Instant;

/**
 * 领域事件基类
 * 所有 IAM 领域事件都应继承此类
 */
public abstract class DomainEvent {
    private final String eventId;
    private final Instant occurredAt;
    private final String eventType;

    protected DomainEvent(String eventType) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getEventType() {
        return eventType;
    }
}
