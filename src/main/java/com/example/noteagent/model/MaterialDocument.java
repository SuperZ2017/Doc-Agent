package com.example.noteagent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDocument {
    private String path;
    private String fileName;
    private String title;
    private List<String> headings;
    private String rawMarkdown;
    private List<String> codeBlocks;
    private long charCount;
}
