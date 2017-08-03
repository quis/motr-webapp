package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UnsubscribeConfirmationParams;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnsubscribeConfirmedResourceTest {

    private static final TemplateEngineStub TEMPLATE_ENGINE_STUB = new TemplateEngineStub();
    private static final String MOT_TEST_NUMBER = "123456";

    private UnsubscribeConfirmedResource resource;
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

        this.resource = new UnsubscribeConfirmedResource(TEMPLATE_ENGINE_STUB, motrSession, client);
    }

    @Test(expected = NotFoundException.class)
    public void unsubscribeConfirmedWillThrow404WhenSessionIsEmpty() throws Exception {

        resource = new UnsubscribeConfirmedResource(TEMPLATE_ENGINE_STUB, new MotrSession(), client);
        resource.unsubscribeConfirmed();
    }

    @Test
    public void unsubscribeConfirmedDisplaysPage() throws Exception {

        when(client.fetch(eq("TEST-VRM"))).thenReturn(Optional.of(new VehicleDetails()));
        resource.unsubscribeConfirmed();

        assertEquals(UnsubscribeViewModel.class, TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel").getClass());
        String dataLayerString = (String) TEMPLATE_ENGINE_STUB.getContext(Map.class).get("dataLayer");
        UnsubscribeViewModel viewModel = (UnsubscribeViewModel) TEMPLATE_ENGINE_STUB.getContext(Map.class).get("viewModel");
        assertEquals("test@this-is-a-test-123", viewModel.getEmail());
        assertEquals("10 July 2015", viewModel.getExpiryDate());
        assertEquals("TEST-VRM", viewModel.getRegistration());
        assertEquals("{\"vrm\":\"TEST-VRM\"}", dataLayerString);
    }
}
