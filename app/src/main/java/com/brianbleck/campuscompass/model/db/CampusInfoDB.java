package com.brianbleck.campuscompass.model.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import com.brianbleck.campuscompass.model.dao.TokenDao;
import com.brianbleck.campuscompass.model.db.CampusInfoDB.Converters;
import com.brianbleck.campuscompass.model.entity.Token;
import com.brianbleck.campuscompass.model.utility.TokenType;

/**
 * Class that defines the {@link Database}.
 */
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

  /**
   * Ensures that the class behaves as a singleton.
   * @param context
   * @return the singleton instance of the class.
   */
  public synchronized static CampusInfoDB getInstance(Context context) {
    if (instance == null) {
      instance = Room.databaseBuilder(context.getApplicationContext(), CampusInfoDB.class, DB_NAME)
          .build();
    }
    return instance;
  }

  /**
   * Removes the reference to the singleton instance of the class.
   */
  public synchronized static void forgetInstance() {
    instance = null;
  }

  /**
   * Contains converters for use by {@link Room}.
   */
  public static class Converters{

    @TypeConverter
    public static int intFromType(TokenType type) {
      return type.ordinal();
    }

    @TypeConverter
    public static TokenType typeFromInt(int ordinal) {
      return TokenType.values()[ordinal];
    }

  }
}
