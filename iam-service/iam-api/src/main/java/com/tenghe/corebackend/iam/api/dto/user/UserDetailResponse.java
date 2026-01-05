package com.tenghe.corebackend.iam.api.dto.user;

import com.tenghe.corebackend.iam.api.dto.member.RoleSelectionDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDetailResponse {
  private String id;
  private String username;
  private String name;
  private String phone;
  private String email;
  private String status;
  private String createdDate;
  private List<Long> organizationIds;
  private List<RoleSelectionDto> roleSelections;
}
