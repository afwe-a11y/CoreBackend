# 11-prompt-pack.md

## 使用说明
- 本文件用于后续增量更新文档包（01–12），避免全量重建。
- 变更范围以代码 diff 为准（ASSUMED：使用 git 或变更清单）。

## 增量更新模板（INCREMENTAL）
```
[变更范围]
- 变更模块/路径：
- 变更类型：新增/修改/删除
- 影响的接口/用例/数据模型：

[更新动作]
- 需要更新的文档：
- 修改的章节/条目：
- 新增的 Evidence：

[一致性检查]
- 文档与代码一致性：
- UNKNOWN/ASSUMED 变化：

[变更记录]
- 日期：
- 原因：
- 影响文件：
- 未解决的 UNKNOWN/ASSUMED：
```

## 回归检查要求（Regression Checklist）
- 路由入口覆盖率=100%（Controller/Route 全量枚举）。
- 每个 endpoint 条目包含 Side Effects 与 Idempotency。
- 每份文档包含 Evidence 与 UNKNOWN/ASSUMED。
- DDL 仅来源于 PO；表名与字段 SQL 类型必须有证据。
- PO 标注“非MYSQL表”需全部排除。
- DDL 字段行包含注释来源且格式正确。
- 不输出 infer 约束（长度/精度/默认/索引等）。
- 自证报告 Overall 判定严格：任一 MUST FAIL => Overall FAIL。

## REBUILD 触发条件
- 用户显式指定 `DOCS_RUN_MODE=REBUILD`。
- 文档包缺失或无法增量修复。

## Evidence
- AGENTS.docs.v3.md | DOCS MODE 规则 | 增量/重建与回归要求 | L1-L180

## UNKNOWN/ASSUMED
- 变更范围获取方式 ASSUMED：默认从 git diff 获取，代码未提供工具链。
