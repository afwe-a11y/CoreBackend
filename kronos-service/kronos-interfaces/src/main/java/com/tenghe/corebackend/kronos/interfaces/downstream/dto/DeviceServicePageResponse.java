package com.tenghe.corebackend.kronos.interfaces.downstream.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Device Service 分页响应（防腐层 DTO）。
 *
 * @param <T> 数据元素类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceServicePageResponse<T> {

  private List<T> items;
  private long total;
  private int page;
  private int size;
}
