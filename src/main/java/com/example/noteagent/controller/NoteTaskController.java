package com.example.noteagent.controller;

import com.example.noteagent.model.NoteTaskRequest;
import com.example.noteagent.model.NoteTaskResponse;
import com.example.noteagent.model.OutlineConfirmRequest;
import com.example.noteagent.service.NoteTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/note-tasks")
@RequiredArgsConstructor
public class NoteTaskController {

    private final NoteTaskService noteTaskService;

    @PostMapping
    public NoteTaskResponse createTask(@Valid @RequestBody NoteTaskRequest request) {
        return noteTaskService.createTask(request);
    }

    @PostMapping("/{taskId}/outline/confirm")
    public NoteTaskResponse confirmOutline(@PathVariable String taskId,
                                           @Valid @RequestBody OutlineConfirmRequest request) {
        return noteTaskService.confirmOutline(taskId, request);
    }

    @GetMapping("/{taskId}")
    public NoteTaskResponse getTask(@PathVariable String taskId) {
        return noteTaskService.getTask(taskId);
    }
}
