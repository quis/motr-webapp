package uk.gov.dvsa.motr.web.validator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator validator;

    @Before
    public void setUp() {

        this.validator = new PhoneNumberValidator();
    }

    @Test
    public void emptyPhoneNumberIsInvalid() {

        assertFalse(validator.isValid(""));
    }

    @Test
    public void nullPhoneNumberIsInvalid() {

        assertFalse(validator.isValid(null));
    }

    @Test
    public void tooShortIsInvalid() {

        assertFalse(validator.isValid("0780652671"));
    }

    @Test
    public void tooLongIsInvalid() {

        assertFalse(validator.isValid("078065267111"));
    }

    @Test
    public void landLineIsInvalid() {

        assertFalse(validator.isValid("02890435617"));
    }

    @Test
    public void validPhoneNumberIsValid() {

        assertTrue(validator.isValid("07809716253"));
    }
}
