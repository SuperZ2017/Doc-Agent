package com.example.noteagent.service;

import com.example.noteagent.agent.ValidationAgent;
import com.example.noteagent.model.NoteTaskSession;
import com.example.noteagent.model.ValidationIssue;
import com.example.noteagent.model.ValidationReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteValidationService {

    private final ValidationAgent validationAgent;

    public ValidationReport validate(NoteTaskSession session, String confirmedOutlineMarkdown, String finalMarkdown) {
        return validationAgent.validate(
                session.getMode(),
                confirmedOutlineMarkdown,
                finalMarkdown,
                session.getMaterials(),
                session.getExistingNote(),
                session.getMergePlan()
        );
    }

    public boolean hasHighSeverityIssue(ValidationReport report) {
        return report != null && report.getIssues() != null && report.getIssues().stream()
                .map(ValidationIssue::getSeverity)
                .anyMatch(severity -> "HIGH".equalsIgnoreCase(severity));
    }
}
