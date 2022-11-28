package com.takecarefridge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.guieffect.qual.UI;

public class MainActivity extends AppCompatActivity {
    String ID;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String ID;
    private String userUID;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent preIntent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ID = "lim";
        //ID = getIntent.getStringExtra("ID");

        firebaseAuth = FirebaseAuth.getInstance();
        user =firebaseAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        //현재 로그인 되어있는 사용자의 UID 확인
        if(user != null){
            userUID = user.getUid();
            Log.d("Main", userUID);
        }

        DocumentReference nameRef = db.collection("사용자")
                .document(user.getUid());
        nameRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document =task.getResult();
                    if(document.exists()){
                        ID = document.getString("name");
                        Log.d("login", "로그인한 유저의 닉네임 : " + ID);
                    }
                }
            }
        });
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
}