package automation.bml.com.webviewautomation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

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

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if(info != null && info.isConnected()) {
            webview.updateData("UNABLE TO OBTAIN A 3G CONNECTION");
        }
    }
}
