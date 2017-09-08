package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService;
import uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService.Confirmation;
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
            SmsConfirmationService smsConfirmationService,
            UrlHelper urlHelper
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
        modelMap.put("showCodeEntry", true);

        return Response.ok(renderer.render(SMS_CONFIRMATION_CODE_TEMPLATE, modelMap)).build();
    }

    @POST
    public Response smsConfirmationCodePagePost(@FormParam("confirmationCode") String confirmationCode) throws Exception {

        Validator validator = new SmsConfirmationCodeValidator();
        boolean resendAllowed = true;
        boolean showCodeEntry = true;

        //this block is for when the entered code passes simple front end validation
        if (validator.isValid(confirmationCode)) {

            String confirmationId = motrSession.getConfirmationIdFromSession();
            Confirmation codeValid = smsConfirmationService.verifySmsConfirmationCode(
                    motrSession.getVrmFromSession(),
                    motrSession.getPhoneNumberFromSession(),
                    confirmationId,
                    confirmationCode);

            switch (codeValid) {
                case CODE_NOT_VALID_MAX_ATTEMPTS_REACHED_RESEND_NOT_ALLOWED_TIME_LIMITED:
                    validator.setMessage(SmsConfirmationCodeValidator.CODE_INCORRECT_3_TIMES_CANNOT_GET_NEW_CODE);
                    validator.setMessageAtField(SmsConfirmationCodeValidator.CODE_INCORRECT_3_TIMES_CANNOT_GET_NEW_CODE_AT_FIELD);
                    resendAllowed = false;
                    showCodeEntry = false;
                    break;
                case CODE_NOT_VALID_MAX_ATTEMPTS_REACHED_RESEND_ALLOWED:
                    validator.setMessage(SmsConfirmationCodeValidator.CODE_INCORRECT_3_TIMES_CAN_GET_NEW_CODE);
                    validator.setMessageAtField(SmsConfirmationCodeValidator.CODE_INCORRECT_3_TIMES_CAN_GET_NEW_CODE_AT_FIELD);
                    resendAllowed = true;
                    showCodeEntry = false;
                    break;
                case CODE_NOT_VALID_RESEND_ALLOWED:
                    validator.setMessage(SmsConfirmationCodeValidator.INVALID_CONFIRMATION_CODE_MESSAGE);
                    validator.setMessageAtField(SmsConfirmationCodeValidator.INVALID_CONFIRMATION_CODE_MESSAGE_AT_FIELD);
                    resendAllowed = true;
                    showCodeEntry = true;
                    break;
                case CODE_NOT_VALID_RESEND_NOT_ALLOWED_TIME_LIMITED:
                    validator.setMessage(SmsConfirmationCodeValidator.INVALID_CONFIRMATION_CODE_MESSAGE);
                    validator.setMessageAtField(SmsConfirmationCodeValidator.INVALID_CONFIRMATION_CODE_MESSAGE_AT_FIELD);
                    resendAllowed = false;
                    showCodeEntry = true;
                    break;
                case CODE_VALID:
                    return redirect(urlHelper.confirmSubscriptionLink(confirmationId));
                default:
                    break;
            }
        }

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put(MESSAGE_MODEL_KEY, validator.getMessage());
        modelMap.put(MESSAGE_AT_FIELD_MODEL_KEY, validator.getMessageAtField());
        dataLayerHelper.putAttribute(ERROR_KEY, validator.getMessage());

        modelMap.put(CONFIRMATION_CODE_MODEL_KEY, confirmationCode);
        modelMap.put("continue_button_text", "Continue");
        modelMap.put("resendUrl", "resend");
        modelMap.put("resendAllowed", resendAllowed);
        modelMap.put("showCodeEntry", showCodeEntry);
        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return Response.ok(renderer.render(SMS_CONFIRMATION_CODE_TEMPLATE, modelMap)).build();
    }
}
