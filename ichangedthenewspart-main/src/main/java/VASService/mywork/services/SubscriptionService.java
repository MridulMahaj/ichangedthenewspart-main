package VASService.mywork.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final JdbcTemplate jdbcTemplate;
    private final BillingService billingService; // for billing_id

    public SubscriptionService(JdbcTemplate jdbcTemplate, BillingService billingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.billingService = billingService;
    }

    // Subscribe user
    public boolean subscribeUser(String userId, String serviceName) {
        try {
            // bill user → get billing_id
            int billingId = billingService.chargeUser(userId, serviceName);

            String sql = "INSERT INTO subscriptions (user_id, service_name, billing_id, active) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, userId, serviceName, billingId, 1);

            return true;
        } catch (Exception e) {
            System.err.println("Error subscribing user: " + e.getMessage());
            return false;
        }
    }

    // Unsubscribe user
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
}
