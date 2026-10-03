package org.example.aispringboot.service;

import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.mapper.ConsultationMessageMapper;
import org.example.aispringboot.mapper.ConsultationSessionMapper;
import org.example.aispringboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class KnowledgeConsultationService {
    @Autowired
    private ConsultationSessionMapper sessionMapper;
    @Autowired
    private ConsultationMessageMapper messageMapper;
    @Autowired
    private UserMapper userMapper;
}
