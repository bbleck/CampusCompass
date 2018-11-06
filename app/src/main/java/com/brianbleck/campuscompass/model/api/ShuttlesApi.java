package com.brianbleck.campuscompass.model.api;

import com.brianbleck.campuscompass.model.entity.Token;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ShuttlesApi {


  @GET("unmshuttles.json")
  Call<List<Token>> getShuttlesJson();


}
