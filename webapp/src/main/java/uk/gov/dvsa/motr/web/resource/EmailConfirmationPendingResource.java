package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/email-confirmation-pending")
@Produces("text/html")
public class EmailConfirmationPendingResource {

    private final TemplateEngine renderer;
    private final MotrSession session;

    @Inject
    public EmailConfirmationPendingResource(
            TemplateEngine renderer,
            MotrSession session
    ) {

        this.renderer = renderer;
        this.session = session;
    }

    @GET
    public String confirmEmailGet() {

        session.setShouldClearCookies(true);
        return renderer.render("email-confirmation-pending", emptyMap());
    }
}
