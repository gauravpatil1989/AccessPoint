package com.example.apple.accesspoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by surve on 18-Jul-17.
 */

public class SharedPref {

    private static SharedPreferences sharedpreferences;
    public static final String M_IN = "M_IN";

    public static final String EMPID = "emp_id";
    public static final String MEMID = "mem_id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String GATENO = "gete_no";
    public static final String camId = "camId";
    public static final String USERLOGEDIN = "USER_LOGEDIN";
    public static final String LOGGEDINSTATUS = "LOGGED_OUT";


    public static void putSharedPreferenceForString(Context context, String key, String addingParameter) {
        sharedpreferences = context.getSharedPreferences(M_IN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, addingParameter);
        editor.apply();
    }

    public static void putSharedPreferenceForInt(Context context, String key, int addingParameter) {
        sharedpreferences = context.getSharedPreferences(M_IN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, addingParameter);
        editor.apply();
    }

    public static String getSharedPreferenceForString(Context context, String key) {
        sharedpreferences = context.getSharedPreferences(M_IN, Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, "");
    }

    public static int getSharedPreferenceForInt(Context context, String key) {
        sharedpreferences = context.getSharedPreferences(M_IN, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(key, 0);
    }

    public static void clearAllData(Context context){
        sharedpreferences = context.getSharedPreferences(M_IN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void writeToLogFIle(String lineToWrite)
    {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            File myFile = new File("/sdcard/DstLog.txt");
            if(!myFile.exists())
                myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile,true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            Calendar cal = Calendar.getInstance();

            myOutWriter.append(dateFormat.format(cal.getTime())+" "+lineToWrite+"\n");
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            Log.e("ERRR", "Could not create file",e);
        }
    }




}
