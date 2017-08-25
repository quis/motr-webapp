package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixtureTable;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.smsConfirmationTableName;

public class SmsConfirmationTable extends DynamoDbFixtureTable<SmsConfirmationItem>  {

    public SmsConfirmationTable() {
        super(smsConfirmationTableName());
    }
}
