package com.takecarefridge;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

class   IngredientData{
    String name; //재료 이름
    String imagePath; //이미지 경로

    public IngredientData(String name, String imagePath){
        this.name= name;
        this.imagePath = imagePath;
    }
}

public class AddIngredientListAdapter extends RecyclerView.Adapter<AddIngredientListAdapter.AddIngredientViewHolder> {
    static ArrayList<IngredientData> ingredientDataList = new ArrayList<>();

    public AddIngredientListAdapter(ArrayList<IngredientData> ingredientDataList){
        this.ingredientDataList = ingredientDataList;
    }

    @NonNull
    @Override
    public AddIngredientListAdapter.AddIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_ingredient_list,parent,false);
        return new AddIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddIngredientListAdapter.AddIngredientViewHolder holder, int position) {
        IngredientData item = ingredientDataList.get(position);

        holder.name.setText(item.name);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference submitPng = storageRef.child(item.imagePath);

        Glide.with(holder.image.getContext()).load(submitPng).into(holder.image);
    }


    @Override
    public int getItemCount() {
        return ingredientDataList.size();
    }

    public static class AddIngredientViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;

        public AddIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_addingredientImage);
            name = itemView.findViewById(R.id.tv_addingredientName);
        }
    }
}
