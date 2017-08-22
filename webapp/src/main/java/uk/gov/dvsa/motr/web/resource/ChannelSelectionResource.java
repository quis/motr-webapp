package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.ChannelSelectionValidator;

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
@Path("/channel-selection")
@Produces("text/html")
public class ChannelSelectionResource {

    private static final String CHANNEL_SELECTION_TEMPLATE = "channel-selection";
    private static final String CHANNEL_SELECTION = "channel-selection";
    private static final String MESSAGE_MODEL_KEY = "message";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;

    private DataLayerHelper dataLayerHelper;

    @Inject
    public ChannelSelectionResource(
            MotrSession motrSession,
            TemplateEngine renderer
    ) {
        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    public Response channelSelectionPageGet() throws Exception {

        if (!motrSession.isAllowedOnChannelSelectionPage()) {
            return redirect("/");
        }

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("isEmailChecked", (motrSession.getChannelFromSession().equals("email")));
        modelMap.put("isTextChecked", (motrSession.getChannelFromSession().equals("text")));
        modelMap.put("continue_button_text", "Continue");
        modelMap.put("back_button_text", "Back");
        modelMap.put("back_url", "vrm");

        return Response.ok(renderer.render(CHANNEL_SELECTION_TEMPLATE, modelMap)).build();
    }

    @POST
    public Response channelSelectionPagePost(@FormParam("selectChannel") String formParamChannel) throws Exception {

        ChannelSelectionValidator channelSelectionValidator = new ChannelSelectionValidator();

        if (channelSelectionValidator.isValid(formParamChannel)) {
            if (formParamChannel.equals("email")) {
                motrSession.setChannel("email");

                return redirect("email");
            } else if (formParamChannel.equals("text")) {
                motrSession.setChannel("text");

                return redirect("phone-number");
            }
        }

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put(MESSAGE_MODEL_KEY, channelSelectionValidator.getMessage());
        dataLayerHelper.putAttribute(ERROR_KEY, channelSelectionValidator.getMessage());

        modelMap.put("isEmailChecked", (motrSession.getChannelFromSession().equals("email")));
        modelMap.put("isTextChecked", (motrSession.getChannelFromSession().equals("text")));
        modelMap.put("continue_button_text", "Continue");
        modelMap.put("back_button_text", "Back");
        modelMap.put("back_url", "vrm");

        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return Response.ok(renderer.render(CHANNEL_SELECTION_TEMPLATE, modelMap)).build();
    }
}
