package VASService.mywork.classes;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageCentralConfig {

    @Value("${messagecentral.authToken}")
    private String authToken;
    @Value("${messagecentral.customerId}")
    private String customerId;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    // Getters for config values
    public String getAuthToken() {
        return authToken;
    }

    public String getCustomerId() {
        return customerId;
    }
}
