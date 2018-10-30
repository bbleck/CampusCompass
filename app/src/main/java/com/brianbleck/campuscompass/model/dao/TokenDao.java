package com.brianbleck.campuscompass.model.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.brianbleck.campuscompass.model.entity.Token;

import java.util.List;

@Dao
public interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insert(Token token);


    @Query("SELECT * FROM Token WHERE token_type ")
    List<Token> select();

    @Delete
    int delete(Token token);

}
