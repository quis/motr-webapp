package uk.gov.dvsa.motr.web.validator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChannelSelectionValidatorTest {

    private ChannelSelectionValidator validator;

    @Before
    public void setUp() {

        validator = new ChannelSelectionValidator();
    }

    @Test
    public void emptyChannelSelectionIsInvalid() {

        assertFalse(validator.isValid(""));
    }

    @Test
    public void nullChannelSelectionIsInvalid() {

        assertFalse(validator.isValid(null));
    }

    @Test
    public void invalidChannelSelectionIsInvalid() {

        assertFalse(validator.isValid("post"));
    }

    @Test
    public void emailChannelSelectionIsValid() {

        assertTrue(validator.isValid("email"));
    }

    @Test
    public void textChannelSelectionIsValid() {

        assertTrue(validator.isValid("text"));
    }
}
