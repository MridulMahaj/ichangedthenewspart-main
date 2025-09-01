package VASService.mywork.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final BillingService billingService;
    private final JdbcTemplate jdbcTemplate;

    public SubscriptionService(BillingService billingService, JdbcTemplate jdbcTemplate) {
        this.billingService = billingService;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Subscribe user
    public boolean subscribeUser(int userId, String serviceName) {
        try {
            int billingId = billingService.chargeUser(userId, serviceName);
            String sql = "INSERT INTO subscriptions (user_id, service_name, billing_id, active) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, userId, serviceName, billingId, 1);
            System.out.println("Inserted subscription for user " + userId + " with billing ID " + billingId);
            return true;
        } catch (Exception e) {
            System.err.println("Error in subscribeUser: " + e.getMessage());
            return false;
        }
    }

    // Unsubscribe user
    public boolean unsubscribeUser(int userId, String serviceName) {
        try {
            String sql = "UPDATE subscriptions SET active = 0, updated_at = NOW() WHERE user_id = ? AND service_name = ?";
            int rows = jdbcTemplate.update(sql, userId, serviceName);
            System.out.println("Deactivated subscription for user " + userId);
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error in unsubscribeUser: " + e.getMessage());
            return false;
        }
    }
}
