package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateRoleCommand {
    private Long roleId;
    private String roleName;
    private String description;
    private String status;
}
