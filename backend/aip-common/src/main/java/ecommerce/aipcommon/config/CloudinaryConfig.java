package ecommerce.aipcommon.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dukqrda6g",
                "api_key", "216637823337147",
                "api_secret", "3E8ZnjzeXbx6b_QZJH2zIFbd9HM",
                "secure", true
        ));
    }
}
