package com.example.noteagent.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "workspace")
public class WorkspaceProperties {

    @NotBlank
    private String root = "/workspace";

    @Min(1)
    private long maxFileSizeKb = 1024;

    @NotBlank
    private String outputDir = "/workspace/output";
}
