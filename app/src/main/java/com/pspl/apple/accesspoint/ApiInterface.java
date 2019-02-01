package com.pspl.apple.accesspoint;

import com.google.gson.JsonObject;

import org.json.JSONObject;

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

    @FormUrlEncoded
    @POST("Exhibitor_GateAccess")
    retrofit2.Call<UniversalModel> ExhibitorGateAccess(@Field("gateid") String gateId,@Field("exhibid") String exhibId,@Field("staffid") String EmpId);


    @FormUrlEncoded
    @POST("ExhibitorAccessInfo")
    retrofit2.Call<JsonObject> ExhibitorAccess(@Field("data") String gateId);

    @FormUrlEncoded
    @POST("SaveBulkGateVisits")
    retrofit2.Call<JsonObject> SaveBulkGateVisits(@Field("data") JSONObject EmpId);
}
