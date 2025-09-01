package VASService.mywork.classes;

public class Subscription {
    private int id;
    private int userId;
    private String serviceName;
    private int billingId;
    private boolean active;
    private String subscriptionDate;
    private String updatedAt;

    // ✅ Add these setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setBillingId(int billingId) {
        this.billingId = billingId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSubscriptionDate(String subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ✅ Optionally add getters too
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getBillingId() {
        return billingId;
    }

    public boolean isActive() {
        return active;
    }

    public String getSubscriptionDate() {
        return subscriptionDate;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
