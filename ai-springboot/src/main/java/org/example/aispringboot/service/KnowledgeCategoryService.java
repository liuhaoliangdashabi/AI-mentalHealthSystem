package org.example.aispringboot.service;

import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.DTO.response.CategoryResponseDTO;
import org.example.aispringboot.entity.KnowledgeCategory;
import org.example.aispringboot.mapper.KnowledgeCategoryMapper;
import org.example.aispringboot.service.convert.KnowledgeConvert;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class KnowledgeCategoryService {
    private final KnowledgeCategoryMapper categoryMapper;
    public KnowledgeCategoryService(KnowledgeCategoryMapper categoryMapper){
        this.categoryMapper=categoryMapper;
    }

    public List<CategoryResponseDTO> listAll(){
        List<KnowledgeCategory> list=categoryMapper.selectList(null);
        return list.stream().map(KnowledgeConvert::categoryToResponse).toList();
    }
}
