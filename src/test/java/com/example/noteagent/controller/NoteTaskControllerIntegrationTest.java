package com.example.noteagent.controller;

import com.example.noteagent.agent.AgentInvoker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteTaskControllerIntegrationTest {

    private static final Path workspace;
    private static final Path notesDir;
    private static final Path outputDir;

    @Autowired
    private MockMvc mockMvc;

    static {
        try {
            workspace = Files.createTempDirectory("note-agent-test");
            notesDir = workspace.resolve("notes");
            outputDir = workspace.resolve("output");
            Files.createDirectories(notesDir);
            Files.createDirectories(outputDir);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @BeforeAll
    static void beforeAll() {
        assertThat(Files.exists(workspace)).isTrue();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("dashscope.api-key", () -> "test-key");
        registry.add("workspace.root", () -> workspace.toString());
        registry.add("workspace.output-dir", () -> outputDir.toString());
        registry.add("note-agent.validation.auto-fix-enabled", () -> "true");
    }

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(notesDir);
        Files.createDirectories(outputDir);
        Files.writeString(notesDir.resolve("liquibase-1.md"), """
                # Liquibase 是什么

                Liquibase 用于数据库变更管理。
                """, StandardCharsets.UTF_8);
        Files.writeString(notesDir.resolve("liquibase-changeset.md"), """
                # Changeset

                changeset 描述一次数据库变更。

                ```xml
                <changeSet id="1" author="dev"></changeSet>
                ```
                """, StandardCharsets.UTF_8);
        Files.writeString(notesDir.resolve("liquibase-note.md"), """
                # Liquibase 学习笔记

                ## 基础概念

                Liquibase 用于数据库变更管理。
                """, StandardCharsets.UTF_8);
        Files.writeString(notesDir.resolve("liquibase-rollback.md"), """
                # Rollback

                rollback 用于回滚数据库变更。
                """, StandardCharsets.UTF_8);
        Files.writeString(notesDir.resolve("not-markdown.txt"), "plain text", StandardCharsets.UTF_8);
    }

    @Test
    void createsOrganizeTaskSuccessfully() throws Exception {
        mockMvc.perform(post("/api/note-tasks")
                        .contentType("application/json")
                        .content(organizeRequest("liquibase-note.md")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING_OUTLINE_CONFIRMATION"))
                .andExpect(jsonPath("$.outlineMarkdown").exists())
                .andExpect(jsonPath("$.outline.title").value("Liquibase 学习笔记"));
    }

    @Test
    void rejectsIncrementalUpdateWithoutExistingNotePath() throws Exception {
        String request = """
                {
                  "mode": "INCREMENTAL_UPDATE",
                  "materialPaths": ["%s"]
                }
                """.formatted(notesDir.resolve("liquibase-rollback.md"));

        mockMvc.perform(post("/api/note-tasks")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void rejectsNonMarkdownFiles() throws Exception {
        String request = """
                {
                  "mode": "ORGANIZE",
                  "materialPaths": ["%s"],
                  "outputDir": "%s"
                }
                """.formatted(notesDir.resolve("not-markdown.txt"), outputDir);

        mockMvc.perform(post("/api/note-tasks")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_FILE_TYPE"));
    }

    @Test
    void rejectsPathTraversal() throws Exception {
        String request = """
                {
                  "mode": "ORGANIZE",
                  "materialPaths": ["../outside.md"],
                  "outputDir": "%s"
                }
                """.formatted(outputDir);

        mockMvc.perform(post("/api/note-tasks")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("UNSAFE_PATH"));
    }

    @Test
    void cannotGenerateFinalNoteWhenOutlineIsNotApproved() throws Exception {
        String taskId = createOrganizeTask();

        mockMvc.perform(post("/api/note-tasks/{taskId}/outline/confirm", taskId)
                        .contentType("application/json")
                        .content("""
                                {
                                  "approved": false
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("TASK_CONFLICT"));
    }

    @Test
    void confirmationGeneratesFinalResult() throws Exception {
        String taskId = createOrganizeTask();

        String response = mockMvc.perform(post("/api/note-tasks/{taskId}/outline/confirm", taskId)
                        .contentType("application/json")
                        .content("""
                                {
                                  "approved": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.finalNotePath").exists())
                .andExpect(jsonPath("$.validationReportPath").exists())
                .andExpect(jsonPath("$.finalNoteMarkdown").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).contains("validation-report.json");

        mockMvc.perform(get("/api/note-tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    private String createOrganizeTask() throws Exception {
        String response = mockMvc.perform(post("/api/note-tasks")
                        .contentType("application/json")
                        .content(organizeRequest("liquibase-note.md")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        int marker = response.indexOf("\"taskId\":\"");
        int start = marker + "\"taskId\":\"".length();
        int end = response.indexOf('"', start);
        return response.substring(start, end);
    }

    private String organizeRequest(String outputFileName) {
        return """
                {
                  "mode": "ORGANIZE",
                  "materialPaths": [
                    "%s",
                    "%s"
                  ],
                  "outputDir": "%s",
                  "outputFileName": "%s",
                  "preferences": {
                    "audience": "初学者到中级开发者",
                    "style": "技术博客",
                    "focus": "偏实战，保留关键概念",
                    "keepCodeExamples": true,
                    "language": "zh-CN"
                  }
                }
                """.formatted(
                notesDir.resolve("liquibase-1.md"),
                notesDir.resolve("liquibase-changeset.md"),
                outputDir,
                outputFileName
        );
    }

    @TestConfiguration
    static class FakeAgentConfig {
        @Bean
        @Primary
        AgentInvoker fakeAgentInvoker() {
            return (agentName, systemPrompt, userPrompt) -> {
                if (agentName.contains("MaterialAnalysisAgent")) {
                    return """
                            {
                              "inferredTopic": "Liquibase",
                              "coreConcepts": ["数据库变更管理", "changeset"],
                              "importantDetails": ["Liquibase 用于数据库变更管理"],
                              "codeExampleSummaries": ["包含 changeset XML 示例"],
                              "duplicateCandidates": [],
                              "unclearParts": [],
                              "missingButImportantParts": ["素材中未提供更多细节"],
                              "summary": "素材围绕 Liquibase 基础和 changeset。"
                            }
                            """;
                }
                if (agentName.contains("OutlineAgent")) {
                    return """
                            {
                              "outline": {
                                "title": "Liquibase 学习笔记",
                                "description": "整理 Liquibase 基础概念和 changeset。",
                                "sections": [
                                  {
                                    "heading": "Liquibase 的定位",
                                    "level": 2,
                                    "purpose": "说明 Liquibase 解决的问题",
                                    "keyPoints": ["数据库变更管理"],
                                    "children": []
                                  },
                                  {
                                    "heading": "Changeset",
                                    "level": 2,
                                    "purpose": "解释 changeset 的作用",
                                    "keyPoints": ["一次数据库变更", "保留代码示例"],
                                    "children": []
                                  }
                                ]
                              },
                              "outlineMarkdown": "# Liquibase 学习笔记\\n\\n## Liquibase 的定位\\n\\n## Changeset\\n"
                            }
                            """;
                }
                if (agentName.contains("MergePlannerAgent")) {
                    return """
                            {
                              "outline": {
                                "title": "Liquibase 学习笔记",
                                "description": "加入 rollback 内容。",
                                "sections": [
                                  {"heading":"基础概念","level":2,"purpose":"保留已有说明","keyPoints":["数据库变更管理"],"children":[]},
                                  {"heading":"Rollback","level":2,"purpose":"补充回滚能力","keyPoints":["rollback"],"children":[]}
                                ]
                              },
                              "outlineMarkdown": "# Liquibase 学习笔记\\n\\n## 基础概念\\n\\n## Rollback\\n",
                              "mergePlan": {
                                "existingNoteSummary": "已有笔记包含基础概念。",
                                "items": [
                                  {
                                    "sourceMaterial": "liquibase-rollback.md",
                                    "action": "CREATE_NEW_SECTION",
                                    "targetSection": "Rollback",
                                    "reason": "新增回滚知识点",
                                    "keyPoints": ["rollback"]
                                  }
                                ],
                                "ignoredMaterials": [],
                                "risks": []
                              }
                            }
                            """;
                }
                if (agentName.contains("ValidationAgent")) {
                    return """
                            {
                              "passed": true,
                              "score": 95,
                              "issues": [],
                              "outlineCoverage": ["已覆盖大纲"],
                              "missingSections": [],
                              "duplicatedParts": [],
                              "hallucinationRisks": [],
                              "improvementSuggestions": []
                            }
                            """;
                }
                if (agentName.contains("NoteWriterAgent") || agentName.contains("NoteRepairAgent")) {
                    return """
                            # Liquibase 学习笔记

                            ## Liquibase 的定位

                            Liquibase 用于数据库变更管理。

                            ## Changeset

                            changeset 描述一次数据库变更。

                            ```xml
                            <changeSet id="1" author="dev"></changeSet>
                            ```
                            """;
                }
                return "{}";
            };
        }
    }
}
