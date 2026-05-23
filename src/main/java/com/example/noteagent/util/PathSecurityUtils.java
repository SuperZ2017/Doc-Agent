package com.example.noteagent.util;

import com.example.noteagent.config.WorkspaceProperties;
import com.example.noteagent.exception.BadRequestException;
import com.example.noteagent.exception.ResourceNotFoundException;
import com.example.noteagent.exception.UnsafePathException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class PathSecurityUtils {

    private final WorkspaceProperties workspaceProperties;

    public Path resolveInputMarkdown(String userPath) {
        if (!StringUtils.hasText(userPath)) {
            throw new BadRequestException("EMPTY_PATH", "Markdown file path must not be empty");
        }
        ensureMarkdownSuffix(userPath);
        Path root = workspaceRoot();
        Path candidate = normalizeUnderRoot(root, userPath);
        if (!candidate.startsWith(root)) {
            throw new UnsafePathException("Path is outside configured workspace: " + userPath);
        }
        if (!Files.exists(candidate)) {
            throw new ResourceNotFoundException("Markdown file not found: " + userPath);
        }
        try {
            Path real = candidate.toRealPath(LinkOption.NOFOLLOW_LINKS);
            Path realRoot = root.toRealPath(LinkOption.NOFOLLOW_LINKS);
            if (!real.startsWith(realRoot)) {
                throw new UnsafePathException("Path is outside configured workspace: " + userPath);
            }
            if (Files.isSymbolicLink(real)) {
                throw new UnsafePathException("Symbolic links are not allowed for markdown input: " + userPath);
            }
            if (!Files.isRegularFile(real)) {
                throw new BadRequestException("NOT_A_FILE", "Path is not a file: " + userPath);
            }
            return real;
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Unable to access markdown file: " + userPath);
        }
    }

    public Path resolveOutputDir(String userOutputDir) {
        String dir = StringUtils.hasText(userOutputDir) ? userOutputDir : workspaceProperties.getOutputDir();
        Path root = workspaceRoot();
        Path outputDir = normalizeUnderRoot(root, dir);
        if (!outputDir.startsWith(root)) {
            throw new UnsafePathException("Output directory is outside configured workspace: " + dir);
        }
        if (Files.exists(outputDir)) {
            try {
                Path real = outputDir.toRealPath(LinkOption.NOFOLLOW_LINKS);
                Path realRoot = root.toRealPath(LinkOption.NOFOLLOW_LINKS);
                if (!real.startsWith(realRoot) || Files.isSymbolicLink(real)) {
                    throw new UnsafePathException("Output directory is outside configured workspace: " + dir);
                }
            } catch (IOException ex) {
                throw new UnsafePathException("Unable to validate output directory: " + dir);
            }
        }
        return outputDir;
    }

    public Path resolveOutputFile(Path outputDir, String fileName) {
        String safeName = MarkdownUtils.sanitizeFileName(fileName, "technical-note.md");
        Path output = outputDir.resolve(safeName).normalize();
        if (!output.startsWith(outputDir.normalize())) {
            throw new UnsafePathException("Output file is outside output directory: " + fileName);
        }
        return output;
    }

    public void ensureMarkdownSuffix(String path) {
        if (!path.toLowerCase(Locale.ROOT).endsWith(".md")) {
            throw new BadRequestException("UNSUPPORTED_FILE_TYPE", "Only .md files are supported: " + path);
        }
    }

    private Path workspaceRoot() {
        Path root = Path.of(workspaceProperties.getRoot()).toAbsolutePath().normalize();
        if (!Files.exists(root)) {
            throw new ResourceNotFoundException("Workspace root does not exist: " + root);
        }
        return root;
    }

    private Path normalizeUnderRoot(Path root, String path) {
        Path candidate = Path.of(path);
        if (!candidate.isAbsolute()) {
            candidate = root.resolve(candidate);
        }
        return candidate.toAbsolutePath().normalize();
    }
}
