package com.tenghe.corebackend.application.service.result;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserListItemResult {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String status;
    private List<String> organizationNames;
    private List<String> roleNames;
    private Instant createdAt;
}
