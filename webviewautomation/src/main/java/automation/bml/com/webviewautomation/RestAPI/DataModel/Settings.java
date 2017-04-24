package automation.bml.com.webviewautomation.RestAPI.DataModel;

import java.util.Map;

/**
 * Created by krzysztof on 4/12/17.
 */
public class Settings {
    private String interceptMsisdn;
    private String transactionId;
    private Map<String, Object> additionalAttributes;

    public Settings()
    {

    }

    public String getInterceptMsisdn() {
        return interceptMsisdn;
    }

    public void setInterceptMsisdn(String interceptMsisdn) {
        this.interceptMsisdn = interceptMsisdn;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public Map<String, Object> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Map<String, Object> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }
}

