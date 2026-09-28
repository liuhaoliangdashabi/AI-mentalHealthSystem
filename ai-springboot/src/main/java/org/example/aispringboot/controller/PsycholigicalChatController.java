package org.example.aispringboot.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.aispringboot.AiService.PsychologicalSupportService;
import org.example.aispringboot.AiService.StructOutPut;
import org.example.aispringboot.DTO.command.ConsultationSectionCreateDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/psychological-chat")
public class PsycholigicalChatController {
    @Autowired
    private PsychologicalSupportService psychologicalSupportService;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/session/start")
    public Result<StructOutPut> startSession(@Valid @RequestBody ConsultationSectionCreateDTO createDTO){
        //获取当前用户
        String token=jwtTokenUtil.getCurrentToken();
        DecodedJWT jwt= JwtTokenUtil.verifyToken(token);
        Long userId=jwt.getClaim("userId").asLong();

        psychologicalSupportService.startSession(userId,createDTO);
        return Result.success();
    }
}
