package ecommerce.paymentservice.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaypalConfig {
    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public APIContext apiContext() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("mode", mode);
            config.put("oauth.EndPoint", "https://api.sandbox.paypal.com");
            config.put("service.EndPoint", "https://api.sandbox.paypal.com");
            config.put("clientId", clientId);
            config.put("clientSecret", clientSecret);

            OAuthTokenCredential authTokenCredential = new OAuthTokenCredential(clientId, clientSecret, config);
            APIContext context = new APIContext(authTokenCredential.getAccessToken());
            context.setConfigurationMap(config);
            return context;
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Lỗi khi khởi tạo PayPal APIContext", e);
        }
    }
}
