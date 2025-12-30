package com.tenghe.corebackend.application.service.impl;

import com.tenghe.corebackend.application.AuthenticationApplicationService;

import com.tenghe.corebackend.application.command.LoginCommand;
import com.tenghe.corebackend.application.exception.BusinessException;
import com.tenghe.corebackend.application.service.result.LoginResult;
import com.tenghe.corebackend.application.validation.ValidationUtils;
import com.tenghe.corebackend.interfaces.CaptchaServicePort;
import com.tenghe.corebackend.interfaces.PasswordEncoderPort;
import com.tenghe.corebackend.interfaces.TokenServicePort;
import com.tenghe.corebackend.interfaces.UserRepositoryPort;
import com.tenghe.corebackend.model.User;
import com.tenghe.corebackend.model.enums.UserStatusEnum;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationApplicationServiceImpl implements AuthenticationApplicationService {
    private static final int MAX_FAILED_ATTEMPTS = 10;
    private static final int LOCK_DURATION_MINUTES = 15;

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenServicePort tokenService;
    private final CaptchaServicePort captchaService;

    public AuthenticationApplicationServiceImpl(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenServicePort tokenService,
            CaptchaServicePort captchaService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.captchaService = captchaService;
    }

    public LoginResult login(LoginCommand command) {
        ValidationUtils.requireNonBlank(command.getIdentifier(), "登录标识不能为空");
        ValidationUtils.requireNonBlank(command.getPassword(), "密码不能为空");
        ValidationUtils.requireNonBlank(command.getCaptcha(), "验证码不能为空");

        if (!captchaService.validateCaptcha(command.getCaptchaKey(), command.getCaptcha())) {
            throw new BusinessException("验证码错误");
        }

        User user = findUserByIdentifier(command.getIdentifier());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == UserStatusEnum.DISABLED) {
            throw new BusinessException("账号已被禁用");
        }

        if (isAccountLocked(user)) {
            throw new BusinessException("账号已被锁定，请15分钟后重试");
        }

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new BusinessException("用户名或密码错误");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.update(user);

        String token = tokenService.generateToken(user.getId(), user.getUsername());

        LoginResult result = new LoginResult();
        result.setUserId(user.getId());
        result.setUsername(user.getUsername());
        result.setToken(token);
        result.setRequirePasswordReset(user.isInitialPasswordFlag());
        return result;
    }

    public void logout(String token) {
        tokenService.invalidateToken(token);
    }

    public Long validateSession(String token) {
        Long userId = tokenService.validateToken(token);
        if (userId == null) {
            return null;
        }
        User user = userRepository.findById(userId);
        if (user == null || user.isDeleted() || user.getStatus() == UserStatusEnum.DISABLED) {
            tokenService.invalidateToken(token);
            return null;
        }
        return userId;
    }

    public String generateCaptcha(String key) {
        return captchaService.generateCaptcha(key);
    }

    private User findUserByIdentifier(String identifier) {
        User user = userRepository.findByUsername(identifier);
        if (user != null) {
            return user;
        }
        user = userRepository.findByEmail(identifier);
        if (user != null) {
            return user;
        }
        user = userRepository.findByPhone(identifier);
        return user;
    }

    private boolean isAccountLocked(User user) {
        if (user.getLockedUntil() == null) {
            return false;
        }
        return Instant.now().isBefore(user.getLockedUntil());
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(Instant.now().plus(LOCK_DURATION_MINUTES, ChronoUnit.MINUTES));
        }
        userRepository.update(user);
    }
}
