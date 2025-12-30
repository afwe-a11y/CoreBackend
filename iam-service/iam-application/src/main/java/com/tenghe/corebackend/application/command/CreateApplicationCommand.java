package com.tenghe.corebackend.application.command;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateApplicationCommand {
    private String appName;
    private String appCode;
    private String description;
    private List<Long> permissionIds;
}
