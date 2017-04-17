package automation.bml.com.webviewautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import automation.bml.com.webviewautomation.RestAPI.Constants;
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
    private final String sharedPreferenceName = "BML_WEBVIEW_AUTOMATION";
    Context context;
    private int mnc, mcc;
    private String cssSelector;

    public AutomatedWebview(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public AutomatedWebview(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.context = context;
        init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && canGoBack()) {
            goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    public void init()
    {
        //changeWifiStatus(true);
        setUUID(); // Setting the UUID on installation
        try{
            loadUrl("https://www.google.com");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        getSettings().setJavaScriptEnabled(true);

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url)
            {
                return false;
            }
            public void onPageFinished(WebView view, String url) {
                //Checking 3G/4G
                //click("lst-ib");
                waitSeconds(1);
                focus("lst-ib");
                enter("Sample text");
                takeScreenshot(generateFileName(url));
                //String connectionType = getConnectionType();
                if (Connectivity.isConnectedWifi(context))
                {
                    Log.d("Connection Status: ", "Wifi");
                    //changeWifiStatus(false);
                }

//                if(Connectivity.isConnectedMobile(context))
//                {
                    Log.d("Connection Status: ", "3g/4g");
                    //changeWifiStatus(false);
//                    if(Connectivity.isConnectedMobile(context)) //If connected to 3G/4G
//                    {
                        getMNCMCC();
                        if(mnc != 0 || mcc != 0) //If MNC and MCC are not empty
                        {
                            TransactionRequest request = new TransactionRequest();
                            // Setting the parameters for API call
                            request.setAction("start");
                            //request.setMccmnc(String.valueOf(mcc)+String.valueOf(mnc));
                            request.setMccmnc("20404");
                            request.setInstall_id(getUUID());
                            request.setApp_id("1");
                            request.setIp(getIPAddress());
                            request.setUseragent(getUserAgent());

                            //Calling the api
                            try {
                            OkHttpClient httpClient = new OkHttpClient.Builder().build();
                            Gson gson = new GsonBuilder()
                                    .setLenient()
                                    .create();
                            Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(RestAPI.BASE_URL).client(httpClient).build();

                            RestAPI service = retrofit.create(RestAPI.class);
                            Call<TransactionResponse> meResponse = service.loadData("1", getUUID(), getUserAgent(), getIPAddress(), "20404", "start");
                                meResponse.enqueue(new Callback<TransactionResponse>() {
                                    @Override
                                    public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                                        if (response.isSuccessful()) {
                                            TransactionResponse body = response.body();
//                                            Actions actions = body.getActions();
//                                            Map<String, String> params = actions.getParams();
                                        }
                                        else
                                        {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<TransactionResponse> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                            }
                    //}
//                    else
//                    {
//
//                    }
                //}
                super.onPageFinished(view,url);
            }
        });
    }
    // Automated actions
    public void waitSeconds(int seconds)
    {
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void focus(String selector)
    {
        String script = "document.getElementById('"+selector+"').focus();";
        cssSelector = selector;
        injectJS(script);
    }

    public void enter(String text)
    {
        String script = "(function() {document.getElementById('"+cssSelector+"').value= '"+text+"';}) ();";
        injectJS(script);
    }

    public void click(String selector)
    {
        String script = "(function() {document.getElementById('"+selector+"').click();})();";
        injectJS(script);
    }

    public void takeScreenshot(String url)
    {
        Picture picture = capturePicture();
        Bitmap b = Bitmap.createBitmap( picture.getWidth(),
                picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        picture.draw(c);
        FileOutputStream fos;
        try {
            if(createDirIfNotExists(Constants.DIRECTORY)) {
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + Constants.DIRECTORY + "/" + "profile.jpg");
                fos = new FileOutputStream(file);
                if (fos != null) {
                    b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void process(String action, String parameter)
    {
        if(action.equalsIgnoreCase("load"))
            loadUrl(parameter);

        else if(action.equalsIgnoreCase("wait")) {
            int seconds = 0;
            try {
                seconds = Integer.parseInt(parameter);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            waitSeconds(seconds);
        }
        else if(action.equalsIgnoreCase("focus")){
            focus(parameter);
        }
        else if(action.equalsIgnoreCase("enter")){
            enter(parameter);
        }
        else if(action.equalsIgnoreCase("click")){
            click(parameter);
        }
        else if(action.equalsIgnoreCase("screenshot")){
            takeScreenshot(parameter);
        }
    }

    // Processing functions
    private NetworkInfo getConnectionInfo()
    {
        NetworkInfo info = Connectivity.getNetworkInfo(getContext());
        return info;
    }

    private String getConnectionType()
    {
        return Connectivity.getNetworkInfo(getContext()).getTypeName();
    }

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

    public String getIPAddress()
    {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public void setUUID()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if(getUUID().isEmpty())
        {
            String newId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putString("uuid", newId);
            editor.commit();
        }
    }

    public String getUUID()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString("uuid","");
        return uuid;
    }

    // Miscellenous functions
    private void injectJS(String script) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(script);
            loadUrl("javascript:" +script);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("Creating directory: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }

    private String generateFileName(String url)
    {
        String name = "screenshot.jpg";
        URL u = null;
        try {
            u = new URL(url);
            name = u.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return name;
    }
}