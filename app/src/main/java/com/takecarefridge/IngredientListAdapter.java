package com.takecarefridge;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
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

class Code{
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_BODY = 1;
}

class FridgeData implements Comparable<FridgeData>{
    String imagePath;
    String name; //재료 종류
    String documentName; //document 이름에 들어가는 값
    long totalED; //등록일 기준으로 유통기한 ED==유통기한(expiration date)
    long remainED; //남은 유통기한
    int viewType; //Menu인지 BODY인지

    public FridgeData(String imagePath ,String name,String documentName,long totalED, long remainED, int viewType){
        this.imagePath = imagePath;
        this.name= name;
        this.documentName = documentName;
        this.totalED=totalED;
        this.remainED = remainED;
        this.viewType = viewType;
    }

    @Override
    public int compareTo(FridgeData fridgeData) {
        if(remainED > fridgeData.remainED){
            return 1;
        }
        else return -1;
    }
}

public class IngredientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<FridgeData> fridgeDataList = new ArrayList<>();

    private IngredientListAdapter.OnItemClickListener mListener = null;

    //FridgeMain, FreezerMain에서 온클릭 함수 구현시키면 된다.
    public void setOnItemClickListener(IngredientListAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public IngredientListAdapter(ArrayList<FridgeData> fridgeDataList) {
        this.fridgeDataList = fridgeDataList;
    }

    @Override
    public int getItemViewType(int position) {
        return fridgeDataList.get(position).viewType;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Code.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_menu, parent, false);
            return new IngredientHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list, parent, false);
            return new IngredientBodyViewHolder(view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, FridgeData data);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewholder, int position) {
        if(viewholder instanceof IngredientBodyViewHolder){
            IngredientBodyViewHolder holder = ((IngredientBodyViewHolder) viewholder);
            FridgeData item = fridgeDataList.get(position);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference pathReference = storageRef.child(item.imagePath);

            Glide.with(holder.image.getContext()).load(pathReference).into(holder.image);
            holder.name.setText(item.name);

            if (item.remainED < 0) {
                holder.EDProgressBar.setProgress(0);
                holder.remainED.setText(item.remainED + "일(만료)");
            } else {
                if (item.remainED <= 10) {
                    holder.EDProgressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                }
                holder.EDProgressBar.setProgress((int) item.remainED); //남은 일자로 수정
                holder.remainED.setText(item.remainED + "일");
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null)
                            mListener.onItemClick(view, fridgeDataList.get(pos));
                    }
                }
            });
        }

        else if(viewholder instanceof IngredientHeaderViewHolder){
            FridgeData item = fridgeDataList.get(position);

            IngredientHeaderViewHolder holder = ((IngredientHeaderViewHolder) viewholder);
            holder.name.setText(item.name);
        }
    }

    @Override
    public int getItemCount() {
        return fridgeDataList.size();
    }


    public class IngredientHeaderViewHolder extends RecyclerView.ViewHolder{
        TextView name;

        public IngredientHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.iv_ingredientMenuName);
        }
    }

    public class IngredientBodyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView remainED;
        ProgressBar EDProgressBar;

        public IngredientBodyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.iv_ingredientImage);
            name = itemView.findViewById(R.id.tv_ingredientName);
            remainED = itemView.findViewById(R.id.tv_ingredientED);
            EDProgressBar = itemView.findViewById(R.id.pb_edProgressBar);
        }
    }
}



