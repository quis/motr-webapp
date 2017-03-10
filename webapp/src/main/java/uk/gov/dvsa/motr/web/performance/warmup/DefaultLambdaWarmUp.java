package uk.gov.dvsa.motr.web.performance.warmup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Responsible for warming up all entities that require significant initialization time during first execution.
 * The process is parallelized to optimise execution time.
 */
public class DefaultLambdaWarmUp implements LambdaWarmUp {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLambdaWarmUp.class);

    private List<Callable> tasks = new ArrayList<>();

    private int warmUpTimeoutSeconds;

    public DefaultLambdaWarmUp(List<Callable> tasks, int warmUpTimeoutSeconds) {

        this.tasks = tasks;
        this.warmUpTimeoutSeconds = warmUpTimeoutSeconds;
    }

    public void warmUp() {

        try {

            ExecutorService warmUpExecutor = Executors.newCachedThreadPool();
            tasks.forEach(warmUpExecutor::submit);
            warmUpExecutor.awaitTermination(warmUpTimeoutSeconds, SECONDS);
            warmUpExecutor.shutdownNow();

        } catch (InterruptedException exception) {

            logger.info("Interrupted due to timeout");

        } catch (Exception exception) {

            logger.error("Error occurred", exception);
        }
    }
}
