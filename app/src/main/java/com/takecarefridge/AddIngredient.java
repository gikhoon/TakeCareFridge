package com.takecarefridge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AddIngredient extends AppCompatActivity {
    String before;

    RecyclerView mIngredientList;
    AddIngredientListAdapter mAddIngredientListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        before = intent.getExtras().getString("preActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        /*ArrayList<IngredientData> ingredientDataList = new ArrayList<>();
        IngredientData data = new IngredientData("육류","재료/육류.png");

        ingredientDataList.add(data);

        mIngredientList = findViewById(R.id.rv_addIngredientListRecyclerView);
        mAddIngredientListAdapter = new AddIngredientListAdapter(ingredientDataList);

        mIngredientList.setAdapter(mAddIngredientListAdapter);
        mIngredientList.setLayoutManager(new LinearLayoutManager(this));*/

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<IngredientData> ingredientDataList = new ArrayList<>();

        db.collection("재료")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                if(document.exists()){
                                    String name = document.getId();
                                    String imagePath = document.getString("이미지");

                                    IngredientData fd = new IngredientData(name,imagePath);

                                    ingredientDataList.add(fd);
                                }
                            }

                            mIngredientList = findViewById(R.id.rv_addIngredientListRecyclerView);
                            mAddIngredientListAdapter = new AddIngredientListAdapter(ingredientDataList);

                            mIngredientList.setAdapter(mAddIngredientListAdapter);
                            mIngredientList.setLayoutManager(new LinearLayoutManager(AddIngredient.this));
                        }
                    }
                });

    }

        //TextView tv = new TextView(this);
        //tv.setText(before);

        /*ImageView iv1 = findViewById(R.id.imageview1);
        TextView tv1 = findViewById(R.id.textview1);

        StorageReference pathReference = storageRef.child("재료");
        if (pathReference == null) {
            Toast.makeText(AddIngredient.this, "저장소에 사진이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            StorageReference submitPng = storageRef.child("재료/육류.png");
            submitPng.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(AddIngredient.this).load(uri).into(iv1);
                    tv1.setText("육류");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }*/

    public void goBeforeActivity(View v) {
        if (before.equals("Freezer")) startActivity(new Intent(this, FreezerMain.class));
        else if (before.equals("Fridge")) startActivity(new Intent(this, FridgeMain.class));
    }
}
