package ecommerce.apicommon1.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@FeignClient(name = "REDIS", path = "/api/redis")
public interface RedisClient {
    @PostMapping("/put")
    void putCache(@RequestParam String key, @RequestBody Object value);

    // PUT cache có TTL
    @PostMapping("/put-ttl")
    void putTtlCache(@RequestParam String key,
                     @RequestBody Object value,
                     @RequestParam long ttl,
                     @RequestParam TimeUnit ttlUnit);

    // GET cache
    @GetMapping("/get")
    Object getCache(@RequestParam String key);

    // CHECK EXISTS
    @GetMapping("/exists")
    boolean exists(@RequestParam String key);

    // DELETE cache
    @DeleteMapping("/del")
    String delCache(@RequestParam String key);

    // BLACKLIST token
    @PostMapping("/token/blacklist")
    String tokenBlacklist(@RequestParam String token,
                          @RequestParam long ttl,
                          @RequestParam TimeUnit ttlUnit);


    // CHECK token blacklisted
    @GetMapping("/token/exists")
    boolean tokenExists(@RequestParam String token);

    // DELETE token from blacklist
    @DeleteMapping("/token/del")
    String tokenDelCache(@RequestParam String token);
}
