package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.UserRepositoryPort;
import com.tenghe.corebackend.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepositoryPort {
    private final Map<Long, User> store = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(Long id) {
        return store.get(id);
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (User user : store.values()) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> searchByKeyword(String keyword) {
        List<User> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            for (User user : store.values()) {
                if (!user.isDeleted()) {
                    results.add(user);
                }
            }
            return results;
        }
        String value = keyword.trim();
        for (User user : store.values()) {
            if (user.isDeleted()) {
                continue;
            }
            if (contains(user.getUsername(), value) || contains(user.getEmail(), value) || contains(user.getPhone(), value)) {
                results.add(user);
            }
        }
        return results;
    }

    @Override
    public List<User> findByIds(Set<Long> ids) {
        List<User> results = new ArrayList<>();
        if (ids == null) {
            return results;
        }
        for (Long id : ids) {
            User user = store.get(id);
            if (user != null && !user.isDeleted()) {
                results.add(user);
            }
        }
        return results;
    }

    @Override
    public void softDeleteById(Long userId) {
        User user = store.get(userId);
        if (user != null) {
            user.setDeleted(true);
        }
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.contains(keyword);
    }
}
