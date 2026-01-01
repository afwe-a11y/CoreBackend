package com.tenghe.corebackend.kronos.interfaces.downstream.dto.product;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新产品协议映射请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductMappingRequest {

  private Map<String, String> protocolMapping;
}
