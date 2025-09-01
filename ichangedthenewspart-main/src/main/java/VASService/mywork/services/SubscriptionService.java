package VASService.mywork.services;

import VASService.mywork.classes.Subscription;
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

    public boolean subscribeUser(String userId, String serviceName) {
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

    public boolean unsubscribeUser(String userId, String serviceName) {
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

    public String getUserPhoneNumber(String userId) {
        String sql = "SELECT user_phone_number FROM user WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, String.class);
    }

    public Subscription getLatestSubscription(String userId, String serviceName) {
        String sql = "SELECT * FROM subscriptions WHERE user_id = ? AND service_name = ? ORDER BY updated_at DESC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId, serviceName}, (rs, rowNum) -> {
            Subscription sub = new Subscription();
            sub.setId(rs.getInt("id"));
            sub.setUserId(rs.getInt("user_id"));
            sub.setServiceName(rs.getString("service_name"));
            sub.setBillingId(rs.getInt("billing_id"));
            sub.setActive(rs.getBoolean("active"));
            sub.setSubscriptionDate(rs.getString("subscription_date"));
            sub.setUpdatedAt(rs.getString("updated_at"));
            return sub;
        });
    }
}
