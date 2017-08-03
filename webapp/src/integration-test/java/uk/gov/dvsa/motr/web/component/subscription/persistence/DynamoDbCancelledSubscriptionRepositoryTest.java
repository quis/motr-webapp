package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.CancelledSubscriptionItem;
import uk.gov.dvsa.motr.web.component.subscription.model.CancelledSubscription;
import uk.gov.dvsa.motr.web.component.subscription.service.UnsubscribeService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.cancelledSubscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.client;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.region;

public class DynamoDbCancelledSubscriptionRepositoryTest {

    private static final String REASON_FOR_CANCELLATION_USER_CANCELLED = "User cancelled";

    CancelledSubscriptionRepository repository;
    DynamoDbFixture fixture;

    @Before
    public void setUp() {

        repository = new DynamoDbCancelledSubscriptionRepository(cancelledSubscriptionTableName(), region());
        fixture = new DynamoDbFixture(client());
    }

    @Test
    public void saveCancelledSubscriptionForMotVehicle_CorrectlySavesNonModelAttributesToDb() {

        CancelledSubscriptionItem cancelledSubscriptionItemForMotVehicle = new CancelledSubscriptionItem();

        MotIdentification motIdentification = new MotIdentification(cancelledSubscriptionItemForMotVehicle.getMotTestNumber(), null);

        CancelledSubscription cancelledSubscriptionForMotVehicle = new CancelledSubscription();
        cancelledSubscriptionForMotVehicle
                .setUnsubscribeId(cancelledSubscriptionItemForMotVehicle.getUnsubscribeId())
                .setEmail(cancelledSubscriptionItemForMotVehicle.getEmail())
                .setVrm(cancelledSubscriptionItemForMotVehicle.getVrm())
                .setMotIdentification(motIdentification)
                .setReasonForCancellation(REASON_FOR_CANCELLATION_USER_CANCELLED);

        repository.save(cancelledSubscriptionForMotVehicle);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("id = :id AND email = :email")
                .withValueMap(new ValueMap().withString(":id", cancelledSubscriptionForMotVehicle.getUnsubscribeId()).withString(":email",
                cancelledSubscriptionForMotVehicle.getEmail()));

        Item savedItem = new DynamoDB(client()).getTable(cancelledSubscriptionTableName()).query(spec).iterator().next();

        assertNotNull("cancelled_at cannot be null when saving db", savedItem.getString("cancelled_at"));

        assertEquals("Returned item does not match", cancelledSubscriptionForMotVehicle.getEmail(), savedItem.getString("email"));
    }

    @Test
    public void saveCancelledSubscriptionForDvlaVehicle_CorrectlySavesNonModelAttributesToDb() {

        CancelledSubscriptionItem cancelledSubscriptionItemForDvlaVehicle = new CancelledSubscriptionItem();
        cancelledSubscriptionItemForDvlaVehicle.setMotTestNumber(null);

        MotIdentification motIdentification = new MotIdentification(null, cancelledSubscriptionItemForDvlaVehicle.getDvlaId());

        CancelledSubscription cancelledSubscriptionForDvlaVehicle = new CancelledSubscription();
        cancelledSubscriptionForDvlaVehicle
                .setUnsubscribeId(cancelledSubscriptionItemForDvlaVehicle.getUnsubscribeId())
                .setEmail(cancelledSubscriptionItemForDvlaVehicle.getEmail())
                .setVrm(cancelledSubscriptionItemForDvlaVehicle.getVrm())
                .setMotIdentification(motIdentification)
                .setReasonForCancellation(REASON_FOR_CANCELLATION_USER_CANCELLED);

        repository.save(cancelledSubscriptionForDvlaVehicle);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("id = :id AND email = :email")
                .withValueMap(new ValueMap().withString(":id", cancelledSubscriptionForDvlaVehicle.getUnsubscribeId()).withString(":email",
                cancelledSubscriptionForDvlaVehicle.getEmail()));

        Item savedItem = new DynamoDB(client()).getTable(cancelledSubscriptionTableName()).query(spec).iterator().next();

        assertNotNull("cancelled_at cannot be null when saving db", savedItem.getString("cancelled_at"));

        assertEquals("Returned item does not match", cancelledSubscriptionForDvlaVehicle.getEmail(), savedItem.getString("email"));
    }
}
