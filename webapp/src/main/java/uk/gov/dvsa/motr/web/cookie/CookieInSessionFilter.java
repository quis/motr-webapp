package uk.gov.dvsa.motr.web.cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.session.SessionMalformedEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.Provider;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Date.from;

import static javax.ws.rs.Priorities.AUTHORIZATION;
import static javax.ws.rs.core.Cookie.DEFAULT_VERSION;

@Priority(AUTHORIZATION + 1)
@Provider
public class CookieInSessionFilter implements ContainerResponseFilter, ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CookieInSessionFilter.class);
    public static final int MAX_COOKIE_AGE_IN_SECONDS = 60 * 20;
    private MotrSession motrSession;
    private Clock clockReference = Clock.system(ZoneId.of("Europe/London"));

    @Inject
    public CookieInSessionFilter(MotrSession motrSession) {

        this.motrSession = motrSession;
    }

    public void setClock(Clock clock) {
        this.clockReference = clock;
    }


    @Override
    public void filter(ContainerRequestContext requestContext) {

        try {
            populateSessionWithCookie(requestContext);
        } catch (Exception e) {
            EventLogger.logErrorEvent(new SessionMalformedEvent(), e);
            throw new NotFoundException();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {


        storeSessionInCookie(responseContext);
    }

    private void populateSessionWithCookie(ContainerRequestContext requestContext) throws IOException, ClassNotFoundException {

        Cookie sessionCookie = getSessionCookie(requestContext);
        if (null != sessionCookie) {
            CookieSession cookieSession = (CookieSession) fromString(sessionCookie.getValue());
            if (cookieSession.getAttributes() != null && !cookieSession.getAttributes().isEmpty()) {
                cookieSession.getAttributes().forEach(this.motrSession::setAttribute);
            }
        }
    }

    private void storeSessionInCookie(ContainerResponseContext responseContext) throws IOException {

        CookieSession cookieSession = new CookieSession();
        /**
         * CSRF cookie needs to be set as part of session because API Gateway does not allow
         * to set multiple Set-Cookie headers!
         */
        this.motrSession.getAttributes().forEach(cookieSession::setAttribute);

        responseContext.getHeaders().add("Set-Cookie",
                getSecureHttpOnlyCookieHeader("session", toString(cookieSession)));
    }

    private String getSecureHttpOnlyCookieHeader(String key, Object value) {

        logger.debug("getSecureHttpOnlyCookieHeader has isShouldClearCookies value of {}",
                this.motrSession.isShouldClearCookies());
        int maxAge = this.motrSession.isShouldClearCookies() ? 0 : MAX_COOKIE_AGE_IN_SECONDS;

        Date expires = from(ZonedDateTime.now(clockReference).plus(MAX_COOKIE_AGE_IN_SECONDS, SECONDS)
                .toInstant());
        NewCookie newCookie = new NewCookie(
                key,
                value.toString(),
                "/",
                null,
                DEFAULT_VERSION,
                null,
                maxAge,
                expires,
                true,
                true
        );

        logger.debug("NewCookie has value of {}", newCookie.toString());

        return newCookie.toString();
    }

    private Cookie getSessionCookie(ContainerRequestContext requestContext) {

        Map<String, Cookie> cookies = requestContext.getCookies();
        if (cookies != null) {
            return cookies.get("session");
        }
        return null;
    }

    private String toString(Serializable object) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    private Object fromString(String string) throws IOException, ClassNotFoundException {

        byte[] data = Base64.getDecoder().decode(string);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }
}
