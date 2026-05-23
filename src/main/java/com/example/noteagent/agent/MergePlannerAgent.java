package com.example.noteagent.agent;

import com.example.noteagent.model.MaterialAnalysis;
import com.example.noteagent.model.MaterialDocument;
import com.example.noteagent.model.MergePlanningResult;
import com.example.noteagent.model.NotePreferences;
import com.example.noteagent.util.JsonUtils;
import com.example.noteagent.util.MarkdownUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MergePlannerAgent {

    private final AgentInvoker agentInvoker;
    private final AgentJsonParser jsonParser;
    private final ObjectMapper objectMapper;

    public MergePlanningResult plan(MaterialDocument existingNote,
                                    List<MaterialDocument> newMaterials,
                                    MaterialAnalysis newMaterialAnalysis,
                                    NotePreferences preferences) {
        String prompt = """
                请为增量更新生成更新后大纲和合并计划。只输出严格 JSON，不要写最终正文，不要使用 Markdown 代码块。

                JSON 字段必须匹配：
                {
                  "outline": {"title":"string","description":"string","sections":[]},
                  "outlineMarkdown": "# 更新后标题\\n\\n## 章节...",
                  "mergePlan": {
                    "existingNoteSummary": "string",
                    "items": [
                      {
                        "sourceMaterial": "string",
                        "action": "MERGE_INTO_EXISTING_SECTION | ADD_TO_EXISTING_SECTION | CREATE_NEW_SECTION | IGNORE_DUPLICATE",
                        "targetSection": "string",
                        "reason": "string",
                        "keyPoints": ["string"]
                      }
                    ],
                    "ignoredMaterials": ["string"],
                    "risks": ["string"]
                  }
                }

                写作偏好：
                %s

                已有笔记：
                <existing-note path="%s">
                %s
                </existing-note>

                新增素材分析：
                %s

                新增素材：
                %s
                """.formatted(
                toJson(preferences),
                existingNote.getPath(),
                existingNote.getRawMarkdown(),
                toJson(newMaterialAnalysis),
                renderMaterials(newMaterials)
        );

        String response = agentInvoker.invoke("MergePlannerAgent", AgentPrompts.MERGE_PLANNER, prompt);
        MergePlanningResult result = jsonParser.parseOrRepair("MergePlannerAgent", response, MergePlanningResult.class);
        if (result.getOutlineMarkdown() == null && result.getOutline() != null) {
            result.setOutlineMarkdown(MarkdownUtils.outlineToMarkdown(result.getOutline()));
        }
        return result;
    }

    private String renderMaterials(List<MaterialDocument> materials) {
        StringBuilder builder = new StringBuilder();
        for (MaterialDocument material : materials) {
            builder.append("\n\n<material path=\"").append(material.getPath()).append("\">\n")
                    .append(material.getRawMarkdown())
                    .append("\n</material>");
        }
        return builder.toString();
    }

    private String toJson(Object value) {
        return JsonUtils.toPrettyJson(objectMapper, value);
    }
}
