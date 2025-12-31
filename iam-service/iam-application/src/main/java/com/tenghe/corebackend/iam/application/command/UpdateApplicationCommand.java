package com.tenghe.corebackend.iam.application.command;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateApplicationCommand {
    private Long appId;
    private String appName;
    private String description;
    private String status;
    private List<Long> permissionIds;
}
