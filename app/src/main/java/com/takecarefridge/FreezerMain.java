package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class FreezerMain extends AppCompatActivity {
    RecyclerView mFridgeList;
    IngredientListAdapter mIngredientListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freezer_main);

        ArrayList<FridgeData> fridgeDataList = new ArrayList<>();


        Collections.sort(fridgeDataList);


        //출력
        mFridgeList = findViewById(R.id.rv_freezerListRecyclerView);
        mIngredientListAdapter = new IngredientListAdapter(fridgeDataList);

        mFridgeList.setAdapter(mIngredientListAdapter);
        mFridgeList.setLayoutManager(new LinearLayoutManager(this));
    }

    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }
}