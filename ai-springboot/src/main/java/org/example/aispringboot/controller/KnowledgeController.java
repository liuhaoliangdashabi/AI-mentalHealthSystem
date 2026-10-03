package org.example.aispringboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.DTO.command.ArticleCommandDTO;
import org.example.aispringboot.DTO.command.ArticlePageQueryDTO;
import org.example.aispringboot.DTO.command.ArticleStatusCommandDTO;
import org.example.aispringboot.DTO.command.ConsultationPageQueryDTO;
import org.example.aispringboot.DTO.response.ArticleResponseDTO;
import org.example.aispringboot.DTO.response.CategoryResponseDTO;
import org.example.aispringboot.DTO.response.ConsultationResponseDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.entity.KnowledgeArticle;
import org.example.aispringboot.service.KnowledgeCategoryService;
import org.example.aispringboot.service.KnowledgeArticleService;
import org.example.aispringboot.service.KnowledgeConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    @Autowired
    private KnowledgeCategoryService categoryService;
    @Autowired
    private KnowledgeArticleService articleService;
    @Autowired
    private KnowledgeConsultationService consultationService;

    @GetMapping("/category/tree")
    public Result<List<CategoryResponseDTO>> categoryTree(
            @AuthenticationPrincipal UserLoginResponseDTO.UserDetailResponseDTO user
    ){
        log.debug("Id={}查询分类树",user.getId());
        return Result.success(categoryService.listAll());
    }


    //-----------------------------------Article相关
    @GetMapping("/article/page")
    public Result<IPage<ArticleResponseDTO>> articlePage(
            @Valid ArticlePageQueryDTO pageQueryDTO,
            @AuthenticationPrincipal UserLoginResponseDTO.UserDetailResponseDTO user){
        log.debug("Id={}在分页查询文章列表",user.getId());
        IPage<ArticleResponseDTO> articleList=articleService.selectPage(pageQueryDTO);
        return Result.success(articleList);
    }

    @PostMapping("/article")
    public Result<Void> createArticle(
            @Valid @RequestBody ArticleCommandDTO commandDTO,
            @AuthenticationPrincipal UserLoginResponseDTO.UserDetailResponseDTO user){
        log.debug("Id={}新建文章",user.getId());
        String username=user.getUsername().isBlank()?
                user.getEmail():user.getUsername();
        articleService.createArticle(commandDTO,user.getId());
        return Result.success();
    }

    @PutMapping("/article/{id}/status")
    public Result<Void> putArticleStatus(@PathVariable("id") String articleId,@Valid @RequestBody ArticleStatusCommandDTO status,
                                         @AuthenticationPrincipal UserLoginResponseDTO.UserDetailResponseDTO user){
        log.debug("Id={}获取文章详情",user.getId());
        articleService.putArticleStatus(articleId,status);
        return Result.success();
    }

    @GetMapping("/article/{id}")
    public Result<ArticleResponseDTO> getArticleDetail(@PathVariable String id,
                                         @AuthenticationPrincipal UserLoginResponseDTO.UserDetailResponseDTO user){
        log.debug("Id={}获取文章articleId={}详情",user.getId(),id);
        ArticleResponseDTO responseDTO=articleService.getArticleDetail(id);
        return Result.success(responseDTO);
    }

    @PutMapping("/article/{id}")
    public Result<Void> updateArticleDetail(@PathVariable String id,
                                      @Valid @RequestBody ArticleCommandDTO commandDTO,
                                      @AuthenticationPrincipal UserLoginResponseDTO.UserDetailResponseDTO user){
        log.debug("Id={}修改文章articleId={}详情",user.getId(),id);
        articleService.putArticleDetail(id,commandDTO);
        return Result.success();
    }
    @DeleteMapping("/article/{id}")
    public Result<Void> deleteArticle(@PathVariable String id,
                                      @AuthenticationPrincipal UserLoginResponseDTO.UserDetailResponseDTO user){
        log.debug("Id={}要删除articleId={}",user.getId(),id);
        articleService.deleteArticle(id);
        return Result.success();
    }



    //-------------------------------------Consultation相关
    @GetMapping("/psychological-chat/sessions")
    public Result<ConsultationResponseDTO> getConsultationPage(@Valid @RequestBody ConsultationPageQueryDTO queryDTO){
        log.debug("查询心理会话列表，currentPage={}，size={}",queryDTO.getCurrentPage(),queryDTO.getSize());
        ConsultationResponseDTO responseDTO=null;
        return Result.success(responseDTO);
    }
}
