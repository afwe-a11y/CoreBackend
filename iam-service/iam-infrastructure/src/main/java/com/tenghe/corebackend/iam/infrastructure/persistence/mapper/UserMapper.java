package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {
  int insert(UserPO user);

  int update(UserPO user);

  UserPO findById(@Param("id") Long id);

  UserPO findByUsername(@Param("username") String username);

  UserPO findByEmail(@Param("email") String email);

  UserPO findByPhone(@Param("phone") String phone);

  List<UserPO> searchByKeyword(@Param("keyword") String keyword);

  List<UserPO> findByIds(@Param("ids") Set<Long> ids);

  int softDeleteById(@Param("id") Long id);
}
