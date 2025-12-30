package com.tenghe.corebackend.interfaces;

import com.tenghe.corebackend.model.User;
import java.util.List;
import java.util.Set;

public interface UserRepositoryPort {
    User save(User user);

    User update(User user);

    User findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> searchByKeyword(String keyword);

    List<User> findByIds(Set<Long> ids);

    void softDeleteById(Long userId);
}
