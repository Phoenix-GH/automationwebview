package automation.bml.com.webviewautomation.RestAPI.DataModel;

/**
 * Created by krzysztof on 4/12/17.
 */

public class TransactionRequest {
    private String action;
    public String getAction()
    {
        return action;
    }
    public void setAction(String action)
    {
        this.action = action;
    }

    private String app_id;
    public String getApp_id()
    {
        return app_id;
    }
    public void setApp_id(String app_id)
    {
        this.app_id = app_id;
    }

    private String install_id;
    public String getInstall_id()
    {
        return install_id;
    }
    public void setInstall_id(String install_id)
    {
        this.install_id = install_id;
    }

    private String useragent;
    public String getUseragent()
    {
        return useragent;
    }
    public void setUseragent(String useragent)
    {
        this.useragent = useragent;
    }

    private String ip;
    public String getIp()
    {
        return ip;
    }
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    private String mccmnc;
    public String getMccmnc()
    {
        return mccmnc;
    }
    public void setMccmnc(String mccmnc)
    {
        this.mccmnc = mccmnc;
    }
}
