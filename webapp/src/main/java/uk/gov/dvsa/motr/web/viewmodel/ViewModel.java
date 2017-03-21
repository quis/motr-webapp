package uk.gov.dvsa.motr.web.viewmodel;

import java.util.HashMap;
import java.util.Map;

public class ViewModel {

    private String template;

    private Map<String, String> map;

    public ViewModel(String template) {
        this(template, new HashMap<>());
    }

    public ViewModel(String template, Map<String, String> contextMap) {

        this.template = template;
        this.map = contextMap;
    }

    public Map<String, String> getContextMap() {

        return map;
    }

    public String getTemplate() {

        return template;
    }
}
