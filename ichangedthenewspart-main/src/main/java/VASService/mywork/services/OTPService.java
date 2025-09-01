package VASService.mywork.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${messagecentral.authToken}")
    private String authToken;

    @Value("${messagecentral.customerId}")
    private String customerId;

    private static final String BASE_URL = "https://cpaas.messagecentral.com/verification/v3";

    // Store verificationId per mobileNumber
    private final Map<String, String> verificationIdMap = new ConcurrentHashMap<>();

    public String sendOtp(String mobileNumber) throws IOException {
        String url = String.format("%s/send?countryCode=91&customerId=%s&flowType=SMS&mobileNumber=%s",
                BASE_URL, customerId, mobileNumber);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(new byte[0]))
                .addHeader("authToken", authToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                JsonNode rootNode = objectMapper.readTree(body);
                JsonNode dataNode = rootNode.path("data");
                String verificationId = dataNode.path("verificationId").asText();

                // Save verificationId against mobile number
                verificationIdMap.put(mobileNumber, verificationId);

                return verificationId;
            } else {
                throw new IOException("Failed to send OTP: HTTP " + response.code());
            }
        }
    }

    public boolean validateOtp(String mobileNumber, String otp) throws IOException {
        // Get verificationId from the map
        String verificationId = verificationIdMap.get(mobileNumber);
        if (verificationId == null) {
            System.err.println("No verificationId found for " + mobileNumber);
            return false;
        }

        String url = String.format(
                "%s/validateOtp?countryCode=91&mobileNumber=%s&verificationId=%s&customerId=%s&code=%s",
                BASE_URL, mobileNumber, verificationId, customerId, otp
        );

        System.out.println("Calling validateOtp API: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("authToken", authToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            System.out.println("validateOtp Response Body: " + body);

            if (response.isSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(body);
                JsonNode dataNode = rootNode.path("data");
                String verificationStatus = dataNode.path("verificationStatus").asText();

                System.out.println("Parsed verificationStatus: " + verificationStatus);
                return "VERIFICATION_COMPLETED".equalsIgnoreCase(verificationStatus);
            } else {
                System.err.println("validateOtp HTTP error: " + response.code());
                return false;
            }
        }
    }
}
