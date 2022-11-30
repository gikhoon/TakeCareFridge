package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddIngredientDetail extends AppCompatActivity {

    RecyclerView mIngredientList;
    AddIngredientDetailListAdapter mAddIngredientDetailListAdapter;
    String largeClass;
    String before;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent preIntent = getIntent(); //largeClass에 대분류 값 preActivity에 시작된 장소(freezer, fridge)
        largeClass = preIntent.getStringExtra("largeClass");
        before = preIntent.getStringExtra("preActivity");
        ID = preIntent.getStringExtra("ID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<IngredientDetailData> ingredientDetailDataList = new ArrayList<>();

        db.collection("재료").document(largeClass).collection(largeClass)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                if(document.exists()){
                                    String name = document.getId();
                                    String imagePath = document.getString("이미지");

                                    IngredientDetailData fd = new IngredientDetailData(name, imagePath);

                                    ingredientDetailDataList.add(fd);
                                }
                            }
                            mIngredientList = findViewById(R.id.rv_addIngredientDetailListRecyclerView);
                            mAddIngredientDetailListAdapter = new AddIngredientDetailListAdapter(ingredientDetailDataList);
                            mIngredientList.setAdapter(mAddIngredientDetailListAdapter);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(AddIngredientDetail.this, 2);
                            mIngredientList.setLayoutManager(gridLayoutManager);
                            mAddIngredientDetailListAdapter.setOnClickListener(new AddIngredientDetailListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, IngredientDetailData data) {
                                    Intent intent = new Intent(AddIngredientDetail.this, SetIngredient.class);
                                    intent.putExtra("preActivity",before);
                                    intent.putExtra("largeClass", largeClass);
                                    intent.putExtra("smallClass", data.name);
                                    intent.putExtra("ID", ID);
                                    intent.putExtra("addSelf", false);
                                    startActivity(intent);
                                    //largeClass, preActivity, ID 필요
                                }
                            });

                        }
                    }
                });
    }

    public void goBeforeActivity(View v) {
        Intent intent = new Intent(this ,AddIngredient.class);
        intent.putExtra("preActivity", before);
        intent.putExtra("ID", ID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void goAddSelfActivity(View v) {
        Intent intent = new Intent(this, SetIngredient.class);
        intent.putExtra("preActivity", before);
        intent.putExtra("largeClass", largeClass);
        intent.putExtra("smallClass", "null");
        intent.putExtra("ID", ID);
        intent.putExtra("addSelf", true);
        startActivity(intent);
    }
}