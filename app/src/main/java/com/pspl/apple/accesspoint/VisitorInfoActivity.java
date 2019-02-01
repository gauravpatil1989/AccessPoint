package com.pspl.apple.accesspoint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorInfoActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    TextView visitor_name,company_name,visitor_email,visitor_mobile,total_entries,last_visit;
    Button btn_access;
    ApiInterface apiInterface;
    RelativeLayout logout_layout,back_layout;
    String id ;
    Toolbar toolbar;
    TextView toolbar_title;
    String gateNo;
    String empName;
    String sharedchoice;
    DatabaseHelper databaseHelper;
    OfflineVisitors offlineVisitors;
    String NAME,cname,mobile,email;
    String totalentries,lastaccess;
    ArrayList<OfflineVisitors> visitorsarrayList;
    private static VisitorInfoActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_info);
        instance = this;
        databaseHelper = new DatabaseHelper(this);
        apiInterface = APIList.getApClient().create(ApiInterface.class);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        logout_layout = (RelativeLayout) findViewById(R.id.logout_layout);
        logout_layout.setVisibility(View.VISIBLE);
        back_layout = (RelativeLayout) toolbar.findViewById(R.id.back_layout);
        back_layout.setVisibility(View.VISIBLE);
        visitor_name = (TextView) findViewById(R.id.visitor_name);
        company_name = (TextView) findViewById(R.id.company_name);
        visitor_email = (TextView) findViewById(R.id.visitor_email);
        visitor_mobile = (TextView) findViewById(R.id.visitor_mobile);
        total_entries = (TextView) findViewById(R.id.total_entries);
        last_visit = (TextView) findViewById(R.id.last_visit);
        btn_access = (Button) findViewById(R.id.btn_access);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("VisitorInfo");
        gateNo = SharedPref.getSharedPreferenceForString(VisitorInfoActivity.this, SharedPref.GATENO);
        empName = SharedPref.getSharedPreferenceForString(VisitorInfoActivity.this, SharedPref.NAME);
        sharedchoice = SharedPref.getSharedPreferenceForString(VisitorInfoActivity.this, SharedPref.CHOICE);
        visitorsarrayList = new ArrayList<>();

        if (isNetworkAvailable()){
            NAME = getIntent().getStringExtra("FULLNAME");
            id = getIntent().getStringExtra("VisitorID");//
            email = getIntent().getStringExtra("EMAIL");
            mobile = getIntent().getStringExtra("MOBILE");
            cname = getIntent().getStringExtra("CNAME");
            totalentries = getIntent().getStringExtra("TOTALENTRIES");
            lastaccess = getIntent().getStringExtra("LASTACCESS");
        }else{
            if (sharedchoice.equals("E")){
                id = getIntent().getStringExtra("VisitorID");
                cname = getIntent().getStringExtra("CNAME");
                NAME = getIntent().getStringExtra("FULLNAME");
            }else{
                id = getIntent().getStringExtra("VisitorID");
                cname = getIntent().getStringExtra("CNAME");
                NAME = getIntent().getStringExtra("FULLNAME");
            }
        }
        Log.d("DataOne",empName+" "+id+" "+gateNo+" ");
       // UploadOfflineDataToServer();

        visitor_name.setText("Name :-"+NAME);
        company_name.setText(email);
        visitor_email.setText(mobile);
        visitor_mobile.setText("Company :-"+cname);
        total_entries.setText("Total Entries :"+totalentries);
        last_visit.setText("Last Visit :"+lastaccess);

        btn_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()){
                    if (sharedchoice.equals("V")){
                        gateAccess(gateNo,id,empName);
                    }else{
                        exhibitorGateAccess(gateNo,id,empName);
                    }
                }else {
                    //Toast.makeText(VisitorInfoActivity.this, "Internet not connected", Toast.LENGTH_SHORT).show();
                    String time = GetToday();
                    AddData(empName,id,gateNo,time);
                }
            }
        });


        back_layout = (RelativeLayout) toolbar.findViewById(R.id.back_layout) ;
        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VisitorInfoActivity.this,StaffLoginActivity.class));
                finish();
            }
        });
        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(VisitorInfoActivity.this);
                builder1.setCancelable(false);
                builder1
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to Logout From App ? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPref.putSharedPreferenceForString(VisitorInfoActivity.this, SharedPref.LOGGEDINSTATUS, "LOGGED_OUT");
                                startActivity(new Intent(VisitorInfoActivity.this,MainActivity.class));
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

    public static String GetToday(){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Log.d("currentDateTimeString",currentDateTimeString);
        return currentDateTimeString;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void gateAccess(String gateId,String visitorId,String staffId) {
        Call<UniversalModel> call = apiInterface.gateAccess(gateId,visitorId,staffId);

        //progressDialog.show();
        // progressDialog.setCancelable(false);


        call.enqueue(new Callback<UniversalModel>() {
            @Override
            public void onResponse(Call<UniversalModel> call, Response<UniversalModel> response) {
                Log.d("Visitorgateresponse", new Gson().toJson(response.body()));
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


    private void exhibitorGateAccess(String gateId,String visitorId,String staffId) {
        Call<UniversalModel> call = apiInterface.ExhibitorGateAccess(gateId,visitorId,staffId);

        //progressDialog.show();
        // progressDialog.setCancelable(false);


        call.enqueue(new Callback<UniversalModel>() {
            @Override
            public void onResponse(Call<UniversalModel> call, Response<UniversalModel> response) {
                Log.d("exhibitorGateResponse", new Gson().toJson(response.body()));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(VisitorInfoActivity.this,StaffLoginActivity.class));
        finish();
    }

    public void AddData(String emp_name,String vid,String gateId,String scannedTime){
        boolean insertData = databaseHelper.addData(emp_name,vid,gateId,scannedTime);
        if (insertData){
            Toast.makeText(this, "Data Inserted Successfully Offline...!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, " Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    public void UploadOfflineDataToServer(){
        databaseHelper = new DatabaseHelper(this);
        Cursor data = databaseHelper.getListVisitors();
        int numRows = data.getCount();
        if (numRows == 0){
            Toast.makeText(this, "There is nothing in database", Toast.LENGTH_SHORT).show();
        }else {
            while (data.moveToNext()) {
                offlineVisitors = new OfflineVisitors(data.getString(0), data.getString(1), data.getString(2), data.getString(3));
                visitorsarrayList.add(offlineVisitors);
                String vId = offlineVisitors.getV_id();
                String exhibId = offlineVisitors.getE_id();
                String eName = offlineVisitors.getEmp_name();
                String scannedTime = offlineVisitors.getTime();

                try {
                    JSONObject json = jsonResult(vId, exhibId, eName, scannedTime);
                    Log.d("OfflineDataJSOONOBJECT", json.toString());
                    JSONObject dataToSend = new JSONObject();
                    JSONArray jsonArrayProfile = new JSONArray();
                    jsonArrayProfile.put(json);
                    dataToSend.put("Visitors", jsonArrayProfile);
                    Log.d("dataToSend", dataToSend.toString());
                    saveDataOnline(dataToSend);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized VisitorInfoActivity getInstance() {
        return instance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        VisitorInfoActivity.getInstance().setConnectivityListener(this);
    }

    private JSONObject jsonResult(String gid, String exid, String empName,String time) throws JSONException {
        JSONObject json = null;
        json = new JSONObject("{\"" + "gateid" + "\":" + "\"" + gid+
                "\"" + "," + "\"" + "exhibid" + "\":" +"\""+ exid +
                "\"" + "," + "\"" + "scannedby" + "\":" +"\""+ empName +
                "\"" + "," + "\"" + "scannedtime"+ "\":" + "\"" + time+ "\"" + "}");
        return json;
    }


    public void saveDataOnline(JSONObject jsonData) {

        Call<JsonObject> call = apiInterface.SaveBulkGateVisits(jsonData);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("saveDataResponse", new Gson().toJson(response.body()));
                Log.d("response",response.toString());
                JsonObject object = response.body();
                if (new Gson().toJson(response.body())!=null){
                    if (object.get("code").getAsString().equals("1")) {
                        databaseHelper.deleteAll();
                        Log.d("visitorsarrayList", String.valueOf(visitorsarrayList.size()));
                        Toast.makeText(VisitorInfoActivity.this, visitorsarrayList.size()+" Offline Visitors Data is Uploaded to Server", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(VisitorInfoActivity.this, object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(VisitorInfoActivity.this, new Gson().toJson(response.body()), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //progressDialog.dismiss();

                Toast.makeText(VisitorInfoActivity.this, "Cannot Upload Data to Server", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.d("Network Status","Status "+isConnected);

        if(isConnected){
            UploadOfflineDataToServer();
            Toast.makeText(instance, "You are Connected Online", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(instance, "You have Lost Connection", Toast.LENGTH_SHORT).show();
        }
    }
}
