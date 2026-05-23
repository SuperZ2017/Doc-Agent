package com.example.noteagent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteTaskSession {
    private String taskId;
    private NoteMode mode;
    private TaskStatus status;
    private NoteTaskRequest request;
    @Builder.Default
    private List<MaterialDocument> materials = new ArrayList<>();
    private MaterialDocument existingNote;
    private MaterialAnalysis materialAnalysis;
    private NoteOutline outline;
    private String outlineMarkdown;
    private String confirmedOutlineMarkdown;
    private MergePlan mergePlan;
    private String finalNoteMarkdown;
    private String finalNotePath;
    private String validationReportPath;
    private ValidationReport validationReport;
    private String errorMessage;
    private Instant createdAt;
    private Instant updatedAt;
}
