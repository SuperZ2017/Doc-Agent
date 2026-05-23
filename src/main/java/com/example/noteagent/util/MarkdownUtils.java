package com.example.noteagent.util;

import com.example.noteagent.model.NoteOutline;
import com.example.noteagent.model.OutlineSection;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarkdownUtils {

    private static final Pattern HEADING_PATTERN = Pattern.compile("(?m)^(#{1,6})\\s+(.+?)\\s*#*\\s*$");
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("(?s)```.*?```");

    private MarkdownUtils() {
    }

    public static List<String> extractHeadings(String markdown) {
        List<String> headings = new ArrayList<>();
        Matcher matcher = HEADING_PATTERN.matcher(markdown == null ? "" : markdown);
        while (matcher.find()) {
            headings.add(matcher.group(2).trim());
        }
        return headings;
    }

    public static String extractTitle(String markdown, String fallback) {
        Matcher matcher = HEADING_PATTERN.matcher(markdown == null ? "" : markdown);
        while (matcher.find()) {
            if (matcher.group(1).length() == 1) {
                return matcher.group(2).trim();
            }
        }
        return fallback;
    }

    public static List<String> extractCodeBlocks(String markdown) {
        List<String> blocks = new ArrayList<>();
        Matcher matcher = CODE_BLOCK_PATTERN.matcher(markdown == null ? "" : markdown);
        while (matcher.find()) {
            blocks.add(matcher.group());
        }
        return blocks;
    }

    public static String outlineToMarkdown(NoteOutline outline) {
        if (outline == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(nullToEmpty(outline.getTitle())).append("\n\n");
        if (StringUtils.hasText(outline.getDescription())) {
            builder.append("> ").append(outline.getDescription()).append("\n\n");
        }
        for (OutlineSection section : outline.getSections()) {
            appendSection(builder, section);
        }
        return builder.toString().trim() + "\n";
    }

    public static String sanitizeFileName(String fileName, String defaultName) {
        String value = StringUtils.hasText(fileName) ? fileName : defaultName;
        value = value.replace("\\", "/");
        int slash = value.lastIndexOf('/');
        if (slash >= 0) {
            value = value.substring(slash + 1);
        }
        value = value.replaceAll("[\\p{Cntrl}:*?\"<>|]", "-").trim();
        value = value.replaceAll("\\s+", "-");
        if (!StringUtils.hasText(value)) {
            value = defaultName;
        }
        if (!value.toLowerCase(Locale.ROOT).endsWith(".md")) {
            value = value + ".md";
        }
        return value;
    }

    public static String slugify(String title) {
        String normalized = Normalizer.normalize(nullToEmpty(title), Normalizer.Form.NFKD)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]+", "-")
                .replaceAll("^-+|-+$", "");
        return StringUtils.hasText(normalized) ? normalized : "technical-note";
    }

    public static String unwrapMarkdownFence(String text) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        if (trimmed.startsWith("```markdown") && trimmed.endsWith("```")) {
            return trimmed.substring("```markdown".length(), trimmed.length() - 3).trim() + "\n";
        }
        if (trimmed.startsWith("```md") && trimmed.endsWith("```")) {
            return trimmed.substring("```md".length(), trimmed.length() - 3).trim() + "\n";
        }
        return text;
    }

    private static void appendSection(StringBuilder builder, OutlineSection section) {
        if (section == null || !StringUtils.hasText(section.getHeading())) {
            return;
        }
        int level = Math.max(2, Math.min(6, section.getLevel()));
        builder.append("#".repeat(level)).append(" ").append(section.getHeading()).append("\n\n");
        if (StringUtils.hasText(section.getPurpose())) {
            builder.append("- 目的：").append(section.getPurpose()).append("\n");
        }
        if (section.getKeyPoints() != null && !section.getKeyPoints().isEmpty()) {
            builder.append("- 要点：\n");
            for (String keyPoint : section.getKeyPoints()) {
                builder.append("  - ").append(keyPoint).append("\n");
            }
        }
        builder.append("\n");
        if (section.getChildren() != null) {
            for (OutlineSection child : section.getChildren()) {
                appendSection(builder, child);
            }
        }
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
