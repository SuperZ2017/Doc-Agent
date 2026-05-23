package com.example.noteagent.agent;

import com.example.noteagent.exception.AgentInvocationException;
import com.example.noteagent.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentJsonParser {

    private final ObjectMapper objectMapper;
    private final AgentInvoker agentInvoker;

    public <T> T parseOrRepair(String agentName, String rawResponse, Class<T> targetType) {
        try {
            return JsonUtils.readJson(objectMapper, rawResponse, targetType);
        } catch (JsonProcessingException firstFailure) {
            String repairPrompt = """
                    目标 Java 类型：%s

                    原始输出：
                    %s

                    请返回严格 JSON 对象。
                    """.formatted(targetType.getSimpleName(), rawResponse);
            String repaired = agentInvoker.invoke(agentName + "JsonRepair", AgentPrompts.JSON_REPAIR, repairPrompt);
            try {
                return JsonUtils.readJson(objectMapper, repaired, targetType);
            } catch (JsonProcessingException secondFailure) {
                throw new AgentInvocationException("Failed to parse JSON response for " + agentName, secondFailure);
            }
        }
    }
}
