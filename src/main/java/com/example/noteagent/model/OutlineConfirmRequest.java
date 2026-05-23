package com.example.noteagent.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutlineConfirmRequest {

    @NotNull
    private Boolean approved;

    private String outlineMarkdown;
}
