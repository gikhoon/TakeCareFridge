package com.takecarefridge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

class IngredientDetailData{
    String name;
    String imagePath;

    IngredientDetailData(String name, String imagePath){
        this.name=name;
        this.imagePath=imagePath;
    }
}

public class AddIngredientDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static ArrayList<IngredientDetailData> ingredientDetailDataList = new ArrayList<>();
    private static AddIngredientDetailListAdapter.OnItemClickListener mListener = null;

    public AddIngredientDetailListAdapter(ArrayList<IngredientDetailData> ingredientDetailData){
        this.ingredientDetailDataList = ingredientDetailData;
    }

    public void setOnClickListener(AddIngredientDetailListAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, IngredientDetailData data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_ingredient_detail_list, parent, false);
        return new AddIngredientDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AddIngredientDetailViewHolder holder = (AddIngredientDetailViewHolder)viewHolder;
        IngredientDetailData item = ingredientDetailDataList.get(position);

        holder.name.setText(item.name);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference submitPng = storageRef.child(item.imagePath);

        Glide.with(holder.image.getContext()).load(submitPng).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return ingredientDetailDataList.size();
    }

    public static class AddIngredientDetailViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;

        public AddIngredientDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_addIngredientDetailImage);
            name = itemView.findViewById(R.id.tv_addIngredientDetailName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(view, ingredientDetailDataList.get(position));
                        }
                    }
                }
            });
        }
    }
}
