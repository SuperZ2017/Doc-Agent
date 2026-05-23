package com.example.noteagent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotePreferences {
    private String audience;
    private String style;
    private String focus;
    private Boolean keepCodeExamples;
    private String language;
    private String length;
    private String extraInstructions;
}
