---
description: 设备管理相关API
---

### 1. Device Models & Points

```ts
export interface DeviceModelListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {}

/** 设备模型列表项 */
export interface DeviceModelListItemVO {
  deviceModelId: long; // 设备模型ID
  deviceModelIdentifier: string; // 设备模型标识
  name: string; // 名称
  attributePointCount: int; // 属性点数量
  measurementPointCount: int; // 测量点数量
}

/** 创建设备模型请求 */
export interface CreateDeviceModelRequestDTO {
  deviceModelIdentifier: string; // 设备模型标识
  name: string; // 名称
  description?: string; // 描述
  deviceType?: DeviceTypeEnum; // deviceType 字段
  sourceType: DeviceModelSourceTypeEnum; // sourceType 字段
  parentDeviceModelId?: long; // parentDeviceModel ID
}

/** 枚举选项项 */
export interface EnumItemDTO {
  optionKey: string; // 选项Key
  optionName: string; // 选项名称
  disabledStatus?: DisabledStatusEnum; // 禁用状态
}

export interface DeviceModelPointListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {
  deviceModelId: long;
}

/** 设备模型点位列表项 */
export interface DeviceModelPointListItemVO {
  pointId: long; // 点位ID
  pointIdentifier: string; // 点位标识
  pointNameZh: string; // 点位中文名
  pointNameEn: string; // 点位英文名
  pointType: PointTypeEnum; // 点位类型
  dataFormat: DataFormatEnum; // 数据格式
  unit: UnitCodeEnum; // 单位
  source: string; // source 字段
  telemetrySource: string; // telemetrySource 字段
}

/** 创建/更新设备模型点位请求 */
export interface CreateOrUpdateDeviceModelPointRequestDTO {
  pointIdentifier: string; // 点位标识
  pointNameZh: string; // 点位中文名
  pointNameEn: string; // 点位英文名
  pointType: PointTypeEnum; // 点位类型
  unit: UnitCodeEnum; // 单位
  dataFormat: DataFormatEnum; // 数据格式
  enumItems?: EnumItemDTO[]; // 枚举项列表
  createRequiredStatus?: RequiredStatusEnum; // 创建必填状态
  configRequiredStatus?: RequiredStatusEnum; // 配置必填状态
}
```

### 2. Products & Mappings

```ts
export interface ProductListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {}

/** 产品列表项 */
export interface ProductListItemVO {
  productId: long; // 产品ID
  name: string; // 名称
  deviceModelName: string; // deviceModel 名称
  accessMethod: AccessMethodEnum; // 接入方式
  connectedDeviceCount: int; // 接入设备数
}

/** 创建产品请求 */
export interface CreateProductRequestDTO {
  name: string; // 名称
  productKey: string; // 产品Key
  productSecret: string; // 产品Secret
  deviceModelId: long; // 设备模型ID
  accessMethod: AccessMethodEnum; // 接入方式
  description?: string; // 描述
}

/** 更新产品请求 */
export interface UpdateProductRequestDTO {
  name: string; // 名称
  description?: string; // 描述
}

/** 产品密钥返回 */
export interface ProductSecretVO {
  productKey: string; // 产品Key
  productSecret: string; // 产品Secret
}

/** 产品详情 */
export interface ProductDetailVO {
  productId: long; // 产品ID
  name: string; // 名称
  productKey: string; // 产品Key
  productSecret: string; // 产品Secret
  deviceModelName: string; // deviceModel 名称
  accessMethod: AccessMethodEnum; // 接入方式
  description: string; // 描述
}

/** 产品测点映射项 */
export interface ProductPointMappingItemVO {
  mappingId: long; // 映射ID
  pointIdentifier: string; // 点位标识
  pointNameZh: string; // 点位中文名
  collectorPointName: string; // 采集点名称
  accessStatus: MappingAccessStatusEnum; // 接入状态
}

/** 更新产品测点映射请求 */
export interface UpdateProductPointMappingsRequestDTO {
  mappings: Array<{ // 映射列表
    mappingId: long; // 映射ID
    collectorPointName: string; // 采集点名称
    accessStatus?: MappingAccessStatusEnum; // 接入状态
  }>;
}
```

### 3. Gateways

```ts
export interface GatewayListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {
  stationCode?: string;
}

/** 网关列表项 */
export interface GatewayListItemVO {
  gatewayId: long; // 网关ID
  name: string; // 名称
  stationName: string; // station 名称
  stationCode: string; // 站点编码
  gatewayProductName: string; // gatewayProduct 名称
  gatewaySn: string; // 网关SN
  onlineStatus: OnlineStatusEnum; // 在线状态
  enableStatus: EnableStatusEnum; // 启用状态
  lastOnlineAt: long; // 最后在线时间
}

/** 创建网关请求 */
export interface CreateGatewayRequestDTO {
  name: string; // 名称
  stationCode: string; // 站点编码
  gatewayType: GatewayTypeEnum; // gatewayType 字段
  gatewayProductId: long; // gatewayProduct ID
  gatewaySn?: string; // 网关SN
}

/** 更新网关请求 */
export interface UpdateGatewayRequestDTO {
  name?: string; // 名称
  stationCode?: string; // 站点编码
  gatewayType?: GatewayTypeEnum; // gatewayType 字段
  gatewaySn?: string; // 网关SN
}

/** 更新网关启用状态请求 */
export interface UpdateGatewayEnableStatusRequestDTO {
  enableStatus: EnableStatusEnum; // 启用状态
}
```

### 4. Devices（deviceCode）

```ts
export interface DeviceListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {
  productId?: long;
  gatewayId?: long;
  stationCode?: string;
  onlineStatus?: OnlineStatusEnum;
  enableStatus?: EnableStatusEnum;
}

/** 设备列表项 */
export interface DeviceListItemVO {
  deviceCode: string; // 设备编码（唯一键）
  name: string; // 名称
  productName: string; // product 名称
  deviceType: string; // deviceType 字段
  stationName: string; // station 名称
  onlineStatus: OnlineStatusEnum; // 在线状态
  gatewayName: string; // gateway 名称
  enableStatus: EnableStatusEnum; // 启用状态
  lastOnlineAt: long; // 最后在线时间
}

/** 设备统计 */
export interface DeviceStatsVO {
  enabledDeviceCount: int; // 启用设备数
  onlineDeviceCount: int; // 在线设备数
}

/** 创建设备请求 */
export interface CreateDeviceRequestDTO {
  name: string; // 名称
  productId: long; // 产品ID
  deviceCode?: string; // 设备编码（唯一键）
  gatewayId: long; // 网关ID
  enableStatus: EnableStatusEnum; // 启用状态
  dynamicAttributes?: JsonObject; // 动态属性
}

/** 更新设备请求 */
export interface UpdateDeviceRequestDTO {
  name?: string; // 名称
  gatewayId?: long; // 网关ID
  enableStatus?: EnableStatusEnum; // 启用状态
  dynamicAttributes?: JsonObject; // 动态属性
}

/** 更新设备启用状态请求 */
export interface UpdateDeviceEnableStatusRequestDTO {
  enableStatus: EnableStatusEnum; // 启用状态
}

/** 设备密钥返回 */
export interface DeviceSecretVO {
  deviceCode: string; // 设备编码（唯一键）
  deviceSecret: string; // deviceSecret 字段
}

/** 设备最新数据项 */
export interface DeviceLatestDataItemVO {
  pointNameZh: string; // 点位中文名
  pointIdentifier: string; // 点位标识
  updatedAt: long; // 更新时间
  value: string; // value 字段
  unit: string; // 单位
}

/** 设备最新数据返回 */
export interface DeviceLatestDataVO {
  items: DeviceLatestDataItemVO[]; // 列表项
}
```