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
@Schema(description = "Markdown 素材分析结果")
public class MaterialAnalysis {

    @Schema(description = "推断主题。根据多份素材综合判断出的学习主题", example = "Liquibase")
    private String inferredTopic;

    @Schema(description = "核心概念。素材中反复出现或对理解主题最关键的概念")
    @Builder.Default
    private List<String> coreConcepts = new ArrayList<>();

    @Schema(description = "重要细节。整理最终笔记时应该保留的配置、步骤、注意事项或结论")
    @Builder.Default
    private List<String> importantDetails = new ArrayList<>();

    @Schema(description = "代码示例摘要。对素材中代码块用途的简要说明，便于后续决定是否保留")
    @Builder.Default
    private List<String> codeExampleSummaries = new ArrayList<>();

    @Schema(description = "重复候选。多份素材中表达重复或高度相似的内容")
    @Builder.Default
    private List<String> duplicateCandidates = new ArrayList<>();

    @Schema(description = "不清楚的部分。素材表达含糊、上下文不足或需要用户确认的内容")
    @Builder.Default
    private List<String> unclearParts = new ArrayList<>();

    @Schema(description = "重要但缺失的部分。对理解主题有帮助但素材中未提供的信息，只能标记不能编造")
    @Builder.Default
    private List<String> missingButImportantParts = new ArrayList<>();

    @Schema(description = "分析摘要。对全部素材内容和整理方向的整体说明")
    private String summary;
}
