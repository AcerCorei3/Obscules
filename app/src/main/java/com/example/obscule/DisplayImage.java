package com.example.obscule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.obscule.adapters.RecyclerAdapter;
import com.example.obscule.object.FetchData;
import com.example.obscule.object.Images;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DisplayImage extends AppCompatActivity {
    ImageView imageView;
    FloatingActionButton heartfloat, addfloat;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView = findViewById(R.id.image_view_display);
        heartfloat = findViewById(R.id.float_heart);
        addfloat = findViewById(R.id.float_add);



        reference = FirebaseDatabase.getInstance().getReference("recyclerview");
        Glide.with(this).load(RecyclerAdapter.global_image).into(imageView);
//        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/obscule-d20dd.appspot.com/o/1.jpg?alt=media&token=1fd45694-a1cc-47d5-813f-613d873174a0").into(imageView);

        heartfloat.setOnClickListener(v->{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                heartfloat.setImageDrawable(getResources().getDrawable(R.drawable.ic_up_chevron, this.getTheme()));
                Images images = new Images();
                images.setImageroom(imageView.toString());
                MainActivity.imagesDatabase.imageDao().addimage(images);
                Toast.makeText(getApplicationContext(), "Ishladi", Toast.LENGTH_SHORT).show();
            } else {
                heartfloat.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_24));
            }
        });
    }
}