package automation.bml.com.webviewautomation;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    String packageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        packageName = getPackageName();
    }

//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//
//            if (!Telephony.Sms.getDefaultSmsPackage(this).equals(packageName)) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
//                        .setCancelable(false)
//                        .setTitle("Alert!")
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @TargetApi(19)
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
//                                startActivity(intent);
//                            }
//                        });
//                builder.show();
//            }
//        }
//    }

}
