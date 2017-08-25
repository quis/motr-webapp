package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.test.data.RandomDataUtil;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixtureTableItem;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SmsConfirmationItem implements DynamoDbFixtureTableItem {

    private String confirmationId = UUID.randomUUID().toString();

    private String vrm = RandomDataUtil.vrm();

    private String phoneNumber = RandomDataUtil.phoneNumber();

    private int attempts = RandomDataUtil.singleDigitBetweenZeroAndThree();

    private int resendAttempts = RandomDataUtil.singleDigitBetweenZeroAndThree();

    private LocalDateTime lastResendAttempt = RandomDataUtil.latestResendAttempt();

    private String code = RandomDataUtil.confirmationCode();

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public SmsConfirmationItem setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public SmsConfirmationItem setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    public SmsConfirmationItem setConfirmationId(String confirmationId) {
        this.confirmationId = confirmationId;
        return this;
    }

    public int getAttempts() {
        return attempts;
    }

    public SmsConfirmationItem setAttempts(int attempts) {
        this.attempts = attempts;
        return this;
    }

    public int getResendAttempts() {
        return resendAttempts;
    }

    public SmsConfirmationItem setResendAttempts(int resendAttempts) {
        this.resendAttempts = resendAttempts;
        return this;
    }

    public LocalDateTime getLastResendAttempt() {
        return lastResendAttempt;
    }

    public SmsConfirmationItem setLastResendAttempt(LocalDateTime lastResendAttempt) {
        this.lastResendAttempt = lastResendAttempt;
        return this;
    }

    public String getCode() {
        return code;
    }

    public SmsConfirmationItem setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public Item toItem() {

        Item item = new Item()
                .with("id", confirmationId)
                .with("latest_resend_attempt", lastResendAttempt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .with("vrm", vrm)
                .with("phone_number", phoneNumber)
                .with("code", code)
                .with("attempts", attempts)
                .with("resend_attempts", resendAttempts);

        return item;
    }
}