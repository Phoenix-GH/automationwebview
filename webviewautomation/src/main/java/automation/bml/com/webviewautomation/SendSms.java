package com.example.vrs.smsapplicationdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by vrs on 5/2/16.
 */
public class SendSms extends AppCompatActivity implements View.OnClickListener {

    EditText txtNumber, txtMessage;
    Button btnSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmsg);

        txtNumber = (EditText) findViewById(R.id.txtNumber);
        txtMessage = (EditText) findViewById(R.id.txtMesssage);
        btnSend = (Button) findViewById(R.id.btnSMS);

        // Attached Click Listener
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSend) {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(txtNumber.getText().toString(), null,txtMessage.getText().toString(), null, null);
            Toast.makeText(this, "Message sent successfully", Toast.LENGTH_LONG)
                    .show();

        }
    }
}
