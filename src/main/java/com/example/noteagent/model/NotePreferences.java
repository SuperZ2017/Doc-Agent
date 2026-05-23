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
@Schema(description = "笔记写作偏好")
public class NotePreferences {

    @Schema(description = "目标读者。用于控制解释深度，例如初学者、中级开发者或面试准备者", example = "初学者到中级开发者")
    private String audience;

    @Schema(description = "写作风格。用于控制最终笔记的表达形式，例如技术博客、复习笔记或面试总结", example = "技术博客")
    private String style;

    @Schema(description = "内容侧重点。描述更偏实战、原理、面试、配置说明或其他方向", example = "偏实战，保留关键概念")
    private String focus;

    @Schema(description = "是否保留代码示例。为 true 时会提醒 Agent 尽量保留关键代码块", example = "true")
    private Boolean keepCodeExamples;

    @Schema(description = "输出语言。用于控制最终笔记使用的语言", example = "zh-CN")
    private String language;

    @Schema(description = "篇幅要求。用于约束最终笔记的长短，例如精简、适中或详细", example = "适中")
    private String length;

    @Schema(description = "额外指令。用户对写作、保留内容或表达方式的补充要求")
    private String extraInstructions;

}
