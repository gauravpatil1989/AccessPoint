package com.example.apple.accesspoint;




import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    ApiInterface apiInterface;


    private static final String TAG = "Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIList.getApClient().create(ApiInterface.class);

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
        Log.d("QRScannedresult", resultString);
        String[] resultArray = new String[]{};
        for (int i = 0; i < resultString.length(); i++) {
            resultArray = resultString.split("\n");
        }

            VisitorId = resultArray[0].split(":");
        String user = VisitorId[0];
        String whiteSpacea = user.replaceAll("\\s","");
        if (whiteSpacea.equals("UserID")){
            name = resultArray[1].split(":");
            company = resultArray[2].split(":");
            email = resultArray[3].split(":");
            mobile = resultArray[4].split(":");
            designation = resultArray[5].split(":");
            staffLogin(VisitorId[1]);
        }else{
            Toast.makeText(this, "Wrong QR Code", Toast.LENGTH_SHORT).show();
        }

        zXingScannerView.resumeCameraPreview(this);
    }
    @Override
    public void onPointerCaptureChanged ( boolean hasCapture){

    }


    private void staffLogin(String id) {
        Call<JsonObject> call = apiInterface.StaffLogin(id);

        //progressDialog.show();
        //progressDialog.setCancelable(false);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Log.d("staffLoginresponse", new Gson().toJson(response.body()));
                // progressDialog.dismiss();



                Log.d("VisitorResponse",response.body().toString());
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
                        startActivity(intent);
                        finish();
                    }else{
                        String message = object.get("message").toString();
                        Toast.makeText(QRScannerActivity.this, message, Toast.LENGTH_LONG).show();
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



}
