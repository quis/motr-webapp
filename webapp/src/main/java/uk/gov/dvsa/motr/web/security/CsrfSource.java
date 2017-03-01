package uk.gov.dvsa.motr.web.security;

import uk.gov.dvsa.motr.web.cookie.MotrSession;

import javax.inject.Inject;

import static java.util.UUID.randomUUID;

/**
 * Source for csrf tokens
 */
public class CsrfSource {

    private MotrSession session;

    @Inject
    public CsrfSource(MotrSession session) {
        this.session = session;
    }

    /**
     * Retrieves token from the session or generates a new one
     * @return csrf token
     */
    public String getToken() {

        if (!session.hasCsrfToken()) {
            session.setCsrfToken(randomUUID().toString());
        }
        return session.getCsrfToken();
    }
}
