package uk.gov.dvsa.motr.web.component.subscription.persistence;

import uk.gov.dvsa.motr.web.component.subscription.model.SmsConfirmation;

import java.util.Optional;

public interface SmsConfirmationRepository {

    Optional<SmsConfirmation> findByConfirmationId(String id);

    void save(SmsConfirmation smsConfirmation);
}
