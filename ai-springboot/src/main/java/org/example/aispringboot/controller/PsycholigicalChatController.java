package org.example.aispringboot.controller;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.aispringboot.AiService.PsychologicalSupportService;
import org.example.aispringboot.AiService.StructOutPut;
import org.example.aispringboot.DTO.command.ConsultationSectionCreateDTO;
import org.example.aispringboot.DTO.command.ConsultationStreamDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.common.ResultCode;
import org.example.aispringboot.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.awt.*;

@RestController
@RequestMapping("/api/psychological-chat")
public class PsycholigicalChatController {
    @Autowired
    private PsychologicalSupportService psychologicalSupportService;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/session/start")
    public Result<StructOutPut.StreamChatSession> startSession(@Valid @RequestBody ConsultationSectionCreateDTO createDTO){
        //获取当前用户
        String token=jwtTokenUtil.getCurrentToken();
        DecodedJWT jwt= JwtTokenUtil.verifyToken(token);
        Long userId=jwt.getClaim("userId").asLong();

        StructOutPut.StreamChatSession session=psychologicalSupportService.startSession(userId,createDTO);
        return Result.success(session);
    }

    @PostMapping(value="/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)//produces定义返回类型
    public Flux<ServerSentEvent<String>> streamChat(@Valid @RequestBody ConsultationStreamDTO streamDTO){
        //获取当前用户
        String token=jwtTokenUtil.getCurrentToken();
        DecodedJWT jwt= JwtTokenUtil.verifyToken(token);
        Long userId=jwt.getClaim("userId").asLong();

        if(userId==null){
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data(JSONUtil.toJsonStr(Result.error(ResultCode.UNAUTHORIZED.getCode(),ResultCode.UNAUTHORIZED.getMsg(),"用户未登录")))
                    .build());
        }

        return null;
    }
}
