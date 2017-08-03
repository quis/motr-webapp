package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;
import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class PendingSubscriptionEvent extends Event {

    public PendingSubscriptionEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public PendingSubscriptionEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public PendingSubscriptionEvent setMotDueDate(LocalDate motDueDate) {

        params.put("mot-due-date", motDueDate.format(DateTimeFormatter.ISO_DATE));
        return this;
    }

    public PendingSubscriptionEvent setMotIdentification(MotIdentification motIdentification) {

        if (motIdentification.getMotTestNumber().isPresent()) {
            params.put("mot-test-number", motIdentification.getMotTestNumber().get());
        } else {
            params.put("dvla-id", motIdentification.getDvlaId().get());
        }
        return this;
    }
}
