package com.takecarefridge;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetIngredient extends AppCompatActivity {
    String before;
    String largeClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_ingredient);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent preIntent = getIntent();
        before = preIntent.getStringExtra("preActivity");
        largeClass = preIntent.getStringExtra("largeClass");

    }

    public void goBeforeActivity(View v) {
        Intent intent = new Intent(this ,AddIngredientDetail.class);
        intent.putExtra("preActivity", before);
        intent.putExtra("largeClass", largeClass);
        startActivity(intent);
    }
}