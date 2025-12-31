package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendEmailCodeCommand {
    private Long userId;
    private String email;
}
