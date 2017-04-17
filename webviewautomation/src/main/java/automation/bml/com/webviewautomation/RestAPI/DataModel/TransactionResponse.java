package automation.bml.com.webviewautomation.RestAPI.DataModel;

/**
 * Created by krzysztof on 4/12/17.
 */
public class TransactionResponse {

    private Settings settings;
    private Actions actions;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

}
