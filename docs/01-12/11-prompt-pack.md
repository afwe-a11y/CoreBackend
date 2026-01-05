# 11-prompt-pack.md

## 增量更新模板（INCREMENTAL）

- 变更范围：<模块/接口/数据模型>
- 触发原因：<需求/缺陷/一致性修正>
- 影响文件：<docs/01-12/...>
- 证据清单：
    - path | symbol | summary | locator
- 未解决项：<UNKNOWN/ASSUMED 列表>

## 回归检查清单

1) 路由入口覆盖率：是否覆盖所有 Controller/Route 入口。
2) Evidence：每份文档是否包含 Evidence。
3) UNKNOWN/ASSUMED：每份文档末尾是否包含清单。
4) API 条目：每个 endpoint 是否包含 Side Effects 与 Idempotency。
5) DDL 规则：若生成 DDL，是否仅来源于 PO 且字段含注释来源。
6) 继承处理：若生成 DDL，是否展开父类字段并记录证据。

## 回滚提示

- 若发现文档与代码不一致，优先修正文档并记录差异原因。

## Evidence

- /Users/sirgan/Downloads/CoreBackend/AGENTS.docs.v3.md | PACK-001 | Prompt pack 需包含增量模板与回归检查 | L135-L139

## UNKNOWN/ASSUMED

- UNKNOWN：后续是否启用 REBUILD 或 INCREMENTAL 模式由调用方决定。
