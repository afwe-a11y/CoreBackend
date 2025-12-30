package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.ApplicationRepositoryPort;
import com.tenghe.corebackend.model.Application;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryApplicationRepository implements ApplicationRepositoryPort {
    private final Map<Long, Application> store = new ConcurrentHashMap<>();

    @Override
    public Application save(Application application) {
        store.put(application.getId(), application);
        return application;
    }

    @Override
    public Application update(Application application) {
        store.put(application.getId(), application);
        return application;
    }

    @Override
    public Application findById(Long id) {
        if (id == null) {
            return null;
        }
        Application app = store.get(id);
        if (app != null && app.isDeleted()) {
            return null;
        }
        return app;
    }

    @Override
    public Application findByCode(String appCode) {
        if (appCode == null) {
            return null;
        }
        for (Application app : store.values()) {
            if (!app.isDeleted() && appCode.equals(app.getAppCode())) {
                return app;
            }
        }
        return null;
    }

    @Override
    public List<Application> listAll() {
        List<Application> results = new ArrayList<>();
        for (Application app : store.values()) {
            if (!app.isDeleted()) {
                results.add(app);
            }
        }
        return results;
    }

    @Override
    public List<Application> findByIds(Set<Long> ids) {
        List<Application> results = new ArrayList<>();
        if (ids == null) {
            return results;
        }
        for (Long id : ids) {
            Application app = store.get(id);
            if (app != null && !app.isDeleted()) {
                results.add(app);
            }
        }
        return results;
    }

    @Override
    public void softDeleteById(Long id) {
        Application app = store.get(id);
        if (app != null) {
            app.setDeleted(true);
        }
    }
}
