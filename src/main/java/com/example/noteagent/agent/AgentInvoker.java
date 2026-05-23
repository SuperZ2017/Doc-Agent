package com.example.noteagent.agent;

public interface AgentInvoker {
    String invoke(String agentName, String systemPrompt, String userPrompt);
}
