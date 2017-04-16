package automation.bml.com.webviewautomation;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

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
        setUUID(); // Setting the UUID on installation
        try{
            loadUrl("https://www.google.com");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new AutoJavaScriptInterface(), "MYOBJECT");
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= 19) {
            setWebContentsDebuggingEnabled(true);
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url)
            {
                return true;
            }
            public void onPageFinished(WebView view, String url) {
                //Checking 3G/4G
                focus("lst-ib");
                takeScreenshot();
                //enter("Sample text");
                //String connectionType = getConnectionType();
                if (Connectivity.isConnectedWifi(context))
                {
                    Log.d("Connection Status: ", "Wifi");
                }

                else if(Connectivity.isConnectedMobile(context))
                {
                    Log.d("Connection Status: ", "3g/4g");
                    //changeWifiStatus(false);
//                    if(Connectivity.isConnectedMobile(context)) //If connected to 3G/4G
//                    {
//                        getMNCMCC();
//                        if(mnc != 0 || mcc != 0) //If MNC and MCC are not empty
//                        {
//                            TransactionRequest request = new TransactionRequest();
//                            // Setting the parameters for API call
//                            request.setAction("start");
//                            request.setMccmnc(String.valuesOf(mcc)+String.valueOf(mnc));
//                            request.setInstall_id(getUUID());
//                            request.setApp_id(getUUID());
//                            request.setIp(getIPAddress());
//                            request.setUseragent(getUserAgent());
//
//                            //Calling the api
//                            try {
//                            OkHttpClient httpClient = new OkHttpClient.Builder().build();
//                            Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(RestAPI.BASE_URL).client(httpClient).build();
//                            RestAPI service = retrofit.create(RestAPI.class);
//
//                            Call<TransactionResponse> meResponse = service.loadData(request);
//
//                                meResponse.enqueue(new Callback<TransactionResponse>() {
//                                    @Override
//                                    public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
//                                        if (response.isSuccessful()) {
//                                            TransactionResponse body = response.body();
//                                            Actions actions = body.getActions();
//                                            Map<String, String> params = actions.getParams();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<TransactionResponse> call, Throwable t) {
//                                        t.printStackTrace();
//                                    }
//                                });
//                            }
//                            catch(Exception e)
//                            {
//                                e.printStackTrace();
//                            }
//                            }
                    }
                    else
                    {

                    }
                //}
                super.onPageFinished(view,url);
            }
        });
    }
    // Automated actions
    public void wait(int seconds)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
            }
        }, 1000 * seconds);
    }

    public void focus(String selector)
    {
        //String script = "$('"+selector+"').focus()";
//        String script = "$(function() {"+"" +
//                "        $('"+selector+"').focus();"+
//        "});";
        String script = "document.getElementById('"+selector+"').focus();";
        cssSelector = selector;
        injectJS(script);
    }

    public void enter(String text)
    {
        String script = "document.getElementById('"+cssSelector+"').innerHTML='"+text+"';";
        /*String script = "$(function() {"+"" +
                "        $('"+cssSelector+"').html('"+text+"');"+
                "});";*/
        injectJS(script);
    }

    public void click(String selector)
    {
//        String script = "$(function() {"+"" +
//                "        $('"+selector+"').trigger('click');"+
//                "});";
        String script = "document.getElementById('"+selector+"').click();";
        injectJS(script);
    }

    public void takeScreenshot()
    {
        Picture picture = capturePicture();
        Bitmap b = Bitmap.createBitmap( picture.getWidth(),
                picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        picture.draw(c);
        FileOutputStream fos;
        try {
            ContextWrapper cw = new ContextWrapper(context);
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File mypath=new File(directory,"profile.jpg");
            fos = new FileOutputStream(mypath);
            if ( fos != null )
            {
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
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
            wait(seconds);
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
            takeScreenshot();
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void injectJS(String script) {
        try {
            //loadData("<script type='javascript' src='https://code.jquery.com/jquery-3.2.1.min.js'></script>","text/html", "UTF-8");

            StringBuilder sb = new StringBuilder();
            sb.append(script);
            //wait(10);
//            loadUrl("javascript:(function() {" +
//                    "var parent = document.getElementsByTagName('head').item(0);" +
//                    "var script = document.createElement('script');" +
//                    "script.type = 'text/javascript';" +
//                    // Tell the browser to BASE64-decode the string into your script !!!
//                    "script.innerHTML = window.atob('" + sb.toString() + "');" +
//                    "parent.appendChild(script)" +
//                    "})()");

            loadUrl("javascript:" +script);
//            evaluateJavascript(script, new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    Log.d("JS execution", "Successfully completed");
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}