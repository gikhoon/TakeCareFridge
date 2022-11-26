package com.takecarefridge;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.Calendar;

public class SetIngredient extends AppCompatActivity {
    String before;
    String largeClass;
    String smallClass;
    String ID;
    boolean isSelfAdd;
    long leftDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_ingredient);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent preIntent = getIntent();
        before = preIntent.getStringExtra("preActivity");
        largeClass = preIntent.getStringExtra("largeClass");
        smallClass = preIntent.getStringExtra("smallClass");
        ID = preIntent.getStringExtra("ID");
        isSelfAdd = preIntent.getBooleanExtra("addSelf", false);

        Log.d("HELLO6", before + " " + largeClass + " " + smallClass+" "+ID);

        setSetIngredient();
    }
    public void setSetIngredient(){
        ImageView iv = findViewById(R.id.iv_settingIngredientImage);
        TextView tv_largeClass = findViewById(R.id.tv_settingIngredientLargeClass);
        TextView tv_addPlace = findViewById(R.id.tv_settingIngredientPlace);
        TextView tv_date = findViewById(R.id.tv_settingDate);
        EditText et_settingIngredientName = findViewById(R.id.et_settingIngredientName);

        if(before.equals("Freezer")){
            tv_addPlace.setText("냉동실");
        }else if(before.equals("Fridge")){
            tv_addPlace.setText("냉장실");
        }

        tv_largeClass.setText(largeClass);

        Calendar cal = Calendar.getInstance();
        tv_date.setText(cal.get(Calendar.YEAR) +"-"+ (cal.get(Calendar.MONTH)+1) +"-"+ cal.get(Calendar.DATE));

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
    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
            TextView tv_date = findViewById(R.id.tv_settingDate);
            tv_date.setText(String.format("%d-%d-%d", yy,mm+1,dd));
            Calendar dDay = Calendar.getInstance();
            dDay.set(yy, mm, dd);
            long dDayTime = dDay.getTimeInMillis();
            long todayTime = Calendar.getInstance().getTimeInMillis();
            leftDate = (dDayTime - todayTime) / (60 * 60 * 24 * 1000);

            //유통기한 일수 계산
        }
    };

    public void goDatePicker(View v){
        Calendar cal = Calendar.getInstance();
        DatePickerDialog d = new DatePickerDialog(this, mDateSetListener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        d.getDatePicker().setMinDate(System.currentTimeMillis());
        d.show();
    }


    public void goBeforeActivity(View v) {
        Intent intent = new Intent(this ,AddIngredientDetail.class);
        intent.putExtra("preActivity", before);
        intent.putExtra("largeClass", largeClass);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

}