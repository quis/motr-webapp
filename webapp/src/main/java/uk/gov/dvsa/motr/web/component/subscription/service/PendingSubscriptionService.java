package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsService;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreatedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreationFailedEvent;
import uk.gov.dvsa.motr.web.formatting.MakeModelFormatter;

import java.time.LocalDate;
import java.util.Optional;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.component.subscription.service.RandomIdGenerator.generateId;

public class PendingSubscriptionService {

    private PendingSubscriptionRepository pendingSubscriptionRepository;
    private SubscriptionRepository subscriptionRepository;
    private NotifyService notifyService;
    private UrlHelper urlHelper;
    private VehicleDetailsClient client;

    @Inject
    public PendingSubscriptionService(
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

    public String handlePendingSubscriptionCreation(String vrm, String email, LocalDate motDueDate, MotIdentification motIdentification) {

        Optional<Subscription> subscription = subscriptionRepository.findByVrmAndEmail(vrm, email);

        if (subscription.isPresent()) {
            updateSubscriptionMotDueDate(subscription.get(), motDueDate);

            return urlHelper.emailConfirmedNthTimeLink();
        } else {
            createPendingSubscription(vrm, email, motDueDate, generateId(), motIdentification);

            return urlHelper.emailConfirmationPendingLink();
        }
    }

    /**
     * Creates pending subscription in the system to be confirmed later by confirmation link
     * @param vrm        subscription vrm
     * @param email      subscription email
     * @param motDueDate most recent mot due date
     * @param confirmationId confirmation id
     * @param motIdentification the identifier for this vehicle (may be dvla id or mot test number)
     */
    public void createPendingSubscription(String vrm, String email, LocalDate motDueDate,
            String confirmationId, MotIdentification motIdentification) {

        PendingSubscription pendingSubscription = new PendingSubscription()
                .setConfirmationId(confirmationId)
                .setEmail(email)
                .setVrm(vrm)
                .setMotDueDate(motDueDate)
                .setMotIdentification(motIdentification);

        try {
            pendingSubscriptionRepository.save(pendingSubscription);
            VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(vrm, client);
            notifyService.sendEmailAddressConfirmationEmail(
                    email,
                    urlHelper.confirmEmailLink(pendingSubscription.getConfirmationId()),
                    MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails, ", ") + vrm
            );
            EventLogger.logEvent(
                    new PendingSubscriptionCreatedEvent().setVrm(vrm).setEmail(email).setMotDueDate(motDueDate)
                    .setMotIdentification(motIdentification)
            );
        } catch (Exception e) {
            EventLogger.logErrorEvent(
                    new PendingSubscriptionCreationFailedEvent().setVrm(vrm).setEmail(email).setMotDueDate(motDueDate)
                    .setMotIdentification(motIdentification), e);
            throw e;
        }
    }

    private Subscription updateSubscriptionMotDueDate(Subscription subscription, LocalDate motDueDate) {

        subscription.setMotDueDate(motDueDate);
        subscriptionRepository.save(subscription);

        return subscription;
    }
}
