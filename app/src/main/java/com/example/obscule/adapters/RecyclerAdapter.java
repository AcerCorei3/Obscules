package com.example.obscule.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.example.obscule.DisplayImage;
import com.example.obscule.R;
import com.example.obscule.object.FetchData;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    ArrayList<FetchData> fetchDataList;
    public static Context context;
    public static String global_image;


    public RecyclerAdapter(ArrayList<FetchData> fetchDataList, Context context) {
        this.fetchDataList = fetchDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false),context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        FetchData fetchData = fetchDataList.get(position);
        Glide.with(holder.imageView.getContext()).load(fetchData.getImage()).into(holder.imageView);
        holder.itemView.setOnClickListener(v->{
            startImageActivity(fetchData.getImage());
        });

    }

    @Override
    public int getItemCount() {
        return fetchDataList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        Context context;

        public RecyclerViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
            this.context = context;
        }


        @Override
        public void onClick(View view) {
            global_image = fetchDataList.get(getAdapterPosition()).getImage();
            context.startActivity(new Intent(context,DisplayImage.class));
        }
    }
    void startImageActivity(String imgUrl) {
        Intent intent = new Intent(context, DisplayImage.class);
        intent.putExtra("recyclerview",imgUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        //bu pastdagi kod listdagi rasmlarni linki bilan googledan ochib beradi

//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imgUrl));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);


    }
}
