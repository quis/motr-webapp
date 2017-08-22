package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.PhoneNumberValidator;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.ERROR_KEY;
import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/phone-number")
@Produces("text/html")
public class PhoneNumberResource {

    private static final String PHONE_NUMBER_TEMPLATE = "phone-number";
    private static final String PHONE_NUMBER_MODEL_KEY = "phoneNumber";
    private static final String MESSAGE_MODEL_KEY = "message";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;

    private DataLayerHelper dataLayerHelper;

    @Inject
    public PhoneNumberResource(
            MotrSession motrSession,
            TemplateEngine renderer
    ) {
        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    public Response phoneNumberPageGet() throws Exception {

        if (!motrSession.isAllowedOnPhoneNumberEntryPage()) {
            return redirect("/");
        }

        if (!motrSession.visitingFromReviewPage()) {
            motrSession.setVisitingFromContactEntry(true);
        }

        String phoneNumber = motrSession.getPhoneNumberFromSession();

        Map<String, Object> modelMap = new HashMap<>();
        ReviewFlowUpdater.updateMapBasedOnReviewFlow(modelMap,
                motrSession.visitingFromContactEntryPage(),
                motrSession.visitingFromReviewPage());

        modelMap.put(PHONE_NUMBER_MODEL_KEY, phoneNumber);

        return Response.ok(renderer.render(PHONE_NUMBER_TEMPLATE, modelMap)).build();
    }

    @POST
    public Response phoneNumberPagePost(@FormParam("phoneNumber") String phoneNumber) throws Exception {

        PhoneNumberValidator validator = new PhoneNumberValidator();

        if (validator.isValid(phoneNumber)) {
            motrSession.setChannel("text");
            motrSession.setPhoneNumber(phoneNumber);

            return redirect("review");
        }

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put(MESSAGE_MODEL_KEY, validator.getMessage());
        dataLayerHelper.putAttribute(ERROR_KEY, validator.getMessage());
        ReviewFlowUpdater.updateMapBasedOnReviewFlow(modelMap,
                motrSession.visitingFromContactEntryPage(),
                motrSession.visitingFromReviewPage());

        modelMap.put(PHONE_NUMBER_MODEL_KEY, phoneNumber);
        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return Response.ok(renderer.render(PHONE_NUMBER_TEMPLATE, modelMap)).build();
    }
}
