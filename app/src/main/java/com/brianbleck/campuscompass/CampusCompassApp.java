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


  @Override
  public void onCreate() {
    super.onCreate();
    initSetupTasks();

  }



  private void initSetupTasks() {
    Stetho.initializeWithDefaults(this);
    database = CampusInfoDB.getInstance(this);
    database.getTokenDao();
  }



}
