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
@Schema(description = "增量更新规划结果")
public class MergePlanningResult {

    @Schema(description = "更新后大纲。合并新增素材后建议采用的完整文章大纲")
    private NoteOutline outline;

    @Schema(description = "大纲 Markdown。用于展示和让用户确认的 Markdown 版本大纲")
    private String outlineMarkdown;

    @Schema(description = "合并计划。说明新增素材如何融入已有笔记")
    private MergePlan mergePlan;
}
