package ecommerce.redis.controller;

import ecommerce.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/redis")
public class RedisController {
    private final RedisService redisService;

    // PUT cache không TTL
    @PostMapping("/put")
    public void putCache(@RequestParam String key, @RequestBody Object value) {
        redisService.putCache(key, value);
    }

    // PUT cache có TTL
    @PostMapping("/put-ttl")
    public void putTtlCache(@RequestParam String key,
                            @RequestBody Object value,
                            @RequestParam long ttl,
                            @RequestParam TimeUnit ttlUnit) {
        redisService.putCache(key, value, ttl, ttlUnit);
    }

    // GET cache
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getCache(@RequestParam String key) {
        return redisService.getCache(key);
    }

    // CHECK EXISTS
    @GetMapping("/exists")
    public boolean exists(@RequestParam String key) {
        return redisService.existsCache(key);
    }

    // DELETE cache
    @DeleteMapping("/del")
    public void delCache(@RequestParam String key) {
        redisService.delCache(key);
    }

    // BLACKLIST token
    @PostMapping("/token/blacklist")
    public void tokenBlacklist(@RequestParam String token,
                               @RequestParam long ttl,
                               @RequestParam TimeUnit ttlUnit) {
        redisService.blacklistCache(token, ttl, ttlUnit);
    }


    // CHECK token blacklisted
    @GetMapping("/token/exists")
    public boolean tokenExists(@RequestParam String token) {
        return redisService.isTokenBlacklisted(token);
    }

    // DELETE token from blacklist
    @DeleteMapping("/token/del")
    public void tokenDelCache(@RequestParam String token) {
        redisService.removeTokenBlacklisted(token);
    }
}
