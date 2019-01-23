package com.example.apple.accesspoint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText login_username,login_password,gate_no;
    Button loginBtn;
    ApiInterface apiInterface;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (SharedPref.getSharedPreferenceForString(MainActivity.this, SharedPref.LOGGEDINSTATUS).equals("LOGGED_IN")) {
            startActivity(new Intent(MainActivity.this,MenuSelectionActivity.class));
            finish();
        }else{
            apiInterface = APIList.getApClient().create(ApiInterface.class);
            login_username = (EditText) findViewById(R.id.login_username);
            login_password = (EditText) findViewById(R.id.login_password);
            gate_no = (EditText) findViewById(R.id.gate_no);

            loginBtn = (Button) findViewById(R.id.login_submit);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(login_username.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Enter the Staff Name", Toast.LENGTH_SHORT).show();
                    }else if (gate_no.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Enter Gate Number", Toast.LENGTH_SHORT).show();
                    }else if (login_username.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPref.putSharedPreferenceForString(MainActivity.this, SharedPref.NAME, login_username.getText().toString());
                        SharedPref.putSharedPreferenceForString(MainActivity.this, SharedPref.GATENO, gate_no.getText().toString());
                        visitorLogin(login_username.getText().toString(),login_password.getText().toString());
                    }

                }
            });
        }

    }


    private void visitorLogin(String email,String Password) {
        Call<UniversalModel> call = apiInterface.Login(email,Password);
        dialog = ProgressDialog.show(MainActivity.this, "", "", false, true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_bar);
        dialog.setCancelable(false);

        call.enqueue(new Callback<UniversalModel>() {
            @Override
            public void onResponse(Call<UniversalModel> call, Response<UniversalModel> response) {
                Log.d("loginresponse", new Gson().toJson(response.body()));
                dialog.dismiss();
                if (!new  Gson().toJson(response.body()).equals(null)){
                    String code = new Gson().toJson(response.body().getCode());
                    String trimCode = code.replaceAll("\"","");

                    if (trimCode.equals("1")) {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        SharedPref.putSharedPreferenceForString(MainActivity.this, SharedPref.LOGGEDINSTATUS, "LOGGED_IN");

                        startActivity(new Intent(MainActivity.this,MenuSelectionActivity.class));
                        finish();
                    } else if (response.body().getCode().equals("0")) {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Null In Response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UniversalModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
