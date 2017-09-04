package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsService;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.UnsubscribeService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UnsubscribeConfirmationParams;
import uk.gov.dvsa.motr.web.formatting.MakeModelFormatter;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.UNSUBSCRIBE_FAILURE_KEY;
import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/unsubscribe")
@Produces("text/html")
public class UnsubscribeResource {

    private UnsubscribeService unsubscribeService;
    private TemplateEngine renderer;
    private MotrSession motrSession;
    private VehicleDetailsClient client;

    @Inject
    public UnsubscribeResource(
            UnsubscribeService unsubscribeService,
            TemplateEngine renderer,
            MotrSession motrSession,
            VehicleDetailsClient client
    ) {

        this.unsubscribeService = unsubscribeService;
        this.renderer = renderer;
        this.motrSession = motrSession;
        this.client = client;
    }

    @GET
    @Path("{unsubscribeId}")
    public String unsubscribeGet(@PathParam("unsubscribeId") String unsubscribeId) throws Exception {

        return unsubscribeService.findSubscriptionForUnsubscribe(unsubscribeId).map(subscription -> {

            UnsubscribeViewModel viewModel = populateViewModelFromSubscription(subscription);
            Map<String, Object> map = new HashMap<>();
            map.put("viewModel", viewModel);
            return renderer.render("unsubscribe", map);
        }).orElseGet(() -> {
            DataLayerHelper helper = new DataLayerHelper();
            helper.putAttribute(UNSUBSCRIBE_FAILURE_KEY, unsubscribeId);
            Map<String, Object> map = new HashMap<>();
            map.putAll(helper.formatAttributes());
            return renderer.render("unsubscribe-error", map);
        });
    }

    @POST
    @Path("{unsubscribeId}")
    public Response unsubscribePost(@PathParam("unsubscribeId") String unsubscribeId) throws Exception {

        Subscription removedSubscription = unsubscribeService.unsubscribe(unsubscribeId);

        UnsubscribeConfirmationParams params = new UnsubscribeConfirmationParams();
        params.setContact(removedSubscription.getEmail());
        params.setExpiryDate(removedSubscription.getMotDueDate().toString());
        params.setRegistration(removedSubscription.getVrm());

        motrSession.setUnsubscribeConfirmationParams(params);

        return redirect("unsubscribe/confirmed");
    }

    private UnsubscribeViewModel populateViewModelFromSubscription(Subscription subscription) {

        VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(subscription.getVrm(), client);

        return new UnsubscribeViewModel()
                .setEmail(subscription.getEmail())
                .setExpiryDate(subscription.getMotDueDate())
                .setRegistration(subscription.getVrm())
                .setMakeModel(MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails, ", "));
    }
}
