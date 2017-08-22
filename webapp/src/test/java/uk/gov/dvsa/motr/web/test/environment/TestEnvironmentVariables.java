package uk.gov.dvsa.motr.web.test.environment;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.web.system.SystemVariable;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;
import static uk.gov.dvsa.motr.web.system.SystemVariable.DO_WARM_UP;
import static uk.gov.dvsa.motr.web.system.SystemVariable.FEATURE_TOGGLE_SMS;
import static uk.gov.dvsa.motr.web.system.SystemVariable.LOG_LEVEL;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_API_URI;
import static uk.gov.dvsa.motr.web.system.SystemVariable.MOT_TEST_REMINDER_INFO_TOKEN;
import static uk.gov.dvsa.motr.web.system.SystemVariable.RELEASE_VERSION;
import static uk.gov.dvsa.motr.web.system.SystemVariable.STATIC_ASSETS_HASH;
import static uk.gov.dvsa.motr.web.system.SystemVariable.STATIC_ASSETS_URL;
import static uk.gov.dvsa.motr.web.system.SystemVariable.WARM_UP_TIMEOUT_SEC;

public class TestEnvironmentVariables extends EnvironmentVariables {

    public TestEnvironmentVariables() {

        assetsUrl("http://url");
        assetsHash("981347823940237907edfdfdfdf");
        logLevel("INFO");
        baseUrl("http://url");
        motTestReminderInfoApiUri("some_uri");
        doWarmUp(false);
        warmUpTimeoutSec(10);
        motTestReminderInfoToken("test-token");
        featureToggleSms(true);
        releaseVersion("releaseVersion");
    }

    public TestEnvironmentVariables assetsUrl(String value) {
        return set(STATIC_ASSETS_URL, value);
    }

    public TestEnvironmentVariables assetsHash(String value) {
        return set(STATIC_ASSETS_HASH, value);
    }

    public TestEnvironmentVariables logLevel(String value) {
        return set(LOG_LEVEL, value);
    }

    public TestEnvironmentVariables baseUrl(String value) {
        return set(BASE_URL, value);
    }

    public TestEnvironmentVariables doWarmUp(boolean value) {
        return set(DO_WARM_UP, String.valueOf(value));
    }

    public TestEnvironmentVariables warmUpTimeoutSec(int value) {
        return set(WARM_UP_TIMEOUT_SEC, String.valueOf(value));
    }

    public TestEnvironmentVariables motTestReminderInfoApiUri(String value) {
        return set(MOT_TEST_REMINDER_INFO_API_URI, value);
    }

    public TestEnvironmentVariables motTestReminderInfoToken(String value) {
        return set(MOT_TEST_REMINDER_INFO_TOKEN, value);
    }

    public TestEnvironmentVariables featureToggleSms(boolean value) {
        return set(FEATURE_TOGGLE_SMS, String.valueOf(value));
    }

    public TestEnvironmentVariables releaseVersion(String value) {

        return set(RELEASE_VERSION, value);
    }

    private TestEnvironmentVariables set(SystemVariable var, String value) {
        set(var.getName(), value);
        return this;
    }
}
