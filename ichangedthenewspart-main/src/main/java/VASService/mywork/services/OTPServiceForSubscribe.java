package VASService.mywork.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OTPServiceForSubscribe {
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> sendOtp(String phone) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(Map.of("user_phone_number", phone), headers);

        return restTemplate.postForObject("http://localhost:8080/sendotp", entity, Map.class);
    }

    public Map<String, Object> verifyOtp(String phone, String otp) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(Map.of(
                "user_phone_number", phone,
                "otp", otp
        ), headers);

        return restTemplate.postForObject("http://localhost:8080/verifyotp", entity, Map.class);
    }
}
