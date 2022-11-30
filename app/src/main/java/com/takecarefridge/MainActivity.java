package com.takecarefridge;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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