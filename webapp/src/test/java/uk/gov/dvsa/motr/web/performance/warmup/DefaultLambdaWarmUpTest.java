package uk.gov.dvsa.motr.web.performance.warmup;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.Callable;

import static java.util.Collections.singletonList;

public class DefaultLambdaWarmUpTest {

    @Test(timeout = 2000)
    public void testWarmUpFinishesWithinTimeout() throws Exception {

        warmUpWithin1Second(singletonList(taskWithDurationMs(500)));
    }

    @Test(timeout = 2000)
    public void warmUpExecutesWithoutError() throws Exception {

        warmUpWithin1Second(singletonList(taskWithDurationMs(3_000)));
    }

    private Callable taskWithDurationMs(int millis) throws Exception {

        return () -> {
            try {
                Thread.sleep(millis);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            return null;
        };
    }

    private void warmUpWithin1Second(List<Callable> tasks) {

        new DefaultLambdaWarmUp(tasks,1).warmUp();
    }
}
