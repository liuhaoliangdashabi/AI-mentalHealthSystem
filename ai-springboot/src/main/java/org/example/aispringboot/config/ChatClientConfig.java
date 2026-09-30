package org.example.aispringboot.config;

import org.example.aispringboot.AiService.PromptManage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .maxMessages(20) //保留最近20条消息
                .build();
    }

    @Bean("open-ai")
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel){
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory()).build())
                .defaultSystem(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
                .build();
    }
}
