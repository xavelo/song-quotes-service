package com.xavelo.sqs.adapter.in.http.chatgpt;

import com.xavelo.sqs.port.in.ChatGptQuestionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gpt")
public class ChatGptController {

    private final ChatGptQuestionUseCase chatGptQuestionUseCase;

    public ChatGptController(ChatGptQuestionUseCase chatGptQuestionUseCase) {
        this.chatGptQuestionUseCase = chatGptQuestionUseCase;
    }

    @GetMapping("/ask")
    public ResponseEntity<String> ask(@RequestParam("q") String question) {
        String answer = chatGptQuestionUseCase.askQuestion(question);
        return ResponseEntity.ok(answer);
    }
}
