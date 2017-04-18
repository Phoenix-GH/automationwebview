package automation.bml.com.webviewautomation.RestAPI.DataModel;

/**
 * Created by krzysztof on 4/12/17.
 */

public class Action {
    private String action;
    private String parameter;
    public Action()
    {

    }
    public Action(String action, String parameter)
    {
        this.action = action;
        this.parameter = parameter;
    }

    public String getAction()
    {
        return action;
    }
    public void setAction(String action)
    {
        this.action = action;
    }

    public String getParameter()
    {
        return parameter;
    }
    public void setParameter(String parameter)
    {
        this.parameter = parameter;
    }
}
