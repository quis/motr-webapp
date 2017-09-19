package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/resend")
public class ResendSmsResource {

    private final MotrSession motrSession;
    private UrlHelper urlHelper;

    private SmsConfirmationService smsConfirmationService;

    @Inject
    public ResendSmsResource(
            MotrSession motrSession,
            SmsConfirmationService smsConfirmationService,
            UrlHelper urlHelper
    ) {
        this.motrSession = motrSession;
        this.smsConfirmationService = smsConfirmationService;
        this.urlHelper = urlHelper;
    }

    @GET
    public Response resendSmsResourceGet() throws Exception {

        if (!motrSession.isAllowedToResendSmsConfirmationCode()) {
            return redirect("/");
        }

        String redirectUri;
        boolean resendLimited = !smsConfirmationService.smsSendingNotRestrictedByRateLimiting(
                motrSession.getPhoneNumberFromSession(),
                motrSession.getConfirmationIdFromSession());

        motrSession.setSmsConfirmResendLimited(resendLimited);
        if (resendLimited) {
            redirectUri = urlHelper.phoneConfirmationLink();
        } else {
            redirectUri = smsConfirmationService.resendSms(motrSession.getPhoneNumberFromSession(),
                    motrSession.getConfirmationIdFromSession());
        }

        return redirect(redirectUri);
    }
}
