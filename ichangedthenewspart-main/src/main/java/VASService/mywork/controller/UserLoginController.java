package VASService.mywork.controller;

import VASService.mywork.services.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class UserLoginController {

    @Autowired
    private UserLogin userLogin;

    @GetMapping("/login")
    public List<Map<String, Object>> login(@RequestParam Long user_phone_number) {
        return userLogin.login(user_phone_number);
    }
}
