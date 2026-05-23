package com.example.noteagent.config;

import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentScopeConfig {

    @Bean
    public Model dashScopeChatModel(DashScopeProperties properties) {
        GenerateOptions options = GenerateOptions.builder()
                .temperature(properties.getTemperature())
                .maxTokens(properties.getMaxTokens())
                .build();

        return DashScopeChatModel.builder()
                .apiKey(properties.getApiKey())
                .modelName(properties.getModelName())
                .enableSearch(false)
                .defaultOptions(options)
                .build();
    }
}
