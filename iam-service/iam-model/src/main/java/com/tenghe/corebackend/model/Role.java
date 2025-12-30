package com.tenghe.corebackend.model;

import com.tenghe.corebackend.model.enums.RoleStatusEnum;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Role {
    private Long id;
    private Long appId;
    private String roleName;
    private String roleCode;
    private String description;
    private RoleStatusEnum status;
    private boolean preset;
    private Instant createdAt;
    private boolean deleted;
}
