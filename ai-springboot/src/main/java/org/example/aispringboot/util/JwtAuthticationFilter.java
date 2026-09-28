package org.example.aispringboot.util;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.ResultCode;
import org.example.aispringboot.config.SecurityConfig;
import org.example.aispringboot.enumClass.UserStatus;
import org.example.aispringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
public class JwtAuthticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserService userService;



    //确保每个请求只执行一次，不要在过滤器中反复打转
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String requestUri=request.getRequestURI();
        return SecurityConfig.isPublicPATH(requestUri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //获取请求的URL和方法
        String requestUri=request.getRequestURI();
        String method=request.getMethod();
        log.info("获取到请求的URI和方法：request={},method={}",requestUri,method);

        //1.提取JWT token
        String token=JwtTokenUtil.extractTokenFromRequest(request);
        if(StringUtils.hasText(token)){
            //2.验证token并提取信息
            JwtTokenUtil.TokenVeriticationResult validationResult=JwtTokenUtil.validateToken(token);
            if(validationResult!=null && validationResult.isValid()){
                //3.根据解析出的Id查询验证用户状态
                UserLoginResponseDTO.UserDetailResponseDTO user =
                        userService.getUserById(validationResult.getUserId());
                log.info("打印查询到的用户信息：user={}", JSONUtil.parseObj(user));
                if(user!=null && UserStatus.NORMAL.getCode().equals(user.getStatus())){

                }else{
                    log.info("user数据或user状态有误：userStatus={},user={}",user.getStatus(),JSONUtil.parseObj(user));
                    clearSecurityContext();
                    ResponseUtil.WriteError(response,ResultCode.TOKEN_ACCESS_FORBIDDEN);
                }
            }else{
                log.info("token解析出来有问题，准备清除上下文，然后错误信息,token={}",token);
                clearSecurityContext();
                ResponseUtil.WriteError(response, ResultCode.TOKEN_INVALID);
                return;
            }
        }else{
            log.info("提取token失败，token={}，准备清除上下文，然后错误信息",token);
            clearSecurityContext();
            ResponseUtil.WriteError(response, ResultCode.ACCESS_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request,response);

    }

    //清理Spring Security上下文
    private void clearSecurityContext(){
        SecurityContextHolder.clearContext();
    }

}
