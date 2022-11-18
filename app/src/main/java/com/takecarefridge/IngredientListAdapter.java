package com.takecarefridge;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

class FridgeData implements Comparable<FridgeData>{
    String imagePath;
    String name; //재료 이름
    long totalED; //등록일 기준으로 유통기한 ED==유통기한(expiration date)
    long remainED; //남은 유통기한

    public FridgeData(String imagePath,String name,long totalED, long remainED){
        this.imagePath = imagePath;
        this.name= name;
        this.totalED=totalED;
        this.remainED = remainED;
    }

    @Override
    public int compareTo(FridgeData fridgeData) {
        if(remainED > fridgeData.remainED){
            return 1;
        }
        else return -1;
    }
}

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder> {
    ArrayList<FridgeData> fridgeDataList = new ArrayList<>();

    private IngredientListAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(IngredientListAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    public IngredientListAdapter(ArrayList<FridgeData> fridgeDataList){
        this.fridgeDataList = fridgeDataList;
    }


    @NonNull
    @Override
    public IngredientListAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list,parent,false);
        return new IngredientViewHolder(view);
    }
    public interface OnItemClickListener{
        void onItemClick(View v, FridgeData data);
    }


    @Override
    public void onBindViewHolder(@NonNull IngredientListAdapter.IngredientViewHolder holder, int position) {
        FridgeData item = fridgeDataList.get(position);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(item.imagePath);

        Glide.with(holder.image.getContext()).load(pathReference).into(holder.image);
        holder.name.setText(item.name);

        if(item.remainED<0){
            holder.EDProgressBar.setProgress(0);
            holder.remainED.setText(item.remainED+"\n(만료)");
        }
        else {
            if(item.remainED<=10){
                holder.EDProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            }
            holder.EDProgressBar.setProgress((int)item.remainED); //남은 일자로 수정
            holder.remainED.setText(item.remainED+"일");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos  = holder.getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    if(mListener != null)
                        mListener.onItemClick(view, fridgeDataList.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fridgeDataList.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView remainED;
        ProgressBar EDProgressBar;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.iv_ingredientImage);
            name = itemView.findViewById(R.id.tv_ingredientName);
            remainED = itemView.findViewById(R.id.tv_ingredientED);
            EDProgressBar = itemView.findViewById(R.id.pb_edProgressBar);
        }


    }
}
