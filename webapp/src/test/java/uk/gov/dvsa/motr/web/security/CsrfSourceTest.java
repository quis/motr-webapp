package uk.gov.dvsa.motr.web.security;

import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CsrfSourceTest {

    private CsrfSource csrfSource = new CsrfSource(new MotrSession());

    @Test
    public void csrfTokenIsNotEmpty() {

        assertNotNull(csrfSource.getToken());
    }

    @Test
    public void csrfTokenRemainsTheSameForSessionLifetime() {

        assertEquals(csrfSource.getToken(), csrfSource.getToken());
    }
}
