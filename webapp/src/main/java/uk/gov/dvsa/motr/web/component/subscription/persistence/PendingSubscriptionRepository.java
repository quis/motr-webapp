package uk.gov.dvsa.motr.web.component.subscription.persistence;

import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;

import java.util.Optional;


public interface PendingSubscriptionRepository {

    Optional<PendingSubscription> findByConfirmationId(String id);

    void save(PendingSubscription subscription);

    void delete(PendingSubscription pendingSubscription);

    public Optional<PendingSubscription> findByVrmAndContactDetails(String vrm, String contactDetails);
}
