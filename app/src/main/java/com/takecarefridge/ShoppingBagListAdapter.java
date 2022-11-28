package com.takecarefridge;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

class ShoppingData{ //데이터를 담을 Modle 생성
    String name; // 재료 이름
    String imagePath; // 이미지 경로
    String where; //냉동실인지 냉장실인지
    String BigCategory;// 대분류
    String DetailCategory; //소분류

    public ShoppingData(String name, String imagePath, String where, String BigCategory, String DetailCategory){
        this.name = name;
        this.imagePath = imagePath;
        this.where = where;
        this.BigCategory = BigCategory;
        this.DetailCategory = DetailCategory;
    }
}


public class ShoppingBagListAdapter extends RecyclerView.Adapter<ShoppingBagListAdapter.ShoppingViewHolder> {
    private static ShoppingBagListAdapter.OnItemClickListener mListener = null;

    static ArrayList<ShoppingData> ShoppingDataList = new ArrayList<>();

    public ShoppingBagListAdapter(ArrayList<ShoppingData> ShoppingDataList){
        this.ShoppingDataList = ShoppingDataList;
    }
    public void setOnItemClickListener(ShoppingBagListAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    //클릭
    public interface OnItemClickListener {
        void onItemClick(View v, ShoppingData data);
    }


    @NonNull
    @Override
    public ShoppingBagListAdapter.ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoppingbag_list,parent,false);
        return new ShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingBagListAdapter.ShoppingViewHolder holder, int position){
        ShoppingData item = ShoppingDataList.get(position);

        holder.name.setText(item.name);

    }
    @Override
    public int getItemCount(){return ShoppingDataList.size();}

    public static class ShoppingViewHolder extends RecyclerView.ViewHolder{
        TextView name;

        public ShoppingViewHolder(@NonNull View itemView){
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.iv_shoppingList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onItemClick(view, ShoppingDataList.get(position));
                        }
                    }
                }
            });
        }
    }
}
