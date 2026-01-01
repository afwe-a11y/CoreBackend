package com.tenghe.corebackend.kronos.api.dto.devicemodel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型点位批量导入请求DTO。
 * 事务性导入：全部成功或全部失败。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPointImportDTO {

  /**
   * 点位列表
   */
  @NotEmpty(message = "点位列表不能为空")
  @Valid
  private List<ModelPointDTO> points;
}
