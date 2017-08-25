package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.component.subscription.persistence.CancelledSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.DynamoDbCancelledSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.DynamoDbPendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.DynamoDbSmsConfirmationRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.DynamoDbSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SmsConfirmationRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;

public class RepositoryBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(DynamoDbSubscriptionRepository.class).to(SubscriptionRepository.class);
        bind(DynamoDbPendingSubscriptionRepository.class).to(PendingSubscriptionRepository.class);
        bind(DynamoDbSmsConfirmationRepository.class).to(SmsConfirmationRepository.class);
        bind(DynamoDbCancelledSubscriptionRepository.class).to(CancelledSubscriptionRepository.class);
    }
}
