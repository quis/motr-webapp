package uk.gov.dvsa.motr.web.cookie;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import static uk.gov.dvsa.motr.web.system.SystemVariable.FEATURE_TOGGLE_SMS;

@Singleton
public class MotrSession {

    private static final String VRM_COOKIE_ID = "regNumber";
    private static final String EMAIL_COOKIE_ID = "email";
    private static final String PHONE_NUMBER_COOKIE_ID = "phoneNumber";
    private static final String CHANNEL_COOKIE_ID = "channel";
    private static final String VEHICLE_DETAILS_ID = "vehicleDetails";
    private static final String VISITING_FROM_REVIEW_COOKIE_ID = "visitingFromReview";
    private static final String VISITING_FROM_CONTACT_ENTRY_COOKIE_ID = "visitingFromContactEntry";
    private static final String UNSUBSCRIBE_CONFIRMATION_PARAMS = "unsubscribeConfirmationParams";
    private static final String EMAIL_CONFIRMATION_PARAMS = "emailConfirmationParams";
    private static final String EMAIL_CHANNEL = "email";
    private static final String TEXT_CHANNEL = "text";

    private Map<String, Object> attributes;

    private boolean shouldClearCookies;
    private boolean allowedOnChannelSelectionPage;
    private boolean allowedOnPhoneNumberEntryPage;
    private boolean smsFeatureToggle;

    public MotrSession(@SystemVariableParam(FEATURE_TOGGLE_SMS) Boolean featureToggleSms) {

        this.attributes = new HashMap<>();
        this.allowedOnChannelSelectionPage = featureToggleSms;
        this.allowedOnPhoneNumberEntryPage = featureToggleSms;
        this.smsFeatureToggle = featureToggleSms;
    }

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

    public String getPhoneNumberFromSession() {

        Object phoneNumberFromSession = getAttribute(PHONE_NUMBER_COOKIE_ID);
        return phoneNumberFromSession == null ? "" : phoneNumberFromSession.toString();
    }

    public String getChannelFromSession() {

        Object channelFromSession = getAttribute(CHANNEL_COOKIE_ID);
        return channelFromSession == null ? "" : channelFromSession.toString();
    }

    public boolean isUsingEmailChannel() {

        return getChannelFromSession().equals(EMAIL_CHANNEL);
    }

    public boolean isUsingSmsChannel() {

        return getChannelFromSession().equals(TEXT_CHANNEL);
    }

    public VehicleDetails getVehicleDetailsFromSession() {

        return (VehicleDetails) getAttribute(VEHICLE_DETAILS_ID);
    }

    public EmailConfirmationParams getEmailConfirmationParams() {

        return (EmailConfirmationParams) getAttribute(EMAIL_CONFIRMATION_PARAMS);
    }

    public UnsubscribeConfirmationParams getUnsubscribeConfirmationParams() {

        return (UnsubscribeConfirmationParams) getAttribute(UNSUBSCRIBE_CONFIRMATION_PARAMS);
    }

    public boolean visitingFromReviewPage() {

        Object visitingFromReviewPage = getAttribute(VISITING_FROM_REVIEW_COOKIE_ID);
        return (visitingFromReviewPage != null && ((Boolean) visitingFromReviewPage));
    }

    public boolean visitingFromContactEntryPage() {

        Object visitingFromContactEntryPage = getAttribute(VISITING_FROM_CONTACT_ENTRY_COOKIE_ID);
        return (visitingFromContactEntryPage != null && ((Boolean) visitingFromContactEntryPage));
    }

    public boolean isAllowedOnEmailPage() {

        return !getVrmFromSession().isEmpty();
    }

    public boolean isAllowedOnChannelSelectionPage() {

        return allowedOnChannelSelectionPage && !getVrmFromSession().isEmpty();
    }

    public boolean isAllowedOnPhoneNumberEntryPage() {

        return allowedOnPhoneNumberEntryPage && (!getVrmFromSession().isEmpty() && !getVrmFromSession().equals(""));
    }

    public boolean isAllowedOnReviewPage() {

        return isAllowedOnEmailPage() && (!getEmailFromSession().isEmpty() || !getPhoneNumberFromSession().isEmpty());
    }

    public void setVisitingFromReview(boolean visitingFromReview) {

        this.setAttribute(VISITING_FROM_REVIEW_COOKIE_ID, visitingFromReview);
    }

    public void setVisitingFromContactEntry(boolean visitingFromContactEntry) {

        this.setAttribute(VISITING_FROM_CONTACT_ENTRY_COOKIE_ID, visitingFromContactEntry);
    }

    public void setEmail(String emailValue) {

        this.setAttribute(EMAIL_COOKIE_ID, emailValue);
    }

    public void setPhoneNumber(String phoneNumberValue) {

        this.setAttribute(PHONE_NUMBER_COOKIE_ID, phoneNumberValue);
    }

    public void setVrm(String vrmValue) {

        this.setAttribute(VRM_COOKIE_ID, vrmValue);
    }

    public void setVehicleDetails(VehicleDetails vehicleDetails) {

        this.setAttribute(VEHICLE_DETAILS_ID, vehicleDetails);
    }

    public void setChannel(String channel) {

        this.setAttribute(CHANNEL_COOKIE_ID, channel);
    }

    public void setEmailConfirmationParams(EmailConfirmationParams subscription) {

        this.setAttribute(EMAIL_CONFIRMATION_PARAMS, subscription);
    }

    public void setUnsubscribeConfirmationParams(UnsubscribeConfirmationParams unsubscribeConfirmationParams) {

        this.setAttribute(UNSUBSCRIBE_CONFIRMATION_PARAMS, unsubscribeConfirmationParams);
    }

    public boolean isSmsFeatureToggleOn() {

        return smsFeatureToggle;
    }

    protected void setAttribute(String attributeKey, Object attributeValue) {

        this.attributes.put(attributeKey, attributeValue);
    }

    protected Object getAttribute(String attributeKey) {

        return this.attributes.get(attributeKey);
    }

    protected boolean isShouldClearCookies() {

        return shouldClearCookies;
    }

    protected Map<String, Object> getAttributes() {

        return attributes;
    }

    public void clear() {

        this.attributes.clear();
        shouldClearCookies = false;
    }

    @Override
    public String toString() {
        return "MotrSession{" +
                "attributes=" + attributes +
                '}';
    }
}
