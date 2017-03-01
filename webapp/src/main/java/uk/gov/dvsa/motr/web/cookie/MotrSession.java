package uk.gov.dvsa.motr.web.cookie;

import java.util.HashMap;
import java.util.Map;

public class MotrSession {

    private static final String VRM_COOKIE_ID = "regNumber";
    private static final String EMAIL_COOKIE_ID = "email";
    private static final String CSRF = "__csrf";
    private static final String VISITING_FROM_REVIEW_COOKIE_ID = "visitingFromReview";
    private static final String UNSUBSCRIBE_CONFIRMATION_PARAMS = "unsubscribeConfirmationParams";

    private Map<String, Object> attributes = new HashMap<>();

    private boolean shouldClearCookies;


    public void setShouldClearCookies(boolean shouldClearCookies) {

        this.shouldClearCookies = shouldClearCookies;
    }

    public String getVrmFromSession() {

        Object regFromSession = getAttribute(VRM_COOKIE_ID);
        return regFromSession == null ? "" : regFromSession.toString();
    }

    public String getEmailFromSession() {

        Object emailFromSession = getAttribute(EMAIL_COOKIE_ID);
        return emailFromSession == null ? "" : emailFromSession.toString();
    }

    public UnsubscribeConfirmationParams getUnsubscribeConfirmationParams() {

        return (UnsubscribeConfirmationParams) getAttribute(UNSUBSCRIBE_CONFIRMATION_PARAMS);
    }

    public String getCsrfToken() {

        return getAttribute(CSRF).toString();
    }

    public boolean hasCsrfToken() {

        return getAttribute(CSRF) != null;
    }

    public void setCsrfToken(String token) {

        setAttribute(CSRF, token);
    }

    public boolean visitingFromReviewPage() {

        Object visitingFromReviewPage = getAttribute(VISITING_FROM_REVIEW_COOKIE_ID);
        return (visitingFromReviewPage != null && ((Boolean) visitingFromReviewPage));
    }

    public boolean isAllowedOnEmailPage() {

        return !getVrmFromSession().isEmpty();
    }

    public boolean isAllowedOnPage() {

        return isAllowedOnEmailPage() && !getEmailFromSession().isEmpty();
    }

    public void setVisitingFromReview(boolean visitingFromReview) {

        this.setAttribute(VISITING_FROM_REVIEW_COOKIE_ID, visitingFromReview);
    }

    public void setEmail(String emailValue) {

        this.setAttribute(EMAIL_COOKIE_ID, emailValue);
    }

    public void setVrm(String vrmValue) {

        this.setAttribute(VRM_COOKIE_ID, vrmValue);
    }

    public void setUnsubscribeConfirmationParams(UnsubscribeConfirmationParams unsubscribeConfirmationParams) {

        this.setAttribute(UNSUBSCRIBE_CONFIRMATION_PARAMS, unsubscribeConfirmationParams);
    }

    protected void setAttribute(String attributeKey, Object attributeValue) {

        this.attributes.put(attributeKey, attributeValue);
    }

    protected Object getAttribute(String attributeKey) {

        return this.attributes.get(attributeKey);
    }

    public boolean isShouldClearCookies() {

        return shouldClearCookies;
    }

    protected Map<String, Object> getAttributes() {

        return attributes;
    }

    @Override
    public String toString() {
        return "MotrSession{" +
                "attributes=" + attributes +
                '}';
    }
}
