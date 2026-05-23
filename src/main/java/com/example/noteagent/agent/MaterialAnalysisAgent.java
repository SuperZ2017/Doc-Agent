package com.example.noteagent.agent;

import com.example.noteagent.model.MaterialAnalysis;
import com.example.noteagent.model.MaterialDocument;
import com.example.noteagent.model.NotePreferences;
import com.example.noteagent.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaterialAnalysisAgent {

    private final AgentInvoker agentInvoker;
    private final AgentJsonParser jsonParser;
    private final ObjectMapper objectMapper;

    public MaterialAnalysis analyze(List<MaterialDocument> materials, NotePreferences preferences) {
        String prompt = """
                请分析下面的 Markdown 学习素材，并只输出严格 JSON，不要使用 Markdown 代码块。

                JSON 字段必须匹配：
                {
                  "inferredTopic": "string",
                  "coreConcepts": ["string"],
                  "importantDetails": ["string"],
                  "codeExampleSummaries": ["string"],
                  "duplicateCandidates": ["string"],
                  "unclearParts": ["string"],
                  "missingButImportantParts": ["string"],
                  "summary": "string"
                }

                写作偏好：
                %s

                素材：
                %s
                """.formatted(toJson(preferences), renderMaterials(materials));

        String response = agentInvoker.invoke("MaterialAnalysisAgent", AgentPrompts.MATERIAL_ANALYSIS, prompt);
        return jsonParser.parseOrRepair("MaterialAnalysisAgent", response, MaterialAnalysis.class);
    }

    private String renderMaterials(List<MaterialDocument> materials) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < materials.size(); i++) {
            MaterialDocument material = materials.get(i);
            builder.append("\n\n<material index=\"").append(i + 1).append("\" path=\"")
                    .append(material.getPath()).append("\">\n")
                    .append(material.getRawMarkdown())
                    .append("\n</material>");
        }
        return builder.toString();
    }

    private String toJson(Object value) {
        return JsonUtils.toPrettyJson(objectMapper, value);
    }
}
