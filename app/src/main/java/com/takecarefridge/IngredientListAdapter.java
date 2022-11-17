package com.takecarefridge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

class FridgeData implements Comparable<FridgeData>{
    String name; //재료 이름
    long totalED; //등록일 기준으로 유통기한 ED==유통기한(expiration date)
    long remainED; //남은 유통기한

    public FridgeData(String name,int totalED, int remainED){
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

    public IngredientListAdapter(ArrayList<FridgeData> fridgeDataList){
        this.fridgeDataList = fridgeDataList;
    }

    @NonNull
    @Override
    public IngredientListAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list,parent,false);
        return new IngredientViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull IngredientListAdapter.IngredientViewHolder holder, int position) {
        FridgeData item = fridgeDataList.get(position);

        holder.name.setText(item.name);
        holder.remainED.setText(String.valueOf(item.remainED));
        holder.EDProgressBar.setProgress((int)item.remainED*100/(int)item.totalED);
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
