package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;

public class InvalidSmsConfirmationIdUsedEvent extends Event {

    @Override
    public String getCode() {

        return "INVALID-SMS-CONFIRMATION-ID-USED";
    }

    public InvalidSmsConfirmationIdUsedEvent setUsedId(String usedId) {

        params.put("used-id", usedId);
        return this;
    }

    public InvalidSmsConfirmationIdUsedEvent setPhoneNumber(String phoneNumber) {

        params.put("phone-number", phoneNumber);
        return this;
    }
}
