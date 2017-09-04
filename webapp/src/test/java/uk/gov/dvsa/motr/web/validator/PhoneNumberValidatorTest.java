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

        when(subscriptionService.hasMaxTwoSubscriptionsForPhoneNumber(any())).thenReturn(true);

        assertTrue(validator.isValid("07809716253"));
    }
}
