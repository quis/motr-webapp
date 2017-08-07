package uk.gov.dvsa.motr.web.analytics;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataLayerHelper {

    public static final String ERROR_KEY = "error";
    public static final String VRM_KEY = "vrm";
    public static final String UNSUBSCRIBE_FAILURE_KEY = "unsubscribe-failure";
    public static final String DLVA_ID_KEY = "dvla-id";
    public static final String MOT_TEST_NUMBER_KEY = "mot-test-number";

    private Map<String, String> attributes;

    public DataLayerHelper() {
        attributes = new HashMap<>();
    }

    public void putAttribute(String key, String value) {

        attributes.put(key, value);
    }

    public Map<String, String> formatAttributes() {

        if (attributes.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> map = new HashMap<>();
        map.put("dataLayer", JSONObject.valueToString(attributes));
        return map;
    }

    public void clear() {

        attributes.clear();
    }
}
