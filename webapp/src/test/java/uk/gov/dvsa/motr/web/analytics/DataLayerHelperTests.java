package uk.gov.dvsa.motr.web.analytics;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataLayerHelperTests {

    private DataLayerHelper dataLayerHelper;

    @Before
    public void setUp() {
        dataLayerHelper = new DataLayerHelper();
    }

    @Test
    public void dataLayerAttributesFormattedInCorrectFormat() {

        dataLayerHelper.putAttribute("second-message", "test");
        dataLayerHelper.putAttribute("error", "Something went wrong");

        Map<String, String> map = dataLayerHelper.formatAttributes();

        assertTrue(map.containsKey("dataLayer"));
        assertEquals("{\"second-message\":\"test\",\"error\":\"Something went wrong\"}", map.get("dataLayer"));
    }

    @Test
    public void dataLayerWithNoAttributesReturnsEmptyMap() {

        Map<String, String> map = dataLayerHelper.formatAttributes();
        assertTrue(map.isEmpty());
    }

    @Test
    public void dataLayerThatHasBeenClearedReturnsEmptyMap() {

        dataLayerHelper.putAttribute("second-message", "test");
        dataLayerHelper.clear();

        Map<String, String> map = dataLayerHelper.formatAttributes();
        assertTrue(map.isEmpty());
    }
}
