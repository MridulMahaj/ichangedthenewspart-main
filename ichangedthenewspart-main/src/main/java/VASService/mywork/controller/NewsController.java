package VASService.mywork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final WebClient webClient = WebClient.create("http://localhost:8081");
    // <-- assuming rtnews runs on :8081

    // --- GET endpoint for browser / ngrok ---
    @GetMapping
    public Mono<ResponseEntity<String>> getNewsDataByQuery(@RequestParam String story) {
        String uri = "/api/news/full-story?story=" + story + "&sortBy=RELEVANCE&region=US&lang=en";

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Failed to fetch news.")));
    }

    // --- POST endpoint for Postman / JSON body ---
    @PostMapping
    public Mono<ResponseEntity<String>> getNewsDataByBody(@RequestBody Map<String, String> body) {
        String story = body.get("story");
        if (story == null || story.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Story parameter is required."));
        }

        String uri = "/api/news/full-story?story=" + story + "&sortBy=RELEVANCE&region=US&lang=en";

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Failed to fetch news.")));
    }
}
