package VASService.mywork.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;

@RestController

@CrossOrigin(origins = "http://localhost:3000/" )
public class MainController {

    private final RestTemplate restTemplate = new RestTemplate();

    // ================= Existing Services =================

    @GetMapping("/cricket")
    public ResponseEntity<?> getCricketData() {
        String url = "http://localhost:8083/matches/live";
        try {
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON) // ✅ force JSON response
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Unable to fetch cricket data\"}");
        }
    }


    @GetMapping("/movies")
    public ResponseEntity<Object> getMovieData() {
        String url = "http://localhost:8084/api/movies";
        Object response = restTemplate.getForObject(url, Object.class);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tetris")
    public ResponseEntity<Object> getTetrisData() {
        String url = "http://localhost:8085/api/tetris";
        Object response = restTemplate.getForObject(url, Object.class);
        return ResponseEntity.ok(response);
    }




    // ================= Subscription Flow (via OTP) =================

    @PostMapping("/subscribe")
    public ResponseEntity<Object> subscribe(@RequestParam String user_phone_number) {
        String url = "http://localhost:8080/sendotp";

        // send JSON body
        Map<String, String> request = Map.of("user_phone_number", user_phone_number);
        Map response = restTemplate.postForObject(url, request, Map.class);

        return ResponseEntity.ok(
                Map.of(
                        "message", "OTP sent to " + user_phone_number,
                        "response", response
                )
        );
    }

    @PostMapping("/verify-subscribe")
    public ResponseEntity<Object> verifySubscribe(@RequestParam String user_phone_number,
                                                  @RequestParam String otp) {
        String url = "http://localhost:8080/verifyotp";

        // send JSON body
        Map<String, Object> request = Map.of(
                "user_phone_number", user_phone_number,
                "otp", otp
        );
        Map response = restTemplate.postForObject(url, request, Map.class);

        if ("success".equalsIgnoreCase((String) response.get("status"))) {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "success",
                            "message", "Subscription successful for " + user_phone_number
                    )
            );
        } else {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "failed",
                            "message", "OTP verification failed for subscription."
                    )
            );
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Object> unsubscribe(@RequestParam String user_phone_number) {
        String url = "http://localhost:8080/sendotp";

        Map<String, String> request = Map.of("user_phone_number", user_phone_number);
        Map response = restTemplate.postForObject(url, request, Map.class);

        return ResponseEntity.ok(
                Map.of(
                        "message", "OTP sent for unsubscribe to " + user_phone_number,
                        "response", response
                )
        );
    }

    @PostMapping("/verify-unsubscribe")
    public ResponseEntity<Object> verifyUnsubscribe(@RequestParam String user_phone_number,
                                                    @RequestParam String otp) {
        String url = "http://localhost:8080/verifyotp";

        Map<String, Object> request = Map.of(
                "user_phone_number", user_phone_number,
                "otp", otp
        );
        Map response = restTemplate.postForObject(url, request, Map.class);

        if ("success".equalsIgnoreCase((String) response.get("status"))) {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "success",
                            "message", "Unsubscription successful for " + user_phone_number
                    )
            );
        } else {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "failed",
                            "message", "OTP verification failed for unsubscription."
                    )
            );
        }
    }


}
