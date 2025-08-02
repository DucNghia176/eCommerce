package ecommerce.userservice.email;

import ecommerce.userservice.dto.request.PendingRegistration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingRegistrationStorage {
    private final Map<String, PendingRegistration> storage = new ConcurrentHashMap<>();

    public void store(String email, PendingRegistration registration) {
        storage.put(email, registration);
    }

    public PendingRegistration get(String email) {
        return storage.get(email);
    }

    public void remove(String email) {
        storage.remove(email);
    }
}


