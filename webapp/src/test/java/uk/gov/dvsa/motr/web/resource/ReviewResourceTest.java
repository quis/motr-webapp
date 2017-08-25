package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.response.PendingSubscriptionServiceResponse;
import uk.gov.dvsa.motr.web.component.subscription.service.PendingSubscriptionService;
import uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.SubscriptionConfirmationParams;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.time.LocalDate;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReviewResourceTest {

    private static final PendingSubscriptionService PENDING_SUBSCRIPTION_SERVICE = mock(PendingSubscriptionService.class);
    private static final SmsConfirmationService PENDING_SMS_CONFIRMATION_SERVICE = mock(SmsConfirmationService.class);
    private static final TemplateEngineStub TEMPLATE_ENGINE_STUB = new TemplateEngineStub();
    private static final String VRM = "YN13NTX";
    private static final String EMAIL = "test@test.com";
    private static final String MOBILE = "07912345678";
    private static final String CONFIRMATION_ID = "ABC123";
    private static final String TEST_MOT_TEST_NUMBER = "123456";
    private static final String EMAIL_CONFIRMATION_PENDING_URI = "email-confirmation-pending";
    private static final String PHONE_CONFIRMATION_URI = "confirm-phone";
    private static final Subscription.ContactType CONTACT_TYPE_EMAIL = Subscription.ContactType.EMAIL;
    private static final Subscription.ContactType CONTACT_TYPE_MOBILE = Subscription.ContactType.MOBILE;

    private MotrSession motrSession;

    private ReviewResource resource;

    @Before
    public void setUp() {

        motrSession = mock(MotrSession.class);

        this.resource = new ReviewResource(
                motrSession,
                TEMPLATE_ENGINE_STUB,
                PENDING_SUBSCRIPTION_SERVICE,
                PENDING_SMS_CONFIRMATION_SERVICE
        );

        when(motrSession.getVrmFromSession()).thenReturn(VRM);
        when(motrSession.getEmailFromSession()).thenReturn(EMAIL);
        when(motrSession.getPhoneNumberFromSession()).thenReturn(MOBILE);
    }

    @Test
    public void reviewTemplateIsRenderedOnGetWithViewModel() throws Exception {

        when(motrSession.isAllowedOnReviewPage()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicleDetailsInSession());

        assertEquals(200, resource.reviewPage().getStatus());
        assertEquals("review", TEMPLATE_ENGINE_STUB.getTemplate());
        assertEquals(ReviewViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
    }

    @Test(expected = NotFoundException.class)
    public void whenNoVehicleReturnedFromApiNotFoundThrown() throws Exception {

        when(motrSession.isAllowedOnReviewPage()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(null);
        resource.reviewPage();
    }

    @Test
    public void userIsRedirectedAfterSuccessfullFormSubmission() throws Exception {

        LocalDate now = LocalDate.now();
        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMotExpiryDate(now);
        MotIdentification expectedMotIdentification = new MotIdentification(TEST_MOT_TEST_NUMBER, null);
        ArgumentCaptor<SubscriptionConfirmationParams> paramsArgumentCaptor = ArgumentCaptor.forClass(SubscriptionConfirmationParams.class);

        PendingSubscriptionServiceResponse pendingSubscriptionResponse = new PendingSubscriptionServiceResponse()
                .setRedirectUri(EMAIL_CONFIRMATION_PENDING_URI);

        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicleDetails);
        when(motrSession.isUsingEmailChannel()).thenReturn(true);
        when(PENDING_SUBSCRIPTION_SERVICE.handlePendingSubscriptionCreation(any(), any(), any(), any(), any()))
                .thenReturn(pendingSubscriptionResponse);
        doNothing().when(PENDING_SUBSCRIPTION_SERVICE).createPendingSubscription(
                VRM, EMAIL, now, "randomID", expectedMotIdentification, CONTACT_TYPE_EMAIL);

        Response response = resource.confirmationPagePost();

        verify(PENDING_SUBSCRIPTION_SERVICE, times(1)).handlePendingSubscriptionCreation(eq(VRM), eq(EMAIL), eq(now),
                any(MotIdentification.class), eq(CONTACT_TYPE_EMAIL));
        verify(motrSession, times(1)).setSubscriptionConfirmationParams(paramsArgumentCaptor.capture());
        assertEquals(302, response.getStatus());
        assertEquals(EMAIL_CONFIRMATION_PENDING_URI, response.getLocation().toString());
        assertEquals(EMAIL, paramsArgumentCaptor.getValue().getContact());
    }

    @Test
    public void userIsRedirectedAfterSuccessfullFormSubmissionWhenUsingSmsChannel() throws Exception {

        LocalDate now = LocalDate.now();
        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMotExpiryDate(now);
        MotIdentification expectedMotIdentification = new MotIdentification(TEST_MOT_TEST_NUMBER, null);
        ArgumentCaptor<SubscriptionConfirmationParams> paramsArgumentCaptor = ArgumentCaptor.forClass(SubscriptionConfirmationParams.class);

        PendingSubscriptionServiceResponse pendingSubscriptionResponse = new PendingSubscriptionServiceResponse()
                .setConfirmationId(CONFIRMATION_ID);

        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicleDetails);
        when(motrSession.isUsingEmailChannel()).thenReturn(false);
        when(motrSession.isUsingSmsChannel()).thenReturn(true);
        when(PENDING_SUBSCRIPTION_SERVICE.handlePendingSubscriptionCreation(any(), any(), any(), any(), any()))
                .thenReturn(pendingSubscriptionResponse);
        when(PENDING_SMS_CONFIRMATION_SERVICE.handleSmsConfirmationCreation(VRM, MOBILE, pendingSubscriptionResponse.getConfirmationId()))
                .thenReturn(PHONE_CONFIRMATION_URI);
        doNothing().when(PENDING_SUBSCRIPTION_SERVICE).createPendingSubscription(
                VRM, MOBILE, now, "randomID", expectedMotIdentification, CONTACT_TYPE_MOBILE);

        Response response = resource.confirmationPagePost();

        verify(PENDING_SUBSCRIPTION_SERVICE, times(1)).handlePendingSubscriptionCreation(eq(VRM), eq(MOBILE), eq(now),
                any(MotIdentification.class), eq(CONTACT_TYPE_MOBILE));
        verify(PENDING_SMS_CONFIRMATION_SERVICE, times(1)).handleSmsConfirmationCreation(eq(VRM), eq(MOBILE), eq(CONFIRMATION_ID));
        verify(motrSession, times(1)).setSubscriptionConfirmationParams(paramsArgumentCaptor.capture());
        verify(motrSession, times(1)).setConfirmationId(pendingSubscriptionResponse.getConfirmationId());
        assertEquals(302, response.getStatus());
        assertEquals(PHONE_CONFIRMATION_URI, response.getLocation().toString());
        assertEquals(MOBILE, paramsArgumentCaptor.getValue().getContact());
    }

    @Test(expected = NotFoundException.class)
    public void notFoundExceptionIsThrownWhenVehicleDetailsAreInvalid() throws Exception {

        when(motrSession.getVehicleDetailsFromSession()).thenReturn(null);

        resource.confirmationPagePost();
    }

    @Test
    public void whenYearOfManufactureIsNull_thenReviewPageShouldDisplayViewModelProperly() throws Exception {

        VehicleDetails vehicleDetails = vehicleDetailsInSession();
        vehicleDetails.setYearOfManufacture(null);

        when(motrSession.isAllowedOnReviewPage()).thenReturn(true);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicleDetails);

        assertEquals(200, resource.reviewPage().getStatus());
        assertEquals("review", TEMPLATE_ENGINE_STUB.getTemplate());
        assertEquals(ReviewViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
    }

    private VehicleDetails vehicleDetailsInSession() {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMake("make");
        vehicleDetails.setModel("model");
        vehicleDetails.setYearOfManufacture(2000);
        vehicleDetails.setMotExpiryDate(LocalDate.now());
        vehicleDetails.setMotTestNumber(TEST_MOT_TEST_NUMBER);
        return vehicleDetails;
    }
}
