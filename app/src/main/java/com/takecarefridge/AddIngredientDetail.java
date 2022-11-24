package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient_detail);

        Intent intent = getIntent();
        String largeClass = intent.getStringExtra("largeClass");
        String id = "asd";
    }
}