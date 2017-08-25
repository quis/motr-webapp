package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.SubscriptionConfirmationParams;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.DLVA_ID_KEY;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.MOT_TEST_NUMBER_KEY;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/confirm-subscription")
@Produces("text/html")
public class SubscriptionConfirmedResource {

    private final TemplateEngine renderer;
    private final DataLayerHelper dataLayerHelper;
    private SubscriptionConfirmationService subscriptionConfirmationService;
    private MotrSession motrSession;
    private UrlHelper urlHelper;

    @Inject
    public SubscriptionConfirmedResource(
            TemplateEngine renderer,
            SubscriptionConfirmationService pendingSubscriptionActivatorService,
            MotrSession motrSession,
            UrlHelper urlHelper
    ) {

        this.renderer = renderer;
        this.subscriptionConfirmationService = pendingSubscriptionActivatorService;
        this.motrSession = motrSession;
        this.urlHelper = urlHelper;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    @Path("{confirmationId}")
    public Response confirmSubscriptionGet(@PathParam("confirmationId") String confirmationId) {

        try {
            Subscription subscription = subscriptionConfirmationService.confirmSubscription(confirmationId);
            motrSession.clear();

            MotIdentification motIdentification = subscription.getMotIdentification();
            SubscriptionConfirmationParams params = new SubscriptionConfirmationParams();
            params.setRegistration(subscription.getVrm());
            params.setDvlaId(motIdentification.getDvlaId().orElse(""));
            params.setMotTestNumber(motIdentification.getMotTestNumber().orElse(""));
            params.setContactType(subscription.getContactType().getValue());
            motrSession.setSubscriptionConfirmationParams(params);

            return RedirectResponseBuilder.redirect(urlHelper.subscriptionConfirmedFirstTimeLink());
        } catch (InvalidConfirmationIdException e) {
            return Response.ok(renderer.render("subscription-error", emptyMap())).build();
        }
    }

    @GET
    @Path("confirmed")
    public String confirmSubscriptionFirstTimeGet() {

        return showConfirmationPage();
    }

    @GET
    @Path("already-confirmed")
    public String confirmSubscriptionNthTimeGet() {

        return showConfirmationPage();
    }

    private String showConfirmationPage() {

        SubscriptionConfirmationParams subscription = motrSession.getSubscriptionConfirmationParams();

        Map<String, Object> modelMap = new HashMap<>();

        if (null != subscription) {
            dataLayerHelper.putAttribute(VRM_KEY, subscription.getRegistration());

            if (subscription.getContactType().equals(Subscription.ContactType.MOBILE.getValue())) {
                modelMap.put("usingSms", true);
                modelMap.put("replyNumber", "07491163040");
                modelMap.put("registration", subscription.getRegistration());
            }

            if (subscription.getMotTestNumber() != null && !subscription.getMotTestNumber().isEmpty()) {
                dataLayerHelper.putAttribute(MOT_TEST_NUMBER_KEY, subscription.getMotTestNumber());
            } else {
                dataLayerHelper.putAttribute(DLVA_ID_KEY, subscription.getDvlaId());
            }
            return renderer.render("subscription-confirmation", buildMap(modelMap));
        } else {
            return renderer.render("subscription-error", emptyMap());
        }
    }

    private Map<String, Object> buildMap(Map<String, Object> map) {

        map.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return map;
    }
}
