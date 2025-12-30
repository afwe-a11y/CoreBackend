package com.tenghe.corebackend.device.application.service;

import com.tenghe.corebackend.device.application.command.CreateDeviceModelCommand;
import com.tenghe.corebackend.device.application.command.DeviceModelPointCommand;
import com.tenghe.corebackend.device.application.command.ImportDeviceModelPointsCommand;
import com.tenghe.corebackend.device.application.command.UpdateDeviceModelPointsCommand;
import com.tenghe.corebackend.device.application.exception.BusinessException;
import com.tenghe.corebackend.device.application.service.result.DeviceModelDetailResult;
import com.tenghe.corebackend.device.application.service.result.DeviceModelListItemResult;
import com.tenghe.corebackend.device.application.service.result.DeviceModelPointResult;
import com.tenghe.corebackend.device.application.service.result.PageResult;
import com.tenghe.corebackend.device.application.validation.ValidationUtils;
import com.tenghe.corebackend.device.interfaces.DeviceModelRepositoryPort;
import com.tenghe.corebackend.device.interfaces.DeviceRepositoryPort;
import com.tenghe.corebackend.device.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.device.interfaces.ProductRepositoryPort;
import com.tenghe.corebackend.device.interfaces.TransactionManagerPort;
import com.tenghe.corebackend.device.model.DeviceDataType;
import com.tenghe.corebackend.device.model.DeviceModel;
import com.tenghe.corebackend.device.model.DeviceModelPoint;
import com.tenghe.corebackend.device.model.DeviceModelSource;
import com.tenghe.corebackend.device.model.DevicePointType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class DeviceModelApplicationService {
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final DeviceModelRepositoryPort deviceModelRepository;
    private final ProductRepositoryPort productRepository;
    private final DeviceRepositoryPort deviceRepository;
    private final IdGeneratorPort idGenerator;
    private final TransactionManagerPort transactionManager;

    public DeviceModelApplicationService(
            DeviceModelRepositoryPort deviceModelRepository,
            ProductRepositoryPort productRepository,
            DeviceRepositoryPort deviceRepository,
            IdGeneratorPort idGenerator,
            TransactionManagerPort transactionManager) {
        this.deviceModelRepository = deviceModelRepository;
        this.productRepository = productRepository;
        this.deviceRepository = deviceRepository;
        this.idGenerator = idGenerator;
        this.transactionManager = transactionManager;
    }

    public PageResult<DeviceModelListItemResult> listModels(Integer page, Integer size) {
        List<DeviceModel> models = deviceModelRepository.listAll();
        models.sort(Comparator.comparing(DeviceModel::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        long total = models.size();
        List<DeviceModel> paged = paginate(models, normalizePage(page), normalizeSize(size));
        List<DeviceModelListItemResult> results = new ArrayList<>();
        for (DeviceModel model : paged) {
            DeviceModelListItemResult item = new DeviceModelListItemResult();
            item.setId(model.getId());
            item.setIdentifier(model.getIdentifier());
            item.setName(model.getName());
            item.setSource(model.getSource() == null ? null : model.getSource().name());
            item.setParentModelId(model.getParentModelId());
            item.setPointCount(model.getPoints() == null ? 0 : model.getPoints().size());
            results.add(item);
        }
        return new PageResult<>(results, total, normalizePage(page), normalizeSize(size));
    }

    public DeviceModelDetailResult getModel(Long modelId) {
        DeviceModel model = requireModel(modelId);
        return toDetail(model);
    }

    public DeviceModelDetailResult createModel(CreateDeviceModelCommand command) {
        ValidationUtils.requireNonBlank(command.getIdentifier(), "Model identifier is required");
        ValidationUtils.validateModelIdentifier(command.getIdentifier(), "Model identifier must be 2-8 letters or digits");
        ValidationUtils.requireNonBlank(command.getName(), "Model name is required");
        ValidationUtils.requireMaxLength(command.getName(), 50, "Model name too long");

        if (deviceModelRepository.findByIdentifier(command.getIdentifier()) != null) {
            throw new BusinessException("Model identifier already exists");
        }

        DeviceModelSource source = DeviceModelSource.fromValue(command.getSource());
        if (source == null) {
            throw new BusinessException("Model source is required");
        }

        DeviceModel parent = null;
        if (source == DeviceModelSource.INHERIT) {
            if (command.getParentModelId() == null) {
                throw new BusinessException("Parent model is required for inheritance");
            }
            parent = deviceModelRepository.findById(command.getParentModelId());
            if (parent == null || parent.isDeleted()) {
                throw new BusinessException("Parent model not found");
            }
            if (parent.getParentModelId() != null) {
                throw new BusinessException("Inheritance depth exceeds limit");
            }
        }

        List<DeviceModelPoint> points = toPoints(command.getPoints());
        validatePoints(points, parent == null ? null : parent.getPoints());

        DeviceModel model = new DeviceModel();
        model.setId(idGenerator.nextId());
        model.setIdentifier(command.getIdentifier());
        model.setName(command.getName());
        model.setSource(source);
        model.setParentModelId(source == DeviceModelSource.INHERIT ? command.getParentModelId() : null);
        model.setPoints(points);
        model.setCreatedAt(Instant.now());
        model.setDeleted(false);
        deviceModelRepository.save(model);
        return toDetail(model);
    }

    public void deleteModel(Long modelId) {
        DeviceModel model = requireModel(modelId);
        if (!productRepository.listByDeviceModelId(modelId).isEmpty()) {
            throw new BusinessException("Model is bound to products");
        }
        if (!deviceModelRepository.listByParentId(modelId).isEmpty()) {
            throw new BusinessException("Model is referenced by child models");
        }
        if (isModelUsedByDevice(modelId)) {
            throw new BusinessException("Model is used by devices");
        }
        model.setDeleted(true);
        deviceModelRepository.update(model);
    }

    public DeviceModelDetailResult updatePoints(UpdateDeviceModelPointsCommand command) {
        DeviceModel model = requireModel(command.getModelId());
        List<DeviceModelPoint> parentPoints = resolveParentPoints(model);
        Map<String, DeviceModelPoint> existing = indexPoints(model.getPoints());
        List<DeviceModelPoint> incoming = toPoints(command.getPoints());
        validatePoints(incoming, parentPoints);
        for (DeviceModelPoint point : incoming) {
            if (containsIdentifier(parentPoints, point.getIdentifier())) {
                throw new BusinessException("Cannot modify parent model point: " + point.getIdentifier());
            }
            DeviceModelPoint current = existing.get(point.getIdentifier());
            if (current != null && current.getDataType() != point.getDataType()) {
                throw new BusinessException("Point data type mismatch: " + point.getIdentifier());
            }
            existing.put(point.getIdentifier(), point);
        }
        model.setPoints(new ArrayList<>(existing.values()));
        deviceModelRepository.update(model);
        return toDetail(model);
    }

    public DeviceModelDetailResult importPoints(ImportDeviceModelPointsCommand command) {
        DeviceModel model = requireModel(command.getModelId());
        List<DeviceModelPoint> parentPoints = resolveParentPoints(model);
        List<DeviceModelPoint> incoming = toPoints(command.getPoints());
        validatePoints(incoming, parentPoints);
        Map<String, DeviceModelPoint> merged = indexPoints(model.getPoints());
        for (DeviceModelPoint point : incoming) {
            if (containsIdentifier(parentPoints, point.getIdentifier())) {
                throw new BusinessException("Cannot modify parent model point: " + point.getIdentifier());
            }
            DeviceModelPoint current = merged.get(point.getIdentifier());
            if (current != null && current.getDataType() != point.getDataType()) {
                throw new BusinessException("Point data type mismatch: " + point.getIdentifier());
            }
            merged.put(point.getIdentifier(), point);
        }
        transactionManager.doInTransaction(() -> {
            model.setPoints(new ArrayList<>(merged.values()));
            deviceModelRepository.update(model);
        });
        return toDetail(model);
    }

    private DeviceModel requireModel(Long modelId) {
        if (modelId == null) {
            throw new BusinessException("Model not found");
        }
        DeviceModel model = deviceModelRepository.findById(modelId);
        if (model == null || model.isDeleted()) {
            throw new BusinessException("Model not found");
        }
        return model;
    }

    private List<DeviceModelPoint> resolveParentPoints(DeviceModel model) {
        if (model.getParentModelId() == null) {
            return null;
        }
        DeviceModel parent = deviceModelRepository.findById(model.getParentModelId());
        if (parent == null || parent.isDeleted()) {
            return null;
        }
        return parent.getPoints();
    }

    private List<DeviceModelPoint> toPoints(List<DeviceModelPointCommand> commands) {
        if (commands == null) {
            return new ArrayList<>();
        }
        List<DeviceModelPoint> points = new ArrayList<>();
        for (DeviceModelPointCommand command : commands) {
            DeviceModelPoint point = new DeviceModelPoint();
            point.setIdentifier(command.getIdentifier());
            point.setType(DevicePointType.fromValue(command.getType()));
            point.setDataType(DeviceDataType.fromValue(command.getDataType()));
            point.setEnumItems(command.getEnumItems());
            points.add(point);
        }
        return points;
    }

    private void validatePoints(List<DeviceModelPoint> points, List<DeviceModelPoint> parentPoints) {
        Set<String> identifiers = new HashSet<>();
        if (parentPoints != null) {
            for (DeviceModelPoint parentPoint : parentPoints) {
                if (parentPoint != null && parentPoint.getIdentifier() != null) {
                    identifiers.add(parentPoint.getIdentifier());
                }
            }
        }
        for (DeviceModelPoint point : points) {
            ValidationUtils.requireNonBlank(point.getIdentifier(), "Point identifier is required");
            if (identifiers.contains(point.getIdentifier())) {
                throw new BusinessException("Point identifier already exists: " + point.getIdentifier());
            }
            if (!identifiers.add(point.getIdentifier())) {
                throw new BusinessException("Duplicate point identifier: " + point.getIdentifier());
            }
            if (point.getType() == null) {
                throw new BusinessException("Point type is required");
            }
            if (point.getDataType() == null) {
                throw new BusinessException("Point data type is required");
            }
            if (point.getDataType() == DeviceDataType.ENUM) {
                ValidationUtils.requireListNotEmpty(point.getEnumItems(), "Enum point items are required");
            }
        }
    }

    private boolean containsIdentifier(List<DeviceModelPoint> points, String identifier) {
        if (points == null) {
            return false;
        }
        for (DeviceModelPoint point : points) {
            if (point != null && Objects.equals(point.getIdentifier(), identifier)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, DeviceModelPoint> indexPoints(List<DeviceModelPoint> points) {
        Map<String, DeviceModelPoint> indexed = new HashMap<>();
        if (points == null) {
            return indexed;
        }
        for (DeviceModelPoint point : points) {
            if (point != null && point.getIdentifier() != null) {
                indexed.put(point.getIdentifier(), point);
            }
        }
        return indexed;
    }

    private DeviceModelDetailResult toDetail(DeviceModel model) {
        DeviceModelDetailResult detail = new DeviceModelDetailResult();
        detail.setId(model.getId());
        detail.setIdentifier(model.getIdentifier());
        detail.setName(model.getName());
        detail.setSource(model.getSource() == null ? null : model.getSource().name());
        detail.setParentModelId(model.getParentModelId());
        List<DeviceModelPointResult> pointResults = new ArrayList<>();
        if (model.getPoints() != null) {
            for (DeviceModelPoint point : model.getPoints()) {
                DeviceModelPointResult result = new DeviceModelPointResult();
                result.setIdentifier(point.getIdentifier());
                result.setType(point.getType() == null ? null : point.getType().name());
                result.setDataType(point.getDataType() == null ? null : point.getDataType().name());
                result.setEnumItems(point.getEnumItems());
                pointResults.add(result);
            }
        }
        detail.setPoints(pointResults);
        return detail;
    }

    private boolean isModelUsedByDevice(Long modelId) {
        return deviceRepository.listAll().stream()
                .map(device -> productRepository.findById(device.getProductId()))
                .anyMatch(product -> product != null && !product.isDeleted()
                        && Objects.equals(product.getDeviceModelId(), modelId));
    }

    private <T> List<T> paginate(List<T> items, int page, int size) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        int fromIndex = Math.max(0, (page - 1) * size);
        if (fromIndex >= items.size()) {
            return new ArrayList<>();
        }
        int toIndex = Math.min(fromIndex + size, items.size());
        return items.subList(fromIndex, toIndex);
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
