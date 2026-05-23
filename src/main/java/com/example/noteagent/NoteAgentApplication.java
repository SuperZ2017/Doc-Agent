package com.example.noteagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NoteAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoteAgentApplication.class, args);
    }
}
