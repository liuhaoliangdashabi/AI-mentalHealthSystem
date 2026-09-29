package org.example.aispringboot.AiService;

import org.example.aispringboot.DTO.command.ConsultationSectionCreateDTO;
import org.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import org.example.aispringboot.entity.ConsultationSession;
import org.example.aispringboot.service.ConsultationMessageService;
import org.example.aispringboot.service.ConsultationSessionsService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class PsychologicalSupportService {
    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private ConsultationSessionsService consultationSessionsService;
    @Autowired
    private ConsultationMessageService consultationMessageService;

    public StructOutPut.StreamChatSession startSession(Long userId, ConsultationSectionCreateDTO createDTO){
        ConsultationSession consultationSession=consultationSessionsService.createSession(userId,createDTO);
        consultationMessageService.saveUserMessage(consultationSession.getId(),createDTO.getInitialMessage(),null);

        String sessionId="session_"+consultationSession.getId();
        return new StructOutPut.StreamChatSession(
                sessionId,
                userId,
                createDTO.getInitialMessage(),
                System.currentTimeMillis(),
                System.currentTimeMillis()+86400000L,//24小时超时时间
                1,
                "ACTIVE"
        );
    }

    public Flux<String> streamPsychologicalChat(String sessionId,String userMessage){
        //创建响应流
        return Flux.create(sink->{
            //sink.next()：发布数据
            //sink.complete()：完成流，告诉前端已完成
            //sink.error(exception)：发布错误
            Long dbSessionId=extractSessionId(sessionId);
            if(dbSessionId==null){
                sink.error(new RuntimeException("会话Id格式错误"));
                return;
            }

            boolean isInitialMessage=false;//是否为初始消息
            //判断是否为初始消息，避免重复请求
            Integer messageCount=consultationMessageService.getMessageCountBySessionId(dbSessionId);
            if(messageCount>0){
                //直接发布
                ConsultationMessageResponseDTO lastMessage =
                        consultationMessageService.getLastMessageBySessionId(dbSessionId);
                if(lastMessage!=null && lastMessage.getSenderType()==1 && userMessage.equals(lastMessage.getContent())){
                    //发送者必须是用户的类型，不然AI检测到AI发的，无限递归的发了
                    isInitialMessage=true;
                }
            }

            if(!isInitialMessage){
                //该保存用户消息到数据库
                consultationMessageService.saveUserMessage(dbSessionId,userMessage,null);
            }

            //流式对话——基于ChatClient
            //生成对话记忆管理
            String conversationId="conversation_"+sessionId;
            List<Message> userMessages=new ArrayList<>();
            userMessages.add(new UserMessage(userMessage));
            chatMemory.add(conversationId,userMessages);
            Prompt prompt=new Prompt(List.of(
                    new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
            ));
            //存储AI完整响应
            StringBuilder fullResponse=new StringBuilder();
            //用chatClient发送消息到OpenAI
            chatClient.prompt(prompt)
                    .user(userMessage)
                    .advisors(advisorSpec ->
                            advisorSpec.param(ChatMemory.CONVERSATION_ID,conversationId))
                    .stream()
                    .content()
                    .doOnNext(fragment->{
                        fullResponse.append(fragment);
                        sink.next(fragment);
                    })
                    .doOnComplete(()->{
                        String completeRes=fullResponse.toString();
                        //消息存储到表中
                        consultationMessageService.saveAiMessage(dbSessionId,completeRes,"openai");
                        //AI回复也要添加到记忆体
                        List<Message> aiMessages=new ArrayList<>();
                        aiMessages.add(new AssistantMessage(completeRes));
                        chatMemory.add(conversationId,aiMessages);

                        sink.complete();
                    })
                    .doOnError(error->{
                        sink.error(error);
                    })
                    .subscribe();//订阅、启动流
        });

    }

    public Long extractSessionId(String sessionId){
        if(sessionId!=null && sessionId.startsWith("session_")){
            return Long.parseLong(sessionId.substring("session_".length()));
        }
        return null;
    }


}
