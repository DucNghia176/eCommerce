package ecommerce.apicommon1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean("contextAwareExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("asyncExecutor-");
        executor.initialize();
        return command -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            executor.submit(() -> {
                try {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    command.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            });
        };
    }
}
