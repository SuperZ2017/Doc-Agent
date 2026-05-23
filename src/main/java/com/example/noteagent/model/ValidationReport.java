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
@Schema(description = "笔记质量校验报告")
public class ValidationReport {

    @Schema(description = "是否通过校验。true 表示没有发现阻断性问题", example = "true")
    private boolean passed;

    @Schema(description = "校验分数。0 到 100 的质量评分", example = "95")
    private int score;

    @Schema(description = "问题列表。结构、内容、重复、事实依据和格式方面的具体问题")
    @Builder.Default
    private List<ValidationIssue> issues = new ArrayList<>();

    @Schema(description = "大纲覆盖情况。说明最终笔记对确认大纲的覆盖程度")
    @Builder.Default
    private List<String> outlineCoverage = new ArrayList<>();

    @Schema(description = "缺失章节。确认大纲中存在但最终笔记缺失或覆盖不足的章节")
    @Builder.Default
    private List<String> missingSections = new ArrayList<>();

    @Schema(description = "重复内容。最终笔记中明显重复或可合并的内容")
    @Builder.Default
    private List<String> duplicatedParts = new ArrayList<>();

    @Schema(description = "编造风险。可能缺少素材依据的具体断言或细节")
    @Builder.Default
    private List<String> hallucinationRisks = new ArrayList<>();

    @Schema(description = "改进建议。对最终笔记可读性、结构或内容完整性的优化建议")
    @Builder.Default
    private List<String> improvementSuggestions = new ArrayList<>();
}
