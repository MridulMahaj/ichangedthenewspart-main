package VASService.mywork.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import VASService.mywork.services.SubscriptionService;

import java.util.Map;

@Controller
public class SubscriptionWebSocketController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final SubscriptionService subscriptionService;

    public SubscriptionWebSocketController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @MessageMapping("/subscription")        // Client sends to /app/subscription
    @SendTo("/topic/subscription")          // Server sends responses to /topic/subscription
    public Map<String, Object> handleMessage(Map<String, Object> payload) {
        String action = (String) payload.get("action");
        Integer userId = Integer.parseInt(payload.get("user_id").toString());
        String serviceName = (String) payload.get("service_name");

        switch (action) {
            case "subscribe":
                return sendOtp(userId, serviceName, "subscribe");

            case "verify_subscribe":
                return verifyAndSubscribe(userId, serviceName, (String) payload.get("otp"));

            case "unsubscribe":
                return sendOtp(userId, serviceName, "unsubscribe");

            case "verify_unsubscribe":
                return verifyAndUnsubscribe(userId, serviceName, (String) payload.get("otp"));

            default:
                return Map.of("status", "error", "message", "Unknown action");
        }
    }

    private Map<String, Object> sendOtp(int userId, String serviceName, String type) {
        String url = "http://localhost:8080/sendotp";
        Map<String, String> request = Map.of("user_phone_number", String.valueOf(userId));
        Map response = restTemplate.postForObject(url, request, Map.class);

        return Map.of(
                "status", "otp_sent",
                "message", "OTP sent for " + type + " to user " + userId + " for service " + serviceName,
                "response", response
        );
    }

    private Map<String, Object> verifyAndSubscribe(int userId, String serviceName, String otp) {
        if (!verifyOtp(String.valueOf(userId), otp)) {
            return Map.of("status", "failed", "message", "OTP verification failed");
        }

        boolean success = subscriptionService.subscribeUser(String.valueOf(userId), serviceName);
        return Map.of(
                "status", success ? "success" : "failed",
                "message", success
                        ? "Subscription successful for " + serviceName
                        : "Subscription failed for " + serviceName
        );
    }

    private Map<String, Object> verifyAndUnsubscribe(int userId, String serviceName, String otp) {
        if (!verifyOtp(String.valueOf(userId), otp)) {
            return Map.of("status", "failed", "message", "OTP verification failed");
        }

        boolean success = subscriptionService.unsubscribeUser(String.valueOf(userId), serviceName);
        return Map.of(
                "status", success ? "success" : "failed",
                "message", success
                        ? "Unsubscription successful for " + serviceName
                        : "Unsubscription failed for " + serviceName
        );
    }

    private boolean verifyOtp(String phone, String otp) {
        String url = "http://localhost:8080/verifyotp";
        Map<String, Object> request = Map.of("user_phone_number", phone, "otp", otp);
        Map response = restTemplate.postForObject(url, request, Map.class);
        return "success".equalsIgnoreCase((String) response.get("status"));
    }
}
