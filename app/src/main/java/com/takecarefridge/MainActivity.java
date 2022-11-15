package com.takecarefridge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        DocumentReference docRef = db.collection("레시피")
                .document("계란말이");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String path = document.getString("이미지");
                        ImageView iv = findViewById(R.id.test2);

                        StorageReference storageRef = storage.getReference();
                        StorageReference pathReference = storageRef.child(path);

                        Glide.with(MainActivity.this)
                                .load(pathReference)
                                .into(iv);
                    }
                }
            }
        });


    }
    public void goShoppingBag(View v){
        startActivity(new Intent(this, ShoppingBag.class));
    }

    public void goRecipeMain(View v){
        startActivity(new Intent(this, RecipeMain.class));
    }

    public void goFreezeMain(View v){
        startActivity(new Intent(this, FreezerMain.class));
    }

    public void goFridgeMain(View v){
        startActivity(new Intent(this, FridgeMain.class));
    }
}