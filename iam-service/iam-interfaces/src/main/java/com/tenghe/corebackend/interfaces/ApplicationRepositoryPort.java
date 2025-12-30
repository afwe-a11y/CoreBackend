package com.tenghe.corebackend.interfaces;

import com.tenghe.corebackend.model.Application;
import java.util.List;
import java.util.Set;

public interface ApplicationRepositoryPort {
    Application save(Application application);

    Application update(Application application);

    Application findById(Long id);

    Application findByCode(String appCode);

    List<Application> listAll();

    List<Application> findByIds(Set<Long> ids);

    void softDeleteById(Long id);
}
