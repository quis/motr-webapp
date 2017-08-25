package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.service.SmsConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResendSmsResourceTest {

    private static final SmsConfirmationService PENDING_SMS_SUBSCRIPTION_SERVICE = mock(SmsConfirmationService.class);
    private static final String PHONE_NUMBER = "07801856718";
    private static final String CONFIRMATION_ID = "ABC123";
    private static final String SMS_CONFIRMATION_CODE_LINK = "confirm-phone";

    private MotrSession motrSession;
    private ResendSmsResource resendSmsResource;

    @Before
    public void setup() {

        motrSession = mock(MotrSession.class);
        resendSmsResource = new ResendSmsResource(motrSession, PENDING_SMS_SUBSCRIPTION_SERVICE);
        when(motrSession.getPhoneNumberFromSession()).thenReturn(PHONE_NUMBER);
        when(motrSession.getConfirmationIdFromSession()).thenReturn(CONFIRMATION_ID);
    }

    @Test
    public void smsIsResentOnValidGet() throws Exception {

        when(motrSession.isAllowedToResendSmsConfirmationCode()).thenReturn(true);
        when(PENDING_SMS_SUBSCRIPTION_SERVICE.resendSms(any(), any())).thenReturn(SMS_CONFIRMATION_CODE_LINK);

        Response response = resendSmsResource.resendSmsResourceGet();

        verify(PENDING_SMS_SUBSCRIPTION_SERVICE, times(1)).resendSms(PHONE_NUMBER, CONFIRMATION_ID);
        assertEquals(302, response.getStatus());
        assertEquals("confirm-phone", response.getLocation().toString());
    }
}
