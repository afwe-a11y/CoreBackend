package com.tenghe.corebackend.iam.infrastructure.persistence.repository;

import com.tenghe.corebackend.iam.infrastructure.persistence.json.JsonCodec;
import com.tenghe.corebackend.iam.infrastructure.persistence.mapper.UserMapper;
import com.tenghe.corebackend.iam.infrastructure.persistence.po.UserPo;
import com.tenghe.corebackend.iam.interfaces.UserRepositoryPort;
import com.tenghe.corebackend.iam.model.User;
import com.tenghe.corebackend.iam.model.enums.AccountTypeEnum;
import com.tenghe.corebackend.iam.model.enums.UserStatusEnum;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class MyBatisUserRepository implements UserRepositoryPort {
  private static final String KEY_INITIAL_PASSWORD = "initialPasswordFlag";
  private static final String KEY_FAILED_ATTEMPTS = "failedLoginAttempts";
  private static final String KEY_LOCKED_UNTIL = "lockedUntil";
  private static final String KEY_ACCOUNT_TYPE = "accountType";

  private final UserMapper userMapper;
  private final JsonCodec jsonCodec;

  public MyBatisUserRepository(UserMapper userMapper, JsonCodec jsonCodec) {
    this.userMapper = userMapper;
    this.jsonCodec = jsonCodec;
  }

  @Override
  public User save(User user) {
    UserPo po = toPo(user, null);
    userMapper.insert(po);
    return user;
  }

  @Override
  public User update(User user) {
    UserPo existing = userMapper.findById(user.getId());
    String existingAttributes = existing == null ? null : existing.getAttributes();
    UserPo po = toPo(user, existingAttributes);
    userMapper.update(po);
    return user;
  }

  @Override
  public User findById(Long id) {
    return toModel(userMapper.findById(id));
  }

  @Override
  public User findByUsername(String username) {
    return toModel(userMapper.findByUsername(username));
  }

  @Override
  public User findByEmail(String email) {
    return toModel(userMapper.findByEmail(email));
  }

  @Override
  public User findByPhone(String phone) {
    return toModel(userMapper.findByPhone(phone));
  }

  @Override
  public List<User> searchByKeyword(String keyword) {
    return userMapper.searchByKeyword(keyword).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<User> findByIds(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return userMapper.findByIds(ids).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public void softDeleteById(Long userId) {
    userMapper.softDeleteById(userId);
  }

  private User toModel(UserPo po) {
    if (po == null) {
      return null;
    }
    User user = new User();
    user.setId(po.getId());
    user.setUsername(po.getUsername());
    user.setName(po.getNickname());
    user.setEmail(po.getEmail());
    user.setPhone(po.getPhone());
    user.setPassword(po.getPasswordHash());
    user.setPrimaryOrgId(po.getHomeOrgId());
    user.setCreatedAt(po.getCreateTime());
    user.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
    UserStatusEnum status = UserStatusEnum.fromValue(po.getStatus());
    if (status == null && po.getStatus() != null && "LOCKED".equalsIgnoreCase(po.getStatus())) {
      status = UserStatusEnum.DISABLED;
    }
    user.setStatus(status);
    applyAttributes(user, po.getAttributes());
    return user;
  }

  private UserPo toPo(User user, String existingAttributes) {
    UserPo po = new UserPo();
    po.setId(user.getId());
    po.setHomeOrgId(user.getPrimaryOrgId());
    po.setUsername(user.getUsername());
    po.setEmail(user.getEmail());
    po.setPhone(user.getPhone());
    po.setPasswordHash(user.getPassword());
    po.setNickname(user.getName());
    po.setStatus(user.getStatus() == null ? null : user.getStatus().name());
    po.setCreateTime(user.getCreatedAt());
    po.setDeleted(user.isDeleted() ? 1 : 0);
    po.setAttributes(buildAttributes(user, existingAttributes));
    return po;
  }

  private void applyAttributes(User user, String attributesJson) {
    Map<String, Object> attributes = jsonCodec.readMap(attributesJson);
    user.setInitialPasswordFlag(booleanValue(attributes.get(KEY_INITIAL_PASSWORD)));
    Integer attempts = intValue(attributes.get(KEY_FAILED_ATTEMPTS));
    user.setFailedLoginAttempts(attempts == null ? 0 : attempts);
    Long lockedUntil = longValue(attributes.get(KEY_LOCKED_UNTIL));
    user.setLockedUntil(lockedUntil == null ? null : Instant.ofEpochMilli(lockedUntil));
    String accountType = stringValue(attributes.get(KEY_ACCOUNT_TYPE));
    user.setAccountType(AccountTypeEnum.fromValue(accountType));
  }

  private String buildAttributes(User user, String existingAttributes) {
    Map<String, Object> attributes = new LinkedHashMap<>(jsonCodec.readMap(existingAttributes));
    attributes.put(KEY_INITIAL_PASSWORD, user.isInitialPasswordFlag());
    attributes.put(KEY_FAILED_ATTEMPTS, user.getFailedLoginAttempts());
    if (user.getLockedUntil() != null) {
      attributes.put(KEY_LOCKED_UNTIL, user.getLockedUntil().toEpochMilli());
    } else {
      attributes.remove(KEY_LOCKED_UNTIL);
    }
    if (user.getAccountType() != null) {
      attributes.put(KEY_ACCOUNT_TYPE, user.getAccountType().name());
    } else {
      attributes.remove(KEY_ACCOUNT_TYPE);
    }
    if (attributes.isEmpty()) {
      return null;
    }
    return jsonCodec.writeValue(attributes);
  }

  private Boolean booleanValue(Object value) {
    if (value instanceof Boolean booleanValue) {
      return booleanValue;
    }
    if (value == null) {
      return false;
    }
    return Boolean.parseBoolean(value.toString());
  }

  private Integer intValue(Object value) {
    if (value instanceof Integer intValue) {
      return intValue;
    }
    if (value instanceof Number number) {
      return number.intValue();
    }
    if (value == null) {
      return null;
    }
    try {
      return Integer.parseInt(value.toString());
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  private Long longValue(Object value) {
    if (value instanceof Long longValue) {
      return longValue;
    }
    if (value instanceof Number number) {
      return number.longValue();
    }
    if (value == null) {
      return null;
    }
    try {
      return Long.parseLong(value.toString());
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  private String stringValue(Object value) {
    return value == null ? null : value.toString();
  }
}
