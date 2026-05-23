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
public class MergePlanItem {
    private String sourceMaterial;
    private String action;
    private String targetSection;
    private String reason;
    @Builder.Default
    private List<String> keyPoints = new ArrayList<>();
}
