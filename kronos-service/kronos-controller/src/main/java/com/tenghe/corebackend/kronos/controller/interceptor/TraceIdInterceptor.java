package com.tenghe.corebackend.kronos.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * TraceId 拦截器。
 * 为每个请求生成或传递 traceId，并放入 MDC 中供日志使用。
 */
@Slf4j
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

  private static final String TRACE_ID_HEADER = "X-Trace-Id";
  private static final String TRACE_ID_MDC_KEY = "traceId";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String traceId = request.getHeader(TRACE_ID_HEADER);
    if (traceId == null || traceId.isEmpty()) {
      traceId = generateTraceId();
    }
    
    MDC.put(TRACE_ID_MDC_KEY, traceId);
    response.setHeader(TRACE_ID_HEADER, traceId);
    
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    MDC.remove(TRACE_ID_MDC_KEY);
  }

  /**
   * 生成 traceId
   */
  private String generateTraceId() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
