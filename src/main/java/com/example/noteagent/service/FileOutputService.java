package com.example.noteagent.service;

import com.example.noteagent.model.NoteMode;
import com.example.noteagent.model.NoteTaskSession;
import com.example.noteagent.util.JsonUtils;
import com.example.noteagent.util.MarkdownUtils;
import com.example.noteagent.util.PathSecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class FileOutputService {

    private final PathSecurityUtils pathSecurityUtils;
    private final ObjectMapper objectMapper;

    public Path writeOutline(NoteTaskSession session) {
        Path outputDir = outputDir(session);
        return writeString(outputDir.resolve("outline.md"), session.getOutlineMarkdown());
    }

    public Path writeFinalNote(NoteTaskSession session, String finalMarkdown) {
        Path outputDir = outputDir(session);
        String requestedName = session.getRequest().getOutputFileName();
        String defaultName = defaultFinalNoteName(session);
        Path output = pathSecurityUtils.resolveOutputFile(outputDir,
                StringUtils.hasText(requestedName) ? requestedName : defaultName);
        return writeString(output, finalMarkdown);
    }

    public Path writeValidationReport(NoteTaskSession session) {
        Path outputDir = outputDir(session);
        String json = JsonUtils.toPrettyJson(objectMapper, session.getValidationReport());
        return writeString(outputDir.resolve("validation-report.json"), json);
    }

    public Path writeMergePlan(NoteTaskSession session) {
        if (session.getMode() != NoteMode.INCREMENTAL_UPDATE || session.getMergePlan() == null) {
            return null;
        }
        Path outputDir = outputDir(session);
        String json = JsonUtils.toPrettyJson(objectMapper, session.getMergePlan());
        return writeString(outputDir.resolve("merge-plan.json"), json);
    }

    private Path outputDir(NoteTaskSession session) {
        Path outputDir = pathSecurityUtils.resolveOutputDir(session.getRequest().getOutputDir());
        try {
            Files.createDirectories(outputDir);
            return outputDir;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create output directory: " + outputDir, ex);
        }
    }

    private Path writeString(Path path, String content) {
        try {
            Files.writeString(path, content == null ? "" : content, StandardCharsets.UTF_8);
            return path;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write output file: " + path, ex);
        }
    }

    private String defaultFinalNoteName(NoteTaskSession session) {
        String title = session.getOutline() == null ? "technical-note" : session.getOutline().getTitle();
        String suffix = session.getMode() == NoteMode.INCREMENTAL_UPDATE ? "-updated.md" : ".md";
        return MarkdownUtils.slugify(title) + suffix;
    }
}
