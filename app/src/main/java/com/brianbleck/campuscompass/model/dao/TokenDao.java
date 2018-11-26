package com.brianbleck.campuscompass.model.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.brianbleck.campuscompass.model.entity.Token;
import com.brianbleck.campuscompass.model.utility.TokenType;
import java.util.List;

/**
 *  A {@link Dao} class for use with {@link android.arch.persistence.room.Room}, that describes interations with the {@link android.arch.persistence.room.Database}.
 */
@Dao
public interface TokenDao {

  /**
   * Inserts a {@link Token} into the {@link android.arch.persistence.room.Database}.
   * @param token the object to be inserted.
   * @return the number inserted
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(Token token);

  /**
   * Inserts a {@link List} of {@link Token} into the {@link android.arch.persistence.room.Database}.
   * @param tokens the {@link List} to be inserted.
   *
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(List<Token> tokens);

  /**
   * Retrieves a {@link List} of {@link Token} with a given {@link TokenType} from the {@link android.arch.persistence.room.Database}.
   * @param type the specified {@link TokenType}
   * @return a {@link List} of {@link Token}
   */
  @Query("SELECT * FROM Token WHERE token_type = :type")
  List<Token> select(TokenType type);

  /**
   * Retrieves a complete {@link List} of {@link Token} from the {@link android.arch.persistence.room.Database}.
   * @return a {@link List} of {@link Token}
   */
  @Query("SELECT * FROM Token")
  List<Token> selectAll();

  /**
   * Deletes a {@link Token} from the {@link android.arch.persistence.room.Database}.
   * @param token the {@link Token} to be deleted
   * @return the result identifier
   */
  @Delete
  int delete(Token token);

  /**
   * Deletes the contents of the {@link android.arch.persistence.room.Database}.
   * @return the result identifier
   */
  @Query("DELETE FROM Token")
  int nuke();

}
