package com.example.obscule.object;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "images")
public class Images {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "imageroom")
    String imageroom;

    public String getImageroom() {
        return imageroom;
    }

    public void setImageroom(String imageroom) {
        this.imageroom = imageroom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
