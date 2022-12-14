package com.takecarefridge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SetShoppingIngredient extends AppCompatActivity {
    String before;
    String largeClass;
    String smallClass;
    String ID;
    boolean isSelfAdd;
    private Spinner spinner;
    private String whereTo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_shopping_ingredient);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //drop down 메뉴바 생성
        spinner = (Spinner)findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
                whereTo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Intent preIntent = getIntent();
        before = preIntent.getStringExtra("preActivity");
        largeClass = preIntent.getStringExtra("largeClass");
        smallClass = preIntent.getStringExtra("smallClass");
        ID = preIntent.getStringExtra("ID");
        isSelfAdd = preIntent.getBooleanExtra("addSelf", false);

        Log.d("HELLO6", before + " " + largeClass + " " + smallClass+" "+ID);

        setSetIngredient();

        Button b = findViewById(R.id.bt_addShoppingIngredientInDB);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIngredient(view);
            }
        });
    }

    public void setSetIngredient(){
        ImageView iv = findViewById(R.id.iv_settingShoppingIngredientImage);
        TextView tv_largeClass = findViewById(R.id.tv_settingShoppingIngredientLargeClass);
        EditText et_settingIngredientName = findViewById(R.id.et_settingShoppingIngredientName);


        tv_largeClass.setText(largeClass);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference bigStorageRef = storage.getReference("재료");

        if(isSelfAdd){
            et_settingIngredientName.setEnabled(true);
            StorageReference sr = bigStorageRef.child(largeClass + ".png");
            Glide.with(iv.getContext()).load(sr).into(iv);
        }else{
            et_settingIngredientName.setText(smallClass);
            StorageReference smallStorageRef = storage.getReference("재료/"+largeClass).child(smallClass+".png");
            Glide.with(iv.getContext()).load(smallStorageRef).into(iv);
        }
    }

    public void goBeforeActivity(View v) {
        Intent intent = new Intent(this ,AddIngredientDetail.class);
        intent.putExtra("preActivity", before);
        intent.putExtra("largeClass", largeClass);
        intent.putExtra("ID", ID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void saveIngredient(View v){
        String saveIngredientPlace;
        EditText et = findViewById(R.id.et_settingShoppingIngredientName);
      /*  TextView tv = findViewById(R.id.tv_settingShoppingIngredientPlace);*/
        String ingredientName = et.getText().toString().trim();

        if(isSelfAdd){
            if(ingredientName.equals("")) {
                Toast.makeText(getApplicationContext(), "이름이 없습니다 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference smallClassRef;
        if(isSelfAdd){
            smallClassRef = db.collection("사용자").document(ID).collection("장바구니").document(ingredientName);
        }else{
            smallClassRef = db.collection("사용자").document(ID).collection("장바구니").document(smallClass);
        }

        smallClassRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    Toast.makeText(getApplicationContext(), "이미 존재하는 목록입니다. 다른 목록을 선택해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    Map<String, Object> m = new HashMap<>();

                    if(isSelfAdd){
                        m.put("분류", ingredientName);
                        String str = "재료/" + largeClass+".png";
                        m.put("이미지",str);
                    }else {
                        m.put("분류", smallClass);
                        String str = "재료/" + largeClass + "/" + smallClass + ".png";
                        m.put("이미지",str);
                    }
                    m.put("대분류", largeClass);
                    m.put("where", whereTo);
                    smallClassRef.set(m);

                    Intent intent = new Intent(SetShoppingIngredient.this,ShoppingBag.class);
                    intent.putExtra("ID", ID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }
}