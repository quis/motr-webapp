package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.helper.UnsubscriptionUrlHelper;

import java.time.LocalDate;
import java.util.UUID;

import javax.inject.Inject;

import static java.lang.String.format;

public class SubscriptionService {

    private SubscriptionRepository subscriptionRepository;
    private NotifyService notifyService;
    private UnsubscriptionUrlHelper unsubscriptionUrlHelper;

    @Inject
    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            NotifyService notifyService,
            UnsubscriptionUrlHelper unsubscriptionUrlHelper
    ) {

        this.subscriptionRepository = subscriptionRepository;
        this.notifyService = notifyService;
        this.unsubscriptionUrlHelper = unsubscriptionUrlHelper;
    }

    public void createSubscription(String vrm, String email, LocalDate motDueDate) throws Exception {

        if (!doesSubscriptionAlreadyExist(vrm, email)) {
            Subscription subscription = new Subscription(UUID.randomUUID().toString())
                    .setEmail(email)
                    .setVrm(vrm)
                    .setMotDueDate(motDueDate);
            notifyService.sendConfirmationEmail(email, vrm, motDueDate, unsubscriptionUrlHelper.build(subscription.getId()));
            subscriptionRepository.save(subscription);
        } else {
            throw new SubscriptionAlreadyExistsException(format("A subscription exists for vehicle: %s with an email of: %s", vrm, email));
        }
    }

    private boolean doesSubscriptionAlreadyExist(String vrm, String email) {

        return subscriptionRepository.findByVrmAndEmail(vrm, email).isPresent();
    }
}
