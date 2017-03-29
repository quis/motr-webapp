package uk.gov.dvsa.motr.web.httpcache;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@CacheDynamic
@Provider
public class CacheDynamicFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {

        if (responseContext.getEntity() instanceof String) {
            String entity = (String) responseContext.getEntity();
            responseContext.getHeaders().add("ETag", DigestUtils.md5Hex(entity));
        }
    }
}