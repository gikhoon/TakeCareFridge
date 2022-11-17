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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

class IngredientData{
    String name; //재료 이름

    public IngredientData(String name){
        this.name= name;
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
        Context context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_ingredient_list,parent,false);

        /*FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference submitPng = storageRef.child("재료/"+item.name+".png");
        submitPng.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(iv1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });*/

        return new AddIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddIngredientListAdapter.AddIngredientViewHolder holder, int position) {
        IngredientData item = ingredientDataList.get(position);
        holder.name.setText(item.name);
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
            image = itemView.findViewById(R.id.iv_ingredientImage);
            name = itemView.findViewById(R.id.tv_ingredientName);
        }
    }
}
