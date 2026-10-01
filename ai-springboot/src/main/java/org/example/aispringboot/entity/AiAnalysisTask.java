package org.example.aispringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("ai_analysis_task")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisTask {
    @TableId(type= IdType.AUTO)
    private Long id;

    @NotBlank(message = "日记Id不能为空")
    @TableField("diary_id")
    private Long diaryId;

    @NotBlank(message = "用户Id不能为空")
    @TableField("user_id")
    private Long userId;

    @NotBlank(message = "用户状态不能为空")
    private String status;

    @NotBlank(message = "任务类型不能为空")
    @TableField("task_type")
    private String taskType;

    @NotBlank(message = "优先级不能为空")
    private Integer priority;

    @NotBlank(message = "重试次数不能为空")
    @TableField("retry_count")
    private Integer retryCount;

    @NotBlank(message = "最大重试次数不能为空")
    @TableField("max_retry_count")
    private Integer maxRetryCount;

    @TableField("error_message")
    private String errorMessage;

    @TableField("started_at")
    private LocalDateTime startedAt;
    @TableField("completed_at")
    private LocalDateTime completedAt;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
