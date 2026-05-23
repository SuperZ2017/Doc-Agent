package com.example.noteagent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一错误响应")
public class ErrorResponse {

    @Schema(description = "是否成功。错误响应固定为 false", example = "false")
    private boolean success;

    @Schema(description = "错误码。用于前端或调用方识别错误类型", example = "UNSAFE_PATH")
    private String errorCode;

    @Schema(description = "错误信息。给调用方阅读的具体失败原因", example = "Path is outside configured workspace")
    private String message;
}
