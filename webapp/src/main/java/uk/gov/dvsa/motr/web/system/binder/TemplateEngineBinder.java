package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.render.HandlebarsTemplateEngine;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Singleton;

public class TemplateEngineBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(HandlebarsTemplateEngine.class).to(TemplateEngine.class).in(Singleton.class);
    }

}
