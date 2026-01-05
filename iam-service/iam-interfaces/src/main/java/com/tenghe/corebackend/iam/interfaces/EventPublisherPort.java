package com.tenghe.corebackend.iam.interfaces;

import com.tenghe.corebackend.iam.interfaces.event.DomainEvent;

/**
 * 领域事件发布端口
 * 供 Application 层发布事件，由 Infrastructure 层实现
 * 其他微服务可以订阅这些事件进行响应
 */
public interface EventPublisherPort {

  /**
   * 发布领域事件
   *
   * @param event 领域事件
   */
  void publish(DomainEvent event);

  /**
   * 异步发布领域事件
   *
   * @param event 领域事件
   */
  void publishAsync(DomainEvent event);
}
