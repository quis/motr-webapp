package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;

public class NotifyClientFailedEvent extends Event {

    public enum Type {
        EMAIL_CONFIRMATION,
        SUBSCRIPTION_CONFIRMATION,
        SMS_ONE_TIME_PASSCODE_CONFIRMATION,
        SMS_SUBSCRIPTION_CONFIRMATION,
    }

    public NotifyClientFailedEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }


    public NotifyClientFailedEvent setType(Type type) {

        params.put("type", type.toString());
        return this;
    }

    @Override
    public String getCode() {

        return "NOTIFY-CLIENT-FAILED";
    }
}
