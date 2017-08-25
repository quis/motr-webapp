package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.test.data.RandomDataUtil;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixtureTableItem;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SubscriptionItem implements DynamoDbFixtureTableItem {


    private String unsubscribeId = UUID.randomUUID().toString();

    private LocalDate motDueDate = RandomDataUtil.dueDate();

    private String vrm = RandomDataUtil.vrm();

    private String email = RandomDataUtil.emailOrPhoneNumber();

    private String motTestNumber = RandomDataUtil.motTestNumber();

    private String dvlaId = RandomDataUtil.motTestNumber();

    private Subscription.ContactType contactType = RandomDataUtil.emailOrMobileContactType();

    public String getUnsubscribeId() {
        return unsubscribeId;
    }

    public LocalDate getMotDueDate() {
        return motDueDate;
    }

    public String getMotTestNumber() {
        return this.motTestNumber;
    }

    public SubscriptionItem setMotTestNumber(String motTestNumber) {
        this.motTestNumber = motTestNumber;
        return this;
    }

    public SubscriptionItem setMotDueDate(LocalDate motDueDate) {
        this.motDueDate = motDueDate;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public SubscriptionItem setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public SubscriptionItem setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getDvlaId() {
        return dvlaId;
    }

    public SubscriptionItem setDvlaId(String dvlaId) {
        this.dvlaId = dvlaId;
        return this;
    }

    public Subscription.ContactType getContactType() {
        return this.contactType;
    }

    public SubscriptionItem setContactType(Subscription.ContactType contactType) {
        this.contactType = contactType;
        return this;
    }

    @Override
    public Item toItem() {

        Item item = new Item()
                .with("id", unsubscribeId)
                .with("mot_due_date", motDueDate.format(DateTimeFormatter.ISO_DATE))
                .with("vrm", vrm)
                .with("email", email)
                .with("contact_type", contactType.getValue());

        if (motTestNumber != null) {
            item.with("mot_test_number", motTestNumber);
        } else {
            item.with("dvla_id", dvlaId);
        }

        return item;
    }
}
