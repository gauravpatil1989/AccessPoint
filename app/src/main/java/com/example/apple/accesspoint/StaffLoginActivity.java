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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffLoginActivity extends AppCompatActivity {

    EditText staff_username;
    Button loginBtn;
    ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);
        apiInterface = APIList.getApClient().create(ApiInterface.class);
        staff_username = (EditText) findViewById(R.id.staff_username);
        loginBtn = (Button) findViewById(R.id.login_submit);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = staff_username.getText().toString();
                Log.d("MobileNumber",mobile);
                staffLogin(mobile);
            }
        });
    }

    private void staffLogin(String email) {
        Call<JsonObject> call = apiInterface.StaffLogin("9769569608");

        //progressDialog.show();
        // progressDialog.setCancelable(false);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
               // Log.d("staffLoginresponse", new Gson().toJson(response.body()));
                // progressDialog.dismiss();



                        Log.d("VisitorResponse",response.body().toString());
                        if (response != null) {
                            JsonObject object = (JsonObject) new JsonParser().parse(String.valueOf(response.body()));
                            Log.d("VisitorResponse", String.valueOf(object));
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
                            intent.putExtra("ID", id);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("MOBILE", mobile);
                            intent.putExtra("CNAME", CName);
                            intent.putExtra("TOTALENTRIES", totalEntries);
                            intent.putExtra("LASTACCESS", lastAccess);
                            startActivity(intent);

                        }
//                        startActivity(new Intent(StaffLoginActivity.this,StaffLoginActivity.class));

//                        finish();
//                    } else if (response.body().getCode().equals("0")) {
//                        Toast.makeText(StaffLoginActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
//                    }




            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //progressDialog.dismiss();
                Toast.makeText(StaffLoginActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
