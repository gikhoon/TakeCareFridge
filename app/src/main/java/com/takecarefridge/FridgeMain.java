package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FridgeMain extends AppCompatActivity {
    RecyclerView mFridgeList;
    IngredientListAdapter mIngredientListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fridge_main);

        ArrayList<FridgeData> fridgeDataList = new ArrayList<>();
        FridgeData data = new FridgeData("계란", 180, 60);

        fridgeDataList.add(data);

        //출력
        mFridgeList = findViewById(R.id.rv_fridgeListRecyclerView);
        mIngredientListAdapter = new IngredientListAdapter(fridgeDataList);

        mFridgeList.setAdapter(mIngredientListAdapter);
        mFridgeList.setLayoutManager(new LinearLayoutManager(this));

    }


    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }
}