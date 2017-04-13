package automation.bml.com.webviewautomation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutomatedWebview webview = new AutomatedWebview(getApplicationContext());

        RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.activity_main);
        main_layout.addView(webview);
        try{
            webview.loadUrl("https://www.bing.com");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

       // webview.getMNCMCC();
    }
}
