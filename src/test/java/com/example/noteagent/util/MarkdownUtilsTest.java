package com.example.noteagent.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownUtilsTest {

    @Test
    void extractsHeadings() {
        String markdown = """
                # Liquibase

                ## Changeset

                ### Rollback
                """;

        assertThat(MarkdownUtils.extractHeadings(markdown))
                .containsExactly("Liquibase", "Changeset", "Rollback");
    }

    @Test
    void extractsCodeBlocks() {
        String markdown = """
                # Demo

                ```yaml
                spring:
                  liquibase:
                    enabled: true
                ```

                text

                ```java
                class Demo {}
                ```
                """;

        List<String> blocks = MarkdownUtils.extractCodeBlocks(markdown);

        assertThat(blocks).hasSize(2);
        assertThat(blocks.getFirst()).contains("```yaml");
        assertThat(blocks.get(1)).contains("class Demo");
    }
}
