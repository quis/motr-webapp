package uk.gov.dvsa.motr.web.validator;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhoneNumberValidatorTest {

    private static final String PHONE_NUMBER_TOO_SHORT = "0780652671";
    private static final String PHONE_NUMBER_TOO_LONG = "078065267111";
    private static final String PHONE_NUMBER_LANDLINE = "02890435617";
    private static final String PHONE_NUMBER_VALID = "07809716253";
    private SubscriptionService subscriptionService;
    private PhoneNumberValidator validator;

    @Before
    public void setUp() {

        subscriptionService = mock(SubscriptionService.class);

        this.validator = new PhoneNumberValidator(subscriptionService);
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

        assertFalse(validator.isValid(PHONE_NUMBER_TOO_SHORT));
    }

    @Test
    public void tooLongIsInvalid() {

        assertFalse(validator.isValid(PHONE_NUMBER_TOO_LONG));
    }

    @Test
    public void landLineIsInvalid() {

        assertFalse(validator.isValid(PHONE_NUMBER_LANDLINE));
    }

    @Test
    public void validPhoneNumberIsValid() {

        when(subscriptionService.hasMaxTwoSubscriptionsForPhoneNumber(PHONE_NUMBER_VALID)).thenReturn(true);

        assertTrue(validator.isValid(PHONE_NUMBER_VALID));
    }

    @Test
    public void phoneNumberWithTwoSubscriptionsIsInvalid() {

        when(subscriptionService.hasMaxTwoSubscriptionsForPhoneNumber(PHONE_NUMBER_VALID)).thenReturn(false);

        assertFalse(validator.isValid(PHONE_NUMBER_VALID));
    }
}
