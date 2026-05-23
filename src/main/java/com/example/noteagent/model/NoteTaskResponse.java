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
@Schema(description = "笔记任务响应")
public class NoteTaskResponse {

    @Schema(description = "任务 ID。创建任务后生成的 UUID，用于确认大纲和查询状态")
    private String taskId;

    @Schema(description = "任务模式。说明当前任务是整理模式还是增量更新模式")
    private NoteMode mode;

    @Schema(description = "任务状态。用于表示任务当前执行到哪个阶段")
    private TaskStatus status;

    @Schema(description = "结构化大纲。Agent 生成或用户确认前展示的大纲对象")
    private NoteOutline outline;

    @Schema(description = "大纲 Markdown。用于用户阅读、确认或修改的大纲文本")
    private String outlineMarkdown;

    @Schema(description = "合并计划。仅增量更新模式返回，说明新增素材如何融入已有笔记")
    private MergePlan mergePlan;

    @Schema(description = "最终笔记路径。任务完成后生成的 Markdown 文件绝对路径")
    private String finalNotePath;

    @Schema(description = "校验报告路径。任务完成后生成的 validation-report.json 文件绝对路径")
    private String validationReportPath;

    @Schema(description = "校验报告。最终笔记的结构一致性、重复、缺失和风险检查结果")
    private ValidationReport validationReport;

    @Schema(description = "错误信息。任务失败时记录的错误原因")
    private String errorMessage;

    @Schema(description = "最终 Markdown。确认大纲并完成生成后返回的完整笔记内容")
    private String finalNoteMarkdown;
}
