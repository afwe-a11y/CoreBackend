package com.tenghe.corebackend.device.application.validation;

import com.tenghe.corebackend.device.application.exception.BusinessException;

import java.util.List;
import java.util.regex.Pattern;

public final class ValidationUtils {
  private static final Pattern MODEL_IDENTIFIER_PATTERN = Pattern.compile("^[A-Za-z0-9]{2,8}$");
  private static final Pattern DEVICE_KEY_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,30}$");
  private static final Pattern PRODUCT_SECRET_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,30}$");

  private ValidationUtils() {
  }

  public static void requireNonBlank(String value, String message) {
    if (value == null || value.trim().isEmpty()) {
      throw new BusinessException(message);
    }
  }

  public static void requireMaxLength(String value, int maxLength, String message) {
    if (value != null && value.length() > maxLength) {
      throw new BusinessException(message);
    }
  }

  public static void requireMatch(String value, Pattern pattern, String message) {
    if (value == null || !pattern.matcher(value).matches()) {
      throw new BusinessException(message);
    }
  }

  public static void requireListNotEmpty(List<?> list, String message) {
    if (list == null || list.isEmpty()) {
      throw new BusinessException(message);
    }
  }

  public static void requireNotNull(Object value, String message) {
    if (value == null) {
      throw new BusinessException(message);
    }
  }

  public static void requireTrue(boolean condition, String message) {
    if (!condition) {
      throw new BusinessException(message);
    }
  }

  public static void validateModelIdentifier(String identifier, String message) {
    requireMatch(identifier, MODEL_IDENTIFIER_PATTERN, message);
  }

  public static void validateDeviceKey(String deviceKey, String message) {
    requireMatch(deviceKey, DEVICE_KEY_PATTERN, message);
  }

  public static void validateProductSecret(String secret, String message) {
    if (secret != null && !secret.trim().isEmpty()) {
      requireMatch(secret, PRODUCT_SECRET_PATTERN, message);
    }
  }
}
