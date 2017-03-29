package uk.gov.dvsa.motr.web.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;

import uk.gov.dvsa.motr.web.performance.warmup.PingAwareAwsProxyRequest;
import uk.gov.dvsa.motr.web.test.aws.TestLambdaContext;
import uk.gov.dvsa.motr.web.test.environment.TestEnvironmentVariables;

import static org.junit.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class MotrWebHandlerNotFoundTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private final MotrWebHandler handler = new MotrWebHandler();

    @DataProvider
    public static Object[][] notFoundResources() {
        // @formatter:off
        return new Object[][]{
                {"/resource-that-does-not-exist"},
                {"/home"},
        };
    }
    // @formatter:on

    @UseDataProvider("notFoundResources")
    @Test
    public void return404WhenResourceDoesNotExist(String resourcePath) throws Exception {

        PingAwareAwsProxyRequest req = buildRequest("GET", resourcePath);

        assertEquals(404, handle(req).getStatusCode());
    }

    @UseDataProvider("notFoundResources")
    @Test
    public void returnHtmlResponseWhenResourceDoesNotExist(String resourcePath) throws Exception {

        PingAwareAwsProxyRequest req = buildRequest("GET", resourcePath);

        assertEquals("text/html", handle(req).getHeaders().get("Content-type"));
    }

    private PingAwareAwsProxyRequest buildRequest(String method, String path) {

        PingAwareAwsProxyRequest req = new PingAwareAwsProxyRequest();
        req.setPath(path);
        req.setHttpMethod(method);
        return req;
    }

    private AwsProxyResponse handle(PingAwareAwsProxyRequest req) {
        return handler.handleRequest(req, new TestLambdaContext());
    }
}
