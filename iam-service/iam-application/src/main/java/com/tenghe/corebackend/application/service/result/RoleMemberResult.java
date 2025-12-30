package com.tenghe.corebackend.application.service.result;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleMemberResult {
    private Long userId;
    private String username;
    private String name;
    private String phone;
    private String email;
}
