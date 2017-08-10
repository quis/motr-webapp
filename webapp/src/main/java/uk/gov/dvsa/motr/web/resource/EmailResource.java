package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.EmailValidator;

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
@Path("/email")
@Produces("text/html")
public class EmailResource {

    private static final String EMAIL_MODEL_KEY = "email";
    private static final String EMAIL_TEMPLATE_NAME = "email";
    private static final String MESSAGE_MODEL_KEY = "message";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;

    private DataLayerHelper dataLayerHelper;

    @Inject
    public EmailResource(
            MotrSession motrSession,
            TemplateEngine renderer
    ) {

        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    public Response emailPage() throws Exception {

        if (!this.motrSession.isAllowedOnEmailPage()) {
            return redirect("/");
        }

        String email = this.motrSession.getEmailFromSession();

        Map<String, Object> modelMap = new HashMap<>();
        updateMapBasedOnReviewFlow(modelMap);

        modelMap.put(EMAIL_MODEL_KEY, email);

        return Response.ok(renderer.render(EMAIL_TEMPLATE_NAME, modelMap)).build();
    }

    @POST
    public Response emailPagePost(@FormParam("emailAddress") String email) throws Exception {

        EmailValidator emailValidator = new EmailValidator();

        if (emailValidator.isValid(email)) {
            this.motrSession.setEmail(email);

            //TODO Dynamo DB check and redirection to review page when it's ready
            return redirect("review");
        }

        Map<String, Object> map = new HashMap<>();

        map.put(MESSAGE_MODEL_KEY, emailValidator.getMessage());
        dataLayerHelper.putAttribute(ERROR_KEY, emailValidator.getMessage());
        updateMapBasedOnReviewFlow(map);

        map.put(EMAIL_MODEL_KEY, email);
        map.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return Response.status(200).entity(renderer.render(EMAIL_TEMPLATE_NAME, map)).build();
    }

    private void updateMapBasedOnReviewFlow(Map<String, Object> modelMap) {

        if (this.motrSession.visitingFromReviewPage()) {
            modelMap.put("continue_button_text", "Save and return to review");
            modelMap.put("back_button_text", "Cancel and return");
            modelMap.put("back_url", "review");
        } else {
            modelMap.put("continue_button_text", "Continue");
            modelMap.put("back_button_text", "Back");
            modelMap.put("back_url", "vrm");
        }
    }
}
