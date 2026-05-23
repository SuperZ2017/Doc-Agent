package com.example.noteagent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MergePlanningResult {
    private NoteOutline outline;
    private String outlineMarkdown;
    private MergePlan mergePlan;
}
