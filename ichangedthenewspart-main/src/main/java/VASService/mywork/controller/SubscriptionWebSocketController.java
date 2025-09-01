package VASService.mywork.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import VASService.mywork.services.SubscriptionService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

@Controller
public class SubscriptionWebSocketController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final SubscriptionService subscriptionService;
    private final JdbcTemplate jdbcTemplate;

    public SubscriptionWebSocketController(SubscriptionService subscriptionService, JdbcTemplate jdbcTemplate) {
        this.subscriptionService = subscriptionService;
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------------- STOMP WebSocket ----------------
    @MessageMapping("/subscription")
    @SendTo("/topic/subscription")
    public Map<String, Object> handleWebSocket(Map<String, Object> payload) {
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

    // ---------------- REST Endpoints for Postman ----------------
    @RestController
    @RequestMapping("/api")
    class SubscriptionRestController {

        @PostMapping("/subscribe")
        public Map<String,Object> restSubscribe(@RequestParam String userId, @RequestParam String serviceName) {
            return sendOtp(Integer.parseInt(userId), serviceName, "subscribe");
        }

        @PostMapping("/verify_subscribe")
        public Map<String,Object> restVerifySubscribe(@RequestParam String userId,
                                                      @RequestParam String serviceName,
                                                      @RequestParam String otp) {
            return verifyAndSubscribe(Integer.parseInt(userId), serviceName, otp);
        }

        @PostMapping("/unsubscribe")
        public Map<String,Object> restUnsubscribe(@RequestParam String userId, @RequestParam String serviceName) {
            return sendOtp(Integer.parseInt(userId), serviceName, "unsubscribe");
        }

        @PostMapping("/verify_unsubscribe")
        public Map<String,Object> restVerifyUnsubscribe(@RequestParam String userId,
                                                        @RequestParam String serviceName,
                                                        @RequestParam String otp) {
            return verifyAndUnsubscribe(Integer.parseInt(userId), serviceName, otp);
        }
    }

    // ---------------- Common methods ----------------

    /**
     * Send OTP using your real MessageCentral API via /sendotp
     */
    private Map<String, Object> sendOtp(int userId, String serviceName, String type) {
        String phone = getUserPhoneNumber(userId);

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
                        "message", "OTP sent for " + type + " to phone " + phone,
                        "verificationId", response.get("verificationId")
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
     * Verify OTP via your real MessageCentral API and subscribe
     */
    private Map<String, Object> verifyAndSubscribe(int userId, String serviceName, String otp) {
        String phone = getUserPhoneNumber(userId);

        // Call /verifyotp endpoint
        Map<String, Object> request = Map.of("user_phone_number", phone, "otp", otp);
        Map<String, String> response = restTemplate.postForObject(
                "http://localhost:8080/verifyotp",
                request,
                Map.class
        );

        if (!"success".equalsIgnoreCase(response.get("status"))) {
            return Map.of("status", "failed", "message", "OTP verification failed: " + response.get("message"));
        }

        boolean success = subscriptionService.subscribeUser(String.valueOf(userId), serviceName);
        return success ?
                Map.of("status", "success", "message", "Subscription successful for " + serviceName) :
                Map.of("status", "failed", "message", "Subscription failed for " + serviceName);
    }

    /**
     * Verify OTP via your real MessageCentral API and unsubscribe
     */
    private Map<String, Object> verifyAndUnsubscribe(int userId, String serviceName, String otp) {
        String phone = getUserPhoneNumber(userId);

        Map<String, Object> request = Map.of("user_phone_number", phone, "otp", otp);
        Map<String, String> response = restTemplate.postForObject(
                "http://localhost:8080/verifyotp",
                request,
                Map.class
        );

        if (!"success".equalsIgnoreCase(response.get("status"))) {
            return Map.of("status", "failed", "message", "OTP verification failed: " + response.get("message"));
        }

        boolean success = subscriptionService.unsubscribeUser(String.valueOf(userId), serviceName);
        return success ?
                Map.of("status", "success", "message", "Unsubscription successful for " + serviceName) :
                Map.of("status", "failed", "message", "Unsubscription failed for " + serviceName);
    }

    /**
     * Get user phone number from DB
     */
    private String getUserPhoneNumber(int userId) {
        String sql = "SELECT user_phone_number FROM user WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, String.class);
    }
}
