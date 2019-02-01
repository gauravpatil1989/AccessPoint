package com.pspl.apple.accesspoint;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIList {
    public static Retrofit retrofit = null;
    public static final String BASEURL = "http://rs1.expolab.in/Api/";


    public static Retrofit getApClient(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASEURL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return  retrofit;
    }
}
