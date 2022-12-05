package com.takecarefridge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    String ID;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent preIntent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        user =firebaseAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        //현재 로그인 되어있는 사용자의 UID 확인
        if(user != null){
            ID = user.getUid();
        }

        updateFreezerBigIngredientFreshness(ID);
        updateFridgeBigIngredientFreshness(ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFridgeTotalFreshness(ID);//전체 게이지 출력 메소드
        showFreezerTotalFreshness(ID);//전체 게이지 출력 메소드
    }

    public void LogOut(View view){
        firebaseAuth.signOut();
        Log.d("logout", "로그아웃 성공!");
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }


    public void goShoppingBag(View v){
        Intent intent = new Intent(this, ShoppingBag.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

    public void goRecipeMain(View v){
        Intent intent = new Intent(this, RecipeMain.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

    public void goFreezeMain(View v){
        Intent intent = new Intent(this, FreezerMain.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

    public void goFridgeMain(View v){
        Intent intent = new Intent(this, FridgeMain.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

    void updateFreezerBigIngredientFreshness(String userID) {
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

    void updateFridgeBigIngredientFreshness(String userID) {
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
    } //남은 유통기한 합, 전체 갯수 업데이트

    public void showFridgeTotalFreshness(String userID){
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
                    ProgressBar pb = findViewById(R.id.pb_mainFridgeTotalED);
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
                }
            }
        });
    } //값 totalProcess에 출력

    public void showFreezerTotalFreshness(String userID){
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
                    ProgressBar pb = findViewById(R.id.pb_mainFreezerTotalED);
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
                }
            }
        });
    }


    private long backKeyPressedTime = 0L;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime+2000){
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "앱을 종료하시려면 \'뒤로가기\' 버튼을 한번 더 눌러주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        if(System.currentTimeMillis() <= backKeyPressedTime+2000){
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}