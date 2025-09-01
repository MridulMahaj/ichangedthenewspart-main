package VASService.mywork.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriptionService {

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, String> otpStore = new HashMap<>(); // phone -> OTP

    public SubscriptionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Subscribe user (insert into DB)
    public boolean subscribeUser(String userId, String serviceName) {
        try {
            int billingId = (int) (Math.random() * 10000); // simulate billing ID

            String sql = "INSERT INTO subscriptions (user_id, service_name, billing_id, active) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, userId, serviceName, billingId, 1);

            return true;
        } catch (Exception e) {
            System.err.println("Error subscribing user: " + e.getMessage());
            return false;
        }
    }

    // Unsubscribe user (deactivate)
    public boolean unsubscribeUser(String userId, String serviceName) {
        try {
            String sql = "UPDATE subscriptions SET active = 0 WHERE user_id = ? AND service_name = ?";
            int rows = jdbcTemplate.update(sql, userId, serviceName);
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error unsubscribing user: " + e.getMessage());
            return false;
        }
    }

    // OTP simulation
    public void storeOtp(String phone, String otp) {
        otpStore.put(phone, otp);
    }

    public boolean verifyOtp(String phone, String otp) {
        String correctOtp = otpStore.get(phone);
        return correctOtp != null && correctOtp.equals(otp);
    }
}
