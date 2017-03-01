package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.encryption.Decryptor;
import uk.gov.dvsa.motr.web.security.CsrfSource;
import uk.gov.dvsa.motr.web.system.binder.factory.AwsKmsDecryptorFactory;
import uk.gov.dvsa.motr.web.system.binder.factory.MotrConfigFactory;
import uk.gov.dvsa.motr.web.system.binder.factory.VehicleDetailsClientFactory;

import javax.inject.Singleton;

public class ConfigBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bindFactory(MotrConfigFactory.class).to(Config.class).in(Singleton.class);
        bindFactory(AwsKmsDecryptorFactory.class).to(Decryptor.class);
        bindFactory(VehicleDetailsClientFactory.class).to(VehicleDetailsClient.class);
        bind(CsrfSource.class).to(CsrfSource.class);
    }
}
