package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import uk.gov.dvsa.motr.test.data.RandomDataUtil;

import java.util.UUID;

public class CancelledSubscriptionItem {

    private String unsubscribeId = UUID.randomUUID().toString();

    private String vrm = RandomDataUtil.vrm();

    private String email = RandomDataUtil.emailOrPhoneNumber();

    private String motTestNumber = RandomDataUtil.motTestNumber();

    private String dvlaId = RandomDataUtil.motTestNumber();

    public String getUnsubscribeId() {
        return unsubscribeId;
    }

    public String getVrm() {
        return vrm;
    }

    public String getEmail() {
        return email;
    }

    public String getMotTestNumber() {
        return motTestNumber;
    }

    public void setMotTestNumber(String motTestNumber) {
        this.motTestNumber = motTestNumber;
    }

    public String getDvlaId() {
        return dvlaId;
    }

    public void setDvlaId(String dvlaId) {
        this.dvlaId = dvlaId;
    }

    @Override
    public String toString() {
        return "CancelledSubscriptionItem{" +
                "unsubscribeId='" + unsubscribeId + '\'' +
                ", vrm='" + vrm + '\'' +
                ", email='" + email + '\'' +
                ", motTestNumber='" + motTestNumber + '\'' +
                ", dvlaId='" + dvlaId + '\'' +
                '}';
    }
}
