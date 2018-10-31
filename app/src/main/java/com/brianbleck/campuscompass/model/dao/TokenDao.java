package com.brianbleck.campuscompass.model.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.brianbleck.campuscompass.model.entity.Token;
import com.brianbleck.campuscompass.model.utility.TokenType;
import java.util.List;

@Dao
public interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insert(Token token);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insert(List<Token> tokens);


    @Query("SELECT * FROM Token WHERE token_type = :type")
    List<Token> select(TokenType type);

    @Query("SELECT * FROM Token")
    List<Token> selectAll();

    @Delete
    int delete(Token token);

  @Query("DELETE FROM Token")
  int nuke();

}
