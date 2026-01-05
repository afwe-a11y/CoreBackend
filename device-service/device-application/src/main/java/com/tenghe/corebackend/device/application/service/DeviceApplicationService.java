package com.tenghe.corebackend.device.application.service;

import com.tenghe.corebackend.device.application.command.CreateDeviceCommand;
import com.tenghe.corebackend.device.application.command.DeviceImportRowCommand;
import com.tenghe.corebackend.device.application.command.UpdateDeviceCommand;
import com.tenghe.corebackend.device.application.exception.BusinessException;
import com.tenghe.corebackend.device.application.service.result.*;
import com.tenghe.corebackend.device.application.validation.ValidationUtils;
import com.tenghe.corebackend.device.interfaces.*;
import com.tenghe.corebackend.device.model.*;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceApplicationService {
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final String SECRET_PREFIX = "DS";
  private static final char[] SECRET_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
  private static final DateTimeFormatter EXPORT_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());

  private final DeviceRepositoryPort deviceRepository;
  private final ProductRepositoryPort productRepository;
  private final GatewayRepositoryPort gatewayRepository;
  private final DeviceModelRepositoryPort deviceModelRepository;
  private final TelemetryRepositoryPort telemetryRepository;
  private final IdGeneratorPort idGenerator;
  private final TransactionManagerPort transactionManager;
  private final SecureRandom secureRandom = new SecureRandom();

  public DeviceApplicationService(
      DeviceRepositoryPort deviceRepository,
      ProductRepositoryPort productRepository,
      GatewayRepositoryPort gatewayRepository,
      DeviceModelRepositoryPort deviceModelRepository,
      TelemetryRepositoryPort telemetryRepository,
      IdGeneratorPort idGenerator,
      TransactionManagerPort transactionManager) {
    this.deviceRepository = deviceRepository;
    this.productRepository = productRepository;
    this.gatewayRepository = gatewayRepository;
    this.deviceModelRepository = deviceModelRepository;
    this.telemetryRepository = telemetryRepository;
    this.idGenerator = idGenerator;
    this.transactionManager = transactionManager;
  }

  public PageResult<DeviceListItemResult> listDevices(Long productId, String keyword, Integer page, Integer size) {
    List<Device> devices = deviceRepository.search(keyword, productId);
    devices.sort(Comparator.comparing(Device::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .reversed());
    long total = devices.size();
    List<Device> paged = paginate(devices, normalizePage(page), normalizeSize(size));
    List<DeviceListItemResult> items = new ArrayList<>();
    for (Device device : paged) {
      items.add(toListItem(device));
    }
    return new PageResult<>(items, total, normalizePage(page), normalizeSize(size));
  }

  public CreateDeviceResult createDevice(CreateDeviceCommand command) {
    ValidationUtils.requireNonBlank(command.getName(), "Device name is required");
    ValidationUtils.requireMaxLength(command.getName(), 50, "Device name too long");
    ValidationUtils.requireNotNull(command.getProductId(), "Product is required");
    ValidationUtils.requireNonBlank(command.getDeviceKey(), "Device key is required");
    ValidationUtils.requireNotNull(command.getGatewayId(), "Gateway is required");
    ValidationUtils.validateDeviceKey(command.getDeviceKey(), "Device key is invalid");

    Product product = requireProduct(command.getProductId());
    if (deviceRepository.findByDeviceKey(command.getDeviceKey()) != null) {
      throw new BusinessException("Device key already exists");
    }
    Gateway gateway = requireGateway(command.getGatewayId());
    validateDynamicAttributes(product, command.getDynamicAttributes());

    Device device = new Device();
    device.setId(idGenerator.nextId());
    device.setName(command.getName());
    device.setProductId(command.getProductId());
    device.setDeviceKey(command.getDeviceKey());
    device.setDeviceSecret(generateDeviceSecret());
    device.setGatewayId(command.getGatewayId());
    device.setStationId(gateway.getStationId());
    device.setStatus(DeviceStatus.OFFLINE);
    device.setDynamicAttributes(command.getDynamicAttributes());
    device.setCreatedAt(Instant.now());
    device.setDeleted(false);
    deviceRepository.save(device);

    CreateDeviceResult result = new CreateDeviceResult();
    result.setId(device.getId());
    result.setDeviceSecret(device.getDeviceSecret());
    return result;
  }

  public void updateDevice(UpdateDeviceCommand command) {
    Device device = requireDevice(command.getDeviceId());
    if (command.getName() != null) {
      ValidationUtils.requireMaxLength(command.getName(), 50, "Device name too long");
      device.setName(command.getName());
    }
    if (command.getGatewayId() != null) {
      Gateway gateway = requireGateway(command.getGatewayId());
      device.setGatewayId(gateway.getId());
      device.setStationId(gateway.getStationId());
    }
    if (command.getDynamicAttributes() != null) {
      Product product = requireProduct(device.getProductId());
      validateDynamicAttributes(product, command.getDynamicAttributes());
      device.setDynamicAttributes(command.getDynamicAttributes());
    }
    deviceRepository.update(device);
  }

  public DeviceImportPreviewResult previewImport(List<DeviceImportRowCommand> rows) {
    DeviceImportPreviewResult result = new DeviceImportPreviewResult();
    List<DeviceImportPreviewItemResult> items = new ArrayList<>();
    int createCount = 0;
    int updateCount = 0;
    int invalidCount = 0;
    if (rows != null) {
      int index = 1;
      for (DeviceImportRowCommand row : rows) {
        ImportDecision decision = validateImportRow(row);
        DeviceImportPreviewItemResult item = new DeviceImportPreviewItemResult();
        item.setRowIndex(index);
        item.setAction(decision.action);
        item.setMessage(decision.message);
        items.add(item);
        if ("CREATE".equals(decision.action)) {
          createCount++;
        } else if ("UPDATE".equals(decision.action)) {
          updateCount++;
        } else {
          invalidCount++;
        }
        index++;
      }
    }
    result.setTotal(rows == null ? 0 : rows.size());
    result.setCreateCount(createCount);
    result.setUpdateCount(updateCount);
    result.setInvalidCount(invalidCount);
    result.setItems(items);
    return result;
  }

  public DeviceImportCommitResult commitImport(List<DeviceImportRowCommand> rows) {
    DeviceImportCommitResult result = new DeviceImportCommitResult();
    List<DeviceImportFailureResult> failures = new ArrayList<>();
    int success = 0;
    int failure = 0;
    if (rows != null) {
      int index = 1;
      for (DeviceImportRowCommand row : rows) {
        ImportDecision decision = validateImportRow(row);
        if ("INVALID".equals(decision.action)) {
          DeviceImportFailureResult failureResult = new DeviceImportFailureResult();
          failureResult.setRowIndex(index);
          failureResult.setMessage(decision.message);
          failures.add(failureResult);
          failure++;
          index++;
          continue;
        }
        try {
          if ("CREATE".equals(decision.action)) {
            createFromImport(row);
          } else {
            updateFromImport(row);
          }
          success++;
        } catch (BusinessException ex) {
          DeviceImportFailureResult failureResult = new DeviceImportFailureResult();
          failureResult.setRowIndex(index);
          failureResult.setMessage(ex.getMessage());
          failures.add(failureResult);
          failure++;
        }
        index++;
      }
    }
    result.setSuccessCount(success);
    result.setFailureCount(failure);
    result.setFailures(failures);
    return result;
  }

  public DeviceExportResult exportDevices(Long productId, String keyword) {
    List<Device> devices = deviceRepository.search(keyword, productId);
    List<DeviceExportRowResult> rows = devices.stream()
        .map(device -> {
          DeviceExportRowResult row = new DeviceExportRowResult();
          row.setName(device.getName());
          row.setDeviceKey(device.getDeviceKey());
          row.setProductId(device.getProductId());
          row.setGatewayId(device.getGatewayId());
          row.setStationId(device.getStationId());
          return row;
        })
        .collect(Collectors.toList());

    String productName = "Devices";
    String description = "Export";
    if (productId != null) {
      Product product = requireProduct(productId);
      if (product.getName() != null && !product.getName().trim().isEmpty()) {
        productName = product.getName().trim();
      }
      if (product.getDescription() != null && !product.getDescription().trim().isEmpty()) {
        description = product.getDescription().trim();
      }
    }
    String fileName = String.format("%s_%s_%s.xlsx", productName, description, EXPORT_FORMATTER.format(Instant.now()));

    DeviceExportResult result = new DeviceExportResult();
    result.setFileName(fileName);
    result.setRows(rows);
    return result;
  }

  public List<DeviceTelemetryResult> listLatestTelemetry(Long deviceId) {
    Device device = requireDevice(deviceId);
    List<DeviceTelemetry> telemetry = telemetryRepository.listLatestByDeviceId(device.getId());
    telemetry.sort(Comparator.comparing(DeviceTelemetry::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .reversed());
    List<DeviceTelemetryResult> results = new ArrayList<>();
    for (DeviceTelemetry record : telemetry) {
      DeviceTelemetryResult result = new DeviceTelemetryResult();
      result.setPointIdentifier(record.getPointIdentifier());
      result.setValue(record.getValue());
      result.setUpdatedAt(record.getUpdatedAt());
      results.add(result);
    }
    return results;
  }

  private Device requireDevice(Long deviceId) {
    if (deviceId == null) {
      throw new BusinessException("Device not found");
    }
    Device device = deviceRepository.findById(deviceId);
    if (device == null || device.isDeleted()) {
      throw new BusinessException("Device not found");
    }
    return device;
  }

  private Product requireProduct(Long productId) {
    if (productId == null) {
      throw new BusinessException("Product not found");
    }
    Product product = productRepository.findById(productId);
    if (product == null || product.isDeleted()) {
      throw new BusinessException("Product not found");
    }
    return product;
  }

  private Gateway requireGateway(Long gatewayId) {
    if (gatewayId == null) {
      throw new BusinessException("Gateway not found");
    }
    Gateway gateway = gatewayRepository.findById(gatewayId);
    if (gateway == null || gateway.isDeleted()) {
      throw new BusinessException("Gateway not found");
    }
    return gateway;
  }

  private DeviceListItemResult toListItem(Device device) {
    DeviceListItemResult item = new DeviceListItemResult();
    item.setId(device.getId());
    item.setName(device.getName());
    item.setProductId(device.getProductId());
    item.setDeviceKey(device.getDeviceKey());
    item.setGatewayId(device.getGatewayId());
    item.setStationId(device.getStationId());
    item.setStatus(device.getStatus() == null ? null : device.getStatus().name());
    return item;
  }

  private void validateDynamicAttributes(Product product, Map<String, Object> attributes) {
    if (attributes == null || attributes.isEmpty()) {
      return;
    }
    Map<String, DeviceModelPoint> allowed = resolveAttributePoints(product);
    for (Map.Entry<String, Object> entry : attributes.entrySet()) {
      DeviceModelPoint point = allowed.get(entry.getKey());
      if (point == null) {
        throw new BusinessException("Unknown attribute: " + entry.getKey());
      }
      if (!isCompatible(point.getDataType(), entry.getValue(), point.getEnumItems())) {
        throw new BusinessException("Attribute value mismatch: " + entry.getKey());
      }
    }
  }

  private Map<String, DeviceModelPoint> resolveAttributePoints(Product product) {
    DeviceModel model = deviceModelRepository.findById(product.getDeviceModelId());
    if (model == null || model.isDeleted()) {
      throw new BusinessException("Device model not found");
    }
    List<DeviceModelPoint> points = new ArrayList<>();
    if (model.getParentModelId() != null) {
      DeviceModel parent = deviceModelRepository.findById(model.getParentModelId());
      if (parent != null && !parent.isDeleted() && parent.getPoints() != null) {
        points.addAll(parent.getPoints());
      }
    }
    if (model.getPoints() != null) {
      points.addAll(model.getPoints());
    }
    Map<String, DeviceModelPoint> allowed = new HashMap<>();
    for (DeviceModelPoint point : points) {
      if (point.getType() == DevicePointType.ATTRIBUTE && point.getIdentifier() != null) {
        allowed.put(point.getIdentifier(), point);
      }
    }
    return allowed;
  }

  private boolean isCompatible(DeviceDataType dataType, Object value, List<String> enumItems) {
    if (value == null) {
      return true;
    }
    if (dataType == null) {
      return false;
    }
    switch (dataType) {
      case INT:
        return isIntegerValue(value);
      case FLOAT:
      case DOUBLE:
        return isDecimalValue(value);
      case STRING:
        return value instanceof String;
      case ENUM:
        if (value instanceof String) {
          return enumItems != null && enumItems.contains(value);
        }
        return false;
      case BOOL:
        return isBooleanValue(value);
      case DATETIME:
        return isDateTimeValue(value);
      default:
        return false;
    }
  }

  private boolean isIntegerValue(Object value) {
    if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
      return true;
    }
    if (value instanceof Number) {
      double number = ((Number) value).doubleValue();
      return Math.floor(number) == number;
    }
    if (value instanceof String) {
      try {
        Integer.parseInt(((String) value).trim());
        return true;
      } catch (NumberFormatException ex) {
        return false;
      }
    }
    return false;
  }

  private boolean isDecimalValue(Object value) {
    if (value instanceof Number) {
      return true;
    }
    if (value instanceof String) {
      try {
        Double.parseDouble(((String) value).trim());
        return true;
      } catch (NumberFormatException ex) {
        return false;
      }
    }
    return false;
  }

  private boolean isBooleanValue(Object value) {
    if (value instanceof Boolean) {
      return true;
    }
    if (value instanceof String) {
      String normalized = ((String) value).trim().toLowerCase(Locale.ROOT);
      return "true".equals(normalized) || "false".equals(normalized);
    }
    return false;
  }

  private boolean isDateTimeValue(Object value) {
    if (value instanceof Instant) {
      return true;
    }
    if (value instanceof java.util.Date) {
      return true;
    }
    return value instanceof String;
  }

  private ImportDecision validateImportRow(DeviceImportRowCommand row) {
    if (row == null) {
      return new ImportDecision("INVALID", "Row is empty");
    }
    try {
      if (row.getId() == null) {
        validateCreateRow(row);
        return new ImportDecision("CREATE", "OK");
      }
      Device existing = deviceRepository.findById(row.getId());
      if (existing == null || existing.isDeleted()) {
        return new ImportDecision("INVALID", "Device not found");
      }
      validateUpdateRow(row, existing);
      return new ImportDecision("UPDATE", "OK");
    } catch (BusinessException ex) {
      return new ImportDecision("INVALID", ex.getMessage());
    }
  }

  private void validateCreateRow(DeviceImportRowCommand row) {
    ValidationUtils.requireNonBlank(row.getName(), "Device name is required");
    ValidationUtils.requireMaxLength(row.getName(), 50, "Device name too long");
    ValidationUtils.requireNotNull(row.getProductId(), "Product is required");
    ValidationUtils.requireNonBlank(row.getDeviceKey(), "Device key is required");
    ValidationUtils.requireNotNull(row.getGatewayId(), "Gateway is required");
    ValidationUtils.validateDeviceKey(row.getDeviceKey(), "Device key is invalid");

    Product product = requireProduct(row.getProductId());
    if (deviceRepository.findByDeviceKey(row.getDeviceKey()) != null) {
      throw new BusinessException("Device key already exists");
    }
    requireGateway(row.getGatewayId());
    validateDynamicAttributes(product, row.getDynamicAttributes());
  }

  private void validateUpdateRow(DeviceImportRowCommand row, Device existing) {
    if (row.getProductId() != null && !Objects.equals(existing.getProductId(), row.getProductId())) {
      throw new BusinessException("Product cannot be changed");
    }
    if (row.getDeviceKey() != null && !Objects.equals(existing.getDeviceKey(), row.getDeviceKey())) {
      throw new BusinessException("Device key cannot be changed");
    }
    if (row.getName() != null) {
      ValidationUtils.requireMaxLength(row.getName(), 50, "Device name too long");
    }
    if (row.getGatewayId() != null) {
      requireGateway(row.getGatewayId());
    }
    Product product = requireProduct(existing.getProductId());
    validateDynamicAttributes(product, row.getDynamicAttributes());
  }

  private void createFromImport(DeviceImportRowCommand row) {
    transactionManager.doInTransaction(() -> {
      Gateway gateway = requireGateway(row.getGatewayId());
      Device device = new Device();
      device.setId(idGenerator.nextId());
      device.setName(row.getName());
      device.setProductId(row.getProductId());
      device.setDeviceKey(row.getDeviceKey());
      device.setDeviceSecret(generateDeviceSecret());
      device.setGatewayId(row.getGatewayId());
      device.setStationId(gateway.getStationId());
      device.setStatus(DeviceStatus.OFFLINE);
      device.setDynamicAttributes(row.getDynamicAttributes());
      device.setCreatedAt(Instant.now());
      device.setDeleted(false);
      deviceRepository.save(device);
    });
  }

  private void updateFromImport(DeviceImportRowCommand row) {
    transactionManager.doInTransaction(() -> {
      Device device = requireDevice(row.getId());
      if (row.getName() != null) {
        device.setName(row.getName());
      }
      if (row.getGatewayId() != null) {
        Gateway gateway = requireGateway(row.getGatewayId());
        device.setGatewayId(gateway.getId());
        device.setStationId(gateway.getStationId());
      }
      if (row.getDynamicAttributes() != null) {
        device.setDynamicAttributes(row.getDynamicAttributes());
      }
      deviceRepository.update(device);
    });
  }

  private String generateDeviceSecret() {
    StringBuilder builder = new StringBuilder();
    builder.append(SECRET_PREFIX);
    for (int i = 0; i < 16; i++) {
      builder.append(SECRET_CHARS[secureRandom.nextInt(SECRET_CHARS.length)]);
    }
    return builder.toString().toUpperCase(Locale.ROOT);
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

  private static class ImportDecision {
    private final String action;
    private final String message;

    private ImportDecision(String action, String message) {
      this.action = action;
      this.message = message;
    }
  }
}
