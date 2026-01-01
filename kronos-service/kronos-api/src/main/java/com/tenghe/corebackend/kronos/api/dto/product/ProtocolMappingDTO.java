package com.tenghe.corebackend.kronos.api.dto.product;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议映射更新请求DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolMappingDTO {

  /**
   * 协议映射：模型点位标识符 -> 物理采集名
   */
  @NotEmpty(message = "协议映射不能为空")
  private Map<String, String> mappings;
}
