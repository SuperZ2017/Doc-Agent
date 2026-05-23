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
@Schema(description = "校验问题")
public class ValidationIssue {

    @Schema(description = "严重级别。LOW、MEDIUM 或 HIGH，用于判断是否需要自动修复",
            allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private String severity;

    @Schema(description = "问题类型。描述校验发现的问题类别",
            allowableValues = {"OUTLINE_MISMATCH", "DUPLICATION", "MISSING_CONTENT", "UNSUPPORTED_CLAIM", "STYLE_PROBLEM", "MARKDOWN_FORMAT_PROBLEM"})
    private String type;

    @Schema(description = "问题描述。对具体问题的可读说明")
    private String message;

    @Schema(description = "问题位置。指出问题所在章节、标题或大致位置")
    private String location;

    @Schema(description = "修复建议。说明应该如何调整最终笔记")
    private String suggestion;
}
