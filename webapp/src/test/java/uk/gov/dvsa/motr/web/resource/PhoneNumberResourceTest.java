package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhoneNumberResourceTest {

    private static final String PHONE_NUMBER = "07801856718";

    private MotrSession motrSession;
    private TemplateEngineStub engine;
    private PhoneNumberResource resource;

    @Before
    public void setup() {

        motrSession = mock(MotrSession.class);
        engine = new TemplateEngineStub();
        resource = new PhoneNumberResource(motrSession, engine);
        when(motrSession.getPhoneNumberFromSession()).thenReturn(PHONE_NUMBER);
    }

    @Test
    public void phoneNumberTemplateIsRenderedOnGet() throws Exception {

        when(motrSession.isAllowedOnPhoneNumberEntryPage()).thenReturn(true);

        assertEquals(200, resource.phoneNumberPageGet().getStatus());
        assertEquals("phone-number", engine.getTemplate());
    }

    @Test
    public void onPostWithValid_ThenRedirectedToReviewPage() throws Exception {

        Response response = resource.phoneNumberPagePost(PHONE_NUMBER);

        assertEquals(302, response.getStatus());
    }
}
