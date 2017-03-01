package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class EmailConfirmationPendingResourceTest {

    private TemplateEngineStub engine = new TemplateEngineStub();
    private MotrSession session = new MotrSession();
    EmailConfirmationPendingResource resource = new EmailConfirmationPendingResource(engine, session);

    @Test
    public void whenAccessed_emailConfirmationPendingTemplateIsRendered() throws Exception {

        assertEquals(RESPONSE, resource.confirmEmailGet());
        assertEquals("email-confirmation-pending", engine.getTemplate());
    }

    @Test
    public void whenAccessed_cookiesShouldBeCleared() throws Exception {

        resource.confirmEmailGet();
        assertTrue(session.isShouldClearCookies());
    }
}
