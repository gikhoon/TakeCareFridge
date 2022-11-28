package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;

public class ShoppingBag extends AppCompatActivity{

    RecyclerView mShoppingList;
    ShoppingBagListAdapter mShoppingBagListAdapter;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_bag);

        Button PlusShoppingBtn = findViewById(R.id.plus_shopping);
        /*PlusShoppingBtn.setOnClickListener(this);*/

        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        ActionBar actionBar =getSupportActionBar();
        actionBar.hide();
        Log.d("HELLO", ID);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateShoppingList(ID);
    }

    public void updateShoppingList(String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<ShoppingData> ShoppingDataList = new ArrayList<>();

        CollectionReference ShoppingRef = db.collection("사용자").document(userID)
                .collection("장바구니");
        ShoppingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.exists()){
                            String name = document.getId();
                            if(!name.equals("base")) {
                                ShoppingData fd = new ShoppingData(name, null, null, null, null);
                                ShoppingDataList.add(fd);
                            }
                        }
                    }
                }

                mShoppingList = findViewById(R.id.ShoppingRecyclerView);
                mShoppingBagListAdapter = new ShoppingBagListAdapter(ShoppingDataList);

                mShoppingList.setAdapter(mShoppingBagListAdapter);

                mShoppingBagListAdapter.setOnItemClickListener(new ShoppingBagListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, ShoppingData data) {
                        Log.d("HELLO", data.name);
                    }
                });
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ShoppingBag.this, 1);
                mShoppingList.setLayoutManager(gridLayoutManager);
            }
        });
    }


    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }

    public void goAddIngredient(View v){
        Intent intent = new Intent(this, AddIngredient.class);
        intent.putExtra("preActivity", "Shopping");
        intent.putExtra("ID", ID);
        startActivity(intent);
    }
}