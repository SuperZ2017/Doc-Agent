package com.example.noteagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "大纲确认请求")
public class OutlineConfirmRequest {

    @Schema(description = "是否确认大纲。必须为 true 才会开始生成最终笔记", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull
    private Boolean approved;

    @Schema(description = "用户修改后的大纲 Markdown。为空时使用系统生成的大纲")
    private String outlineMarkdown;
}
