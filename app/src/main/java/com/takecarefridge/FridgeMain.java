package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FridgeMain extends AppCompatActivity {
    RecyclerView mFridgeList;
    IngredientListAdapter mIngredientListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fridge_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        ArrayList<FridgeData> fridgeDataList = new ArrayList<>();

        db.collection("사용자").document("asd")
                .collection("냉장실")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                if(document.exists()){
                                    System.out.println("성공!!!!!!!!!!!!");
                                //RemainED 구하기
                                    Timestamp registerTS = document.getTimestamp("timestamp");
                                    long totalEd = document.getLong("유통기한");

                                    long seconds = Timestamp.now().getSeconds()-registerTS.getSeconds();
                                    long remainED = totalEd-(seconds/(60*60*24));

                                //TotalEd

                                    String imagePath = document.getString("이미지");
                                    String name = document.getId();

                                    System.out.println(remainED+" "+name);

                                    FridgeData fd = new FridgeData(imagePath,name , totalEd, remainED);

                                    fridgeDataList.add(fd);
                                }
                            }

                            //출력
                            mFridgeList = findViewById(R.id.rv_fridgeListRecyclerView);
                            mIngredientListAdapter = new IngredientListAdapter(fridgeDataList);

                            mFridgeList.setAdapter(mIngredientListAdapter);
                            mFridgeList.setLayoutManager(new LinearLayoutManager(FridgeMain.this));
                        }
                    }
                });

    }


    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }
}