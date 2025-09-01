package VASService.mywork.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionDAO {
    private final JdbcTemplate jdbcTemplate;

    public SubscriptionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveSubscription(int userId, String serviceName, int billingId) {
        String sql = "INSERT INTO subscriptions (user_id, service_name, billing_id, active) VALUES (?, ?, ?, 1)";
        jdbcTemplate.update(sql, userId, serviceName, billingId);
    }

    public void deactivateSubscription(int userId, String serviceName) {
        String sql = "UPDATE subscriptions SET active = 0 WHERE user_id = ? AND service_name = ?";
        jdbcTemplate.update(sql, userId, serviceName);
    }
}
