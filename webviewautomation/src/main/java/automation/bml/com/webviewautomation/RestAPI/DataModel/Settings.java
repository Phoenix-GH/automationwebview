package automation.bml.com.webviewautomation.RestAPI.DataModel;

/**
 * Created by krzysztof on 4/12/17.
 */
public class Settings {
    private String intercept_msisdn;
    private String transaction_id;

    public String getInterceptMsisdn() {
        return intercept_msisdn;
    }

    public void setInterceptMsisdn(String interceptMsisdn) {
        this.intercept_msisdn = intercept_msisdn;
    }

    public String getTransactionId() {
        return transaction_id;
    }
    public void setTransactionId(String transaction_id) {
        this.transaction_id = transaction_id;
    }

}

