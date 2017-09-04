package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsService;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UnsubscribeConfirmationParams;
import uk.gov.dvsa.motr.web.formatting.MakeModelFormatter;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;

@Singleton
@Path("/unsubscribe")
@Produces("text/html")
public class UnsubscribeConfirmedResource {

    private TemplateEngine renderer;
    private MotrSession motrSession;
    private VehicleDetailsClient client;

    @Inject
    public UnsubscribeConfirmedResource(
            TemplateEngine renderer,
            MotrSession motrSession,
            VehicleDetailsClient client

    ) {

        this.renderer = renderer;
        this.motrSession = motrSession;
        this.client = client;
    }

    @GET
    @Path("confirmed")
    public String unsubscribeConfirmed() throws Exception {

        UnsubscribeConfirmationParams params = motrSession.getUnsubscribeConfirmationParams();
        if (params == null) {
            throw new NotFoundException();
        }

        VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(params.getRegistration(), client);

        UnsubscribeViewModel viewModel = new UnsubscribeViewModel()
                .setRegistration(params.getRegistration())
                .setExpiryDate(LocalDate.parse(params.getExpiryDate()))
                .setEmail(params.getContact())
                .setMakeModel(MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails, ", "));

        DataLayerHelper helper = new DataLayerHelper();
        helper.putAttribute(VRM_KEY, params.getRegistration());

        Map<String, Object> map = new HashMap<>();
        map.putAll(helper.formatAttributes());
        map.put("viewModel", viewModel);
        return renderer.render("unsubscribe-confirmation", map);
    }
}
