package com.example.noteagent.controller;

import com.example.noteagent.model.NoteTaskRequest;
import com.example.noteagent.model.NoteTaskResponse;
import com.example.noteagent.model.OutlineConfirmRequest;
import com.example.noteagent.service.NoteTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "笔记任务", description = "技术学习笔记整理 Agent 的任务创建、确认大纲和状态查询接口")
public class NoteTaskController {

    private final NoteTaskService noteTaskService;

    @PostMapping
    @Operation(
            summary = "创建笔记整理或增量更新任务",
            description = "读取用户指定的 Markdown 素材，生成文章大纲，并将任务置为等待大纲确认状态。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "任务创建成功，返回待确认的大纲",
                    content = @Content(schema = @Schema(implementation = NoteTaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误或路径不安全",
                    content = @Content(schema = @Schema(implementation = com.example.noteagent.model.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "输入 Markdown 文件不存在",
                    content = @Content(schema = @Schema(implementation = com.example.noteagent.model.ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Agent 或大模型调用失败",
                    content = @Content(schema = @Schema(implementation = com.example.noteagent.model.ErrorResponse.class)))
    })
    public NoteTaskResponse createTask(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "创建任务请求，包含模式、素材路径、输出目录和写作偏好",
                    required = true
            )
            @Valid @RequestBody NoteTaskRequest request) {
        return noteTaskService.createTask(request);
    }

    @PostMapping("/{taskId}/outline/confirm")
    @Operation(
            summary = "确认或修改大纲并生成最终笔记",
            description = "确认系统生成的大纲，或提交用户修改后的大纲，然后生成最终 Markdown、校验报告和输出文件。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "大纲确认成功，最终笔记生成完成",
                    content = @Content(schema = @Schema(implementation = NoteTaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = com.example.noteagent.model.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "任务不存在",
                    content = @Content(schema = @Schema(implementation = com.example.noteagent.model.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "任务状态不允许确认大纲或生成正文",
                    content = @Content(schema = @Schema(implementation = com.example.noteagent.model.ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Agent 或大模型调用失败",
                    content = @Content(schema = @Schema(implementation = com.example.noteagent.model.ErrorResponse.class)))
    })
    public NoteTaskResponse confirmOutline(
            @Parameter(description = "任务 ID，创建任务接口返回的 UUID", required = true)
            @PathVariable String taskId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "大纲确认请求。approved 为 true 时开始生成最终笔记；outlineMarkdown 可传入用户修改后的大纲。",
                    required = true
            )
            @Valid @RequestBody OutlineConfirmRequest request) {
        return noteTaskService.confirmOutline(taskId, request);
    }

    @GetMapping("/{taskId}")
    @Operation(
            summary = "查询任务状态",
            description = "根据任务 ID 查询当前状态、大纲、合并计划、最终文件路径、校验报告和错误信息。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = NoteTaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "任务不存在",
                    content = @Content(schema = @Schema(implementation = com.example.noteagent.model.ErrorResponse.class)))
    })
    public NoteTaskResponse getTask(
            @Parameter(description = "任务 ID，创建任务接口返回的 UUID", required = true)
            @PathVariable String taskId) {
        return noteTaskService.getTask(taskId);
    }
}
