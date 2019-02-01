package com.pspl.apple.accesspoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffLoginActivity extends AppCompatActivity {

    EditText staff_username,gate_number;
    Button loginBtn;
    TextView emp_name_text;
    ApiInterface apiInterface;
    ImageView btn_qrcode;
    RelativeLayout logout_layout;
    ImageView btn_camera;
    TextView txt_cam,edit_name,toolbar_title;
    ProgressDialog dialog;
    ImageView chenage_name;
    String gateNo;
    RelativeLayout back_layout;
    Toolbar toolbar;
    LinearLayout linear_login;
    boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);
        String empName = SharedPref.getSharedPreferenceForString(StaffLoginActivity.this, SharedPref.NAME);
        gateNo = SharedPref.getSharedPreferenceForString(StaffLoginActivity.this, SharedPref.GATENO);
        apiInterface = APIList.getApClient().create(ApiInterface.class);
        logout_layout = (RelativeLayout) findViewById(R.id.logout_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        linear_login = (LinearLayout) findViewById(R.id.linear_login);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        logout_layout.setVisibility(View.VISIBLE);
        emp_name_text = (TextView) findViewById(R.id.emp_name_text);
        gate_number = (EditText) findViewById(R.id.gate_number);
        emp_name_text.setText("Staff Name :"+empName);
        gate_number.setText(gateNo);
        staff_username = (EditText) findViewById(R.id.staff_username);
        edit_name = (TextView) findViewById(R.id.edit_name);
        btn_qrcode = (ImageView) findViewById(R.id.btn_qrcode);
        final String sharedchoice = SharedPref.getSharedPreferenceForString(StaffLoginActivity.this, SharedPref.CHOICE);
        Log.d("sharedchoice",sharedchoice);

            if (sharedchoice.equals("V")){
                toolbar_title.setText("Visitor");
            }else{
                toolbar_title.setText("Exhibitor");
                linear_login.setVisibility(View.GONE);
            }

        loginBtn = (Button) findViewById(R.id.login_submit);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()){
                    String mobile = staff_username.getText().toString();
                    Log.d("MobileNumber",mobile);
                    if (gate_number.getText().toString().isEmpty()){
                        gate_number.requestFocus();
                        Toast.makeText(StaffLoginActivity.this, "Pls Enter the Gate Number...", Toast.LENGTH_SHORT).show();
                    }else if (staff_username.getText().toString().isEmpty()){
                        Toast.makeText(StaffLoginActivity.this, "Enter the Mobile Number or Email or VisitorId", Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPref.putSharedPreferenceForString(StaffLoginActivity.this, SharedPref.GATENO,gate_number.getText().toString());
                        gate_number.setClickable(false);
                        gate_number.setEnabled(false);

                        if (sharedchoice.equals("V")){
                            staffLogin(mobile);
                        }else{
                            exhibitorGateAccess(mobile);
                        }
                    }
                }else{
                    Toast.makeText(StaffLoginActivity.this, "Internet Connection Lost....!", Toast.LENGTH_SHORT).show();

                }

            }
        });
        gate_number.setClickable(false);
        gate_number.setEnabled(false);

        chenage_name = (ImageView) findViewById(R.id.chenage_name);
        chenage_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag){
                    flag = false;
                    if (gate_number.getText().toString().isEmpty()){
                        Toast.makeText(StaffLoginActivity.this, "Pls Enter the Gate Number...", Toast.LENGTH_SHORT).show();
                    }else{
                        disableEditText();
                        edit_name.setText("Tap to Edit");
                        gate_number.requestFocus();
                        SharedPref.putSharedPreferenceForString(StaffLoginActivity.this, SharedPref.GATENO,gate_number.getText().toString());
                    }
                }else{
                    flag = true;
                    edit_name.setText("Tap to Save");
                    enableEditText();
                     gate_number.setText("");
                }
            }
        });

        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(StaffLoginActivity.this);
                builder1.setCancelable(false);
                builder1
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to Logout From App ? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPref.putSharedPreferenceForString(StaffLoginActivity.this, SharedPref.LOGGEDINSTATUS, "LOGGED_OUT");
                                startActivity(new Intent(StaffLoginActivity.this,MainActivity.class));
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

        back_layout = (RelativeLayout) findViewById(R.id.back_layout) ;
        back_layout.setVisibility(View.VISIBLE);
        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StaffLoginActivity.this,MenuSelectionActivity.class));
                finish();
            }
        });

        btn_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StaffLoginActivity.this,QRScannerActivity.class));
                finish();
            }
        });
        txt_cam = (TextView) findViewById(R.id.txt_cam);
        btn_camera = (ImageView) findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int camId = SharedPref.getSharedPreferenceForInt(StaffLoginActivity.this, SharedPref.camId);
                if(camId == 1)
                {
                    SharedPref.putSharedPreferenceForInt(StaffLoginActivity.this, SharedPref.camId, 0);
                    txt_cam.setText("Back Cam");
                }
                else
                {
                    SharedPref.putSharedPreferenceForInt(StaffLoginActivity.this, SharedPref.camId, 1);
                    txt_cam.setText("Front Cam");
                }
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void enableEditText() {
        gate_number.setFocusableInTouchMode(true);
        gate_number.setFocusable(true);
        gate_number.setEnabled(true);
    }

    private void disableEditText() {
        gate_number.setEnabled(false);
        gate_number.setFocusable(false);
        gate_number.setFocusableInTouchMode(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(StaffLoginActivity.this,MenuSelectionActivity.class));
        finish();
    }

    private void staffLogin(String email) {
        Call<JsonObject> call = apiInterface.StaffLogin(email);
        dialog = ProgressDialog.show(StaffLoginActivity.this, "", "", false, true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_bar);
        dialog.setCancelable(false);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    dialog.dismiss();
                        if (response != null) {
                            JsonObject object = response.body();
                            JsonObject objectNew = (JsonObject) new JsonParser().parse(String.valueOf(response.body()));
                            Log.d("VisitorResponse", String.valueOf(objectNew));
                            if (object.get("code").getAsString().equals("1")){
                                JsonObject jsonObject = object.get("VisitorInfo").getAsJsonObject();
                                String name = jsonObject.get("FullName").getAsString();
                                String id = jsonObject.get("VisitorID").getAsString();
                                String email = jsonObject.get("Email").getAsString();
                                String mobile = jsonObject.get("Mobile").getAsString();
                                String CName = jsonObject.get("CompanyName").getAsString();
                                String totalEntries = jsonObject.get("TotalEntries").getAsString();
                                String lastAccess = jsonObject.get("LastAccess").getAsString();

                                Intent intent = new Intent(getBaseContext(), VisitorInfoActivity.class);
                                intent.putExtra("FULLNAME", name);
                                intent.putExtra("VisitorID", id);
                                intent.putExtra("EMAIL", email);
                                intent.putExtra("MOBILE", mobile);
                                intent.putExtra("CNAME", CName);
                                intent.putExtra("Choice", "V");
                                intent.putExtra("TOTALENTRIES", totalEntries);
                                intent.putExtra("LASTACCESS", lastAccess);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                            }
                            else{
                                dialog.dismiss();
                                Toast.makeText(StaffLoginActivity.this, object.get("message").getAsString(), Toast.LENGTH_LONG).show();
                            }
                        }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(StaffLoginActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void exhibitorGateAccess(String email) {
        Call<JsonObject> call = apiInterface.ExhibitorAccess(email);
        dialog = ProgressDialog.show(StaffLoginActivity.this, "", "", false, true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_bar);
        dialog.setCancelable(false);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                if (response != null) {
                    JsonObject object = response.body();
                    JsonObject objectNew = (JsonObject) new JsonParser().parse(String.valueOf(response.body()));
                    Log.d("exhibitorGateAccess", String.valueOf(objectNew));
                    if (object.get("code").getAsString().equals("1")){
                        JsonObject jsonObject = object.get("ExhibitorInfo").getAsJsonObject();
                        String name = jsonObject.get("CompanyName").getAsString();
                        String id = jsonObject.get("ExhibID").getAsString();
                        String email = jsonObject.get("Email").getAsString();
                        String mobile = jsonObject.get("Mobile").getAsString();
                       // String CName = jsonObject.get("CompanyName").getAsString();
                        String totalEntries = jsonObject.get("TotalEntries").getAsString();
                        String lastAccess = jsonObject.get("LastAccess").getAsString();

                        Intent intent = new Intent(getBaseContext(), VisitorInfoActivity.class);
                        intent.putExtra("FULLNAME", name);
                        intent.putExtra("VisitorID", id);
                        intent.putExtra("EMAIL", email);
                        intent.putExtra("MOBILE", mobile);
                        intent.putExtra("CNAME", "");
                        intent.putExtra("Choice", "E");
                        intent.putExtra("TOTALENTRIES", totalEntries);
                        intent.putExtra("LASTACCESS", lastAccess);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        dialog.dismiss();
                        Toast.makeText(StaffLoginActivity.this, object.get("message").getAsString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(StaffLoginActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
