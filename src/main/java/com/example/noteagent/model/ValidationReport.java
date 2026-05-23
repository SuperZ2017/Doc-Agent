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
public class ValidationReport {
    private boolean passed;
    private int score;
    @Builder.Default
    private List<ValidationIssue> issues = new ArrayList<>();
    @Builder.Default
    private List<String> outlineCoverage = new ArrayList<>();
    @Builder.Default
    private List<String> missingSections = new ArrayList<>();
    @Builder.Default
    private List<String> duplicatedParts = new ArrayList<>();
    @Builder.Default
    private List<String> hallucinationRisks = new ArrayList<>();
    @Builder.Default
    private List<String> improvementSuggestions = new ArrayList<>();
}
