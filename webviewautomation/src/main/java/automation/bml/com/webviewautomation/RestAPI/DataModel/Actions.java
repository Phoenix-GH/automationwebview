package automation.bml.com.webviewautomation.RestAPI.DataModel;

import java.util.Map;

/**
 * Created by krzysztof on 4/12/17.
 */

public class Actions {
    private Map<String, String> params;
    /**
     *
     * @return
     * The params
     */
    public Map<String, String> getParams()
    {
        return params;
    }
    /**
     *
     * @param params
     * The params
     */
    public void setParams(Map<String,String> params)
    {
        this.params = params;
    }
}
