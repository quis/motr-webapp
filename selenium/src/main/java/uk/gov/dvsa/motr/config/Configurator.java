package uk.gov.dvsa.motr.config;

import org.openqa.selenium.Platform;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

public abstract class Configurator {

    private static final String SELENIUM_DRIVER_PROPERTIES = "SELENIUM_DRIVER_PROPERTIES";
    private static final String SELENIUM_ENV_PROPERTIES = "SELENIUM_ENV_PROPERTIES";
    private static final String DEFAULT_SELENIUM_DRIVER_PROPERTIES_FILE_PATH =
            "/selenium/driver/default.properties";

    private static final String DEFAULT_SELENIUM_ENV_PROPERTIES_FILE_PATH =
            "/selenium/environment/default.properties";

    private static Properties props;

    private static final int defaultDriverTimeout = 20;

    public static SimpleDateFormat screenshotDateFormat =
            new SimpleDateFormat("yyyyMMdd-HHmmss");

    static {
        props = new Properties();
        loadEnvironmentPropertiesFromFile();
        loadBrowserPropertiesFromFile();
        overrideWithSystemProperties();
        props.forEach((key, val) -> System.out.println("Key: " + key + ", Val: " + val));
    }

    private static void loadEnvironmentPropertiesFromFile() {
        loadPropertiesFromFile(DEFAULT_SELENIUM_ENV_PROPERTIES_FILE_PATH, SELENIUM_ENV_PROPERTIES);
    }

    private static void loadBrowserPropertiesFromFile() {
        loadPropertiesFromFile(DEFAULT_SELENIUM_DRIVER_PROPERTIES_FILE_PATH, SELENIUM_DRIVER_PROPERTIES);
    }

    private static void overrideWithSystemProperties() {
        System.getProperties().forEach((key, val) -> {
            boolean valIsNotEmpty = val != null && !"".equals(((String) val).trim());
            if (valIsNotEmpty && props.containsKey(key)) {
                props.put(key, val);
            }
        });
    }

    public enum SeleniumGrid {
        NONE,
        SELENIUM,
        BROWSERSTACK
    }


    /**
     * Load properties from the properties file specified unless the envVariableOverride parameter is specified, in
     * which case load the properties from the file specified in the environment variable.
     *
     * @param defaultPropertiesFilePath Properties file to be loaded
     * @param envVariableOverride       Specifies the environment variable name that contains a property file to be loaded in preference
     * @param ignoreNotFound            skips this resource if not found
     */
    private static void loadPropertiesFromFile(String defaultPropertiesFilePath,
                                               String envVariableOverride, boolean ignoreNotFound) {
        String customPropertiesFilePath = System.getenv(envVariableOverride);
        boolean useCustomProperties =
                customPropertiesFilePath != null && !customPropertiesFilePath.trim().isEmpty();

        try {
            InputStream propsStream = Configurator.class.getResourceAsStream(defaultPropertiesFilePath);
            props.load(propsStream);
            propsStream.close();
            if (useCustomProperties) {
                InputStream overridePropsStream = new FileInputStream(customPropertiesFilePath);
                props.load(overridePropsStream);
                overridePropsStream.close();
            }
        } catch (Exception ex) {
            if (!ignoreNotFound) {
                ex.printStackTrace();
                throw new RuntimeException(
                        "Problem loading test properties file [" + ex.getMessage() + "]. Is " +
                                (useCustomProperties ?
                                        (customPropertiesFilePath + " a valid file?") :
                                        (defaultPropertiesFilePath + " on the classpath?")), ex);

            }
        }
    }

    private static void loadPropertiesFromFile(String defaultPropertiesFilePath,
                                               String envVariableOverride) {
        loadPropertiesFromFile(defaultPropertiesFilePath, envVariableOverride, false);
    }

    /**
     * Return a property
     *
     * @param key The name of the property to be returned
     * @return Value of property or null if the key does not exist
     */
    protected static String getProp(String key) {
        return getProp(key, null);
    }

    /**
     * Return a property, using a default value if it is not set
     *
     * @param key          The name of the property to be returned
     * @param defaultValue The default value to return if the property is not set
     * @return Value of property or the default if the key does not exist
     */
    protected static String getProp(String key, String defaultValue) {
        String s = props.getProperty(key, defaultValue);
        return (s != null) ? s.trim() : null;
    }

    public static String baseUrl() {
        return getProp("test.baseUrl");
    }

    public static String dynamoDbRegion() {
        return getProp("test.dynamoDb.region");
    }

    public static String dynamoDbTableName() {
        return getProp("test.dynamoDb.table.name");
    }

    public static String dynamoDbTablePendingName() {
        return getProp("test.dynamoDb.pendingTable.name");
    }

    public static String dynamoDbTableSmsConfirmName() {
        return getProp("test.dynamoDb.smsConfirm.name");
    }

    public String getChromeDriverPath() {
        return getProp("test.chromeDriverPath");
    }

    public SeleniumGrid getGridStatus() {
        switch (getProp("test.gridEnabled")) {
            case "selenium":
                return SeleniumGrid.SELENIUM;
            case "browserstack":
                return SeleniumGrid.BROWSERSTACK;
            default:
                return SeleniumGrid.NONE;
        }
    }

    public String getGridUrl() {
        return getProp("test.gridUrl");
    }

    public int getDefaultDriverTimeout() {
        return defaultDriverTimeout;
    }

    public static boolean isErrorScreenshotEnabled() {
        return "yes".equalsIgnoreCase(getProp("test.screenshots.error.enabled"));
    }

    public static String getErrorScreenshotPath() {
        String errorFolder = System.getProperty("test.screenshots.error.folder");
        return errorFolder != null ?
                errorFolder :
                getProp("test.screenshots.error.folder", "/tmp/selenium-screenshots");
    }

    public boolean getJavascriptStatus() {
        return "yes".equalsIgnoreCase(getProp("test.javascript.enabled"));
    }

    public static String getBuildNumber() {
        String buildNumber = System.getenv("BUILD_NUMBER");

        return buildNumber != null ? buildNumber : "";
    }

    /**
     * Get the desired Platform from properties file to use in Grid
     *
     * @return Platform - the matching value from the Platform class
     */
    public Platform getPlatform() {
        switch (getProp("test.platform")) {
            case "windows":
                return Platform.WINDOWS;
            case "android":
                return Platform.ANDROID;
            case "linux":
                return Platform.LINUX;
            case "mac":
                return Platform.MAC;
            default:
                return null;
        }
    }

    public String getOs() {
        return getProp("test.os");
    }

    public String getOsVersion() {
        return getProp("test.osVersion");
    }

    public Browser getBrowser() {
        switch (String.valueOf(getProp("test.browserName")).toLowerCase()) {
            case "firefox":
                return Browser.FIREFOX;
            case "chrome":
                return Browser.CHROME;
            case "safari":
                return Browser.SAFARI;
            case "ie":
                return Browser.IE;
            case "ipad":
                return Browser.IPAD;
            case "iphone":
                return Browser.IPHONE;
            case "android":
                return Browser.ANDROID;
            default:
                return null;
        }
    }

    public String getBrowserVersion() {
        return getProp("test.browserVersion");
    }

    public String getResolution() {
        return getProp("test.resolution");
    }

    public String getDevice() {
        return getProp("test.device");
    }

    public String getDeviceOrientation() {
        return getProp("test.deviceOrientation");
    }
}
