package automation.bml.com.webviewautomation.RestAPI.DataModel;

import java.util.Map;

/**
 * Created by krzysztof on 4/12/17.
 */
public class TransactionResponse {

    private Settings settings;
    private Map<String, Object> actions;
    private Map<String, Object> additionalAttributes;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Map<String, Object> getActions() {
        return actions;
    }

    public void setActions(Map<String, Object> actions) {
        this.actions = actions;
    }


    public Map<String, Object> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Map<String, Object> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

}
