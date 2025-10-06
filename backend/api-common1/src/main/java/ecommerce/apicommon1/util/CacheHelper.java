package ecommerce.apicommon1.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.apicommon1.client.RedisClient;
import ecommerce.apicommon1.model.response.CachedPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CacheHelper {

    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;

    /**
     * Lấy dữ liệu từ Redis nếu có.
     */
    public <T> Page<T> getPageCache(String redisKey, Class<T> clazz) {
        Object obj = redisClient.getCache(redisKey);
        CachedPage<?> cachedPage = objectMapper.convertValue(obj, CachedPage.class);
        if (cachedPage == null) return null;

        return new PageImpl<>(
                objectMapper.convertValue(
                        cachedPage.getContent(),
                        objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, clazz)
                ),
                PageRequest.of(cachedPage.getPageNumber(), cachedPage.getPageSize()),
                cachedPage.getTotalElements()
        );
    }

    /**
     * Lưu dữ liệu lên Redis với TTL.
     */
    public <T> void setPageCache(String redisKey, Page<T> page, long ttl, TimeUnit ttlUnit) {
        CachedPage<T> cacheObj = new CachedPage<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
        redisClient.putTtlCache(redisKey, cacheObj, ttl, ttlUnit);
    }
}
