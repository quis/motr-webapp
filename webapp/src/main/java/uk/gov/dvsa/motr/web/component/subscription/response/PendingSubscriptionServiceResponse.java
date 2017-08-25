package uk.gov.dvsa.motr.web.component.subscription.response;

public class PendingSubscriptionServiceResponse {

    private String redirectUri;
    private String confirmationId;

    public String getRedirectUri() {

        return redirectUri;
    }

    public PendingSubscriptionServiceResponse setRedirectUri(String redirectUri) {

        this.redirectUri = redirectUri;
        return this;
    }

    public String getConfirmationId() {

        return confirmationId;
    }

    public PendingSubscriptionServiceResponse setConfirmationId(String confirmationId) {

        this.confirmationId = confirmationId;
        return this;
    }
}
