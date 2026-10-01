package org.example.aispringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("knowledge_article")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeArticle {
    private String id;

    @NotBlank(message = "分类Id不能为空")
    @TableField("category_id")
    private Long categoryId;

    @NotBlank(message = "文章标题不能为空")
    @Size(max=30,message = "文章标题不能超过30个字符")
    private String title;

    @Size(max=100,message="文章摘要不能超过100个字符")
    private String summary;

    @NotBlank(message = "文章内容不能为空")
    @Size(max=1000,message="文章内容不能超过1000个字符")
    private String content;

    @TableField("cover_image")
    private String coverImage;

    private String tags;

    @NotBlank(message = "作者Id不能为空")
    @TableField("author_id")
    private Long authorId;

    private Integer readCount;

    private Integer status;

    @TableField("published_at")
    private LocalDateTime publishedAt;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
