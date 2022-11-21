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

class RecipeData{
    String name; //재료 이름
    String imagePath; //이미지 경로
    String listIngredient; //재료목록
    String link;

    public RecipeData(String name, String imagePath, String listIngredient, String link){
        this.name= name;
        this.imagePath = imagePath;
        this.listIngredient = listIngredient;
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {
    static ArrayList<RecipeData> RecipeDataList = new ArrayList<>();

    public RecipeListAdapter(ArrayList<RecipeData> recipeDataList){
        this.RecipeDataList = recipeDataList;
    }

    @NonNull
    @Override
    public RecipeListAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list,parent,false);
        return new RecipeViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapter.RecipeViewHolder holder, int position) {
        RecipeData item = RecipeDataList.get(position);

        holder.name.setText(item.name);
        holder.listIngredient.setText(item.listIngredient);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference submitPng = storageRef.child(item.imagePath);

        Glide.with(holder.image.getContext()).load(submitPng).into(holder.image);
    }
    @Override
    public int getItemCount() {
        return RecipeDataList.size();
    }

    //검색
    public void setItems(ArrayList<RecipeData> list){
        RecipeDataList = list;
        notifyDataSetChanged();
    }

    //클릭
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private static OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public interface OnLongItemClickListener {
        void onLongItemClick(int pos);
    }

    private static OnLongItemClickListener onLongItemClickListener = null;

    public void setOnLongItemClickListener(OnLongItemClickListener listener) {
        this.onLongItemClickListener = listener;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView listIngredient;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_recipeImage);
            name = itemView.findViewById(R.id.tv_recipeName);
            listIngredient = itemView.findViewById(R.id.tv_recipeListIngredient);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (onLongItemClickListener != null) {
                            onLongItemClickListener.onLongItemClick(position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }
}
