package uk.gov.dvsa.motr.web.httpcache;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

public class CacheDynamicFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {

        if (responseContext.getEntity() instanceof String) {
            String entity = (String) responseContext.getEntity();
            responseContext.getHeaders().add("Cache-Control", "public, max-age=60, s-maxage=60, must-revalidate");
        }
    }
}