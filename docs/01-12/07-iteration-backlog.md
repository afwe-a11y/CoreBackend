# 07-iteration-backlog.md

## 候选迭代项（来自代码缺口与风险）

1) 鉴权与鉴别机制补全（如过滤器、权限校验）。ASSUMED
2) 设备导入 Excel 解析与文件上传流程补齐。ASSUMED
3) 网关 disable 状态下的数据处理停止策略落地（当前仅保存 enabled）。ASSUMED
4) 验证码返回类型（图片/文本）与协议统一（captchaImage 字段语义）。ASSUMED
5) 成员创建“手机号/邮箱至少一个”的规则一致性（当前邮箱必填）。ASSUMED

## Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/AuthController.java |
getCaptcha | captchaImage 字段承载验证码内容 | L33-L37
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
createInternalMember | 邮箱必填 | L108-L129
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
previewImport/commitImport | 导入仅接受 rows | L149-L221
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
toggleGateway | 仅更新 enabled | L182-L186

## UNKNOWN/ASSUMED

- ASSUMED：鉴权与外部导入能力可能由上层服务或网关承担；需结合全局架构确认。
