package com.takecarefridge;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class FreezerMain extends AppCompatActivity {
    RecyclerView mFridgeList;
    IngredientListAdapter mIngredientListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freezer_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent getIntent = getIntent();

        showFreezerScreen("asd");//RecyclerView 출력시켜주는 메소드 ( 추후 userId에 회원 ID 넣어야함)

        showTotalFreshness("asd"); //전체 게이지 출력 메소드
    }

    public void showFreezerScreen(String userID){
        ArrayList<FridgeData> fridgeDataList = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();;

        db.collection("사용자").document(userID)
                .collection("냉동실")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                if(document.exists()){

                                    //RemainED 구하기
                                    Timestamp registerTS = document.getTimestamp("timestamp");
                                    long totalEd = document.getLong("유통기한");

                                    long seconds = Timestamp.now().getSeconds()-registerTS.getSeconds();
                                    long remainED = totalEd-(seconds/(60*60*24));

                                    //TotalEd 구하기
                                    String imagePath = document.getString("이미지");
                                    String name = document.getId();

                                    //DataList에 넣기
                                    FridgeData fd = new FridgeData(imagePath,name , totalEd, remainED);
                                    fridgeDataList.add(fd);
                                }
                            }
                            Collections.sort(fridgeDataList);

                            //출력
                            mFridgeList = findViewById(R.id.rv_freezerListRecyclerView);
                            mIngredientListAdapter = new IngredientListAdapter(fridgeDataList);

                            //view홀더 하나하나 클릭시 실행되는 onClickMethod
                            mIngredientListAdapter.setOnItemClickListener(new IngredientListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, FridgeData data) {
                                    PopupMenu popup = new PopupMenu(getApplicationContext(),v);

                                }
                            });

                            mFridgeList.setAdapter(mIngredientListAdapter);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(FreezerMain.this, 2);
                            mFridgeList.setLayoutManager(gridLayoutManager);
                        }
                    }
                });
    }

    public void showTotalFreshness(String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("사용자").document(userID)
                .collection("냉동실")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        long addAllDate = 0;
                        int num = 0;
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Timestamp registerTS = document.getTimestamp("timestamp");
                                    long totalEd = document.getLong("유통기한");

                                    long seconds = Timestamp.now().getSeconds()-registerTS.getSeconds();
                                    long remainED = totalEd-(seconds/(60*60*24));
                                    if(remainED<0){remainED=0;}
                                    if(remainED>100) {remainED=100;}
                                    addAllDate+=remainED;
                                    num++;
                                }
                            }
                        }
                        ProgressBar pb = findViewById(R.id.pb_freezerTotalED);
                        TextView freshnessTV = findViewById(R.id.tv_freezerFreshness);
                        int totalEDProgress;
                        if(num>0) {
                            totalEDProgress = (int) addAllDate / num;
                        }else{
                            totalEDProgress=0;
                        }
                        if(totalEDProgress<=10){
                            pb.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                        }

                        if(totalEDProgress>100) {totalEDProgress=100;}
                        pb.setProgress(totalEDProgress);
                        freshnessTV.setText(totalEDProgress+"점");
                    }
                });
    }
    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }
}