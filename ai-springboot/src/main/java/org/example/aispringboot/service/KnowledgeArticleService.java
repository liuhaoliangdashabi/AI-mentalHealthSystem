package org.example.aispringboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.DTO.command.ArticleCommandDTO;
import org.example.aispringboot.DTO.command.ArticlePageQueryDTO;
import org.example.aispringboot.DTO.command.ArticleStatusCommandDTO;
import org.example.aispringboot.DTO.response.ArticleResponseDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.entity.KnowledgeArticle;
import org.example.aispringboot.entity.KnowledgeCategory;
import org.example.aispringboot.entity.User;
import org.example.aispringboot.exception.BusinessException;
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
@Slf4j
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
                User u=userMapper.selectById(a.getAuthorId());
                if(u==null){
                    log.warn("文章{}的作者{}不存在，可能是脏数据",a.getId(),a.getAuthorId());
                }
                AuthorIdWithName.put(a.getAuthorId(),u==null?null:u.getDisplayName());

            }
            if(!CategoryIdWithName.containsKey(a.getCategoryId())){
                KnowledgeCategory c=categoryMapper.selectById(a.getCategoryId());
                if(c==null){
                    log.warn("文章{}的分类{}不存在，可能是脏数据，请尽快查看",a.getId(),a.getCategoryId());
                }
                CategoryIdWithName.put(a.getCategoryId(),c==null?null:c.getCategoryName());
            }
        }
        return page.convert(a-> KnowledgeConvert.articleToResponse(
                    a,AuthorIdWithName.get(a.getAuthorId()),
                    CategoryIdWithName.get(a.getCategoryId()))
        );
    }

    public void createArticle(@Valid ArticleCommandDTO commandDTO,Long id) {
        if(categoryMapper.selectById(commandDTO.getCategoryId())==null){
            throw new BusinessException(commandDTO.getCategoryId()+"分类不存在");
        }
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

    public ArticleResponseDTO getArticleDetail(String id) {
        KnowledgeArticle a=articleMapper.selectById(id);
        if(a==null)throw new BusinessException("id={}文章不存在");
        User u=userMapper.selectById(a.getAuthorId());
        if(u==null)log.warn("文章{}的作者{}不存在，可能是脏数据",id,a.getAuthorId());
        KnowledgeCategory c=categoryMapper.selectById(a.getCategoryId());
        if(c==null)log.warn("文章{}的分类{}不存在，可能是脏数据",id,a.getCategoryId());
        return KnowledgeConvert.articleToResponse(a,u==null?null:u.getDisplayName(),
                c==null?null:c.getCategoryName());
    }

    public void putArticleDetail(String articleId, @Valid ArticleCommandDTO commandDTO) {
        KnowledgeArticle entity=KnowledgeConvert.toUpdateEntity(articleId,commandDTO);
        int count=articleMapper.updateById(entity);
        if(count==0){
            log.warn("文章{}不存在",articleId);
            throw new BusinessException("文章不存在");
        }
        log.debug("文章已更新：articleId={}",articleId);
    }

    public void deleteArticle(String articleId) {
        int count=articleMapper.deleteById(articleId);
        if(count==0){
            log.warn("文章{}不存在",articleId);
            throw new BusinessException("文章不存在");
        }
    }
}

