package com.takecarefridge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
    String ID;

    RecyclerView mIngredientList;
    AddIngredientListAdapter mAddIngredientListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        before = intent.getExtras().getString("preActivity");
        ID = intent.getStringExtra("ID");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(AddIngredient.this, 2);
                            mIngredientList.setLayoutManager(gridLayoutManager);
                            mAddIngredientListAdapter.setOnItemClickListener(new AddIngredientListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, IngredientData data) {
                                    Intent intent = new Intent(AddIngredient.this, AddIngredientDetail.class);
                                    intent.putExtra("preActivity", before);
                                    intent.putExtra("largeClass", data.name);
                                    intent.putExtra("ID", ID);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });

    }


    public void goBeforeActivity(View v) {
        if (before.equals("Freezer")){
            Intent intent = new Intent(this, FreezerMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("ID", ID);
            startActivity(intent);
        }
        else if (before.equals("Fridge")){
            Intent intent = new Intent(this, FridgeMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("ID", ID);
            startActivity(intent);
        }
    }
}
