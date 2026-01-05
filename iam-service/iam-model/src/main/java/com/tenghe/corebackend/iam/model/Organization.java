package com.tenghe.corebackend.iam.model;

import com.tenghe.corebackend.iam.model.enums.OrganizationStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Organization {
  private Long id;
  private String name;
  private String code;
  private String description;
  private OrganizationStatusEnum status;
  private Instant createdAt;
  private String primaryAdminDisplay;
  private String contactName;
  private String contactPhone;
  private String contactEmail;
  private boolean deleted;
}
