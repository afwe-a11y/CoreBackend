package com.tenghe.corebackend.application.command;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserQueryCommand {
    private String keyword;
    private String status;
    private List<Long> organizationIds;
    private List<String> roleCodes;
    private Integer page;
    private Integer size;
}
