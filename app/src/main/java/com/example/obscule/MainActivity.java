package com.example.obscule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.obscule.adapters.RecyclerAdapter;
import com.example.obscule.object.FetchData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    List<FetchData> fetchDataList;
    DatabaseReference reference;
    RecyclerAdapter helperAdapter;
    ImageView imageView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FloatingActionButton float_left, float_right;
    Toolbar toolbar;
    Animation animation;
    public static ImagesDatabase imagesDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Please Wait....", Toast.LENGTH_SHORT).show();
        getFromFirebase();
        find();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //roomni ishlatarkan
        imagesDatabase = Room.databaseBuilder(getApplicationContext(),ImagesDatabase.class,"imagesdb").allowMainThreadQueries().build();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && float_left.getVisibility() == View.VISIBLE && dy > 0 && float_right.getVisibility() == View.VISIBLE) {
                    float_left.hide();
                    float_right.hide();
                } else if (dy < 0 && float_left.getVisibility() != View.VISIBLE && dy < 0 && float_right.getVisibility() != View.VISIBLE) {
                    float_left.show();
                    float_right.show();
                }

            }
        });

        float_left.setOnClickListener(v -> {
            helperAdapter = new RecyclerAdapter((ArrayList<FetchData>) fetchDataList, getApplicationContext());
            recyclerView.swapAdapter(helperAdapter, true);
        });
        float_right.setOnClickListener(v -> {
            CountDownTimer countDownTimer = new CountDownTimer(1000,1) {
                @Override
                public void onTick(long l) {
                    animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animcha);
                    float_right.startAnimation(animation);
                }

                @Override
                public void onFinish() {
                    startActivity(new Intent(getApplicationContext(),DisplayImage.class));
                }
            };
            countDownTimer.start();
        });
    }

    //firebasedan rasm olish
    public void getFromFirebase() {
        reference = FirebaseDatabase.getInstance().getReference("recyclerview");
        fetchDataList = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    FetchData fetchData = ds.getValue(FetchData.class);
                    fetchDataList.add(fetchData);
                    shuffleFun();
                }
                helperAdapter = new RecyclerAdapter((ArrayList<FetchData>) fetchDataList, getApplicationContext());
                recyclerView.setAdapter(helperAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Xatolik", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //navigationdrawerni itemlarini bosilishi
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download_item:
                Toast.makeText(this, "Downloads", Toast.LENGTH_SHORT).show();
                break;
            case R.id.favorite_item:
//                startActivity(new Intent(this,Favorite.class));
                Toast.makeText(this, "Favourite", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share_item:
                shareApplication();
                break;
            case R.id.policy_item:
                policyFun();
                break;
            case R.id.exit_item:
                drawerLayout.closeDrawers();
                chiqish();
                break;
        }
        return false;
    }

    private void shareApplication() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("*/*");

        File originalApk = new File(filePath);

        try {
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", tempFile);
            intent.putExtra(Intent.EXTRA_STREAM, photoURI   );
            startActivity(Intent.createChooser(intent, "Choose type"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //toolbar uchun menu yaratish
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //toolbar menusini bosilishi
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item2:
                animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.toolbar_animcha);
                shuffleFun();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //rasmlarni random qilish
    public void shuffleFun() {
        Collections.shuffle(fetchDataList);
        helperAdapter = new RecyclerAdapter((ArrayList<FetchData>) fetchDataList, getApplicationContext());
        recyclerView.swapAdapter(helperAdapter, false);
    }

    public void find() {
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.image_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        float_left = findViewById(R.id.float_btn_to_left);
        float_right = findViewById(R.id.float_btn_to_right);
        recyclerView = findViewById(R.id.recycler_view);
        navigationView = findViewById(R.id.navigation_view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            chiqish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void chiqish() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Exit");
        alert.setMessage("Dasturdan chiqish");
        alert.setPositiveButton("OK", (dialog, which) -> {
            finish();
        });
        alert.setNegativeButton("Cancel", ((dialogInterface, i) -> {
        }));
        final AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teal_200));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.setIcon(R.drawable.ic_exit_to_app_24);
            }
        });
        dialog.show();

    }
    public void policyFun(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Disclaimer");
        alert.setMessage(R.string.text);
        alert.setPositiveButton("Close",((dialogInterface, i) -> {
        }));
        final AlertDialog dialog = alert.create();
        dialog.show();
    }
}