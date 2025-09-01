package VASService.mywork.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import VASService.mywork.classes.Admin;

@Service
public class AdminRegisterService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    public void register_admin(Admin admin){
        String sql = "INSERT INTO admin(admin_name, admin_password) values(?,?)";
        jdbcTemplate.update(sql, admin.getAdmin_name(), admin.getAdmin_password());
    }

}
