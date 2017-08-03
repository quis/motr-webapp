package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class SubscriptionConfirmationServiceTest {

    private final PendingSubscriptionRepository pendingSubscriptionRepository = mock(PendingSubscriptionRepository.class);
    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final NotifyService notifyService = mock(NotifyService.class);
    private final UrlHelper urlHelper = mock(UrlHelper.class);
    private static final String CONFIRMATION_ID = "asdasdasd";
    private static final LocalDate DATE = LocalDate.now();
    private static final String VRM = "vrm";
    private static final String EMAIL = "email";
    private final VehicleDetailsClient client = mock(VehicleDetailsClient.class);
    private static final String MOT_TEST_NUMBER = "12345";

    private SubscriptionConfirmationService subscriptionService;

    @Before
    public void setUp() {

        this.subscriptionService = new SubscriptionConfirmationService(
                pendingSubscriptionRepository,
                subscriptionRepository,
                notifyService,
                urlHelper,
                client
        );
    }

    @Test
    public void saveSubscriptionWhenSubscriptionDoesNotExistCallsDbToSaveDetails() throws Exception {
        when(client.fetch(eq(VRM))).thenReturn(Optional.of(new VehicleDetails()));
        PendingSubscription pendingSubscription = pendingSubscriptionStub();
        withPendingSubscriptionFound(of(pendingSubscription));

        subscriptionService.confirmSubscription(CONFIRMATION_ID);

        verify(pendingSubscriptionRepository, times(1)).findByConfirmationId(CONFIRMATION_ID);
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
        verify(pendingSubscriptionRepository, times(1)).delete(pendingSubscription);
        verify(notifyService, times(1)).sendSubscriptionConfirmationEmail(
                eq(pendingSubscription.getEmail()),
                eq(pendingSubscription.getVrm()),
                eq(pendingSubscription.getMotDueDate()),
                anyString(),
                any(MotIdentification.class)
        );
    }

    @Test(expected = InvalidConfirmationIdException.class)
    public void throwsExceptionWhenPendingSubscriptionDoesNotExists() throws Exception {

        withPendingSubscriptionFound(empty());

        subscriptionService.confirmSubscription(CONFIRMATION_ID);

        verify(subscriptionRepository, times(0)).save(any());
        verify(pendingSubscriptionRepository, times(0)).delete(any(PendingSubscription.class));
    }

    private void withPendingSubscriptionFound(Optional<PendingSubscription> finding) {

        when(pendingSubscriptionRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(finding);
    }

    private PendingSubscription pendingSubscriptionStub() {

        return new PendingSubscription()
                .setConfirmationId(CONFIRMATION_ID)
                .setMotDueDate(DATE)
                .setEmail(EMAIL)
                .setVrm(VRM);
    }

    private MotIdentification motIdentificationStub() {

        return new MotIdentification(MOT_TEST_NUMBER, null);
    }
}
