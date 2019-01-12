package com.example.apple.accesspoint;

import android.content.Intent;
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

    EditText login_username,login_password;
    Button loginBtn;
    ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiInterface = APIList.getApClient().create(ApiInterface.class);
        login_username = (EditText) findViewById(R.id.login_username);
        login_password = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_submit);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visitorLogin(login_username.getText().toString(),login_password.getText().toString());
            }
        });
    }


    private void visitorLogin(String email,String Password) {
        Call<UniversalModel> call = apiInterface.Login(email,Password);

        //progressDialog.show();
       // progressDialog.setCancelable(false);


        call.enqueue(new Callback<UniversalModel>() {
            @Override
            public void onResponse(Call<UniversalModel> call, Response<UniversalModel> response) {
                Log.d("loginresponse", new Gson().toJson(response.body()));
               // progressDialog.dismiss();
                if (!new  Gson().toJson(response.body()).equals(null)){
                    String code = new Gson().toJson(response.body().getCode());
                    String trimCode = code.replaceAll("\"","");

                    if (trimCode.equals("1")) {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this,StaffLoginActivity.class));
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
                //progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
