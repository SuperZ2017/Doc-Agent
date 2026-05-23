package com.example.noteagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "大纲章节")
public class OutlineSection {

    @Schema(description = "章节标题。最终 Markdown 中该章节的标题文本")
    private String heading;

    @Schema(description = "标题层级。Markdown 标题层级，通常从 2 开始", example = "2")
    private int level;

    @Schema(description = "章节目的。说明该章节为什么存在、要解决什么问题")
    private String purpose;

    @Schema(description = "关键要点。该章节正文应覆盖的知识点")
    @Builder.Default
    private List<String> keyPoints = new ArrayList<>();

    @Schema(description = "子章节。该章节下的更细粒度结构")
    @Builder.Default
    private List<OutlineSection> children = new ArrayList<>();
}
