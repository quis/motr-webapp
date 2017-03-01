package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

import uk.gov.dvsa.motr.web.cookie.MotrSession;

public class SessionBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bind(MotrSession.class).to(MotrSession.class).in(RequestScoped.class)
                .proxy(true).proxyForSameScope(false);

    }
}
