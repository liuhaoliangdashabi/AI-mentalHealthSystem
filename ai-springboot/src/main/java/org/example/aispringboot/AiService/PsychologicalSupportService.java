package org.example.aispringboot.AiService;

import org.example.aispringboot.DTO.command.ConsultationSectionCreateDTO;
import org.example.aispringboot.service.ConsultationSessionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PsychologicalSupportService {
    @Autowired
    private ConsultationSessionsService consultationSessionsService;

    public StructOutPut.StreamChatSession startSession(Long userId, ConsultationSectionCreateDTO createDTO){
        //创建数据库会话记录
        return null;
    }
}
