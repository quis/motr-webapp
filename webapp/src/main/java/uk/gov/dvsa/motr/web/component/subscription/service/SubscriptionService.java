package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;

import java.util.List;

import javax.inject.Inject;

public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Inject
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {

        this.subscriptionRepository = subscriptionRepository;
    }

    public boolean hasMaxTwoSubscriptionsForPhoneNumber(String number) {

        List<Subscription> subscriptions = subscriptionRepository.findByEmail(number);

        return subscriptions.size() < 2;
    }
}
