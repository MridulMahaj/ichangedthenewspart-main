package VASService.mywork.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BillingService {

    private final AtomicInteger billingCounter = new AtomicInteger(1000); // start from 1000

    /**
     * Mock billing logic: generates a new billing ID for the user + service.
     */
    public int chargeUser(String userId, String serviceName) {
        // In real world: call payment gateway API here.
        System.out.println("Charging user " + userId + " for service " + serviceName);

        return billingCounter.incrementAndGet(); // generate unique billing_id
    }
}
