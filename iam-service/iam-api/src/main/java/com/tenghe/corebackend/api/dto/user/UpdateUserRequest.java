package com.tenghe.corebackend.api.dto.user;

import com.tenghe.corebackend.api.dto.member.RoleSelectionDto;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserRequest {
    private String name;
    private String phone;
    private String email;
    private List<Long> organizationIds;
    private List<RoleSelectionDto> roleSelections;
}
