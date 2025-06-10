package com.xavelo.sqs.adapter.out.chatgpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.port.out.ChatGptPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class ChatGptAdapter implements ChatGptPort {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey;

    public ChatGptAdapter(@Value("${openai.api-key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String askQuestion(String question) {
        if (apiKey == null || apiKey.isBlank()) {
            return "API key not configured";
        }
        String url = "https://api.openai.com/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(Map.of("role", "user", "content", question))
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);
            return node.at("/choices/0/message/content").asText();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
