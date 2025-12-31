package com.tenghe.corebackend.iam.application.service.impl;

import com.tenghe.corebackend.iam.application.PasswordResetApplicationService;

import com.tenghe.corebackend.iam.application.command.ResetPasswordCommand;
import com.tenghe.corebackend.iam.application.command.SendEmailCodeCommand;
import com.tenghe.corebackend.iam.application.exception.BusinessException;
import com.tenghe.corebackend.iam.application.validation.ValidationUtils;
import com.tenghe.corebackend.iam.interfaces.EmailCodeServicePort;
import com.tenghe.corebackend.iam.interfaces.EmailServicePort;
import com.tenghe.corebackend.iam.interfaces.PasswordEncoderPort;
import com.tenghe.corebackend.iam.interfaces.TokenServicePort;
import com.tenghe.corebackend.iam.interfaces.UserRepositoryPort;
import com.tenghe.corebackend.iam.model.User;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetApplicationServiceImpl implements PasswordResetApplicationService {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailServicePort emailService;
    private final EmailCodeServicePort emailCodeService;
    private final TokenServicePort tokenService;

    public PasswordResetApplicationServiceImpl(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            EmailServicePort emailService,
            EmailCodeServicePort emailCodeService,
            TokenServicePort tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.emailCodeService = emailCodeService;
        this.tokenService = tokenService;
    }

    @Override
    public void sendEmailCode(SendEmailCodeCommand command) {
        User user = requireUser(command.getUserId());
        String email = command.getEmail();
        if (email == null || email.trim().isEmpty()) {
            email = user.getEmail();
        }
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("邮箱不能为空");
        }
        if (!email.equals(user.getEmail())) {
            throw new BusinessException("邮箱与绑定邮箱不匹配");
        }
        if (!emailCodeService.canSendCode(email)) {
            throw new BusinessException("发送过于频繁，请30秒后重试");
        }
        String code = emailCodeService.generateCode(email);
        emailService.sendVerificationCode(email, code);
        emailCodeService.markCodeSent(email);
    }

    @Override
    public void resetPassword(ResetPasswordCommand command) {
        User user = requireUser(command.getUserId());
        ValidationUtils.requireNonBlank(command.getOldPassword(), "原密码不能为空");
        ValidationUtils.requireNonBlank(command.getNewPassword(), "新密码不能为空");
        ValidationUtils.requireNonBlank(command.getEmailCode(), "邮箱验证码不能为空");

        if (!passwordEncoder.matches(command.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        if (!emailCodeService.validateCode(user.getEmail(), command.getEmailCode())) {
            throw new BusinessException("邮箱验证码错误或已过期");
        }

        validatePasswordStrength(command.getNewPassword());

        user.setPassword(passwordEncoder.encode(command.getNewPassword()));
        user.setInitialPasswordFlag(false);
        userRepository.update(user);

        tokenService.invalidateUserTokens(user.getId());
    }

    private User requireUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户不存在");
        }
        User user = userRepository.findById(userId);
        if (user == null || user.isDeleted()) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private void validatePasswordStrength(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new BusinessException("密码长度必须为8-20位");
        }
        int typeCount = 0;
        if (LETTER_PATTERN.matcher(password).find()) {
            typeCount++;
        }
        if (DIGIT_PATTERN.matcher(password).find()) {
            typeCount++;
        }
        if (SPECIAL_PATTERN.matcher(password).find()) {
            typeCount++;
        }
        if (typeCount < 2) {
            throw new BusinessException("密码必须包含字母、数字、特殊字符中的至少两种");
        }
    }
}
