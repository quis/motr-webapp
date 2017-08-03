package uk.gov.dvsa.motr.notifications.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.web.helper.DateDisplayHelper;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotifyServiceTest {

    private NotifyService service;
    private static final NotificationClient CLIENT = mock(NotificationClient.class);

    private String email = "test@test.com";
    private String templateId = "180";
    private String emailConfirmationTemplateId = "180";
    private String registrationNumber = "testreg";
    private LocalDate motExpiryDate = LocalDate.of(2017, 1, 1);
    private String unsubscribeLink = "https://gov.uk";
    private static final String MOT_TEST_NUMBER = "12345";

    @Before
    public void setUp() {

        this.service = new NotifyService(CLIENT, templateId, emailConfirmationTemplateId);
    }

    @Test
    public void notifyCalledWithCorrectValues() throws NotificationClientException {

        Map<String, String> personalisationMap = stubPersonalisationMap(registrationNumber, motExpiryDate, unsubscribeLink);

        when(CLIENT.sendEmail(any(), any(), any(), any())).thenReturn(mock(SendEmailResponse.class));

        this.service.sendSubscriptionConfirmationEmail(email, registrationNumber, motExpiryDate, unsubscribeLink, motIdentificationStub());

        verify(CLIENT, times(1)).sendEmail(templateId, email, personalisationMap, "");
    }

    @Test(expected = RuntimeException.class)
    public void whenNotifyFailsExceptionIsThrown() throws NotificationClientException {

        when(CLIENT.sendEmail(any(), any(), any(), any())).thenThrow(NotificationClientException.class);

        this.service.sendSubscriptionConfirmationEmail(email, registrationNumber, motExpiryDate, unsubscribeLink, motIdentificationStub());
    }

    private Map<String, String> stubPersonalisationMap(String registrationNumber, LocalDate expiryDate, String link) {
        Map<String, String> map = new HashMap<>();
        map.put("registration_number", registrationNumber);
        map.put("mot_expiry_date", DateDisplayHelper.asDisplayDate(expiryDate));
        map.put("unsubscribe_link", link);
        map.put("is_due_or_expires", "expires");
        map.put("due_or_expiry", "expiry");
        return map;
    }

    private MotIdentification motIdentificationStub() {

        return new MotIdentification(MOT_TEST_NUMBER, null);
    }
}
