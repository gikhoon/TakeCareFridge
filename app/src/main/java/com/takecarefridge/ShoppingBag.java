package com.takecarefridge;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ShoppingBag extends AppCompatActivity{

    RecyclerView mShoppingList;
    ShoppingBagListAdapter mShoppingBagListAdapter;
    String ID;
    View v_d;
    long leftDate =0;
    String saveIngredientPlace;
    String largeClass;
    String smallClass;
    String imagePath;
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

    //DatePicker onClickListener
    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
            TextView tv_date = v_d.findViewById(R.id.duedateset);
            tv_date.setText(String.format("%d-%d-%d", yy,mm+1,dd));
            Calendar dDay = Calendar.getInstance();
            dDay.set(yy, mm, dd);
            long dDayTime = dDay.getTimeInMillis();
            long todayTime = Calendar.getInstance().getTimeInMillis();
            leftDate = (dDayTime - todayTime) / (60 * 60 * 24 * 1000);
            //유통기한 일수 계산
        }
    };

    public void SgoDatePicker(View v){
        Calendar cal = Calendar.getInstance();
        DatePickerDialog d = new DatePickerDialog(this, mDateSetListener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        d.getDatePicker().setMinDate(System.currentTimeMillis());
        d.show();
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
                        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.menu_main1, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch(menuItem.getItemId()){
                                    case R.id.menu1_delete:
                                    DocumentReference docRef = db.collection("사용자").document(ID).collection("장바구니")
                                            .document(data.name);
                                    docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(), data.name + "이 삭제됐습니다", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    Intent intent = new Intent(ShoppingBag.this, ShoppingBag.class);
                                    intent.putExtra("ID", ID);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    return true;
                                    case R.id.menu1_move:

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingBag.this);

                                        v_d = (View) View.inflate(ShoppingBag.this, R.layout.diagonal_date,null);



                                        builder.setView(v_d);

                                        Calendar cal = Calendar.getInstance();
                                        TextView tv1 = v_d.findViewById(R.id.duedateset);
                                        Log.d("HELLO",cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE));
                                        tv1.setText(cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE));


                                        builder.setPositiveButton("입력",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        //dialog가 떴을 때 입력 누르면 어떻게 되는지
                                                        DocumentReference docRef = db.collection("사용자").document(ID).collection("장바구니")
                                                                .document(data.name);
                                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                //타임 스템프랑 유통기한을 넣고

                                                                //변수에 기존 값 가지고 오기
                                                                //냉장실인지 냉동실인지, 이미지
                                                                if(task.isSuccessful()){
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if(document.exists()){
                                                                        saveIngredientPlace = document.getString("where");
                                                                        largeClass = document.getString("대분류");
                                                                        smallClass = document.getString("분류");
                                                                        imagePath = document.getString("이미지");
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
                                                                                        largeClassRef.update(smallClass,1);
                                                                                    }else{
                                                                                        long ingredientNum = s.getLong(smallClass);
                                                                                        ingredientNum++;
                                                                                        ingredientSum = ingredientNum;
                                                                                        Log.d("HELLO6", smallClass+ "    "+ingredientNum);
                                                                                        largeClassRef.update(smallClass, ingredientNum);
                                                                                    }
                                                                                }
                                                                                DocumentReference smallClassRef;
                                                                                smallClassRef = largeClassRef.collection(largeClass).document(smallClass+ingredientSum);

                                                                                Map<String, Object> m = new HashMap<>();
                                                                                m.put("timestamp", Timestamp.now());


                                                                                m.put("분류", smallClass);
                                                                                m.put("이미지",imagePath);

                                                                                m.put("유통기한", leftDate);
                                                                                m.put("대분류", largeClass);
                                                                                smallClassRef.set(m);

                                                                                docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            Toast.makeText(getApplicationContext(), saveIngredientPlace + "로 보관되었습니다.", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                                Intent intent = new Intent(ShoppingBag.this, ShoppingBag.class);
                                                                                intent.putExtra("ID", ID);
                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                startActivity(intent);

                                                                            }
                                                                        });

                                                                    }
                                                                }

                                                            }
                                                        });

                                                    }
                                                });
                                        builder.show();
                                        return true;
                                    default:
                                        return false;
                                }

                            }
                        });
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