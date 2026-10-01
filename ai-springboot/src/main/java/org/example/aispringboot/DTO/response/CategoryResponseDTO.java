package org.example.aispringboot.DTO.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponseDTO {
    private Long id;
    private String categoryName;
}
