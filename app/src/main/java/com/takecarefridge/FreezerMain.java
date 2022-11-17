package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FreezerMain extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freezer_main);

        Button FreezerPlusBtn = findViewById(R.id.freezer_plus);
        FreezerPlusBtn.setOnClickListener(this);
    }

    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(FreezerMain.this, AddIngredient.class);
        intent.putExtra("preActivity", "Freezer");
        startActivity(intent);
    }
}