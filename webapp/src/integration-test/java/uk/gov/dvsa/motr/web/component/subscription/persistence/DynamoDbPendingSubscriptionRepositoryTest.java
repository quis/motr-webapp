package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.PendingSubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.PendingSubscriptionTable;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.client;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.pendingSubscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.region;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.waitUntilPresent;

import static java.time.format.DateTimeFormatter.ofPattern;

public class DynamoDbPendingSubscriptionRepositoryTest {

    PendingSubscriptionRepository repo;
    DynamoDbFixture fixture;

    @Before
    public void setUp() {
        repo = new DynamoDbPendingSubscriptionRepository(pendingSubscriptionTableName(), region());
        fixture = new DynamoDbFixture(client());
    }

    @Test
    public void getByIdReturnsSubscription_ForMotVehicle_IfExistsInDb() {

        PendingSubscriptionItem expectedSubscriptionForMotVehicle = new PendingSubscriptionItem();
        expectedSubscriptionForMotVehicle.setDvlaId(null);

        fixture.table(new PendingSubscriptionTable().item(expectedSubscriptionForMotVehicle)).run();

        PendingSubscription actualSubscription = waitUntilPresent(
                () -> repo.findByConfirmationId(expectedSubscriptionForMotVehicle.getConfirmationId()),
                true,
                5000
        ).get();

        assertEquals(expectedSubscriptionForMotVehicle.getEmail(), actualSubscription.getContact());
        assertEquals(expectedSubscriptionForMotVehicle.getVrm(), actualSubscription.getVrm());
        assertEquals(expectedSubscriptionForMotVehicle.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(expectedSubscriptionForMotVehicle.getMotTestNumber(),
                actualSubscription.getMotIdentification().getMotTestNumber().get());
    }

    @Test
    public void getByIdReturnsSubscription_ForDvlaVehicle_IfExistsInDb() {

        PendingSubscriptionItem expectedSubscriptionForDvlaVehicle = new PendingSubscriptionItem();
        expectedSubscriptionForDvlaVehicle.setMotTestNumber(null);

        fixture.table(new PendingSubscriptionTable().item(expectedSubscriptionForDvlaVehicle)).run();

        PendingSubscription actualSubscription = waitUntilPresent(
                () -> repo.findByConfirmationId(expectedSubscriptionForDvlaVehicle.getConfirmationId()),
                true,
                5000
        ).get();

        assertEquals(expectedSubscriptionForDvlaVehicle.getEmail(), actualSubscription.getContact());
        assertEquals(expectedSubscriptionForDvlaVehicle.getVrm(), actualSubscription.getVrm());
        assertEquals(expectedSubscriptionForDvlaVehicle.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(expectedSubscriptionForDvlaVehicle.getDvlaId(), actualSubscription.getMotIdentification().getDvlaId().get());
    }

    @Test
    public void saveSubscriptionForMotVehicleCorrectlySavesToDb() {

        PendingSubscriptionItem subscriptionItemForMotVehicle = new PendingSubscriptionItem();

        MotIdentification motIdentification = new MotIdentification(subscriptionItemForMotVehicle.getMotTestNumber(), null);

        PendingSubscription subscription = new PendingSubscription();
        subscription
                .setConfirmationId(subscriptionItemForMotVehicle.getConfirmationId())
                .setContact(subscriptionItemForMotVehicle.getEmail())
                .setVrm(subscriptionItemForMotVehicle.getVrm())
                .setMotDueDate(subscriptionItemForMotVehicle.getMotDueDate())
                .setMotIdentification(motIdentification)
                .setContactType(subscriptionItemForMotVehicle.getContactType());

        repo.save(subscription);

        PendingSubscription actualSubscription = waitUntilPresent(
                () -> repo.findByConfirmationId(subscription.getConfirmationId()),
                true,
                5000
        ).get();

        assertEquals(subscriptionItemForMotVehicle.getEmail(), actualSubscription.getContact());
        assertEquals(subscriptionItemForMotVehicle.getVrm(), actualSubscription.getVrm());
        assertEquals(subscriptionItemForMotVehicle.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(subscriptionItemForMotVehicle.getMotTestNumber(), actualSubscription.getMotIdentification().getMotTestNumber().get());
    }

    @Test
    public void saveSubscriptionForDvlaVehicleCorrectlySavesToDb() {

        PendingSubscriptionItem subscriptionItemForDvlaVehicle = new PendingSubscriptionItem();

        MotIdentification motIdentification = new MotIdentification(null, subscriptionItemForDvlaVehicle.getDvlaId());

        PendingSubscription subscription = new PendingSubscription();
        subscription
                .setConfirmationId(subscriptionItemForDvlaVehicle.getConfirmationId())
                .setContact(subscriptionItemForDvlaVehicle.getEmail())
                .setVrm(subscriptionItemForDvlaVehicle.getVrm())
                .setMotDueDate(subscriptionItemForDvlaVehicle.getMotDueDate())
                .setMotIdentification(motIdentification)
                .setContactType(subscriptionItemForDvlaVehicle.getContactType());

        repo.save(subscription);

        PendingSubscription actualSubscription = waitUntilPresent(
                () -> repo.findByConfirmationId(subscription.getConfirmationId()),
                true,
                5000
        ).get();

        assertEquals(subscriptionItemForDvlaVehicle.getEmail(), actualSubscription.getContact());
        assertEquals(subscriptionItemForDvlaVehicle.getVrm(), actualSubscription.getVrm());
        assertEquals(subscriptionItemForDvlaVehicle.getMotDueDate(), actualSubscription.getMotDueDate());
        assertEquals(subscriptionItemForDvlaVehicle.getDvlaId(), actualSubscription.getMotIdentification().getDvlaId().get());
    }


    @Test
    public void saveSubscriptionCorrectlySavesNonModelAttributesToDb() {

        PendingSubscriptionItem subscriptionItem = new PendingSubscriptionItem();

        MotIdentification motIdentification = new MotIdentification(subscriptionItem.getMotTestNumber(), null);

        PendingSubscription subscription = new PendingSubscription();
        subscription
                .setConfirmationId(subscriptionItem.getConfirmationId())
                .setContact(subscriptionItem.getEmail())
                .setVrm(subscriptionItem.getVrm())
                .setMotDueDate(subscriptionItem.getMotDueDate())
                .setMotIdentification(motIdentification)
                .setContactType(subscriptionItem.getContactType());

        repo.save(subscription);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", subscription.getVrm()).withString(":email", subscription.getContact()));

        Item savedItem = new DynamoDB(client()).getTable(pendingSubscriptionTableName()).query(spec).iterator().next();

        assertNotNull("created_at cannot be null when saving db", savedItem.getString("created_at"));

        String dueDateMd = savedItem.getString("mot_due_date_md");
        assertEquals("due date md fragment is incorrect", dueDateMd, subscription.getMotDueDate().format(ofPattern("MM-dd")));
    }

    @Test
    public void getByIdReturnsEmptyIfSubscriptionDoesNotExist() {

        assertFalse(repo.findByConfirmationId("ID_THAT_DOES_NOT_EXIST").isPresent());
    }

    @Test
    public void subscriptionIsDeleted() {

        PendingSubscriptionItem sub = new PendingSubscriptionItem();
        fixture.table(new PendingSubscriptionTable().item(sub)).run();

        MotIdentification motIdentification = new MotIdentification(sub.getMotTestNumber(), null);

        PendingSubscription subscription = new PendingSubscription();
        subscription
                .setConfirmationId(sub.getConfirmationId())
                .setContact(sub.getEmail())
                .setVrm(sub.getVrm())
                .setMotIdentification(motIdentification)
                .setContactType(sub.getContactType());

        repo.delete(subscription);

        waitUntilPresent(() -> repo.findByConfirmationId(sub.getConfirmationId()), false, 5000);
    }
}
