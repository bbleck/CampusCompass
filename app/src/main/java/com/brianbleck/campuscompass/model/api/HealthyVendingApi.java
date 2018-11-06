package com.brianbleck.campuscompass.model.api;

import com.brianbleck.campuscompass.model.entity.Token;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HealthyVendingApi {



  @GET("healthyvending.json")
  Call<List<Token>> getHealthyVendingJson();



}
