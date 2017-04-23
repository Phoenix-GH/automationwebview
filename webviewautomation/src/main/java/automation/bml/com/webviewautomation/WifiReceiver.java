package automation.bml.com.webviewautomation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by krzysztof on 4/21/17.
 */

public class WifiReceiver extends BroadcastReceiver {
    AutomatedWebview webview;
    boolean isMobileEnabled = false;
    public WifiReceiver(AutomatedWebview webview,boolean isMobileEnabled)
    {
        super();
        this.webview = webview;
        this.isMobileEnabled = isMobileEnabled;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(mWifi != null && mWifi.isConnected() && isMobileEnabled) {
            webview.updateData("UNABLE TO OBTAIN A 3G CONNECTION");
        }
    }
}
