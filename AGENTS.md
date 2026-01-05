---
description: 中台服务多模块项目结构规范 - 用于生成代码时的模块划分指导和代码修正参考
---

# 中台服务多模块项目结构规范

本文档定义了基于 DDD（领域驱动设计）思想的中台服务项目结构规范，适用于 `{service-name}-service` 类型的微服务项目。

---

## 一、模块总览

项目采用 **7 模块分层架构**，模块间依赖关系清晰，职责分明：

```
{service-name}-service/
├── {service-name}-main           # 启动模块
├── {service-name}-model          # 基础模型模块（最底层）
├── {service-name}-api            # API 契约模块（对外暴露）
├── {service-name}-interfaces     # 接口定义模块（内部契约）
├── {service-name}-application    # 应用服务模块（业务编排）
├── {service-name}-infrastructure # 基础设施模块（技术实现）
└── {service-name}-controller     # 控制器模块（HTTP 入口）
```

---

## 二、模块依赖关系

```
                    ┌─────────────────┐
                    │  {service}-main │  (启动入口，聚合所有模块)
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
┌───────────────┐  ┌─────────────────────┐  ┌─────────────────────┐
│  controller   │  │    application      │  │   infrastructure    │
│  (HTTP入口)   │  │    (业务编排)       │  │   (技术实现)        │
└───────┬───────┘  └──────────┬──────────┘  └──────────┬──────────┘
        │                     │                        │
        │                     ▼                        │
        │          ┌─────────────────────┐             │
        └─────────►│    interfaces       │◄────────────┘
                   │  (接口契约/PortData)│
                   └──────────┬──────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│     api       │     │    model      │     │    model      │
│  (对外契约)   │────►│  (基础模型)   │◄────│  (基础模型)   │
└───────────────┘     └───────────────┘     └───────────────┘
```

**核心依赖原则：**
- `model` 是最底层模块，不依赖任何其他业务模块
- `interfaces` 依赖 `model`，定义内部接口契约
- `api` 依赖 `model`，定义对外 API 契约
- `application` 依赖 `interfaces`，编排业务逻辑
- `infrastructure` 依赖 `interfaces`，实现接口契约
- `controller` 依赖 `api` 和 `application`
- `main` 聚合所有模块，负责启动

---

## 三、各模块详细规范

### 3.1 `{service-name}-model` 模块

**职责：** 定义跨模块共享的基础模型，包括常量、枚举、值对象等。

**包结构：**
```
com.helios.{servicename}.model/
├── constants/          # 常量定义
│   └── XxxConstant.java
├── enums/              # 枚举定义
│   └── XxxEnum.java
└── vo/                 # 值对象（Value Object）
    └── XxxVO.java
```

**放置内容：**
| 类型 | 说明 | 示例 |
|------|------|------|
| 常量类 | 服务级别的常量定义 | `UserConstant.FEIGN_CLIENT_USER_SERVICE` |
| 枚举类 | 业务状态枚举 | `UserStatusEnum`, `RoleTypeEnum` |
| 值对象 | 不可变的业务值对象 | `LoggedInUserExtra` |

**注意事项：**
- ❌ 不放 DTO、Entity、PO
- ❌ 不放任何业务逻辑
- ✅ 只放纯数据定义，无任何外部依赖

---

### 3.2 `{service-name}-api` 模块

**职责：** 定义对外暴露的 API 契约，供其他微服务通过 Feign 调用。

**包结构：**
```
com.helios.{servicename}.api/
├── dto/                # 数据传输对象
│   ├── {domain}/       # 按领域分包
│   │   ├── XxxCreateDTO.java
│   │   ├── XxxUpdateDTO.java
│   │   ├── XxxSearchDTO.java
│   │   ├── XxxQueryExistDTO.java
│   │   └── XxxResponseDTO.java
│   └── ...
└── v1/                 # API 版本
    └── XxxApi.java     # Feign 接口定义
```

**放置内容：**
| 类型 | 说明 | 命名规范 |
|------|------|----------|
| DTO | 请求/响应数据传输对象 | `Xxx{Create/Update/Search/Response}DTO` |
| Feign API | 对外暴露的接口定义 | `XxxApi` |

**API 接口规范：**
```java
@Tag(name = "领域名称")
@Validated
@FeignClient(
    value = FEIGN_CLIENT_SERVICE_NAME,
    contextId = "xxxFeignApiContext",
    path = "/xxxApi"
)
public interface XxxApi {
    @Operation(summary = "操作描述")
    @PostMapping("/methodName")
    ApiResponse<XxxResponseDTO> methodName(@RequestBody XxxDTO dto);
}
```

**注意事项：**
- ✅ 所有接口使用 `@PostMapping`
- ✅ 返回值统一使用 `ApiResponse<T>` 包装
- ✅ 使用 Swagger 注解描述接口
- ❌ 不放任何业务逻辑实现

---

### 3.3 `{service-name}-interfaces` 模块

**职责：** 定义内部接口契约，包括 Repository 接口和 PortData（端口数据）。

**包结构：**
```
com.helios.{servicename}.interfaces/
├── cache/              # 缓存相关接口
│   └── XxxCacheRepository.java
├── portdata/           # 端口数据（内部 DTO）
│   └── {domain}/
│       ├── XxxPortData.java
│       ├── XxxSearchPortData.java
│       └── XxxExistPortData.java
└── ports/              # 端口接口（Repository）
    └── {domain}/
        └── XxxRepository.java
```

**放置内容：**
| 类型 | 说明 | 命名规范 |
|------|------|----------|
| PortData | 内部数据传输对象 | `Xxx{Search/Exist}PortData` |
| Repository 接口 | 数据访问接口定义 | `XxxRepository` |
| Cache 接口 | 缓存操作接口定义 | `XxxCacheRepository` |

**Repository 接口规范：**
```java
public interface XxxRepository {
    Long save(XxxPortData data);
    XxxPortData findById(Long id);
    void delete(Long id);
    PageResults<XxxPortData> findAllByPage(XxxSearchPortData data);
    boolean exists(XxxExistPortData data);
}
```

**PortData vs DTO 区别：**
- `DTO`：对外 API 使用，定义在 `api` 模块
- `PortData`：内部模块间传递，定义在 `interfaces` 模块

---

### 3.4 `{service-name}-application` 模块

**职责：** 业务逻辑编排层，协调领域服务和基础设施完成业务用例。

**包结构：**
```
com.helios.{servicename}.application/
├── {domain}/           # 特定领域的应用服务
│   ├── XxxApplicationService.java
│   └── asserts/        # 业务断言
│       └── XxxAssert.java
├── service/            # 通用应用服务
│   └── XxxApplicationService.java
├── convertor/          # 内部转换器（PortData 之间）
│   └── XxxConverter.java
└── startup/            # 启动时执行的逻辑
    └── XxxManagement.java
```

**放置内容：**
| 类型 | 说明 | 命名规范 |
|------|------|----------|
| ApplicationService | 应用服务（业务编排） | `XxxApplicationService` |
| Assert | 业务断言工具类 | `XxxAssert` |
| Converter | PortData 间转换器 | `XxxConverter` |
| Startup | 启动初始化逻辑 | `XxxManagement` |

**ApplicationService 规范：**
```java
@Service
@RequiredArgsConstructor
public class XxxApplicationService {
    private final XxxRepository xxxRepository;  // 注入接口，非实现
    
    @Transactional(rollbackFor = Exception.class, timeout = TRANSACTION_TIME_OUT)
    public XxxPortData create(XxxPortData portData) {
        // 业务编排逻辑
    }
}
```

**注意事项：**
- ✅ 只依赖 `interfaces` 模块定义的接口
- ✅ 使用 `@Transactional` 管理事务
- ✅ 业务断言放在 `asserts` 子包
- ❌ 不直接依赖 `infrastructure` 的实现类

---

### 3.5 `{service-name}-infrastructure` 模块

**职责：** 技术实现层，实现 `interfaces` 模块定义的接口。

**包结构：**
```
com.helios.{servicename}.infrastructure/
├── converter/
│   └── persistence/    # PO ↔ PortData 转换器
│       └── XxxPersistenceConverter.java
└── persistence/
    ├── aggregate/      # 聚合查询结果 PO
    │   └── XxxAggregatePO.java
    ├── mapper/         # MyBatis Mapper
    │   └── XxxMapper.java
    ├── po/             # 持久化对象
    │   └── XxxPO.java
    ├── repository/     # Repository 实现
    │   └── XxxRepositoryImpl.java
    └── service/        # MyBatis-Plus Service
        ├── XxxRepositoryService.java
        └── impl/
            └── XxxRepositoryServiceImpl.java
```

**放置内容：**
| 类型 | 说明 | 命名规范 |
|------|------|----------|
| PO | 持久化对象（对应数据库表） | `XxxPO` |
| Mapper | MyBatis Mapper 接口 | `XxxMapper` |
| RepositoryImpl | Repository 接口实现 | `XxxRepositoryImpl` |
| RepositoryService | MyBatis-Plus IService | `XxxRepositoryService` |
| PersistenceConverter | PO ↔ PortData 转换 | `XxxPersistenceConverter` |
| AggregatePO | 聚合查询结果对象 | `XxxAggregatePO` |

**PO 规范：**
```java
@Data
@SuperBuilder
@NoArgsConstructor
@TableName("table_name")
public class XxxPO extends BasePO {
    private String fieldName;
}
```

**RepositoryImpl 规范：**
```java
@Service
@RequiredArgsConstructor
public class XxxRepositoryImpl implements XxxRepository {
    private final XxxMapper xxxMapper;
    private final XxxRepositoryService xxxService;  // 复杂查询使用
    
    @Override
    public XxxPortData findById(Long id) {
        return XxxPersistenceConverter.INSTANCE.toPortData(xxxMapper.selectById(id));
    }
}
```

---

### 3.6 `{service-name}-controller` 模块

**职责：** HTTP 入口层，实现 `api` 模块定义的接口。

**包结构：**
```
com.helios.{servicename}.controller/
└── web/
    ├── XxxController.java
    └── converter/      # DTO ↔ PortData 转换器
        └── XxxConverter.java
```

**放置内容：**
| 类型 | 说明 | 命名规范 |
|------|------|----------|
| Controller | REST 控制器 | `XxxController` |
| Converter | DTO ↔ PortData 转换 | `XxxConverter` |

**Controller 规范：**
```java
@Tag(name = "领域名称")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/xxxApi")
public class XxxController implements XxxApi {
    private final XxxApplicationService xxxApplicationService;
    
    @Override
    @TraceLog
    public ApiResponse<XxxResponseDTO> create(XxxCreateDTO createDTO) {
        XxxPortData result = xxxApplicationService.create(
            XxxConverter.INSTANCE.toPortData(createDTO)
        );
        return ApiResponse.success(XxxConverter.INSTANCE.toResponseDTO(result));
    }
}
```

**Converter 规范：**
```java
@Mapper(builder = @Builder(disableBuilder = true))
public interface XxxConverter {
    XxxConverter INSTANCE = Mappers.getMapper(XxxConverter.class);
    
    // DTO -> PortData（入参转换）
    XxxPortData toPortData(XxxCreateDTO dto);
    XxxSearchPortData toSearchPortData(XxxSearchDTO dto);
    
    // PortData -> DTO（出参转换）
    XxxResponseDTO toResponseDTO(XxxPortData portData);
}
```

---

### 3.7 `{service-name}-main` 模块

**职责：** 应用启动入口，聚合所有模块，提供配置文件。

**包结构：**
```
com.helios.{servicename}/
└── XxxServiceApplication.java

resources/
├── application.yml
├── bootstrap.yml
└── bootstrap-{env}.yml
```

**Application 规范：**
```java
@EnableAuth
@EnableRestExceptionHandler
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.helios.{servicename}.api"})
public class XxxServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(XxxServiceApplication.class, args);
    }
}
```

---

## 四、数据流转规范

### 4.1 请求数据流

```
HTTP Request
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ Controller                                                   │
│   DTO ──(Converter)──► PortData                             │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ ApplicationService                                           │
│   PortData (业务编排)                                        │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ RepositoryImpl                                               │
│   PortData ──(PersistenceConverter)──► PO                   │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
Database
```

### 4.2 响应数据流

```
Database
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ RepositoryImpl                                               │
│   PO ──(PersistenceConverter)──► PortData                   │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ ApplicationService                                           │
│   PortData (业务编排)                                        │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ Controller                                                   │
│   PortData ──(Converter)──► ResponseDTO                     │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
HTTP Response (ApiResponse<ResponseDTO>)
```

---

## 五、Converter 分布规范

| 模块 | Converter 类型 | 转换方向 |
|------|---------------|----------|
| `controller` | `XxxConverter` | DTO ↔ PortData |
| `application` | `XxxConverter` | PortData 之间（如 UserPortData → UserAclPortData） |
| `infrastructure` | `XxxPersistenceConverter` | PortData ↔ PO |

---

## 六、命名规范速查

| 类型 | 命名规范 | 示例 |
|------|----------|------|
| 模块 | `{service-name}-{layer}` | `auth-service-api` |
| 包名 | `com.helios.{servicename}.{layer}` | `com.helios.authservice.api` |
| API 接口 | `XxxApi` | `UserApi`, `AuthApi` |
| Controller | `XxxController` | `UserController` |
| ApplicationService | `XxxApplicationService` | `UserApplicationService` |
| Repository 接口 | `XxxRepository` | `UserRepository` |
| Repository 实现 | `XxxRepositoryImpl` | `UserRepositoryImpl` |
| DTO | `Xxx{Action}DTO` | `UserCreateDTO`, `UserResponseDTO` |
| PortData | `Xxx{Type}PortData` | `UserPortData`, `UserSearchPortData` |
| PO | `XxxPO` | `UserPO` |
| Converter | `XxxConverter` / `XxxPersistenceConverter` | `UserConverter` |
| 枚举 | `XxxEnum` | `UserStatusEnum` |
| 常量 | `XxxConstant` | `UserConstant` |
| 断言 | `XxxAssert` | `AuthAssert` |

---

## 七、模块发布规范

| 模块 | 是否发布到远程仓库 | 说明 |
|------|-------------------|------|
| `model` | ✅ 是 | 供其他服务依赖 |
| `api` | ✅ 是 | 供其他服务 Feign 调用 |
| `interfaces` | ❌ 否 | 内部使用 |
| `application` | ❌ 否 | 内部使用 |
| `infrastructure` | ❌ 否 | 内部使用 |
| `controller` | ❌ 否 | 内部使用 |
| `main` | ❌ 否 | 仅用于启动 |

---

## 八、常见错误示例

### ❌ 错误：在 api 模块放业务逻辑
```java
// 错误：api 模块不应该有业务逻辑
public interface UserApi {
    default void validateUser(UserDTO dto) { ... }  // ❌
}
```

### ❌ 错误：Controller 直接依赖 Repository
```java
// 错误：Controller 应该通过 ApplicationService 访问数据
@RestController
public class UserController {
    private final UserRepository userRepository;  // ❌ 应该注入 ApplicationService
}
```

### ❌ 错误：ApplicationService 直接使用 PO
```java
// 错误：ApplicationService 应该使用 PortData
@Service
public class UserApplicationService {
    public UserPO findById(Long id) { ... }  // ❌ 应该返回 PortData
}
```

### ❌ 错误：在 model 模块放 DTO
```java
// 错误：DTO 应该放在 api 模块
package com.helios.authservice.model.dto;  // ❌
public class UserDTO { ... }
```

---

## 九、新增功能检查清单

当新增一个领域功能时，确保创建以下文件：

- [ ] `model`: 枚举、常量（如需要）
- [ ] `api/dto/{domain}/`: CreateDTO, UpdateDTO, SearchDTO, ResponseDTO
- [ ] `api/v1/`: XxxApi 接口
- [ ] `interfaces/portdata/{domain}/`: PortData, SearchPortData
- [ ] `interfaces/ports/{domain}/`: XxxRepository 接口
- [ ] `application/service/`: XxxApplicationService
- [ ] `infrastructure/persistence/po/`: XxxPO
- [ ] `infrastructure/persistence/mapper/`: XxxMapper
- [ ] `infrastructure/persistence/repository/`: XxxRepositoryImpl
- [ ] `infrastructure/converter/persistence/`: XxxPersistenceConverter
- [ ] `controller/web/`: XxxController
- [ ] `controller/web/converter/`: XxxConverter

---

*本规范基于 auth-service 项目结构总结，适用于 Helios 平台所有中台微服务项目。*
