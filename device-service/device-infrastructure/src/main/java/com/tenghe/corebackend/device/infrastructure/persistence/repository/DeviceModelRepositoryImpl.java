package com.tenghe.corebackend.device.infrastructure.persistence.repository;

import com.tenghe.corebackend.device.infrastructure.persistence.json.JsonCodec;
import com.tenghe.corebackend.device.infrastructure.persistence.mapper.DeviceModelMapper;
import com.tenghe.corebackend.device.infrastructure.persistence.mapper.DeviceModelPointMapper;
import com.tenghe.corebackend.device.infrastructure.persistence.po.DeviceModelPo;
import com.tenghe.corebackend.device.infrastructure.persistence.po.DeviceModelPointPo;
import com.tenghe.corebackend.device.interfaces.DeviceModelRepositoryPort;
import com.tenghe.corebackend.device.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.device.interfaces.portdata.DeviceModelPointPortData;
import com.tenghe.corebackend.device.interfaces.portdata.DeviceModelPortData;
import com.tenghe.corebackend.device.model.DeviceDataType;
import com.tenghe.corebackend.device.model.DeviceModelSource;
import com.tenghe.corebackend.device.model.enums.PointTypeEnum;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DeviceModelRepositoryImpl implements DeviceModelRepositoryPort {
  private final DeviceModelMapper modelMapper;
  private final DeviceModelPointMapper pointMapper;
  private final JsonCodec jsonCodec;
  private final IdGeneratorPort idGenerator;

  public DeviceModelRepositoryImpl(
      DeviceModelMapper modelMapper,
      DeviceModelPointMapper pointMapper,
      JsonCodec jsonCodec,
      IdGeneratorPort idGenerator) {
    this.modelMapper = modelMapper;
    this.pointMapper = pointMapper;
    this.jsonCodec = jsonCodec;
    this.idGenerator = idGenerator;
  }

  @Override
  public DeviceModelPortData save(DeviceModelPortData model) {
    DeviceModelPo po = toPo(model);
    modelMapper.insert(po);
    upsertPoints(model.getId(), model.getPoints());
    return model;
  }

  @Override
  public DeviceModelPortData update(DeviceModelPortData model) {
    DeviceModelPo po = toPo(model);
    modelMapper.update(po);
    upsertPoints(model.getId(), model.getPoints());
    return model;
  }

  @Override
  public DeviceModelPortData findById(Long id) {
    DeviceModelPo po = modelMapper.findById(id);
    return toModelWithPoints(po);
  }

  @Override
  public DeviceModelPortData findByIdentifier(String identifier) {
    DeviceModelPo po = modelMapper.findByIdentifier(identifier);
    return toModelWithPoints(po);
  }

  @Override
  public List<DeviceModelPortData> listAll() {
    return modelMapper.listAll().stream()
        .map(this::toModelWithPoints)
        .collect(Collectors.toList());
  }

  @Override
  public List<DeviceModelPortData> listByParentId(Long parentModelId) {
    return modelMapper.listByParentId(parentModelId).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  private DeviceModelPortData toModelWithPoints(DeviceModelPo po) {
    DeviceModelPortData model = toModel(po);
    if (model == null) {
      return null;
    }
    model.setPoints(loadPoints(model.getId()));
    return model;
  }

  private DeviceModelPortData toModel(DeviceModelPo po) {
    if (po == null) {
      return null;
    }
    DeviceModelPortData model = new DeviceModelPortData();
    model.setId(po.getId());
    model.setIdentifier(po.getIdentifier());
    model.setName(po.getName());
    model.setSource(DeviceModelSource.fromValue(po.getSource()));
    model.setParentModelId(po.getParentModelId());
    model.setCreatedAt(po.getCreateTime());
    model.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
    return model;
  }

  private DeviceModelPo toPo(DeviceModelPortData model) {
    DeviceModelPo po = new DeviceModelPo();
    po.setId(model.getId());
    po.setIdentifier(model.getIdentifier());
    po.setName(model.getName());
    po.setSource(model.getSource() == null ? null : model.getSource().name());
    po.setParentModelId(model.getParentModelId());
    po.setCreateTime(model.getCreatedAt() == null ? Instant.now() : model.getCreatedAt());
    po.setDeleted(model.isDeleted() ? 1 : 0);
    return po;
  }

  private List<DeviceModelPointPortData> loadPoints(Long modelId) {
    if (modelId == null) {
      return new ArrayList<>();
    }
    return pointMapper.listByModelId(modelId).stream()
        .map(this::toPointModel)
        .collect(Collectors.toList());
  }

  private DeviceModelPointPortData toPointModel(DeviceModelPointPo po) {
    DeviceModelPointPortData point = new DeviceModelPointPortData();
    point.setIdentifier(po.getIdentifier());
    point.setName(po.getName());
    point.setPointType(PointTypeEnum.fromValue(po.getPointType()));
    point.setDataType(DeviceDataType.fromValue(po.getDataType()));
    point.setEnumItems(jsonCodec.readStringList(po.getEnumItemsJson()));
    return point;
  }

  private void upsertPoints(Long modelId, List<DeviceModelPointPortData> points) {
    if (modelId == null || points == null) {
      return;
    }
    int sortNo = 1;
    for (DeviceModelPointPortData point : points) {
      if (point == null || point.getIdentifier() == null) {
        continue;
      }
      DeviceModelPointPo existing = pointMapper.findByModelIdAndIdentifier(modelId, point.getIdentifier());
      DeviceModelPointPo po = new DeviceModelPointPo();
      po.setId(existing == null ? idGenerator.nextId() : existing.getId());
      po.setModelId(modelId);
      po.setPointType(point.getPointType() == null ? null : point.getPointType().name());
      po.setDataType(point.getDataType() == null ? null : point.getDataType().name());
      po.setIdentifier(point.getIdentifier());
      po.setName(resolvePointName(point.getName(), point.getIdentifier()));
      po.setEnumItemsJson(encodeEnumItems(point.getEnumItems()));
      po.setSortNo(sortNo++);
      po.setCreateTime(existing == null ? Instant.now() : existing.getCreateTime());
      po.setDeleted(0);
      if (existing == null) {
        pointMapper.insert(po);
      } else {
        pointMapper.update(po);
      }
    }
  }

  private String encodeEnumItems(List<String> items) {
    if (items == null || items.isEmpty()) {
      return null;
    }
    return jsonCodec.writeValue(items);
  }

  private String resolvePointName(String name, String identifier) {
    if (name == null || name.trim().isEmpty()) {
      return identifier;
    }
    return name;
  }
}
