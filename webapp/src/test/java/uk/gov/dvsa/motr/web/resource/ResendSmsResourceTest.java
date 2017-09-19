package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResendSmsResourceTest {

    private static final String PHONE_NUMBER = "07801856718";
    private static final String CONFIRMATION_ID = "ABC123";
    private static final String SMS_CONFIRMATION_CODE_LINK = "confirm-phone";

    private SmsConfirmationService smsConfirmationService = mock(SmsConfirmationService.class);
    private MotrSession motrSession;
    private ResendSmsResource resendSmsResource;
    private UrlHelper urlHelper;

    @Before
    public void setup() {

        smsConfirmationService = mock(SmsConfirmationService.class);
        motrSession = mock(MotrSession.class);
        urlHelper = mock(UrlHelper.class);
        resendSmsResource = new ResendSmsResource(motrSession, smsConfirmationService, urlHelper);
        when(motrSession.getPhoneNumberFromSession()).thenReturn(PHONE_NUMBER);
        when(motrSession.getConfirmationIdFromSession()).thenReturn(CONFIRMATION_ID);
    }

    @Test
    public void smsIsResentOnValidGet() throws Exception {

        when(motrSession.isAllowedToResendSmsConfirmationCode()).thenReturn(true);
        when(smsConfirmationService.smsSendingNotRestrictedByRateLimiting(eq(PHONE_NUMBER), eq(CONFIRMATION_ID)))
                .thenReturn(true);
        when(smsConfirmationService.resendSms(any(), any())).thenReturn(SMS_CONFIRMATION_CODE_LINK);

        Response response = resendSmsResource.resendSmsResourceGet();

        verify(smsConfirmationService, times(1)).resendSms(PHONE_NUMBER, CONFIRMATION_ID);
        assertEquals(302, response.getStatus());
        assertEquals("confirm-phone", response.getLocation().toString());
    }

    @Test
    public void testNoResendOfSms_whenResendIsLimited() throws Exception {

        when(motrSession.isAllowedToResendSmsConfirmationCode()).thenReturn(true);
        when(smsConfirmationService.smsSendingNotRestrictedByRateLimiting(eq(PHONE_NUMBER), eq(CONFIRMATION_ID)))
                .thenReturn(false);
        when(smsConfirmationService.resendSms(any(), any())).thenReturn(SMS_CONFIRMATION_CODE_LINK);
        when(urlHelper.phoneConfirmationLink()).thenReturn(SMS_CONFIRMATION_CODE_LINK);

        Response response = resendSmsResource.resendSmsResourceGet();

        verify(smsConfirmationService, times(0)).resendSms(PHONE_NUMBER, CONFIRMATION_ID);
        assertEquals(302, response.getStatus());
        assertEquals("confirm-phone", response.getLocation().toString());
    }
}
