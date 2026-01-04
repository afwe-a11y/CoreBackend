package com.tenghe.corebackend.kronos.controller.config;

import com.tenghe.corebackend.kronos.controller.interceptor.AuthenticationInterceptor;
import com.tenghe.corebackend.kronos.controller.interceptor.TraceIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置。
 * 注册拦截器。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final TraceIdInterceptor traceIdInterceptor;
  private final AuthenticationInterceptor authenticationInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(traceIdInterceptor)
        .addPathPatterns("/api/**")
        .order(0);
    
    registry.addInterceptor(authenticationInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns("/api/v1/auth/**", "/api/v1/health")
        .order(1);
  }
}
