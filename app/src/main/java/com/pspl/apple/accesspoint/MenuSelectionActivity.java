package com.pspl.apple.accesspoint;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MenuSelectionActivity extends Activity {

    Button exhibitor_btn;
    Button visitorBtn;
    RelativeLayout logout_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_selection);

        exhibitor_btn = (Button) findViewById(R.id.exhibitor_btn);
        visitorBtn = (Button) findViewById(R.id.visitor_btn);
        logout_layout = (RelativeLayout) findViewById(R.id.logout_layout);
        logout_layout.setVisibility(View.VISIBLE);
        visitorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()){
                    Intent intent = new Intent(getBaseContext(), StaffLoginActivity.class);
                    String choice = "V";
                    SharedPref.putSharedPreferenceForString(MenuSelectionActivity.this, SharedPref.CHOICE, choice);
                    startActivity(intent);
                    finish();
                }else{
                    String choice = "V";
                    SharedPref.putSharedPreferenceForString(MenuSelectionActivity.this, SharedPref.CHOICE, choice);
                    startActivity(new Intent(MenuSelectionActivity.this,OfflineQRCodeActivity.class));
                    finish();
                }

            }
        });

        exhibitor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()){
                    Intent intent = new Intent(getBaseContext(), StaffLoginActivity.class);
                    String choice = "E";
                    SharedPref.putSharedPreferenceForString(MenuSelectionActivity.this, SharedPref.CHOICE, choice);
                    startActivity(intent);
                    finish();
                }else{
                    String choice = "E";
                    SharedPref.putSharedPreferenceForString(MenuSelectionActivity.this, SharedPref.CHOICE, choice);
                    startActivity(new Intent(MenuSelectionActivity.this,OfflineQRCodeActivity.class));
                    finish();
                }

            }
        });

        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MenuSelectionActivity.this);
                builder1.setCancelable(false);
                builder1
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to Logout From App ? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPref.putSharedPreferenceForString(MenuSelectionActivity.this, SharedPref.LOGGEDINSTATUS, "LOGGED_OUT");
                                startActivity(new Intent(MenuSelectionActivity.this,MainActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
