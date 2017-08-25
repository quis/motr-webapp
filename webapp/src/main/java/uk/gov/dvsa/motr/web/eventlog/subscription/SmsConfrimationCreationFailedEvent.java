package uk.gov.dvsa.motr.web.eventlog.subscription;

public class SmsConfrimationCreationFailedEvent extends SmsConfirmationEvent {

    @Override
    public String getCode() {

        return "SMS-CONFIRMATION-CREATION-FAILED";
    }
}
