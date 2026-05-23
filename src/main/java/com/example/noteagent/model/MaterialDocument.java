package com.example.noteagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "已读取的 Markdown 素材文档")
public class MaterialDocument {

    @Schema(description = "文件路径。经过安全校验和规范化后的 Markdown 文件路径")
    private String path;

    @Schema(description = "文件名。Markdown 文件本身的名称", example = "liquibase-1.md")
    private String fileName;

    @Schema(description = "文档标题。优先取一级标题，没有一级标题时使用文件名")
    private String title;

    @Schema(description = "标题列表。按出现顺序提取出的 Markdown 标题文本")
    private List<String> headings;

    @Schema(description = "原始 Markdown。完整保留的 UTF-8 Markdown 内容")
    private String rawMarkdown;

    @Schema(description = "代码块列表。按出现顺序提取出的 fenced code block")
    private List<String> codeBlocks;

    @Schema(description = "字符数。原始 Markdown 的字符数量，用于粗略衡量素材长度", example = "2048")
    private long charCount;
}
