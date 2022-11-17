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



        FridgeData data = new FridgeData("계란", 180, 60);
        FridgeData data2 = new FridgeData("육회", 10, 3);
        FridgeData data3 = new FridgeData("사과", 100, 20);
        FridgeData data4 = new FridgeData("배", 180, 60);
        FridgeData data5 = new FridgeData("포도", 180, 60);
        FridgeData data6 = new FridgeData("포도", 180, 60);
        FridgeData data7 = new FridgeData("포도", 180, 60);
        FridgeData data8 = new FridgeData("포도", 180, 60);
        FridgeData data9 = new FridgeData("포도", 180, 60);
        FridgeData data10 = new FridgeData("포도", 180, 60);
        FridgeData data11 = new FridgeData("포도", 180, 60);
        FridgeData data12 = new FridgeData("포도", 180, 60);
        FridgeData data13 = new FridgeData("포도", 180, 60);
        FridgeData data14 = new FridgeData("포도", 180, 60);

        fridgeDataList.add(data);
        fridgeDataList.add(data2);
        fridgeDataList.add(data3);
        fridgeDataList.add(data4);
        fridgeDataList.add(data5);
        fridgeDataList.add(data6);
        fridgeDataList.add(data7);
        fridgeDataList.add(data8);
        fridgeDataList.add(data9);
        fridgeDataList.add(data10);
        fridgeDataList.add(data11);
        fridgeDataList.add(data12);
        fridgeDataList.add(data13);
        fridgeDataList.add(data14);

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