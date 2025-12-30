package com.tenghe.corebackend.api.dto.role;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatchRoleMemberRequest {
    private Long organizationId;
    private List<Long> userIds;
}
