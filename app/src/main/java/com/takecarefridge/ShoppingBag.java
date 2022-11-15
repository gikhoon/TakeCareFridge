package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ShoppingBag extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_bag);
    }

    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }
}