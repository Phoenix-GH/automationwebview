package automation.bml.com.webviewautomation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by krzysztof on 4/21/17.
 */

public class WifiReceiver extends BroadcastReceiver {
    AutomatedWebview webview;
    public WifiReceiver(AutomatedWebview webview)
    {
        super();
        this.webview = webview;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(mWifi != null && mWifi.isConnected()) {
            webview.updateData("UNABLE TO OBTAIN A 3G CONNECTION");
        }
    }
}
