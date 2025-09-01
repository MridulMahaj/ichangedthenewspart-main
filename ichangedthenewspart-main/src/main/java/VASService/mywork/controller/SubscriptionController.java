package VASService.mywork.controller;

import VASService.mywork.classes.Subscription;
import VASService.mywork.services.OTPServiceForSubscribe;
import VASService.mywork.services.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final OTPServiceForSubscribe otpService;

    public SubscriptionController(SubscriptionService subscriptionService, OTPServiceForSubscribe otpService) {
        this.subscriptionService = subscriptionService;
        this.otpService = otpService;
    }

    // ==================== SUBSCRIBE ====================
    @PostMapping("/subscribe")
    public ResponseEntity<Object> subscribe(@RequestParam String userId, @RequestParam String serviceName) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        if (phone == null) {
            return ResponseEntity.status(404).body(Map.of("status", "error", "message", "User not found"));
        }

        try {
            Map<String, Object> otpResponse = otpService.sendOtp(phone);
            if (!"success".equalsIgnoreCase((String) otpResponse.get("status"))) {
                return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", "OTP sending failed"));
            }

            return ResponseEntity.ok(Map.of(
                    "status", "otp_sent",
                    "verificationId", otpResponse.get("verificationId"),
                    "message", "OTP sent to " + phone + " for service " + serviceName
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "OTP service failed: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-subscribe")
    public ResponseEntity<Object> verifySubscribe(@RequestParam String userId,
                                                  @RequestParam String serviceName,
                                                  @RequestParam String otp) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        if (phone == null) {
            return ResponseEntity.status(404).body(Map.of("status", "error", "message", "User not found"));
        }

        try {
            Map<String, Object> verifyResponse = otpService.verifyOtp(phone, otp);
            if (!"success".equalsIgnoreCase((String) verifyResponse.get("status"))) {
                return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", "OTP verification failed"));
            }

            boolean success = subscriptionService.subscribeUser(userId, serviceName);
            Subscription updated = subscriptionService.getLatestSubscription(userId, serviceName);

            if (success && updated != null) {
                return ResponseEntity.ok(Map.of("status", "success", "subscription", updated));
            } else {
                return ResponseEntity.status(500).body(Map.of("status", "failed", "message", "Subscription failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Verification failed: " + e.getMessage()));
        }
    }

    // ==================== UNSUBSCRIBE ====================
    @PostMapping("/unsubscribe")
    public ResponseEntity<Object> unsubscribe(@RequestParam String userId, @RequestParam String serviceName) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        if (phone == null) {
            return ResponseEntity.status(404).body(Map.of("status", "error", "message", "User not found"));
        }

        try {
            Map<String, Object> otpResponse = otpService.sendOtp(phone);
            if (!"success".equalsIgnoreCase((String) otpResponse.get("status"))) {
                return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", "OTP sending failed"));
            }

            return ResponseEntity.ok(Map.of(
                    "status", "otp_sent",
                    "verificationId", otpResponse.get("verificationId"),
                    "message", "OTP sent to " + phone + " for unsubscribing from " + serviceName
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "OTP service failed: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-unsubscribe")
    public ResponseEntity<Object> verifyUnsubscribe(@RequestParam String userId,
                                                    @RequestParam String serviceName,
                                                    @RequestParam String otp) {
        String phone = subscriptionService.getUserPhoneNumber(userId);
        if (phone == null) {
            return ResponseEntity.status(404).body(Map.of("status", "error", "message", "User not found"));
        }

        try {
            Map<String, Object> verifyResponse = otpService.verifyOtp(phone, otp);
            if (!"success".equalsIgnoreCase((String) verifyResponse.get("status"))) {
                return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", "OTP verification failed"));
            }

            boolean success = subscriptionService.unsubscribeUser(userId, serviceName);
            Subscription updated = subscriptionService.getLatestSubscription(userId, serviceName);

            if (success && updated != null) {
                return ResponseEntity.ok(Map.of("status", "success", "subscription", updated));
            } else {
                return ResponseEntity.status(500).body(Map.of("status", "failed", "message", "Unsubscription failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Verification failed: " + e.getMessage()));
        }
    }
}
