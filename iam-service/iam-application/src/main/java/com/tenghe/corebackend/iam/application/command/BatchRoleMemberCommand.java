package com.tenghe.corebackend.iam.application.command;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatchRoleMemberCommand {
    private Long roleId;
    private Long organizationId;
    private List<Long> userIds;
}
