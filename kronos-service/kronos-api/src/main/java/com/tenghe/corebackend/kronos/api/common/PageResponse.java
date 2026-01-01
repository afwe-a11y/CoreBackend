package com.tenghe.corebackend.kronos.api.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页响应数据。
 *
 * @param <T> 数据元素类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

  /**
   * 数据列表
   */
  private List<T> items;

  /**
   * 总记录数
   */
  private Long total;

  /**
   * 当前页码
   */
  private Integer page;

  /**
   * 每页数量
   */
  private Integer size;

  /**
   * 总页数
   */
  private Integer totalPages;

  /**
   * 构建分页响应
   */
  public static <T> PageResponse<T> of(List<T> items, Long total, Integer page, Integer size) {
    int totalPages = (int) Math.ceil((double) total / size);
    return PageResponse.<T>builder()
        .items(items)
        .total(total)
        .page(page)
        .size(size)
        .totalPages(totalPages)
        .build();
  }
}
