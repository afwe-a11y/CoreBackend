package com.tenghe.corebackend.device.application.service;

import com.tenghe.corebackend.device.application.command.CreateGatewayCommand;
import com.tenghe.corebackend.device.application.command.ToggleGatewayCommand;
import com.tenghe.corebackend.device.application.command.UpdateGatewayCommand;
import com.tenghe.corebackend.device.application.exception.BusinessException;
import com.tenghe.corebackend.device.application.service.result.GatewayListItemResult;
import com.tenghe.corebackend.device.application.service.result.PageResult;
import com.tenghe.corebackend.device.application.validation.ValidationUtils;
import com.tenghe.corebackend.device.interfaces.*;
import com.tenghe.corebackend.device.interfaces.portdata.DevicePortData;
import com.tenghe.corebackend.device.interfaces.portdata.GatewayPortData;
import com.tenghe.corebackend.device.interfaces.portdata.ProductPortData;
import com.tenghe.corebackend.device.model.GatewayType;
import com.tenghe.corebackend.device.model.ProductType;
import com.tenghe.corebackend.device.model.enums.EnableStatusEnum;
import com.tenghe.corebackend.device.model.enums.OnlineStatusEnum;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class GatewayApplicationService {
  private static final int DEFAULT_PAGE_SIZE = 10;

  private final GatewayRepositoryPort gatewayRepository;
  private final ProductRepositoryPort productRepository;
  private final DeviceRepositoryPort deviceRepository;
  private final IdGeneratorPort idGenerator;
  private final TransactionManagerPort transactionManager;

  public GatewayApplicationService(
      GatewayRepositoryPort gatewayRepository,
      ProductRepositoryPort productRepository,
      DeviceRepositoryPort deviceRepository,
      IdGeneratorPort idGenerator,
      TransactionManagerPort transactionManager) {
    this.gatewayRepository = gatewayRepository;
    this.productRepository = productRepository;
    this.deviceRepository = deviceRepository;
    this.idGenerator = idGenerator;
    this.transactionManager = transactionManager;
  }

  public PageResult<GatewayListItemResult> listGateways(String keyword, Integer page, Integer size) {
    List<GatewayPortData> gateways = keyword == null || keyword.trim().isEmpty()
        ? gatewayRepository.listAll()
        : gatewayRepository.searchByNameOrSn(keyword);
    gateways.sort(Comparator.comparing(GatewayPortData::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .reversed());
    long total = gateways.size();
    List<GatewayPortData> paged = paginate(gateways, normalizePage(page), normalizeSize(size));
    List<GatewayListItemResult> items = new ArrayList<>();
    for (GatewayPortData gateway : paged) {
      GatewayListItemResult item = new GatewayListItemResult();
      item.setId(gateway.getId());
      item.setName(gateway.getName());
      item.setType(gateway.getType() == null ? null : gateway.getType().name());
      item.setSn(gateway.getSn());
      item.setProductId(gateway.getProductId());
      item.setStationId(gateway.getStationId());
      item.setStatus(gateway.getOnlineStatus() == null ? null : gateway.getOnlineStatus().name());
      item.setEnabled(gateway.getEnableStatus() == EnableStatusEnum.ENABLED);
      items.add(item);
    }
    return new PageResult<>(items, total, normalizePage(page), normalizeSize(size));
  }

  public GatewayListItemResult createGateway(CreateGatewayCommand command) {
    ValidationUtils.requireNonBlank(command.getName(), "Gateway name is required");
    ValidationUtils.requireMaxLength(command.getName(), 50, "Gateway name too long");
    ValidationUtils.requireNonBlank(command.getType(), "Gateway type is required");
    ValidationUtils.requireNonBlank(command.getSn(), "Gateway SN is required");
    ValidationUtils.requireMaxLength(command.getSn(), 20, "Gateway SN too long");
    ValidationUtils.requireNotNull(command.getProductId(), "Gateway product is required");
    ValidationUtils.requireNotNull(command.getStationId(), "Station is required");

    if (gatewayRepository.findBySn(command.getSn()) != null) {
      throw new BusinessException("Gateway SN already exists");
    }

    ProductPortData product = productRepository.findById(command.getProductId());
    if (product == null || product.isDeleted()) {
      throw new BusinessException("Product not found");
    }
    if (product.getProductType() != ProductType.GATEWAY) {
      throw new BusinessException("Product is not a gateway product");
    }

    GatewayType type = GatewayType.fromValue(command.getType());
    if (type == null) {
      throw new BusinessException("Gateway type is invalid");
    }

    GatewayPortData gateway = new GatewayPortData();
    gateway.setId(idGenerator.nextId());
    gateway.setName(command.getName());
    gateway.setType(type);
    gateway.setSn(command.getSn());
    gateway.setProductId(command.getProductId());
    gateway.setStationId(command.getStationId());
    gateway.setOnlineStatus(OnlineStatusEnum.OFFLINE);
    gateway.setEnableStatus(EnableStatusEnum.ENABLED);
    gateway.setCreatedAt(Instant.now());
    gateway.setDeleted(false);
    gatewayRepository.save(gateway);

    GatewayListItemResult result = new GatewayListItemResult();
    result.setId(gateway.getId());
    result.setName(gateway.getName());
    result.setType(gateway.getType().name());
    result.setSn(gateway.getSn());
    result.setProductId(gateway.getProductId());
    result.setStationId(gateway.getStationId());
    result.setStatus(gateway.getOnlineStatus().name());
    result.setEnabled(gateway.getEnableStatus() == EnableStatusEnum.ENABLED);
    return result;
  }

  public void updateGateway(UpdateGatewayCommand command) {
    GatewayPortData gateway = requireGateway(command.getGatewayId());
    boolean stationChanged = false;
    if (command.getName() != null) {
      ValidationUtils.requireMaxLength(command.getName(), 50, "Gateway name too long");
      gateway.setName(command.getName());
    }
    if (command.getSn() != null) {
      ValidationUtils.requireMaxLength(command.getSn(), 20, "Gateway SN too long");
      GatewayPortData existing = gatewayRepository.findBySn(command.getSn());
      if (existing != null && !existing.getId().equals(gateway.getId())) {
        throw new BusinessException("Gateway SN already exists");
      }
      gateway.setSn(command.getSn());
    }
    if (command.getProductId() != null) {
      ProductPortData product = productRepository.findById(command.getProductId());
      if (product == null || product.isDeleted()) {
        throw new BusinessException("Product not found");
      }
      if (product.getProductType() != ProductType.GATEWAY) {
        throw new BusinessException("Product is not a gateway product");
      }
      gateway.setProductId(command.getProductId());
    }
    if (command.getStationId() != null) {
      stationChanged = !Objects.equals(command.getStationId(), gateway.getStationId());
      gateway.setStationId(command.getStationId());
    }
    final boolean finalStationChanged = stationChanged;
    Runnable persist = () -> {
      gatewayRepository.update(gateway);
      if (finalStationChanged) {
        List<DevicePortData> devices = deviceRepository.listByGatewayId(gateway.getId());
        for (DevicePortData device : devices) {
          device.setStationId(gateway.getStationId());
          deviceRepository.update(device);
        }
      }
    };
    if (finalStationChanged) {
      transactionManager.doInTransaction(persist);
    } else {
      persist.run();
    }
  }

  public void deleteGateway(Long gatewayId) {
    GatewayPortData gateway = requireGateway(gatewayId);
    if (!deviceRepository.listByGatewayId(gatewayId).isEmpty()) {
      throw new BusinessException("Gateway has sub-devices");
    }
    gateway.setDeleted(true);
    gatewayRepository.update(gateway);
  }

  public void toggleGateway(ToggleGatewayCommand command) {
    GatewayPortData gateway = requireGateway(command.getGatewayId());
    gateway.setEnableStatus(command.isEnabled() ? EnableStatusEnum.ENABLED : EnableStatusEnum.DISABLED);
    gatewayRepository.update(gateway);
  }

  private GatewayPortData requireGateway(Long gatewayId) {
    if (gatewayId == null) {
      throw new BusinessException("Gateway not found");
    }
    GatewayPortData gateway = gatewayRepository.findById(gatewayId);
    if (gateway == null || gateway.isDeleted()) {
      throw new BusinessException("Gateway not found");
    }
    return gateway;
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
