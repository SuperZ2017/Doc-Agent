package com.example.noteagent.service;

import com.example.noteagent.config.WorkspaceProperties;
import com.example.noteagent.exception.BadRequestException;
import com.example.noteagent.model.MaterialDocument;
import com.example.noteagent.util.MarkdownUtils;
import com.example.noteagent.util.PathSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkdownMaterialService {

    private final PathSecurityUtils pathSecurityUtils;
    private final WorkspaceProperties workspaceProperties;

    public List<MaterialDocument> loadMaterials(List<String> paths) {
        return paths.stream()
                .map(this::loadMarkdown)
                .toList();
    }

    public MaterialDocument loadMarkdown(String userPath) {
        Path path = pathSecurityUtils.resolveInputMarkdown(userPath);
        long maxBytes = workspaceProperties.getMaxFileSizeKb() * 1024;
        try {
            long size = Files.size(path);
            if (size > maxBytes) {
                throw new BadRequestException("FILE_TOO_LARGE",
                        "Markdown file exceeds max size " + workspaceProperties.getMaxFileSizeKb() + "KB: " + userPath);
            }
            String rawMarkdown = Files.readString(path, StandardCharsets.UTF_8);
            String fileName = path.getFileName().toString();
            List<String> headings = MarkdownUtils.extractHeadings(rawMarkdown);
            return MaterialDocument.builder()
                    .path(path.toString())
                    .fileName(fileName)
                    .title(MarkdownUtils.extractTitle(rawMarkdown, fileName))
                    .headings(headings)
                    .rawMarkdown(rawMarkdown)
                    .codeBlocks(MarkdownUtils.extractCodeBlocks(rawMarkdown))
                    .charCount(rawMarkdown.length())
                    .build();
        } catch (IOException ex) {
            throw new BadRequestException("FILE_READ_FAILED", "Failed to read markdown file: " + userPath);
        }
    }
}
