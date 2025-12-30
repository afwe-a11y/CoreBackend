package com.tenghe.corebackend.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String password;
    private boolean initialPasswordFlag;
    private int failedLoginAttempts;
    private Instant lockedUntil;
    private UserStatus status;
    private AccountType accountType;
    private Long primaryOrgId;
    private Instant createdAt;
    private boolean deleted;
}
