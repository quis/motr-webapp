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
    private static final SmsConfirmationService PENDING_SMS_SUBSCRIPTION_SERVICE = mock(SmsConfirmationService.class);
    private static final TemplateEngineStub TEMPLATE_ENGINE_STUB = new TemplateEngineStub();
    private static final MotrSession MOTR_SESSION = mock(MotrSession.class);
    private static final String VRM = "YN13NTX";
    private static final String EMAIL = "test@test.com";
    private static final String TEST_MOT_TEST_NUMBER = "123456";
    private static final String TEST_DVLA_ID = "3456789";
    private static final Subscription.ContactType CONTACT_TYPE = Subscription.ContactType.EMAIL;

    private ReviewResource resource;

    @Before
    public void setUp() {

        this.resource = new ReviewResource(
                MOTR_SESSION,
                TEMPLATE_ENGINE_STUB,
                PENDING_SUBSCRIPTION_SERVICE,
                PENDING_SMS_SUBSCRIPTION_SERVICE
        );
        when(MOTR_SESSION.getVrmFromSession()).thenReturn(VRM);
        when(MOTR_SESSION.getEmailFromSession()).thenReturn(EMAIL);
    }

    @Test
    public void reviewTemplateIsRenderedOnGetWithViewModel() throws Exception {

        when(MOTR_SESSION.isAllowedOnReviewPage()).thenReturn(true);
        when(MOTR_SESSION.getVehicleDetailsFromSession()).thenReturn(vehicleDetailsInSession());

        assertEquals(200, resource.reviewPage().getStatus());
        assertEquals("review", TEMPLATE_ENGINE_STUB.getTemplate());
        assertEquals(ReviewViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
    }

    @Test(expected = NotFoundException.class)
    public void whenNoVehicleReturnedFromApiNotFoundThrown() throws Exception {

        when(MOTR_SESSION.isAllowedOnReviewPage()).thenReturn(true);
        when(MOTR_SESSION.getVehicleDetailsFromSession()).thenReturn(null);
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
                .setRedirectUri("email-confirmation-pending");

        when(MOTR_SESSION.getVehicleDetailsFromSession()).thenReturn(vehicleDetails);
        when(MOTR_SESSION.isUsingEmailChannel()).thenReturn(true);
        when(PENDING_SUBSCRIPTION_SERVICE.handlePendingSubscriptionCreation(any(), any(), any(), any(), any()))
                .thenReturn(pendingSubscriptionResponse);
        doNothing().when(PENDING_SUBSCRIPTION_SERVICE).createPendingSubscription(
                VRM, EMAIL, now, "randomID", expectedMotIdentification, CONTACT_TYPE);

        Response response = resource.confirmationPagePost();

        verify(PENDING_SUBSCRIPTION_SERVICE, times(1)).handlePendingSubscriptionCreation(eq(VRM), eq(EMAIL), eq(now),
                any(MotIdentification.class), eq(CONTACT_TYPE));
        verify(MOTR_SESSION, times(1)).setSubscriptionConfirmationParams(paramsArgumentCaptor.capture());
        assertEquals(302, response.getStatus());
        assertEquals("email-confirmation-pending", response.getLocation().toString());
        assertEquals(EMAIL, paramsArgumentCaptor.getValue().getContact());
    }

    @Test(expected = NotFoundException.class)
    public void notFoundExceptionIsThrownWhenVehicleDetailsAreInvalid() throws Exception {

        when(MOTR_SESSION.getVehicleDetailsFromSession()).thenReturn(null);

        resource.confirmationPagePost();
    }

    @Test
    public void whenYearOfManufactureIsNull_thenReviewPageShouldDisplayViewModelProperly() throws Exception {

        VehicleDetails vehicleDetails = vehicleDetailsInSession();
        vehicleDetails.setYearOfManufacture(null);

        when(MOTR_SESSION.isAllowedOnReviewPage()).thenReturn(true);
        when(MOTR_SESSION.getVehicleDetailsFromSession()).thenReturn(vehicleDetails);

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
