package com.pspl.apple.accesspoint;




import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView zXingScannerView;
    public String[] VisitorId;
    public String[] company;
    public String[] name;
    public String[] designation;
    public String[] mobile;
    public String[] email;
    int camId = 0;
String sharedchoice;
    ApiInterface apiInterface;


    private static final String TAG = "Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIList.getApClient().create(ApiInterface.class);
       sharedchoice = SharedPref.getSharedPreferenceForString(QRScannerActivity.this, SharedPref.CHOICE);

        zXingScannerView = new ZXingScannerView(this);
        setContentView(zXingScannerView);
        askPermission();
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(QRScannerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QRScannerActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(QRScannerActivity.this);
       // camId = SharedPref.getSharedPreferenceForInt(QRScannerActivity.this, SharedPref.camId);
        if (camId == 1) {
            zXingScannerView.startCamera(1);
        } else
            zXingScannerView.startCamera(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void handleResult(Result result) {
        String resultString = result.getText();
        int colons = 0;
        for (int i=0;i<resultString.length();i++){
            if(resultString.charAt(i) == ':') colons++;
        }
        Log.d("colons",colons+"");
        Log.d("QRScannedresult",resultString);
        String[] resultArray = new String[]{};
        Log.d("resultArray", String.valueOf(resultString.length()));
        for (int i = 0; i < resultString.length(); i++) {
            resultArray = resultString.split("\n");
        }
            VisitorId = resultArray[0].split(":");
        String user = VisitorId[0];
        String whiteSpacea = user.replaceAll("\\s","");
        if (whiteSpacea.equals("UserID")){
            //if (colons<3){
                name = resultArray[1].split(":");
                company = resultArray[2].split(":");
                Log.d("VisitorId",VisitorId[1]);
                Log.d("companyOne",company[1]);


           if (colons>3){
                email = resultArray[3].split(":");
                Log.d("email",email[1]);
           }

           if (colons>4){
                mobile = resultArray[4].split(":");
                Log.d("Moblie$",mobile[1]);
            }

            if (colons>5){
                designation = resultArray[5].split(":");

           }




            if (isNetworkAvailable()){
                staffLogin(VisitorId[1]);

            }else {
                if (sharedchoice.equals("V")){
                    // gateAccess(gateNo,id,empName);
                    Intent intent = new Intent(getBaseContext(), VisitorInfoActivity.class);
                    intent.putExtra("FULLNAME", name[1]);
                    intent.putExtra("VisitorID", VisitorId[1]);
                    intent.putExtra("CNAME", company[1]);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(getBaseContext(), VisitorInfoActivity.class);
                    intent.putExtra("FULLNAME", name[1]);
                    intent.putExtra("VisitorID", VisitorId[1]);
                    intent.putExtra("CNAME", company[1]);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    startActivity(intent);
                    finish();
                }
            }
        }else{
            Toast.makeText(this, "Wrong QR Code", Toast.LENGTH_SHORT).show();
        }

        zXingScannerView.resumeCameraPreview(this);
    }
    @Override
    public void onPointerCaptureChanged ( boolean hasCapture){

    }


    private void staffLogin(String id) {
        Call<JsonObject> call ;

       // call = apiInterface.StaffLogin(id);

        if (sharedchoice.equals("V")){
            call = apiInterface.StaffLogin(id);
        }else{
            call = apiInterface.ExhibitorAccess(id);
        }
        //progressDialog.show();
        //progressDialog.setCancelable(false);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Log.d("staffLoginresponse", new Gson().toJson(response.body()));
                // progressDialog.dismiss();



                Log.d("VisitorResponse",response.body().toString());
                if (sharedchoice.equals("V")){
                    if (response != null) {
                        JsonObject object = (JsonObject) new JsonParser().parse(String.valueOf(response.body()));
                        Log.d("VisitorResponse", String.valueOf(object));
                        String code = object.get("code").toString();
                        String trim = code.replaceAll("\"","");
                        Log.d("Code",trim);
                        if (trim.equals("1")){
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
                            intent.putExtra("TOTALENTRIES", totalEntries);
                            intent.putExtra("LASTACCESS", lastAccess);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            startActivity(intent);
                            finish();
                        }else{
                            String message = object.get("message").toString();
                            Toast.makeText(QRScannerActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }

                }else{
                    if (response != null) {
                        JsonObject object = (JsonObject) new JsonParser().parse(String.valueOf(response.body()));
                        Log.d("VisitorResponse", String.valueOf(object));
                        String code = object.get("code").toString();
                        String trim = code.replaceAll("\"","");
                        Log.d("Code",trim);
                        if (trim.equals("1")){
                            JsonObject jsonObject = object.get("ExhibitorInfo").getAsJsonObject();
                            String name = jsonObject.get("FullName").getAsString();
                            String id = jsonObject.get("ExhibID").getAsString();
                           // String email = jsonObject.get("Email").getAsString();
                           // String mobile = jsonObject.get("Mobile").getAsString();
                            String CName = jsonObject.get("CompanyName").getAsString();
                            String totalEntries = jsonObject.get("TotalEntries").getAsString();
                            String lastAccess = jsonObject.get("LastAccess").getAsString();

                            Intent intent = new Intent(getBaseContext(), VisitorInfoActivity.class);
                            intent.putExtra("FULLNAME", name);
                            intent.putExtra("VisitorID", id);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("MOBILE", mobile);
                            intent.putExtra("CNAME", CName);
                            intent.putExtra("TOTALENTRIES", totalEntries);
                            intent.putExtra("LASTACCESS", lastAccess);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            startActivity(intent);
                            finish();
                        }else{
                            String message = object.get("message").toString();
                            Toast.makeText(QRScannerActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }

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
                Toast.makeText(QRScannerActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();

            }
        });
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
        startActivity(new Intent(QRScannerActivity.this,StaffLoginActivity.class));
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }
}
