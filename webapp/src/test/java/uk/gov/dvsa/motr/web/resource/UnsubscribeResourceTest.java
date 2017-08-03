package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.UnsubscribeService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UnsubscribeConfirmationParams;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Optional.empty;

public class UnsubscribeResourceTest {

    private static final TemplateEngineStub TEMPLATE_ENGINE_STUB = new TemplateEngineStub();
    private static final String UNSUBSCRIBE_ID = "123-test-id";
    private static final String MOT_TEST_NUMBER = "123456";

    private UnsubscribeResource resource;
    private UnsubscribeService unsubscribeService;
    private VehicleDetailsClient client = mock(VehicleDetailsClient.class);
    private MotrSession motrSession = mock(MotrSession.class);

    @Before
    public void setUp() {


        UnsubscribeConfirmationParams params = new UnsubscribeConfirmationParams();

        params.setExpiryDate(LocalDate.of(2015, 7, 10).toString());
        params.setRegistration("TEST-VRM");
        params.setEmail("test@this-is-a-test-123");
        when(motrSession.getUnsubscribeConfirmationParams()).thenReturn(params);
        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setMotTestNumber(MOT_TEST_NUMBER);
        when(motrSession.getVehicleDetailsFromSession()).thenReturn(vehicleDetails);

        this.unsubscribeService = mock(UnsubscribeService.class);
        this.resource = new UnsubscribeResource(unsubscribeService, TEMPLATE_ENGINE_STUB, motrSession, client);
    }

    @Test
    public void unsubscribeGetErrorPageShownIfSubscriptionIsNotFound() throws Exception {

        when(unsubscribeService.findSubscriptionForUnsubscribe(UNSUBSCRIBE_ID)).thenReturn(empty());

        resource.unsubscribeGet(UNSUBSCRIBE_ID);

        verify(unsubscribeService, times(1)).findSubscriptionForUnsubscribe(UNSUBSCRIBE_ID);

        HashMap<String, Object> expectedContext = new HashMap<>();
        expectedContext.put("dataLayer", "{\"unsubscribe-failure\":\"" + UNSUBSCRIBE_ID + "\"}");

        assertEquals(expectedContext.toString(), TEMPLATE_ENGINE_STUB.getContext(Map.class).toString());
        assertEquals("unsubscribe-error", TEMPLATE_ENGINE_STUB.getTemplate());
    }

    @Test
    public void unsubscribeGetWhenFoundDisplayPage() throws Exception {

        when(client.fetch(eq("TEST-VRM"))).thenReturn(Optional.of(new VehicleDetails()));
        when(unsubscribeService.findSubscriptionForUnsubscribe(UNSUBSCRIBE_ID)).thenReturn(Optional.of(subscriptionStub()));

        resource.unsubscribeGet(UNSUBSCRIBE_ID);

        verify(unsubscribeService, times(1)).findSubscriptionForUnsubscribe(UNSUBSCRIBE_ID);
        assertEquals("unsubscribe", TEMPLATE_ENGINE_STUB.getTemplate());
        assertEquals(UnsubscribeViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
        UnsubscribeViewModel viewModel = (UnsubscribeViewModel) TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel");
        assertEquals("test@this-is-a-test-123", viewModel.getEmail());
        assertEquals("10 July 2015", viewModel.getExpiryDate());
        assertEquals("TEST-VRM", viewModel.getRegistration());
    }

    @Test
    public void unsubscribePostRedirectsToUnSubscribeConfirmationOnSuccess() throws Exception {

        Subscription subscription = subscriptionStub();
        when(unsubscribeService.unsubscribe(UNSUBSCRIBE_ID)).thenReturn(subscription);

        Response actual = resource.unsubscribePost(UNSUBSCRIBE_ID);

        verify(unsubscribeService, times(1)).unsubscribe(UNSUBSCRIBE_ID);
        assertEquals(302, actual.getStatus());
        assertEquals(
                "unsubscribe/confirmed",
                actual.getHeaderString("Location")
        );
    }

    private Subscription subscriptionStub() {

        return new Subscription()
                .setUnsubscribeId(UNSUBSCRIBE_ID)
                .setEmail("test@this-is-a-test-123")
                .setMotDueDate(LocalDate.of(2015, 7, 10))
                .setVrm("TEST-VRM");
    }
}
