package automation.bml.com.webviewautomation.RestAPI.DataModel;

import javax.annotation.Generated;

/**
 * Created by krzysztof on 4/12/17.
 */
@Generated("org.jsonschema2pojo")
public class TransactionResponse {
    private Settings settings;
    /**
     *
     * @return
     * The settings
     */
    public Settings getSettings()
    {
        return settings;
    }
    /**
     *
     * @param settings
     * The settings
     */
    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    private Actions actions;
    /**
     *
     * @return
     * The actions
     */
    public Actions getActions()
    {
        return actions;
    }
    /**
     *
     * @param actions
     * The actions
     */
    public void setActions(Actions actions)
    {
        this.actions = actions;
    }
}
