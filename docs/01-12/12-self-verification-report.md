# 12-self-verification-report.md

## Rebuild Report
- 触发原因：用户指定 `DOCS_RUN_MODE=REBUILD`。
- 依据：代码为当前唯一事实来源，重建 01–12 文档包。

## 总览结论
- Overall: FAIL
- FAIL 列表：DDL-003、DDL-005、DDL-006

## 检查项明细
| Check ID | Requirement | Result | Evidence | Impact | Fix Hint |
| --- | --- | --- | --- | --- | --- |
| COV-001 | 路由入口覆盖率=100% | PASS | docs/01-12/02-api-reference.md | Endpoint 条目覆盖所有 Controller 路由 | L8-L725 | 若新增路由需补充 02 文档条目 |
| EVI-001 | 每份文档包含 Evidence | PASS | docs/01-12/01-architecture-overview.md | Evidence 章节 | L61-L74；docs/01-12/02-api-reference.md | Evidence 章节 | L727-L734；docs/01-12/03-domain-model.md | Evidence 章节 | L81-L93；docs/01-12/04-data-model.md | Evidence 章节 | L73-L79；docs/01-12/05-key-flows.md | Evidence 章节 | L42-L48；docs/01-12/06-prd-lite.md | Evidence 章节 | L37-L42；docs/01-12/07-iteration-backlog.md | Evidence 章节 | L19-L24；docs/01-12/08-test-plan.md | Evidence 章节 | L44-L51；docs/01-12/09-runbook.md | Evidence 章节 | L21-L25；docs/01-12/11-prompt-pack.md | Evidence 章节 | L44-L45；docs/01-12/12-self-verification-report.md | Evidence 章节 | L27-L28 | 影响 01–12 | 若缺失需为对应文件补齐 Evidence 章节 |
| API-001 | 每个 endpoint 条目包含 Side Effects 与 Idempotency | PASS | docs/01-12/02-api-reference.md | Side Effects/Idempotency 段 | L42-L725 | 影响所有 endpoint | 若新增 endpoint 需补齐 Side Effects/Idempotency |
| DDL-001 | DDL 仅来源于 PO（含继承） | PASS | docs/01-12/04-data-model.md | DDL 未生成说明 | L69-L70 | 影响 DDL 输出 | 若新增 DDL 必须引用 PO 证据 |
| DDL-002 | 包含“非MYSQL表”的 PO 全部被排除 | PASS | docs/01-12/01-architecture-overview.md | DDL 判定块 Excluded=无 | L3-L13 | 影响 DDL 输出 | 若出现标记需在 DDL 判定块列为 Excluded |
| DDL-003 | 每个 DDL 字段包含注释来源且格式正确 | FAIL | docs/01-12/04-data-model.md | DDL 未生成 | L69-L70 | 影响全部表 | 补齐 PO 字段的列类型与注释来源证据后生成 DDL |
| DDL-004 | 无 infer 约束输出 | PASS | docs/01-12/04-data-model.md | 未输出 DDL 约束 | L69-L70 | 影响 DDL 输出 | 继续保持仅输出有证据的约束 |
| INH-001 | 继承父类字段已展开并记录继承链证据 | PASS | docs/01-12/04-data-model.md | 未发现继承 | L71-L71 | 影响 PO 继承 | 若出现继承需记录展开链 |
| UNK-001 | UNKNOWN/ASSUMED 清单在每份文档中存在 | PASS | docs/01-12/01-architecture-overview.md | UNKNOWN/ASSUMED 章节 | L76-L80；docs/01-12/02-api-reference.md | UNKNOWN/ASSUMED 章节 | L736-L740；docs/01-12/03-domain-model.md | UNKNOWN/ASSUMED 章节 | L95-L98；docs/01-12/04-data-model.md | UNKNOWN/ASSUMED 章节 | L81-L84；docs/01-12/05-key-flows.md | UNKNOWN/ASSUMED 章节 | L50-L52；docs/01-12/06-prd-lite.md | UNKNOWN/ASSUMED 章节 | L44-L47；docs/01-12/07-iteration-backlog.md | UNKNOWN/ASSUMED 章节 | L26-L28；docs/01-12/08-test-plan.md | UNKNOWN/ASSUMED 章节 | L53-L55；docs/01-12/09-runbook.md | UNKNOWN/ASSUMED 章节 | L27-L30；docs/01-12/11-prompt-pack.md | UNKNOWN/ASSUMED 章节 | L47-L48；docs/01-12/12-self-verification-report.md | UNKNOWN/ASSUMED 章节 | L30-L31 | 影响 01–12 | 若新增 UNKNOWN/ASSUMED 需落到各文件末尾 |
| PACK-001 | 11-prompt-pack.md 含增量更新模板与回归检查要求 | PASS | docs/01-12/11-prompt-pack.md | 模板与检查清单 | L7-L38 | 影响文档迭代 | 若模板变更需同步自证 |
| DDL-005 | 表名必须来自 PO 显式声明 | FAIL | docs/01-12/04-data-model.md | DDL 未生成原因（缺表名映射） | L69-L70 | 影响全部表 | 在 PO 中增加表名注解或显式映射证据 |
| DDL-006 | 字段 SQL 类型必须来自 PO 明示信息 | FAIL | docs/01-12/04-data-model.md | DDL 未生成原因（缺列类型） | L69-L70 | 影响全部字段 | 在 PO 字段提供列类型或映射规则证据 |

## Evidence
- AGENTS.docs.v3.md | Self-Verification Rules | 自证报告规则 | L120-L142

## UNKNOWN/ASSUMED
- 无。
