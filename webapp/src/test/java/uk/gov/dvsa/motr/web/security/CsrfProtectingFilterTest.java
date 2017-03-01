package uk.gov.dvsa.motr.web.security;

import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Test;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;

public class CsrfProtectingFilterTest {

    private static final String VALID_TOKEN = "validToken";

    @Test
    public void whenCsrfTokensMatch_requestPassesThrough() throws Exception {

        ContainerRequest request = postRequest(VALID_TOKEN);
        filterInstance().filter(request);
        requestIsAborted(request, false);
    }

    @Test
    public void whenCsrfTokensDontMatch_requestIsAborted() throws Exception {

        ContainerRequest request = postRequest(VALID_TOKEN + "isNowInvalid");
        filterInstance().filter(request);
        requestIsAborted(request, true);
    }

    @Test
    public void skipGetRequest() throws Exception {

        ContainerRequest getRequest = getRequest();
        filterInstance().filter(getRequest);

        verify(getRequest, times(0)).hasEntity();
    }

    private CsrfProtectingFilter filterInstance() {

        CsrfSource csrfSource = mock(CsrfSource.class);
        when(csrfSource.getToken()).thenReturn(VALID_TOKEN);
        return new CsrfProtectingFilter(csrfSource);
    }

    private ContainerRequest getRequest() {

        ContainerRequest request = mock(ContainerRequest.class);
        when(request.getMethod()).thenReturn("GET");
        return request;
    }

    private ContainerRequest postRequest(String csrfToken) {

        ContainerRequest request = mock(ContainerRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getMediaType()).thenReturn(APPLICATION_FORM_URLENCODED_TYPE);
        when(request.hasEntity()).thenReturn(true);
        when(request.readEntity(Form.class)).thenReturn(new Form("__csrf", csrfToken));
        return request;
    }

    private void requestIsAborted(ContainerRequest request, boolean isAborted) {

        verify(request, times(isAborted ? 1 : 0)).abortWith(any(Response.class));
    }
}
