package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.UserPo;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(UserPo user);

    int update(UserPo user);

    UserPo findById(@Param("id") Long id);

    UserPo findByUsername(@Param("username") String username);

    UserPo findByEmail(@Param("email") String email);

    UserPo findByPhone(@Param("phone") String phone);

    List<UserPo> searchByKeyword(@Param("keyword") String keyword);

    List<UserPo> findByIds(@Param("ids") Set<Long> ids);

    int softDeleteById(@Param("id") Long id);
}
