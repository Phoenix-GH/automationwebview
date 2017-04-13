package automation.bml.com.webviewautomation;

import android.content.Context;
import android.net.ConnectivityManager;
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

import automation.bml.com.webviewautomation.RestAPI.DataModel.TransactionRequest;
import automation.bml.com.webviewautomation.RestAPI.DataModel.TransactionResponse;
import automation.bml.com.webviewautomation.RestAPI.RestAPI;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        ipAddress = getIPAddress();
        Log.d("IpAddress", ipAddress);
        //getMNCMCC();
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new AutoJavaScriptInterface(), "MYOBJECT");

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                //Checking 3G/4G

                //String connectionType = getConnectionType();
                if (Connectivity.isConnectedWifi(context))
                {
                    Log.d("Connection Status: ", "Wifi");
                }
                else if(Connectivity.isConnectedMobile(context))
                {
                    Log.d("Connection Status: ", "3g/4g");
                    changeWifiStatus(false);
                    if(Connectivity.isConnectedMobile(context))
                    {
                        getMNCMCC();
                        if(mnc == 0 && mcc == 0) {
                            getUserAgent();

                        }

                    }
                    else
                    {

                    }
                }

                TransactionRequest request = new TransactionRequest();
                OkHttpClient httpClient = new OkHttpClient.Builder().build();
                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(RestAPI.BASE_URL).client(httpClient).build();
                RestAPI service = retrofit.create(RestAPI.class);
                Call<TransactionResponse> meResponse = service.loadData(request);
                meResponse.enqueue(new Callback<TransactionResponse>() {
                    @Override
                    public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                        if (response.isSuccessful()) {
                            TransactionResponse body = response.body();

                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });
    }
    public void init()
    {
    }

    // Automated actions
    public void wait(int seconds)
    {

    }
    public void focus(String selector)
    {

    }
    public void enter(String text)
    {

    }
    public void click(String selector)
    {

    }
    public void takeScreenshot()
    {

    }
    //

    public void changeWifiStatus(boolean status)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(status);

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
    public String getUserAgent()
    {
       return this.getSettings().getUserAgentString();
    }
    private NetworkInfo getConnectionInfo()
    {
        NetworkInfo info = Connectivity.getNetworkInfo(getContext());
        return info;
    }

    private String getConnectionType()
    {
        return Connectivity.getNetworkInfo(getContext()).getTypeName();
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