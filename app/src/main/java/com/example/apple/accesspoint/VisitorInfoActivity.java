package com.example.apple.accesspoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorInfoActivity extends AppCompatActivity {

    TextView visitor_name,company_name,visitor_email,visitor_mobile,total_entries,last_visit;
    Button btn_access;
    ApiInterface apiInterface;
    String id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_info);
        apiInterface = APIList.getApClient().create(ApiInterface.class);




        visitor_name = (TextView) findViewById(R.id.visitor_name);
        company_name = (TextView) findViewById(R.id.company_name);
        visitor_email = (TextView) findViewById(R.id.visitor_email);
        visitor_mobile = (TextView) findViewById(R.id.visitor_mobile);
        total_entries = (TextView) findViewById(R.id.total_entries);
        last_visit = (TextView) findViewById(R.id.last_visit);
        btn_access = (Button) findViewById(R.id.btn_access);


        String NAME= getIntent().getStringExtra("FULLNAME");
        id= getIntent().getStringExtra("ID");
        String email= getIntent().getStringExtra("EMAIL");
        String mobile= getIntent().getStringExtra("MOBILE");
        String cname= getIntent().getStringExtra("CNAME");
        String totalentries= getIntent().getStringExtra("TOTALENTRIES");
        String lastaccess= getIntent().getStringExtra("LASTACCESS");

        visitor_name.setText(NAME);
        //visitor_name.setText(id);
        company_name.setText(email);
        visitor_email.setText(mobile);
        visitor_mobile.setText(cname);
        total_entries.setText("Total Entries :"+totalentries);
        last_visit.setText("Last Visit :"+lastaccess);

        btn_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gateAccess("Gate_1",id,id);
            }
        });

    }

    private void gateAccess(String gateId,String visitorId,String staffId) {
        Call<UniversalModel> call = apiInterface.gateAccess(gateId,visitorId,staffId);

        //progressDialog.show();
        // progressDialog.setCancelable(false);


        call.enqueue(new Callback<UniversalModel>() {
            @Override
            public void onResponse(Call<UniversalModel> call, Response<UniversalModel> response) {
                Log.d("gateresponse", new Gson().toJson(response.body()));
                // progressDialog.dismiss();
                if (!new  Gson().toJson(response.body()).equals(null)){
                    String code = new Gson().toJson(response.body().getCode());
                    String trimCode = code.replaceAll("\"","");

                    if (trimCode.equals("1")) {
                        Toast.makeText(VisitorInfoActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(VisitorInfoActivity.this,StaffLoginActivity.class));
                        finish();
                    } else if (response.body().getCode().equals("0")) {
                        Toast.makeText(VisitorInfoActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(VisitorInfoActivity.this, "Null In Response", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onFailure(Call<UniversalModel> call, Throwable t) {
                //progressDialog.dismiss();
                Toast.makeText(VisitorInfoActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
