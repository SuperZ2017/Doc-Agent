package com.example.noteagent.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "任务状态")
public enum TaskStatus {

    // 已创建。任务 Session 已创建但尚未读取素材。
    CREATED,

    // 素材已加载。Markdown 文件已通过路径校验并读取完成。
    MATERIALS_LOADED,

    // 大纲已生成。Agent 已生成结构化大纲和 Markdown 大纲。
    OUTLINE_GENERATED,

    // 等待确认大纲。系统暂停，等待用户确认或修改大纲。
    WAITING_OUTLINE_CONFIRMATION,

    // 正在生成笔记。用户已确认大纲，Agent 正在生成最终 Markdown。
    GENERATING_NOTE,

    // 正在校验。最终 Markdown 已生成，正在进行质量校验。
    VALIDATING,

    // 已完成。最终笔记和校验报告已输出。
    COMPLETED,

    // 已失败。任务执行过程中出现不可恢复错误。
    FAILED
}
