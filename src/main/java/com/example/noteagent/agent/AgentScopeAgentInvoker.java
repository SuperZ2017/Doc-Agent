package com.example.noteagent.agent;

import com.example.noteagent.exception.AgentInvocationException;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentScopeAgentInvoker implements AgentInvoker {

    private final Model dashScopeChatModel;

    @Override
    public String invoke(String agentName, String systemPrompt, String userPrompt) {
        try {
            ReActAgent agent = ReActAgent.builder()
                    .name(agentName)
                    .sysPrompt(systemPrompt)
                    .model(dashScopeChatModel)
                    .maxIters(3)
                    .build();

            Msg response = agent.call(Msg.builder()
                    .textContent(userPrompt)
                    .build()).block();

            if (response == null || response.getTextContent() == null) {
                throw new AgentInvocationException("Agent returned empty response: " + agentName);
            }
            return response.getTextContent();
        } catch (AgentInvocationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AgentInvocationException("Agent invocation failed: " + agentName, ex);
        }
    }
}
