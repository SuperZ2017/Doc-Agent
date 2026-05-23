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
public class OutlineSection {
    private String heading;
    private int level;
    private String purpose;
    @Builder.Default
    private List<String> keyPoints = new ArrayList<>();
    @Builder.Default
    private List<OutlineSection> children = new ArrayList<>();
}
