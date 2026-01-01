package com.tenghe.corebackend.kronos.api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建产品请求DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

  /**
   * 产品名称
   */
  @NotBlank(message = "产品名称不能为空")
  @Size(max = 50, message = "产品名称最长50字符")
  private String name;

  /**
   * 产品Key（可选，不填则系统生成）
   */
  @Size(max = 30, message = "产品Key最长30字符")
  @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "产品Key只能包含英文和数字")
  private String productKey;

  /**
   * 产品密钥（可选，不填则系统生成）
   */
  @Size(max = 30, message = "产品密钥最长30字符")
  @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "产品密钥只能包含英文和数字")
  private String productSecret;

  /**
   * 关联的设备模型ID
   */
  @NotNull(message = "设备模型ID不能为空")
  private Long deviceModelId;

  /**
   * 接入方式：GENERAL_MQTT/CUSTOM
   */
  @NotBlank(message = "接入方式不能为空")
  private String accessMode;

  /**
   * 产品类型
   */
  private String productType;

  /**
   * 协议映射
   */
  private Map<String, String> protocolMappings;

  /**
   * 描述
   */
  @Size(max = 500, message = "描述最长500字符")
  private String description;
}
