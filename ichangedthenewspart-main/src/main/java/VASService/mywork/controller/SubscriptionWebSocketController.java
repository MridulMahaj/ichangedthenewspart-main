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
    private Map<String, Object> sendOtp(int userId, String serviceName, String type) {
        String phone = getUserPhoneNumber(userId);

        // Simulated OTP for testing
        String simulatedOtp = "1234";
        subscriptionService.storeOtp(phone, simulatedOtp);

        return Map.of(
                "status", "otp_sent",
                "message", "OTP sent for " + type + " to phone " + phone,
                "simulatedOtp", simulatedOtp // For testing only
        );
    }

    private Map<String, Object> verifyAndSubscribe(int userId, String serviceName, String otp) {
        String phone = getUserPhoneNumber(userId);
        if (!subscriptionService.verifyOtp(phone, otp)) {
            return Map.of("status", "failed", "message", "OTP verification failed");
        }

        boolean success = subscriptionService.subscribeUser(String.valueOf(userId), serviceName);
        return success ?
                Map.of("status", "success", "message", "Subscription successful for " + serviceName) :
                Map.of("status", "failed", "message", "Subscription failed for " + serviceName);
    }

    private Map<String, Object> verifyAndUnsubscribe(int userId, String serviceName, String otp) {
        String phone = getUserPhoneNumber(userId);
        if (!subscriptionService.verifyOtp(phone, otp)) {
            return Map.of("status", "failed", "message", "OTP verification failed");
        }

        boolean success = subscriptionService.unsubscribeUser(String.valueOf(userId), serviceName);
        return success ?
                Map.of("status", "success", "message", "Unsubscription successful for " + serviceName) :
                Map.of("status", "failed", "message", "Unsubscription failed for " + serviceName);
    }

    private String getUserPhoneNumber(int userId) {
        String sql = "SELECT user_phone_number FROM user WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, String.class);
    }
}
