package VASService.mywork.controller;
import VASService.mywork.classes.User;
import VASService.mywork.services.UserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRegisterController {
    @Autowired
    private UserRegister userRegister;

    @PostMapping("/register")
    public  String register_user(@RequestBody User user){
        userRegister.register_user(user);
        return "User registered successfully !!!";
    }




}
