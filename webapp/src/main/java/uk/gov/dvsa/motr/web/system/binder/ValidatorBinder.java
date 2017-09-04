package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.validator.PhoneNumberValidator;

public class ValidatorBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bind(PhoneNumberValidator.class).to(PhoneNumberValidator.class);
    }
}
