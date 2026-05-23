package com.example.noteagent.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "dashscope")
public class DashScopeProperties {

    @NotBlank(message = "DASHSCOPE_API_KEY is required")
    private String apiKey;

    @NotBlank
    private String modelName = "qwen-plus";

    @Min(0)
    @Max(2)
    private Double temperature = 0.3;

    @Min(1)
    private Integer maxTokens = 8000;
}
