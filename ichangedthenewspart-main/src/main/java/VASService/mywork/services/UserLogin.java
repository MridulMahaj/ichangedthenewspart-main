package VASService.mywork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class UserLogin {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> login(Long user_phone_number) {
        String sql = "SELECT * FROM user WHERE user_phone_number = ?";
        return jdbcTemplate.queryForList(sql, user_phone_number);
    }
}
