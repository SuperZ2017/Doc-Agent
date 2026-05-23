package com.example.noteagent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建笔记任务请求")
public class NoteTaskRequest {

    @Schema(description = "任务模式。ORGANIZE 表示整理新笔记，INCREMENTAL_UPDATE 表示更新已有笔记", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private NoteMode mode;

    @Schema(description = "素材路径列表。必须至少包含一个 workspace.root 下的 Markdown 文件路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private List<String> materialPaths;

    @Schema(description = "已有笔记路径。仅增量更新模式必填，必须是 workspace.root 下的 Markdown 文件")
    private String existingNotePath;

    @Schema(description = "输出目录。为空时使用 workspace.output-dir，且必须位于 workspace.root 下")
    private String outputDir;

    @Schema(description = "输出文件名。为空时根据大纲标题自动生成 Markdown 文件名", example = "liquibase-note.md")
    private String outputFileName;

    @Schema(description = "写作偏好。用于影响大纲生成、正文写作和增量合并策略")
    @Valid
    private NotePreferences preferences;

    @JsonIgnore
    @AssertTrue(message = "existingNotePath is required for INCREMENTAL_UPDATE mode")
    public boolean isExistingNotePathValid() {
        return mode != NoteMode.INCREMENTAL_UPDATE || StringUtils.hasText(existingNotePath);
    }
}
