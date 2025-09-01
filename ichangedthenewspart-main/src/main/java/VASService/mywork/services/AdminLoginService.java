package VASService.mywork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import VASService.mywork.classes.Admin;


import java.util.List;
import java.util.Map;


@Service
public class AdminLoginService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> login_admin(String admin_name) {
        String sql = "SELECT * FROM admin WHERE admin_name = ?";
        return jdbcTemplate.queryForList(sql, admin_name);
    }
}
