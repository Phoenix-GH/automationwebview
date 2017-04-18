package automation.bml.com.webviewautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import automation.bml.com.webviewautomation.RestAPI.Constants;
import automation.bml.com.webviewautomation.RestAPI.DataModel.Action;
import automation.bml.com.webviewautomation.RestAPI.DataModel.TransactionResponse;
import automation.bml.com.webviewautomation.RestAPI.RestAPI;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.WIFI_SERVICE;

public class AutomatedWebview extends WebView {
    private final String sharedPreferenceName = "BML_WEBVIEW_AUTOMATION";
    Context context;
    private int mnc, mcc;
    private String cssSelector;
    ArrayList<Action> actionList;

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

    public void init() {
        //changeWifiStatus(true);
        setUUID(); // Setting the UUID on installation
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        //Checking 3G/4G
        //String connectionType = getConnectionType();
        if (Connectivity.isConnectedWifi(context)) {
            Log.d("Connection Status: ", "Wifi");
            //changeWifiStatus(false);
        }


        //changeWifiStatus(false);
//        if (Connectivity.isConnectedMobile(context)) //If connected to 3G/4G
//        {
            getMNCMCC();
            if (mnc != 0 || mcc != 0) //If MNC and MCC are not empty
            {
                //Calling the api
                try {
                    OkHttpClient httpClient = new OkHttpClient.Builder().build();
                    Gson gson = new GsonBuilder()
                            .create();
                    Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(RestAPI.BASE_URL).client(httpClient).build();

                    RestAPI service = retrofit.create(RestAPI.class);
                    Call<TransactionResponse> meResponse = service.loadData("1", getUUID(), getUserAgent(), getIPAddress(), "20408", "start");
                    meResponse.enqueue(new Callback<TransactionResponse>() {
                        @Override
                        public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                            if (response.isSuccessful()) {
                                TransactionResponse body = response.body();
                                Map<String, String> actions = body.getActions();
                                actionList = new ArrayList<>();
                                for (Map.Entry<String, String> entry : actions.entrySet()) {
                                    String action = "";
                                    String parameter = "";
                                    if (entry.getValue().length() > 0) {
                                        String array[] = entry.getValue().split(" ", 2);
                                        action = array[0];
                                        if (array.length > 1)
                                            parameter = array[1];
                                    }

                                    actionList.add(new Action(action, parameter));
                                }
                                process();
                            } else {
                                Toast.makeText(context, "Loading data failed, please try again!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<TransactionResponse> call, Throwable t) {
                            Toast.makeText(context, "Network error, please try again!", Toast.LENGTH_LONG).show();
                            t.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//        } else {
//
//        }

        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
            }
        });
    }
    // Automated actions

    public void focus(String selector) {
        String script = "document.querySelector('" + selector + "').focus();";
        cssSelector = selector;
        injectJS(script);
    }

    public void enter(String text) {
        String script = "(function() {document.querySelector('" + cssSelector + "').value= '" + text + "';}) ();";
        injectJS(script);
    }

    public void click(String selector) {
        String script = "(function() {document.querySelector('" + selector + "').click();})();";
        injectJS(script);
    }

    public void takeScreenshot(String url) {
        Picture picture = capturePicture();
        Bitmap b = Bitmap.createBitmap(picture.getWidth(),
                picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        picture.draw(c);
        FileOutputStream fos;
        try {
            if (createDirIfNotExists(Constants.DIRECTORY)) {
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + Constants.DIRECTORY + "/" + "profile.jpg");
                fos = new FileOutputStream(file);
                if (fos != null) {
                    b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Toast.makeText(context, "Saved screenshot!", Toast.LENGTH_LONG).show();
                    fos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process() {
        int seconds = 0;
        Handler handler = new Handler();
        for (final Action item : actionList) {
            if (item.getAction().equalsIgnoreCase("load")) {
                loadUrl(item.getParameter());
            } else if (item.getAction().equalsIgnoreCase("wait")) {
                try {
                    seconds += Integer.parseInt(item.getParameter());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (item.getAction().equalsIgnoreCase("focus")) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        focus(item.getParameter());
                    }
                }, seconds * 1000);

            } else if (item.getAction().equalsIgnoreCase("enter")) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enter(item.getParameter());
                    }
                }, seconds * 1000);

            } else if (item.getAction().equalsIgnoreCase("click")) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        click(item.getParameter());
                    }
                }, seconds * 1000);
            } else if (item.getAction().equalsIgnoreCase("screenshot")) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        takeScreenshot("");
                    }
                }, seconds * 1000);

            }
        }
    }

    // Processing functions
    private NetworkInfo getConnectionInfo() {
        NetworkInfo info = Connectivity.getNetworkInfo(getContext());
        return info;
    }

    private String getConnectionType() {
        return Connectivity.getNetworkInfo(getContext()).getTypeName();
    }

    public void changeWifiStatus(boolean status) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(status);
    }

    private void getMNCMCC() {
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }
    }

    public String getUserAgent() {
        return this.getSettings().getUserAgentString();
    }

    public String getIPAddress() {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public void setUUID() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if (getUUID().isEmpty()) {
            String newId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("uuid", newId);
            editor.commit();
        }
    }

    public String getUUID() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString("uuid", "");
        return uuid;
    }

    // Miscellenous functions
    private void injectJS(String script) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(script);
            loadUrl("javascript:" + script);

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

    private String generateFileName(String url) {
        String name = "screenshot.jpg";
        URL u;
        try {
            u = new URL(url);
            name = u.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return name;
    }

}