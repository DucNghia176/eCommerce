package ecommerce.aipcommon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {
        "ecommerce.aipcommon.client"
})
@SpringBootApplication
public class AipCommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(AipCommonApplication.class, args);
    }

}
