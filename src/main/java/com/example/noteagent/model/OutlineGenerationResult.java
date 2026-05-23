package com.example.noteagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "大纲生成结果")
public class OutlineGenerationResult {

    @Schema(description = "结构化大纲。由 OutlineAgent 生成的章节结构")
    private NoteOutline outline;

    @Schema(description = "大纲 Markdown。用于展示给用户确认的大纲文本")
    private String outlineMarkdown;
}
