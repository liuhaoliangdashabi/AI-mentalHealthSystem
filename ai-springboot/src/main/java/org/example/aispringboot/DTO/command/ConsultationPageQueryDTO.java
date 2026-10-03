package org.example.aispringboot.DTO.command;

import lombok.Data;

@Data
public class ConsultationPageQueryDTO {
    private Integer currentPage=1;
    private Integer size=5;
}
