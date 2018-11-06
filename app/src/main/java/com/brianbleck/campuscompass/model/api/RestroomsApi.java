package com.brianbleck.campuscompass.model.api;

import com.brianbleck.campuscompass.model.entity.Token;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RestroomsApi {


  @GET("restrooms.json")
  Call<List<Token>> getRestroomsJson();



}
