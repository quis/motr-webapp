package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubscriptionsValidationServiceTest {

    private static String PHONE_NUMBER = "07801876516";

    private Subscription subscription;
    private List<Subscription> subscriptionList;
    private SubscriptionRepository repository;
    private SubscriptionsValidationService service;

    @Before
    public void setUp() {

        subscription = mock(Subscription.class);
        repository = mock(SubscriptionRepository.class);
        service = new SubscriptionsValidationService(repository);
    }

    @Test
    public void testReturnsFalseWhenTwoSubscriptions() {

        subscriptionList = Arrays.asList(subscription, subscription);
        when(repository.findByEmail(PHONE_NUMBER)).thenReturn(2);

        assertFalse(service.hasMaxTwoSubscriptionsForPhoneNumber(PHONE_NUMBER));
    }

    @Test
    public void testReturnsTrueWhenLessThanTwoSubscriptions() {

        subscriptionList = Arrays.asList(subscription);
        when(repository.findByEmail(PHONE_NUMBER)).thenReturn(1);

        assertTrue(service.hasMaxTwoSubscriptionsForPhoneNumber(PHONE_NUMBER));
    }
}
