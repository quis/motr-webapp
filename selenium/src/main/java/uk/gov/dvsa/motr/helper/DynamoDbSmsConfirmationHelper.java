package uk.gov.dvsa.motr.helper;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.kms.model.NotFoundException;

import uk.gov.dvsa.motr.config.Configurator;

import java.util.HashMap;
import java.util.Iterator;

import static java.lang.String.format;

public class DynamoDbSmsConfirmationHelper {

    private String smsConfirmTable;
    private String pendingTable;
    private DynamoDB dynamoDb;
    private final AmazonDynamoDB client;

    public DynamoDbSmsConfirmationHelper() {

        pendingTable = Configurator.dynamoDbTablePendingName();
        smsConfirmTable = Configurator.dynamoDbTableSmsConfirmName();
        client = AmazonDynamoDBClientBuilder.standard().withRegion(Configurator.dynamoDbRegion()).build();
        dynamoDb = new DynamoDB(client);
    }

    public String findSmsConfirmCodeFromVrmAndMobileNumber(String vrm, String mobileNumber) {

        String confirmationId = findConfirmationIdByVrmAndMobileNumber(vrm, mobileNumber);

        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression("id = :id")
                .withValueMap(new ValueMap().withString(":id", confirmationId));

        Table table = dynamoDb.getTable(smsConfirmTable);

        ItemCollection<QueryOutcome> items = table.query(query);
        Iterator<Item> resultIterator = items.iterator();

        if (!resultIterator.hasNext()) {
            throw new NotFoundException(format("Item with vrm: %s and mobile number %s not found", vrm, mobileNumber));
        }

        Item item = resultIterator.next();

        return item.getString("code");

    }

    private String findConfirmationIdByVrmAndMobileNumber(String vrm, String mobileNumber) {

        HashMap<String, Object> valueMap = new HashMap<>();
        HashMap<String, String> nameMap = new HashMap<>();

        valueMap.put(":vrm", vrm);
        valueMap.put(":mobileNumber", mobileNumber);
        nameMap.put("#vrm", "vrm");
        nameMap.put("#email", "email");

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#vrm = :vrm AND #email = :mobileNumber")
                .withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<ScanOutcome> result = dynamoDb.getTable(pendingTable).scan(scanSpec);

        return result.iterator().next().get("id").toString();
    }

}
