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

        db.collection("사용자").document("asd")
                .collection("냉장실")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Timestamp registerTS = document.getTimestamp("timestamp");
                                long seconds = Timestamp.now().getSeconds()-registerTS.getSeconds();
                                long betweenDate = seconds/(60*60*24);


                            }
                        }
                    }
                });

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