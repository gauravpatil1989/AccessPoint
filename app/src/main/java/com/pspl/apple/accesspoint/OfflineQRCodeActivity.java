package com.pspl.apple.accesspoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class OfflineQRCodeActivity extends Activity {

    ImageView btn_qrcode;
    RelativeLayout back_layout;
    TextView check_connection,text_connected;
    LinearLayout ll_connected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_qrcode);
        text_connected = (TextView) findViewById(R.id.text_connected);
        btn_qrcode = (ImageView) findViewById(R.id.btn_qrcode);
        back_layout = (RelativeLayout) findViewById(R.id.back_layout);
        ll_connected = (LinearLayout) findViewById(R.id.ll_connected);

        btn_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfflineQRCodeActivity.this, QRScannerActivity.class);
                startActivity(intent);
                finish();
            }
        });


        check_connection = (TextView) findViewById(R.id.check_connection);
        check_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()){
                    startActivity(new Intent(OfflineQRCodeActivity.this,StaffLoginActivity.class));
                    finish();
                }else{
                    Toast.makeText(OfflineQRCodeActivity.this, "No Internet Connection..!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            if (!isNetworkAvailable()){
                                ll_connected.setBackgroundColor(Color.parseColor("#ffdc143c"));
                                text_connected.setText("You are not connected Online");
                                check_connection.setText("Check Connection");
                            }else{
                                ll_connected.setBackgroundColor(Color.parseColor("#ff006400"));
                                text_connected.setText("You are Connected");
                                check_connection.setText("Go Online");
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OfflineQRCodeActivity.this,MainActivity.class));
        finish();
    }
}
