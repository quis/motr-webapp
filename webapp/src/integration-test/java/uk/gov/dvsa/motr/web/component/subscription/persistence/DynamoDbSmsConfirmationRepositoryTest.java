package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SmsConfirmationItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SmsConfirmationTable;
import uk.gov.dvsa.motr.web.component.subscription.model.SmsConfirmation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.client;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.region;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.smsConfirmationTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.waitUntilPresent;

public class DynamoDbSmsConfirmationRepositoryTest {

    SmsConfirmationRepository repo;
    DynamoDbFixture fixture;

    @Before
    public void setUp() {
        repo = new DynamoDbSmsConfirmationRepository(smsConfirmationTableName(), region());
        fixture = new DynamoDbFixture(client());
    }

    @Test
    public void getByIdReturnsSmsConfirmation_IfExistsInDb() {

        SmsConfirmationItem expectedSmsConfirmation = new SmsConfirmationItem();

        fixture.table(new SmsConfirmationTable().item(expectedSmsConfirmation)).run();

        SmsConfirmation actualSmsConfirmation = waitUntilPresent(
                () -> repo.findByConfirmationId(expectedSmsConfirmation.getConfirmationId()),
                true,
                5000
        ).get();

        assertEquals(expectedSmsConfirmation.getPhoneNumber(), actualSmsConfirmation.getPhoneNumber());
        assertEquals(expectedSmsConfirmation.getAttempts(), actualSmsConfirmation.getAttempts());
        assertEquals(expectedSmsConfirmation.getCode(), actualSmsConfirmation.getCode());
        assertEquals(expectedSmsConfirmation.getLastResendAttempt(), actualSmsConfirmation.getLatestResendAttempt());
        assertEquals(expectedSmsConfirmation.getVrm(), actualSmsConfirmation.getVrm());
    }

    @Test
    public void saveSubscriptionForMotVehicleCorrectlySavesToDb() {

        SmsConfirmationItem expectedSmsConfirmationItem = new SmsConfirmationItem();

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setConfirmationId(expectedSmsConfirmationItem.getConfirmationId())
                .setCode(expectedSmsConfirmationItem.getCode())
                .setVrm(expectedSmsConfirmationItem.getVrm())
                .setAttempts(expectedSmsConfirmationItem.getAttempts())
                .setPhoneNumber(expectedSmsConfirmationItem.getPhoneNumber())
                .setResendAttempts(expectedSmsConfirmationItem.getResendAttempts());

        repo.save(smsConfirmation);

        SmsConfirmation actualSmsConfirmation = waitUntilPresent(
                () -> repo.findByConfirmationId(smsConfirmation.getConfirmationId()),
                true,
                5000
        ).get();

        assertEquals(expectedSmsConfirmationItem.getPhoneNumber(), actualSmsConfirmation.getPhoneNumber());
        assertEquals(expectedSmsConfirmationItem.getAttempts(), actualSmsConfirmation.getAttempts());
        assertEquals(expectedSmsConfirmationItem.getCode(), actualSmsConfirmation.getCode());
        assertEquals(expectedSmsConfirmationItem.getVrm(), actualSmsConfirmation.getVrm());
    }

    @Test
    public void saveSmsConfirmationCorrectlySavesNonModelAttributesToDb() {

        SmsConfirmationItem expectedSmsConfirmationItem = new SmsConfirmationItem();

        SmsConfirmation smsConfirmation = new SmsConfirmation()
                .setConfirmationId(expectedSmsConfirmationItem.getConfirmationId())
                .setCode(expectedSmsConfirmationItem.getCode())
                .setVrm(expectedSmsConfirmationItem.getVrm())
                .setAttempts(expectedSmsConfirmationItem.getAttempts())
                .setPhoneNumber(expectedSmsConfirmationItem.getPhoneNumber())
                .setResendAttempts(expectedSmsConfirmationItem.getResendAttempts());

        repo.save(smsConfirmation);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("id = :id AND phone_number = :phoneNumber")
                .withValueMap(
                new ValueMap()
                .withString(":id", smsConfirmation.getConfirmationId())
                .withString(":phoneNumber", smsConfirmation.getPhoneNumber()));

        Item savedItem = new DynamoDB(client()).getTable(smsConfirmationTableName()).query(spec).iterator().next();

        assertNotNull("created_at cannot be null when saving db", savedItem.getString("deletion_date"));
    }

    @Test
    public void getByIdReturnsEmptyIfSubscriptionDoesNotExist() {

        assertFalse(repo.findByConfirmationId("ID_THAT_DOES_NOT_EXIST").isPresent());
    }

}
