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

    @MessageMapping("/subscription")
    @SendTo("/topic/subscription")
    public Map<String, Object> handleMessage(Map<String, Object> payload) {
        String action = (String) payload.get("action");
        String userId = payload.get("user_id").toString();
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

    /**
     * Send OTP via external service (http://localhost:8080/sendotp)
     */
    private Map<String, Object> sendOtp(String userId, String serviceName, String type) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        Map<String, String> request = Map.of("user_phone_number", phone);

        try {
            Map<String, String> response = restTemplate.postForObject(
                    "http://localhost:8080/sendotp",
                    request,
                    Map.class
            );

            if ("success".equalsIgnoreCase(response.get("status"))) {
                return Map.of(
                        "status", "otp_sent",
                        "message", "OTP sent for " + type + " to " + phone + " for service " + serviceName,
                        "otp_demo", response.get("otp") // 👈 include OTP for testing (remove in prod)
                );
            } else {
                return Map.of(
                        "status", "failed",
                        "message", "Failed to send OTP: " + response.get("message")
                );
            }
        } catch (Exception e) {
            return Map.of(
                    "status", "error",
                    "message", "Exception while sending OTP: " + e.getMessage()
            );
        }
    }

    /**
     * Verify OTP and subscribe user
     */
    private Map<String, Object> verifyAndSubscribe(String userId, String serviceName, String otp) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        Map<String, Object> request = Map.of("user_phone_number", phone, "otp", otp);

        Map<String, String> response = restTemplate.postForObject(
                "http://localhost:8080/verifyotp",
                request,
                Map.class
        );

        if (!"success".equalsIgnoreCase(response.get("status"))) {
            return Map.of(
                    "status", "failed",
                    "message", "OTP verification failed: " + response.get("message")
            );
        }

        boolean success = subscriptionService.subscribeUser(userId, serviceName);
        return success
                ? Map.of("status", "success", "message", "Subscription successful for " + phone + " on " + serviceName)
                : Map.of("status", "failed", "message", "Subscription failed for " + phone);
    }

    /**
     * Verify OTP and unsubscribe user
     */
    private Map<String, Object> verifyAndUnsubscribe(String userId, String serviceName, String otp) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        Map<String, Object> request = Map.of("user_phone_number", phone, "otp", otp);

        Map<String, String> response = restTemplate.postForObject(
                "http://localhost:8080/verifyotp",
                request,
                Map.class
        );

        if (!"success".equalsIgnoreCase(response.get("status"))) {
            return Map.of(
                    "status", "failed",
                    "message", "OTP verification failed: " + response.get("message")
            );
        }

        boolean success = subscriptionService.unsubscribeUser(userId, serviceName);
        return success
                ? Map.of("status", "success", "message", "Unsubscribed " + phone + " from " + serviceName)
                : Map.of("status", "failed", "message", "Unsubscription failed for " + phone);
    }
}