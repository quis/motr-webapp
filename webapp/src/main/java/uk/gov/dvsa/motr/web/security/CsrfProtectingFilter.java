package uk.gov.dvsa.motr.web.security;

import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Form;
import javax.ws.rs.ext.Provider;

import static org.glassfish.jersey.message.internal.MediaTypes.typeEqual;

import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.Priorities.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;

/**
 * Crosscutting implementation of CSRF protection. Validates CSRF token on every non-GET request.
 */
@Priority(AUTHORIZATION + 2)
@Provider
public class CsrfProtectingFilter implements ContainerRequestFilter {

    private static final String CSRF_FORM_FIELD = "__csrf";
    private static final Logger logger = LoggerFactory.getLogger(CsrfProtectingFilter.class);
    private CsrfSource csrfSource;

    @Inject
    public CsrfProtectingFilter(CsrfSource csrfSource) {

        this.csrfSource = csrfSource;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (!GET.equalsIgnoreCase(requestContext.getMethod())) {
            ContainerRequest request = (ContainerRequest) requestContext;
            if (requestContext.hasEntity() && typeEqual(APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {

                request.bufferEntity();
                Form form = request.readEntity(Form.class);

                String requestToken = form.asMap().getFirst(CSRF_FORM_FIELD);
                String sessionToken = csrfSource.getToken();
                boolean validationResult = sessionToken.equals(requestToken);
                logger.debug("CSRF verification, incoming token: {}, sessionToken: {}, validationResult: {}",
                        requestToken, sessionToken, validationResult);

                if (!validationResult) {
                    logger.warn("Invalid CSRF token: {}, expecting: {}, redirecting to home page", requestToken, sessionToken);
                    requestContext.abortWith(redirect("/"));
                }
            }
        }
    }
}