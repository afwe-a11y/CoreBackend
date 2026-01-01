# Kronos BFF Service API Documentation

## 概述

Kronos BFF 服务是设备管理的前端网关服务，提供设备模型、产品、网关、设备的管理接口。

**Base URL**: `http://localhost:8082/api/v1`

---

## 1. 设备模型 API

### 1.1 创建设备模型

```
POST /device-models
```

**Request Body**:
```json
{
  "identifier": "MODEL01",
  "name": "光伏逆变器模型",
  "source": "NEW",
  "parentModelId": null,
  "description": "标准光伏逆变器设备模型"
}
```

**Response**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": "1234567890123456789",
    "identifier": "MODEL01",
    "name": "光伏逆变器模型",
    "source": "NEW",
    "parentModelId": null,
    "points": [],
    "createdAt": "2024-01-01 12:00:00"
  }
}
```

### 1.2 更新设备模型

```
PUT /device-models/{id}
```

### 1.3 删除设备模型

```
DELETE /device-models/{id}
```

**阻塞规则**:
- 被产品引用时无法删除
- 被其他模型作为父模型引用时无法删除

### 1.4 查询设备模型详情

```
GET /device-models/{id}
```

### 1.5 分页查询设备模型列表

```
GET /device-models?keyword=xxx&page=1&size=20
```

### 1.6 导入模型点位

```
POST /device-models/{id}/points/import
```

**Request Body**:
```json
{
  "points": [
    {
      "identifier": "voltage",
      "name": "电压",
      "type": "TELEMETRY",
      "dataType": "FLOAT",
      "unit": "V",
      "description": "输出电压"
    },
    {
      "identifier": "status",
      "name": "状态",
      "type": "ATTRIBUTE",
      "dataType": "ENUM",
      "enumItems": ["RUNNING", "STOPPED", "FAULT"]
    }
  ]
}
```

---

## 2. 产品 API

### 2.1 创建产品

```
POST /products
```

**Request Body**:
```json
{
  "name": "户用逆变器",
  "productKey": null,
  "productSecret": null,
  "deviceModelId": 1234567890123456789,
  "accessMode": "GENERAL_MQTT",
  "description": "户用光伏逆变器产品"
}
```

### 2.2 更新产品

```
PUT /products/{id}
```

**仅允许修改**: `name`, `description`

### 2.3 更新协议映射

```
PUT /products/{id}/protocol-mappings
```

**Request Body**:
```json
{
  "mappings": {
    "voltage": "pv_voltage",
    "current": "pv_current"
  }
}
```

### 2.4 删除产品

```
DELETE /products/{id}
```

**阻塞规则**: 产品下存在设备时无法删除

### 2.5 查询产品详情

```
GET /products/{id}
```

### 2.6 分页查询产品列表

```
GET /products?keyword=xxx&page=1&size=20
```

---

## 3. 网关 API

### 3.1 创建网关

```
POST /gateways
```

**Request Body**:
```json
{
  "name": "边缘网关-01",
  "type": "EDGE",
  "sn": "GW20240101001",
  "productId": 1234567890123456789,
  "stationId": 1234567890123456789,
  "description": "A区边缘网关"
}
```

### 3.2 更新网关

```
PUT /gateways/{id}
```

**网关类型(type)创建后不可变**

### 3.3 启用/禁用网关

```
PUT /gateways/{id}/toggle
```

**Request Body**:
```json
{
  "enabled": false
}
```

**注意**: 禁用网关后，后端将停止处理该网关下所有子设备的数据

### 3.4 删除网关

```
DELETE /gateways/{id}
```

**阻塞规则**: 网关下存在设备时无法删除（无论设备状态）

### 3.5 查询网关详情

```
GET /gateways/{id}
```

### 3.6 分页查询网关列表

```
GET /gateways?keyword=xxx&stationId=xxx&page=1&size=20
```

---

## 4. 设备 API

### 4.1 创建设备

```
POST /devices
```

**Request Body**:
```json
{
  "name": "逆变器-001",
  "productId": 1234567890123456789,
  "deviceKey": null,
  "gatewayId": 1234567890123456789,
  "dynamicAttributes": {
    "installDate": "2024-01-01",
    "capacity": 10
  },
  "description": "A区逆变器"
}
```

**注意**: `stationId` 自动从网关同步

### 4.2 更新设备

```
PUT /devices/{id}
```

**不可变字段**: `productId`, `deviceKey`

### 4.3 删除设备

```
DELETE /devices/{id}
```

### 4.4 查询设备详情

```
GET /devices/{id}
```

### 4.5 分页查询设备列表

```
GET /devices?productId=xxx&gatewayId=xxx&stationId=xxx&name=xxx&deviceKey=xxx&status=ONLINE&page=1&size=20
```

### 4.6 批量导入设备

```
POST /devices/products/{productId}/import
```

**Request Body**:
```json
[
  {
    "id": null,
    "name": "逆变器-001",
    "deviceKey": "DEV001",
    "gatewaySn": "GW20240101001",
    "dynamicAttributes": {},
    "description": "",
    "rowNumber": 1
  }
]
```

**导入逻辑**:
- `id` 为空 → 创建新设备
- `id` 存在 → 更新设备

**Response**:
```json
{
  "code": "SUCCESS",
  "data": {
    "successCount": 10,
    "failCount": 2,
    "createCount": 8,
    "updateCount": 2,
    "errors": [
      {
        "rowNumber": 5,
        "deviceKey": "DEV005",
        "reason": "网关不存在"
      }
    ]
  }
}
```

### 4.7 获取设备实时数据

```
GET /devices/{id}/realtime
```

**Response**:
```json
{
  "code": "SUCCESS",
  "data": {
    "deviceId": "1234567890123456789",
    "deviceName": "逆变器-001",
    "status": "ONLINE",
    "telemetryData": {
      "voltage": 380.5,
      "current": 10.2
    },
    "attributeData": {
      "capacity": 10
    },
    "dataTime": "2024-01-01 12:00:00"
  }
}
```

### 4.8 导出设备列表

```
GET /devices/export?productId=xxx&gatewayId=xxx&stationId=xxx&name=xxx&deviceKey=xxx&status=ONLINE
```

**说明**: 
- 导出范围：尊重当前筛选条件（非仅当前页）
- 文件名格式：{Product}_{Description}_{Timestamp}.xlsx

**Response**:
```json
{
  "code": "SUCCESS",
  "data": [
    {
      "id": "1234567890123456789",
      "name": "逆变器-001",
      "deviceKey": "DEV001",
      "productName": "户用逆变器",
      "gatewayName": "边缘网关-01",
      "stationName": "A区电站",
      "status": "ONLINE",
      "createdAt": "2024-01-01 12:00:00"
    }
  ]
}
```

---

## 错误码

| 错误码 | 说明 |
|--------|------|
| SUCCESS | 操作成功 |
| INVALID_PARAM | 参数校验失败 |
| DEVICE_MODEL_NOT_FOUND | 设备模型不存在 |
| DEVICE_MODEL_IDENTIFIER_EXISTS | 设备模型标识符已存在 |
| DEVICE_MODEL_USED_BY_PRODUCT | 设备模型被产品引用，无法删除 |
| DEVICE_MODEL_USED_AS_PARENT | 设备模型被其他模型作为父模型引用，无法删除 |
| PRODUCT_NOT_FOUND | 产品不存在 |
| PRODUCT_KEY_EXISTS | 产品Key已存在 |
| PRODUCT_HAS_DEVICES | 产品下存在设备，无法删除 |
| GATEWAY_NOT_FOUND | 网关不存在 |
| GATEWAY_SN_EXISTS | 网关序列号已存在 |
| GATEWAY_HAS_DEVICES | 网关下存在设备，无法删除 |
| DEVICE_NOT_FOUND | 设备不存在 |
| DEVICE_KEY_EXISTS | 设备Key已存在 |
| INTERNAL_ERROR | 系统内部错误 |
