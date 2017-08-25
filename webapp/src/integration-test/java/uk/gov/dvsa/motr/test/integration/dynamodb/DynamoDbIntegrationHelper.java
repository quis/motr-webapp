package uk.gov.dvsa.motr.test.integration.dynamodb;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.util.Optional.empty;

public class DynamoDbIntegrationHelper {

    public static String subscriptionTableName() {

        return lookupProperty("test.dynamoDB.integration.table.subscription");
    }

    public static String pendingSubscriptionTableName() {

        return lookupProperty("test.dynamoDB.integration.table.pending_subscription");
    }

    public static String cancelledSubscriptionTableName() {

        return lookupProperty("test.dynamoDB.integration.table.cancelled_subscription");
    }

    public static String smsConfirmationTableName() {

        return lookupProperty("test.dynamoDB.integration.table.sms_confirmation");
    }

    /**
     * Returns region for DynamoDB client
     *
     * @return amazon region
     */
    public static String region() {

        return lookupProperty("test.dynamoDB.integration.region");
    }

    private static String lookupProperty(String property) {

        return Optional.ofNullable(System.getProperty(property)).orElseThrow(
                () -> new RuntimeException("Property: " + property + " is not defined!")
        );
    }

    public static AmazonDynamoDB client() {
        return AmazonDynamoDBClientBuilder.standard().withRegion(region())
                .withCredentials(new DefaultAWSCredentialsProviderChain()).build();
    }

    /**
     * Waits till a supplier that returns Optional returns it with a certain state
     *
     * @param optionalSupplier supplier that provides the result
     * @param isPresent        asserted state of Optional
     * @param timeout          maximum time that the asserted state will be waited for
     */
    public static <T> Optional<T> waitUntilPresent(Supplier<Optional<T>> optionalSupplier, boolean isPresent, long timeout) {

        long current = currentTimeMillis();
        boolean conditionSatisfied = false;
        Optional<T> optional = empty();

        while (!conditionSatisfied || (currentTimeMillis() - current) < timeout) {
            try {
                optional = optionalSupplier.get();
                conditionSatisfied = optional.isPresent() == isPresent;
                if (conditionSatisfied) {
                    break;
                }
                sleep(100);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        assertTrue(format("Assert timed out after %s ms", timeout), conditionSatisfied);

        return optional;
    }
}
