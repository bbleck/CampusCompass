package com.brianbleck.campuscompass.model.db;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;
import com.brianbleck.campuscompass.model.dao.TokenDao;
import com.brianbleck.campuscompass.model.db.CampusInfoDB.Converters;
import com.brianbleck.campuscompass.model.entity.Token;

@Database(
    entities = {Token.class},
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters.class)
public abstract class CampusInfoDB extends RoomDatabase {

  private static final String DB_NAME = "campus_info_db";

  private static CampusInfoDB instance = null;

  public abstract TokenDao getTokenDao();

  public synchronized static CampusInfoDB getInstance(Context context) {
    if (instance == null) {
      instance = Room.databaseBuilder(context.getApplicationContext(), CampusInfoDB.class, DB_NAME)
          .build();
    }
    return instance;
  }

  public synchronized static void forgetInstance() {
    instance = null;
  }

  public static class Converters{

  }
}
