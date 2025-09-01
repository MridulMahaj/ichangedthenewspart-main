package VASService.mywork.services;


import org.springframework.stereotype.Service;


@Service
public class SubscriptionService {

    private final SubscriptionDAO subscriptionDAO;
    private final BillingService billingService;

    public SubscriptionService(SubscriptionDAO subscriptionDAO, BillingService billingService) {
        this.subscriptionDAO = subscriptionDAO;
        this.billingService = billingService;
    }

    public String subscribe(int userId, String serviceName) {
        int billingId = billingService.chargeUser(userId, serviceName);
        subscriptionDAO.saveSubscription(userId, serviceName, billingId);
        return "Subscription successful for user " + userId + " on " + serviceName;
    }

    public String unsubscribe(int userId, String serviceName) {
        subscriptionDAO.deactivateSubscription(userId, serviceName);
        return "Unsubscribed user " + userId + " from " + serviceName;
    }
}
