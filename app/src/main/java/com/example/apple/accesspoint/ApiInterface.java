package com.example.apple.accesspoint;

import com.google.gson.JsonObject;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("StaffLogin")
    retrofit2.Call<UniversalModel> Login(@Field("userid") String EmpId,
                                     @Field("password") String idea);



    @FormUrlEncoded
    @POST("VisitorInfo")
    retrofit2.Call<JsonObject> StaffLogin(@Field("data") String EmpId);


    @FormUrlEncoded
    @POST("GateAccess")
    retrofit2.Call<UniversalModel> gateAccess(@Field("gateid") String gateId,@Field("vid") String visitorId,@Field("staffid") String staffId);

}
