package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.validator.EmailValidator;
import uk.gov.dvsa.motr.web.viewmodel.ViewModel;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailResourceTest {

    private MotrSession motrSession;
    private EmailResource resource;

    @Before
    public void setup() {

        motrSession = mock(MotrSession.class);
        resource = new EmailResource(motrSession);
        when(motrSession.getEmailFromSession()).thenReturn("test@test.com");
    }

    @Test
    public void emailTemplateIsRenderedOnGet() throws Exception {

        when(motrSession.isAllowedOnEmailPage()).thenReturn(true);
        ViewModel vm = (ViewModel)resource.emailPage();
        assertEquals("email", vm.getTemplate());

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("back_url", "vrm");
        expectedMap.put("continue_button_text", "Continue");
        expectedMap.put("back_button_text", "Back");
        expectedMap.put("email", "test@test.com");
        assertEquals(expectedMap.toString(), vm.getContextMap().toString());
    }

    @Test
    public void onPostWithValid_ThenRedirectedToReviewPage() throws Exception {

        Response response = (Response)resource.emailPagePost("test@test.com");
        assertEquals(302, response.getStatus());
    }

    @Test
    public void onPostWithInvalidEmailFormatMessageWillBePassedToView() throws Exception {

        HashMap<String, String> expectedContext = new HashMap<>();
        expectedContext.put("message", EmailValidator.EMAIL_INVALID_MESSAGE);
        expectedContext.put("back_url", "vrm");
        expectedContext.put("email", "invalidEmail");
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("dataLayer", "{\"error\":\"" + EmailValidator.EMAIL_INVALID_MESSAGE + "\"}");

        ViewModel vm = (ViewModel) resource.emailPagePost("invalidEmail");
        assertEquals("email", vm.getTemplate());
        assertEquals(expectedContext, vm.getContextMap());
    }

    @Test
    public void onPostWithEmptyEmailFormatMessageWillBePassedToView() throws Exception {

        HashMap<String, String> expectedContext = new HashMap<>();
        expectedContext.put("message", EmailValidator.EMAIL_EMPTY_MESSAGE);
        expectedContext.put("back_url", "vrm");
        expectedContext.put("email", "");
        expectedContext.put("continue_button_text", "Continue");
        expectedContext.put("back_button_text", "Back");
        expectedContext.put("dataLayer", "{\"error\":\"" + EmailValidator.EMAIL_EMPTY_MESSAGE + "\"}");

        ViewModel vm = (ViewModel)resource.emailPagePost("");
        assertEquals("email", vm.getTemplate());
        assertEquals(expectedContext, vm.getContextMap());
    }
}
