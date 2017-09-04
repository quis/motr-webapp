package uk.gov.dvsa.motr.web.system;

import org.glassfish.jersey.server.ResourceConfig;

import uk.gov.dvsa.motr.web.system.binder.ConfigBinder;
import uk.gov.dvsa.motr.web.system.binder.LambdaWarmUpBinder;
import uk.gov.dvsa.motr.web.system.binder.RepositoryBinder;
import uk.gov.dvsa.motr.web.system.binder.ServiceBinder;
import uk.gov.dvsa.motr.web.system.binder.SessionBinder;
import uk.gov.dvsa.motr.web.system.binder.SystemVariableBinder;
import uk.gov.dvsa.motr.web.system.binder.TemplateEngineBinder;
import uk.gov.dvsa.motr.web.system.binder.ValidatorBinder;

public class MotrWebApplication extends ResourceConfig {

    public MotrWebApplication() {

        packages("uk.gov.dvsa.motr.web");

        register(new ConfigBinder());
        register(new TemplateEngineBinder());
        register(new SystemVariableBinder());
        register(new ServiceBinder());
        register(new RepositoryBinder());
        register(new SessionBinder());
        register(new LambdaWarmUpBinder());
        register(new ValidatorBinder());
    }
}
