package com.takecarefridge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        Button b = findViewById(R.id.bt_addIngredientInDB);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIngredient(view);
            }
        });
    }

    //DatePicker onClickListener
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

    //기본 화면 출력
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

    //날짜 누르면 수정되는 메소드
    public void goDatePicker(View v){
        Calendar cal = Calendar.getInstance();
        DatePickerDialog d = new DatePickerDialog(this, mDateSetListener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        d.getDatePicker().setMinDate(System.currentTimeMillis());
        d.show();
    }

    public void saveIngredient(View v){
        String saveIngredientPlace;
        EditText et = findViewById(R.id.et_settingIngredientName);
        String ingredientName = et.getText().toString();
        Log.d("HELLO5", ingredientName);

        if(isSelfAdd){
            if(ingredientName.equals("")) {
                Toast.makeText(getApplicationContext(), "이름이 없습니다 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(before.equals("Freezer")){saveIngredientPlace = "냉동실";}
        else if(before.equals("Fridge")){saveIngredientPlace = "냉장실";}
        else{saveIngredientPlace = "장바구니";}
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Hello6", "사용자/"+ID+"/"+saveIngredientPlace+"/"+largeClass);
        DocumentReference largeClassRef = db.collection("사용자").document(ID).collection(saveIngredientPlace).document(largeClass);
        largeClassRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                long ingredientSum =1;
                if(task.isSuccessful()) {
                    DocumentSnapshot s = task.getResult();

                    //대분류 갯수 업데이트
                    if (s.getLong(largeClass+"갯수") == null) {
                        Log.d("HELLO6", largeClass + "갯수"+"   1");
                        largeClassRef.update(largeClass + "갯수", 1);
                    }else{
                        String str = largeClass + "갯수";
                        long num = s.getLong(str);
                        num++;
                        Log.d("HELLO6", str+"    "+num);
                        largeClassRef.update(largeClass + "갯수", num);
                    }

                    //남은유통기한 없데이트
                    long d = s.getLong("남은기한합");
                    if(d>100){d=100;}
                    d += leftDate;
                    Log.d("HELLO6", "남은기한합    " + d);
                    largeClassRef.update("남은기한합", d);

                    //smallClass 가지고 있는 갯수 업데이트
                    if(s.getLong(smallClass)==null) {
                        ingredientSum=1;
                        Log.d("HELLO6", ingredientName +"   1");
                        largeClassRef.update(ingredientName,1);
                    }else{
                        long ingredientNum = s.getLong(smallClass);
                        ingredientNum++;
                        ingredientSum = ingredientNum;
                        Log.d("HELLO6", smallClass+ "    "+ingredientNum);
                        largeClassRef.update(smallClass, ingredientNum);
                    }
                }
                DocumentReference smallClassRef;
                if(isSelfAdd){
                    smallClassRef = largeClassRef.collection(largeClass).document(ingredientName+ingredientSum);
                }else{
                    smallClassRef = largeClassRef.collection(largeClass).document(smallClass+ingredientSum);
                }
                Map<String, Object> m = new HashMap<>();
                m.put("timestamp", Timestamp.now());

                if(isSelfAdd){
                    m.put("분류", ingredientName);
                    String str = "재료/" + largeClass+".png";
                    m.put("이미지",str);
                }else {
                    m.put("분류", smallClass);
                    String str = "재료/" + largeClass + "/" + smallClass + ".png";
                    m.put("이미지",str);
                }
                m.put("유통기한", leftDate);
                m.put("대분류", largeClass);
                smallClassRef.set(m);

                Intent intent = new Intent(SetIngredient.this,ShoppingBag.class);
                if(before.equals("Freezer")) {
                    intent = new Intent(SetIngredient.this,FreezerMain.class);
                }else if(before.equals("Fridge")){
                    intent = new Intent(SetIngredient.this, FridgeMain.class);
                }
                intent.putExtra("ID", ID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });



    }

    public void goBeforeActivity(View v) {
        Intent intent = new Intent(this ,AddIngredientDetail.class);
        intent.putExtra("preActivity", before);
        intent.putExtra("largeClass", largeClass);
        intent.putExtra("ID", ID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}