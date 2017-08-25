package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.SmsConfirmationCodeValidator;
import uk.gov.dvsa.motr.web.validator.Validator;
import uk.gov.dvsa.motr.web.viewmodel.SmsConfirmationCodeViewModel;

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
@Path("/confirm-phone")
@Produces("text/html")
public class SmsConfirmationCodeResource {

    private static final String SMS_CONFIRMATION_CODE_TEMPLATE = "sms-confirmation-code";
    private static final String CONFIRMATION_CODE_MODEL_KEY = "confirmationCode";
    private static final String MESSAGE_MODEL_KEY = "message";
    private static final String MESSAGE_AT_FIELD_MODEL_KEY = "messageAtField";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;

    private DataLayerHelper dataLayerHelper;
    private SmsConfirmationService smsConfirmationService;
    private UrlHelper urlHelper;

    @Inject
    public SmsConfirmationCodeResource(
            MotrSession motrSession,
            TemplateEngine renderer,
            UrlHelper urlHelper,
            SmsConfirmationService smsConfirmationService
    ) {
        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
        this.smsConfirmationService = smsConfirmationService;
        this.urlHelper = urlHelper;
    }

    @GET
    public Response smsConfirmationCodePageGet() throws Exception {

        if (!motrSession.isAllowedOnSmsConfirmationCodePage()) {
            return redirect("/");
        }

        SmsConfirmationCodeViewModel viewModel = new SmsConfirmationCodeViewModel().setPhoneNumber(motrSession.getPhoneNumberFromSession());
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("continue_button_text", "Continue");
        modelMap.put("resendUrl", "resend");
        modelMap.put("viewModel", viewModel);

        return Response.ok(renderer.render(SMS_CONFIRMATION_CODE_TEMPLATE, modelMap)).build();
    }

    @POST
    public Response smsConfirmationCodePagePost(@FormParam("confirmationCode") String confirmationCode) throws Exception {

        if (!motrSession.isAllowedToPostOnSmsConfirmationCodePage()) {
            return redirect("/");
        }

        Validator validator = new SmsConfirmationCodeValidator();

        if (validator.isValid(confirmationCode)) {

            String phoneNumber = motrSession.getPhoneNumberFromSession();
            String vrm = motrSession.getVrmFromSession();
            String confirmationId = motrSession.getConfirmationIdFromSession();

            boolean confirmationCodeVerified = smsConfirmationService.verifySmsConfirmationCode(
                    vrm, phoneNumber, confirmationId, confirmationCode);

            if (confirmationCodeVerified) {
                return redirect(urlHelper.confirmSubscriptionLink(confirmationId));
            }

            validator.setMessage(SmsConfirmationCodeValidator.INVALID_CONFIRMATION_CODE_MESSAGE);
            validator.setMessageAtField(SmsConfirmationCodeValidator.INVALID_CONFIRMATION_CODE_MESSAGE_AT_FIELD);
        }

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put(MESSAGE_MODEL_KEY, validator.getMessage());
        modelMap.put(MESSAGE_AT_FIELD_MODEL_KEY, validator.getMessageAtField());
        dataLayerHelper.putAttribute(ERROR_KEY, validator.getMessage());

        SmsConfirmationCodeViewModel viewModel = new SmsConfirmationCodeViewModel().setPhoneNumber(motrSession.getPhoneNumberFromSession());
        modelMap.put("viewModel", viewModel);

        modelMap.put(CONFIRMATION_CODE_MODEL_KEY, confirmationCode);
        modelMap.put("continue_button_text", "Continue");
        modelMap.put("resendUrl", "resend");
        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return Response.ok(renderer.render(SMS_CONFIRMATION_CODE_TEMPLATE, modelMap)).build();
    }
}
