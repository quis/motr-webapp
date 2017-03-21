package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.viewmodel.ViewModel;

import static org.junit.Assert.assertEquals;

public class HomepageResourceTest {

    @Test
    public void homepageTemplateIsRenderedWhenRootPathAccessed() throws Exception {

        HomepageResource resource = new HomepageResource(new MotrSession());
        ViewModel vm = resource.homePage();
        assertEquals("home", vm.getTemplate());
    }
}
