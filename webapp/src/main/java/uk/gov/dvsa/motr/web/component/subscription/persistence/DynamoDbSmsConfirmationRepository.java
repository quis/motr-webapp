package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import uk.gov.dvsa.motr.web.component.subscription.model.SmsConfirmation;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import static uk.gov.dvsa.motr.web.system.SystemVariable.DB_TABLE_SMS_CONFIRMATION;
import static uk.gov.dvsa.motr.web.system.SystemVariable.REGION;

@Singleton
public class DynamoDbSmsConfirmationRepository implements SmsConfirmationRepository {

    private static final int MONTHS_TO_DELETION = 1;

    private DynamoDB dynamoDb;
    private String tableName;

    @Inject
    public DynamoDbSmsConfirmationRepository(
            @SystemVariableParam(DB_TABLE_SMS_CONFIRMATION) String tableName,
            @SystemVariableParam(REGION) String region) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();
        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    @Override
    public Optional<SmsConfirmation> findByConfirmationId(String id) {

        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression("id = :id")
                .withValueMap(new ValueMap().withString(":id", id));

        Table table = dynamoDb.getTable(tableName);

        ItemCollection<QueryOutcome> items = table.query(query);

        Iterator<Item> resultIterator = items.iterator();

        if (!resultIterator.hasNext()) {
            return Optional.empty();
        }

        Item item = resultIterator.next();

        return Optional.of(mapItemToSmsConfirmation(item));
    }

    @Override
    public void save(SmsConfirmation smsConfirmation) {

        Item item = new Item()
                .withString("id", smsConfirmation.getConfirmationId())
                .withString("phone_number", smsConfirmation.getPhoneNumber())
                .withString("vrm", smsConfirmation.getVrm())
                .withString("code", smsConfirmation.getCode())
                .withInt("attempts", smsConfirmation.getAttempts())
                .withInt("resend_attempts", smsConfirmation.getResendAttempts())
                .withString("latest_resend_attempt", ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .withNumber("deletion_date", ZonedDateTime.now().plusMonths(MONTHS_TO_DELETION).toEpochSecond());

        dynamoDb.getTable(tableName).putItem(item);
    }

    private SmsConfirmation mapItemToSmsConfirmation(Item item) {

        return new SmsConfirmation()
                .setPhoneNumber(item.getString("phone_number"))
                .setCode(item.getString("code"))
                .setConfirmationId(item.getString("id"))
                .setVrm(item.getString("vrm"))
                .setAttempts(item.getInt("attempts"))
                .setResendAttempts(item.getInt("resend_attempts"))
                .setLatestResendAttempt(LocalDateTime.parse(item.getString("latest_resend_attempt")));
    }
}