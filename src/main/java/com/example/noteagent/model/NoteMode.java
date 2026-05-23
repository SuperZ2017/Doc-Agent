package com.example.noteagent.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "笔记任务模式")
public enum NoteMode {

    // 整理模式。读取多份 Markdown 素材，整理成一篇新的技术笔记。
    ORGANIZE,

    // 增量更新模式。读取已有笔记和新增素材，生成更新后的完整技术笔记。
    INCREMENTAL_UPDATE
}
