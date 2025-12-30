package com.tenghe.corebackend.application.service.result;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetailResult {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String status;
    private Instant createdAt;
    private List<Long> organizationIds;
    private List<RoleSelectionResult> roleSelections;
}
