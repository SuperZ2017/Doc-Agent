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
@Schema(description = "单条素材合并计划")
public class MergePlanItem {

    @Schema(description = "来源素材。当前合并项对应的新增 Markdown 文件或素材名称")
    private String sourceMaterial;

    @Schema(description = "合并动作。说明新增内容应合并、补充、新建章节或忽略重复",
            allowableValues = {"MERGE_INTO_EXISTING_SECTION", "ADD_TO_EXISTING_SECTION", "CREATE_NEW_SECTION", "IGNORE_DUPLICATE"})
    private String action;

    @Schema(description = "目标章节。新增内容应该进入的已有章节或新建章节名称")
    private String targetSection;

    @Schema(description = "原因说明。解释为什么采取该合并动作")
    private String reason;

    @Schema(description = "关键要点。该素材应被保留或处理的主要知识点")
    @Builder.Default
    private List<String> keyPoints = new ArrayList<>();
}
