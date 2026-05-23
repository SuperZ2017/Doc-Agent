package com.example.noteagent.service;

import com.example.noteagent.agent.NoteWriterAgent;
import com.example.noteagent.model.NoteTaskSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteGenerationService {

    private final NoteWriterAgent noteWriterAgent;

    public String generate(NoteTaskSession session, String confirmedOutlineMarkdown) {
        return noteWriterAgent.write(
                session.getMode(),
                confirmedOutlineMarkdown,
                session.getMaterials(),
                session.getExistingNote(),
                session.getMergePlan(),
                session.getRequest().getPreferences()
        );
    }

    public String repair(NoteTaskSession session,
                         String finalMarkdown,
                         String confirmedOutlineMarkdown,
                         String validationReportJson) {
        return noteWriterAgent.repair(finalMarkdown, confirmedOutlineMarkdown, validationReportJson, session.getMaterials());
    }
}
