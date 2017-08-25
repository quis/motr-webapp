package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConfirmationCodeGeneratorTests {

    @Test
    public void pseudoRandomConfirmationCodeIsSixCharactersLong() {

        String id = ConfirmationCodeGenerator.generateCode();
        assertTrue(id.length() == 6);
    }
}
