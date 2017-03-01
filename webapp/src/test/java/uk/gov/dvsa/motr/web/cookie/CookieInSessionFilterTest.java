package uk.gov.dvsa.motr.web.cookie;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Clock;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.time.LocalDateTime.parse;
import static java.time.ZoneOffset.UTC;

public class CookieInSessionFilterTest {

    private final String attributeKey = "vrm";
    private final String attributeValue = "TEST-VRM";
    private final String cookieString = "session=rO0ABXNyACl1ay5nb3YuZHZzYS5tb3RyLndlYi5jb29raWUuQ29va2llU2Vzc2lvbsu1QnSCTN+xAgA" +
            "BTAAKYXR0cmlidXRlc3QAD0xqYXZhL3V0aWwvTWFwO3hwc3IAEWphdmEudXRpbC5IYXNoTWFwBQfawcMWYNEDAAJGAApsb2FkRmFjdG9ySQAJdGhyZXNob2xke" +
            "HA/QAAAAAAADHcIAAAAEAAAAAF0AAN2cm10AAhURVNULVZSTXg=" +
            ";Version=1;Path=/;Max-Age=1200;Secure;HttpOnly;Expires=Sat, 01 Jan 2000 10:20:00 GMT";



    private Clock clockReference = Clock.fixed(parse("2000-01-01T10:00:00").toInstant(UTC), UTC);

    private MotrSession motrSession;
    private CookieInSessionFilter cookieInSessionFilter;

    @Before
    public void setUp() {

        motrSession = mock(MotrSession.class);

        cookieInSessionFilter = new CookieInSessionFilter(motrSession);
        cookieInSessionFilter.setClock(clockReference);
    }

    @Test
    public void whenNoCookiesInRequestSessionIsEmpty() throws IOException {

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getCookies()).thenReturn(null);

        cookieInSessionFilter.filter(containerRequestContext);
        assertTrue(motrSession.getAttributes().isEmpty());
    }

    @Test
    public void whenSessionCookieIsNotFoundSessionIsEmpty() throws IOException {

        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("not-a-session-cookie", new Cookie("not-a-session-cookie", ""));

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getCookies()).thenReturn(cookies);

        cookieInSessionFilter.filter(containerRequestContext);
        assertTrue(motrSession.getAttributes().isEmpty());
    }

    @Test
    public void whenSessionCookieIsFoundSessionIsPopulated() throws Exception {

        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("session", new Cookie("session", setUpCookieString()));

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getCookies()).thenReturn(cookies);

        cookieInSessionFilter.filter(containerRequestContext);
        verify(motrSession, times(1)).setAttribute(attributeKey, attributeValue);
    }

    @Test
    public void valuesEnteredIntoSessionAreSavedIntoTheCookie() throws Exception {

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put(attributeKey, attributeValue);
        MultivaluedMap<String, Object> headerMap = mock(MultivaluedMap.class);

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        ContainerResponseContext containerResponseContext = mock(ContainerResponseContext.class);
        when(motrSession.getAttributes()).thenReturn(hashMap);
        when(containerResponseContext.getHeaders()).thenReturn(headerMap);

        cookieInSessionFilter.filter(containerRequestContext, containerResponseContext);
        verify(headerMap, times(1)).add(eq("Set-Cookie"), eq(cookieString));
        verify(motrSession, times(1)).getAttributes();
    }

    private String setUpCookieString() throws IOException {
        CookieSession cookieSession = new CookieSession();
        cookieSession.setAttribute(attributeKey, attributeValue);

        return toString(cookieSession);
    }

    private String toString(Serializable object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }
}
