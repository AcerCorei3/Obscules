package com.example.obscule;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.obscule.object.Images;

@Database(entities = {Images.class},version = 1)
public abstract class ImagesDatabase extends RoomDatabase {
    public abstract ImageDao imageDao();
}
