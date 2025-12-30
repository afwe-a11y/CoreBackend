package com.tenghe.corebackend.api.dto.user;

import com.tenghe.corebackend.api.dto.member.RoleSelectionDto;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

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
