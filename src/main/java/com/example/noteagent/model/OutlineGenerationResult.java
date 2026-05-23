package com.example.noteagent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutlineGenerationResult {
    private NoteOutline outline;
    private String outlineMarkdown;
}
