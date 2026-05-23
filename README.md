# 技术学习笔记整理 Agent

Spring Boot 3.x + Java 21 + Maven 项目，使用 AgentScope Java 的 `DashScopeChatModel` 调用 DashScope 大模型，把多份本地 Markdown 学习素材整理成一篇可长期沉淀的技术笔记。

当前版本只支持基于本地 `.md` 文件整理，不联网搜索，也不支持只输入主题自动生成文章。

## 技术栈

- Java 21
- Spring Boot 3.x
- Maven
- AgentScope Java `1.0.12`
- DashScope / Qwen
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Lombok：IDE 需要开启 annotation processing

## 项目架构设计

这个项目按典型 Spring Boot 分层实现，核心目标是把“Agent 能力”包在清晰的业务流程里，而不是让 Controller 直接拼 Prompt 或调用大模型。

```text
com.example.noteagent
  ├── controller     REST API 层，只负责接收请求和返回响应
  ├── service        业务编排层，负责任务状态流转、文件读写、生成和校验
  ├── agent          Agent 封装层，负责 Prompt、AgentScope 调用和 JSON 解析
  ├── model          请求、响应、Session、Outline、MergePlan、ValidationReport 等模型
  ├── repository     当前用内存保存任务 Session，后续可替换为数据库
  ├── config         DashScope、AgentScope、workspace 等配置
  ├── util           Markdown、路径安全、JSON 工具
  └── exception      全局异常和统一错误响应
```

主要调用链：

```text
NoteTaskController
  -> NoteTaskService
    -> MarkdownMaterialService
    -> MaterialAnalysisAgent
    -> OutlineAgent / MergePlannerAgent
    -> InMemoryNoteTaskRepository
    -> NoteWriterAgent
    -> ValidationAgent
    -> FileOutputService
```

这里的设计重点是：`NoteTaskService` 负责流程，`agent` 包里的类只负责“把某一步交给 LLM 做”。这样后续如果要换模型、换 AgentScope 用法、接数据库、接前端，都不需要重写整个流程。

## 核心实现思路

项目实现的是“先大纲、后正文、再校验”的工作流。用户创建任务时，系统只做到大纲生成，然后把任务状态置为 `WAITING_OUTLINE_CONFIRMATION`。只有用户确认大纲后，系统才会生成最终笔记。

`ORGANIZE` 模式流程：

```text
读取多份 Markdown
  -> 提取标题、正文、代码块
  -> 分析主题、知识点、重复内容、缺失信息
  -> 生成文章大纲
  -> 等待用户确认大纲
  -> 按确认后的大纲生成完整 Markdown
  -> 校验正文和大纲是否一致
  -> 必要时最多自动修复一次
  -> 输出 final note、outline.md、validation-report.json
```

`INCREMENTAL_UPDATE` 模式多了两步：先读取已有笔记，再由 `MergePlannerAgent` 判断新增素材应该合并到哪个章节、是否新建章节、是否忽略重复内容，并输出 `merge-plan.json`。

Session 状态由 `TaskStatus` 枚举表达：

```text
CREATED
MATERIALS_LOADED
OUTLINE_GENERATED
WAITING_OUTLINE_CONFIRMATION
GENERATING_NOTE
VALIDATING
COMPLETED
FAILED
```

这种状态机能避免一个常见问题：用户还没确认大纲，系统就直接生成正文。接口层也会在状态不合法时返回 `409 TASK_CONFLICT`。

## Agent 设计

当前实现把一个复杂任务拆成多个职责单一的 Agent：

- `MaterialAnalysisAgent`：分析输入素材，提取主题、核心概念、重要细节、代码示例、重复候选和不清楚的部分。
- `OutlineAgent`：根据素材分析结果生成自然的大纲，不套固定模板。
- `MergePlannerAgent`：只用于增量更新，规划新增素材如何融入已有笔记。
- `NoteWriterAgent`：按用户确认后的大纲生成最终 Markdown；增量模式下输出更新后的完整笔记。
- `ValidationAgent`：校验最终笔记是否覆盖大纲、是否重复、是否有疑似编造、Markdown 层级是否合理。
- `AgentJsonParser`：负责解析 Agent 返回的严格 JSON；解析失败时调用一次 JSON 修复 Agent。

这些 Agent 类本身不直接管理任务状态，也不写文件。它们只接收输入上下文，返回结构化结果或 Markdown 字符串。

## AgentScope Java 用法

本项目用到的 AgentScope Java 组件集中在两个类里：

- `AgentScopeConfig`
- `AgentScopeAgentInvoker`

`AgentScopeConfig` 负责创建 DashScope 模型：

```java
GenerateOptions options = GenerateOptions.builder()
        .temperature(properties.getTemperature())
        .maxTokens(properties.getMaxTokens())
        .build();

return DashScopeChatModel.builder()
        .apiKey(properties.getApiKey())
        .modelName(properties.getModelName())
        .enableSearch(false)
        .defaultOptions(options)
        .build();
```

这里用到的 AgentScope / Model 相关类：

- `io.agentscope.core.model.DashScopeChatModel`：DashScope 聊天模型实现。
- `io.agentscope.core.model.GenerateOptions`：配置温度、最大 token 等生成参数。
- `io.agentscope.core.model.Model`：模型抽象，业务代码只依赖这个接口。

`AgentScopeAgentInvoker` 负责把 system prompt 和 user prompt 交给 AgentScope Agent 执行：

```java
ReActAgent agent = ReActAgent.builder()
        .name(agentName)
        .sysPrompt(systemPrompt)
        .model(dashScopeChatModel)
        .maxIters(3)
        .build();

Msg response = agent.call(Msg.builder()
        .textContent(userPrompt)
        .build()).block();
```

这里用到的 AgentScope Agent 相关类：

- `io.agentscope.core.ReActAgent`：当前实际执行 LLM 调用的 Agent。
- `io.agentscope.core.message.Msg`：AgentScope 的消息对象，用来传入用户内容和读取返回文本。

项目里额外定义了一个 `AgentInvoker` 接口：

```java
public interface AgentInvoker {
    String invoke(String agentName, String systemPrompt, String userPrompt);
}
```

这么做有两个好处：

- 业务 Agent 不直接依赖 AgentScope 细节，只关心“给 prompt，拿结果”。
- 测试时可以注入 `FakeAgentInvoker`，不用真实调用 DashScope，也不需要配置真实 API Key。

## Prompt 和结构化输出策略

所有系统 Prompt 放在 `AgentPrompts` 中，方便以后统一调整。当前没有强依赖 AgentScope 的结构化输出能力，而是要求模型返回严格 JSON，再用 Jackson 解析为 Java 对象。

解析策略：

```text
LLM 原始输出
  -> JsonUtils.extractJsonObject
  -> ObjectMapper.readValue
  -> 如果失败，调用 JSON_REPAIR prompt 修复一次
  -> 再次解析
```

这种做法对初学阶段比较友好：先把 Agent 编排流程跑通，再逐步替换为更强的结构化输出能力。

## 适合重点阅读的源码

如果你刚开始学习 Agent 开发，建议按这个顺序看：

1. `NoteTaskController`：REST API 如何进入业务流程。
2. `NoteTaskService`：完整任务状态流转，最适合理解整体编排。
3. `MarkdownMaterialService`：本地 Markdown 如何读取和解析。
4. `AgentScopeConfig`：DashScopeChatModel 如何配置。
5. `AgentScopeAgentInvoker`：AgentScope 的 `ReActAgent` 如何被调用。
6. `MaterialAnalysisAgent`、`OutlineAgent`、`NoteWriterAgent`：如何为不同任务设计不同 Prompt。
7. `ValidationAgent` 和自动修复逻辑：如何让 Agent 参与质量控制。
8. `NoteTaskControllerIntegrationTest`：如何用 fake Agent 做集成测试。

## 配置 API Key

```bash
export DASHSCOPE_API_KEY=your_dashscope_api_key
export DASHSCOPE_MODEL=qwen-plus
export NOTE_AGENT_WORKSPACE=/workspace
export NOTE_AGENT_OUTPUT=/workspace/output
```

也可以只配置 `DASHSCOPE_API_KEY`，其余配置会使用 `application.yml` 默认值。

## 启动

```bash
mvn spring-boot:run
```

启动后访问：

```text
http://localhost:8080/swagger-ui.html
```

## 准备 Markdown 文件

所有输入文件必须在 `workspace.root` 下，且必须是 `.md` 后缀。

示例目录：

```text
/workspace
  /notes
    liquibase-1.md
    liquibase-changeset.md
    liquibase-springboot.md
    liquibase-rollback.md
    liquibase-note.md
  /output
```

## 创建整理任务

```bash
curl -X POST http://localhost:8080/api/note-tasks \
  -H "Content-Type: application/json" \
  -d '{
    "mode": "ORGANIZE",
    "materialPaths": [
      "/workspace/notes/liquibase-1.md",
      "/workspace/notes/liquibase-changeset.md",
      "/workspace/notes/liquibase-springboot.md"
    ],
    "outputDir": "/workspace/output",
    "outputFileName": "liquibase-note.md",
    "preferences": {
      "audience": "初学者到中级开发者",
      "style": "技术博客",
      "focus": "偏实战，保留关键概念",
      "keepCodeExamples": true,
      "language": "zh-CN"
    }
  }'
```

响应会返回 `taskId`、`outline` 和 `outlineMarkdown`，任务状态为 `WAITING_OUTLINE_CONFIRMATION`。

## 确认大纲

直接确认系统生成的大纲：

```bash
curl -X POST http://localhost:8080/api/note-tasks/{taskId}/outline/confirm \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true
  }'
```

提交修改后的大纲：

```bash
curl -X POST http://localhost:8080/api/note-tasks/{taskId}/outline/confirm \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "outlineMarkdown": "# Liquibase 学习笔记\n\n## 1. 核心概念\n\n## 2. Spring Boot 集成\n"
  }'
```

确认后会生成最终 Markdown、校验报告，并把任务置为 `COMPLETED`。

## 创建增量更新任务

```bash
curl -X POST http://localhost:8080/api/note-tasks \
  -H "Content-Type: application/json" \
  -d '{
    "mode": "INCREMENTAL_UPDATE",
    "existingNotePath": "/workspace/notes/liquibase-note.md",
    "materialPaths": [
      "/workspace/notes/liquibase-rollback.md"
    ],
    "outputDir": "/workspace/output",
    "outputFileName": "liquibase-note-updated.md",
    "preferences": {
      "style": "技术博客",
      "focus": "融入已有结构，避免重复",
      "keepCodeExamples": true,
      "language": "zh-CN"
    }
  }'
```

增量更新任务响应会额外返回 `mergePlan`。确认大纲后输出更新后的完整 Markdown，不输出 patch。

## 查询任务状态

```bash
curl http://localhost:8080/api/note-tasks/{taskId}
```

## 输出文件

输出目录由请求里的 `outputDir` 指定；如果为空，使用 `workspace.output-dir`。

输出文件：

- `outline.md`
- 用户指定的最终笔记文件名；未指定时根据大纲标题生成
- `validation-report.json`
- `merge-plan.json`，仅 `INCREMENTAL_UPDATE` 模式

## 当前限制

- 不支持联网搜索
- 不支持只输入主题自动学习或自动写文章
- 不支持非 `.md` 文件
- Session 使用内存存储，重启后任务状态会丢失
- 结构化输出当前通过“严格 JSON + Jackson 解析 + 一次 JSON 修复”实现
