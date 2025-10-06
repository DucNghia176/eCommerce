package ecommerce.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.TimeUnit;

@FeignClient(name = "REDIS", path = "/api/redis")
public interface RedisClient {
    // BLACKLIST token
    @PostMapping("/token/blacklist")
    ResponseEntity<String> tokenBlacklist(@RequestParam String token,
                                          @RequestParam long ttl,
                                          @RequestParam TimeUnit ttlUnit);

    // CHECK token blacklisted
    @GetMapping("/token/exists")
    boolean tokenExists(@RequestParam String token);

    // DELETE token from blacklist
    @DeleteMapping("/token/del")
    ResponseEntity<String> tokenDelCache(@RequestParam String token);
}
