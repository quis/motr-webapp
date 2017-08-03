package uk.gov.dvsa.motr.web.component.subscription.persistence;

import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SubscriptionTest {

    @Test
    public void subscriptionObjectBuiltCorrectly() {

        String testId = UUID.randomUUID().toString();
        LocalDate testDate = LocalDate.now();
        String motTestNumber = "123456";
        String dvlaId = "3456789";
        Subscription subscription = new Subscription();
        subscription.setUnsubscribeId(testId).setVrm("VCS1234").setEmail("my@email.com").setMotDueDate(testDate);
        subscription.setMotIdentification(new MotIdentification(motTestNumber, null));

        assertEquals(testId, subscription.getUnsubscribeId());
        assertEquals("VCS1234", subscription.getVrm());
        assertEquals("my@email.com", subscription.getEmail());
        assertEquals(testDate, subscription.getMotDueDate());
        assertTrue(subscription.getMotIdentification().getMotTestNumber().isPresent());
        assertFalse(subscription.getMotIdentification().getDvlaId().isPresent());
        assertEquals(motTestNumber, subscription.getMotIdentification().getMotTestNumber().get());

        subscription.setMotIdentification(new MotIdentification(null, dvlaId));
        assertTrue(subscription.getMotIdentification().getDvlaId().isPresent());
        assertFalse(subscription.getMotIdentification().getMotTestNumber().isPresent());
        assertEquals(dvlaId, subscription.getMotIdentification().getDvlaId().get());
    }
}
