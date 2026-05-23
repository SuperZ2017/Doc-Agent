package com.example.noteagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "任务 Session，当前版本存储在内存中")
public class NoteTaskSession {

    @Schema(description = "任务 ID。Session 的唯一标识")
    private String taskId;

    @Schema(description = "任务模式。说明当前任务是整理模式还是增量更新模式")
    private NoteMode mode;

    @Schema(description = "任务状态。表示任务当前所处阶段")
    private TaskStatus status;

    @Schema(description = "原始请求。创建任务时传入的请求参数")
    private NoteTaskRequest request;

    @Schema(description = "新增素材列表。已经读取并解析后的 Markdown 素材")
    @Builder.Default
    private List<MaterialDocument> materials = new ArrayList<>();

    @Schema(description = "已有笔记。增量更新模式下读取的原始笔记")
    private MaterialDocument existingNote;

    @Schema(description = "素材分析。对新增 Markdown 素材的结构化分析结果")
    private MaterialAnalysis materialAnalysis;

    @Schema(description = "系统生成的大纲。等待用户确认的大纲对象")
    private NoteOutline outline;

    @Schema(description = "系统生成的大纲 Markdown。等待用户确认的大纲文本")
    private String outlineMarkdown;

    @Schema(description = "用户确认的大纲 Markdown。可能是系统生成版本，也可能是用户修改后的版本")
    private String confirmedOutlineMarkdown;

    @Schema(description = "合并计划。增量更新模式下新增素材如何合入已有笔记的计划")
    private MergePlan mergePlan;

    @Schema(description = "最终 Markdown 内容。生成并校验后的完整技术笔记")
    private String finalNoteMarkdown;

    @Schema(description = "最终笔记路径。最终 Markdown 文件写入的位置")
    private String finalNotePath;

    @Schema(description = "校验报告路径。validation-report.json 文件写入的位置")
    private String validationReportPath;

    @Schema(description = "校验报告。最终笔记质量校验结果")
    private ValidationReport validationReport;

    @Schema(description = "错误信息。任务失败时记录的异常信息")
    private String errorMessage;

    @Schema(description = "创建时间。Session 被创建的时间点")
    private Instant createdAt;

    @Schema(description = "更新时间。Session 最近一次状态或内容变化的时间点")
    private Instant updatedAt;
}
