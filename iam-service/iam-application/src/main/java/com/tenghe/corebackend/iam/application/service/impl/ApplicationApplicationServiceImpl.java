package com.tenghe.corebackend.iam.application.service.impl;

import com.tenghe.corebackend.iam.application.ApplicationApplicationService;

import com.tenghe.corebackend.iam.application.command.CreateApplicationCommand;
import com.tenghe.corebackend.iam.application.command.UpdateApplicationCommand;
import com.tenghe.corebackend.iam.application.exception.BusinessException;
import com.tenghe.corebackend.iam.application.service.result.ApplicationDetailResult;
import com.tenghe.corebackend.iam.application.service.result.ApplicationListItemResult;
import com.tenghe.corebackend.iam.application.service.result.PageResult;
import com.tenghe.corebackend.iam.application.validation.ValidationUtils;
import com.tenghe.corebackend.iam.interfaces.ApplicationPermissionRepositoryPort;
import com.tenghe.corebackend.iam.interfaces.ApplicationRepositoryPort;
import com.tenghe.corebackend.iam.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.iam.interfaces.RoleRepositoryPort;
import com.tenghe.corebackend.iam.interfaces.RolePermissionRepositoryPort;
import com.tenghe.corebackend.iam.interfaces.TransactionManagerPort;
import com.tenghe.corebackend.iam.model.Application;
import com.tenghe.corebackend.iam.model.enums.ApplicationStatusEnum;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ApplicationApplicationServiceImpl implements ApplicationApplicationService {
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ApplicationRepositoryPort applicationRepository;
    private final ApplicationPermissionRepositoryPort applicationPermissionRepository;
    private final RoleRepositoryPort roleRepository;
    private final RolePermissionRepositoryPort rolePermissionRepository;
    private final IdGeneratorPort idGenerator;
    private final TransactionManagerPort transactionManager;

    public ApplicationApplicationServiceImpl(
            ApplicationRepositoryPort applicationRepository,
            ApplicationPermissionRepositoryPort applicationPermissionRepository,
            RoleRepositoryPort roleRepository,
            RolePermissionRepositoryPort rolePermissionRepository,
            IdGeneratorPort idGenerator,
            TransactionManagerPort transactionManager) {
        this.applicationRepository = applicationRepository;
        this.applicationPermissionRepository = applicationPermissionRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.idGenerator = idGenerator;
        this.transactionManager = transactionManager;
    }

    @Override
    public PageResult<ApplicationListItemResult> listApplications(String keyword, Integer page, Integer size) {
        int pageNumber = normalizePage(page);
        int pageSize = normalizeSize(size);
        List<Application> applications = new ArrayList<>(applicationRepository.listAll());
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordValue = keyword.trim();
            applications = applications.stream()
                    .filter(app -> matchesKeyword(app, keywordValue))
                    .toList();
        }
        applications.sort(Comparator.comparing(Application::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        long total = applications.size();
        List<Application> paged = paginate(applications, pageNumber, pageSize);
        List<ApplicationListItemResult> items = paged.stream()
                .map(this::toListItem)
                .toList();
        return new PageResult<>(items, total, pageNumber, pageSize);
    }

    @Override
    public Long createApplication(CreateApplicationCommand command) {
        ValidationUtils.requireNonBlank(command.getAppName(), "应用名称不能为空");
        ValidationUtils.requireNonBlank(command.getAppCode(), "应用编码不能为空");
        ValidationUtils.requireListNotEmpty(command.getPermissionIds(), "包含权限不能为空");

        if (applicationRepository.findByCode(command.getAppCode()) != null) {
            throw new BusinessException("应用编码已被占用");
        }

        Long id = idGenerator.nextId();
        Application application = new Application();
        application.setId(id);
        application.setAppName(command.getAppName());
        application.setAppCode(command.getAppCode());
        application.setDescription(command.getDescription());
        application.setStatus(ApplicationStatusEnum.ENABLED);
        application.setCreatedAt(Instant.now());
        application.setDeleted(false);

        transactionManager.doInTransaction(() -> {
            applicationRepository.save(application);
            Set<Long> permissionIds = new HashSet<>(command.getPermissionIds());
            applicationPermissionRepository.replaceAppPermissions(id, permissionIds);
        });
        return id;
    }

    @Override
    public ApplicationDetailResult getApplicationDetail(Long appId) {
        Application application = requireApplication(appId);
        Set<Long> permissionIds = applicationPermissionRepository.findPermissionIdsByAppId(appId);
        ApplicationDetailResult result = new ApplicationDetailResult();
        result.setId(application.getId());
        result.setAppName(application.getAppName());
        result.setAppCode(application.getAppCode());
        result.setDescription(application.getDescription());
        result.setStatus(application.getStatus() == null ? null : application.getStatus().name());
        result.setCreatedAt(application.getCreatedAt());
        result.setPermissionIds(new ArrayList<>(permissionIds));
        return result;
    }

    @Override
    public void updateApplication(UpdateApplicationCommand command) {
        Application application = requireApplication(command.getAppId());
        ValidationUtils.requireNonBlank(command.getAppName(), "应用名称不能为空");

        ApplicationStatusEnum status = ApplicationStatusEnum.fromValue(command.getStatus());
        if (status == null) {
            throw new BusinessException("应用状态不能为空");
        }

        if (command.getPermissionIds() != null) {
            Set<Long> existingPermissions = applicationPermissionRepository.findPermissionIdsByAppId(command.getAppId());
            Set<Long> newPermissions = new HashSet<>(command.getPermissionIds());
            Set<Long> removedPermissions = existingPermissions.stream()
                    .filter(p -> !newPermissions.contains(p))
                    .collect(Collectors.toSet());

            if (!removedPermissions.isEmpty()) {
                for (Long permissionId : removedPermissions) {
                    if (rolePermissionRepository.existsByPermissionIdAndAppId(permissionId, command.getAppId())) {
                        throw new BusinessException("无法移除已分配给角色的权限");
                    }
                }
            }
        }

        transactionManager.doInTransaction(() -> {
            application.setAppName(command.getAppName());
            application.setDescription(command.getDescription());
            application.setStatus(status);
            applicationRepository.update(application);

            if (command.getPermissionIds() != null) {
                Set<Long> permissionIds = new HashSet<>(command.getPermissionIds());
                applicationPermissionRepository.replaceAppPermissions(command.getAppId(), permissionIds);
            }
        });
    }

    @Override
    public void deleteApplication(Long appId) {
        Application application = requireApplication(appId);
        if (roleRepository.countByAppId(appId) > 0) {
            throw new BusinessException("应用下存在角色，无法删除");
        }
        transactionManager.doInTransaction(() -> {
            application.setDeleted(true);
            applicationRepository.update(application);
            applicationPermissionRepository.softDeleteByAppId(appId);
        });
    }

    private Application requireApplication(Long appId) {
        if (appId == null) {
            throw new BusinessException("应用不存在");
        }
        Application application = applicationRepository.findById(appId);
        if (application == null) {
            throw new BusinessException("应用不存在");
        }
        return application;
    }

    private boolean matchesKeyword(Application application, String keyword) {
        if (application.getAppName() != null && application.getAppName().contains(keyword)) {
            return true;
        }
        if (application.getAppCode() != null && application.getAppCode().contains(keyword)) {
            return true;
        }
        return false;
    }

    private ApplicationListItemResult toListItem(Application application) {
        ApplicationListItemResult item = new ApplicationListItemResult();
        item.setId(application.getId());
        item.setAppName(application.getAppName());
        item.setAppCode(application.getAppCode());
        item.setDescription(application.getDescription());
        item.setStatus(application.getStatus() == null ? null : application.getStatus().name());
        item.setCreatedAt(application.getCreatedAt());
        return item;
    }

    private List<Application> paginate(List<Application> applications, int page, int size) {
        if (applications.isEmpty()) {
            return Collections.emptyList();
        }
        int fromIndex = Math.max(0, (page - 1) * size);
        if (fromIndex >= applications.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + size, applications.size());
        return applications.subList(fromIndex, toIndex);
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return size;
    }
}
