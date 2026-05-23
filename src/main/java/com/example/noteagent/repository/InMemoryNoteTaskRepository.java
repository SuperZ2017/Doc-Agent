package com.example.noteagent.repository;

import com.example.noteagent.model.NoteTaskSession;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryNoteTaskRepository {

    private final ConcurrentMap<String, NoteTaskSession> sessions = new ConcurrentHashMap<>();

    public NoteTaskSession save(NoteTaskSession session) {
        session.setUpdatedAt(Instant.now());
        sessions.put(session.getTaskId(), session);
        return session;
    }

    public Optional<NoteTaskSession> findById(String taskId) {
        return Optional.ofNullable(sessions.get(taskId));
    }
}
