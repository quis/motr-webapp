package uk.gov.dvsa.motr.web.eventlog.subscription;

public class SmsConfirmationCreatedEvent extends SmsConfirmationEvent {

    @Override
    public String getCode() {

        return "SMS-CONFIRMATION-CREATED";
    }
}
