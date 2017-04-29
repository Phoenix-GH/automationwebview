package automation.bml.com.webviewautomation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    AutomatedWebview webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview =(AutomatedWebview)findViewById(R.id.webview);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        webview.startWorkflow();
    }
}
