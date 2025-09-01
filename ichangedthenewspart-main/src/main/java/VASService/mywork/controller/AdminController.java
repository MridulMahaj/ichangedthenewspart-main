package VASService.mywork.controller;

import VASService.mywork.services.AdminRegisterService;
import VASService.mywork.services.AdminLoginService;
import VASService.mywork.classes.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AdminController {
    @Autowired
    private AdminRegisterService adminRegisterService;

    @Autowired
    private AdminLoginService adminLoginService;

    @PostMapping("/adminregister")
    public void register_admin(@RequestBody Admin admin) {
        adminRegisterService.register_admin(admin);
    }



    @GetMapping("/adminlogin")
    public List<Map<String, Object>> login_admin(@RequestParam String admin_name) {
        return adminLoginService.login_admin(admin_name);
    }

}
