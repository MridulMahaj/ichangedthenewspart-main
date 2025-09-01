package VASService.mywork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import VASService.mywork.services.SubscriptionService;
import VASService.mywork.classes.Subscription;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Object> subscribe(@RequestParam String userId, @RequestParam String serviceName) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        Map<String, String> request = Map.of("user_phone_number", phone);

        Map<String, String> response = restTemplate.postForObject("http://localhost:8080/sendotp", request, Map.class);

        if ("success".equalsIgnoreCase(response.get("status"))) {
            return ResponseEntity.ok(Map.of(
                    "status", "otp_sent",
                    "message", "OTP sent to " + phone + " for service " + serviceName,
                    "otp_demo", response.get("otp") // for testing only
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", response.get("message")));
        }
    }

    @PostMapping("/verify-subscribe")
    public ResponseEntity<Object> verifySubscribe(@RequestParam String userId,
                                                  @RequestParam String serviceName,
                                                  @RequestParam String otp) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        Map<String, Object> request = Map.of("user_phone_number", phone, "otp", otp);

        Map<String, String> response = restTemplate.postForObject("http://localhost:8080/verifyotp", request, Map.class);

        if (!"success".equalsIgnoreCase(response.get("status"))) {
            return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", "OTP verification failed"));
        }

        boolean success = subscriptionService.subscribeUser(userId, serviceName);
        Subscription updated = subscriptionService.getLatestSubscription(userId, serviceName);

        return success
                ? ResponseEntity.ok(Map.of("status", "success", "subscription", updated))
                : ResponseEntity.badRequest().body(Map.of("status", "failed", "message", "Subscription failed"));
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Object> unsubscribe(@RequestParam String userId, @RequestParam String serviceName) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        Map<String, String> request = Map.of("user_phone_number", phone);

        Map<String, String> response = restTemplate.postForObject("http://localhost:8080/sendotp", request, Map.class);

        if ("success".equalsIgnoreCase(response.get("status"))) {
            return ResponseEntity.ok(Map.of(
                    "status", "otp_sent",
                    "message", "OTP sent to " + phone + " for unsubscribing from " + serviceName,
                    "otp_demo", response.get("otp")
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", response.get("message")));
        }
    }

    @PostMapping("/verify-unsubscribe")
    public ResponseEntity<Object> verifyUnsubscribe(@RequestParam String userId,
                                                    @RequestParam String serviceName,
                                                    @RequestParam String otp) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        Map<String, Object> request = Map.of("user_phone_number", phone, "otp", otp);

        Map<String, String> response = restTemplate.postForObject("http://localhost:8080/verifyotp", request, Map.class);

        if (!"success".equalsIgnoreCase(response.get("status"))) {
            return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", "OTP verification failed"));
        }

        boolean success = subscriptionService.unsubscribeUser(userId, serviceName);
        Subscription updated = subscriptionService.getLatestSubscription(userId, serviceName);

        return success
                ? ResponseEntity.ok(Map.of("status", "success", "subscription", updated))
                : ResponseEntity.badRequest().body(Map.of("status", "failed", "message", "Unsubscription failed"));
    }
}
