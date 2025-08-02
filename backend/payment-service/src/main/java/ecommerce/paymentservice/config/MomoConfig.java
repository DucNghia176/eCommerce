package ecommerce.paymentservice.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "momo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomoConfig {
    private String endpoint;
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String redirectUrl;
    private String notifyUrl;
}
