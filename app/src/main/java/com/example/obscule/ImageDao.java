package com.example.obscule;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.obscule.object.Images;

import java.util.List;

@Dao
public interface ImageDao {


    @Insert
    public void addimage(Images images);

    @Query("SELECT*FROM images")
    public List<Images> show();

    @Delete
    void delete(Images images);
}
