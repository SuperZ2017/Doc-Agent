package com.example.noteagent.agent;

import com.example.noteagent.model.MaterialDocument;
import com.example.noteagent.model.MergePlan;
import com.example.noteagent.model.NoteMode;
import com.example.noteagent.model.ValidationReport;
import com.example.noteagent.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationAgent {

    private final AgentInvoker agentInvoker;
    private final AgentJsonParser jsonParser;
    private final ObjectMapper objectMapper;

    public ValidationReport validate(NoteMode mode,
                                     String confirmedOutlineMarkdown,
                                     String finalMarkdown,
                                     List<MaterialDocument> materials,
                                     MaterialDocument existingNote,
                                     MergePlan mergePlan) {
        String prompt = """
                请校验最终技术笔记。只输出严格 JSON，不要使用 Markdown 代码块。

                JSON 字段必须匹配：
                {
                  "passed": true,
                  "score": 0,
                  "issues": [
                    {
                      "severity": "LOW | MEDIUM | HIGH",
                      "type": "OUTLINE_MISMATCH | DUPLICATION | MISSING_CONTENT | UNSUPPORTED_CLAIM | STYLE_PROBLEM | MARKDOWN_FORMAT_PROBLEM",
                      "message": "string",
                      "location": "string",
                      "suggestion": "string"
                    }
                  ],
                  "outlineCoverage": ["string"],
                  "missingSections": ["string"],
                  "duplicatedParts": ["string"],
                  "hallucinationRisks": ["string"],
                  "improvementSuggestions": ["string"]
                }

                模式：%s

                用户确认的大纲：
                <outline>
                %s
                </outline>

                最终 Markdown：
                <final-markdown>
                %s
                </final-markdown>

                素材：
                %s

                增量上下文：
                %s
                """.formatted(mode, confirmedOutlineMarkdown, finalMarkdown, renderMaterials(materials),
                renderIncrementalContext(existingNote, mergePlan));

        String response = agentInvoker.invoke("ValidationAgent", AgentPrompts.VALIDATION, prompt);
        return jsonParser.parseOrRepair("ValidationAgent", response, ValidationReport.class);
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

    private String renderIncrementalContext(MaterialDocument existingNote, MergePlan mergePlan) {
        if (existingNote == null) {
            return "";
        }
        return """
                <existing-note path="%s">
                %s
                </existing-note>

                <merge-plan>
                %s
                </merge-plan>
                """.formatted(existingNote.getPath(), existingNote.getRawMarkdown(), toJson(mergePlan));
    }

    private String toJson(Object value) {
        return JsonUtils.toPrettyJson(objectMapper, value);
    }
}
