package com.example.noteagent.agent;

import com.example.noteagent.model.MaterialAnalysis;
import com.example.noteagent.model.MaterialDocument;
import com.example.noteagent.model.NotePreferences;
import com.example.noteagent.model.OutlineGenerationResult;
import com.example.noteagent.util.JsonUtils;
import com.example.noteagent.util.MarkdownUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutlineAgent {

    private final AgentInvoker agentInvoker;
    private final AgentJsonParser jsonParser;
    private final ObjectMapper objectMapper;

    public OutlineGenerationResult generate(MaterialAnalysis analysis,
                                            List<MaterialDocument> materials,
                                            NotePreferences preferences) {
        String prompt = """
                请基于素材分析结果生成技术笔记大纲。只输出严格 JSON，不要输出正文，不要使用 Markdown 代码块。

                JSON 字段必须匹配：
                {
                  "outline": {
                    "title": "string",
                    "description": "string",
                    "sections": [
                      {
                        "heading": "string",
                        "level": 2,
                        "purpose": "string",
                        "keyPoints": ["string"],
                        "children": []
                      }
                    ]
                  },
                  "outlineMarkdown": "# 标题\\n\\n## 章节..."
                }

                写作偏好：
                %s

                素材分析：
                %s

                素材摘要：
                %s
                """.formatted(toJson(preferences), toJson(analysis), renderMaterialSummaries(materials));

        String response = agentInvoker.invoke("OutlineAgent", AgentPrompts.OUTLINE, prompt);
        OutlineGenerationResult result = jsonParser.parseOrRepair("OutlineAgent", response, OutlineGenerationResult.class);
        if (result.getOutlineMarkdown() == null && result.getOutline() != null) {
            result.setOutlineMarkdown(MarkdownUtils.outlineToMarkdown(result.getOutline()));
        }
        return result;
    }

    private String renderMaterialSummaries(List<MaterialDocument> materials) {
        StringBuilder builder = new StringBuilder();
        for (MaterialDocument material : materials) {
            builder.append("- ").append(material.getFileName())
                    .append("，标题：").append(material.getTitle())
                    .append("，标题列表：").append(material.getHeadings())
                    .append("，代码块数：").append(material.getCodeBlocks() == null ? 0 : material.getCodeBlocks().size())
                    .append("\n");
        }
        return builder.toString();
    }

    private String toJson(Object value) {
        return JsonUtils.toPrettyJson(objectMapper, value);
    }
}
