package uk.gov.dvsa.motr.web.httpcache;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

public class CacheDynamicFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {

        if (resourceInfo.getResourceMethod().getAnnotation(CacheDynamic.class) != null) {
            context.register(CacheDynamicFilter.class);
        } else {
            context.register(NoCacheClientResponseFilter.class);
        }
    }
}