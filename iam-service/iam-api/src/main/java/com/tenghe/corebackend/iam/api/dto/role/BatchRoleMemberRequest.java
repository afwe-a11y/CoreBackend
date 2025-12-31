package com.tenghe.corebackend.iam.api.dto.role;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatchRoleMemberRequest {
    private Long organizationId;
    private List<Long> userIds;
}
