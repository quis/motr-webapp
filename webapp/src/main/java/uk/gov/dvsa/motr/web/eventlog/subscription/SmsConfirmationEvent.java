package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;
import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class SmsConfirmationEvent extends Event {

    public SmsConfirmationEvent setPhoneNumber(String phoneNumber) {

        params.put("phoneNumber", phoneNumber);
        return this;
    }

    public SmsConfirmationEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public SmsConfirmationEvent setConfirmationCode(String confirmationCode) {

        params.put("code", confirmationCode);
        return this;
    }

}
