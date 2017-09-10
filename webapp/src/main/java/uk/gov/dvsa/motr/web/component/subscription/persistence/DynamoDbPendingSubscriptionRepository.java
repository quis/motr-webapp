package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import static uk.gov.dvsa.motr.web.system.SystemVariable.DB_TABLE_PENDING_SUBSCRIPTION;
import static uk.gov.dvsa.motr.web.system.SystemVariable.REGION;

@Singleton
public class DynamoDbPendingSubscriptionRepository implements PendingSubscriptionRepository {

    private static final int MONTHS_TO_DELETION = 59;

    private DynamoDB dynamoDb;
    private String tableName;

    @Inject
    public DynamoDbPendingSubscriptionRepository(
            @SystemVariableParam(DB_TABLE_PENDING_SUBSCRIPTION) String tableName,
            @SystemVariableParam(REGION) String region) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();
        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    @Override
    public Optional<PendingSubscription> findByConfirmationId(String id) {

        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression("id = :id")
                .withValueMap(new ValueMap().withString(":id", id));

        Index table = dynamoDb.getTable(tableName).getIndex("id-gsi");

        ItemCollection<QueryOutcome> items = table.query(query);

        Iterator<Item> resultIterator = items.iterator();

        if (!resultIterator.hasNext()) {
            return Optional.empty();
        }

        Item item = resultIterator.next();

        return Optional.of(mapItemToPendingSubscription(item));
    }

    @Override
    public Optional<PendingSubscription> findByVrmAndContactDetails(String vrm, String contactDetails) {

        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :contact")
                .withValueMap(new ValueMap().withString(":vrm", vrm).withString(":contact", contactDetails));

        Table table = dynamoDb.getTable(tableName);

        ItemCollection<QueryOutcome> items = table.query(query);
        Iterator<Item> resultIterator = items.iterator();

        if (!resultIterator.hasNext()) {
            return Optional.empty();
        }

        Item item = resultIterator.next();

        return Optional.of(mapItemToPendingSubscription(item));
    }

    @Override
    public void save(PendingSubscription subscription) {

        Item item = new Item()
                .withString("id", subscription.getConfirmationId())
                .withString("vrm", subscription.getVrm())
                .withString("email", subscription.getEmail())
                .withString("mot_due_date", subscription.getMotDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .withString("mot_due_date_md", subscription.getMotDueDate().format(DateTimeFormatter.ofPattern("MM-dd")))
                .withString("created_at", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .withNumber("deletion_date", ZonedDateTime.now().plusMonths(MONTHS_TO_DELETION).toEpochSecond())
                .withString("contact_type", subscription.getContactType().getValue());

        subscription.getMotIdentification().getMotTestNumber()
                .ifPresent(motTestNumber -> item.withString("mot_test_number", motTestNumber));
        subscription.getMotIdentification().getDvlaId().ifPresent(dvlaId -> item.withString("dvla_id", dvlaId));

        dynamoDb.getTable(tableName).putItem(item);
    }

    @Override
    public void delete(PendingSubscription subscription) {

        PrimaryKey key = new PrimaryKey("vrm", subscription.getVrm(), "email", subscription.getEmail());
        Map<String, Object> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":id", subscription.getConfirmationId());

        dynamoDb.getTable(tableName).deleteItem(
                key,
                "id = :id",
                null,
                expressionAttributeValues
        );
    }

    private PendingSubscription mapItemToPendingSubscription(Item item) {

        PendingSubscription subscription = new PendingSubscription();
        subscription.setConfirmationId(item.getString("id"));
        subscription.setVrm(item.getString("vrm"));
        subscription.setEmail(item.getString("email"));
        subscription.setMotDueDate(LocalDate.parse(item.getString("mot_due_date")));
        subscription.setMotIdentification(new MotIdentification(item.getString("mot_test_number"), item.getString("dvla_id")));
        subscription.setContactType(Subscription.ContactType.valueOf(item.getString("contact_type")));
        return subscription;
    }
}
