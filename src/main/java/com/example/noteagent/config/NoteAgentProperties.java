package com.example.noteagent.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "note-agent")
public class NoteAgentProperties {

    @Valid
    private Validation validation = new Validation();

    @Data
    public static class Validation {
        private boolean autoFixEnabled = true;

        @Min(0)
        private int maxAutoFixCount = 1;
    }
}
