package com.example.phoenix.securityappforgirls03;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM table01")
    List<Contact> getAllWord();
    @Insert
    void insert(Contact contact);
    @Update
    void update(Contact contact);
}
