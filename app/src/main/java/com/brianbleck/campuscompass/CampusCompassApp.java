package com.brianbleck.campuscompass;

import android.app.Application;
import com.brianbleck.campuscompass.model.dao.TokenDao;
import com.brianbleck.campuscompass.model.db.CampusInfoDB;
import com.brianbleck.campuscompass.model.entity.Token;
import com.brianbleck.campuscompass.model.utility.TokenPrepper;
import com.brianbleck.campuscompass.model.utility.TokenType;
import com.facebook.stetho.Stetho;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CampusCompassApp extends Application {

  CampusInfoDB database;
  private static boolean SHOULD_FILL_DB_W_TEST = true;
  private static int NON_SEARCH_TYPES = 2;
  private static int TOTAL_TYPES = TokenType.values().length - NON_SEARCH_TYPES;
  private Random rng;

  @Override
  public void onCreate() {
    super.onCreate();
    initSetupTasks();

  }



  private void initSetupTasks() {
    Stetho.initializeWithDefaults(this);
    database = CampusInfoDB.getInstance(this);
    database.getTokenDao();
    if(SHOULD_FILL_DB_W_TEST){
      fillDBwithTest();
    }else{
      fillDBwithAPI();
    }

  }

  private void fillDBwithAPI() {
  }

  private void fillDBwithTest() {
    rng = new Random();
    List<Token> prepopulateList = new LinkedList<>();
    TokenType tempType = TokenType.BUILDING;
    for (int i = 0; i < TOTAL_TYPES; i++) {
      tempType = TokenType.values()[i];
      for (int j = 0; j < rng.nextInt(25)+25; j++) {
        Token tempToken = new Token();
        tempToken.setTokenType(tempType);
        prepopulateList.add(TokenPrepper.prep(getApplicationContext(), tempToken));
      }
    }
    
  }

}
