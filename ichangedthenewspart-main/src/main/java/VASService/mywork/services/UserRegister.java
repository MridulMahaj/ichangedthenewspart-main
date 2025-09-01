package VASService.mywork.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import VASService.mywork.classes.User;

@Service
public class UserRegister {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    public void register_user(User user){
        String sql = "INSERT INTO User(user_name, user_email,user_phone_number, user_password) values(?,?,?,?)";
        jdbcTemplate.update(sql, user.getUser_name(), user.getUser_email(), user.getUser_phone_number(), user.getUser_password());
    }

}
