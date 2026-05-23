package com.example.noteagent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteOutline {
    private String title;
    private String description;
    @Builder.Default
    private List<OutlineSection> sections = new ArrayList<>();
}
