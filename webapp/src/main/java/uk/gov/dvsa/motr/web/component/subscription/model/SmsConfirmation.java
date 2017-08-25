package uk.gov.dvsa.motr.web.component.subscription.model;

import java.time.LocalDateTime;

public class SmsConfirmation {

    private String phoneNumber;

    private String code;

    private String confirmationId;

    private String vrm;

    private int attempts;

    private int resendAttempts;

    private LocalDateTime latestResendAttempt;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public SmsConfirmation setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getCode() {
        return code;
    }

    public SmsConfirmation setCode(String code) {
        this.code = code;
        return this;
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    public SmsConfirmation setConfirmationId(String id) {
        this.confirmationId = id;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public SmsConfirmation setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public int getAttempts() {
        return attempts;
    }

    public SmsConfirmation setAttempts(int attempts) {
        this.attempts = attempts;
        return this;
    }

    public LocalDateTime getLatestResendAttempt() {
        return latestResendAttempt;
    }

    public SmsConfirmation setLatestResendAttempt(LocalDateTime latestResendAttempt) {
        this.latestResendAttempt = latestResendAttempt;
        return this;
    }

    public int getResendAttempts() {
        return resendAttempts;
    }

    public SmsConfirmation setResendAttempts(int resendAttempts) {
        this.resendAttempts = resendAttempts;
        return this;
    }
}
