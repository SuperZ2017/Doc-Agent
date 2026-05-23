package com.example.noteagent.agent;

import com.example.noteagent.model.MaterialDocument;
import com.example.noteagent.model.MergePlan;
import com.example.noteagent.model.NoteMode;
import com.example.noteagent.model.NotePreferences;
import com.example.noteagent.util.JsonUtils;
import com.example.noteagent.util.MarkdownUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NoteWriterAgent {

    private final AgentInvoker agentInvoker;
    private final ObjectMapper objectMapper;

    public String write(NoteMode mode,
                        String confirmedOutlineMarkdown,
                        List<MaterialDocument> materials,
                        MaterialDocument existingNote,
                        MergePlan mergePlan,
                        NotePreferences preferences) {
        String prompt = """
                模式：%s

                用户确认的大纲：
                <outline>
                %s
                </outline>

                写作偏好：
                %s

                %s

                素材：
                %s

                请只输出最终 Markdown 正文。
                """.formatted(
                mode,
                confirmedOutlineMarkdown,
                toJson(preferences),
                renderIncrementalContext(existingNote, mergePlan),
                renderMaterials(materials)
        );

        String response = agentInvoker.invoke("NoteWriterAgent", AgentPrompts.NOTE_WRITER, prompt);
        return MarkdownUtils.unwrapMarkdownFence(response);
    }

    public String repair(String finalMarkdown,
                         String confirmedOutlineMarkdown,
                         String validationReportJson,
                         List<MaterialDocument> materials) {
        String prompt = """
                用户确认的大纲：
                <outline>
                %s
                </outline>

                校验报告：
                <validation-report>
                %s
                </validation-report>

                原始素材：
                %s

                需要修复的最终 Markdown：
                <final-markdown>
                %s
                </final-markdown>
                """.formatted(confirmedOutlineMarkdown, validationReportJson, renderMaterials(materials), finalMarkdown);

        String response = agentInvoker.invoke("NoteRepairAgent", AgentPrompts.AUTO_FIX, prompt);
        return MarkdownUtils.unwrapMarkdownFence(response);
    }

    private String renderIncrementalContext(MaterialDocument existingNote, MergePlan mergePlan) {
        if (existingNote == null) {
            return "";
        }
        return """
                已有笔记：
                <existing-note path="%s">
                %s
                </existing-note>

                合并计划：
                %s
                """.formatted(existingNote.getPath(), existingNote.getRawMarkdown(), toJson(mergePlan));
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
