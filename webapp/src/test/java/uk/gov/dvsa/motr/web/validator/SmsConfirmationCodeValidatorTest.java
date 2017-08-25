package uk.gov.dvsa.motr.web.validator;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class SmsConfirmationCodeValidatorTest {

    private SmsConfirmationCodeValidator validator;

    @DataProvider
    public static Object[][] invalidCodes() {
        return new Object[][]{
                {"0"},
                {"12"},
                {"123"},
                {"1234"},
                {"12345"},
                {"1234567"},
                {"a"},
                {"abcdef"},
                {"ABCDEF"},
                {"abc123"},
                {"!@.z\";"}
        };
    }

    @DataProvider
    public static Object[][] validCodes() {
        return new Object[][]{
                {"000000"},
                {"123456"},
                {"999999"},
                {RandomStringUtils.randomNumeric(6)}
        };
    }

    @Before
    public void setUp() {

        this.validator = new SmsConfirmationCodeValidator();
    }

    @Test
    public void emptyConfirmationCodeIsInvalid() {

        assertFalse(validator.isValid(""));
        assertSame(SmsConfirmationCodeValidator.EMPTY_CONFIRMATION_CODE_MESSAGE, validator.getMessage());
        assertSame(SmsConfirmationCodeValidator.EMPTY_CONFIRMATION_CODE_MESSAGE_AT_FIELD, validator.getMessageAtField());
    }

    @Test
    public void nullConfirmationCodeIsInvalid() {

        assertFalse(validator.isValid(null));
        assertSame(SmsConfirmationCodeValidator.EMPTY_CONFIRMATION_CODE_MESSAGE, validator.getMessage());
        assertSame(SmsConfirmationCodeValidator.EMPTY_CONFIRMATION_CODE_MESSAGE_AT_FIELD, validator.getMessageAtField());
    }

    @UseDataProvider("invalidCodes")
    @Test
    public void invalidConfirmationCodeFormatIsInvalid(String code) {

        assertFalse(validator.isValid(code));
        assertSame(SmsConfirmationCodeValidator.INVALID_CONFIRMATION_CODE_MESSAGE, validator.getMessage());
        assertSame(SmsConfirmationCodeValidator.INVALID_CONFIRMATION_CODE_MESSAGE_AT_FIELD, validator.getMessageAtField());
    }

    @UseDataProvider("validCodes")
    @Test
    public void validConfirmationCodeIsValid(String code) {

        assertTrue(validator.isValid(code));
        assertNull(validator.getMessage());
        assertNull(validator.getMessageAtField());
    }
}
