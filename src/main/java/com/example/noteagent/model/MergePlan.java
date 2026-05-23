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
@Schema(description = "增量更新合并计划")
public class MergePlan {

    @Schema(description = "已有笔记摘要。对 existingNotePath 指向笔记的结构和内容概述")
    private String existingNoteSummary;

    @Schema(description = "合并项列表。逐条说明新增素材应如何合入已有笔记")
    @Builder.Default
    private List<MergePlanItem> items = new ArrayList<>();

    @Schema(description = "忽略素材。因为重复、价值低或不适合合入而被忽略的素材说明")
    @Builder.Default
    private List<String> ignoredMaterials = new ArrayList<>();

    @Schema(description = "风险提示。增量合并时可能存在的信息缺口、重复风险或结构风险")
    @Builder.Default
    private List<String> risks = new ArrayList<>();
}
