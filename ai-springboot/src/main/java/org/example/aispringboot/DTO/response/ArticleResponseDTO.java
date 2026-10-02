package org.example.aispringboot.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ArticleResponseDTO {
    private String id;
    private String title;
    private String content;
    private String coverImage;
    private Long categoryId;
    private Integer status;
    private Integer readCount;
    private String summary;
    private String tags;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    private String categoryName;
    private String authorName;
}
