package com.momenty.record.util;

import static com.momenty.record.exception.RecordExceptionMessage.TOO_MANY_PROMPT;

import com.momenty.global.exception.GlobalException;
import com.momenty.record.dto.OpenAiResponse;
import com.momenty.record.dto.RecordAnalysisResponse;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AiClient {

    private final WebClient webClient;

    public AiClient(
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.project-id}") String projectId
    ) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("OpenAI-Project", projectId)
                .build();
    }

    public Mono<RecordAnalysisResponse> requestSummary(String prompt) {
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(Map.of(
                        "model", "gpt-4o-mini",
                        "messages", List.of(Map.of("role", "user", "content", prompt))
                ))
                .retrieve()
                .onStatus(
                        status -> status.value() == 429,
                        response -> Mono.error(
                                new GlobalException(TOO_MANY_PROMPT.getMessage(),
                                TOO_MANY_PROMPT.getStatus())
                        )
                )
                .bodyToMono(OpenAiResponse.class)
                .map(response -> {
                    String content = response.choices().get(0).message().content();
                    return RecordAnalysisResponse.from(content);
                });
    }

}
