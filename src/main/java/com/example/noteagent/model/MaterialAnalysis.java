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
public class MaterialAnalysis {
    private String inferredTopic;
    @Builder.Default
    private List<String> coreConcepts = new ArrayList<>();
    @Builder.Default
    private List<String> importantDetails = new ArrayList<>();
    @Builder.Default
    private List<String> codeExampleSummaries = new ArrayList<>();
    @Builder.Default
    private List<String> duplicateCandidates = new ArrayList<>();
    @Builder.Default
    private List<String> unclearParts = new ArrayList<>();
    @Builder.Default
    private List<String> missingButImportantParts = new ArrayList<>();
    private String summary;
}
