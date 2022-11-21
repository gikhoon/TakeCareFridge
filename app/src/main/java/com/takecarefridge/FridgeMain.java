package com.takecarefridge;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FridgeMain extends AppCompatActivity {
    RecyclerView mFridgeList;
    IngredientListAdapter mIngredientListAdapter;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fridge_main);

        //맨위에 액션바 없애주는 코드
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent getIntent = getIntent();
        ID = "asd";

        //RecyclerView 출력시켜주는 코드
        updateBigIngredientFreshness("asd"); //추후 userId에 회원 ID 넣어야함

    }

    @Override
    protected void onResume() {
        super.onResume();
        showTotalFreshness(ID);//전체 게이지 출력 메소드
        showFreezerScreen(ID);//RecyclerView 출력시켜주는 메소드 ( 추후 userId에 회원 ID 넣어야함)
    }

    public void showFreezerScreen(String userID){
        ArrayList<FridgeData> fridgeDataList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();;


        CollectionReference bigIngredientRef = db.collection("사용자").document(userID)
                .collection("냉장실"); //대분류 Ref
        bigIngredientRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.exists()){
                            String bigIngredientName = document.getId();

                            CollectionReference smallIngredientRef = bigIngredientRef.document(bigIngredientName)
                                    .collection(bigIngredientName);
                            smallIngredientRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(!task.getResult().isEmpty()) {
                                        fridgeDataList.add(new FridgeData(null, document.getId(),null, 0, 0, 0));
                                    }
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //RemainED 구하기
                                        Timestamp registerTS = document.getTimestamp("timestamp");
                                        long totalEd = document.getLong("유통기한");

                                        long seconds = Timestamp.now().getSeconds() - registerTS.getSeconds();
                                        long remainED = totalEd - (seconds / (60 * 60 * 24));

                                        //TotalEd 구하기
                                        String imagePath = document.getString("이미지");
                                        String name = document.getString("분류");
                                        String documentName = document.getId();
                                        //DataList에 넣기
                                        FridgeData fd = new FridgeData(imagePath, name, documentName, totalEd, remainED, 1);
                                        fridgeDataList.add(fd);
                                    }

                                    mFridgeList = findViewById(R.id.rv_freezerListRecyclerView);

                                    //setAdapter
                                    mIngredientListAdapter = new IngredientListAdapter(fridgeDataList);
                                    mIngredientListAdapter.setOnItemClickListener(new IngredientListAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, FridgeData data) {
                                            Log.d("HELLO6", data.name);

                                        }
                                    });
                                    mFridgeList.setAdapter(mIngredientListAdapter);

                                    //set
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(FridgeMain.this, 3);
                                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                                        @Override
                                        public int getSpanSize(int position) {
                                            if(mFridgeList.getAdapter().getItemViewType(position)==0){
                                                return 3;
                                            }else{
                                                return 1;
                                            }
                                        }
                                    });
                                    mFridgeList.setLayoutManager(gridLayoutManager);

                                }
                            });
                        }
                    }

                }
            }
        });
    }

    void updateBigIngredientFreshness(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference bigIngredientRef = db.collection("사용자").document(userID)
                .collection("냉장실"); //대분류 Ref

        bigIngredientRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            String bigIngredientName = document.getId();

                            CollectionReference smallIngredientRef = bigIngredientRef.document(bigIngredientName)
                                    .collection(bigIngredientName);
                            smallIngredientRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int num = 0;
                                    int addAllDate = 0;
                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                        Timestamp registerTS = document.getTimestamp("timestamp");
                                        long totalEd = document.getLong("유통기한");

                                        long seconds = Timestamp.now().getSeconds() - registerTS.getSeconds();
                                        long remainED = totalEd - (seconds / (60 * 60 * 24));
                                        if (remainED < 0) {
                                            remainED = 0;
                                        }
                                        if (remainED > 100) {
                                            remainED = 100;
                                        }
                                        addAllDate += remainED;
                                        num++;

                                    }


                                    DocumentReference putFieldRef = bigIngredientRef.document(bigIngredientName);
                                    putFieldRef.update(bigIngredientName+"갯수",num);
                                    putFieldRef.update("남은기한합",addAllDate);
                                }
                            });
                        }

                    }
                }

            }

        });
    }

    public void showTotalFreshness(String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference bigIngredientRef = db.collection("사용자").document(userID)
                .collection("냉장실"); //대분류 Ref
        bigIngredientRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                long totalED = 0;
                long num =0;
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.exists()){
                            totalED+=document.getLong("남은기한합");
                            num+=document.getLong(document.getId() + "갯수");

                        }
                    }
                    ProgressBar pb = findViewById(R.id.pb_freezerTotalED);
                    TextView freshnessTV = findViewById(R.id.tv_freezerFreshness);
                    int totalEDProgress;
                    if (num > 0) {
                        totalEDProgress = (int)(totalED / num);
                    } else {
                        totalEDProgress = 0;
                    }
                    if (totalEDProgress <= 10) {
                        pb.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                    }

                    if (totalEDProgress > 100) {
                        totalEDProgress = 100;
                    }
                    pb.setProgress(totalEDProgress);
                    freshnessTV.setText(totalEDProgress + "점");
                }
            }
        });
    }

    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }
}