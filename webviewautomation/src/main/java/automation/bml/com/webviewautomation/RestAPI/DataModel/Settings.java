package automation.bml.com.webviewautomation.RestAPI.DataModel;

/**
 * Created by krzysztof on 4/12/17.
 */

public class Settings {
    private String intercept_msisdn;
    public String getIntercept_msisdn()
    {
        return intercept_msisdn;
    }
    public void setIntercept_msisdn(String intercept_msisdn)
    {
        this.intercept_msisdn = intercept_msisdn;
    }

    private String transaction_id;
    public String getTransaction_id()
    {
        return transaction_id;
    }
    public void setTransaction_id(String transaction_id)
    {
        this.transaction_id = transaction_id;
    }
}
