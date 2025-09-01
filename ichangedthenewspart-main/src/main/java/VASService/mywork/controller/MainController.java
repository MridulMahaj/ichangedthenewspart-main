package VASService.mywork.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

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
    public ResponseEntity<Object> subscribe(@RequestParam String phoneNumber) {
        String url = "http://localhost:8080/sendotp";
        Map<String, String> body = Map.of("phone", phoneNumber);

        Object response = restTemplate.postForObject(url, body, Object.class);

        return ResponseEntity.ok(
                Map.of(
                        "message", "OTP sent to " + phoneNumber,
                        "response", response
                )
        );
    }


    @PostMapping("/verify-subscribe")
    public ResponseEntity<Object> verifySubscribe(@RequestParam String phoneNumber, @RequestParam String otp) {
        String url = "http://localhost:8080/verifyotp";
        Map<String, String> body = Map.of("phone", phoneNumber, "otp", otp);

        String response = restTemplate.postForObject(url, body, String.class);

        if ("success".equalsIgnoreCase(response)) {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "success",
                            "message", "Subscription successful for " + phoneNumber
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
    public ResponseEntity<Object> unsubscribe(@RequestParam String phoneNumber) {
        String url = "http://localhost:8080/sendotp?phone=" + phoneNumber;
        Object response = restTemplate.postForObject(url, Object.class);
        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "OTP sent for unsubscribe to " + phoneNumber,
                        "response", response
                )
        );
    }

    @PostMapping("/verify-unsubscribe")
    public ResponseEntity<Object> verifyUnsubscribe(@RequestParam String phoneNumber, @RequestParam String otp) {
        String url = "http://localhost:8080/verifyotp?phone=" + phoneNumber + "&otp=" + otp;
        String response = restTemplate.getForObject(url, String.class);

        if ("success".equalsIgnoreCase(response)) {
            return ResponseEntity.ok(
                    java.util.Map.of(
                            "status", "success",
                            "message", "Unsubscription successful for " + phoneNumber
                    )
            );
        } else {
            return ResponseEntity.ok(
                    java.util.Map.of(
                            "status", "failed",
                            "message", "OTP verification failed for unsubscription."
                    )
            );
        }
    }
}
