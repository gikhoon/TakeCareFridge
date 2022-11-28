package com.takecarefridge;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {


    EditText mNicName;
    Button mNicDupCheck;
    EditText mSigEmail;
    EditText mSigpassword;
    EditText mSigpasswordCheck;
    Button mregisterBtn;


    private FirebaseAuth firebaseAuth; //안드로이드와 파이어베이스 사이의 인증을 확인하기 위한 인스턴스 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //파이어베이스 접근
        firebaseAuth = FirebaseAuth.getInstance(); //선언한 인스턴스를 초기화
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // FirebaseDatabase database = FirebaseDatabase.getInstance();

                                                                    //파이어베이스 접근권한 갖기
        mNicName = findViewById(R.id.user_NicName);
        mNicDupCheck = findViewById(R.id.NickName_check);
        mSigEmail = findViewById(R.id.user_id);
        mSigpassword = findViewById(R.id.user_password);
        mSigpasswordCheck = findViewById(R.id.user_password_check);
        mregisterBtn = findViewById(R.id.id_check);

        mNicDupCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String Nickname =mNicName.getText().toString().trim();
                db.collection("사용자")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                boolean flag = false;
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getId().equals(Nickname)) {
                                            mNicName.setText(null);
                                            Toast.makeText(Signup.this, "이미 존재하는 닉네임입니다.\n 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                            flag = true;
                                            break;
                                        }
                                    }
                                }

                                if (!flag) {
                                    Toast.makeText(Signup.this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                    /*mregisterBtn.setEnabled(true);*/

                                }
                            }
                        });
            }
        });
        mregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String email = mSigEmail.getText().toString().trim();
               String pwd = mSigpassword.getText().toString().trim();
               String pwdcheck = mSigpasswordCheck.getText().toString().trim();
               String name =mNicName.getText().toString().trim();

               if(pwd.equals(pwdcheck)){
                   Log.d(TAG, "등록 버튼 " + email + " , " + pwd);
                   final ProgressDialog mDialog = new ProgressDialog(Signup.this);
                   mDialog.setMessage("가입중입니다...");
                   mDialog.show();

                   firebaseAuth.createUserWithEmailAndPassword(email,pwd)
                            .addOnCompleteListener(Signup.this,
                                   new OnCompleteListener<AuthResult>(){
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task){
                                            if(task.isSuccessful()) {
                                                mDialog.dismiss();

                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                String email = user.getEmail();
                                                String uid = user.getUid();


                                                //firebase 사용자 컬렉션에 document 추가
                                                Map<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("uid", uid);
                                                hashMap.put("email", email);
                                                hashMap.put("name", name);
                                                hashMap.put("password", pwd);


                                                Map<String, Object> base = new HashMap<>();
                                                base.put("남은기한", 0);

                                                Map<String, Object> base1 = new HashMap<>();
                                                base1.put("가공식품갯수", 0);
                                                base1.put("남은기한합",0);

                                                Map<String, Object> base2 = new HashMap<>();
                                                base2.put("곡물류갯수", 0);
                                                base2.put("남은기한합",0);

                                                Map<String, Object> base3 = new HashMap<>();
                                                base3.put("과일갯수", 0);
                                                base3.put("남은기한합",0);

                                                Map<String, Object> base4 = new HashMap<>();
                                                base4.put("수산물갯수", 0);
                                                base4.put("남은기한합",0);

                                                Map<String, Object> base5 = new HashMap<>();
                                                base5.put("유제품갯수", 0);
                                                base5.put("남은기한합",0);

                                                Map<String, Object> base6 = new HashMap<>();
                                                base6.put("육류갯수", 0);
                                                base6.put("남은기한합",0);

                                                Map<String, Object> base7 = new HashMap<>();
                                                base7.put("조미료갯수", 0);
                                                base7.put("남은기한합",0);

                                                Map<String, Object> base8 = new HashMap<>();
                                                base8.put("채소갯수", 0);
                                                base8.put("남은기한합",0);





                                                Log.d("asd", name + " ");
                                                db.collection("사용자")
                                                        .document(uid)
                                                        .set(hashMap);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉장실")
                                                        .document("가공식품")
                                                        .set(base1);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉장실")
                                                        .document("곡물류")
                                                        .set(base2);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉장실")
                                                        .document("과일")
                                                        .set(base3);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉장실")
                                                        .document("수산물")
                                                        .set(base4);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉장실")
                                                        .document("유제품")
                                                        .set(base5);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉장실")
                                                        .document("육류")
                                                        .set(base6);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉장실")
                                                        .document("조미료")
                                                        .set(base7);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉장실")
                                                        .document("채소")
                                                        .set(base8);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉동실")
                                                        .document("가공식품")
                                                        .set(base1);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉동실")
                                                        .document("곡물류")
                                                        .set(base2);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉동실")
                                                        .document("과일")
                                                        .set(base3);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉동실")
                                                        .document("수산물")
                                                        .set(base4);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉동실")
                                                        .document("유제품")
                                                        .set(base5);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉동실")
                                                        .document("육류")
                                                        .set(base6);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉동실")
                                                        .document("조미료")
                                                        .set(base7);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("냉동실")
                                                        .document("채소")
                                                        .set(base8);

                                                db.collection("사용자")
                                                        .document(uid)
                                                        .collection("장바구니")
                                                        .document("base")
                                                        .set(base);



                                                Intent intent = new Intent(Signup.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(Signup.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                mSigEmail.setText(null);
                                                Toast.makeText(Signup.this, "이미 존재하는 이메일 아이디 입니다.\n다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                   });

               }
               else{ //비밀번호가 일치하지 않을 때
                   mSigpassword.setText(null);
                   mSigpasswordCheck.setText(null);
                   Toast.makeText(Signup.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                   return;
               }

            }
        });
    }

    public void goToLogin(View view){startActivity(new Intent(this, LoginScreen.class));}
}