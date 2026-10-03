package org.example.aispringboot.DTO.command;

import lombok.Data;

@Data
public class ConsultationPageQueryDTO {
    private Integer currentPage=1;
    private Integer size=5;

    public void setCurrentPage(Integer currentPage) {
        if(currentPage!=null)this.currentPage = currentPage;
    }

    public void setSize(Integer size) {
        if(size!=null)this.size = size;
    }
}
