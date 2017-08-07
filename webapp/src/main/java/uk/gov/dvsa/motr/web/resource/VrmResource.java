package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.eventlog.HoneyPotTriggeredEvent;
import uk.gov.dvsa.motr.web.eventlog.vehicle.VehicleDetailsExceptionEvent;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.MotDueDateValidator;
import uk.gov.dvsa.motr.web.validator.VrmValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.ERROR_KEY;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;
import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/vrm")
@Produces("text/html")
public class VrmResource {

    private static final String VRM_MODEL_KEY = "vrm";
    private static final String VEHICLE_NOT_FOUND_MESSAGE = "Check that you’ve typed in the correct registration number.<br/>" +
            "<br/>You can only sign up if the vehicle has a current MOT.";

    private static final String MESSAGE_KEY = "message";
    private static final String SHOW_INLINE_KEY = "showInLine";
    private static final String VRM_TEMPLATE_NAME = "vrm";
    private static final String SHOW_SYSTEM_ERROR = "showSystemError";

    private final TemplateEngine renderer;
    private final VehicleDetailsClient client;
    private MotDueDateValidator motDueDateValidator;
    private final MotrSession motrSession;
    private DataLayerHelper dataLayerHelper;

    @Inject
    public VrmResource(
            MotrSession motrSession,
            TemplateEngine renderer,
            VehicleDetailsClient client,
            MotDueDateValidator motDueDateValidator
    ) {

        this.motrSession = motrSession;
        this.renderer = renderer;
        this.client = client;
        this.motDueDateValidator = motDueDateValidator;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    public String vrmPageGet() throws Exception {

        String vrm = motrSession.getVrmFromSession();

        Map<String, Object> modelMap = new HashMap<>();
        updateMapBasedOnReviewFlow(modelMap);

        modelMap.put(VRM_MODEL_KEY, vrm);

        return renderer.render("vrm", modelMap);
    }

    @POST
    public Response vrmPagePost(@FormParam("regNumber") String formParamVrm, @FormParam("honey") String formParamHoney) throws Exception {

        if (formParamHoney != null) {
            if (!formParamHoney.isEmpty()) {
                EventLogger.logEvent(new HoneyPotTriggeredEvent());
                return redirect("email-confirmation-pending");
            }
        }

        String vrm = normalizeFormInputVrm(formParamVrm);

        Map<String, Object> modelMap = new HashMap<>();
        dataLayerHelper.putAttribute(VRM_KEY, vrm);
        updateMapBasedOnReviewFlow(modelMap);

        modelMap.put(VRM_MODEL_KEY, vrm);
        modelMap.put(SHOW_INLINE_KEY, true);
        modelMap.put(SHOW_SYSTEM_ERROR, false);

        VrmValidator validator = new VrmValidator();
        if (validator.isValid(vrm)) {
            try {
                Optional<VehicleDetails> vehicle = this.client.fetch(vrm);
                if (vehicleDataIsValid(vehicle)) {
                    motrSession.setVrm(vrm);
                    motrSession.setVehicleDetails(vehicle.get());

                    return getRedirectAfterSuccessfulEdit();
                } else {
                    addVehicleNotFoundErrorMessageToViewModel(modelMap);
                }
            } catch (VehicleDetailsClientException exception) {

                EventLogger.logErrorEvent(new VehicleDetailsExceptionEvent().setVrm(vrm), exception);
                dataLayerHelper.putAttribute(ERROR_KEY, "Trade API error");
                motrSession.setVrm(vrm);
                modelMap.put(SHOW_SYSTEM_ERROR, true);
            }

        } else {
            dataLayerHelper.putAttribute(ERROR_KEY, validator.getMessage());
            modelMap.put(MESSAGE_KEY, validator.getMessage());
        }

        modelMap.put(VRM_MODEL_KEY, vrm);
        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return Response.ok(renderer.render(VRM_TEMPLATE_NAME, modelMap)).build();
    }

    private void addVehicleNotFoundErrorMessageToViewModel(Map<String, Object> modelMap) {

        dataLayerHelper.putAttribute(ERROR_KEY, "Vehicle not found");
        modelMap.put(MESSAGE_KEY, VEHICLE_NOT_FOUND_MESSAGE);
        modelMap.put(SHOW_INLINE_KEY, false);
    }

    private Response getRedirectAfterSuccessfulEdit() {

        if (this.motrSession.visitingFromReviewPage()) {
            return redirect("review");
        }

        return redirect("email");
    }

    private boolean vehicleDataIsValid(Optional<VehicleDetails> vehicle) {

        return vehicle.isPresent() && motDueDateValidator.isDueDateValid(vehicle.get().getMotExpiryDate());
    }

    private void updateMapBasedOnReviewFlow(Map<String, Object> modelMap) {

        if (this.motrSession.visitingFromReviewPage()) {
            modelMap.put("continue_button_text", "Save and return to review");
            modelMap.put("back_button_text", "Cancel and return");
            modelMap.put("back_url", "review");
        } else {
            modelMap.put("continue_button_text", "Continue");
            modelMap.put("back_button_text", "Back");
            modelMap.put("back_url", "/");
        }
    }

    private static String normalizeFormInputVrm(String formInput) {

        return formInput.replaceAll("\\s+", "").toUpperCase();
    }
}
