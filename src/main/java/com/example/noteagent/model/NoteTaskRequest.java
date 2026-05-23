package com.example.noteagent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteTaskRequest {

    @NotNull
    private NoteMode mode;

    @NotEmpty
    private List<String> materialPaths;

    private String existingNotePath;

    private String outputDir;

    private String outputFileName;

    @Valid
    private NotePreferences preferences;

    @JsonIgnore
    @AssertTrue(message = "existingNotePath is required for INCREMENTAL_UPDATE mode")
    public boolean isExistingNotePathValid() {
        return mode != NoteMode.INCREMENTAL_UPDATE || StringUtils.hasText(existingNotePath);
    }
}
