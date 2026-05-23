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
@Schema(description = "技术笔记大纲")
public class NoteOutline {

    @Schema(description = "大纲标题。最终 Markdown 笔记建议使用的一级标题")
    private String title;

    @Schema(description = "大纲描述。说明这篇笔记的组织目标和整体写作方向")
    private String description;

    @Schema(description = "章节列表。一级标题下的主要章节结构")
    @Builder.Default
    private List<OutlineSection> sections = new ArrayList<>();
}
