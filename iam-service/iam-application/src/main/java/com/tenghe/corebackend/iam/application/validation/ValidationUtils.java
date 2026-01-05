package com.tenghe.corebackend.iam.application.validation;

import com.tenghe.corebackend.iam.application.exception.BusinessException;

import java.util.List;
import java.util.regex.Pattern;

public final class ValidationUtils {
  private static final Pattern ORG_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,20}$");
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
  private static final Pattern ADMIN_DISPLAY_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,20}$");

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

  public static void requirePhoneFormat(String value, String message) {
    if (value != null && !value.trim().isEmpty() && !PHONE_PATTERN.matcher(value).matches()) {
      throw new BusinessException(message);
    }
  }

  public static void requireEmailFormat(String value, String message) {
    if (value != null && !value.trim().isEmpty() && !EMAIL_PATTERN.matcher(value).matches()) {
      throw new BusinessException(message);
    }
  }

  public static void requireAtLeastOne(String value1, String value2, String message) {
    boolean blank1 = value1 == null || value1.trim().isEmpty();
    boolean blank2 = value2 == null || value2.trim().isEmpty();
    if (blank1 && blank2) {
      throw new BusinessException(message);
    }
  }

  public static void requireListNotEmpty(List<?> list, String message) {
    if (list == null || list.isEmpty()) {
      throw new BusinessException(message);
    }
  }

  public static void validateOrgCode(String code, String message) {
    requireMatch(code, ORG_CODE_PATTERN, message);
  }

  public static void validateUsername(String username, String message) {
    requireMatch(username, USERNAME_PATTERN, message);
  }

  public static void validateAdminDisplay(String display, String message) {
    if (display != null && !ADMIN_DISPLAY_PATTERN.matcher(display).matches()) {
      throw new BusinessException(message);
    }
  }
}
