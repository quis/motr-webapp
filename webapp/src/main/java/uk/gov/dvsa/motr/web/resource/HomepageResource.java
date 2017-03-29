package uk.gov.dvsa.motr.web.resource;


import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.httpcache.CacheDynamic;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/")
@Produces("text/html")
public class HomepageResource {

    private final TemplateEngine renderer;
    private final MotrSession motrSession;

    @Inject
    public HomepageResource(
            MotrSession motrSession,
            TemplateEngine renderer
    ) {

        this.motrSession = motrSession;
        this.renderer = renderer;
    }

    @CacheDynamic
    @GET
    public String homePage() throws Exception {

        this.motrSession.setShouldClearCookies(true);
        return renderer.render("home", emptyMap());
    }
}
