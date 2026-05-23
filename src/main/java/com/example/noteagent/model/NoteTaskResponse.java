package com.example.noteagent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteTaskResponse {
    private String taskId;
    private NoteMode mode;
    private TaskStatus status;
    private NoteOutline outline;
    private String outlineMarkdown;
    private MergePlan mergePlan;
    private String finalNotePath;
    private String validationReportPath;
    private ValidationReport validationReport;
    private String errorMessage;
    private String finalNoteMarkdown;
}
