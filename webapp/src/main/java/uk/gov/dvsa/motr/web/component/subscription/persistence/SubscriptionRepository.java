package uk.gov.dvsa.motr.web.component.subscription.persistence;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.util.List;
import java.util.Optional;


public interface SubscriptionRepository {

    Optional<Subscription> findByUnsubscribeId(String id);

    Optional<Subscription> findByVrmAndEmail(String vrm, String email);

    /**
     * Will also find by phone number. The column storing both phone numbers
     * and email addresses is simply called "email".
     *
     * @param email the email address (or phone number) to find by.
     * @return A list of subscriptions matching the email parameter.
     */
    List<Subscription> findByEmail(String email);

    void save(Subscription subscription);

    void delete(Subscription subscription);
}
