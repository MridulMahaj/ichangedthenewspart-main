package VASService.mywork.controller;

import VASService.mywork.services.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
public class OTPControllerClass {

    @Autowired
    private OTPService otpService;

    @PostMapping("/sendotp")
    public Map<String, String> sendOtp(@RequestBody Map<String, String> request) throws IOException {
        String user_phone_number = request.get("user_phone_number");

        if (user_phone_number == null || user_phone_number.length() != 10 || !user_phone_number.matches("\\d+")) {
            return Map.of("status", "error", "message", "Invalid mobile number");
        }

        String verificationId = otpService.sendOtp(user_phone_number);
        if (verificationId != null) {
            return Map.of("status", "success", "verificationId", verificationId);
        } else {
            return Map.of("status", "error", "message", "Failed to send OTP");
        }
    }

    @PostMapping("/verifyotp")
    public Map<String, String> verifyOtp(@RequestBody Map<String, Object> request) throws IOException {
        String user_phone_number = (String) request.get("user_phone_number");
        Object otpObj = request.get("otp");

        if (user_phone_number == null || otpObj == null) {
            return Map.of("status", "error", "message", "Missing parameters");
        }

        String otp = otpObj.toString();

        boolean isValid = otpService.validateOtp(user_phone_number, otp);

        if (isValid) {
            return Map.of("status", "success", "message", "OTP verified successfully");
        } else {
            return Map.of("status", "error", "message", "Invalid OTP");
        }
    }


}
