package org.example.aispringboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.example.aispringboot.DTO.command.ArticleCommandDTO;
import org.example.aispringboot.DTO.command.ArticlePageQueryDTO;
import org.example.aispringboot.DTO.command.ArticleStatusCommandDTO;
import org.example.aispringboot.DTO.response.ArticleResponseDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.entity.KnowledgeArticle;
import org.example.aispringboot.mapper.KnowledgeArticleMapper;
import org.example.aispringboot.mapper.KnowledgeCategoryMapper;
import org.example.aispringboot.mapper.UserMapper;
import org.example.aispringboot.service.convert.KnowledgeConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KnowledgeArticleService {
    @Autowired
    private KnowledgeArticleMapper articleMapper;
    @Autowired
    private KnowledgeCategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;

    public IPage<ArticleResponseDTO> selectPage(ArticlePageQueryDTO query) {
        Page<KnowledgeArticle> page=new Page<>(query.getCurrentPage(),query.getSize());
        LambdaQueryWrapper<KnowledgeArticle> qw=new LambdaQueryWrapper<>();
        qw.like(query.getTitle()!=null && !query.getTitle().isEmpty(),
                KnowledgeArticle::getTitle,query.getTitle());
        qw.eq(query.getCategoryId()!=null,
                KnowledgeArticle::getCategoryId,query.getCategoryId());
        qw.eq(query.getStatus()!=null,
                KnowledgeArticle::getStatus,query.getStatus());

        String sortField=query.getSortField()==null?"":query.getSortField();
        switch(sortField){
            case "readCount"->qw.orderByDesc(KnowledgeArticle::getReadCount);
            default         ->qw.orderByDesc(KnowledgeArticle::getPublishedAt);
        }

        articleMapper.selectPage(page,qw);
        Map<Long,String>AuthorIdWithName=new HashMap<>();
        Map<Long,String>CategoryIdWithName=new HashMap<>();
        for(KnowledgeArticle a:page.getRecords()){
            if(!AuthorIdWithName.containsKey(a.getAuthorId())){
                AuthorIdWithName.put(a.getAuthorId(),userMapper.selectById(a.getAuthorId()).getUsername());
            }
            if(!CategoryIdWithName.containsKey(a.getCategoryId())){
                CategoryIdWithName.put(a.getCategoryId(),categoryMapper.selectById(a.getCategoryId()).getCategoryName());
            }
        }
        return page.convert(a-> KnowledgeConvert.articleToResponse(
                    a,AuthorIdWithName.get(a.getAuthorId()),
                    CategoryIdWithName.get(a.getCategoryId()))
        );
    }

    public void createArticle(@Valid ArticleCommandDTO commandDTO,Long id) {
        KnowledgeArticle article=KnowledgeConvert.commandToEntity(commandDTO,id);
        articleMapper.insert(article);
    }

    public void putArticleStatus(String articleId, ArticleStatusCommandDTO status){
        LambdaUpdateWrapper<KnowledgeArticle> uw=new LambdaUpdateWrapper<>();
        uw.eq(KnowledgeArticle::getId,articleId)
                .set(KnowledgeArticle::getStatus, status.getStatus())
                .set(KnowledgeArticle::getUpdatedAt, LocalDateTime.now());
        articleMapper.update(uw);
    }
}

