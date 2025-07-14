package ecommerce.cartservice.client;

import ecommerce.aipcommon.client.ProductClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "product-service", contextId = "cartProductClient", path = "/product")
public interface CartProductClient extends ProductClient {
}
