package com.tenghe.corebackend.application.command;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfigureRolePermissionsCommand {
    private Long roleId;
    private List<Long> permissionIds;
}
