package com.tenghe.corebackend.kronos.api.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页请求参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

  /**
   * 当前页码（从1开始）
   */
  @Min(value = 1, message = "页码最小为1")
  @Builder.Default
  private Integer page = 1;

  /**
   * 每页数量
   */
  @Min(value = 1, message = "每页数量最小为1")
  @Max(value = 100, message = "每页数量最大为100")
  @Builder.Default
  private Integer size = 20;
}
