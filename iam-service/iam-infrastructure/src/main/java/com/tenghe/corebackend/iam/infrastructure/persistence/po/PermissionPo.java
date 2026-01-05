package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class PermissionPo {
  private Long id;
  private Long templateId;
  private String permCode;
  private String permName;
  private String permType;
  private String status;
  private String parentCode;
  private Long parentId;
  private Integer sortNo;
  private Instant createTime;
  private Integer deleted;
}
