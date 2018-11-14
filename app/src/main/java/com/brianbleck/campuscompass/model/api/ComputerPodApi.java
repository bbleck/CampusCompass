package com.brianbleck.campuscompass.model.api;

import com.brianbleck.campuscompass.model.entity.Token;
import com.brianbleck.campuscompass.model.utility.TokenType;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ComputerPodApi extends Service{

  @Override
  @GET("computerpods.json")
  Call<List<Token>> get();

//  TokenType type = TokenType.COMPUTER_POD;
}
