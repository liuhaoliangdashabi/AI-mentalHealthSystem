package org.example.aispringboot.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.example.aispringboot.DTO.command.ConsultationSectionCreateDTO;
import org.example.aispringboot.entity.ConsultationSession;
import org.example.aispringboot.entity.User;
import org.example.aispringboot.mapper.ConsultationSessionMapper;
import org.example.aispringboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class ConsultationSessionsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;

    public ConsultationSession createSession(Long userId, ConsultationSectionCreateDTO createDTO){
        //先验证用户是否存在，数据插入才有意义
        User user=userMapper.selectById(userId);
        if(user!=null){
            ConsultationSession session=ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build();
            if(StrUtil.isBlank(createDTO.getSessionTitle())){
                session.setSessionTitle(String.format("宁渡AI助手 - " + DateUtil.format(LocalDateTime.now(),"MM-dd-HH:mm") ));
            }

            consultationSessionMapper.insert(session);
            return session;
        }
        return null;
    }
}
