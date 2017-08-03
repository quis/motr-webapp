package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsService;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.subscription.InvalidSubscriptionConfirmationIdUsedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionConfirmationFailedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionConfirmedEvent;
import uk.gov.dvsa.motr.web.formatting.MakeModelFormatter;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.component.subscription.service.RandomIdGenerator.generateId;

public class SubscriptionConfirmationService {

    private final NotifyService notifyService;
    private final UrlHelper urlHelper;
    private final PendingSubscriptionRepository pendingSubscriptionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private VehicleDetailsClient client;

    @Inject
    public SubscriptionConfirmationService(
            PendingSubscriptionRepository pendingSubscriptionRepository,
            SubscriptionRepository subscriptionRepository,
            NotifyService notifyService,
            UrlHelper urlHelper,
            VehicleDetailsClient client
    ) {

        this.pendingSubscriptionRepository = pendingSubscriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notifyService = notifyService;
        this.urlHelper = urlHelper;
        this.client = client;
    }

    public Subscription confirmSubscription(String confirmationId) throws InvalidConfirmationIdException {

        return pendingSubscriptionRepository.findByConfirmationId(confirmationId)
                .map(this::confirm)
                .orElseThrow(() -> {
                    EventLogger.logEvent(new InvalidSubscriptionConfirmationIdUsedEvent().setUsedId(confirmationId));
                    return new InvalidConfirmationIdException();
                });
    }

    private Subscription confirm(PendingSubscription pendingSubscription) {

        try {
            Subscription subscription = applyPendingSubscription(pendingSubscription);
            sendSubscriptionConfirmationEmail(subscription);

            EventLogger.logEvent(new SubscriptionConfirmedEvent()
                    .setVrm(subscription.getVrm())
                    .setEmail(subscription.getEmail())
                    .setDueDate(subscription.getMotDueDate()));

            return subscription;

        } catch (Exception e) {

            EventLogger.logErrorEvent(new SubscriptionConfirmationFailedEvent()
                    .setVrm(pendingSubscription.getVrm())
                    .setEmail(pendingSubscription.getEmail())
                    .setDueDate(pendingSubscription.getMotDueDate()));
            throw e;
        }
    }

    private Subscription applyPendingSubscription(PendingSubscription pendingSubscription) {

        Subscription subscription = new Subscription()
                .setUnsubscribeId(generateId())
                .setVrm(pendingSubscription.getVrm())
                .setEmail(pendingSubscription.getEmail())
                .setMotDueDate(pendingSubscription.getMotDueDate())
                .setMotIdentification(pendingSubscription.getMotIdentification());

        subscriptionRepository.save(subscription);
        pendingSubscriptionRepository.delete(pendingSubscription);

        return subscription;
    }

    private void sendSubscriptionConfirmationEmail(Subscription subscription) {

        VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(subscription.getVrm(), client);

        notifyService.sendSubscriptionConfirmationEmail(
                subscription.getEmail(),
                MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails, ", ") + subscription.getVrm(),
                subscription.getMotDueDate(),
                urlHelper.unsubscribeLink(subscription.getUnsubscribeId()),
                subscription.getMotIdentification());
    }
}
