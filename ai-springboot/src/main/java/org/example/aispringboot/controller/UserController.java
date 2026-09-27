package org.example.aispringboot.controller;

import cn.hutool.json.JSONUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.DTO.command.UserLoginCommandDTO;
import org.example.aispringboot.DTO.command.UserRegisterCommandDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO commandDTO){
        log.info("登录请求的controller层，username = {}",commandDTO.getUsername());
        UserLoginResponseDTO responseDTO=userService.login(commandDTO);
        return Result.success(responseDTO);
    }

    @PostMapping("/add")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> register(@Valid @RequestBody UserRegisterCommandDTO commandDTO){
        log.info("注册请求的controller层：{}", JSONUtil.parseObj(commandDTO));
        UserLoginResponseDTO.UserDetailResponseDTO result=userService.register(commandDTO);
        return Result.success(result);
    }
}
