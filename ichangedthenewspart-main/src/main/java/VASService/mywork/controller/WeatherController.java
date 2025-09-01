package VASService.mywork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    // WebClient pointing to the backend weather service
    private final WebClient webClient = WebClient.create("http://localhost:8082");

    @GetMapping
    public Mono<ResponseEntity<String>> getWeatherData(@RequestParam(required = false) String city) {
        if (city == null || city.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("City parameter is required."));
        }

        String uri = "/api/weather/current?city=" + city.trim();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(500).body("Failed to fetch weather data."));
                });
    }
}
