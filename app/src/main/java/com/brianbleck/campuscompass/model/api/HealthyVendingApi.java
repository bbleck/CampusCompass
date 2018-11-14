package com.brianbleck.campuscompass.model.api;

import com.brianbleck.campuscompass.model.entity.Token;
import com.brianbleck.campuscompass.model.utility.TokenType;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HealthyVendingApi extends Service{


  @Override
  @GET("healthyvending.json")
  Call<List<Token>> get();

//  TokenType type = TokenType.HEALTHY_VENDING;

}
