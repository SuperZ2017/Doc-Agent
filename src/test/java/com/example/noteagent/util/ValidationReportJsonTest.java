package com.example.noteagent.util;

import com.example.noteagent.model.ValidationIssue;
import com.example.noteagent.model.ValidationReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationReportJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesValidationReportToJson() throws Exception {
        ValidationReport report = ValidationReport.builder()
                .passed(false)
                .score(72)
                .issues(List.of(ValidationIssue.builder()
                        .severity("HIGH")
                        .type("MISSING_CONTENT")
                        .message("missing rollback section")
                        .location("## Rollback")
                        .suggestion("add rollback notes")
                        .build()))
                .build();

        String json = objectMapper.writeValueAsString(report);

        assertThat(json).contains("\"passed\":false");
        assertThat(json).contains("\"severity\":\"HIGH\"");
        assertThat(objectMapper.readValue(json, ValidationReport.class).getIssues()).hasSize(1);
    }
}
