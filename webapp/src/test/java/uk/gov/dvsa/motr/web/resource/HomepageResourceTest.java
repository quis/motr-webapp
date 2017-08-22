package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class HomepageResourceTest {

    @Test
    public void homepageTemplateIsRenderedWhenRootPathAccessed() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        HomepageResource resource = new HomepageResource(new MotrSession(true), engine);

        assertEquals(RESPONSE, resource.homePage());
        assertEquals("home", engine.getTemplate());
    }
}
