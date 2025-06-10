package com.xavelo.sqs.application.service;

import com.xavelo.sqs.port.in.ChatGptQuestionUseCase;
import com.xavelo.sqs.port.out.ChatGptPort;
import org.springframework.stereotype.Service;

@Service
public class ChatGptService implements ChatGptQuestionUseCase {

    private final ChatGptPort chatGptPort;

    public ChatGptService(ChatGptPort chatGptPort) {
        this.chatGptPort = chatGptPort;
    }

    @Override
    public String askQuestion(String question) {
        return chatGptPort.askQuestion(question);
    }
}
