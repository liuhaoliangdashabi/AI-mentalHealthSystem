package org.example.aispringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table("emotion_diary")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionDiary {
    @TableId(type= IdType.AUTO)
    private Long id;

    @NotBlank(message = "用户Id不能为空")
    @TableField("user_id")
    private Long userId;

    @TableField("diary_date")
    private LocalDate diaryDate;

    @NotBlank(message = "情绪分数不能为空")
    @TableField("mood_score")
    private Integer moodScore;

    @TableField("dominant_emotion")
    private String dominantEmotion;

    @TableField("emotion_triggers")
    @Size(max=100,message="情绪出发不能超过100个字符")
    private String emotionTriggers;

    @TableField("diary_content")
    @Size(max=500,message = "日记内容不能超过500个字符")
    private String diaryContent;

    @TableField("sleep_quality")
    private Integer sleepQuality;

    @TableField("stress_level")
    private Integer stressLevel;

    @TableField("ai_emotion_analysis")
    private String aiEmotionAnalysis;

    @TableField("ai_analysis_updated_at")
    private LocalDateTime aiAnalysisUpdatedAt;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
