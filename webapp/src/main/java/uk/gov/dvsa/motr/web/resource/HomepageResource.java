package uk.gov.dvsa.motr.web.resource;


import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


import static uk.gov.dvsa.motr.web.system.SystemVariable.FEATURE_TOGGLE_SMS;

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

    @GET
    public String homePage() throws Exception {

        this.motrSession.setShouldClearCookies(true);
        Map<String, Object> values = new HashMap<>();
        values.put("featureToggleSms", motrSession.isSmsFeatureToggleOn());

        return renderer.render("home", values);
    }
}
