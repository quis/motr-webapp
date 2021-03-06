package uk.gov.dvsa.motr.web.resource;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.validator.PhoneNumberValidator;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhoneNumberResourceTest {

    private static final String PHONE_NUMBER = "07801856718";
    private static final String PHONE_NUMBER_INVALID = "0780185671";

    private MotrSession motrSession;
    private TemplateEngineStub engine;
    private PhoneNumberResource resource;
    private PhoneNumberValidator validator;

    @Before
    public void setup() {

        validator = mock(PhoneNumberValidator.class);
        motrSession = mock(MotrSession.class);
        engine = new TemplateEngineStub();
        resource = new PhoneNumberResource(motrSession, engine, validator);
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

        when(validator.isValid(PHONE_NUMBER)).thenReturn(true);
        Response response = resource.phoneNumberPagePost(PHONE_NUMBER);

        assertEquals(302, response.getStatus());
    }

    @Test
    public void onPostWithInValid_ThenNotRedirectedToReviewPage() throws Exception {

        when(validator.isValid(PHONE_NUMBER_INVALID)).thenReturn(false);
        Response response = resource.phoneNumberPagePost(PHONE_NUMBER_INVALID);

        assertEquals(200, response.getStatus());
        assertEquals("phone-number", engine.getTemplate());
    }
}
