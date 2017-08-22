package uk.gov.dvsa.motr.web.resource;

import java.util.Map;

class ReviewFlowUpdater {

    static void updateMapBasedOnReviewFlow(
            Map<String, Object> modelMap,
            boolean visitingFromContactEntryPage,
            boolean visitingFromReviewPage
    ) {
        if (visitingFromReviewPage) {
            modelMap.put("continue_button_text", "Save and return to review");
            modelMap.put("back_button_text", "Cancel and return");
            modelMap.put("back_url", "review");
        } else if (visitingFromContactEntryPage) {
            modelMap.put("continue_button_text", "Continue");
            modelMap.put("back_button_text", "Back");
            modelMap.put("back_url", "channel-selection");
        } else {
            modelMap.put("continue_button_text", "Continue");
            modelMap.put("back_button_text", "Back");
            modelMap.put("back_url", "vrm");
        }
    }
}
