package automation.bml.com.webviewautomation;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import static android.content.Context.WIFI_SERVICE;

public class AutomatedWebview extends WebView
{
    Context context;
    private int mnc, mcc;
    private String userAgent = "Android", ipAddress;
    private NetworkInfo info;

    public AutomatedWebview(final Context context) {
        super(context);
        this.context = context;
        //getUserAgent();
        getConnectionInfo();
        ipAddress = getIPAddress();
        Log.d("IpAddress", ipAddress);
        getMNCMCC();
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new AutoJavaScriptInterface(), "MYOBJECT");

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                injectJS();
                //Toast.makeText(context,ipAddress.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public void init()
    {
    }
    private void getMNCMCC()
    {
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }
    }
    private void getUserAgent()
    {
        userAgent = this.getSettings().getUserAgentString();
    }
    private NetworkInfo getConnectionInfo()
    {
        NetworkInfo info = Connectivity.getNetworkInfo(getContext());
        return info;
    }

    private String getIPAddress()
    {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
    private void setUserAgent()
    {
        getSettings().setUserAgentString(userAgent);
    }
    private void injectJS() {
        try {
//
//            InputStream inputStream = context.getAssets().open("jscript.js");
//            byte[] buffer = new byte[inputStream.available()];
//            inputStream.read(buffer);
//            inputStream.close();
//            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
//            loadUrl("javascript:alert('abc');");

            StringBuilder sb = new StringBuilder();
            sb.append("alert('abc');");

            loadUrl("javascript:" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}