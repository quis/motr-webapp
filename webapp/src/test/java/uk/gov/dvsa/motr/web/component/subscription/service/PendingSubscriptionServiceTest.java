package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.response.PendingSubscriptionServiceResponse;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import static java.util.Optional.empty;

public class PendingSubscriptionServiceTest {

    private final PendingSubscriptionRepository pendingSubscriptionRepository = mock(PendingSubscriptionRepository.class);
    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final NotifyService notifyService = mock(NotifyService.class);
    private final UrlHelper urlHelper = mock(UrlHelper.class);
    private final VehicleDetailsClient client = mock(VehicleDetailsClient.class);

    private static final String TEST_VRM = "TEST-REG";
    private static final String EMAIL = "TEST@TEST.com";
    private static final String CONFIRMATION_ID = "Asd";
    private static final String CONFIRMATION_LINK = "CONFIRMATION_LINK";
    private static final String ALREADY_CONFIRMED_LINK = "ALREADY_CONFIRMED_LINK";
    private static final String CONFIRMATION_PENDING_LINK = "CONFIRMATION_PENDING_LINK";
    private static final String TEST_MOT_TEST_NUMBER = "123456";
    private static final String TEST_DVLA_ID = "3456789";
    private static final Subscription.ContactType CONTACT_TYPE = Subscription.ContactType.EMAIL;

    private PendingSubscriptionService subscriptionService;

    @Before
    public void setUp() {

        this.subscriptionService = new PendingSubscriptionService(
                pendingSubscriptionRepository,
                subscriptionRepository,
                notifyService,
                urlHelper,
                client
        );

        when(urlHelper.confirmSubscriptionLink(CONFIRMATION_ID)).thenReturn(CONFIRMATION_LINK);
        when(urlHelper.emailConfirmedNthTimeLink()).thenReturn(ALREADY_CONFIRMED_LINK);
        when(urlHelper.emailConfirmationPendingLink()).thenReturn(CONFIRMATION_PENDING_LINK);
    }

    @Test
    public void saveSubscriptionCallsDbToSaveDetailsAndSendsNotification() throws Exception {
        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("TEST-MAKE");
        vehicleDetails.setModel("TEST-MODEL");
        when(client.fetch(eq(TEST_VRM))).thenReturn(Optional.of(vehicleDetails));

        withExpectedSubscription(empty());
        when(pendingSubscriptionRepository.findByConfirmationId(CONFIRMATION_ID)).thenReturn(empty());
        doNothing().when(notifyService).sendEmailAddressConfirmationEmail(EMAIL, CONFIRMATION_LINK, "TEST-MAKE TEST-MODEL, ");
        LocalDate date = LocalDate.now();

        this.subscriptionService.createPendingSubscription(TEST_VRM, EMAIL, date, CONFIRMATION_ID,
                new MotIdentification(TEST_MOT_TEST_NUMBER, TEST_DVLA_ID), CONTACT_TYPE);

        verify(pendingSubscriptionRepository, times(1)).save(any(PendingSubscription.class));
        verify(notifyService, times(1)).sendEmailAddressConfirmationEmail(
                EMAIL,
                CONFIRMATION_LINK,
                "TEST-MAKE TEST-MODEL, TEST-REG"
        );
    }

    @Test(expected = RuntimeException.class)
    public void whenDbSaveFailsConfirmationEmailIsNotSent() throws Exception {

        withExpectedSubscription(empty());
        doThrow(new RuntimeException()).when(pendingSubscriptionRepository).save(any(PendingSubscription.class));
        LocalDate date = LocalDate.now();

        this.subscriptionService.createPendingSubscription(TEST_VRM, EMAIL, date, CONFIRMATION_ID,
                new MotIdentification(TEST_MOT_TEST_NUMBER, TEST_DVLA_ID), CONTACT_TYPE);
        verify(pendingSubscriptionRepository, times(1)).save(any(PendingSubscription.class));
        verifyZeroInteractions(notifyService);
    }

    @Test
    public void handleSubscriptionWithExistingSubscriptionWillUpdateMotExpiryDate() throws Exception {

        withExpectedSubscription(Optional.of(new Subscription()));
        LocalDate date = LocalDate.now();
        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);

        PendingSubscriptionServiceResponse pendingSubscriptionResponse = this.subscriptionService.handlePendingSubscriptionCreation(
                TEST_VRM, EMAIL, date, new MotIdentification(TEST_MOT_TEST_NUMBER, TEST_DVLA_ID), CONTACT_TYPE);

        verify(subscriptionRepository, times(1)).save(subscriptionArgumentCaptor.capture());
        assertEquals(subscriptionArgumentCaptor.getValue().getMotDueDate(), date);
        assertEquals(ALREADY_CONFIRMED_LINK, pendingSubscriptionResponse.getRedirectUri());
        verifyZeroInteractions(pendingSubscriptionRepository);
    }

    @Test
    public void handleSubscriptionWillCreateNewPendingSubscription() throws Exception {
        when(client.fetch(eq(TEST_VRM))).thenReturn(Optional.of(new VehicleDetails()));
        when(pendingSubscriptionRepository.findByVrmAndContactDetails(TEST_VRM, EMAIL)).thenReturn(Optional.empty());
        withExpectedSubscription(empty());
        LocalDate date = LocalDate.now();
        ArgumentCaptor<PendingSubscription> pendingSubscriptionArgumentCaptor = ArgumentCaptor.forClass(PendingSubscription.class);

        PendingSubscriptionServiceResponse pendingSubscriptionResponse = this.subscriptionService.handlePendingSubscriptionCreation(
                TEST_VRM, EMAIL, date, new MotIdentification(TEST_MOT_TEST_NUMBER, TEST_DVLA_ID), CONTACT_TYPE);

        verify(pendingSubscriptionRepository, times(1)).save(pendingSubscriptionArgumentCaptor.capture());
        assertEquals(pendingSubscriptionArgumentCaptor.getValue().getMotDueDate(), date);
        assertEquals(pendingSubscriptionArgumentCaptor.getValue().getEmail(), EMAIL);
        assertEquals(pendingSubscriptionArgumentCaptor.getValue().getVrm(), TEST_VRM);
        assertEquals(CONFIRMATION_PENDING_LINK, pendingSubscriptionResponse.getRedirectUri());
    }

    private void withExpectedSubscription(Optional<Subscription> subscription) {
        when(subscriptionRepository.findByVrmAndEmail(TEST_VRM, EMAIL)).thenReturn(subscription);
    }
}
