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

import static uk.gov.dvsa.motr.web.system.SystemVariable.DB_TABLE_SUBSCRIPTION;
import static uk.gov.dvsa.motr.web.system.SystemVariable.REGION;

@Singleton
public class DynamoDbSubscriptionRepository implements SubscriptionRepository {

    private DynamoDB dynamoDb;
    private String tableName;

    @Inject
    public DynamoDbSubscriptionRepository(
            @SystemVariableParam(DB_TABLE_SUBSCRIPTION) String tableName,
            @SystemVariableParam(REGION) String region) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    @Override
    public Optional<Subscription> findById(String id) {
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
        Subscription subscription = mapItemToSubscription(item);

        return Optional.of(subscription);
    }

    @Override
    public Optional<Subscription> findByVrmAndEmail(String vrm, String email) {
        return findSubsctiptionByVrmAndEmail(vrm, email);
    }

    private Subscription mapItemToSubscription(Item item) {

        Subscription subscription = new Subscription(item.getString("id"));
        subscription.setVrm(item.getString("vrm"));
        subscription.setEmail(item.getString("email"));
        subscription.setMotDueDate(LocalDate.parse(item.getString("mot_due_date")));
        return subscription;
    }


    public Optional<Subscription> findSubsctiptionByVrmAndEmail(String vrm, String email) {

        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", vrm).withString(":email", email));

        Table table = dynamoDb.getTable(tableName);

        ItemCollection<QueryOutcome> items = table.query(query);
        Iterator<Item> resultIterator = items.iterator();

        if (!resultIterator.hasNext()) {
            return Optional.empty();
        }

        Item item = resultIterator.next();
        Subscription subscription = mapItemToSubscription(item);

        return Optional.of(subscription);
    }

    public void save(Subscription subscription) {

        Item item = new Item()
                .withString("id", subscription.getId())
                .withString("vrm", subscription.getVrm())
                .withString("email", subscription.getEmail())
                .withString("mot_due_date", subscription.getMotDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .withString("mot_due_date_md", subscription.getMotDueDate().format(DateTimeFormatter.ofPattern("MM-dd")))
                .withString("created_at", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));

        dynamoDb.getTable(tableName).putItem(item);
    }

    public void delete(Subscription subscription) {
        PrimaryKey key = new PrimaryKey("vrm", subscription.getVrm(), "email", subscription.getEmail());
        Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
        expressionAttributeValues.put(":id", subscription.getId());

        dynamoDb.getTable(tableName).deleteItem(
                key,
                "id = :id",
                null,
                expressionAttributeValues
        );
    }
}
