package uk.gov.dvsa.motr.web.resource;


import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.viewmodel.ViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Singleton
@Path("/")
@Produces("text/html")
public class HomepageResource {

    private final MotrSession motrSession;

    @Inject
    public HomepageResource(MotrSession motrSession) {

        this.motrSession = motrSession;
    }

    @GET
    public ViewModel homePage() throws Exception {

        this.motrSession.setShouldClearCookies(true);
        return new ViewModel("home");
    }
}
