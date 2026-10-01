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

import java.time.LocalDateTime;

@Data
@Table("knowledge_category")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeCategory {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("parent_id")
    private Long parentId;

    @NotBlank(message = "分类名不能为空")
    @TableField("category_name")
    private String categoryName;

    @TableField("category_code")
    private String categoryCode;

    @Size(max=100,message = "分类描述不能超过100个字符")
    private String description;

    @TableField("sort_order")
    private Integer sortOrder;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
