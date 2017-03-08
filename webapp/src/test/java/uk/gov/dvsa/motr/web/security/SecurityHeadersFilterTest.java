package uk.gov.dvsa.motr.web.security;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.web.handler.MotrWebHandler;
import uk.gov.dvsa.motr.web.test.aws.TestLambdaContext;
import uk.gov.dvsa.motr.web.test.environment.TestEnvironmentVariables;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SecurityHeadersFilterTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private final MotrWebHandler handler = new MotrWebHandler();

    @Test
    public void securityHeadersSet() {

        AwsProxyRequest req = new AwsProxyRequestBuilder("/", "GET").build();

        Map<String, String> headers = handler.handleRequest(req, new TestLambdaContext()).getHeaders();

        assertEquals("1", headers.get("X-XSS-Protection"));
        assertEquals("DENY", headers.get("X-Frame-Options"));
    }
}
