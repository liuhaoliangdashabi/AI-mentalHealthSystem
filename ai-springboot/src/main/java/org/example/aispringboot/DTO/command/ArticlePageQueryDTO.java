package org.example.aispringboot.DTO.command;

import lombok.Data;

@Data
public class ArticlePageQueryDTO {
    private Integer currentPage=1;
    private Integer size=5;
    private String title;
    private Long categoryId;
    private Integer status;
}
