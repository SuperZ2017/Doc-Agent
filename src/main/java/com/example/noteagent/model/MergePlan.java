package com.example.noteagent.model;

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
public class MergePlan {
    private String existingNoteSummary;
    @Builder.Default
    private List<MergePlanItem> items = new ArrayList<>();
    @Builder.Default
    private List<String> ignoredMaterials = new ArrayList<>();
    @Builder.Default
    private List<String> risks = new ArrayList<>();
}
