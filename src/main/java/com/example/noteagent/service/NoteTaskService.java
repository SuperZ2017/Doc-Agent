package com.example.noteagent.service;

import com.example.noteagent.config.NoteAgentProperties;
import com.example.noteagent.exception.ResourceNotFoundException;
import com.example.noteagent.exception.TaskConflictException;
import com.example.noteagent.model.MaterialAnalysis;
import com.example.noteagent.model.MaterialDocument;
import com.example.noteagent.model.MergePlanningResult;
import com.example.noteagent.model.NoteMode;
import com.example.noteagent.model.NoteTaskRequest;
import com.example.noteagent.model.NoteTaskResponse;
import com.example.noteagent.model.NoteTaskSession;
import com.example.noteagent.model.OutlineConfirmRequest;
import com.example.noteagent.model.OutlineGenerationResult;
import com.example.noteagent.model.TaskStatus;
import com.example.noteagent.model.ValidationReport;
import com.example.noteagent.repository.InMemoryNoteTaskRepository;
import com.example.noteagent.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteTaskService {

    private final InMemoryNoteTaskRepository repository;
    private final MarkdownMaterialService markdownMaterialService;
    private final OutlineGenerationService outlineGenerationService;
    private final NoteGenerationService noteGenerationService;
    private final NoteValidationService noteValidationService;
    private final FileOutputService fileOutputService;
    private final NoteAgentProperties noteAgentProperties;
    private final ObjectMapper objectMapper;

    public NoteTaskResponse createTask(NoteTaskRequest request) {
        NoteTaskSession session = NoteTaskSession.builder()
                .taskId(UUID.randomUUID().toString())
                .mode(request.getMode())
                .status(TaskStatus.CREATED)
                .request(request)
                .createdAt(Instant.now())
                .build();
        repository.save(session);

        try {
            List<MaterialDocument> materials = markdownMaterialService.loadMaterials(request.getMaterialPaths());
            session.setMaterials(materials);
            session.setStatus(TaskStatus.MATERIALS_LOADED);
            repository.save(session);

            MaterialAnalysis analysis = outlineGenerationService.analyzeMaterials(materials, request.getPreferences());
            session.setMaterialAnalysis(analysis);

            if (request.getMode() == NoteMode.ORGANIZE) {
                OutlineGenerationResult outlineResult = outlineGenerationService.generateOrganizeOutline(
                        analysis, materials, request.getPreferences());
                session.setOutline(outlineResult.getOutline());
                session.setOutlineMarkdown(outlineResult.getOutlineMarkdown());
            } else {
                MaterialDocument existingNote = markdownMaterialService.loadMarkdown(request.getExistingNotePath());
                session.setExistingNote(existingNote);
                MergePlanningResult planningResult = outlineGenerationService.generateIncrementalPlan(
                        existingNote, materials, analysis, request.getPreferences());
                session.setOutline(planningResult.getOutline());
                session.setOutlineMarkdown(planningResult.getOutlineMarkdown());
                session.setMergePlan(planningResult.getMergePlan());
            }

            session.setStatus(TaskStatus.OUTLINE_GENERATED);
            repository.save(session);

            fileOutputService.writeOutline(session);
            fileOutputService.writeMergePlan(session);

            session.setStatus(TaskStatus.WAITING_OUTLINE_CONFIRMATION);
            repository.save(session);
            return toResponse(session, false);
        } catch (RuntimeException ex) {
            session.setStatus(TaskStatus.FAILED);
            session.setErrorMessage(ex.getMessage());
            repository.save(session);
            throw ex;
        }
    }

    public NoteTaskResponse confirmOutline(String taskId, OutlineConfirmRequest request) {
        NoteTaskSession session = getSession(taskId);
        if (session.getStatus() != TaskStatus.WAITING_OUTLINE_CONFIRMATION) {
            throw new TaskConflictException("Task is not waiting for outline confirmation");
        }
        if (!Boolean.TRUE.equals(request.getApproved())) {
            throw new TaskConflictException("Outline must be approved before generating the final note");
        }

        String confirmedOutline = StringUtils.hasText(request.getOutlineMarkdown())
                ? request.getOutlineMarkdown()
                : session.getOutlineMarkdown();
        if (!StringUtils.hasText(confirmedOutline)) {
            throw new TaskConflictException("No outline is available to confirm");
        }

        try {
            session.setConfirmedOutlineMarkdown(confirmedOutline);
            session.setStatus(TaskStatus.GENERATING_NOTE);
            repository.save(session);

            String finalMarkdown = noteGenerationService.generate(session, confirmedOutline);
            session.setFinalNoteMarkdown(finalMarkdown);

            session.setStatus(TaskStatus.VALIDATING);
            repository.save(session);

            ValidationReport validationReport = noteValidationService.validate(session, confirmedOutline, finalMarkdown);
            int fixes = 0;
            while (shouldAutoFix(validationReport, fixes)) {
                String validationReportJson = JsonUtils.toPrettyJson(objectMapper, validationReport);
                finalMarkdown = noteGenerationService.repair(session, finalMarkdown, confirmedOutline, validationReportJson);
                session.setFinalNoteMarkdown(finalMarkdown);
                validationReport = noteValidationService.validate(session, confirmedOutline, finalMarkdown);
                fixes++;
            }

            session.setValidationReport(validationReport);
            Path finalPath = fileOutputService.writeFinalNote(session, finalMarkdown);
            Path validationPath = fileOutputService.writeValidationReport(session);
            fileOutputService.writeMergePlan(session);
            session.setFinalNotePath(finalPath.toString());
            session.setValidationReportPath(validationPath.toString());
            session.setStatus(TaskStatus.COMPLETED);
            repository.save(session);
            return toResponse(session, true);
        } catch (RuntimeException ex) {
            session.setStatus(TaskStatus.FAILED);
            session.setErrorMessage(ex.getMessage());
            repository.save(session);
            throw ex;
        }
    }

    public NoteTaskResponse getTask(String taskId) {
        return toResponse(getSession(taskId), true);
    }

    private boolean shouldAutoFix(ValidationReport report, int fixes) {
        return noteAgentProperties.getValidation().isAutoFixEnabled()
                && fixes < noteAgentProperties.getValidation().getMaxAutoFixCount()
                && report != null
                && !report.isPassed()
                && noteValidationService.hasHighSeverityIssue(report);
    }

    private NoteTaskSession getSession(String taskId) {
        return repository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    }

    private NoteTaskResponse toResponse(NoteTaskSession session, boolean includeFinalMarkdown) {
        return NoteTaskResponse.builder()
                .taskId(session.getTaskId())
                .mode(session.getMode())
                .status(session.getStatus())
                .outline(session.getOutline())
                .outlineMarkdown(session.getOutlineMarkdown())
                .mergePlan(session.getMergePlan())
                .finalNotePath(session.getFinalNotePath())
                .validationReportPath(session.getValidationReportPath())
                .validationReport(session.getValidationReport())
                .errorMessage(session.getErrorMessage())
                .finalNoteMarkdown(includeFinalMarkdown ? session.getFinalNoteMarkdown() : null)
                .build();
    }
}
