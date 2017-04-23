package automation.bml.com.webviewautomation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

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
        NetworkInfo info = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        boolean isWifiEnabled = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connManager.getAllNetworks();
            for (Network network : networks) {
                info = connManager.getNetworkInfo(network);
                if (info != null) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        isWifiEnabled = true;
                        break;
                    }
                }
            }
        } else {
            info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null)
                isWifiEnabled = true;
            
        }
       
        if(isWifiEnabled)
        {
            if(info.isConnected() && isMobileEnabled)
                webview.updateData("UNABLE TO OBTAIN A 3G CONNECTION");
        }
    }
}
