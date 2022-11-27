package com.takecarefridge;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class FreezerMain extends AppCompatActivity implements View.OnClickListener{
    RecyclerView mFridgeList;
    IngredientListAdapter mIngredientListAdapter;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freezer_main);

        Button FreezerPlusBtn = findViewById(R.id.freezer_plus);
        FreezerPlusBtn.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent getIntent = getIntent();
        ID = getIntent.getStringExtra("ID");

        updateBigIngredientFreshness(ID); //목록 총 데이터 업데이트

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
                .collection("냉동실"); //대분류 Ref
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
                                                fridgeDataList.add(new FridgeData(null, null, document.getId(),null, 0, 0, 0));
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
                                                String largeClass = document.getString("대분류");
                                                String documentName = document.getId();
                                                //DataList에 넣기
                                                FridgeData fd = new FridgeData(imagePath, largeClass, name, documentName, totalEd, remainED, 1);
                                                fridgeDataList.add(fd);
                                            }

                                            mFridgeList = findViewById(R.id.rv_freezerListRecyclerView);

                                            //setAdapter
                                            mIngredientListAdapter = new IngredientListAdapter(fridgeDataList);
                                            mIngredientListAdapter.setOnItemClickListener(new IngredientListAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(View v, FridgeData data) {
                                                    PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                                                    MenuInflater inflater = popup.getMenuInflater();
                                                    inflater.inflate(R.menu.menu_main, popup.getMenu());
                                                    popup.show();
                                                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                        @Override
                                                        public boolean onMenuItemClick(MenuItem item) {
                                                            switch (item.getItemId()){
                                                                case R.id.menu_delete:
                                                                    DocumentReference dr = db.collection("사용자").document(ID).collection("냉동실")
                                                                            .document(data.largeClass).collection(data.largeClass).document(data.documentName);
                                                                    dr.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()) {
                                                                                Toast.makeText(getApplicationContext(), data.name + "이 삭제됐습니다", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                    Intent intent = new Intent(FreezerMain.this, FreezerMain.class);
                                                                    intent.putExtra("ID", ID);
                                                                    startActivity(intent);
                                                                    return true;
                                                                case R.id.menu_searchRecipe:
                                                                    return true;
                                                                default:
                                                                    return false;
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                            mFridgeList.setAdapter(mIngredientListAdapter);

                                            //set
                                            GridLayoutManager gridLayoutManager = new GridLayoutManager(FreezerMain.this, 3);
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
                .collection("냉동실"); //대분류 Ref

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
                                    String s = bigIngredientName + "갯수";
                                    putFieldRef.update(s,num);
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
                .collection("냉동실"); //대분류 Ref
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
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("ID", ID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(FreezerMain.this, AddIngredient.class);
        intent.putExtra("preActivity", "Freezer");
        intent.putExtra("ID", ID);
        startActivity(intent);
    }
}