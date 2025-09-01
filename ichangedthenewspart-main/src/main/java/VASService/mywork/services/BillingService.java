package VASService.mywork.services;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BillingService {
    private final AtomicInteger billingCounter = new AtomicInteger(1000);

    public int chargeUser(String userId, String serviceName) {
        System.out.println("Charging user " + userId + " for service " + serviceName);
        return billingCounter.incrementAndGet();
    }
}
