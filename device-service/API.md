# Device Service API

Base path: `/api`

Response envelope:

- `ApiResponse<T>`: `success`, `message`, `data`
- `PageResponse<T>`: `items`, `total`, `page`, `size`

## Device Models

### GET `/device-models`

Query:

- `page`, `size`
  Response: `ApiResponse<PageResponse<DeviceModelListItem>>`
- `DeviceModelListItem`: `id`, `identifier`, `name`, `source`, `parentModelId`, `pointCount`

### GET `/device-models/{modelId}`

Response: `ApiResponse<DeviceModelDetailResponse>`

- `DeviceModelDetailResponse`: `id`, `identifier`, `name`, `source`, `parentModelId`, `points`
- `DeviceModelPointDto`: `identifier`, `name`, `type`, `dataType`, `enumItems`

### POST `/device-models`

Request: `CreateDeviceModelRequest`

- `identifier`, `name`, `source`, `parentModelId`, `points`
  Response: `ApiResponse<DeviceModelDetailResponse>`

### DELETE `/device-models/{modelId}`

Response: `ApiResponse<Void>`

### PUT `/device-models/{modelId}/points`

Request: `UpdateDeviceModelPointsRequest`

- `points`
  Response: `ApiResponse<DeviceModelDetailResponse>`

### POST `/device-models/{modelId}/points/import`

Request: `ImportDeviceModelPointsRequest`

- `points`
  Response: `ApiResponse<DeviceModelDetailResponse>`

## Products

### GET `/products`

Query:

- `page`, `size`
  Response: `ApiResponse<PageResponse<ProductListItem>>`
- `ProductListItem`: `id`, `productType`, `name`, `productKey`, `deviceModelId`, `accessMode`, `description`

### POST `/products`

Request: `CreateProductRequest`

- `productType`, `name`, `deviceModelId`, `accessMode`, `description`, `protocolMapping`
  Response: `ApiResponse<CreateProductResponse>`
- `CreateProductResponse`: `id`, `productKey`, `productSecret`

### PUT `/products/{productId}`

Request: `UpdateProductRequest`

- `name`, `description`
  Response: `ApiResponse<Void>`

### PUT `/products/{productId}/mapping`

Request: `UpdateProductMappingRequest`

- `protocolMapping`
  Response: `ApiResponse<Void>`

### DELETE `/products/{productId}`

Response: `ApiResponse<Void>`

## Gateways

### GET `/gateways`

Query:

- `keyword`, `page`, `size`
  Response: `ApiResponse<PageResponse<GatewayListItem>>`
- `GatewayListItem`: `id`, `name`, `type`, `sn`, `productId`, `stationId`, `status`, `enabled`

### POST `/gateways`

Request: `CreateGatewayRequest`

- `name`, `type`, `sn`, `productId`, `stationId`
  Response: `ApiResponse<GatewayListItem>`

### PUT `/gateways/{gatewayId}`

Request: `UpdateGatewayRequest`

- `name`, `sn`, `productId`, `stationId`
  Response: `ApiResponse<Void>`

### DELETE `/gateways/{gatewayId}`

Response: `ApiResponse<Void>`

### POST `/gateways/{gatewayId}/enable`

Response: `ApiResponse<Void>`

### POST `/gateways/{gatewayId}/disable`

Response: `ApiResponse<Void>`

## Devices

### GET `/devices`

Query:

- `productId`, `keyword`, `page`, `size`
  Response: `ApiResponse<PageResponse<DeviceListItem>>`
- `DeviceListItem`: `id`, `name`, `productId`, `deviceKey`, `gatewayId`, `stationId`, `status`

### POST `/devices`

Request: `CreateDeviceRequest`

- `name`, `productId`, `deviceKey`, `gatewayId`, `dynamicAttributes`
  Response: `ApiResponse<CreateDeviceResponse>`
- `CreateDeviceResponse`: `id`, `deviceSecret`

### PUT `/devices/{deviceId}`

Request: `UpdateDeviceRequest`

- `name`, `gatewayId`, `dynamicAttributes`
  Response: `ApiResponse<Void>`

### POST `/devices/import/preview`

Request: `DeviceImportPreviewRequest`

- `rows`
- `DeviceImportRowDto`: `id`, `name`, `productId`, `deviceKey`, `gatewayId`, `dynamicAttributes`
  Response: `ApiResponse<DeviceImportPreviewResponse>`
- `DeviceImportPreviewResponse`: `total`, `createCount`, `updateCount`, `invalidCount`, `items`
- `DeviceImportPreviewItem`: `rowIndex`, `action`, `message`

### POST `/devices/import/commit`

Request: `DeviceImportCommitRequest`

- `rows`
  Response: `ApiResponse<DeviceImportCommitResponse>`
- `DeviceImportCommitResponse`: `successCount`, `failureCount`, `failures`
- `DeviceImportFailure`: `rowIndex`, `message`

### GET `/devices/export`

Query:

- `productId`, `keyword`
  Response: `ApiResponse<DeviceExportResponse>`
- `DeviceExportResponse`: `fileName`, `rows`
- `DeviceExportRow`: `name`, `deviceKey`, `productId`, `gatewayId`, `stationId`

### GET `/devices/{deviceId}/telemetry/latest`

Response: `ApiResponse<List<DeviceTelemetryItem>>`

- `DeviceTelemetryItem`: `pointIdentifier`, `value`, `updatedAt`
