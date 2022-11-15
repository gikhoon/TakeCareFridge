package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class FreezerMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freezer_main);
    }

    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }
}