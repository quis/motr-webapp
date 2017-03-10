package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.performance.warmup.DefaultLambdaWarmUp;
import uk.gov.dvsa.motr.web.performance.warmup.LambdaWarmUp;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.system.binder.factory.BaseFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Provider;

import static uk.gov.dvsa.motr.web.system.SystemVariable.DO_WARM_UP;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_TOKEN;
import static uk.gov.dvsa.motr.web.system.SystemVariable.WARM_UP_TIMEOUT_SEC;

public class LambdaWarmUpBinder extends AbstractBinder {

    private static final Logger logger = LoggerFactory.getLogger(LambdaWarmUpBinder.class);

    @Override
    protected void configure() {

        bindFactory(LambdaWarmUpFactory.class).to(LambdaWarmUp.class);
    }

    static class LambdaWarmUpFactory implements BaseFactory<LambdaWarmUp> {

        private Provider<TemplateEngine> templateEngineProvider;
        private Provider<VehicleDetailsClient> vehicleDetailsClientProvider;
        private Provider<SubscriptionRepository> subscriptionRepositoryProvider;

        private Config config;

        @Inject
        public LambdaWarmUpFactory(
                Provider<TemplateEngine> templateEngine,
                Provider<VehicleDetailsClient> vehicleDetailsClient,
                Provider<SubscriptionRepository> subscriptionRepository,
                Config config) {

            this.templateEngineProvider = templateEngine;
            this.vehicleDetailsClientProvider = vehicleDetailsClient;
            this.subscriptionRepositoryProvider = subscriptionRepository;
            this.config = config;
        }

        @Override
        public LambdaWarmUp provide() {

            if (Boolean.parseBoolean(config.getValue(DO_WARM_UP))) {
                return new DefaultLambdaWarmUp(taskList(), Integer.valueOf(config.getValue(WARM_UP_TIMEOUT_SEC)));
            } else {
                return LambdaWarmUp.NOOP;
            }
        }

        private List<Callable> taskList() {

            List<Callable> tasks = new ArrayList<>();

            tasks.add(() -> {

                logger.debug("Warming up template engine - start");

                templateEngineProvider.get();

                logger.debug("Warming up template engine - end");

                return null;
            });

            tasks.add(() -> {

                logger.debug("Warming up DynamoDB client - start");

                subscriptionRepositoryProvider.get();

                logger.debug("Warming up DynamoDB client - end");

                return null;
            });
            
            tasks.add(() -> {

                logger.debug("Warming up notify api key - start");

                String decrypted = config.getValue(MOT_TEST_REMINDER_INFO_TOKEN);

                logger.debug("Warming up notify api key - end {}", decrypted);

                return null;

            });
            
            return tasks;
        }
    }
}


